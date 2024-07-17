package com.so.component;

import java.util.Arrays;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.so.ui.ComponentFactory;
import com.so.util.EncryptionUtils;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@Service
@Scope("prototype")
public class EncryptionComponent extends CommonComponent {

	private static final long serialVersionUID = 3476310495496937206L;
	private Panel mainPanel;
	private VerticalLayout contentLayout;
	private ComboBox<String> encriptionCombo;
	private TextField inputField;
	private TextArea resultArea;

	@Override
	public void initLayout() {
		mainPanel = new Panel();
		setCompositionRoot(mainPanel);
		contentLayout = new VerticalLayout();
		mainPanel.setContent(contentLayout);
		initMainLayout();
		initSelectLayout();
	}

	/**
	 * 布局
	 */
	private void initMainLayout() {
		Label des = new Label("加密字符串生成");
		contentLayout.addComponent(des);
		Label line = new Label();
		line.setHeight("2px");
		line.addStyleName("split_line");
		contentLayout.addComponent(line);

	}

	private void initSelectLayout() {
		HorizontalLayout sel = ComponentFactory.getHorizontalLayout();
		sel.setWidth("20%");
		Label pathLb = new Label("选择加密算法：");
		sel.addComponent(pathLb);
		

		encriptionCombo = ComponentFactory.getStandardComboBox();
		encriptionCombo.setEmptySelectionAllowed(false);
		encriptionCombo.setItems(Arrays.asList("SM3", "SHA3-512", "SHA-1", "SHA-384", "MD5", "SHA-256", "SHA3-384", "SHA-512", "RIPEMD256",
				"Skein-1024-384", "Tiger", "SHA3-256", "SHA3-224", "Skein-1024-1024", "WHIRLPOOL"));
		encriptionCombo.setValue("SM3");
		sel.addComponent(encriptionCombo);
		contentLayout.addComponent(sel);
		//输入要加密的字符串
		AbsoluteLayout sel2 = ComponentFactory.getAbsoluteLayout();
		sel2.setWidth("50%");
		Label pathLb2 = new Label("输入要加密的字符串：");
		pathLb2.setWidth("175px");
		sel2.addComponent(pathLb2);

		inputField = ComponentFactory.getStandardTtextField();
		inputField.setWidth("290px");
		sel2.addComponent(inputField,"left:187px");
		
		Button btn = ComponentFactory.getStandardButton("加密");
//		Button copyBtn = RquestComponent.getStandardButton("复制结果");
//		copyBtn.addClickListener(e ->{
//			String value = resultArea.getValue();
//			if (null == value || value.trim().equals("")) {
//				Notification.show("没有结果可以复制，请先生成加密字符串", Notification.Type.WARNING_MESSAGE);
//				return;
//			}else {
//				ClipboardUtil.setStr(value);
//				Notification.show("复制成功", Notification.Type.WARNING_MESSAGE);
//			}
//		});
		btn.addClickListener(e ->{
			resultArea.clear();
			if (null == inputField.getValue() || inputField.getValue().trim().equals("")) {
				Notification.show("请先输入要加密的字符串", Notification.Type.WARNING_MESSAGE);
				return;
			}
			String digest = EncryptionUtils.getkeyByAlgorithm(encriptionCombo.getValue(), inputField.getValue());
			if (null == digest) {
				resultArea.setValue("请输入正确的加密字符串");
			}else {
				resultArea.setValue(digest);
			}
		});
		sel2.addComponent(btn,"left:500px;");
//		sel2.addComponent(copyBtn);
		contentLayout.addComponent(sel2);
		
		//输入出结果
		resultArea = ComponentFactory.getStandardTtextArea();
		resultArea.setWidth("50%");
		resultArea.setHeight("200px");
		contentLayout.addComponent(resultArea);
		contentLayout.setExpandRatio(resultArea, 1);
		
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
