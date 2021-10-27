package com.cognizant.servelt;

import java.io.IOException;
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

/**
 * Servlet implementation class GetServers
 */
@WebServlet("/GetServers")
public class GetServers extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetServers() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String appgroupId = request.getParameter("id");
		if(null!=appgroupId)
			response.getWriter().append(getAppServers(appgroupId));
		else{
			response.getWriter().append("error");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	public String getAppServers(String appgroupId){
		String sql="select servername,port,logpath from servers where appgroupid=?";
		JSONArray result = new JSONArray();
        JSONArray jsonArr = null;
        try (Connection conn = this.connect();
        		PreparedStatement pstmt = conn.prepareStatement(sql)){
       	 		pstmt.setInt(1, Integer.parseInt(appgroupId));
       	 		ResultSet resultSet =  pstmt.executeQuery();
            // loop through the result set
            while (resultSet.next()) {
            	jsonArr = new JSONArray();
            	jsonArr.put(resultSet.getString("servername"));
            	jsonArr.put(resultSet.getInt("port"));
            	jsonArr.put(resultSet.getString("logpath"));
            	result.put(jsonArr);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result.toString();
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

}
