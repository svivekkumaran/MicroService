package com.cognizant.servelt;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;

import com.genesyslab.platform.applicationblocks.com.ConfServiceFactory;
import com.genesyslab.platform.applicationblocks.com.ConfigException;
import com.genesyslab.platform.applicationblocks.com.IConfService;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAccessGroup;
import com.genesyslab.platform.applicationblocks.com.objects.CfgID;
import com.genesyslab.platform.applicationblocks.com.objects.CfgPerson;
import com.genesyslab.platform.applicationblocks.com.queries.CfgAccessGroupQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgPersonQuery;
import com.genesyslab.platform.commons.protocol.ChannelState;
import com.genesyslab.platform.commons.protocol.Endpoint;
import com.genesyslab.platform.commons.protocol.ProtocolException;
import com.genesyslab.platform.configuration.protocol.ConfServerProtocol;
import com.genesyslab.platform.configuration.protocol.types.CfgAppType;

public class CFGAuthentication {
	   private String configServerHost = null;
	   private int configServerPort = 0;
	   private String CfgAppName = null;
	   private ConfServerProtocol confProtocol = null;
	   private String result = null;
	   private IConfService confService = null;
	   
	  private  Properties getProperties() {		
		InputStream is = null;
		Properties props = null;
		try {
			is = Thread.currentThread().getContextClassLoader().getResourceAsStream("cfgserver.properties");
			if(is != null) {				
				props = new Properties();
				props.load(is);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			props = null;
		}
		finally {
			if(is!=null) {
				try{
					is.close();
				}
				catch(Exception e) { }
			}
		}
		return props;
	}
	
	private  void readProperties() {	
		Properties properties = getProperties();
		if(properties==null) {
			System.out.println("Can't get \'cfgserver.properties\'. Check if file available in resources.");
			return ;
		}

        configServerHost = properties.getProperty("ConfServerHost");
        configServerPort = Integer.parseInt(properties.getProperty("ConfServerPort"));
        CfgAppName = properties.getProperty("ConfServerClientName");
	}
	
	
	public IConfService getConfService(String user, String pass){
        try {
        	  System.out.println("User validation started.");
        	  readProperties();
        	confProtocol = new ConfServerProtocol(new Endpoint(configServerHost,configServerPort));
        	confProtocol.setUserName(user);
        	confProtocol.setUserPassword(pass);
        	confProtocol.setClientName(CfgAppName);
        	confProtocol.setClientApplicationType(CfgAppType.CFGSCE.ordinal());
        	confService = ConfServiceFactory.createConfService(confProtocol);
        	confProtocol.open();
        	if(confProtocol.getState()==ChannelState.Opened) {
        	        //	System.out.println(confService+"The config protocol : "+confProtocol.getClientName());
        	        	result = "success";
        	        
        	        	
        	} else {
        		System.out.println("the conf protocl is not opened");
        		result = "failed";
        	}
        	
		} catch (ProtocolException | InterruptedException e) {
			String errormsg = e.getMessage();
			if(errormsg.contains("user name or password is incorrect")){
				result = "Username or Password is incorrect";
			} else {
				result = "Access Denied. Please contact your administrator.";
			}
			
		}
       
       
        return confService;
	}
	
	public String validate(String user, String pass){
        try {
        	  System.out.println("User validation started.");
        	  readProperties();
        	confProtocol = new ConfServerProtocol(new Endpoint(configServerHost,configServerPort));
        	confProtocol.setUserName(user);
        	confProtocol.setUserPassword(pass);
        	confProtocol.setClientName(CfgAppName);
        	confProtocol.setClientApplicationType(CfgAppType.CFGSCE.ordinal());
        	
        	confProtocol.open();
        	if(confProtocol.getState()==ChannelState.Opened) {
        	        //	System.out.println(confService+"The config protocol : "+confProtocol.getClientName());
        	        	result = "success";
        	       
        	        	
        	} else {
        		System.out.println("the conf protocl is not opened");
        		result = "failed";
        	}
        	 closeConnection(confProtocol);
		} catch (ProtocolException | InterruptedException e) {
			String errormsg = e.getMessage();
			if(errormsg.contains("user name or password is incorrect")){
				result = "Username or Password is incorrect";
			} else {
				result = "Access Denied. Please contact your administrator.";
			}
			 closeConnection(confProtocol);
		}
       
       
        return result;
	}
	
	private void closeConnection(ConfServerProtocol cfgProtocol) {
		try {
			if(null!=cfgProtocol) {
			cfgProtocol.close();
		//	System.out.println("Conf protocol connection closed.");
			}
			
			
		} catch (ProtocolException | IllegalStateException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	
	public Integer readPerson(IConfService service,String user) {
		 Integer personid = null;
		 CfgPersonQuery personQuery = new CfgPersonQuery(service);
		 personQuery.setEmployeeId(user);
		 try {
		CfgPerson persons = service.retrieveObject(personQuery);
		  personid = persons.getDBID();
			
		} catch (ConfigException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return personid;
	}
	
	public boolean checkAccessGroup(String user, String pass, String accessGroup) {
		boolean result = false;
		IConfService service = getConfService(user, pass);
		Integer personid = readPerson(service,user);
		CfgAccessGroupQuery agQuery = new CfgAccessGroupQuery(service);
		agQuery.setName(accessGroup);

		 try {
		
			 CfgAccessGroup acg = service.retrieveObject(agQuery);
			 if(acg !=null) {
			  Collection<CfgID> agentid =  acg.getMemberIDs();
		
			  for(CfgID test:agentid){
				  if(test.getDBID().equals(personid)){
					  result = true;
					  break;
				  }
			  }
			  
			 }
				System.out.println(result);
			
		} catch (ConfigException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 closeConnection(confProtocol);
		 return result;
	}


	 
	public static void main(String[] args) {
		CFGAuthentication obj = new CFGAuthentication();
		String val = obj.validate("ChatORx_TestAgent03","ChatORx_TestAgent03");
		 System.out.println(val);
		 //boolean res = obj.checkAccessGroup("ChatORx_TestAgent01","ChatORx_TestAgent01","Agent1");
		 //System.out.println(res);
}
}