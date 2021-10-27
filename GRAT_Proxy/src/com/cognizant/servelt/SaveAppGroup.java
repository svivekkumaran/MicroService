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

/**
 * Servlet implementation class SaveAppGroup
 */
@WebServlet("/SaveAppGroup")
public class SaveAppGroup extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveAppGroup() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append(String.valueOf(insertAppGroup(request.getParameter("appGroupName"),request.getParameter("mediaType"),request.getParameter("comments"))));
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
	
	private int insertAppGroup(String appGroupName,String mediaId, String comments){
        String sql = "insert into appgroup(appgroupname,mediaid,comments)values(?,?,?)";
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql,1)){
        	 pstmt.setString(1, appGroupName);
             pstmt.setInt(2, Integer.parseInt(mediaId));
             if(null!=comments)
             pstmt.setString(3, comments);
             else
            	 pstmt.setString(3, "");
             pstmt.executeUpdate();
             ResultSet resultSet = pstmt.getGeneratedKeys();
             int id = resultSet.next()?resultSet.getInt(1):0;
             resultSet.close();
             return id;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }


}
