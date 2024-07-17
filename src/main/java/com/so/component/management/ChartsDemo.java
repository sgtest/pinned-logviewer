package com.so.component.management;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.PlotBand;
import com.vaadin.addon.charts.model.PlotOptionsColumn;
import com.vaadin.addon.charts.model.PlotOptionsLine;
import com.vaadin.addon.charts.model.PlotOptionsPie;
import com.vaadin.addon.charts.model.Tooltip;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.addon.charts.model.style.SolidColor;
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
public class ChartsDemo extends CommonComponent {

	
	private static final Logger log = LoggerFactory.getLogger(ChartsDemo.class);

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
		initLineChartDemo();
		initMixChartDemo();
		initColumnChartDemo();
		initGaugeChartDemo();
		initPieChartDemo();
	}
	/**
	 * 曲线图
	 * 如果X轴固定则可以用listSeries
	 * ListSeries series = new ListSeries("Diameter");
		series.setData(4900,  12100,  12800,
		               6800,  143000, 125000,
		               51100, 49500);
		conf.addSeries(series);
	 */
	private void initLineChartDemo() {
		Chart chart = new Chart(ChartType.SPLINE);
		chart.setHeight("300px");
		chart.setWidth("300px");
		Configuration conf = chart.getConfiguration();
		conf.setTitle("曲线图");
		Tooltip tooltip = new Tooltip();
		tooltip.setFormatter(
				"function(){return this.point.name+': '+Highcharts.numberFormat(this.y, 2)+' 元';}");
//		tooltip.setFormatter(
//				"function(){return this.point.name+': '+Highcharts.numberFormat(this.y/100000000, 2)+' 亿元';}");
		conf.setTooltip(tooltip);
		DataSeries series = new DataSeries("资产结构");
		conf.addSeries(series);
		series.add(new DataSeriesItem("货币资金",200));
		series.add(new DataSeriesItem("应收票据",260));
		series.add(new DataSeriesItem("应收账款",500));
		series.add(new DataSeriesItem("预付款项",300));
		DataSeries series2 = new DataSeries("xx结构");
		conf.addSeries(series2);
		series2.add(new DataSeriesItem("货币资金",300));
		series2.add(new DataSeriesItem("应收票据",260));
		series2.add(new DataSeriesItem("应收账款",600));
		series2.add(new DataSeriesItem("预付款项",900));
		chart.drawChart();
		contentLayout.addComponent(chart);
	}
	private void initMixChartDemo() {
		Chart chart = new Chart(ChartType.SPLINE);
		chart.setHeight("300px");
		chart.setWidth("300px");
		Configuration conf = chart.getConfiguration();
		conf.setTitle("混合图");
		Tooltip tooltip = new Tooltip();
		tooltip.setFormatter(
				"function(){return this.point.name+': '+Highcharts.numberFormat(this.y, 2)+' 元';}");
//		tooltip.setFormatter(
//				"function(){return this.point.name+': '+Highcharts.numberFormat(this.y/100000000, 2)+' 亿元';}");
		conf.setTooltip(tooltip);
		// A data series as column graph
		DataSeries series1 = new DataSeries();
		PlotOptionsColumn options1 = new PlotOptionsColumn();
		options1.setColor(SolidColor.BLUE);
		series1.setPlotOptions(options1);
		series1.setData(4900,  12100,  12800,
		    6800,  143000, 125000, 51100, 49500);
		conf.addSeries(series1);

		// A data series as line graph
		ListSeries series2 = new ListSeries("Diameter");
		PlotOptionsLine options2 = new PlotOptionsLine();
		options2.setColor(SolidColor.RED);
		series2.setPlotOptions(options2);
		series2.setData(4900,  12100,  12800,
		    6800,  143000, 125000, 51100, 49500);
		conf.addSeries(series2);
		chart.drawChart();
		contentLayout.addComponent(chart);
	}
	private void initColumnChartDemo() {
		Chart chart = new Chart(ChartType.COLUMN);
		chart.setHeight("300px");
		chart.setWidth("300px");
		Configuration conf = chart.getConfiguration();
		conf.setTitle("柱形图");
		DataSeries series = new DataSeries("资产结构");
		conf.addSeries(series);
		series.add(new DataSeriesItem("货币资金",200));
		series.add(new DataSeriesItem("应收票据",260));
		series.add(new DataSeriesItem("应收账款",500));
		series.add(new DataSeriesItem("预付款项",300));
		DataSeries series2 = new DataSeries("xx结构");
		conf.addSeries(series2);
		series2.add(new DataSeriesItem("货币资金",270));
		series2.add(new DataSeriesItem("应收票据",360));
		series2.add(new DataSeriesItem("应收账款",400));
		series2.add(new DataSeriesItem("预付款项",380));
		chart.drawChart();
		contentLayout.addComponent(chart);
	}
	private void initGaugeChartDemo() {
		Chart chart = new Chart(ChartType.GAUGE);
		chart.setHeight("300px");
		chart.setWidth("300px");
		Configuration conf = chart.getConfiguration();
		conf.setTitle("demo");
		conf.setTitle("Speedometer");
		conf.getPane().setStartAngle(-135);
		conf.getPane().setEndAngle(135);
		
		YAxis yaxis = new YAxis();
		yaxis.setTitle("km/h");

		// A gauge has only an Y-axis. You need to provide both a minimum and maximum value for it.
		// The limits are mandatory
		yaxis.setMin(0);
		yaxis.setMax(100);

		yaxis.getLabels().setStep(1);
		yaxis.setTickInterval(10);
		yaxis.setTickLength(10);
		yaxis.setTickWidth(1);
		yaxis.setMinorTickInterval("1");
		yaxis.setMinorTickLength(5);
		yaxis.setMinorTickWidth(1);
		yaxis.setPlotBands(new PlotBand[]{
		        new PlotBand(0,  60,  SolidColor.GREEN),
		        new PlotBand(60, 80,  SolidColor.YELLOW),
		        new PlotBand(80, 100, SolidColor.RED)});
		yaxis.setGridLineWidth(0); // Disable grid

		conf.addyAxis(yaxis);
		
//		A gauge only displays a single value, which you can define as a data series of length one, such as as follows:
		ListSeries series = new ListSeries("Speed", 80);
		conf.addSeries(series);
		
		chart.drawChart();
		contentLayout.addComponent(chart);
	}
	
	private void initPieChartDemo(){
		Chart chart = new Chart(ChartType.PIE);
		chart.setHeight("300px");
		Configuration conf = chart.getConfiguration();
		PlotOptionsPie options = new PlotOptionsPie();
		options.setInnerSize("0");
		options.setSize("75%");  // Default
		options.setCenter("50%", "50%"); // Default
		conf.setPlotOptions(options);
		
		DataSeries series = new DataSeries();
		conf.addSeries(series);
		series.add(new DataSeriesItem("Mercury", 4900));
		series.add(new DataSeriesItem("Venus", 12100));
		// Slice one sector out
		DataSeriesItem earth = new DataSeriesItem("Earth", 12800);
		earth.setSliced(true);
		series.add(earth);
		
		// The inner pie
//		DataSeries innerSeries = new DataSeries();
//		innerSeries.setName("Browsers");
//		PlotOptionsPie innerPieOptions = new PlotOptionsPie();
//		innerPieOptions.setSize("60%");
//		innerSeries.setPlotOptions(innerPieOptions);
//		conf.addSeries(innerSeries);
//
//		DataSeries outerSeries = new DataSeries();
//		conf.addSeries(outerSeries);
//		outerSeries.setName("Versions");
//		PlotOptionsPie outerSeriesOptions = new PlotOptionsPie();
//		outerSeriesOptions.setInnerSize("60%");
//		outerSeries.setPlotOptions(outerSeriesOptions);

		chart.drawChart();
		contentLayout.addComponent(chart);
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


