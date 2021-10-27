package com.cognizant.servelt;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Servlet implementation class GetInteractions
 */
@WebServlet("/GetInteractions")
public class GetInteractions extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetInteractions() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		JSONArray finalResponse = new JSONArray();
		try{
			String agentId = request.getParameter("agentId");
			Map<Integer, byte[]> ixnmap = selectAgentInteraction(agentId);
			if(null!=ixnmap){
				for(Map.Entry<Integer, byte[]> i : ixnmap.entrySet()){
					ByteArrayInputStream in = new ByteArrayInputStream(i.getValue());
				    ObjectInputStream is = new ObjectInputStream(in);
				    AgentInteraction agentInteraction = (AgentInteraction) is.readObject();
				    JSONObject jsonObject = new JSONObject();
				    jsonObject.put("interactionUUID", agentInteraction.getInteractionUUID());
				    jsonObject.put("ixnId", i.getKey());
				    jsonObject.put("media", agentInteraction.getMedia());
				    jsonObject.put("ENT_ContactFirstNm", agentInteraction.getENT_ContactFirstNm());
				    jsonObject.put("ENT_ContactLastNm", agentInteraction.getENT_ContactLastNm());
				    jsonObject.put("ENT_SubFunction",agentInteraction.getENT_SubFunction());
				    jsonObject.put("ENT_Segment", agentInteraction.getENT_Segment());
				    jsonObject.put("ENT_Function",agentInteraction.getENT_Function());
				    jsonObject.put("Subject",agentInteraction.getSubject());
				    jsonObject.put("IxnTime",String.valueOf(new Timestamp(Long.parseLong(agentInteraction.getRoutingStartTime()))));
				    finalResponse.put(jsonObject);
				    in.close();
				    is.close();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		response.getWriter().append(finalResponse.toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
	
	private Map<Integer, byte[]> selectAgentInteraction(String agentId){
        String sql = "select ixnid,attachdata from interaction where agentId = ? and ticketflag=0 and date(ixntime)=date('now')  ";
        //String sql = "select ixnid,attachdata from interaction where agentId = ? and ticketflag=0   ";
        Map<Integer,byte[]> ixnlist = new LinkedHashMap<>();
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
        	 pstmt.setString(1, agentId);
        	 ResultSet resultSet =  pstmt.executeQuery();
        	 while(resultSet.next()){
        		 ixnlist.put(resultSet.getInt("ixnid"), resultSet.getBytes("attachdata"));
        	 }
        	 resultSet.close();
             return ixnlist;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

}
