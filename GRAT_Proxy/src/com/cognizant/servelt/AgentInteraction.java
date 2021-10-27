package com.cognizant.servelt;

import java.io.Serializable;

public class AgentInteraction implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String eventType;
	private String interactionUUID;
	private String RoutingChatAttempt;
	private String RTargetAgentSelected;
	private String ENT_Unit;
	private String ENT_ContactDOB;
	private String ENT_SubFunction;
	private String ENT_ContactConstituentType;
	private String ENT_SourceMemberID;
	private String ENT_Client;
	private String ENT_SubjectRelationshipType;
	private String ENT_ChatType;
	private String ENT_Function;
	private String ENT_ContactLastNm;
	private String ENT_Segment;
	private String ENT_ContactFirstNm;
	private String interactionObject;
	private String RoutingStartTime;
	private String ticketId;
	private String media;
	private String Subject;
	
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getInteractionUUID() {
		return interactionUUID;
	}
	public void setInteractionUUID(String interactionUUID) {
		this.interactionUUID = interactionUUID;
	}
	public String getRoutingChatAttempt() {
		return RoutingChatAttempt;
	}
	public void setRoutingChatAttempt(String routingChatAttempt) {
		RoutingChatAttempt = routingChatAttempt;
	}
	public String getRTargetAgentSelected() {
		return RTargetAgentSelected;
	}
	public void setRTargetAgentSelected(String rTargetAgentSelected) {
		RTargetAgentSelected = rTargetAgentSelected;
	}
	public String getENT_Unit() {
		return ENT_Unit;
	}
	public void setENT_Unit(String eNT_Unit) {
		ENT_Unit = eNT_Unit;
	}
	public String getENT_ContactDOB() {
		return ENT_ContactDOB;
	}
	public void setENT_ContactDOB(String eNT_ContactDOB) {
		ENT_ContactDOB = eNT_ContactDOB;
	}
	public String getENT_SubFunction() {
		return ENT_SubFunction;
	}
	public void setENT_SubFunction(String eNT_SubFunction) {
		ENT_SubFunction = eNT_SubFunction;
	}
	public String getENT_ContactConstituentType() {
		return ENT_ContactConstituentType;
	}
	public void setENT_ContactConstituentType(String eNT_ContactConstituentType) {
		ENT_ContactConstituentType = eNT_ContactConstituentType;
	}
	public String getENT_SourceMemberID() {
		return ENT_SourceMemberID;
	}
	public void setENT_SourceMemberID(String eNT_SourceMemberID) {
		ENT_SourceMemberID = eNT_SourceMemberID;
	}
	public String getENT_Client() {
		return ENT_Client;
	}
	public void setENT_Client(String eNT_Client) {
		ENT_Client = eNT_Client;
	}
	public String getENT_SubjectRelationshipType() {
		return ENT_SubjectRelationshipType;
	}
	public void setENT_SubjectRelationshipType(String eNT_SubjectRelationshipType) {
		ENT_SubjectRelationshipType = eNT_SubjectRelationshipType;
	}
	public String getENT_ChatType() {
		return ENT_ChatType;
	}
	public void setENT_ChatType(String eNT_ChatType) {
		ENT_ChatType = eNT_ChatType;
	}
	public String getENT_Function() {
		return ENT_Function;
	}
	public void setENT_Function(String eNT_Function) {
		ENT_Function = eNT_Function;
	}
	public String getENT_ContactLastNm() {
		return ENT_ContactLastNm;
	}
	public String getRoutingStartTime() {
		return RoutingStartTime;
	}
	public void setRoutingStartTime(String routingStartTime) {
		RoutingStartTime = routingStartTime;
	}
	public String getSubject() {
		return Subject;
	}
	public void setSubject(String subject) {
		Subject = subject;
	}
	public void setENT_ContactLastNm(String eNT_ContactLastNm) {
		ENT_ContactLastNm = eNT_ContactLastNm;
	}
	public String getENT_Segment() {
		return ENT_Segment;
	}
	public String getMedia() {
		return media;
	}
	public void setMedia(String media) {
		this.media = media;
	}
	public String getTicketId() {
		return ticketId;
	}
	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}
	public void setENT_Segment(String eNT_Segment) {
		ENT_Segment = eNT_Segment;
	}
	public String getENT_ContactFirstNm() {
		return ENT_ContactFirstNm;
	}
	public void setENT_ContactFirstNm(String eNT_ContactFirstNm) {
		ENT_ContactFirstNm = eNT_ContactFirstNm;
	}
	public String getInteractionObject() {
		return interactionObject;
	}
	public void setInteractionObject(String interactionObject) {
		this.interactionObject = interactionObject;
	}
}
