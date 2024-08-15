package com.so.component.remote;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ssh.JschUtil;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.so.component.CommonComponent;
import com.so.component.util.TabSheetUtil;
import com.so.entity.ConnectionInfo;
import com.so.ui.ComponentFactory;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 远程SSH
 * 
 * @author Administrator
 *
 */
@Service
@Scope("prototype")
public class RemoteSSHComponentV2 extends CommonComponent {

	private static final Logger log = LoggerFactory.getLogger(RemoteSSHComponentV2.class);

	private static final long serialVersionUID = 1578091801743458376L;

	private Panel mainPanel;
	private VerticalLayout contentLayout;

	private Session session;
	private ChannelExec channel;
	private ConnectionInfo addr;
	private String hostName;
	private TextArea textArea;

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
		textArea.setHeight("617px");
		textArea.setWidthFull();
		textArea.addStyleName("ssh-textarea");
		textArea.setCaptionAsHtml(true);
		contentLayout.addComponent(textArea);
		textArea.setCaptionAsHtml(true);
		//lable
		Label standardLabel = ComponentFactory.getStandardLabel("提示：该功能仅用于执行单行命令,不能使用vim、rz、sz等特殊命令");
		AbsoluteLayout absoluteLayout = ComponentFactory.getAbsoluteLayout();
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

		absoluteLayout.addComponent(standardLabel,"left:0px;");
		absoluteLayout.addComponent(returnBtn,"right:1%");
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
		textArea.addShortcutListener(new ShortcutListener("exe",null,67,17) {
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
		textArea.addShortcutListener(new ShortcutListener("exe",null,KeyCode.R, ShortcutAction.ModifierKey.CTRL) {
			@Override
			public void handleAction(Object sender, Object target) {
				String content = textArea.getValue();
				String[] split = content.split("\\n");
				String lastLine = split[split.length - 1];
				System.out.println(lastLine);
				String[] split1 = lastLine.split("#");
				String cmd = null;
				if (lastLine.contains("#")){
					System.out.println(split1);
					cmd = split1[1];
				}else{
					cmd = split1[0].trim();
				}

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
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	/**
	 *  String ansiString = "\u001B[31m红色文本\u001B[0m";
	 *         String htmlString = StringEscapeUtils.escapeHtml4(ansiString);
	 *         System.out.println(htmlString);
	 * @param input
	 * @throws IOException
	 */
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
		if (value.length()>2000){
			String sub = StrUtil.sub(value, value.length() - 2000, value.length());
			textArea.setValue(sub + bf.toString());
		}else {
			textArea.setValue(value + bf.toString());
		}
		textArea.setCursorPosition(textArea.getValue().length());
//		UI.getCurrent().getPage().getJavaScript().execute("this.scrollTop = this.scrollHeight;");
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
