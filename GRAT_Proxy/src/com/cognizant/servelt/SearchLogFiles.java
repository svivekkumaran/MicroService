package com.cognizant.servelt;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchLogFiles {

	public void SearchLogs(JSONArray jArr,String IXN){

		try {
			JSONObject jobj = null;
			ThreadPoolExecutor executor = null;
			String sSearchIXN = IXN;
			try {
				executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(jArr.length());
				CopyLogFiles task = null;
				for (int i = 1;i<jArr.length();i++){
					jobj = jArr.getJSONObject(i);
					task = new CopyLogFiles(sSearchIXN, jobj);
					executor.execute(task);
				}

				executor.shutdown();
				while(!executor.isTerminated()){}
				System.out.println("Completed Task Count" + executor.getCompletedTaskCount());

			}catch (Exception e) {
				e.printStackTrace();
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}


}