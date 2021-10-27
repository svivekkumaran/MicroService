package com.cognizant.restfull;

import org.json.JSONObject;

public class HoopRequestData {
	public static String data = "{\"knowledgebase-request\":{\"inOutFacts\":{\"named-fact\":[{\"id\":\"Env\",\"fact\":{\"@class\":\"huskies.project.chat.hoop.rt._GRS_Environment\",\"phase\":\"1.Classification\"}},{\"id\":\"RulesResults\",\"fact\":{\"@class\":\"com.genesyslab.brs.api.RulesResults\",\"rulesResults\":{\"rule_results\":{\"ixnType\":\"chat\",\"ENT_ChatType\":\"OptumRX\",\"ENT_Segment\":\"BhvPhysHealthOps\",\"ENT_Location\":\"NJ\",\"ENT_Language\":\"English\"}}}}]}}}";
	 
	public static JSONObject json = new JSONObject(data);
	
	public static void changeClass(String className){
		json.getJSONObject("knowledgebase-request").getJSONObject("inOutFacts").getJSONArray("named-fact").getJSONObject(0).getJSONObject("fact").put("@class", className+"._GRS_Environment");
	}
	
	public static void changeChatType(String chatType){
		json.getJSONObject("knowledgebase-request").getJSONObject("inOutFacts").getJSONArray("named-fact").getJSONObject(1).getJSONObject("fact").getJSONObject("rulesResults").getJSONObject("rule_results").put("ENT_ChatType", chatType);
		System.out.println(json.toString());
	}
		public static void main(String[] args){
		///System.out.println(new HoopService().invokeUrl("OptumRX").getString("HOOP_OpenFlag"));
		
		System.out.println("Response--"+new StatService().invokeUrl("VQ_Digital_Test"));
	}
	
}
