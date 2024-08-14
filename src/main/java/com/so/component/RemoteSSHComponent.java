package com.so.component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import cn.hutool.core.util.StrUtil;
import com.so.component.util.TabSheetUtil;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.so.entity.ConnectionInfo;
import com.so.entity.PathEntityInfo;
import com.so.ui.ComponentFactory;
import com.so.ui.HorizontalGroupLayout;
import com.so.util.JSchUtil;
import com.so.util.Util;
import com.vaadin.event.ShortcutListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.extra.ssh.JschUtil;

/**
 * 远程SSH
 * 
 * @author Administrator
 *
 */
@Service
@Scope("prototype")
public class RemoteSSHComponent extends CommonComponent {

	private static final Logger log = LoggerFactory.getLogger(RemoteSSHComponent.class);

	private static final long serialVersionUID = 1578091801743458376L;

	private Panel mainPanel;
	private Button sendBtn;
	private VerticalLayout contentLayout;

	private Session session;
	private ChannelExec channel;
	private ConnectionInfo addr;
	private String hostName;
	private TextArea textArea;

	private TextField cmdField;
	private ChannelShell channelShell;
	String ANSI_PATTERN = "\\033\\[[;\\d]*m";

	@Override
	public void initLayout() {
		mainPanel = new Panel();
		contentLayout = new VerticalLayout();
		setCompositionRoot(mainPanel);
		mainPanel.setContent(contentLayout);
		contentLayout.setWidth("100%");
		contentLayout.setHeight("700px");
		initMainLayout();
	}

	/**
	 * 布局
	 */
	private void initMainLayout() {
		textArea = new TextArea(hostName);
		textArea.setHeight("515px");
		textArea.setWidthFull();
		textArea.addStyleName("ssh-textarea");
		textArea.setCaptionAsHtml(true);
		contentLayout.addComponent(textArea);
		//lable
		Label standardLabel = ComponentFactory.getStandardLabel("提示：该功能仅用于执行单行命令,不能使用vim、rz、sz等特殊命令");
		contentLayout.addComponent(standardLabel);
		AbsoluteLayout absoluteLayout = ComponentFactory.getAbsoluteLayout();
		cmdField = ComponentFactory.getStandardTtextField();
		absoluteLayout.addComponent(cmdField);
		cmdField.setWidth("75%");
		sendBtn = ComponentFactory.getStandardButton("发送");
		Button returnBtn = ComponentFactory.getStandardButton("退出");
		returnBtn.addClickListener(e ->{
			TabSheetUtil.closeCurrrentTab();
			try {
				channelShell.disconnect();
				session.disconnect();
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		});
		sendBtn.setClickShortcut(KeyCode.ENTER, null);
		sendBtn.setDescription("可直接回车代替点击");
		absoluteLayout.addComponent(sendBtn,"right:20%");
		absoluteLayout.addComponent(returnBtn,"right:15%");
		contentLayout.addComponent(absoluteLayout);
		contentLayout.setExpandRatio(absoluteLayout, 1.0f);
	}

	@Override
	public void initContent() {
		createSession();
		session.setConfig("StrictHostKeyChecking", "no");
		if (!session.isConnected()){
			try {
				session.connect();
				channelShell = (ChannelShell) session.openChannel("shell");
				// 连接通道
				channelShell.connect();
			} catch (JSchException e) {
				Notification.show("链接失败！请确认该机器是否能正常访问", Notification.Type.WARNING_MESSAGE);
				log.error(e.getMessage());
			}
		}

	}

	@Override
	public void detach() {
		super.detach();
		channelShell.disconnect();
		session.disconnect();
	}

	@Override
	public void registerHandler() {
		sendBtn.addClickListener(e -> {
			String cmd = cmdField.getValue();
			log.info("命令："+cmd);
			if (cmd.startsWith("vim") || cmd.startsWith("rz") || cmd.startsWith("sz")){
				Notification.show("暂不支持当前命令", Notification.Type.WARNING_MESSAGE);
				return;
			}
			try {
				// 获取输入、输出流
				InputStream input = channelShell.getInputStream();
				OutputStream out = channelShell.getOutputStream();
				if (cmd.equals("3")){
					out.write((char)3);
				}else {
					out.write((cmd+"\n").getBytes());
				}
				out.flush();
				// 读取命令输出
				try {
					Thread.sleep(500);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				receiveRemoteResult(input);
				cmdField.clear();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

		cmdField.addShortcutListener(new ShortcutListener("exe",null,67,17) {
			@Override
			public void handleAction(Object sender, Object target) {
				try {
					InputStream input = channelShell.getInputStream();
					OutputStream out = channelShell.getOutputStream();
					out.write((char)3);
					out.flush();
					// 读取命令输出
					try {
						Thread.sleep(500);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					receiveRemoteResult(input);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	private void receiveRemoteResult(InputStream input) throws IOException {
		StringBuffer bf = new StringBuffer();
		bf.append("------------------------------------------");
		byte[] tmp = new byte[1024];
		while (input.available() > 0) {
			int i = input.read(tmp, 0, 1024);
			if (i < 0) {break;}
			String s = new String(tmp, 0, i);
			bf.append(s.replaceAll(ANSI_PATTERN,""));
		}
		if (channelShell.isClosed()) {
			System.out.println("Exit status: " + channelShell.getExitStatus());
		}
		String value = textArea.getValue();
		if (value.length()>8000){
			String sub = StrUtil.sub(value, value.length() - 8000, value.length());
			textArea.setValue(sub + bf.toString());
		}else {
			textArea.setValue(value + bf.toString());
		}
		UI.getCurrent().getPage().getJavaScript().execute("this.scrollTop = this.scrollHeight;");
	}

	private void createSession() {
		try {
			if (addr.getCdKeyPath() == null) {
				//无秘钥连接
				session = JschUtil.createSession(hostName, Integer.parseInt(addr.getCdPort()), addr.getIdUser(), addr.getCdPassword());
//				channel = JschUtil.openShell(session);
			}else if(addr.getCdKeyPath() != null){
				//秘钥连接
				session = JschUtil.createSession(hostName, Integer.parseInt(addr.getCdPort()), addr.getIdUser(),addr.getCdKeyPath(),  addr.getCdPassword() == null ?null :addr.getCdPassword().getBytes());
//				channel = JschUtil.openShell(session);
			}
		} catch (NumberFormatException e) {
			Notification.show("连接失败请检查配置", Notification.Type.WARNING_MESSAGE);
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * 断开SFTP Channel、Session连接
	 * 
	 * @throws Exception
	 */
	public void closeChannel() throws Exception {
		if (channel != null) {
			channel.disconnect();
		}
		if (session != null) {
			session.disconnect();
		}
		log.info("disconnected SFTP successfully!");
	}

	public void setAddr(ConnectionInfo addr) {
		this.addr = addr;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	
	

}
