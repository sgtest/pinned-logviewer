package com.so.component;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

@Service
@Scope("prototype")
public class ComponentTemeplate extends CommonComponent {

	private Panel mainPanel;
	private VerticalLayout contentLayout;

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
