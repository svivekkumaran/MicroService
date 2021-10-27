package com.cognizant.servelt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Servlet implementation class SaveApps
 */
@WebServlet("/SaveApps")
public class SaveApps extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveApps() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String apps = request.getParameter("servers");
		String appGroupId = request.getParameter("appGroupId");
		String serverType = request.getParameter("serverType");
		JSONArray jsonArray = new JSONArray(apps);
		int res = insertServers(appGroupId,jsonArray);
		updateServerType(serverType,Integer.parseInt(appGroupId));
		
		response.getWriter().append(String.valueOf(res));
	}

	private void updateServerType(String serverType,int appGroupId) {
		String sql = "update appgroup set servertype = ? where appgroupid = ?";
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
        		pstmt.setString(1, serverType);
        		pstmt.setInt(2, appGroupId);
        		pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
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
	
	private int insertServers(String appGroupId ,JSONArray jsonArray){
        String sql = "insert into servers(servername,port,logpath,appgroupid)values(?,?,?,?)";
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
        		int length = jsonArray.length();
        		for(int i=0;i<length;i++){
        			JSONObject obj = (JSONObject) jsonArray.get(i);
        			pstmt.setString(1, obj.getString("hostname"));
        			pstmt.setInt(2, Integer.parseInt(obj.getString("port")));
        			pstmt.setString(3, obj.getString("logpath"));
        			pstmt.setInt(4, Integer.parseInt(appGroupId));
        			pstmt.executeUpdate();
        		}
        		
        		return 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

}
