package com.cognizant.servelt;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SaveCredentials
 */
@WebServlet("/SaveCredentials")
public class SaveCredentials extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveCredentials() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String MSUSERNAME = request.getParameter("msusername");
		String MSPASSWORD = request.getParameter("mspassword");
		String UNIXUSERNAME = request.getParameter("unixusername");
		String UNIXPASSWORD = request.getParameter("unixpassword");
		String SNOWUSERNAME = request.getParameter("snowusername");
		String SNOWPASSWORD = request.getParameter("snowpassword");
		response.getWriter().append(saveCredentials(MSUSERNAME,MSPASSWORD,UNIXUSERNAME,UNIXPASSWORD,SNOWUSERNAME,SNOWPASSWORD));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	public String saveCredentials(String msusername,String mspassword,String unixusername,String unixpassword,String snowusername,String snowpassword) {
		Properties writer = null;
		FileOutputStream out = null;
		try{
			
			writer = new Properties();
			writer.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("credentials.properties"));
			URL url = Thread.currentThread().getContextClassLoader().getResource("credentials.properties");
			out = new FileOutputStream(url.toURI().getPath());
			writer.put("ms.username",msusername );
			writer.put("ms.password",mspassword);
			writer.put("unix.username",unixusername);
			writer.put("unix.password",unixpassword);
			writer.put("snow.username",snowusername);
			writer.put("snow.password",snowpassword);
			writer.store(out, "Updated at "+new Date());
			out.close();
			return "true";
		}catch(Exception e){
			e.printStackTrace();
		}
		return "false";
	}

}
