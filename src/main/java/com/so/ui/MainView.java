package com.so.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.so.component.UserManagementComponent;
import com.so.entity.User;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Audio;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;

@UIScope
@SpringView(name = "mainView")
public class MainView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(MainView.class);
	// @Autowired
	// private LoginProcess loginProcess;
	// @Autowired
	// private MenuProcess menuProcess;
	// @Autowired
	// private UserOperationProcess userOperationProcess;

	@Override
	public void enter(ViewChangeEvent event) {
		// focus the username field when user arrives to the login view
		addHeader();
		// splitPanle demo
		// HorizontalSplitPanel pan = new HorizontalSplitPanel(RquestComponent.getStandardLabel("lbs1"),RquestComponent.getStandardButton("demo2"));
		// pan.setSplitPosition(10);
		// pan.setHeight("50px");
		// addComponent(pan);

		accordionDemo();

		testOtherComponents();

//		UserManagementComponent userManagementComponent = new UserManagementComponent();
//		userManagementComponent.initLayout();
//		userManagementComponent.initContent();
//		userManagementComponent.registerHandler();
//		addComponent(userManagementComponent);
//		setExpandRatio(userManagementComponent, 1);
		setMargin(false);
	}

	private void testOtherComponents() {

		
	}

	/**
	 * accordion Demo
	 */
	private void accordionDemo() {
		// Create the accordion
		Accordion accordion = new Accordion();
		accordion.addSelectedTabChangeListener(new SelectedTabChangeListener() {
			
			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				System.out.println(accordion.getSelectedTab().getCaption());
				Notification.show(accordion.getSelectedTab().getCaption(), Notification.Type.WARNING_MESSAGE);
			}
		});
		accordion.setWidth("200px");
		// Create the first tab, specify caption when adding
		Layout tab1 = new VerticalLayout(); // Wrap in a layout
		Button but = new Button("demo1");
		Button but2 = new Button("demo2");
		Button but3 = new Button("demo3");
		tab1.addComponents(but,but2,but3);
		tab1.setCaption("Mercury");
		accordion.addTab(tab1, "Mercury", new ThemeResource("img/user.png"));
		accordion.setSelectedTab(tab1);

		// This tab gets its caption from the component caption
		Component tab2 = new Image("Venus", new ThemeResource("img/user.png"));
		accordion.addTab(tab2).setIcon(new ThemeResource("img/user.png"));

		// And so forth the other tabs...
		String[] tabs = { "Earth", "Mars", "Jupiter", "Saturn" };
		for (String caption : tabs) {
//			String basename = "img/planets/" + caption;
			VerticalLayout tab = new VerticalLayout();
			tab.addComponent(new Image(null, new ThemeResource("img/user.png")));
			accordion.addTab(tab, caption, new ThemeResource("img/user.png"));
		}
		addComponent(accordion);
		setComponentAlignment(accordion, Alignment.MIDDLE_LEFT);
	}

	private void addHeader() {
		AbsoluteLayout cssl = ComponentFactory.getAbsoluteLayout();
		cssl.setWidth("100%");
		cssl.addStyleName("mainview-cssl");
		Label lb = new Label("测试");
		lb.setHeight("30px");
		Image img = new Image("", new ThemeResource("img/user.png"));
		cssl.addComponent(img, "right:70px;top:10px;");
		cssl.addComponent(lb, "right:10px;top:5px;");
		Label lb2 = ComponentFactory.getStandardLabel("欢迎...");
		cssl.addComponent(lb2, "left:10px;top:5px;");
		addComponent(cssl);
	}

	public MainView() {
		super();
		addStyleName("login-new-general");
		setSizeFull();
	};

}
