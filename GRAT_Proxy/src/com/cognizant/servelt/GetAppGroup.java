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
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Servlet implementation class GetAppGroup
 */
@WebServlet("/GetAppGroup")
public class GetAppGroup extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetAppGroup() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// TODO Auto-generated method stub
		
		String forAppTable = request.getParameter("forAppTable");
		String registered = request.getParameter("registered");
		if(null!=forAppTable && "true".equalsIgnoreCase(forAppTable))
			response.getWriter().append(selectAppGroupOnly());
		else if(null!=registered && "true".equalsIgnoreCase(registered))
			response.getWriter().append(selectRegisteredAppGroup());
		else
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
	
	private String selectAll(){
		String sql="SELECT a.appgroupname, a.appgroupid,m.medianame,a.comments from appgroup a, media m where m.mediaid = a.mediaid";
        AppGroup ag = null;
        String response= null;
        List<AppGroup> agList = new ArrayList<>();
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            // loop through the result set
            while (rs.next()) {
            	ag = new AppGroup();
            	ag.setAppGroupId((String.valueOf(rs.getInt("appgroupid"))));
            	ag.setAppGroupName(rs.getString("appgroupname"));
            	ag.setMediaName(rs.getString("medianame"));
            	ag.setComments(rs.getString("comments"));
            	agList.add(ag);
            }
           response =  writeListToJsonArray(agList);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return response;
    }
	
	private String selectAppGroupOnly(){
		String sql = "select appgroupid,appgroupname from appgroup where appgroupid not in (select appgroupid from servers)";
        JSONObject ag = null;
        String response= null;
        JSONArray jsonArray  = new JSONArray();
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            // loop through the result set
            while (rs.next()) {
            	ag = new JSONObject();
            	ag.put("appGroupId",String.valueOf(rs.getInt("appgroupid")));
            	ag.put("appGroupName",rs.getString("appgroupname"));
            	jsonArray.put(ag);
            }
           response = jsonArray.toString();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return response;
    }
	
	private String selectRegisteredAppGroup(){
		String sql = "SELECT DISTINCT a.appgroupname, a.appgroupid,m.medianame from appgroup a, media m ,servers s where m.mediaid = a.mediaid and a.appgroupid = s.appgroupid";
		AppGroup ag = null;
        String response= null;
        List<AppGroup> agList = new ArrayList<>();
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            // loop through the result set
            while (rs.next()) {
            	ag = new AppGroup();
            	ag.setAppGroupId((String.valueOf(rs.getInt("appgroupid"))));
            	ag.setAppGroupName(rs.getString("appgroupname"));
            	ag.setMediaName(rs.getString("medianame"));
            	agList.add(ag);
            }
           response =  writeListToJsonArray(agList);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return response;
	}
	
	public String writeListToJsonArray(List<AppGroup> agList)  {  
	   try{
		    final ByteArrayOutputStream out = new ByteArrayOutputStream();
		    final ObjectMapper mapper = new ObjectMapper();
		    mapper.writeValue(out, agList);
		    final byte[] data = out.toByteArray();
		    return new String(data);
	   }catch(IOException e){
		   System.out.println(e.getMessage());
	   }
	   return null;
	   
	}
}
