package com.survey.mvc.web.model;

public class AdminData {
	public AdminData(String a_username, String a_password) {
		super();
		this.a_username = a_username;
		this.a_password = a_password;
	}
	public String getA_username() {
		return a_username;
	}
	public void setA_username(String a_username) {
		this.a_username = a_username;
	}
	public String getA_password() {
		return a_password;
	}
	public void setA_password(String a_password) {
		this.a_password = a_password;
	}
	private String a_username;
	private String a_password;

}
