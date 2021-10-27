package com.cognizant.servelt;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Servlet implementation class GetCredentials
 */
@WebServlet("/GetCredentials")
public class GetCredentials extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetCredentials() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Properties properties = getProperties() ;
		JSONObject jsonObject  = new JSONObject();
		String res = null;
		if(null!=properties){
			
			jsonObject.put("msusername", properties.getProperty("ms.username"));
			jsonObject.put("mspassword", properties.getProperty("ms.password"));
			jsonObject.put("unixusername", properties.getProperty("unix.username"));
			jsonObject.put("unixpassword", properties.getProperty("unix.password"));
			jsonObject.put("snowusername", properties.getProperty("snow.username"));
			jsonObject.put("snowpassword", properties.getProperty("snow.password"));
			
		}
		res = jsonObject.toString();
		response.getWriter().append(res);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
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

}
