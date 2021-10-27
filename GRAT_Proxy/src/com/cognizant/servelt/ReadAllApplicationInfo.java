package com.cognizant.servelt;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.genesyslab.platform.applicationblocks.com.AsyncRequestResult;
import com.genesyslab.platform.applicationblocks.com.ConfigException;
import com.genesyslab.platform.applicationblocks.com.IConfService;
import com.genesyslab.platform.applicationblocks.com.objects.CfgApplication;
import com.genesyslab.platform.applicationblocks.com.queries.CfgApplicationQuery;
import com.genesyslab.platform.commons.protocol.ProtocolException;
import com.genesyslab.platform.configuration.protocol.types.CfgFlag;

public class ReadAllApplicationInfo {
	
	private IConfService getConfigService() {
		IConfService service = null;
		Properties properties = getProperties();
		if(properties==null) {
			System.out.println("Can't get \'application.properties\'. Check if file available in resources.");
			return null;
		}
		String tempAppName   = "AppName4Test"; // Unique name for temp app to be created,
		String someAppName = properties.getProperty("ConfServerClientName");
		if (null == someAppName || someAppName.equals("")) {
			someAppName = "default";
		}
		String configServerHost = properties.getProperty("ConfServerHost");
		int configServerPort = Integer.parseInt(properties.getProperty("ConfServerPort"));
		String configServerUser = properties.getProperty("ConfServerUser");
		String configServerPass = properties.getProperty("ConfServerPassword");

		try {
			service = InitializationSamples.initializeConfigService(tempAppName, configServerHost, configServerPort, configServerUser, configServerPass);
		} catch (ProtocolException | ConfigException | InterruptedException e) {
			e.printStackTrace();
		}
		return service;
	}


	private Properties getProperties() {		
		Properties properties =  null;
		try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("configserver.properties")){
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
	
	public ArrayNode readAllCfgApplicationsAsync() {
		
		IConfService iconfService = getConfigService();
		if(null==iconfService){
			return null;
		}
		ArrayNode arrayNode = null;
		// Start application configuration objects reading:
		AsyncRequestResult<CfgApplication> asyncRequest = null;
		try {
			asyncRequest = iconfService.beginRetrieveMultipleObjects(CfgApplication.class, new CfgApplicationQuery(), null);
		} catch (ConfigException e) {
			e.printStackTrace();
		}
		int sleepDelta = 1;
		System.out.print("Applications reading: ");
		do {
			System.out.print(".");
			try {
				Thread.sleep(sleepDelta);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// We can also use following method instead of sleep():
			// asyncRequest.waitDone(sleepDelta, TimeUnit.MILLISECONDS);
			// This method will wait for read finish for not more than mentioned timeout.
			// So, by this way sleeping will be stopped immediately when data received.

			sleepDelta++;
		} while (!asyncRequest.isDone());

		try {
			
			ObjectMapper objectMapper = new ObjectMapper();
			ObjectNode objectNode = null;
			arrayNode = objectMapper.createArrayNode();
			Collection<CfgApplication> applications =null;
			try {
				applications = asyncRequest.get();
			} catch (InterruptedException | IllegalStateException e) {
				e.printStackTrace();
			}
			for (CfgApplication application : applications) {

				if(application.getIsServer()==CfgFlag.CFGTrue) {
					objectNode = objectMapper.createObjectNode();
					objectNode.put("AppName", application.getName());
					objectNode.put("AppType",  application.getType().name());

					if(application.getServerInfo()!=null) {
						if(application.getServerInfo().getHost()!=null) {
							objectNode.put("AppHostName", application.getServerInfo().getHost().getName());
							objectNode.put("AppIPAddress",  application.getServerInfo().getHost().getIPaddress());
						} else {
							objectNode.put("AppHostName", "null");
							objectNode.put("AppIPAddress",   "null");
						}

						if(application.getServerInfo().getPort()!=null) {	
							objectNode.put("AppPort",  application.getServerInfo().getPort());
						} else {
							objectNode.put("AppPort",   "null");
						}
					}
					if(application.getOptions()!=null)
						if(application.getOptions().getList("log")!=null) {
							objectNode.put("LogPath",  application.getOptions().getList("log").getAsString("all"));
						} else {
							objectNode.put("LogPath", "null");
						}
					arrayNode.add(objectNode);

				}

			}
			System.out.println("totally "+arrayNode.size()+" app servers found!");
		} catch (ExecutionException | IllegalStateException e) {
			System.out.println("Error reading applications: " + e);
			System.out.println("inner exception: " + e.getCause());
		}finally{
			try {
				if(null!=iconfService)
				InitializationSamples.uninitializeConfigService(iconfService);
			} catch (ProtocolException | IllegalStateException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(".................Applications reading done...................");
		return arrayNode;
	}
}
