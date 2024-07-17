package com.so.component.remote;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.so.component.CommonComponent;
import com.so.component.ComponentUtil;
import com.so.component.util.TabSheetUtil;
import com.so.entity.ConnectionInfo;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ssh.JschUtil;

/**
 * 远程应用管理页面，将tomcat管理、jar包管理、NGINX等应用管理放到一个tabsheet里面
 * @author Administrator
 *
 */
@Service
@Scope("prototype")
public class RemoteAppManagement extends CommonComponent {

	private static final long serialVersionUID = -5574943080620362841L;

	private static final Logger log = LoggerFactory.getLogger(RemoteAppManagement.class);

	private Panel mainPanel;
	private TabSheet tabsheet;
	private Session session;
	private ChannelSftp channel;
	private ConnectionInfo addr;
	private String hostName;
	@Override
	public void initLayout() {
		mainPanel = new Panel();
		setCompositionRoot(mainPanel);
		tabsheet = new TabSheet();
		mainPanel.setContent(tabsheet);
		readyToConnect();
		initMainLayout();
	}

	/**
	 * 布局
	 */
	private void initMainLayout() {
		RemoteLogSearchComponent logSearchComponent = ComponentUtil.applicationContext.getBean(RemoteLogSearchComponent.class);
		logSearchComponent.setJschSession(session);
		logSearchComponent.setAddr(addr);
		logSearchComponent.initLayout();
		logSearchComponent.initContent();
		logSearchComponent.registerHandler();
		tabsheet.addTab(logSearchComponent,"　日志搜索-"+StrUtil.sub(addr.getIdHost(), 8, 20)).setClosable(false);
		tabsheet.setSelectedTab(logSearchComponent);

		RemoteJarMgmtComponent bean = ComponentUtil.applicationContext.getBean(RemoteJarMgmtComponent.class);
		bean.setJschSession(session);
		bean.setAddr(addr);
		bean.initLayout();
		bean.initContent();
		bean.registerHandler();
		tabsheet.addTab(bean,"Jar项目管理-"+StrUtil.sub(addr.getIdHost(), 8, 20)).setClosable(false);
		tabsheet.setSelectedTab(bean);
		//tomat 管理页面
		RemoteTomcatMgmtComponent tomcat = ComponentUtil.applicationContext.getBean(RemoteTomcatMgmtComponent.class);
		tomcat.setJschSession(session);
		tomcat.setAddr(addr);
		tomcat.initLayout();
		tomcat.initContent();
		tomcat.registerHandler();
		tabsheet.addTab(tomcat,"Tomcat管理-"+StrUtil.sub(addr.getIdHost(), 8, 20)).setClosable(false);
//		nginx及其它管理页面
		CommonProjecttMgmtComponent common = ComponentUtil.applicationContext.getBean(CommonProjecttMgmtComponent.class);
		common.setJschSession(session);
		common.setAddr(addr);
		common.initLayout();
		common.initContent();
		common.registerHandler();
		tabsheet.addTab(common,"通用项目管理-"+StrUtil.sub(addr.getIdHost(), 8, 20)).setClosable(false);
	}

	@Override
	public void initContent() {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerHandler() {
		// TODO Auto-generated method stub
	}
	private void readyToConnect() {
		try {
			if (addr.getCdKeyPath() == null) {
				//无秘钥连接
				session = JschUtil.createSession(hostName, Integer.parseInt(addr.getCdPort()), addr.getIdUser(), addr.getCdPassword());
			}else if(addr.getCdKeyPath() != null){
				//秘钥连接
				session = JschUtil.createSession(hostName, Integer.parseInt(addr.getCdPort()), addr.getIdUser(),addr.getCdKeyPath(),  addr.getCdPassword() == null ?null :addr.getCdPassword().getBytes());
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
