package com.so.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("projects")
public class ProjectList {

	private String idHost;
	@TableId
	private String idProject;
	private String nameProject;
	private String cdDescription;
	private String cdTag;
	
	private String cdCommand;
	private String jvmParam;
	private String jarParam;
	private String cdParentPath;
	private String jarName;
	
	
	
	public String getCdParentPath() {
		return cdParentPath;
	}
	public void setCdParentPath(String cdParentPath) {
		this.cdParentPath = cdParentPath;
	}
	public String getCdCommand() {
		return cdCommand;
	}
	public void setCdCommand(String cdCommand) {
		this.cdCommand = cdCommand;
	}
	public String getIdProject() {
		return idProject;
	}
	public void setIdProject(String idProject) {
		this.idProject = idProject;
	}
	public String getNameProject() {
		return nameProject;
	}
	public void setNameProject(String nameProject) {
		this.nameProject = nameProject;
	}
	public String getCdDescription() {
		return cdDescription;
	}
	public void setCdDescription(String cdDescription) {
		this.cdDescription = cdDescription;
	}
	public String getCdTag() {
		return cdTag;
	}
	public void setCdTag(String cdTag) {
		this.cdTag = cdTag;
	}
	public String getJvmParam() {
		return jvmParam;
	}
	public void setJvmParam(String jvmParam) {
		this.jvmParam = jvmParam;
	}
	public String getJarParam() {
		return jarParam;
	}
	public void setJarParam(String jarParam) {
		this.jarParam = jarParam;
	}
	public String getJarName() {
		return jarName;
	}
	public void setJarName(String jarName) {
		this.jarName = jarName;
	}
	public String getIdHost() {
		return idHost;
	}
	public void setIdHost(String idHost) {
		this.idHost = idHost;
	}

	@Override
	public String toString() {
		return "ProjectList{" +
				"idHost='" + idHost + '\'' +
				", idProject='" + idProject + '\'' +
				", nameProject='" + nameProject + '\'' +
				", cdDescription='" + cdDescription + '\'' +
				", cdTag='" + cdTag + '\'' +
				", cdCommand='" + cdCommand + '\'' +
				", jvmParam='" + jvmParam + '\'' +
				", jarParam='" + jarParam + '\'' +
				", cdParentPath='" + cdParentPath + '\'' +
				", jarName='" + jarName + '\'' +
				'}';
	}
}
