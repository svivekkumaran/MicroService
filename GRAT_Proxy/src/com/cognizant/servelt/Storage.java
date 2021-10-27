package com.cognizant.servelt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Storage {
	private static Map<String, List<AgentInteraction>> ixnStorage = null;
	private Storage(){
		
	}
	public static Map<String, List<AgentInteraction>> getIxnStorage(){
		if(null!=ixnStorage){
			return ixnStorage;
		}else{
			ixnStorage = new HashMap<>();
			return ixnStorage;
		}
	}
}
