package com.so.entity;

import java.util.Date;

import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;

public class PathEntityInfo implements Comparable<PathEntityInfo>{

	private String parentPath;
	private String fileName;
	private String absolutePath;
	private String fileSize;
	private String suffix;
	private String createDate;
	
	private Button button;
	private AbstractLayout layout;
	
	
	public AbstractLayout getLayout() {
		return layout;
	}
	public void setLayout(AbstractLayout layout) {
		this.layout = layout;
	}
	public Button getButton() {
		return button;
	}
	public void setButton(Button button) {
		this.button = button;
	}
	public String getParentPath() {
		return parentPath;
	}
	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getAbsolutePath() {
		return absolutePath;
	}
	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	@Override
	public String toString() {
		return "PathEntityInfo [parentPath=" + parentPath + ", fileName=" + fileName + ", absolutePath=" + absolutePath + ", fileSize=" + fileSize
				+ ", suffix=" + suffix + "]";
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	@Override
	public int compareTo(PathEntityInfo o) {
		// TODO Auto-generated method stub
		return this.createDate.compareTo(o.getCreateDate());
		
	}

	
	
	
}
