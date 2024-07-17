package com.so.component.management;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.so.component.CommonComponent;
import com.so.ui.ComponentFactory;
import com.so.util.Util;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

@Service
@Scope("prototype")
public class TestTomcatManage extends CommonComponent {

	private static final long serialVersionUID = 3821978878502347316L;

	private static final Logger log = LoggerFactory.getLogger(TestTomcatManage.class);

	private Panel mainPanel;
	private VerticalLayout contentLayout;

	@Override
	public void initLayout() {
		mainPanel = new Panel();
		setCompositionRoot(mainPanel);
		mainPanel.setHeight("700px");
		contentLayout = new VerticalLayout();
		mainPanel.setContent(contentLayout);
		initButtonDemo();
	}

	private void initButtonDemo() {
		Button  gbtn= ComponentFactory.getStandardButton("启动Tomcat");
		contentLayout.addComponent(gbtn);
		gbtn.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 4935289663946956029L;

			@Override
			public void buttonClick(ClickEvent event) {
				log.warn("开始发送启动命令：");
				List<String> asList = Arrays.asList("cd /home/test/ ","cd apache-tomcat-8.5.14","./bin/startup.sh ");
				Util.executeNewFlow(asList);
			}
		});
		Button  gbtn2= ComponentFactory.getStandardButton("停止Tomcat");
		contentLayout.addComponent(gbtn2);
		gbtn2.addClickListener(new ClickListener() {
			private static final long serialVersionUID = -7775686428573612664L;
			@Override
			public void buttonClick(ClickEvent event) {
				log.warn("开始发送停止命令：");
				List<String> asList = Arrays.asList("cd /home/test/ ","cd apache-tomcat-8.5.14","./bin/shutdown.sh ");
				Util.executeNewFlow(asList);
			}
		});
		Button  gbtn3= ComponentFactory.getStandardButton("发送单条命令");
		contentLayout.addComponent(gbtn3);
		gbtn3.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 7066298894231777738L;

			@Override
			public void buttonClick(ClickEvent event) {
				log.warn("开始发送一条命令：");
				String property = System.getProperty("user.dir");
				System.out.println(property);
				String cmd = "./apache-tomcat-8.5.14/bin/startup.sh";
				Util.executeLinuxCmd(cmd);
				log.info("===命令执行结束======");
			}
		});
		
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
