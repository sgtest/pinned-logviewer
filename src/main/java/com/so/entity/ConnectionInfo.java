package com.so.entity;

public class ConnectionInfo {

	private String idHost;
	private String cdPort;
	private String idUser;
	private String cdPassword;
	private String cdKeyPath;
	private String cdLogpath;
	
	
	
	public ConnectionInfo(String idHost, String cdPort, String idUser, String cdPassword, String cdKeyPath) {
		super();
		this.idHost = idHost;
		this.cdPort = cdPort;
		this.idUser = idUser;
		this.cdPassword = cdPassword;
		this.cdKeyPath = cdKeyPath;
	}
	public String getIdHost() {
		return idHost;
	}
	public void setIdHost(String idHost) {
		this.idHost = idHost;
	}
	public String getCdPort() {
		return cdPort;
	}
	public void setCdPort(String cdPort) {
		this.cdPort = cdPort;
	}
	public String getIdUser() {
		return idUser;
	}
	public void setIdUser(String idUser) {
		this.idUser = idUser;
	}
	public String getCdPassword() {
		return cdPassword;
	}
	public void setCdPassword(String cdPassword) {
		this.cdPassword = cdPassword;
	}
	public String getCdKeyPath() {
		return cdKeyPath;
	}
	public void setCdKeyPath(String cdKeyPath) {
		this.cdKeyPath = cdKeyPath;
	}
	public String getCdLogpath() {
		return cdLogpath;
	}
	public void setCdLogpath(String cdLogpath) {
		this.cdLogpath = cdLogpath;
	}
	
	
}