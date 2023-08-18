package com.survey.mvc.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.survey.mvc.web.model.ClientResponse;
import com.survey.mvc.web.model.Questions;
import com.survey.mvc.web.model.Responses;
import com.survey.mvc.web.model.ServerQuestions;

/**
 * Servlet implementation class ClientController
 */
@WebServlet("/ClientController")
public class ClientController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private ClientDbUtil clientDbUtil;
	
	@Resource(name="jdbc/rspl_survey")
	private DataSource dataSource;
	
	@Override
	public void init() throws ServletException {
		super.init();
		
		// create our student db util ... and pass in the conn pool / datasource
		try {
			clientDbUtil = new ClientDbUtil(dataSource);
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
				requiredFunction = "Home";
			}
			switch (requiredFunction) {
			
			case "Home":
				homepage(request, response);
				break;
				
			case "SaveResponse":
				submitResponse(request, response);
				break;
			case "LoadSurvey":
				loadCurrentSurvey(request, response);
				break;	
			
			default:
				homepage(request, response);
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			response.getWriter().print("err "+ex.getMessage());
		}
	}
	
	

	private void loadCurrentSurvey(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		int surveyId = Integer.parseInt(request.getParameter("surveyId"));
		String uname = request.getParameter("username");
		Questions qos=  clientDbUtil.fetchQosFromServer(surveyId);
		System.out.println("I have this amount of elements "+qos.getServerQuestionList().size());

		List<ServerQuestions> QOS =  qos.getServerQuestionList();
		
		request.setAttribute("surveyID",surveyId);
		request.setAttribute("username",uname);
		request.setAttribute("_questions",QOS);
		request.setAttribute("_sname",qos.getSurveyName());
	
		RequestDispatcher dispatcher = request.getRequestDispatcher("/client-view.jsp");
		dispatcher.forward(request, response);
		
	}

	private void homepage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		RequestDispatcher dispatcher = request.getRequestDispatcher("/client-view.jsp");
		dispatcher.forward(request, response);
	}

	private void submitResponse(HttpServletRequest request, HttpServletResponse response) throws Exception {
	
		String username = request.getParameter("username");
	    String[] integerStrings = request.getParameterValues("questionIDS[]");
		List<ClientResponse> cr = new ArrayList<ClientResponse>();
		String clientResponse = "answer_";
		int counter = 0;
		while(counter<integerStrings.length && request.getParameter(clientResponse+integerStrings[counter])!=null)
		{
			
			cr.add(new ClientResponse(
					Integer.parseInt(integerStrings[counter]),
							request.getParameter(clientResponse+integerStrings[counter]),
							0
							));
			System.out.println(counter + request.getParameter(clientResponse+integerStrings[counter]));
			counter++;
		}
			
		Responses clientResponses = new Responses();
		clientResponses.setClientResponses(cr);
		clientResponses.setClientName(username);
		if(clientDbUtil.saveClientResponses(clientResponses)) {
			
			RequestDispatcher dispatcher = request.getRequestDispatcher("/client-view-saved.html");
			dispatcher.forward(request, response);
		}
		else
		{
			
			RequestDispatcher dispatcher = request.getRequestDispatcher("/client-view-failed.html");
			dispatcher.forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
