package com.cognizant.restfull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.json.JSONObject;

@Path("/hoopservice")
public class HoopService {
	@POST
	@Path("/service_state")
	@Produces("application/json")
	public String getServiceState(@FormParam("chatType") String chatType){
		JSONObject json = null;
		try{
			json = invokeUrl(chatType);

		}catch(Exception e){
			e.printStackTrace();
		}
		return json.getString("HOOP_OpenFlag");

	}

	@POST
	@Path("/service_message")
	@Produces("application/json")
	public String getOpenMessage(@FormParam("chatType") String chatType){
		JSONObject json = null;
		try{
			json = invokeUrl(chatType);
			if("true".equalsIgnoreCase(json.getString("HOOP_OpenFlag"))){
				return json.getString("HOOP_Open_Message");
			}else if("true".equalsIgnoreCase(json.getString("HOOP_HolidayFlag"))){
				return json.getString("HOOP_Holiday_Msg");
			}else{
				return json.getString("HOOP_Closed_Message");
			}

		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	@POST
	@Path("/hoop_begintime")
	@Produces("application/json")
	public String getBeginTime(@FormParam("chatType") String chatType){
		JSONObject json = null;
		try{
			json = invokeUrl(chatType);
			return json.getString("HOOP_BeginTime");
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	@POST
	@Path("/hoop_endtime")
	@Produces("application/json")
	public String getEndTime(@FormParam("chatType") String chatType){
		JSONObject json = null;
		try{
			json = invokeUrl(chatType);
			return json.getString("HOOP_EndTime");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return "";
	}
	
	@POST
	@Path("/hoop_lefttime")
	@Produces("application/json")
	public String getLeftTime(@FormParam("chatType") String chatType){
		JSONObject json = null;
		try{
			json = invokeUrl(chatType);
			return json.getString("HOOP_LeftTime");
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	public JSONObject invokeUrl(String chatType){

		String input = "";
		String output="";
		StringBuilder response  = new StringBuilder();
		try{
			
			HoopRequestData.changeChatType(chatType);
			URL url = new URL("http://apsrd8403.uhc.com:8780/genesys-rules-engine/knowledgebase/huskies.project.chat.hoop.rt");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			input = HoopRequestData.json.toString();

			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();
			
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			
			while ((output = br.readLine()) != null) {
				response.append(output);
			}
			System.out.println("Response "+response.toString());
			conn.disconnect();
			JSONObject json_res = new JSONObject(response.toString());
			
			return json_res.getJSONObject("knowledgebase-response").getJSONArray("inOutFacts").getJSONObject(1).getJSONObject("fact").getJSONObject("rulesResults").getJSONObject("rule_results");

		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
