package com.so.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;


@TableName("users")
public class User implements Serializable{
	private static final long serialVersionUID = -8183726775316718897L;
	@TableId(value="id_user")
	private String userId;
	@TableField(value="name_user")
	private String userName;

	private String email;

	private String password;

	private Date createTime;
	
	private Date expireTime;

	private String organization;
	
	
	private String cdPhone;
	
	private String userFlag;
	
	private String permission;
	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId == null ? null : userId.trim();
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName == null ? null : userName.trim();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email == null ? null : email.trim();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password == null ? null : password.trim();
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	/**
	 * @return the expireTime
	 */
	public Date getExpireTime() {
		return expireTime;
	}

	/**
	 * @param expireTime the expireTime to set
	 */
	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	/**
	 * @return the cdPhone
	 */
	public String getCdPhone() {
		return cdPhone;
	}

	/**
	 * @param cdPhone the cdPhone to set
	 */
	public void setCdPhone(String cdPhone) {
		this.cdPhone = cdPhone;
	}

	/**
	 * @return the userFlag
	 */
	public String getUserFlag() {
		return userFlag;
	}

	/**
	 * @param userFlag the userFlag to set
	 */
	public void setUserFlag(String userFlag) {
		this.userFlag = userFlag;
	}


	public User(String userId, String userName, String email) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.email = email;
	}

	public User(String userId) {
		super();
		this.userId = userId;
	}

	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}
}