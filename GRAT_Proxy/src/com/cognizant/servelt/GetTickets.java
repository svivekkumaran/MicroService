package com.cognizant.servelt;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Servlet implementation class GetTickets
 */
@WebServlet("/GetTickets")
public class GetTickets extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetTickets() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject result = new JSONObject();;
		try{
			String username = request.getParameter("agentName");
			JSONArray finalResponse = selectAgentInteraction(username);
			if(finalResponse.length()>0){
				result.put("records", "true");
				result.put("data", finalResponse);
			}else{
				result.put("records", "false");
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		response.getWriter().append(result.toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	private Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:C:/UHG/sqliteDB/tmdb.db";
        Connection conn = null;
        try {
        	Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(url);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
	
	private JSONArray selectAgentInteraction(String agentId){
        String sql = "select i.attachdata,t.status,t.ticketid,t.sys_id,t.createdtime from Interaction i, Tickets t where i.agentid=? and i.ixnid=t.ixnid";
        JSONArray finalResponse = new JSONArray();
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
        	 pstmt.setString(1, agentId);
        	 ResultSet resultSet =  pstmt.executeQuery();
        	 while(resultSet.next()){
        		 ByteArrayInputStream in = new ByteArrayInputStream(resultSet.getBytes("attachdata"));
				    ObjectInputStream is = new ObjectInputStream(in);
				    AgentInteraction agentInteraction = (AgentInteraction) is.readObject();
				    JSONObject jsonObject = new JSONObject();
				    jsonObject.put("interactionUUID", agentInteraction.getInteractionUUID());
				    jsonObject.put("media", agentInteraction.getMedia());
				    jsonObject.put("ENT_ContactFirstNm", agentInteraction.getENT_ContactFirstNm());
				    jsonObject.put("ENT_ContactLastNm", agentInteraction.getENT_ContactLastNm());
				    jsonObject.put("ENT_Segment", agentInteraction.getENT_Segment());
				    jsonObject.put("ENT_Function",agentInteraction.getENT_Function());
				    jsonObject.put("ticketTime",resultSet.getString("createdtime"));
				    jsonObject.put("tstate", resultSet.getInt("status"));
				    jsonObject.put("tnumber", resultSet.getString("ticketid"));
				    jsonObject.put("sys_id", resultSet.getString("sys_id"));
				    finalResponse.put(jsonObject);
				    in.close();
				    is.close();
        	 }
        	 resultSet.close();
             return finalResponse;
        } catch (SQLException | IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

}
