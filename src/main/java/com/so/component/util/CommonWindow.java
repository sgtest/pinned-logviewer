package com.so.component.util;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Window;

/**
 * 通用Window 将layout传入即可
 * AbsoluteLayout abs = ComponentFactory.getAbsoluteLayout();
 * 			abs.setHeightFull();
 */
public class CommonWindow extends Window {
		public CommonWindow(String title, String width, String height, AbsoluteLayout layout) {
			super(title); // Set window caption
			center();
			setModal(true);
			setClosable(true);
			setHeight("370px");
			setWidth("370px");
			setContent(layout);
		}
	}