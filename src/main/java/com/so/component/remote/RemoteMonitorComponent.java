package com.so.component.remote;

import cn.hutool.core.util.NumberUtil;
import com.so.component.CommonComponent;
import com.so.entity.ConnectionInfo;
import com.so.ui.ComponentFactory;
import com.so.util.Constants;
import com.so.util.SSHClientUtil;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.ui.Label;
import com.vaadin.ui.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 服务器监控页面
 *
 * @author Administrator
 */
@Service
@Scope("prototype")
public class RemoteMonitorComponent extends CommonComponent {

    private static final Logger log = LoggerFactory.getLogger(RemoteMonitorComponent.class);

    private Panel mainPanel;
    private VerticalLayout contentLayout;
    private VerticalLayout monitorVerticalLayout;
    private ConnectionInfo currentConnectionInfo;
    private SSHClientUtil clientUtil;
    private Chart cupChart;
    private Chart diskChart;
    private Chart memoryChart;
    private UI currentUI;
    private ListSeries cupSeries;
    private ListSeries diskSerial;
    private ListSeries memorySeries;

    @Override
    public void initLayout() {
        currentUI = UI.getCurrent();
        mainPanel = new Panel();
        contentLayout = new VerticalLayout();
        setCompositionRoot(mainPanel);
        mainPanel.setContent(contentLayout);
        contentLayout.setWidth("100%");
        contentLayout.setHeight("700px");
        initMainLayout();
        initMonitorLayout();
    }

    /**
     * 布局
     */
    private void initMainLayout() {
        contentLayout.removeAllComponents();
        HorizontalLayout horizontalLayout = ComponentFactory.getHorizontalLayout();
        horizontalLayout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        Label lb = ComponentFactory.getStandardLabel(currentConnectionInfo.getIdHost());

        Label pathLb = ComponentFactory.getStandardLabel("主机：");
        horizontalLayout.addComponent(pathLb);
        horizontalLayout.addComponent(lb);
        horizontalLayout.setExpandRatio(lb, 1);
        contentLayout.addComponent(horizontalLayout);
        //链接服务器
        try {
            if (null == clientUtil && null != currentConnectionInfo) {
                clientUtil = new SSHClientUtil(currentConnectionInfo.getIdHost(), Integer.parseInt(currentConnectionInfo.getCdPort()), currentConnectionInfo.getIdUser(), currentConnectionInfo.getCdPassword());
                clientUtil.openConnection();
            }
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            Notification.show("链接当前机器失败，请检查该IP：" + currentConnectionInfo.getIdHost(), Notification.Type.ERROR_MESSAGE);
        }
    }

    @Override
    public void detach() {
        super.detach();
        this.clientUtil.closeConnection();
    }

    /**
     * 监控图表页面
     */
    private void initMonitorLayout() {
        if (null != monitorVerticalLayout) {
            contentLayout.removeComponent(monitorVerticalLayout);
        }
        monitorVerticalLayout = new VerticalLayout();
        contentLayout.addComponent(monitorVerticalLayout);
        contentLayout.setExpandRatio(monitorVerticalLayout, 1);
        HorizontalLayout firstLayout = ComponentFactory.getHorizontalLayout();
        monitorVerticalLayout.addComponent(firstLayout);
        firstLayout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        //CUP使用率
        Chart chart = createCupUseageChart();
        firstLayout.addComponent(chart);
        //磁盘使用情况
        Chart diskChart = createDiskUsageChart();
        firstLayout.addComponent(diskChart);
        //内存使用率
        Chart memoryChart = createMemoryUsageChart();
        firstLayout.addComponent(memoryChart);

        //其它指标
        HorizontalLayout secondLayout = ComponentFactory.getHorizontalLayout();
        monitorVerticalLayout.addComponent(secondLayout);
        monitorVerticalLayout.setExpandRatio(secondLayout, 1);
    }

    private Chart createCupUseageChart() {
        cupChart = new Chart(ChartType.SOLIDGAUGE);
        cupChart.setWidth("300px");
        cupChart.setHeight("300px");
        Configuration conf = cupChart.getConfiguration();
        conf.setTitle("CUP使用率(%)");

        Pane pane = conf.getPane();
        pane.setSize("125%");           // For positioning tick labels
        pane.setCenter("50%", "70%"); // Move center lower
        pane.setStartAngle(-90);        // Make semi-circle
        pane.setEndAngle(90);           // Make semi-circle

        Background bkg = new Background();
        bkg.setBackgroundColor(new SolidColor("#eeeeee")); // Gray
        bkg.setInnerRadius("60%");  // To make it an arc and not circle
        bkg.setOuterRadius("100%"); // Default - not necessary
        bkg.setShape("solid");        // solid or arc
        pane.setBackground(bkg);

        YAxis yaxis = new YAxis();
        yaxis.setTitle("使用率");
// The limits are mandatory
        yaxis.setMin(0);
        yaxis.setMax(100);
// Configure ticks and labels
        yaxis.setTickInterval(10);  // At 0, 100, and 200
        yaxis.getLabels().setY(-5); // Move 16 px upwards
        yaxis.setGridLineWidth(0); // Disable grid
        yaxis.setStops(new Stop(0.1f, SolidColor.GREEN),
                new Stop(0.5f, SolidColor.YELLOW),
                new Stop(0.8f, SolidColor.RED));

        conf.addyAxis(yaxis);

        cupSeries = new ListSeries("cup usage",0);
        cupChart.getConfiguration().addSeries(cupSeries);
        updateCupUsage();
        cupChart.drawChart();
        return cupChart;
    }

    private void updateCupUsage() {
        try {
            String s = clientUtil.executeCommand(Constants.CUP_CMD);
            String replace = s.replace("%", "");
            cupSeries.updatePoint(0, Double.parseDouble(replace));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 磁盘使用率
     *
     * @return
     */
    private Chart createDiskUsageChart() {
        diskChart = new Chart(ChartType.SOLIDGAUGE);
        diskChart.setWidth("300px");
        diskChart.setHeight("300px");
        Configuration conf = diskChart.getConfiguration();
        conf.setTitle("磁盘使用率(%)");

        Pane pane = conf.getPane();
        pane.setSize("125%");           // For positioning tick labels
        pane.setCenter("50%", "70%"); // Move center lower
        pane.setStartAngle(-90);        // Make semi-circle
        pane.setEndAngle(90);           // Make semi-circle

        Background bkg = new Background();
        bkg.setBackgroundColor(new SolidColor("#eeeeee")); // Gray
        bkg.setInnerRadius("60%");  // To make it an arc and not circle
        bkg.setOuterRadius("100%"); // Default - not necessary
        bkg.setShape("solid");        // solid or arc
        pane.setBackground(bkg);

        YAxis yaxis = new YAxis();
        yaxis.setTitle("使用率");
//        yaxis.getTitle().setY(-80); // Move 70 px upwards from center

// The limits are mandatory
        yaxis.setMin(0);
        yaxis.setMax(100);

// Configure ticks and labels
        yaxis.setTickInterval(10);  // At 0, 100, and 200
        yaxis.getLabels().setY(-5); // Move 16 px upwards
        yaxis.setGridLineWidth(0); // Disable grid
        yaxis.setStops(new Stop(0.1f, SolidColor.GREEN),
                new Stop(0.5f, SolidColor.YELLOW),
                new Stop(0.8f, SolidColor.RED));

        conf.addyAxis(yaxis);

        diskSerial = new ListSeries("dis useage:", 0);
        diskChart.getConfiguration().addSeries(diskSerial);
        updateDiskUsage();
        diskChart.drawChart();
        return diskChart;
    }

    private void updateDiskUsage() {
        try {
            String s = clientUtil.executeCommand("df -h");
            String[] split = s.split("\\n");
            for (int i = 0; i < split.length; i++) {
                if (split[i].endsWith("/")) {
                    String[] s1 = split[i].split(" ");
                    String s2 = s1[s1.length - 2];
                    String replace = s2.replace("%", "");
                    diskSerial.updatePoint(0, Double.parseDouble(replace));
                    break;
                }
            }
//			ListSeries series = new ListSeries("cup usage",Double.parseDouble(replace));
//			diskChart.getConfiguration().addSeries(series);
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 磁盘使用率
     *
     * @return
     */
    private Chart createMemoryUsageChart() {
        memoryChart = new Chart(ChartType.SOLIDGAUGE);
        memoryChart.setWidth("300px");
        memoryChart.setHeight("300px");
        Configuration conf = memoryChart.getConfiguration();
        conf.setTitle("内存使用率(%)");

        Pane pane = conf.getPane();
        pane.setSize("125%");           // For positioning tick labels
        pane.setCenter("50%", "70%"); // Move center lower
        pane.setStartAngle(-90);        // Make semi-circle
        pane.setEndAngle(90);           // Make semi-circle

        Background bkg = new Background();
        bkg.setBackgroundColor(new SolidColor("#eeeeee")); // Gray
        bkg.setInnerRadius("60%");  // To make it an arc and not circle
        bkg.setOuterRadius("100%"); // Default - not necessary
        bkg.setShape("solid");        // solid or arc
        pane.setBackground(bkg);

        YAxis yaxis = new YAxis();
        yaxis.setTitle("使用率");
        yaxis.getTitle().setY(-80); // Move 70 px upwards from center

// The limits are mandatory
        yaxis.setMin(0);
        yaxis.setMax(100);

// Configure ticks and labels
        yaxis.setTickInterval(10);  // At 0, 100, and 200
        yaxis.getLabels().setY(-10); // Move 16 px upwards
        yaxis.setGridLineWidth(0); // Disable grid
        yaxis.setStops(new Stop(0.1f, SolidColor.GREEN),
                new Stop(0.5f, SolidColor.YELLOW),
                new Stop(0.8f, SolidColor.RED));

        conf.addyAxis(yaxis);

        memorySeries = new ListSeries("Mem usage", 0);
        memoryChart.getConfiguration().addSeries(memorySeries);
        updateMemUsage();
        memoryChart.drawChart();
        return memoryChart;
    }

    private void updateMemUsage() {
        try {
            String s = clientUtil.executeCommand("free -m");
            String[] split = s.split("\\n");
            for (int i = 0; i < split.length; i++) {
                if (split[i].contains("Mem")) {
                    String[] split1 = split[i].split("       ");
                    String totalMem = split1[1].trim();
                    double totalMemNum = Double.parseDouble(totalMem);
                    String freeMem = split1[3].trim();
                    double freeMemNum = Double.parseDouble(freeMem);
                    double ratio = ((totalMemNum - freeMemNum) / totalMemNum) * 100;
                    double ratioNew = NumberUtil.round(ratio, 2).doubleValue();
                    memorySeries.updatePoint(0,ratioNew);
                    break;
                }
                System.out.println(split[i]);
            }
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public void initContent() {
    }

    @Override
    public void registerHandler() {
        BackgroundThread backgroundThread = new BackgroundThread();
        backgroundThread.start();

        this.addDetachListener((DetachListener) event -> backgroundThread.interrupt());
    }

    class BackgroundThread extends Thread {
        int count = 0;

        @Override
        public void run() {
            try {
                // Update the data for a while
                while (count < 4800) {
                    Thread.sleep(3000);
                    if (null == currentUI) {
                        this.interrupt();
                    }
                    currentUI.access(new Runnable() {
                        @Override
                        public void run() {
                            updateCupUsage();
                            updateDiskUsage();
                            updateMemUsage();
                        }
                    });
                    count++;
                }
                // Inform that we have stopped running
                currentUI.access(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("time over");
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            } catch (UIDetachedException e) {
                e.printStackTrace();
            }
        }
    }

    public ConnectionInfo getCurrentConnectionInfo() {
        return currentConnectionInfo;
    }

    public void setCurrentConnectionInfo(ConnectionInfo currentConnectionInfo) {
        this.currentConnectionInfo = currentConnectionInfo;
    }
}
