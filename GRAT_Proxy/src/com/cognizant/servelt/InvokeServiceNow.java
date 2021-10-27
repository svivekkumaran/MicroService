package com.cognizant.servelt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Servlet implementation class CheckFileType
 */
@WebServlet("/CreateIncident")
public class InvokeServiceNow extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InvokeServiceNow() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		JSONObject result=new JSONObject();
		String usernameColonPassword = "admin:Pointel@123";
        String basicAuthPayload = "Basic " + Base64.getEncoder().encodeToString(usernameColonPassword.getBytes());
        System.out.println(request.getParameter("incident"));
        JSONObject obj = new JSONObject(request.getParameter("incident"));
        String image = obj.getString("u_screenshot");
        obj.remove("u_screenshot");
        StringBuilder sb = new StringBuilder();
        BufferedReader httpResponseReader = null;
        try {
            URL serverUrl = new URL("https://dev68143.service-now.com/api/now/table/incident");
            HttpURLConnection urlConnection = (HttpURLConnection) serverUrl.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestMethod("POST");
            urlConnection.addRequestProperty("Authorization", basicAuthPayload);
            urlConnection.setConnectTimeout(10000);
            urlConnection.connect();
            String data = obj.toString();
            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(data);
            writer.close();
            outputStream.close();
            httpResponseReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String lineRead;
            while((lineRead = httpResponseReader.readLine()) != null) {
           	 sb.append(lineRead);
            }
            JSONObject jObj = new JSONObject(sb.toString());
            if(jObj.has("result")){
            	result.put("state", "success");
            	result.put("message", "ticket created successfully!");
            	insertTicketPayLoad(jObj.getJSONObject("result"),request.getParameter("ixnId"));
            	updateTicketFlag(request.getParameter("ixnId"));
            	uploadAttachment(image,jObj.getJSONObject("result").getString("sys_id"));
            }else{
            	result.put("state", "error");
            	result.put("message", "unable to create the ticket at this moment!");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            result.put("state", "error");
            result.put("message", ioe.getMessage());
        } finally {
 
            if (httpResponseReader != null) {
                try {
                    httpResponseReader.close();
                } catch (IOException ioe) {
                	result.put("state", "error");
                    result.put("message", ioe.getMessage());
                }
            }
        }
        
		response.getWriter().append(result.toString());
	}
	
	private void uploadAttachment(String image,String sys_id){
		String usernameColonPassword = "admin:Pointel@123";
        String basicAuthPayload = "Basic " + Base64.getEncoder().encodeToString(usernameColonPassword.getBytes());
        StringBuilder sb = new StringBuilder();
        BufferedReader httpResponseReader = null;
        JSONObject payload = new JSONObject();
        payload.put("payload", image);
        payload.put("agent", "AttachmentCreator");
        payload.put("topic", "AttachmentCreator");
        payload.put("source","incident:"+sys_id);
        payload.put("name", "attachment.png:image/png");
        try {
            URL serverUrl = new URL("https://dev68143.service-now.com/api/now/table/ecc_queue");
            HttpURLConnection urlConnection = (HttpURLConnection) serverUrl.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestMethod("POST");
            urlConnection.addRequestProperty("Authorization", basicAuthPayload);
            urlConnection.setConnectTimeout(10000);
            urlConnection.connect();
            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(payload.toString());
            writer.close();
            outputStream.close();
            httpResponseReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String lineRead;
            while((lineRead = httpResponseReader.readLine()) != null) {
           	 sb.append(lineRead);
            }
            JSONObject jObj = new JSONObject(sb.toString());
            if(jObj.has("result")){
            	System.out.println("success");
            }else{
            	System.out.println("failed");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            
        } finally {
 
            if (httpResponseReader != null) {
                try {
                    httpResponseReader.close();
                } catch (IOException ioe) {
                	
                	System.out.println( ioe.getMessage());
                }
            }
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
	
	private int insertTicketPayLoad(JSONObject jObj,String ixnId){
        String sql = "insert into Tickets(ixnid,status,ticketid,sys_id,createdtime,incidentpayload)values(?,?,?,?,?,?)";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql,1)){
        	 pstmt.setInt(1, Integer.parseInt(ixnId));
             pstmt.setInt(2, Integer.parseInt(jObj.getString("state")));
             pstmt.setString(3, jObj.getString("number"));
             pstmt.setString(4, jObj.getString("sys_id"));
             pstmt.setString(5, String.valueOf(new Timestamp(System.currentTimeMillis())));
             pstmt.setString(6, jObj.toString());
             int id = pstmt.executeUpdate();
             return id;
        } catch (SQLException e) {
           e.printStackTrace();
        }
        return 0;
    }
	private void updateTicketFlag(String ixnId){
        String sql = "update interaction set ticketflag = 1 where ixnid=?";
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql,1)){
        	 pstmt.setInt(1, Integer.parseInt(ixnId));
        	 pstmt.executeUpdate();
        } catch (SQLException e) {
           e.printStackTrace();
        }
    }
}
