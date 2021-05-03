/**
 * @author svanthen
 * Organization : CodeDepot, Optum
 * Created : Aug 25, 2020
 * 
 */
package codedepot.sms.utils;

import java.util.Map;
import codedepot.sms.entity.Chat2SMSfromAgentEntity;
import codedepot.sms.entity.InboundMsgFromSmsMgrEntity;
import codedepot.sms.entity.OutboundMsgFromWWEEntity;
import codedepot.sms.entity.PostMsgChatSmsEntity;
import codedepot.sms.entity.PostMsgMarkdownSmsEntity;
import codedepot.sms.entity.PostMsgUpdateSmsContactEntity;

public class TransactionData {

	String smsSubject = "";
	String SMSACK = "false";
	String interactionId = "";
	String programId = "";
	String phoneId = "";
	String smsMessage = "";
	String ani = "";
	String messageId = "";
	String messageBody = "";
	String messageTo = "";
	String messageFrom = "";
	String messageType = "";
	String RequestFrom = "";
	String ENT_Unit = "";
	String ENT_Segment = "";
	String ENT_Function = "";
	String firstName = "";
	String lastName = "";
	String fullName = "";
	String nickname = "";
	boolean updateContactName = false;
	String customerId = "";
	String contactId = "";
	String timezone = "";
	String countryCode = "US";
	int serviceCount = 0;
	String secureKey = "";
	String smsStatus = "";
	String chatId = "";
	String alias = "";
	String userId = "";
	String sessionId = "";
	String assignedAgentId = "";
	String serviceId = "";
	int subscriptionId = 0;
	String subscriptionStatus = "";
	boolean chatEnded = false;
	// appConfig data
	Map<String, String> appConfigMap;
	String gms_server_url = "";
	String omni_sms_service_url = "";
	String sms_request_timeout_secs = "";
	String gms_request_timeout_secs = "";
	String c360_request_timeout_secs = "";
	String sms_mgr_url = "";
	String sms_oauth2_url = "";
	String CM_ServiceURL = "";
	String CM_RequestURL = "";
	// programId json
	Map<String, String> programIdMap;
	String auto_ack_message_text = "";
	String _auto_ack_message_sendflag = "";
	String _tokenClientID = "";
	String _tokenClientSecret = "";
	String _tokenGrantType = "";
	String CM_ContactType = "";
	String CM_ProfileExtensions = "";
	String CM_ServiceExtensions = "";
	String CM_Services = "";
	String CM_ServiceStatus = "";
	String send_immediately = "";
	String UseExternalAssignedAgentWebService = "";
	String UseExternalAssignedAgentWebServiceURL = "";

	String gmsChatServiceURL = "";
	String chat_to_sms = "Async_SMS_Reply";
	String sms_to_chat = "Async_SMS_Inbound";
	String ENT_TargetOverrideFlag = "";
	String ENT_HOOPOverrideFlag = "";

	public TransactionData() {

	}
	
	public void setAppConfigMap(Map<String, String> appConfigMap) {
		this.appConfigMap = appConfigMap;
		this.gms_server_url = appConfigMap.get("gms_server_url");
		this.omni_sms_service_url = appConfigMap.get("omni_sms_service_url");
		this.sms_request_timeout_secs = appConfigMap.get("sms_request_timeout_secs");
		this.gms_request_timeout_secs = appConfigMap.get("gms_request_timeout_secs");
		this.c360_request_timeout_secs = appConfigMap.get("c360_request_timeout_secs");
		this.sms_mgr_url = appConfigMap.get("sms_mgr_url");
		this.sms_oauth2_url = appConfigMap.get("sms_oauth2_url");
		this.CM_ServiceURL = appConfigMap.get("CM_ServiceURL");
		this.CM_RequestURL = appConfigMap.get("CM_RequestURL");
	}

	public void setProgramIdMap(Map<String, String> programIdMap) {
		this.programIdMap = programIdMap;
		this.auto_ack_message_text = programIdMap.get("_auto_ack_message_text");
		this._auto_ack_message_sendflag = programIdMap.get("_auto_ack_message_sendflag");
		this._tokenClientID = programIdMap.get("_tokenClientID");
		this._tokenClientSecret = programIdMap.get("_tokenClientSecret");
		this._tokenGrantType = programIdMap.get("_tokenGrantType");
		this.CM_ContactType = programIdMap.get("CM_ContactType");
		this.CM_ProfileExtensions = programIdMap.get("CM_ProfileExtensions");
		this.CM_ServiceExtensions = programIdMap.get("CM_ServiceExtensions");
		this.CM_Services = programIdMap.get("CM_Services");
		this.CM_ServiceStatus = programIdMap.get("CM_ServiceStatus");
		this.ENT_Function = programIdMap.get("ENT_Function");
		this.ENT_Segment = programIdMap.get("ENT_Segment");
		this.ENT_Unit = programIdMap.get("ENT_Unit");
		this.send_immediately = programIdMap.get("send-immediately");
		this.UseExternalAssignedAgentWebService = programIdMap.get("UseExternalAssignedAgentWebService");
		this.UseExternalAssignedAgentWebServiceURL = programIdMap.get("UseExternalAssignedAgentWebServiceURL");
	}

	public void setSmsOutboundParams(OutboundMsgFromWWEEntity outboundSmsReq) {

		this.assignedAgentId = outboundSmsReq.get_outbound_agent_id();
		this.contactId = outboundSmsReq.get_outbound_contact_id();
		this.firstName = outboundSmsReq.get_outbound_first_name();
		this.lastName = outboundSmsReq.get_outbound_last_name();
		this.messageFrom = outboundSmsReq.get_outbound_message_from();
		this.programId = outboundSmsReq.get_outbound_message_program_id();
		// derived params
		this.nickname = this.firstName + this.lastName;
		this.fullName = this.firstName + " " + this.lastName;
		this.ani = this.messageFrom;
		// this.customerId= this.contactId;
	}

	public void setchatSMSParams(PostMsgChatSmsEntity chtSmsReq) {
		this.programId = chtSmsReq.get_message_program_id();
		this.smsMessage = chtSmsReq.get_message_body();
		this.phoneId = chtSmsReq.get_message_phone_id();
		this.interactionId = chtSmsReq.getInteractionId();
	}

//setupdateSMSContactParams

	public void setupdateSMSContactParams(PostMsgUpdateSmsContactEntity updateSmsContactReq) {
		this.programId = updateSmsContactReq.get_sms_program_id();
		this.firstName = updateSmsContactReq.get_ENT_ContactFirstNm();
		this.lastName = updateSmsContactReq.get_ENT_ContactLastNm();
		this.serviceId = updateSmsContactReq.get_sms_service_id();
		this.phoneId = updateSmsContactReq.get_sms_phone_id();

	}

	public void setSmsInboundParams(InboundMsgFromSmsMgrEntity inboundSmsReq) {
		this.messageId = inboundSmsReq.get_inbound_message_id();
		this.messageBody = inboundSmsReq.get_inbound_message_body();
		this.messageFrom = inboundSmsReq.get_inbound_message_from();
		this.phoneId = inboundSmsReq.get_inbound_message_phone_id();
		this.programId = inboundSmsReq.get_inbound_message_program_id();
		this.messageTo = inboundSmsReq.get_inbound_message_to();
		this.ENT_Unit = inboundSmsReq.getENT_Unit();
		this.ENT_Segment = inboundSmsReq.getENT_Segment();
		this.ENT_Function = inboundSmsReq.getENT_Function();
		this.ENT_HOOPOverrideFlag = inboundSmsReq.getENT_HOOPOverrideFlag();
		this.ENT_TargetOverrideFlag = inboundSmsReq.getENT_TargetOverrideFlag();

		String tmp_ani = inboundSmsReq.get_inbound_message_from();
		try {
			if (tmp_ani.startsWith("+1")) {
				this.ani = tmp_ani.replace("+1", "");
			} else if (tmp_ani.startsWith("+")) {
				this.ani = tmp_ani.replace("+", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.fullName = inboundSmsReq.get_inbound_message_full_name();
		this.firstName = "unknown";
		this.lastName = "unknown";
		try {
			if (fullName != null && !fullName.isEmpty() && !fullName.equalsIgnoreCase("unkown")) {
				if (fullName.split(" ").length > 1) {
					this.firstName = fullName.split(" ")[0];
					this.lastName = fullName.split(" ")[1];
				} else {
					this.firstName = fullName;
				}
			} else {
				this.setUpdateContactName(true);
			}
		} catch (Exception e) {
			this.firstName = fullName;
		}
		this.nickname = this.firstName + this.lastName;
	}

	public void setChat2SmsParams(Chat2SMSfromAgentEntity chat2smsReq) {
		this.messageBody = chat2smsReq.get_outbound_message_body();
		this.phoneId = chat2smsReq.get_outbound_message_phone_id();
		this.programId = chat2smsReq.get_outbound_message_program_id();

	}

	// getters and setters
	public String getSmsSubject() {
		return smsSubject;
	}

	public void setSmsSubject(String smsSubject) {
		this.smsSubject = smsSubject;
	}

	public String getSMSACK() {
		return SMSACK;
	}

	public void setSMSACK(String sMSACK) {
		SMSACK = sMSACK;
	}

	public String getInteractionId() {
		return interactionId;
	}

	public void setInteractionId(String interactionId) {
		this.interactionId = interactionId;
	}

	public String getRequestFrom() {
		return RequestFrom;
	}

	public void setRequestFrom(String requestFrom) {
		RequestFrom = requestFrom;
	}

	public boolean getChatEnded() {
		return chatEnded;
	}

	public void setChatEnded(boolean chatEnded) {
		this.chatEnded = chatEnded;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public boolean isUpdateContactName() {
		return updateContactName;
	}

	public void setUpdateContactName(boolean updateContactName) {
		this.updateContactName = updateContactName;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;

		if (fullName.split(" ").length > 1) {
			this.firstName = fullName.split(" ")[0];
			this.lastName = fullName.split(" ")[1];
		} else {
			this.firstName = fullName;
		}

		this.nickname = this.firstName + this.lastName;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public int getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(int subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public String getSubscriptionStatus() {
		return subscriptionStatus;
	}

	public void setSubscriptionStatus(String subscriptionStatus) {
		this.subscriptionStatus = subscriptionStatus;
	}

	public String getENT_TargetOverrideFlag() {
		return ENT_TargetOverrideFlag;
	}

	public void setENT_TargetOverrideFlag(String eNT_TargetOverrideFlag) {
		ENT_TargetOverrideFlag = eNT_TargetOverrideFlag;
	}

	public String getENT_HOOPOverrideFlag() {
		return ENT_HOOPOverrideFlag;
	}

	public void setENT_HOOPOverrideFlag(String eNT_HOOPOverrideFlag) {
		ENT_HOOPOverrideFlag = eNT_HOOPOverrideFlag;
	}

	public String getChat_to_sms() {
		return chat_to_sms;
	}

	public String getSms_to_chat() {
		return sms_to_chat;
	}

	public String getNickname() {
		if (nickname == null && nickname.isEmpty()) {
			return firstName + lastName;
		} else {
			return nickname;
		}
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getFullName() {
		return fullName;
	}

	public String getGmsChatServiceURL() {
		return gms_server_url + "/genesys/2/chat/" + ENT_Unit + "_" + ENT_Segment + "_" + ENT_Function + "_SMS";
	}

	public void setGmsChatServiceURL(String gmsChatServiceURL) {
		this.gmsChatServiceURL = gmsChatServiceURL;
	}

	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}

	public String getPhoneId() {
		return phoneId;
	}

	public void setPhoneId(String phoneId) {
		this.phoneId = phoneId;
	}

	public String getSmsMessage() {
		return smsMessage;
	}

	public void setSmsMessage(String smsMessage) {
		this.smsMessage = smsMessage;
	}

	public String getAni() {
		return ani;
	}

	public void setAni(String ani) {
		this.ani = ani;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getMessageBody() {
		return messageBody;
	}

	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}

	public String getMessageTo() {
		return messageTo;
	}

	public void setMessageTo(String messageTo) {
		this.messageTo = messageTo;
	}

	public String getMessageFrom() {
		return messageFrom;
	}

	public void setMessageFrom(String messageFrom) {
		this.messageFrom = messageFrom;
	}

	public String getENT_Unit() {
		return ENT_Unit;
	}

	public void setENT_Unit(String eNT_Unit) {
		ENT_Unit = eNT_Unit;
	}

	public String getENT_Segment() {
		return ENT_Segment;
	}

	public void setENT_Segment(String eNT_Segment) {
		ENT_Segment = eNT_Segment;
	}

	public String getENT_Function() {
		return ENT_Function;
	}

	public void setENT_Function(String eNT_Function) {
		ENT_Function = eNT_Function;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public int getServiceCount() {
		return serviceCount;
	}

	public void setServiceCount(int serviceCount) {
		this.serviceCount = serviceCount;
	}

	public String getSecureKey() {
		return secureKey;
	}

	public void setSecureKey(String secureKey) {
		this.secureKey = secureKey;
	}

	public String getSmsStatus() {
		return smsStatus;
	}

	public void setSmsStatus(String smsStatus) {
		this.smsStatus = smsStatus;
	}

	public String getChatId() {
		return chatId;
	}

	public void setChatId(String chatId) {
		this.chatId = chatId;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getAssignedAgentId() {
		return assignedAgentId;
	}

	public void setAssignedAgentId(String assignedAgentId) {
		this.assignedAgentId = assignedAgentId;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public Map<String, String> getAppConfigMap() {
		return appConfigMap;
	}

	public String getGms_server_url() {
		return gms_server_url;
	}

	public void setGms_server_url(String gms_server_url) {
		this.gms_server_url = gms_server_url;
	}

	public String getOmni_sms_service_url() {
		return omni_sms_service_url;
	}

	public void setOmni_sms_service_url(String omni_sms_service_url) {
		this.omni_sms_service_url = omni_sms_service_url;
	}

	public String getSms_request_timeout_secs() {
		return sms_request_timeout_secs;
	}

	public void setSms_request_timeout_secs(String sms_request_timeout_secs) {
		this.sms_request_timeout_secs = sms_request_timeout_secs;
	}

	public String getGms_request_timeout_secs() {
		return gms_request_timeout_secs;
	}

	public void setGms_request_timeout_secs(String gms_request_timeout_secs) {
		this.gms_request_timeout_secs = gms_request_timeout_secs;
	}

	public String getC360_request_timeout_secs() {
		return c360_request_timeout_secs;
	}

	public void setC360_request_timeout_secs(String c360_request_timeout_secs) {
		this.c360_request_timeout_secs = c360_request_timeout_secs;
	}

	public String getSms_mgr_url() {
		return sms_mgr_url;
	}

	public void setSms_mgr_url(String sms_mgr_url) {
		this.sms_mgr_url = sms_mgr_url;
	}

	public String getSms_oauth2_url() {
		return sms_oauth2_url;
	}

	public void setSms_oauth2_url(String sms_oauth2_url) {
		this.sms_oauth2_url = sms_oauth2_url;
	}

	public String getCM_ServiceURL() {
		return CM_ServiceURL;
	}

	public void setCM_ServiceURL(String cM_ServiceURL) {
		CM_ServiceURL = cM_ServiceURL;
	}

	public String getCM_RequestURL() {
		return CM_RequestURL;
	}

	public void setCM_RequestURL(String cM_RequestURL) {
		CM_RequestURL = cM_RequestURL;
	}

	public Map<String, String> getProgramIdMap() {
		return programIdMap;
	}

	public String getAuto_ack_message_text() {
		return auto_ack_message_text;
	}

	public void setAuto_ack_message_text(String auto_ack_message_text) {
		this.auto_ack_message_text = auto_ack_message_text;
	}

	public String get_auto_ack_message_sendflag() {
		return _auto_ack_message_sendflag;
	}

	public void set_auto_ack_message_sendflag(String _auto_ack_message_sendflag) {
		this._auto_ack_message_sendflag = _auto_ack_message_sendflag;
	}

	public String get_tokenClientID() {
		return _tokenClientID;
	}

	public void set_tokenClientID(String _tokenClientID) {
		this._tokenClientID = _tokenClientID;
	}

	public String get_tokenClientSecret() {
		return _tokenClientSecret;
	}

	public void set_tokenClientSecret(String _tokenClientSecret) {
		this._tokenClientSecret = _tokenClientSecret;
	}

	public String get_tokenGrantType() {
		return _tokenGrantType;
	}

	public void set_tokenGrantType(String _tokenGrantType) {
		this._tokenGrantType = _tokenGrantType;
	}

	public String getCM_ContactType() {
		return CM_ContactType;
	}

	public void setCM_ContactType(String cM_ContactType) {
		CM_ContactType = cM_ContactType;
	}

	public String getCM_ProfileExtensions() {
		return CM_ProfileExtensions;
	}

	public void setCM_ProfileExtensions(String cM_ProfileExtensions) {
		CM_ProfileExtensions = cM_ProfileExtensions;
	}

	public String getCM_ServiceExtensions() {
		return CM_ServiceExtensions;
	}

	public void setCM_ServiceExtensions(String cM_ServiceExtensions) {
		CM_ServiceExtensions = cM_ServiceExtensions;
	}

	public String getCM_Services() {
		return CM_Services;
	}

	public void setCM_Services(String cM_Services) {
		CM_Services = cM_Services;
	}

	public String getCM_ServiceStatus() {
		return CM_ServiceStatus;
	}

	public void setCM_ServiceStatus(String cM_ServiceStatus) {
		CM_ServiceStatus = cM_ServiceStatus;
	}

	public String getSend_immediately() {
		return send_immediately;
	}

	public void setSend_immediately(String send_immediately) {
		this.send_immediately = send_immediately;
	}

	public String getUseExternalAssignedAgentWebService() {
		return UseExternalAssignedAgentWebService;
	}

	public void setUseExternalAssignedAgentWebService(String useExternalAssignedAgentWebService) {
		UseExternalAssignedAgentWebService = useExternalAssignedAgentWebService;
	}

	public String getUseExternalAssignedAgentWebServiceURL() {
		return UseExternalAssignedAgentWebServiceURL;
	}

	public void setUseExternalAssignedAgentWebServiceURL(String useExternalAssignedAgentWebServiceURL) {
		UseExternalAssignedAgentWebServiceURL = useExternalAssignedAgentWebServiceURL;
	}

	public void setMarkdownSMSParams(PostMsgMarkdownSmsEntity markdownSmsReq) {
		this.serviceId = markdownSmsReq.get_sms_service_id();
	}
}
