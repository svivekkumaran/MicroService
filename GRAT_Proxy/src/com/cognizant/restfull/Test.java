package com.cognizant.restfull;

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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class Test{
	
	public static void main(String[] args) {
		List<String> ixnIds = new Test().getIxnIds();
		int length = ixnIds.size();
		String request = null;
		for(int i=0;i<length;i++){
			request = "{\"interactionId\":\""+ixnIds.get(i)+"\"}";
			System.out.println(request);
			invokeService(request);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private static void invokeService(String request){
		BufferedReader httpResponseReader = null;
		try {
			URL serverUrl = new URL("http://vetsvra00002562:8081/SMSTimeStampManager/storageHandler/updateIxnDetails");
			HttpURLConnection urlConnection = (HttpURLConnection) serverUrl.openConnection();
			urlConnection.setDoOutput(true);
			urlConnection.setRequestProperty("Content-Type", "application/json");
			urlConnection.setRequestMethod("POST");
			urlConnection.setConnectTimeout(10000);
			OutputStream outputStream = urlConnection.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
			writer.write(request);
			writer.close();
			outputStream.close();
			urlConnection.connect();
			httpResponseReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String lineRead;
			while((lineRead = httpResponseReader.readLine()) != null) {
				System.out.println(lineRead);
			}
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		} finally {
			if (httpResponseReader != null) {
				try {
					httpResponseReader.close();
				} catch (IOException ioe) {
					System.out.println(ioe.getMessage());
				}
			}
		}
	}

	private Connection connect() {
		// MySQL connection string
		String url = "jdbc:mysql://localhost:3306/time_stamp?user=root&password=myadmin&useUnicode=true&characterEncoding=UTF-8";
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url);
		} catch (SQLException | ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
		return conn;
	}
	
	
	
	
	private String readOutboundMessages(String phoneId){
		StringBuilder sb = new StringBuilder();
		BufferedReader httpResponseReader = null;
		try {
			URL serverUrl = new URL("https://ocp.optum.com/smsmgrstage/v1/outbound-messages?phone-id="+phoneId+"&sort-by=created-at&sort-order=desc");
			HttpURLConnection urlConnection = (HttpURLConnection) serverUrl.openConnection();
			String tokenResponse = getOptumMessangerToken();
			JSONObject tokenObject = new JSONObject(tokenResponse);
			urlConnection.setRequestProperty("Authorization", "Bearer " +tokenObject.getString("access_token") );
			urlConnection.setRequestProperty("Accept", "application/json");
			urlConnection.setRequestMethod("GET");
			urlConnection.setConnectTimeout(10000);
			urlConnection.connect();
			httpResponseReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String lineRead;
			while((lineRead = httpResponseReader.readLine()) != null) {
				sb.append(lineRead);
			}
		} catch (IOException | JSONException ioe) {
			System.out.println(ioe.getMessage());
		} finally {
			if (httpResponseReader != null) {
				try {
					httpResponseReader.close();
				} catch (IOException ioe) {
					System.out.println(ioe.getMessage());
				}
			}
		}
		return sb.toString();

	}

	private String getOptumMessangerToken(){
		String requestParam = "client_id=c8a51391-e26e-4dd4-95ff-0af65b2dfcb5&client_secret=63ebb0c3-add0-4ce5-83e1-58bbbcf3d68b&grant_type=client_credentials";
		StringBuilder sb = new StringBuilder();	
		BufferedReader httpResponseReader = null;
		try {
			URL serverUrl = new URL("https://ocp-stage.optum.com/authstage/oauth2/token");
			HttpURLConnection urlConnection = (HttpURLConnection) serverUrl.openConnection();
			urlConnection.setDoOutput(true);
			urlConnection.setRequestProperty("Accept", "application/json");
			urlConnection.setRequestMethod("POST");
			urlConnection.setConnectTimeout(10000);
			OutputStream outputStream = urlConnection.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
			writer.write(requestParam);
			writer.close();
			outputStream.close();
			urlConnection.connect();
			httpResponseReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String lineRead;
			while((lineRead = httpResponseReader.readLine()) != null) {
				sb.append(lineRead);
			}
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		} finally {
			if (httpResponseReader != null) {
				try {
					httpResponseReader.close();
				} catch (IOException ioe) {
					System.out.println(ioe.getMessage());
				}
			}
		}
		return sb.toString();
	}
	
 private List<String> getIxnIds(){
	 List<String> idList = new ArrayList<String>();
	 Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try {
			conn = this.connect();
			//insert interaction
			pstmt = conn.prepareStatement("select ixn from ixntable");
			
			resultSet= pstmt.executeQuery();
			while(resultSet.next()){
				idList.add(resultSet.getString(1));
			}
			resultSet.close();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				if(null!=resultSet && !resultSet.isClosed()){
					resultSet.close();
				}
				if (null!=pstmt && !pstmt.isClosed()) { 
					pstmt.close();
				}
				if(null!=conn && !conn.isClosed()){
					conn.close();
				}
			}catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		return idList;
 }
}

