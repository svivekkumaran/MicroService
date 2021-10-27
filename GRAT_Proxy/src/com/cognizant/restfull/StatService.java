package com.cognizant.restfull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.json.JSONObject;

@Path("/statservice")
public class StatService {
	
	@POST
	@Path("/pos")
	@Produces("text/html")
	public String getPOS(@FormParam("vq") String vq ){
		String result =  "\"{\"error\":\"unable to fech the POS data\"}";
		try{
			return String.valueOf(invokeUrl(vq).get("pos"));			
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	
	@POST
	@Path("/wpos")
	@Produces("application/json")
	public String getwqpos(@FormParam("vq") String vq){
		
		String result =  "\"{\"error\":\"unable to fech the WPOS data\"}";
		try{
			return String.valueOf(invokeUrl(vq).get("wpos"));
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	@POST
	@Path("/ewt")
	@Produces("text/html")
	public String getEWT(@FormParam("vq") String vq ){
		String result =  "\"{\"error\":\"unable to fech the EWT data\"}";
		
		try{
			return String.valueOf(invokeUrl(vq).get("ewt"));			
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public JSONObject invokeUrl(String vq){

		String output="";
		StringBuilder response  = new StringBuilder();
		try{
			
			
			URL url = new URL("http://apsrd8405.uhc.com:3072/urs/call/max/lvq/"+vq+"?tenant=Environment");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			
			while ((output = br.readLine()) != null) {
				response.append(output);
			}
			
			conn.disconnect();
			JSONObject json_res = new JSONObject(response.toString());
			
			return json_res;

		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

}
