package com.cognizant.servelt;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Servlet implementation class MoveLogFiles
 */
@WebServlet("/MoveLogFiles")
public class MoveLogFiles extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MoveLogFiles() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		JSONObject credential = fetchServerCredentials(request.getParameter("mediaType"));
    	JSONArray jsonArray = fetchServers(request.getParameter("mediaType"),credential);
    	SearchLogFiles searchLogFiles = new SearchLogFiles();
    	searchLogFiles.SearchLogs(jsonArray,request.getParameter("interactionId"));
		response.getWriter().append("success");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	private JSONObject fetchServerCredentials(String parameter) {
		String sql = "select serverType from appgroup where mediaid = (select mediaid from media where medianame = '"+parameter+"' COLLATE NOCASE) ";
        JSONObject ag = null;
       
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            // loop through the result set
        	String type = null;
            if (rs.next()) {
            	type = rs.getString("servertype");
            }
            System.out.println(type);
            ag = getServerCredentials(type);
        } catch (SQLException e) {
           e.printStackTrace();
        }
        return ag;
	}

	private JSONObject getServerCredentials(String type) {
		// TODO Auto-generated method stub
		JSONObject obj = new JSONObject();
		Properties properties = getProperties();
		if(null!=properties){
			if("UNIX".equalsIgnoreCase(type)){
				obj.put("User", properties.getProperty("unix.username"));
				obj.put("Password", properties.getProperty("unix.password"));
			}else if("MS".equalsIgnoreCase(type)){
				obj.put("User", properties.getProperty("ms.username"));
				obj.put("Password", properties.getProperty("ms.password"));
			}
		}
		return obj;
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
	
	private Properties getProperties() {		
		Properties properties =  null;
		try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("credentials.properties")){
			if(is != null) {				
				properties = new Properties();
				properties.load(is);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			properties = null;
		}
		return properties;
	}

	private JSONArray fetchServers(String parameter,JSONObject cre) {
		String sql = "select servername,logpath from servers where appgroupid = (select appgroupid from appgroup where mediaid = (select mediaid from media where medianame = '"+parameter+"' COLLATE NOCASE))";
        JSONObject ag = null;
        JSONArray jsonArray  = new JSONArray();
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            // loop through the result set
            while (rs.next()) {
            	ag = new JSONObject();
            	ag.put("Host",rs.getString("servername"));
            	ag.put("LogPath",rs.getString("logpath"));
            	ag.put("User", cre.get("User"));
            	ag.put("Password", cre.get("Password"));
            	jsonArray.put(ag);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return jsonArray;
	}

}
