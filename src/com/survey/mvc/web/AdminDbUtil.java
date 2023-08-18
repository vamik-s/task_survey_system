package com.survey.mvc.web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.survey.mvc.web.model.AdminCreatedSurvey;
import com.survey.mvc.web.model.AdminData;
import com.survey.mvc.web.model.AdminSurveyView;
import com.survey.mvc.web.model.Questions;


public class AdminDbUtil  {
	private static final long serialVersionUID = 1L;
       
	private DataSource dataSource;
	
    public AdminDbUtil(DataSource thedataSource) {
		dataSource = thedataSource;
		
	}


    public boolean RegisterAdmin(AdminData admin) throws Exception
    {
    	Connection myConn = null;
		PreparedStatement myStmt = null;
		boolean success= false;
		try {
			// get db connection
			myConn = dataSource.getConnection();
			
			// create sql for insert
			String sql = "insert into rs_admin "
					   + "(a_username, a_password) "
					   + "values (?, ?)";
			
			myStmt = myConn.prepareStatement(sql);
			
			// set the param values for the student
			myStmt.setString(1, admin.getA_username());
			myStmt.setString(2, admin.getA_password());
		
			
			// execute sql insert
			myStmt.execute();
			success=true;
		}
		finally {
			// clean up JDBC objects
			close(myConn, myStmt, null);
			
		}
		
		return success;
    }

    public int CheckLogInAdmin(AdminData admin) throws Exception
    {
    	Connection myConn = null;
		PreparedStatement myStmt = null;
		ResultSet myRs = null;
		int success= -1;
		try {
			// get db connection
			myConn = dataSource.getConnection();
			
			// create sql for insert
			String sql = "select a_password,aid from rs_admin "
					   + "where a_username = ? ";
					  
			
			myStmt = myConn.prepareStatement(sql);
			
			// set the param values for the student
			myStmt.setString(1, admin.getA_username());
			myRs = myStmt.executeQuery();
			// execute sql insert
			if (myRs.next()) {
				String adminPassword = myRs.getString("a_password");
				if(adminPassword.equals(admin.getA_password()))
				{
					success= myRs.getInt("aid");
				}
			}
			else {
				throw new Exception("Invalid Crdentials!");
			}				
			
			
		}
		finally {
			// clean up JDBC objects
			close(myConn, myStmt, null);
			
		}
		
		return success;
    }
    
    public int submitAdminQos(Questions qos) throws Exception
    {
    	int success= -1;
    	
    	if(RegisterSurvey(qos.getSurveyName(), qos.getAdminID(), qos.getSurveyID()))
    	{
    		
    		Connection myConn = null;
    		PreparedStatement myStmt = null;
    		ResultSet myRs = null;
    		
    		try {
    			// get db connection
    			myConn = dataSource.getConnection();
    			
    			
    			for(String questionText : qos.getQuestionText())
    			{
    				myStmt = null;
    				String sql = "INSERT INTO `rs_quesions` (`questionText`, `surveyID`) VALUES "
     					   + "(? , ?)";
    				
    				  // Set the param values for the question
    				myStmt = myConn.prepareStatement(sql);
    				myStmt.setString(1, questionText);
    		        myStmt.setInt(2, qos.getSurveyID());
    		        
    		        // Execute the query
    		        myStmt.executeUpdate();
    		        
    		        // Close the statement
    		        myStmt.close();
    			}
    			// create sql for insert
    			
    					  
    			
    		
    			// execute sql insert
    						
    			
    			
    		}
    		finally {
    			// clean up JDBC objects
    			close(myConn, myStmt, null);
    			
    		}
    	}
    	
    	
		
		return success;
    }
    
   
    public boolean RegisterSurvey( String sname ,int adminID,int surveyId) throws Exception
    {
    	Connection myConn = null;
		PreparedStatement myStmt = null;
		boolean success= false;
		try {
			// get db connection
			myConn = dataSource.getConnection();
		
			// create sql for insert
			
			String sql = "INSERT INTO `rs_survey` (`surveyID`, `surveyTitle`, `adminID`) "
					   + " VALUES "
					   + "(?, ?, ?)";
			
			myStmt = myConn.prepareStatement(sql);
			
			// set the param values for the student
			myStmt.setInt(1, surveyId);
			myStmt.setString(2, sname);
			myStmt.setInt(3,adminID);
		
			
			// execute sql insert
			myStmt.execute();
			success=true;
		}
		finally {
			// clean up JDBC objects
			close(myConn, myStmt, null);
			
		}
		
		return success;
    }

    public List<AdminSurveyView> retriveSurveyData(int adminId , int surveyID) throws Exception
    {
    	Connection myConn = null;
		PreparedStatement myStmt = null;
		ResultSet myRs = null;
		List<AdminSurveyView> listview = new ArrayList();
	
		try {
			// get db connection
			myConn = dataSource.getConnection();
			// create sql for insert
			String sql = "CALL GetAdminResponses(?,?) ";
			
			myStmt = myConn.prepareStatement(sql);
			
			// set the param values for the student
			myStmt.setInt(1, adminId);
			myStmt.setInt(2, surveyID);
			
			myRs = myStmt.executeQuery();
			
			while(myRs.next())
			{
				String userName = myRs.getString("u_username");
				String questionText = myRs.getString("questionText");
				String responseText = myRs.getString("responseText");
				listview.add(new AdminSurveyView(userName, questionText, responseText));
			}
						
			
			
		}
		finally {
			// clean up JDBC objects
			close(myConn, myStmt, null);
			
		}
		
		return listview;
    }
    public List<AdminCreatedSurvey> getSurveyList(int adminId ) throws Exception
    {
    	Connection myConn = null;
		PreparedStatement myStmt = null;
		ResultSet myRs = null;
		List<AdminCreatedSurvey> listview = new ArrayList();
	
		try {
			// get db connection
			myConn = dataSource.getConnection();
			// create sql for insert
			String sql = "SELECT * FROM rs_survey where adminID=? ";
			
			myStmt = myConn.prepareStatement(sql);
			
			// set the param values for the student
			myStmt.setInt(1, adminId);
			
			
			myRs = myStmt.executeQuery();
			
			while(myRs.next())
			{
				String surveyName = myRs.getString("surveyTitle");
				int sid = myRs.getInt("surveyID");
			
				listview.add(new AdminCreatedSurvey(sid,surveyName));
			}
						
			
			
		}
		finally {
			// clean up JDBC objects
			close(myConn, myStmt, null);
			
		}
		
		return listview;
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
