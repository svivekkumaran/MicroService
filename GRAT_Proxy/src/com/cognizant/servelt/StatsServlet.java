package com.cognizant.servelt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jettison.json.JSONObject;

/**
 * Servlet implementation class StatsServlet
 */
@WebServlet("/StatsServlet")
public class StatsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StatsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		StringBuffer response1 = new StringBuffer();
		StringBuffer response2 = new StringBuffer();
		BufferedReader bufferedReader = null;
		JSONObject finalResponse = new JSONObject();
		int output =0;
		try{
			
			ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			Future<InputStream> responseFuture1 = executor.submit(new Request("http://stg-rhel-ctc-URSp.uhc.com:3072/urs/call/max/lvq/VQ_OPT_OptumRx_CustSrvc_Member?tenant=Environment"));
			Future<InputStream> responseFuture2 = executor.submit(new Request("http://stg-rhel-elr-URSp.uhc.com:3072/urs/call/max/lvq/VQ_OPT_OptumRx_CustSrvc_Member?tenant=Environment"));
			executor.shutdown();
			bufferedReader = new BufferedReader(new InputStreamReader(responseFuture1.get()));
			String inputLine;
			
			while ((inputLine = bufferedReader.readLine()) != null) {
				response1.append(inputLine);
			}
			bufferedReader.close();
			
			bufferedReader = new BufferedReader(new InputStreamReader(responseFuture2.get()));
			
			while ((inputLine = bufferedReader.readLine()) != null) {
				response2.append(inputLine);
			}
			bufferedReader.close();
			JSONObject jobject1 = new JSONObject(response1.toString());
			JSONObject jobject2 = new JSONObject(response2.toString());
			int temp  = jobject1.getInt("pos")+jobject2.getInt("pos");
			output = (temp==2)?1:temp-2;
			finalResponse.put("chatsInQueue", output);
			finalResponse.put("longestWaitTime", jobject1.getString("ewt"));
			
			System.out.println("Final Resposne....."+finalResponse.toString());
		}catch(Exception e){
			
		}
		response.getWriter().append(finalResponse.toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
