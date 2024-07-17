package com.so.component;

import com.so.entity.Menu;
import com.vaadin.server.Page.BrowserWindowResizeListener;
import com.vaadin.ui.CustomComponent;


public abstract class CommonComponent extends CustomComponent{
	
		private static final long serialVersionUID = -1489796323160495508L;
		
		public Menu selectedMenu;
		public BrowserWindowResizeListener reSizeListener;
		

		public CommonComponent(){
			super();
		}
		
		public Menu getSelectedMenu() {
			return selectedMenu;
		}

		public void setSelectedMenu(Menu selectedMenu) {
			this.selectedMenu = selectedMenu;
		}

		public abstract void initLayout();
		
		public abstract void initContent();
		
		public abstract void registerHandler();
		
		
		
}
