package com.cognizant.agent;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

import com.genesyslab.platform.applicationblocks.com.ConfigException;
import com.genesyslab.platform.applicationblocks.com.IConfService;
import com.genesyslab.platform.applicationblocks.com.objects.CfgPerson;
import com.genesyslab.platform.applicationblocks.com.objects.CfgSkill;
import com.genesyslab.platform.applicationblocks.com.objects.CfgSkillLevel;
import com.genesyslab.platform.applicationblocks.com.queries.CfgPersonQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgSkillQuery;

public class ReadAgentDetails {
	public JSONArray readAndPrintAllCfgPersons(final IConfService service,String skillName,int sLevel)throws ConfigException, InterruptedException {
      JSONArray personArray = new JSONArray();
      JSONObject personObject = null;
      try{
    	  CfgPersonQuery cfgPersonQuery = new CfgPersonQuery();
          CfgSkill cfgSkill = service.retrieveObject(CfgSkill.class, new CfgSkillQuery(skillName));
          if(null!=cfgSkill){
          int skillId = cfgSkill.getDBID();
	          cfgPersonQuery.setSkillDbid(skillId);
	          Collection<CfgPerson> persons = service.retrieveMultipleObjects(CfgPerson.class,cfgPersonQuery);
	          for (CfgPerson person : persons) {
	        	  Collection<CfgSkillLevel> skillLevel = person.getAgentInfo().getSkillLevels();
	        	  for(CfgSkillLevel level:skillLevel) {
	        		  if(level.getSkill().getName().equalsIgnoreCase(skillName) && level.getLevel()>=sLevel){
	        			 personObject = new JSONObject();
	        			 personObject.put("DBID", person.getDBID());
	        			 personObject.put("FirstName", person.getFirstName());
	        			 personObject.put("LastName", person.getLastName());
	        			 personObject.put("UserName", person.getUserName());
	        			 personObject.put("IsAgent", person.getIsAgent());
	        			 personObject.put("State", person.getState());
	        			 personArray.put(personObject);
	        		  }
	        	  }
	             
	          }
	        }
      }catch(Exception e){
    	  e.printStackTrace();
      }finally{
    	  IConfServiceManager.uninitializeConfigService(service);
      }
      return personArray;
    }
	
	public JSONObject getPersonByPersonId(String agentId,IConfService service){
		try{
			CfgPersonQuery cfgPersonQuery  = new CfgPersonQuery();
			cfgPersonQuery.setUserName(agentId);
			CfgPerson person = service.retrieveObject(CfgPerson.class,cfgPersonQuery);
			if(null!=person){
				 JSONObject personObject = new JSONObject();
				 personObject = new JSONObject();
				 personObject.put("DBID", person.getDBID());
				 personObject.put("FirstName", person.getFirstName());
				 personObject.put("LastName", person.getLastName());
				 personObject.put("UserName", person.getUserName());
				 personObject.put("IsAgent", person.getIsAgent());
				 personObject.put("State", person.getState());
				 return personObject;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			IConfServiceManager.uninitializeConfigService(service);
		}
		return null;
	}
	
	/*public JSONObject getPersonInfo(int DBID,IConfService service){
		JSONObject agentInfoObj = new JSONObject();
		try{
			CfgPerson cfgPerson = service.retrieveObject(CfgPerson.class, new CfgPersonQuery(DBID));
			CfgAgentInfo agentInfo = cfgPerson.getAgentInfo();
			CfgAgentGroupQuery aq = new CfgAgentGroupQuery();
			aq.setPersonDbid(cfgPerson.getDBID());
			CfgAgentGroup cfgAgentGroup  = service.retrieveObject(CfgAgentGroup.class,aq);
			agentInfoObj.put("CapacityRule",agentInfo.getCapacityRule().getName());
			agentInfoObj.put("Place",agentInfo.getPlace().getName() );
			agentInfoObj.put("Path",agentInfo.getSite().getObjectPath() );
			agentInfoObj.put("Tenant",cfgPerson.getTenant().getName() );
			agentInfoObj.put("",cfgAgentGroup.getGroupInfo().get );
			agentInfoObj.put("", );
			agentInfoObj.put("", );
			agentInfoObj.put("", );
			agentInfoObj.put("", );
			agentInfoObj.put("", );
			agentInfoObj.put("", );
			agentInfoObj.put("", );
			agentInfoObj.put("", );
			agentInfoObj.put("", );
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			IConfServiceManager.uninitializeConfigService(service);
		}
		return agentInfoObj;
	}*/

	public IConfService getIConfService(String environment){
		Properties properties = getConfigProperties();
		String configServerHost=null;
		int configServerPort=0;
		String configServerUser=null;
		String configServerPass=null;
		if(properties==null) {
			System.out.println("Can't get \'application.properties\'. Check if file available in resources.");
			return null;
		}
		String tempAppName   = "AppName4Test"; // Unique name for temp app to be created,
		String someAppName = properties.getProperty("ConfServerClientName");
		
		
		if (null == someAppName || someAppName.equals("")) {
			someAppName = "default";
		}
		if(null!=environment){
			if(environment.equalsIgnoreCase("dev")){
				configServerHost = properties.getProperty("DevConfServerHost");
				configServerPort = Integer.parseInt(properties.getProperty("DevConfServerPort"));
				configServerUser = properties.getProperty("DevConfServerUser");
				configServerPass = properties.getProperty("DevConfServerPassword");
			}else if(environment.equalsIgnoreCase("stage")){
				configServerHost = properties.getProperty("StgConfServerHost");
				configServerPort = Integer.parseInt(properties.getProperty("StgConfServerPort"));
				configServerUser = properties.getProperty("StgConfServerUser");
				configServerPass = properties.getProperty("StgConfServerPassword");
			}else if(environment.equalsIgnoreCase("prod")){
				configServerHost = properties.getProperty("ProdConfServerHost");
				configServerPort = Integer.parseInt(properties.getProperty("ProdConfServerPort"));
				configServerUser = properties.getProperty("ProdConfServerUser");
				configServerPass = properties.getProperty("ProdConfServerPassword");
			}
		}
		 

		return IConfServiceManager.initializeConfigService(tempAppName, configServerHost, configServerPort, configServerUser, configServerPass);

	}
	
	private Properties getConfigProperties() {		
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
	
}
