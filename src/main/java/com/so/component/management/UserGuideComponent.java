package com.so.component.management;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.so.ui.ComponentFactory;
import com.so.util.Util;
import com.so.component.CommonComponent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

import cn.hutool.core.util.StrUtil;

/**
 * 使用说明
 * 
 * @author Administrator
 *
 */
@Service
@Scope("prototype")
public class UserGuideComponent extends CommonComponent {

	private static final long serialVersionUID = -5516121570034623010L;
	private Panel mainPanel;
	private VerticalLayout contentLayout;
	private TextArea textArea;

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

		textArea = new TextArea("使用说明");
		textArea.setWidth("100%");
		textArea.setHeight("690px");
		List<String> userGuide = Util.getUserGuide();
		StringBuilder bf = new StringBuilder();
		userGuide.stream().forEach(e-> {
			if (StrUtil.isNotBlank(e)) {
				bf.append(e+System.lineSeparator());
			}
		});
		textArea.setValue(bf.toString());
		textArea.setReadOnly(true);
		contentLayout.addComponent(textArea);
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
