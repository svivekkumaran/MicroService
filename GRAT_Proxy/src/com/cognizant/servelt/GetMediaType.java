package com.cognizant.servelt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;



/**
 * Servlet implementation class Display
 */
@WebServlet("/getMediaType")
public class GetMediaType extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetMediaType() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String isAppGroup = request.getParameter("isAppGroup");
		String sql = "SELECT * From Media";
		
		if(null!=isAppGroup && "true".equalsIgnoreCase(isAppGroup)){
			sql = "select * from media where mediaid not in (select mediaid from appgroup) ";
		}
		response.getWriter().append(selectAll(sql));
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
	
	private String selectAll(String sql){
        MediaType mt = null;
        String response= null;
        List<MediaType> mtList = new ArrayList<>();
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            // loop through the result set
            while (rs.next()) {
            	mt = new MediaType();
            	mt.setMediaId(String.valueOf(rs.getInt("MediaId")));
            	mt.setMediaName(rs.getString("MediaName"));
            	mtList.add(mt);
            }
           response =  writeListToJsonArray(mtList);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return response;
    }
	
	public String writeListToJsonArray(List<MediaType> mtList)  {  
	   try{
		    final ByteArrayOutputStream out = new ByteArrayOutputStream();
		    final ObjectMapper mapper = new ObjectMapper();
		    mapper.writeValue(out, mtList);
		    final byte[] data = out.toByteArray();
		    return new String(data);
	   }catch(IOException e){
		   System.out.println(e.getMessage());
	   }
	   return null;
	   
	}

}
