package com.so.ui;

import javax.servlet.annotation.WebServlet;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.UI;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
@Widgetset(value="com.so.AppWidgetset")
@PreserveOnRefresh
@Push
@SpringUI
public class MyUI extends UI {

	@Autowired
	private SpringViewProvider viewProvider;
	
	@Autowired		
	private LoginView loginView;
	public  VaadinRequest rqst;
    @Override
    protected void init(VaadinRequest vaadinRequest) {
    	rqst=vaadinRequest;
    	vaadinRequest.getAttributeNames();
    	vaadinRequest.getCookies();
		Navigator navigator = new Navigator(this, this);
		navigator.addProvider(viewProvider);
		navigator.addView("", loginView);
    	navigator.addView("loginView", loginView);
		navigator.navigateTo("loginView");
//		navigator.addViewChangeListener(viewChangeListener);
		setNavigator(navigator);
    }
    
private static ViewChangeListener viewChangeListener = new ViewChangeListener() {
		
	private static final long serialVersionUID = -333941841405607830L;

		@Override
		public boolean beforeViewChange(ViewChangeEvent event) {
			boolean isLoggedIn = VaadinSession.getCurrent().getAttribute("user") != null;
	        boolean isLoginView = event.getNewView() instanceof MainView;
	        if (!isLoggedIn && !isLoginView) {
	        	UI.getCurrent().getNavigator().navigateTo("loginView");
	            return false;
	        }else if (isLoggedIn && isLoginView) {
	            return false;
	        }
	        return true;
		}
		
		@Override
		public void afterViewChange(ViewChangeEvent event) {
			
		}
	};

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = true)
    public static class MyUIServlet extends VaadinServlet {

		private static final long serialVersionUID = 7232690359314390481L;
    }
}
