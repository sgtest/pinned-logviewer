package com.so.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.so.ui.HorizontalGroupLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * 首页展示的内容
 * 
 * @author Administrator
 *
 */
@Service
@Scope("prototype")
public class HomePageComponent extends CommonComponent {

	private static final Logger log = LoggerFactory.getLogger(HomePageComponent.class);

	private static final long serialVersionUID = 1578091801743458376L;

	private Panel mainPanel;

	private VerticalLayout contentLayout;

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
		// AbsoluteLayout abs = RquestComponent.getAbsoluteLayout();
		HorizontalGroupLayout abs = new HorizontalGroupLayout(new Integer[] {1,1});
		abs.setHeight("41px");
		contentLayout.addComponent(abs);
		Label pathLb = new Label("主页内容待补充");
		abs.getAbsoluteLayouts().get(0).addComponent(pathLb);

	}

	@Override
	public void initContent() {
		
	}

	@Override
	public void registerHandler() {
		// TODO Auto-generated method stub

	}

}
