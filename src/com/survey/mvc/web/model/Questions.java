package com.survey.mvc.web.model;

import java.util.ArrayList;
import java.util.List;

public class Questions {
	public Questions(List<String> questionText, int surveyID, int adminID, String surveyName) {
		super();
		this.questionText = questionText;
		this.surveyID = surveyID;
		this.adminID = adminID;
		this.surveyName = surveyName;
	}
	public Questions() {
		// TODO Auto-generated constructor stub
	}
	public List<String> getQuestionText() {
		return questionText;
	}
	public void setQuestionText(List<String> questionText) {
		this.questionText = questionText;
	}
	public int getSurveyID() {
		return surveyID;
	}
	public void setSurveyID(int surveyID) {
		this.surveyID = surveyID;
	}
	public int getAdminID() {
		return adminID;
	}
	public void setAdminID(int adminID) {
		this.adminID = adminID;
	}
	public String getSurveyName() {
		return surveyName;
	}
	public void setSurveyName(String surveyName) {
		this.surveyName = surveyName;
	}
	private List<String> questionText = new ArrayList();
	private int surveyID;
	private int adminID;
	private String surveyName;
	private List<ServerQuestions> serverQuestionList;
	public List<ServerQuestions> getServerQuestionList() {
		return serverQuestionList;
	}
	public void setServerQuestionList(List<ServerQuestions> serverQuestionList) {
		this.serverQuestionList = serverQuestionList;
	}
}
