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

import com.cognizant.pojo.BusinessUnit;

/**
 * Servlet implementation class Display
 */
@WebServlet("/getBusinessUnit")
public class GetBusinessUnit extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetBusinessUnit() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		response.getWriter().append(selectAll());
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
	
	private String selectAll() {
        String sql = "SELECT * FROM BusinessUnit";
        BusinessUnit bu = null;
        String response= null;
        List<BusinessUnit> buList = new ArrayList<>();
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            // loop through the result set
            while (bu!=null) {
            	bu = new BusinessUnit();
            	bu.setBUId(String.valueOf(rs.getInt("BUId")));
            	bu.setBUName(rs.getString("BUName"));
            	bu.setBUDescription(rs.getString("BUDescription"));
            	buList.add(bu);
            }
           response =  writeListToJsonArray(buList);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return response;
    }
	
	public String writeListToJsonArray(List<BusinessUnit> buList)  {  
	   try{
		    final ByteArrayOutputStream out = new ByteArrayOutputStream();
		    final ObjectMapper mapper = new ObjectMapper();
		    mapper.writeValue(out, buList);
		    final byte[] data = out.toByteArray();
		    return new String(data);
	   }catch(IOException e){
		   System.out.println(e.getMessage());
	   }
	   return null;
	   
	}

}
