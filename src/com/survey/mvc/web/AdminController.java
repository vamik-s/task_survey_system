package com.survey.mvc.web;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import com.survey.mvc.web.model.AdminCreatedSurvey;
import com.survey.mvc.web.model.AdminData;
import com.survey.mvc.web.model.AdminSurveyView;
import com.survey.mvc.web.model.Questions;
import java.util.ArrayList;

/**
 * Servlet implementation class AdminController
 */
@WebServlet("/AdminController")
public class AdminController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	
	private AdminDbUtil adminDbUtil;
	
	@Resource(name="jdbc/rspl_survey")
	private DataSource dataSource;
	
	@Override
	public void init() throws ServletException {
		super.init();
		
		// create our student db util ... and pass in the conn pool / datasource
		try {
			adminDbUtil = new AdminDbUtil(dataSource);
		}
		catch (Exception exc) {
			throw new ServletException(exc);
		}
	}
 
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		try {
			String requiredFunction = request.getParameter("function");
			
			if (requiredFunction == null) {
				requiredFunction = "register";
			}
			switch (requiredFunction) {
			
			case "Register":
				registerAdmin(request, response);
				break;
				
			case "Login":
				checkLoginAdmin(request, response);
				break;
			case "CreateSurvey":
				submitQuestions(request, response);
				break;	
			case "ViewSurvey":
				viewSurvey(request, response);
				break;
			case "SelectSurvey":
				selectSurvey(request, response);
				break;
			case "GetSurveyList":
				getSurveyList(request, response);
				break;	
			default:
				registerAdmin(request, response);
			}
			
		}
		catch(Exception ex)
		{
			response.getWriter().print("err "+ex.getMessage());
		}
	}
	
	
	

	private void getSurveyList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		
		  List<AdminCreatedSurvey> adminCreatedSurveys= new ArrayList();
		  HttpSession session = request.getSession();
		  int adminID = (int) session.getAttribute("admin_id");
		  adminCreatedSurveys=adminDbUtil.getSurveyList(adminID);
		  session.setAttribute("_allSurvey", adminCreatedSurveys);
		  
		   RequestDispatcher dispatcher = request.getRequestDispatcher("/admin-view-survey.jsp");
		   dispatcher.forward(request, response);
		
	}

	private void selectSurvey(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		int surveyId = Integer.parseInt(request.getParameter("sid"));
		  HttpSession session = request.getSession();
		  session.setAttribute("sid", surveyId);
		  viewSurvey(request,response);
		
	}

	private void viewSurvey(HttpServletRequest request, HttpServletResponse response) throws Exception {
		  HttpSession session = request.getSession();
		  int adminID = (int)session.getAttribute("admin_id");
		  int surveyId = (int) session.getAttribute("sid");
		  
		   List<AdminSurveyView> adminSurveyViews = adminDbUtil.retriveSurveyData(adminID, surveyId);
		   session.setAttribute("_sRecords", adminSurveyViews);
		   RequestDispatcher dispatcher = request.getRequestDispatcher("/admin-view-survey.jsp");
		   dispatcher.forward(request, response);
		
	}

	private void checkLoginAdmin(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String username = request.getParameter("uname");
		String password = request.getParameter("upassword");
		AdminData temp = new AdminData(username,password);
		int db_response=adminDbUtil.CheckLogInAdmin(temp);
		if(db_response>=0)
		{
	
		    HttpSession session = request.getSession();
	        
	        // Set session data (attributes)
	        session.setAttribute("admin_username", temp.getA_username());
	        session.setAttribute("admin_id", db_response);
			
			RequestDispatcher dispatcher = request.getRequestDispatcher("/admin-landing-page.html");
			dispatcher.forward(request, response);
		}
		else
		{
			response.getWriter().print("InValid");
		}
	
	}

	private void registerAdmin(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String username = request.getParameter("uname");
		String password = request.getParameter("upassword");
		AdminData temp = new AdminData(username,password);
		
		if(adminDbUtil.RegisterAdmin(temp))
		{
			response.getWriter().print("Success ");
			RequestDispatcher dispatcher = request.getRequestDispatcher("/admin-log-in.html");
			dispatcher.forward(request, response);
		}
		else
		{
			response.getWriter().print("Fail");
			RequestDispatcher dispatcher = request.getRequestDispatcher("/admin-register.html");
			dispatcher.forward(request, response);
		}
		
	}

	private void submitQuestions(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String username = request.getParameter("uname");
		String surveyName = request.getParameter("sname");
		int adminId = Integer.parseInt( request.getParameter("aid"));
		int surveyId = Integer.parseInt(request.getParameter("sid"));
		List<String> questions = new ArrayList<>();
		String questionParamPrefix  = "question_";
		int questionCounter  = 0;

		while(request.getParameter(questionParamPrefix +questionCounter )!=null)
		{
			
			questions.add(request.getParameter(questionParamPrefix +questionCounter ));
			System.out.println(questionCounter  + request.getParameter(questionParamPrefix +questionCounter));
			questionCounter ++;
		}
			
		Questions surveyQuestions  = new Questions(questions, surveyId, adminId, surveyName);
		adminDbUtil.submitAdminQos(surveyQuestions );
	
		request.setAttribute("_USERNAME", username);
		request.setAttribute("_USERID", surveyQuestions .getAdminID());
		RequestDispatcher dispatcher = request.getRequestDispatcher("/admin-dashboard.jsp");
		dispatcher.forward(request, response);

		
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		doGet(request, response);
	}

}
