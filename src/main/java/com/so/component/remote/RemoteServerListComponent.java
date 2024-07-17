package com.so.component.remote;

import java.util.List;

import com.so.component.CommonComponent;
import com.so.component.ComponentUtil;
import com.so.component.RemoteSSHComponent;
import com.so.ui.LoginView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.so.component.util.TabSheetUtil;
import com.so.entity.ConnectionInfo;
import com.so.mapper.ConnectionInfoMapper;
import com.so.ui.ComponentFactory;
import com.so.util.Util;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * 远程服务器列表，读取配置文件中的列表
 * remoteServerList.properties
 * @author Administrator
 *
 */
@Service
@Scope("prototype")
public class RemoteServerListComponent extends CommonComponent {

	
	private static final Logger log = LoggerFactory.getLogger(RemoteServerListComponent.class);

	private static final long serialVersionUID = 8995914798319911923L;
	private Panel mainPanel;
	private VerticalLayout contentLayout;
	@Autowired
	private ConnectionInfoMapper connectionInfoMapper;
	@Override
	public void initLayout() {
		mainPanel = new Panel();
		setCompositionRoot(mainPanel);
		contentLayout = new VerticalLayout();
		mainPanel.setContent(contentLayout);
		initMainLayout();
	}

	/**
	 * 布局
	 */
	private void initMainLayout() {
		contentLayout.removeAllComponents();
		VerticalLayout serverLayout = new VerticalLayout();
		serverLayout.setSpacing(true);
		serverLayout.setWidth("100%");
		contentLayout.addComponent(serverLayout);
		List<ConnectionInfo> serverListFromDb = connectionInfoMapper.selectList(new QueryWrapper<ConnectionInfo>());
		List<String> remoteServerList = Util.getRemoteServerList();
		for (int i = 0; i < remoteServerList.size(); i++) {
			String[] split = remoteServerList.get(i).split("=");
			ConnectionInfo info = new ConnectionInfo(split[0], split[3],  split[1],  split[2], split[4]);
			serverListFromDb.add(info);
		}
		int i = 0;
		for (ConnectionInfo info : serverListFromDb) {
			AbsoluteLayout abs = ComponentFactory.getAbsoluteLayout();
			if (i%2 != 0){
				abs.addStyleName("remote-server-list-odd");
			}else{
				abs.addStyleName("remote-server-list-even");
			}
			serverLayout.addComponent(abs);
			Label serverLb = new Label(info.getIdHost());
			Button deleteBtn = ComponentFactory.getStandardButton("删除");
			deleteBtn.setData(info);
			deleteBtn.addClickListener(e ->{
				if (!LoginView.checkPermission("delete")){
					Notification.show("权限不足，请联系管理员", Notification.Type.WARNING_MESSAGE);
					return;
				}
				refreshAndDeleteRecord(info);
			});
			Button manageBtn = ComponentFactory.getStandardButton("应用管理");
			manageBtn.setData(info);
			manageBtn.addClickListener(e ->{
				addRemoteAppTab(info);
			});
			Button sshBtn = ComponentFactory.getStandardButton("SSH管理");
			sshBtn.setData(info);
			sshBtn.addClickListener(e ->{
				addRemoteSSHTab(info);
			});
			abs.addComponent(serverLb);
			abs.addComponent(manageBtn,"left:155px;");
			abs.addComponent(sshBtn,"left:300px;");
			abs.addComponent(deleteBtn,"left:440px;");
			if (i == serverListFromDb.size()-1) {
				serverLayout.setExpandRatio(abs, 1);
			}
			i ++;
		}
		serverLayout.setHeight((600+remoteServerList.size()*40)+"px");
	}

	private void refreshAndDeleteRecord(ConnectionInfo data) {
		Window win = new Window("提示");
		win.setHeight("150px");
		win.setWidth("300px");
		win.setModal(true);
		AbsoluteLayout absoluteLayout = ComponentFactory.getAbsoluteLayoutHeightFull();
		
		Label standardLabel = ComponentFactory.getStandardLabel("确认删除吗？");
		Button button = ComponentFactory.getStandardButton("确认");
		button.addClickListener(e ->{
			QueryWrapper<ConnectionInfo> queryWrapper = new QueryWrapper<ConnectionInfo>();
			queryWrapper.eq("id_host", data.getIdHost()).eq("cd_port",data.getCdPort());
			int delete = connectionInfoMapper.delete(queryWrapper);
			if (delete >0) {
				Notification.show("删除成功", Notification.Type.WARNING_MESSAGE);
			}else {
				Notification.show("该条数据在配置文件，需要手动删除。", Notification.Type.WARNING_MESSAGE);
			}
			win.close();
			initMainLayout();
		});
		absoluteLayout.addComponent(standardLabel,"left:35%;top:18%;");
		absoluteLayout.addComponent(button,"right:10%;bottom:10px;");
		win.setContent(absoluteLayout);
		UI.getCurrent().addWindow(win);
	}

	/**
	 * 远程应用管理
	 * @param data
	 */
	private void addRemoteAppTab(ConnectionInfo data) {
		RemoteAppManagement bean = ComponentUtil.applicationContext.getBean(RemoteAppManagement.class);
		bean.setAddr(data);
		bean.setHostName(data.getIdHost());
		bean.initLayout();
		bean.initContent();
		bean.registerHandler();
		TabSheetUtil.getMainTabsheet().addTab(bean,"应用管理-"+data.getIdHost()).setClosable(true);
		TabSheetUtil.getMainTabsheet().setSelectedTab(bean);
	}
	/**
	 * 远程ssh窗口
	 * @param data
	 */
	private void addRemoteSSHTab(ConnectionInfo data) {
		RemoteSSHComponent bean = ComponentUtil.applicationContext.getBean(RemoteSSHComponent.class);
		bean.setAddr(data);
		bean.setHostName(data.getIdHost());
		bean.initLayout();
		bean.initContent();
		bean.registerHandler();
		TabSheetUtil.getMainTabsheet().addTab(bean,"SSH-"+data.getIdHost()).setClosable(true);
		TabSheetUtil.getMainTabsheet().setSelectedTab(bean);
	}

	@Override
	public void initContent() {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerHandler() {
		// TODO Auto-generated method stub

	}
}
