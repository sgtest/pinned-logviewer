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
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

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
		contentLayout.addComponent(textArea);
		//lable
		Label standardLabel = ComponentFactory.getStandardLabel("提示：该功能仅用于执行简单的单行命令,多条命令之间无关联，尽量要用分号写成一条命令！！！");
		contentLayout.addComponent(standardLabel);
		AbsoluteLayout absoluteLayout = ComponentFactory.getAbsoluteLayout();
		cmdField = ComponentFactory.getStandardTtextField();
		absoluteLayout.addComponent(cmdField);
		cmdField.setWidth("75%");
		sendBtn = ComponentFactory.getStandardButton("发送");
		sendBtn.setClickShortcut(KeyCode.ENTER, null);
		absoluteLayout.addComponent(sendBtn,"right:20%");
		contentLayout.addComponent(absoluteLayout);
		contentLayout.setExpandRatio(absoluteLayout, 1.0f);
	}

	@Override
	public void initContent() {
	}
	
	@Override
	public void registerHandler() {
		sendBtn.addClickListener(e -> {
			String cmd = cmdField.getValue();
			try {
				List<String>  list= JSchUtil.remoteExecute(session,cmd);
				System.out.println(list.toString());
				textArea.setValue(list.toString());
				cmdField.clear();
			} catch (JSchException e1) {
				e1.printStackTrace();
			}
		});
		readyToConnect();
	}

	private void readyToConnect() {
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
