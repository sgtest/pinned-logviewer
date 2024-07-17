package com.so.component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.so.ui.ComponentFactory;
import com.so.util.Constants;
import com.so.util.Util;
import com.so.entity.PathEntityInfo;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.json.JSONUtil;

/**
 * 日志详细展示页面
 * 
 * @author Administrator
 *
 */
@Service
@Scope("prototype")
public class LogDetailComponent extends CommonComponent {

	private static final Logger log = LoggerFactory.getLogger(LogDetailComponent.class);

	private static final long serialVersionUID = 6472452553393605385L;
	private Panel mainPanel;
	private TextArea textArea;
	private String fileEncoding;
	private PathEntityInfo filePathInfo;
	private VerticalLayout contentLayout;
	// 游标
	private Integer readStartLineIndex = 0;
	private Integer readEndLineIndex = 0;
	// 当前页的行数
	private Integer currentPageLines = 0;
	private Label pageLb;
	// 第几页
	private Integer currentPage = 0;
	private List<String> readLines;
	private Button preLogBtn;
	private Button nextLogBtn;
	private Button dowloadLogBtn;

	@Override
	public void initLayout() {
		mainPanel = new Panel();
		contentLayout = new VerticalLayout();
		setCompositionRoot(mainPanel);
		mainPanel.setContent(contentLayout);
		contentLayout.setWidth("100%");
		contentLayout.setHeight("725px");
		initMainLayout();
	}

	/**
	 * 布局
	 */
	private void initMainLayout() {
		AbsoluteLayout abs = new AbsoluteLayout();
		abs.setWidth("100%");
		abs.setHeightFull();
		contentLayout.addComponent(abs);

		textArea = new TextArea("日志详细信息");
		textArea.setWidth("100%");
		textArea.setHeight("640px");

		dowloadLogBtn = ComponentFactory.getStandardButton("下载", e -> {
			dowloadLogFile();
		});
		preLogBtn = ComponentFactory.getStandardButton("上一页", e -> {
			loadContentPrviousPage();
		});
		nextLogBtn = ComponentFactory.getStandardButton("下一页", e -> {
			loadContentNextPage();
		});
		pageLb = new Label("第1页");
		abs.addComponent(textArea);
		abs.addComponent(pageLb, "bottom:10px;left:50px;");
		abs.addComponent(dowloadLogBtn, "bottom:10px;right:230px;");
		abs.addComponent(preLogBtn, "bottom:10px;right:120px;");
		abs.addComponent(nextLogBtn, "bottom:10px;right:10px;");
	}

	private void dowloadLogFile() {
		// DownloadStream d = new DownloadStream(stream, contentType, fileName)
		FileDownloader fileDownloader = new FileDownloader(new StreamResource(new FileStreamResource(), filePathInfo.getFileName()));
		fileDownloader.extend(dowloadLogBtn);
	}

	@Override
	public void initContent() {
		if (null == fileEncoding) {
			fileEncoding = CharsetUtil.defaultCharset().name();
//			fileEncoding = Util.getFileEncode();
		}
		if (null != filePathInfo) {

			readLines = FileUtil.readLines(filePathInfo.getAbsolutePath(), fileEncoding);
			if (filePathInfo.getSuffix().equals("json")) {
				loadPrettyJson();
			} else {
				loadPageData();
			}
		}
		dowloadLogFile();
	}

	private void loadPrettyJson() {
		StringBuffer content = new StringBuffer();
		for (String l : readLines) {
			content.append(l);
		}
		textArea.clear();
		if (JSONUtil.isTypeJSON(content.toString())) {
			String jsonPrettyStr = JSONUtil.toJsonPrettyStr(JSONUtil.parseObj(content.toString()));
			textArea.setValue(jsonPrettyStr);
			content = null;
		} else {
			Notification.show("当前文件不是标准的json文件，无法正确格式化", Notification.Type.WARNING_MESSAGE);
			loadPageData();
		}

	}

	private void loadContentNextPage() {
		if (currentPageLines < Constants.defaulutPageSize) {
			Notification.show("当前已经是最后一页", Notification.Type.WARNING_MESSAGE);
			return;
		}
		readStartLineIndex = readEndLineIndex;
		if (readLines.size()-readEndLineIndex>=Constants.defaulutPageSize) {
			readEndLineIndex = readEndLineIndex + Constants.defaulutPageSize;
		}else {
			readEndLineIndex = (Constants.defaulutPageSize*currentPage) +readLines.size()-readEndLineIndex;
		}
		// if (currentPage == 1) {
		// Notification.show("当前已经是第一页", Notification.Type.WARNING_MESSAGE);
		// return;
		// }
		currentPageLines = 0;
		StringBuffer content = new StringBuffer();
		for (int i = readStartLineIndex; i < readEndLineIndex; i++) {
			if (i < readLines.size()) {
				content.append(readLines.get(i)+System.lineSeparator());
				currentPageLines++;
			}
		}
		
		textArea.clear();
		textArea.setValue(content.toString());
		content = null;
		currentPage += 1;
		pageLb.setValue("第" + currentPage + "页");
	}

	private void loadPageData() {
		currentPageLines = 0;
		StringBuffer content = new StringBuffer();
		if (readLines.size() < Constants.defaulutPageSize) {
			for (int i = 0; i < readLines.size(); i++) {
				content.append(readLines.get(i)+System.lineSeparator());
				currentPageLines++;
			}
			textArea.clear();
			textArea.setValue(content.toString());
			content = null;
			currentPage += 1;
		} else {
			readEndLineIndex = readLines.size();
			readStartLineIndex = (Constants.defaulutPageSize*((int)(Math.floor(readEndLineIndex/Constants.defaulutPageSize))));
			for (int i = readStartLineIndex; i < readEndLineIndex; i++) {
				content.append(readLines.get(i)+System.lineSeparator());
				 currentPageLines++;
			}
			textArea.clear();
			textArea.setValue(content.toString());
			content = null;
			
			currentPage =(int) Math.ceil(readEndLineIndex/Constants.defaulutPageSize)+1;
		}

		pageLb.setValue("第" + currentPage + "页");
	}

	private void loadContentPrviousPage() {
		if (currentPage == 1) {
			Notification.show("当前已经是第一页", Notification.Type.WARNING_MESSAGE);
			return;
		}
		if (readStartLineIndex == 0) {
			Notification.show("当前已经是第一页", Notification.Type.WARNING_MESSAGE);
			return;
		}
		readEndLineIndex = readStartLineIndex;
		readStartLineIndex = readEndLineIndex - Constants.defaulutPageSize;
		if (readStartLineIndex < 0) {
			readStartLineIndex = 0;
		}
		currentPageLines = 0;
		StringBuffer content = new StringBuffer();
		for (int i = readStartLineIndex; i < readEndLineIndex; i++) {
			content.append(readLines.get(i)+System.lineSeparator());
			currentPageLines++;
		}
		textArea.clear();
		textArea.setValue(content.toString());
		content = null;
		currentPage -= 1;
		pageLb.setValue("第" + currentPage + "页");
	}

	@Override
	public void registerHandler() {
		// TODO Auto-generated method stub

	}

	public String getEncoding() {
		return fileEncoding;
	}

	public void setEncoding(String encoding) {
		this.fileEncoding = encoding;
	}

	public PathEntityInfo getFilePathInfo() {
		return filePathInfo;
	}

	public void setFilePathInfo(PathEntityInfo filePathInfo) {
		this.filePathInfo = filePathInfo;
	}

	class FileStreamResource implements StreamSource {

		private static final long serialVersionUID = 6327185867459484865L;

		@Override
		public InputStream getStream() {
			try {
				return new FileInputStream(new File(filePathInfo.getAbsolutePath()));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				log.error("下载文件出现错误");
			}
			return null;
		}

	}

}
