package com.cognizant.agent;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.genesyslab.platform.applicationblocks.com.ConfigException;

/**
 * Servlet implementation class GetAgent
 */
@WebServlet("/GetAgent")
public class GetAgent extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetAgent() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ReadAgentDetails readAgentDetails = new ReadAgentDetails();
		JSONArray responseArray = null;
		try {
			responseArray = readAgentDetails.readAndPrintAllCfgPersons(readAgentDetails.getIConfService(request.getParameter("environment")),request.getParameter("skillName"), Integer.parseInt(request.getParameter("skillLevel")));
		} catch (NumberFormatException | ConfigException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.getWriter().append(responseArray.toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
