package com.so.component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * 定时任务配置界面
 * 功能待完成，包括预定义调用Java任务，调用脚本等任务
 * 此功能需配合数据库使用较为方便；下个数据库版本添加；
 * @author Administrator
 *
 */
@Service
@Scope("prototype")
public class CrontabComponent extends CommonComponent {

	
	private static final Logger log = LoggerFactory.getLogger(CrontabComponent.class);

	private static final long serialVersionUID = -1234502120368507746L;
	private Panel mainPanel;
	private VerticalLayout contentLayout;

	@Override
	public void initLayout() {
		mainPanel = new Panel();
		setCompositionRoot(mainPanel);
		contentLayout = new VerticalLayout();
		mainPanel.setContent(contentLayout);
		initMainLayout();
		initConditionLayout();
	}

	/**
	 * 布局
	 */
	private void initMainLayout() {
		
		Label des = new Label("定时任务配置");
		contentLayout.addComponent(des);
		Label line = new Label();
		line.setHeight("2px");
		line.addStyleName("split_line");
		contentLayout.addComponent(line);

	}
	
	/**
	 * 可选择的脚本和corn表达式
	 */
	private void initConditionLayout() {
		
	}

	@Override
	public void initContent() {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerHandler() {
		// TODO Auto-generated method stub

	}
	
	public void importDateTohive() {
        try {
            String shpath = "/data/hadoop/percisettask/2_merge_userlog.sh";
            Process ps = Runtime.getRuntime().exec(shpath);
            ps.waitFor();

            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            String result = sb.toString();
           log.info(result);
        } catch (Exception e) {
        	log.error("脚本执行失败{}",e.getMessage());
            e.printStackTrace();
        }
    }

}
