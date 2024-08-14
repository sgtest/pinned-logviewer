package com.so.component.management;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.so.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.vaadin.addons.ComboBoxMultiselect;

import com.so.ui.ComponentFactory;
import com.so.component.CommonComponent;
import com.so.component.util.ButtonType;
import com.so.component.util.ColorEnum;
import com.so.entity.User;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.Slider;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.renderers.ImageRenderer;
import com.vaadin.ui.renderers.NumberRenderer;
import com.vaadin.ui.renderers.ProgressBarRenderer;
import com.vaadin.ui.themes.ValoTheme;

import cn.hutool.core.collection.CollectionUtil;

@Service
@Scope("prototype")
public class GridUseDemoTest extends CommonComponent {

	
	private static final Logger log = LoggerFactory.getLogger(GridUseDemoTest.class);

	private static final long serialVersionUID = -5516121570034623010L;
	private Panel mainPanel;
	private VerticalLayout contentLayout;

	@Override
	public void initLayout() {
		mainPanel = new Panel();
		setCompositionRoot(mainPanel);
		mainPanel.setHeight("700px");
		contentLayout = new VerticalLayout();
		mainPanel.setContent(contentLayout);
		initTableDemo();
		initButtonDemo();
		initGridDemo();
		initGridGraphicsDemo();
		initTextFieldDemo();
		initCard();
		initpagesplitDemo();
		initRichTextArea();
	}
	
	/**
	 * 布局Grid 类似table的一个组件的使用示例
	 */
	private void initTableDemo() {
		// Have some data
		List<User> users = Arrays.asList(
				new User("Nicolaus Copernicus", "15443", "1"), 
				new User("Nicolaus Copernicus2", "15473", "2"),
				new User("Nicolaus Copernicus5", "154693", "3"));

		// Create a grid bound to the list
		Grid<User> grid = new Grid<User>();
		grid.setRowHeight(50.0);
		grid.setItems(users);
		grid.setWidthFull();
		grid.addColumn(User::getUserName).setCaption("Name");
		grid.addColumn(User::getEmail).setCaption("email");

		/** 使用render 生成列的button */
		ButtonRenderer<User> ren = new ButtonRenderer<User>();
		ren.addClickListener(e -> {
			Set<User> selectedItems = grid.getSelectedItems();
			System.out.println(selectedItems);
		});
		grid.addColumn(person -> Constants.DELETE, ren).setCaption("button");

		/**使用componentColumn生成列的button ，相比render 更为灵活可以单独设置button的各种属性*/
		grid.addComponentColumn(p -> {
			Button b = ComponentFactory.getPrimaryButtonWithType("普通按钮", ButtonType.PRIMARY);
			b.addStyleName("");
			b.addClickListener(e -> {
				// 获取当前按钮对应行的对象
				System.out.println(p.getUserId());
				// 获取已经选择的行
				Set<User> selectedItems = grid.getSelectedItems();
				selectedItems.forEach(u -> System.out.println(u.getUserId()));
			});
			return b;
		}).setCaption("component");

		grid.addComponentColumn(p -> {
			// ThemeResource img = new ThemeResource("img/user.png");
			Label lb = new Label("label");
			return lb;
		}).setCaption("label");
		
		/** 图片 */
		Column<User, ThemeResource> imageColumn = grid.addColumn(p -> 
		new ThemeResource("img/user.png"), new ImageRenderer()).setCaption("img");
		contentLayout.addComponent(grid);
		
		/** TextField */
		Map<User, TextField> textFields = new HashMap<>();
		grid.addColumn(user -> {
			// Check for existing text field
			if (textFields.containsKey(user)) {
				return textFields.get(user);
			}
			// Create a new one
			TextField textField = ComponentFactory.getStandardTtextField();
			textField.setValue(user.getUserName());
			// Store the text field when user updates the value
			textField.addValueChangeListener(change -> textFields.put(user, textField));
			return textField;
		}, new ComponentRenderer());
	}

	private void initButtonDemo() {
		// TODO Auto-generated method stub
		Button  gbtn= ComponentFactory.getStandardButton("普通button");
		contentLayout.addComponent(gbtn);
		Button  gbtn2= ComponentFactory.getNativeButton("连接button");
		contentLayout.addComponent(gbtn2);
		
		Button  imageBtn= ComponentFactory.getImageButton();
		imageBtn.setIcon(new ThemeResource("img/sure.png"));//问号
		contentLayout.addComponent(imageBtn);
		
		Button  colorBtn= ComponentFactory.getButtonWithColor("绿色",ColorEnum.GREEN);
		Button  colorBtn2= ComponentFactory.getButtonWithColor("红色",ColorEnum.RED);
		Button  colorBtn3= ComponentFactory.getButtonWithColor("灰色",ColorEnum.GRAY);
		Button  colorBtn4= ComponentFactory.getButtonWithColor("黄色",ColorEnum.YELLOW);
		contentLayout.addComponent(colorBtn);
		contentLayout.addComponent(colorBtn2);
		contentLayout.addComponent(colorBtn3);
		contentLayout.addComponent(colorBtn4);
		HorizontalLayout hsplitLine = ComponentFactory.getHsplitLine();//一条水平分割线
		contentLayout.addComponent(hsplitLine);
//		===========================定制按钮===================
		Button primaryB = ComponentFactory.getPrimaryButtonWithType("主要按钮", ButtonType.PRIMARY);
		contentLayout.addComponent(primaryB);
		Button primaryB2 = ComponentFactory.getPrimaryButtonWithType("信息按钮", ButtonType.INFO);
		contentLayout.addComponent(primaryB2);
		Button primaryB3 = ComponentFactory.getPrimaryButtonWithType("默认按钮", ButtonType.WHITE);
		contentLayout.addComponent(primaryB3);
		Button primaryB4 = ComponentFactory.getPrimaryButtonWithType("危险按钮", ButtonType.DANGER);
		contentLayout.addComponent(primaryB4);
		Button primaryB5 = ComponentFactory.getPrimaryButtonWithType("警告按钮", ButtonType.WARNNING);
		contentLayout.addComponent(primaryB5);
		
		Button circleButton = ComponentFactory.getCircleButton(ColorEnum.RED);
		circleButton.setIcon(new ThemeResource("img/user.png"));
		contentLayout.addComponent(circleButton);
		HorizontalLayout hsplitLine2 = ComponentFactory.getHsplitLine();//一条水平分割线
		contentLayout.addComponent(hsplitLine2);
	}

	/**
	 * grid 
	 */
	private void initGridDemo() {
		List<User> users = Arrays.asList(
				new User("Copernicus", "15443", "1"), 
				new User("Copernicus2", "15473", "2"),
				new User("Copernicus5", "154693", "3"));
		Grid<User> mg = ComponentFactory.getStandardGrid();
		mg.setSelectionMode(SelectionMode.MULTI);//多选模式
//		mg.getEditor().setEnabled(true);
		mg.setItems(users);
		mg.addColumn(User::getUserId).setCaption("ID");
		mg.addColumn(User::getUserName).setCaption("Name");
		mg.addColumn(User::getEmail).setCaption("邮箱");
		mg.addComponentColumn(p -> {
			Button b = ComponentFactory.getButtonWithColor(p.getUserName(), ColorEnum.YELLOW);
			b.setData(p);
			b.addClickListener(e -> {
				// 获取已经选择的行
				User source = (User) e.getButton().getData();
				String userId = source.getUserId();
//				Set<User> selectedItems = mg.getSelectedItems();
//				selectedItems.forEach(u -> System.out.println(u.getUserId()));
				System.out.println(userId);
			});
			return b;
		}).setCaption("<div style=\"text-align: center;\">操作</div>");
//		合并表头，成为多层表头,报错
//		HeaderRow groupingHeader = mg.prependHeaderRow();
//		groupingHeader.join(groupingHeader.getCell("ID"),groupingHeader.getCell("Name")).setText("合并表头");
//		groupingHeader.join(groupingHeader.getCell("邮箱"),groupingHeader.getCell("操作")).setText("合并表2");
//		HeaderCell namesCell = groupingHeader.join(
//		    groupingHeader.getCell("firstname"),
//		    groupingHeader.getCell("lastname")).setText("Person");
//		HeaderCell yearsCell = groupingHeader.join(
//		    groupingHeader.getCell("born"),
//		    groupingHeader.getCell("died"),
//		    groupingHeader.getCell("lived")).setText("Dates of Life");
		
		//列添加图片
//		Column<User, ThemeResource> imageColumn = mg.addColumn(
//			    p -> new ThemeResource("img/"+p.getCdPhone()+".jpg"),
//			    new ImageRenderer());
//		mg.addItemClickListener(event -> Notification.show("Value: " + event.getItem()));
		
		//列使用HTML格式
//		Column<User, String> htmlColumn = mg.addColumn(person ->
//	      "<a href='" + person.getDetailsUrl() + "' target='_top'>info</a>",
//	      new HtmlRenderer());
//		mg.addSelectionListener(event -> {
//		    Set<User> selected = event.getAllSelectedItems();
//		    Notification.show(selected.size() + " items selected");
//		});
		contentLayout.addComponent(mg);
	}


	private void initGridGraphicsDemo() {
//	        contentLayout.addComponent(grid);
		
	}
	
	private void initCard() {
		VerticalLayout card = ComponentFactory.getBlankCard();
		contentLayout.addComponent(card);
		VerticalLayout card2 = ComponentFactory.getStandardCard("卡片示例",CollectionUtil.newArrayList("宫保鸡丁","青椒土豆丝","蒜泥黄瓜"));
		contentLayout.addComponent(card2);
	}

	private void initTextFieldDemo() {
		// TODO Auto-generated method stub
		TextField ttextField = ComponentFactory.getStandardTtextField("demo");
		contentLayout.addComponent(ttextField);
	}

	private void initpagesplitDemo() {
		CheckBoxGroup<String> boxGroup = ComponentFactory.getStandardCheckBoxGroup(Arrays.asList("a","b","c"));
		contentLayout.addComponent(boxGroup);
		
		RadioButtonGroup raGroup = ComponentFactory.getStandardRadioButtonGroup(Arrays.asList("a","b","c"));
		contentLayout.addComponent(raGroup);
		
		ProgressBar bar = new ProgressBar();
		bar.setIndeterminate(true);
//		bar.setValue(0.5f);
		contentLayout.addComponent(bar);
	}

	private void initRichTextArea() {
		RichTextArea rtarea = new RichTextArea();
		rtarea.setCaption("My Rich Text Area");

		// Set initial content as HTML
		rtarea.setValue("<h1>Hello</h1>\n" +
		    "<p>This rich text area contains some text.</p>");
		contentLayout.addComponent(rtarea);
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


