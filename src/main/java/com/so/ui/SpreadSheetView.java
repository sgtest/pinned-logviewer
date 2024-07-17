package com.so.ui;
//package com.so.view;
//
//import java.io.File;
//import java.io.IOException;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.vaadin.addon.spreadsheet.Spreadsheet;
//import com.vaadin.navigator.View;
//import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
//import com.vaadin.spring.annotation.SpringView;
//import com.vaadin.spring.annotation.UIScope;
//import com.vaadin.ui.VerticalLayout;
//
//@UIScope
//@SpringView(name = "sheetView")
//public class SpreadSheetView extends VerticalLayout implements View {
//
//	private static final long serialVersionUID = 1L;
//
//	private static final Logger logger = LoggerFactory.getLogger(SpreadSheetView.class);
//
//	public SpreadSheetView() {
//		super();
//		setSizeFull();
//		initUi();
//	};
//
//	protected void initUi() {
//		File file = new File("D:\\abc.xlsx");
//		Spreadsheet spreadsheet = null;
//		try {
//		     spreadsheet = new Spreadsheet(file);
//		     addComponent(spreadsheet);
//		} catch (IOException e) {
//		   e.printStackTrace();
//		}
//		
//	}
//
//
//	protected void refreshUi() {
//		initUi();
//	}
//
//
//	@Override
//	public void enter(ViewChangeEvent event) {
//		// focus the username field when user arrives to the login view
//	}
//
//}
