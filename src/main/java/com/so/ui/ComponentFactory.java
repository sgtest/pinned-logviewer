package com.so.ui;

import java.util.List;
import java.util.Set;

import cn.hutool.core.util.StrUtil;
import com.so.component.util.ButtonType;
import com.so.component.util.ColorEnum;
import com.so.entity.User;
import com.so.util.Constants;
import com.so.util.Util;
import com.vaadin.addon.charts.model.DashStyle;
import com.vaadin.addon.charts.model.XAxis;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.addon.charts.model.style.Color;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.addons.ComboBoxMultiselect;

public class ComponentFactory {
	/**
	 * 设置Label后面输入框的位置
	 * @param x Label起始位置
	 * @param y Label的字符数(按半角计算，一个中文字为两个字符)
	 * @return int
	 */
	public static int  getPosition(int x,int y){
		return (int)(x+y*6.5);
	}


	/**
	 * 获取标准子TabSheet（用于页面中）
	 * @return TabSheet
	 */
	public static TabSheet  getSubTabSheet(){
		TabSheet tabSheet=new TabSheet();
		tabSheet.addStyleName("sub_tabsheet");
		return tabSheet;
	}


	
	/**
	 * 获取标准背景色的Panel窗体
	 * @return Panel
	 */
	public static Panel  getSubPanel(){
		Panel panel=new Panel();
		panel.addStyleName("sub_panel");
		panel.setHeight("850px");
		return panel;
	}

	/**
	 * 获取一个绝对布局AbsoluteLayout
	 * @return AbsoluteLayout
	 */
	public static AbsoluteLayout  getAbsoluteLayout(){
		AbsoluteLayout absoluteLayout=new AbsoluteLayout();
		absoluteLayout.setHeight("40px");
		absoluteLayout.setWidth("100%");
		return absoluteLayout;
	}
	/**
	 * 获取一个绝对布局AbsoluteLayout
	 * @return AbsoluteLayout
	 */
	public static AbsoluteLayout  getAbsoluteLayoutHeightFull(){
		AbsoluteLayout absoluteLayout=new AbsoluteLayout();
		absoluteLayout.setHeight("100%");
		absoluteLayout.setWidth("100%");
		return absoluteLayout;
	}
	/**
	 * 获取一个绝对布局AbsoluteLayout
	 * @return AbsoluteLayout
	 */
	public static HorizontalLayout  getHorizontalLayout(){
		HorizontalLayout absoluteLayout=new HorizontalLayout();
		absoluteLayout.setHeight("40px");
		absoluteLayout.setWidth("100%");
		return absoluteLayout;
	}

	public static HorizontalLayout  getHorizontalLayoutRight(){
		HorizontalLayout absoluteLayout=new HorizontalLayout();
		absoluteLayout.setHeight("40px");
		absoluteLayout.setWidth("100%");
		absoluteLayout.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
		return absoluteLayout;
	}
	
	/**
	 * 获取一个标准的页面标题Title
	 * @param title 标题名称
	 * @return AbsoluteLayout
	 */
	
	public static AbsoluteLayout  getTitleLayout(String title){
		AbsoluteLayout absoluteLayout=new AbsoluteLayout();
		Label rptNameLb =new Label(title);
		absoluteLayout.setHeight("50px");
		absoluteLayout.setWidth("100%");
		rptNameLb.addStyleName("title_standard");
		absoluteLayout.addComponent(rptNameLb,"top:10px;left:0px");
		return absoluteLayout;
	}

	/**
	 * 获取一个标准的页面Layout(垂直布局)
	 * @return VerticalLayout
	 */
	public static VerticalLayout  getMainLayout(){
		VerticalLayout mainLayout=new VerticalLayout();
		mainLayout.setHeight("680px");
		mainLayout.setWidth("100%");
		mainLayout.addStyleName("mainLayout");
		mainLayout.setMargin(true);
		return mainLayout;
	}
	
	/**
	 * 获取一个横向按比例分割布局
	 * @param widths 布局宽度比
	 * @return HorizontalGroupLayout
	 */
	public static HorizontalGroupLayout  getHorizontalGroupLayout(Integer[] widths){
		HorizontalGroupLayout layout=new HorizontalGroupLayout(widths);
		layout.setHeight("40px");
		layout.setWidth("100%");
		return layout;
	}


	/**
	 * 获取一个标准的Grid
	 * @param <T>
	 * @return Grid
	 */

	public static <T> Grid<T>  getStandardGrid(){
		Grid<T> grid=new Grid<T>();
		grid.setWidth("90%");
		grid.setColumnReorderingAllowed(true);
		grid.addStyleName("grid_standard");
		return grid;
	}
	public static <T> Grid<T>  getStandardMultipleGrid(){
		Grid<T> grid=new Grid<T>();
		grid.setColumnReorderingAllowed(true);
		grid.setSelectionMode(SelectionMode.MULTI);
		grid.setHeightFull();
		grid.setCaptionAsHtml(true);
		grid.addStyleName("grid_standard");
		return grid;
	}
	
	
	/**
	 *  获取一个标准的标题标签 Label
	 * @param caption 标签内容
	 * @return Label
	 */
	public static Label  getStandardTitle(String caption){
		Label label=new Label(caption);
		label.addStyleName("title_standard");
		return label;
	}
	/**
	 *  获取一个标准的子标题标签 Label
	 * @param caption 标签内容
	 * @return Label
	 */
	public static Label  getStandardSubTitle(String caption){
		Label label=new Label(caption);
		label.addStyleName("subtitle_standard");
		return label;
	}
	/**
	 *  获取一个标准的标签 Label
	 * @param caption 标签内容
	 * @return Label
	 */
	public static Label  getStandardLabel(String caption){
		Label label=new Label(caption);
		label.addStyleName("label_standard");
		label.addStyleName("field_box_standard_height");
		return label;
	}
	/**
	 * 获取一个标准的上传按钮
	 * @param caption String 按钮名
	 * @param receiver 上传文件的接收者
	 * @return Upload
	 */
	public static Upload  getUpload(String caption,Receiver receiver){
		Upload button=new Upload(caption,receiver);
		button.addStyleName("button_upload");
		return button;
	}

	
	/**
	 * 获取一个图标按钮（只有样式，具体图标需在具体代码里set）
	 * @return Button
	 */
	public static Button  getImageButton(){
		Button button=new Button();
		button.addStyleName("button_image");
		button.addStyleName("field_box_standard_height");
		return button;
	}
	/**
	 * 获取一个标准的按钮（已设置宽高、背景色、边框、字体、阴影的按钮（宽为：120px））
	 * @param caption String 按钮名
	 * @return Button
	 */ 
	public static Button  getStandardButton(String caption){
		Button button=new Button(caption);
		button.addStyleName("field_box_standard_height");
		button.addStyleName("button_standard");
		return button;
	}
	
	public static Button  getLinkButton(String caption){
		return getNativeButton(caption);
	}
	/**
	 * 获取一个标准的按钮（已设置宽高、背景色、边框、字体、阴影的按钮（宽为：120px））
	 * @param caption String 按钮名
	 * @return Button
	 */ 
	public static Button  getStandardButton(String caption, ClickListener listener){
		Button button = getStandardButton(caption);
		button.addClickListener(listener);
		return button;
	}
	/**
	 * 一个具有超链接样式的button
	 */
	public static NativeButton  getNativeButton(String caption){
		NativeButton button = new NativeButton(caption);
//		button.addStyleName(ValoTheme.BUTTON_LINK);
		button.addStyleName("link_button_custom");
		return button;
	}
	/**
	 * 获取一个超宽的按钮（已设置宽高、背景色、边框、字体、阴影的按钮（宽为：160px））
	 * @param caption String 按钮名
	 * @return Button
	 */
	public static Button  getButtonWithColor(String caption,ColorEnum e){
		Button button = getStandardButton(caption);
		switch (e) {
		case RED:
			button.addStyleName("button_standard-red");
			break;
		case GRAY:
			button.addStyleName("button_standard-gray");
			break;
		case GREEN:
			button.addStyleName("button_standard-green");
			break;
		case YELLOW:
			button.addStyleName("button_standard-yellow");
			break;
		default:
			break;
		}
		return button;
	}
	/**
	 * @param caption String 按钮名
	 * @return Button
	 */
	public static Button  getPrimaryButtonWithType(String caption,ButtonType type){
		Button button = getStandardButton(caption);
		button.addStyleName("el-button");
		switch (type) {
		case PRIMARY:
			button.addStyleName("el-button--primary");
			break;
		case SUCCESS:
			button.addStyleName("el-button--success");
			break;
		case INFO:
			button.addStyleName("el-button--info");
			break;
		case WARNNING:
			button.addStyleName("el-button--warning");
			break;
		case WHITE:
			button.addStyleName("el-button--white");
			break;
		case DANGER:
			button.addStyleName("el-button--danger");
			break;
		default:
			break;
		}
		return button;
	}
	
	public static HorizontalLayout getHsplitLine() {
		HorizontalLayout line = new HorizontalLayout();
		line.addStyleName("horizontal-line");
		line.setHeight("2px");
		line.setWidth("100%");
		return line;
	}
	
	public static VerticalLayout getVsplitLine() {
		VerticalLayout line = new VerticalLayout();
		line.addStyleName("horizontal-line");
		line.setHeight("2px");
		line.setWidth("100%");
		return line;
	}
	
	/**
	 * 获取一个超窄的按钮（已设置宽高、背景色、边框、字体、阴影的按钮（宽为：80px））
	 * @param caption String 按钮名
	 * @return Button
	 */
	public static Button  getNarrowButton(String caption){
		Button button=new Button(caption);
		button.addStyleName("button_narrow");
		button.addStyleName("field_box_standard_height");
		return button;
	}

	/**
	 * 获取一个查询的按钮（图标按钮）
	 * @return Button
	 */
	
	public static Button  getQueryButton(){
		Button button=getImageButton();
		button.setIcon(new ThemeResource("img/query-button.png"));
		return button;
	}
	/**
	 * 获取圆形按钮，自己设置图标
	 * button.setIcon(new ThemeResource("img/query-button.png"));
	 * @return
	 */
	public static Button  getCircleButton(ColorEnum e){
		Button button=new Button();
		switch (e) {
		case RED:
			button.addStyleName("button_standard-red");
			break;
		case GRAY:
			button.addStyleName("button_standard-gray");
			break;
		case GREEN:
			button.addStyleName("button_standard-green");
			break;
		case YELLOW:
			button.addStyleName("button_standard-yellow");
			break;
		default:
			break;
		}
		button.addStyleName("button_circle");
		return button;
	}
	
	/**
	 * 获取一个标准卡片
	 * @return VerticalLayout
	 */
	public static VerticalLayout  getBlankCard(){
		VerticalLayout mainLayout=new VerticalLayout();
		mainLayout.setHeight("230px");
		mainLayout.setWidth("400px");
		mainLayout.addStyleName("standard-card");
		mainLayout.setMargin(true);
		return mainLayout;
	}
	/**
	 * 获取一个标准卡片
	 * @return VerticalLayout
	 */
	public static VerticalLayout  getStandardCard(String title,List<String> contents){
		VerticalLayout mainLayout=new VerticalLayout();
		Label standardTitle = getStandardTitle(title);
		HorizontalLayout hsplitLine = getHsplitLine();
		mainLayout.addComponents(standardTitle,hsplitLine);
		mainLayout.setHeight("230px");
		mainLayout.setWidth("400px");
		mainLayout.addStyleName("standard-card");
		mainLayout.setMargin(true);
		for (String caption : contents) {
			mainLayout.addComponent(getStandardLabel(caption));
		}
		return mainLayout;
	}
	
	/**
	 * 获取一个标准的文本输入框（已设置字体大小、宽高、边框的文本输入框）
	 * @return TextField
	 */
	public static TextField  getStandardTtextField(){
		TextField textField=new TextField();
		textField.addStyleName("textfield_standard");
		textField.addStyleName("field_box_standard_height");
		return textField;
	}

	public static TextArea getTextArea(){
		TextArea area = new TextArea();
		area.setHeight("300px");
		area.setWidth("100%");
		return area;
	}

	public static TextArea getTextArea(String caption){
		TextArea area = new TextArea(caption);
		area.setHeight("300px");
		area.setWidth("100%");
		return area;
	}

	/**
	 * 获取一个带提示标签的文本输入框（已设置字体大小、宽高、边框的文本输入框）
	 * height: 30px;
		width: 200px;
	 * @param caption String 标签名
	 * @return TextField
	 */
	public static TextField  getStandardTtextField(String caption){
		TextField textField=new TextField(caption);
		textField.addStyleName("textfield_standard");
		textField.addStyleName("field_box_standard_height");
		return textField;
	}

	/**
	 * 获取一个标准的口令文本输入框（已设置字体大小、宽高、边框的文本输入框）
	 * @param caption String 标签名
	 * @return  PasswordField
	 */

	public static PasswordField  getStandardPassedwordField(String caption){
		PasswordField textField=new PasswordField(caption);
		textField.addStyleName("textfield_standard");
		textField.addStyleName("field_box_standard_height");
		return textField;
	}
	
	public static YAxis getStandardYAxis(){
		YAxis yAxis=new YAxis();
		yAxis.setLineWidth(1);
		Color color =new SolidColor("#808080");		
		yAxis.setLineColor(color);
		yAxis.setGridLineDashStyle(DashStyle.DOT);
		return yAxis;
		
	}

	public static XAxis getStandardXAxis(){
		XAxis xAxis=new XAxis();
		xAxis.setLineWidth(1);
		Color color =new SolidColor("#808080");		
		xAxis.setLineColor(color);
		xAxis.setGridLineDashStyle(DashStyle.DOT);
		return xAxis;
		
	}


	/**
	 * 获取一个标准的块文本输入框
	 * @return TextField
	 */
	public static TextArea  getStandardTtextArea(){
		TextArea textArea=new TextArea();
		textArea.addStyleName("textfield_standard");
		return textArea;
	}

	
	
	/**
	 * 获取一个标准的日期输入框（已设置字体大小、宽高、边框、背景等）
	 * @return DateField
	 */
	public static DateField  getStandardDateField(){
		DateField dateField=new DateField();
		dateField.addStyleName("datefield_standard");
		dateField.addStyleName("field_box_standard_height");
		dateField.setDateFormat("yyyy-MM-dd");
		return dateField;
	}
	/**
	 * 获取一个标准的单选按钮
	 * @return CheckBox
	 */
	public static CheckBox  getStandardCheckBox(){
		CheckBox checkBox=new CheckBox();
		return checkBox;
	}
	/**
	 * 获取一个标准的单选按钮
	 * @param <T>
	 * @return CheckBox
	 */
	public static <T> CheckBoxGroup<T>  getStandardCheckBoxGroup(List<T> items){
		CheckBoxGroup<T> group=new CheckBoxGroup<T>();
		group.setResponsive(true);
		group.setItems(items);
		group.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
		return group;
	}

	public static <T> ComboBox<T> getStandardComboBox() {
		ComboBox<T> com = new ComboBox<T>();
		com.addStyleName("field_box_standard_height");
		return com;
	}
	public static <T> ComboBox<T> getStandardComboBox(String caption) {
		ComboBox<T> com = new ComboBox<T>(caption);
		com.addStyleName("field_box_standard_height");
		return com;
	}
	public static ComboBoxMultiselect getComboxMultiselect(){
		ComboBoxMultiselect multi = new ComboBoxMultiselect();
		return multi;
	}

	public static ComboBoxMultiselect getComboxMultiselect(String caption){
		ComboBoxMultiselect multi = new ComboBoxMultiselect(caption);
		multi.addStyleName("field_box_standard_height");
		return multi;
	}
	public static  RadioButtonGroup<String> getStandardRadioButtonGroup(List<String> items) {
		RadioButtonGroup<String> com = new RadioButtonGroup<String>();
		com.setResponsive(true);
		com.setItems(items);
		com.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
		com.addStyleName("field_box_standard_height");
		return com;
	}

}

	