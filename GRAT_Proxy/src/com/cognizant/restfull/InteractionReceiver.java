package com.cognizant.restfull;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;

import com.cognizant.servelt.AgentInteraction;



@Path("/storageHandler")
public class InteractionReceiver {

	@POST
	@Path("/saveIxn")
	@Produces(MediaType.TEXT_XML)
	@Consumes(MediaType.TEXT_XML)
	public String saveIxn(String ixnObject){
		try{
			if(null!=ixnObject){
				JSONObject jobj = new JSONObject(ixnObject);
				JSONObject data = jobj.getJSONObject("data");
				System.out.println(data.getString("eventType"));
				JSONObject userData  = data.getJSONObject("interaction").getJSONObject("userData");
				AgentInteraction agentInteraction = new AgentInteraction();
				agentInteraction.setInteractionObject(ixnObject);
				agentInteraction.setInteractionUUID(data.getJSONObject("interaction").getString("interactionUUID"));
				agentInteraction.setMedia(data.getJSONObject("interaction").getString("media"));
				agentInteraction.setENT_ChatType(userData.getString("ENT_ChatType"));
				agentInteraction.setENT_Client(userData.getString("ENT_Client"));
				agentInteraction.setENT_ContactConstituentType(userData.getString("ENT_ContactConstituentType"));
				agentInteraction.setENT_ContactDOB(userData.getString("ENT_ContactDOB"));
				agentInteraction.setENT_ContactFirstNm(userData.getString("ENT_ContactFirstNm"));
				agentInteraction.setENT_ContactLastNm(userData.getString("ENT_ContactLastNm"));
				agentInteraction.setENT_Function(userData.getString("ENT_Function"));
				agentInteraction.setENT_Segment(userData.getString("ENT_Segment"));
				agentInteraction.setENT_SourceMemberID(userData.getString("ENT_SourceMemberID"));
				agentInteraction.setENT_SubFunction(userData.getString("ENT_SubFunction"));
				agentInteraction.setENT_SubjectRelationshipType(userData.getString("ENT_SubjectRelationshipType"));
				agentInteraction.setENT_Unit(userData.getString("ENT_Unit"));
				agentInteraction.setRoutingChatAttempt(String.valueOf(userData.getInt("RoutingChatAttempt")));
				agentInteraction.setRTargetAgentSelected(userData.getString("RTargetAgentSelected"));
				agentInteraction.setEventType(data.getString("eventType"));
				agentInteraction.setRoutingStartTime(userData.getString("RoutingStartTime"));
				agentInteraction.setSubject(userData.has("Subject")?userData.getString("Subject"):"");
				try( ByteArrayOutputStream baos = new ByteArrayOutputStream();
						 ObjectOutputStream oos = new ObjectOutputStream(baos);){
					 	oos.writeObject(agentInteraction);
					    oos.flush();
					    oos.close();
					    int dbid = insertInteraction(agentInteraction.getRTargetAgentSelected(), baos.toByteArray(),agentInteraction.getMedia(),agentInteraction.getRoutingStartTime());
					    JSONObject jsonObject = new JSONObject();
					    jsonObject.put("interactionUUID", agentInteraction.getInteractionUUID());
					    jsonObject.put("ixnId", dbid);
					    jsonObject.put("media", agentInteraction.getMedia());
					    jsonObject.put("ENT_ContactFirstNm", agentInteraction.getENT_ContactFirstNm());
					    jsonObject.put("ENT_ContactLastNm", agentInteraction.getENT_ContactLastNm());
					    jsonObject.put("ENT_SubFunction",agentInteraction.getENT_SubFunction());
					    jsonObject.put("Subject",agentInteraction.getSubject());
					    return jsonObject.toString();
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return "null";

	}
	
	private Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:C:/UHG/sqliteDB/tmdb.db";
        Connection conn = null;
        try {
        	Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(url);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
	
	private int insertInteraction(String agentId,byte[] blob,String media,String ixnTime){
        String sql = "insert into interaction(agentid,attachdata,mediaid,ixntime)values(?,?,?,?)";
        String sql1="select mediaid from media where medianame = ? COLLATE NOCASE";
        try {
        	 Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql1);
        	 pstmt.setString(1, media);
             ResultSet resultSet = pstmt.executeQuery();
             int mediaId = resultSet.next()?resultSet.getInt(1):0;
             resultSet.close();
             pstmt.close();
             pstmt = conn.prepareStatement(sql);
             pstmt.setString(1, agentId);
             pstmt.setBytes(2, blob);
             pstmt.setInt(3, mediaId);
             pstmt.setString(4, String.valueOf(new Timestamp(Long.parseLong(ixnTime))));
             int id = pstmt.executeUpdate();
             pstmt.close();
             conn.close();
             return id;
        } catch (SQLException e) {
           e.printStackTrace();
        }
        return 0;
    }
}
