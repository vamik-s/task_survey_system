package com.survey.mvc.web;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.survey.mvc.web.model.ClientData;
import com.survey.mvc.web.model.ClientResponse;
import com.survey.mvc.web.model.Questions;
import com.survey.mvc.web.model.Responses;
import com.survey.mvc.web.model.ServerQuestions;
import com.survey.mvc.web.model.SurveyInfo;

public class ClientDbUtil {

	private DataSource dataSource;
	public ClientDbUtil(DataSource mydataSource) {
		// TODO Auto-generated constructor stub
		dataSource=mydataSource;
	}

	//returns client id 
	public int saveClientInfo(ClientData cd) throws SQLException {
	    int success = -1;

	    Connection myConn = null;
	    PreparedStatement myStmt = null;
	    ResultSet myRs = null;
	    PreparedStatement selectStmt = null;
	    try {
	        // get db connection
	    	   myConn = dataSource.getConnection();
	           
	           // First, insert the record
	           String insertSql = "INSERT INTO `rs_users` (`u_username`, `u_password`) VALUES (?, ?)";
	           myStmt = myConn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
	           myStmt.setString(1, cd.clientName());
	           myStmt.setString(2, "1234");
	           myStmt.executeUpdate();
	           
	           // Retrieve the last inserted ID
	           String selectSql = "SELECT LAST_INSERT_ID() AS `insertedId`";
	           selectStmt = myConn.prepareStatement(selectSql);
	           myRs = selectStmt.executeQuery();
	           
	           if (myRs.next()) {
	               success = myRs.getInt("insertedId");
	               System.out.println("User registered " + success);
	           }

	        // Close the result set and statement
	        myRs.close();
	        myStmt.close();
	    } finally {
	        // clean up JDBC objects
	        close(myConn, myStmt, myRs);
	    }

	    return success;
	}

	public boolean saveClientResponses(Responses res) throws Exception {
	    boolean success = false;
	    int clientId = saveClientInfo(new ClientData(res.getClientName(), "12345"));

	    if (clientId != -1) {
	        Connection myConn = null;
	        PreparedStatement myStmt = null;

	        try {
	            myConn = dataSource.getConnection();
	            String sql = "INSERT INTO rs_responses (responseText, questionID, uid) VALUES (?, ?, ?)";
	            myStmt = myConn.prepareStatement(sql);

	            for (ClientResponse response : res.getClientResponses()) {
	                myStmt.setString(1, response.ResponseText());
	                myStmt.setInt(2, response.QuestionID());
	                myStmt.setInt(3, clientId);

	                myStmt.addBatch(); // Add the statement to the batch
	            }

	            int[] batchResults = myStmt.executeBatch(); // Execute the batch

	            // Check if all statements in the batch were executed successfully
	            for (int result : batchResults) {
	                if (result <= 0) {
	                    throw new SQLException("Failed to insert responses.");
	                }
	            }

	            success = true;
	        } finally {
	            close(myConn, myStmt, null);
	        }
	    } else {
	        throw new Exception("Error Saving Data");
	    }

	    return success;
	}

	
	public Questions fetchQosFromServer(int surveyId) throws Exception
	{
		Questions qos = new Questions();
		
		SurveyInfo currentSurveyInfo = fetchSurveyData(surveyId);
		qos.setAdminID(currentSurveyInfo.adminId());
		qos.setSurveyName(currentSurveyInfo.Title());
		qos.setSurveyID(surveyId);
		
		Connection myConn = null;
		PreparedStatement myStmt = null;
		ResultSet myRs = null;
		int success= -1;
		try {
			// get db connection
			myConn = dataSource.getConnection();
			
			// create sql for insert
			String sql = "select * from rs_quesions "
					   + "where surveyID = ? ";
					  
			
			myStmt = myConn.prepareStatement(sql);
			
			// set the param values for the student
			myStmt.setInt(1, surveyId);
			myRs = myStmt.executeQuery();
			List<ServerQuestions> qoss = new ArrayList();
			// execute sql insert
			while (myRs.next()) {
				String stitle = myRs.getString("questionText");
				int qId = myRs.getInt("questionID");
				
				qoss.add(new ServerQuestions(qId, stitle,surveyId));
			}
			
			if(qoss.equals(null)||qoss.size()==0)
			{
				throw new Exception("Error Fetching Survey Data!");
			}
			qos.setServerQuestionList(qoss);
			
		}
		finally {
			// clean up JDBC objects
			close(myConn, myStmt, null);
			
		}
		
		return qos;
	}
	
	private SurveyInfo fetchSurveyData(int surveyId) throws Exception
	{
		SurveyInfo si = null;
		Connection myConn = null;
		PreparedStatement myStmt = null;
		ResultSet myRs = null;
		int success= -1;
		try {
			// get db connection
			myConn = dataSource.getConnection();
			
			// create sql for insert
			String sql = "select * from rs_survey "
					   + "where surveyID = ? ";
					  
			
			myStmt = myConn.prepareStatement(sql);
			
			// set the param values for the student
			myStmt.setInt(1, surveyId);
			System.out.println("My Survey Id is "+surveyId);
			myRs = myStmt.executeQuery();
			// execute sql insert
			if (myRs.next()) {
				String stitle = myRs.getString("surveyTitle");
				int adminID = myRs.getInt("adminID");
				 
				si = new SurveyInfo(surveyId, stitle, adminID);
			}
			else {
				throw new Exception("Error Fetching Survey Data!");
			}				
			
			
		}
		finally {
			// clean up JDBC objects
			close(myConn, myStmt, null);
			
		}
		
		return si;
	}
	private void close(Connection myConn, Statement myStmt, ResultSet myRs) {

		try {
			if (myRs != null) {
				myRs.close();
			}
			
			if (myStmt != null) {
				myStmt.close();
			}
			
			if (myConn != null) {
				myConn.close();   // doesn't really close it ... just puts back in connection pool
			}
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
	}
}
