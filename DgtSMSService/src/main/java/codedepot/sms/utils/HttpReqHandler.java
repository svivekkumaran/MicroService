/**
 * @author svanthen
 * Organization : CodeDepot, Optum
 * Created : Aug 25, 2020
 * 
 */
package codedepot.sms.utils;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.hc.client5.http.async.methods.AbstractCharResponseConsumer;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequests;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.http.nio.support.AsyncRequestBuilder;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import codedepot.sms.controller.DgtSMSServiceController;

@Component
public class HttpReqHandler {

	private static final Logger logger = LoggerFactory.getLogger(HttpReqHandler.class);
	RequestConfig defaultRequestConfig;
	private static final ObjectMapper objectMapper = new ObjectMapper();

	public HttpReqHandler() {
	}

	public void getService_C360(TransactionData tData) {

		String url = tData.getCM_ServiceURL();
		String msgBody = "";
		if (tData.getFirstName().isEmpty() || tData.getFirstName().equalsIgnoreCase("unknown")
				|| tData.getFirstName().equalsIgnoreCase("anonymous")) {
			msgBody = "{\"_CM_ProfileExtensions\": \"ConstituentProfile\",\r\n" + "	\"_CM_Services\": \"SMS\",\r\n"
					+ "	\"_CM_ServiceExtensions\": \"SMS_Extension\",\r\n" + "	\"_CM_ServiceStatus\": \"Active\",\r\n"
					+ "	\"_CreateOnEmpty\": true,\r\n" + "	\"_CM_ContactType\": \"SMS\",\r\n" + "	\"_ANI\": \""
					+ tData.getAni() + "\",\r\n" + "	\"_Request\": \"" + tData.getCM_RequestURL() + "\",\r\n"
					+ "	\"_MethodName\": \"CMProfile,CMServices\",\r\n" + "	\"_CM_CustomerId\": \"\",\r\n"
					+ "	\"_CM_ServiceFilter\": \"{\\\"ServiceType\\\":\\\"SMS\\\",\\\"Criteria\\\":{\\\"SMS_Extension.SMS_PhoneID\\\":\\\""
					+ tData.getPhoneId() + "\\\",\\\"SMS_Extension.SMS_ProgramID\\\":\\\"" + tData.getProgramId()
					+ "\\\"}}\"\r\n" + "}";
		} else {
			msgBody = "{\"_CM_ProfileExtensions\": \"ConstituentProfile\",\r\n" + "	\"_CM_Services\": \"SMS\",\r\n"
					+ "	\"_CM_ServiceExtensions\": \"SMS_Extension\",\r\n" + "	\"_CM_ServiceStatus\": \"Active\",\r\n"
					+ "	\"_CreateOnEmpty\": true,\r\n" + "	\"_CM_ContactType\": \"SMS\",\r\n" + "	\"_ANI\": \""
					+ tData.getAni() + "\",\r\n" + "	\"_Request\": \"" + tData.getCM_RequestURL() + "\",\r\n"
					+ "	\"_MethodName\": \"CMProfile,CMServices\",\r\n" + "	\"_Fname\": \"" + tData.getFirstName()
					+ "\",\r\n" + "	\"_Lname\": \"" + tData.getLastName() + "\",\r\n"
					+ "	\"_CM_CustomerId\": \"\",\r\n"
					+ "	\"_CM_ServiceFilter\": \"{\\\"ServiceType\\\":\\\"SMS\\\",\\\"Criteria\\\":{\\\"SMS_Extension.SMS_PhoneID\\\":\\\""
					+ tData.getPhoneId() + "\\\",\\\"SMS_Extension.SMS_ProgramID\\\":\\\"" + tData.getProgramId()
					+ "\\\"}}\"\r\n" + "}";
		}
		String response = doHttpPost(url, msgBody, Integer.parseInt(tData.getC360_request_timeout_secs()),
				"jsonEncode");

		if (response.startsWith("200")) {
			try {
				JsonParser jsonParser = new JsonFactory().createParser(response.split("::", 2)[1]);
				while (jsonParser.nextToken() != null) {
					// get the current token
					String fieldname = jsonParser.getCurrentName();
					if ("customer_id".equals(fieldname)) {
						jsonParser.nextToken();
						tData.setCustomerId(jsonParser.getText());
					} else if ("ServiceCount".equals(fieldname)) {
						jsonParser.nextToken();
						tData.setServiceCount(jsonParser.getIntValue());
					} else if ("FirstName".equals(fieldname)) {
						jsonParser.nextToken();
						String tmp = jsonParser.getText();
						if (tmp != null && !tmp.isEmpty() && !tmp.equals("unknown")) {
							if (!tData.getFirstName().equalsIgnoreCase(tmp)) {
								tData.setUpdateContactName(true);
							}
							tData.setFirstName(tmp);
						}
					} else if ("LastName".equals(fieldname)) {
						jsonParser.nextToken();
						String tmp = jsonParser.getText();
						if (tmp != null && !tmp.isEmpty() && !tmp.equals("unknown")) {
							if (!tData.getLastName().equalsIgnoreCase(tmp)) {
								tData.setUpdateContactName(true);
							}
							tData.setLastName(tmp);
						}
					} else if ("SMS_SecureKey".equals(fieldname)) {
						jsonParser.nextToken();
						tData.setSecureKey(jsonParser.getText());
					} else if ("SMS_ChatId".equals(fieldname)) {
						jsonParser.nextToken();
						tData.setChatId(jsonParser.getText());
					} else if ("SMS_Alias".equals(fieldname)) {
						jsonParser.nextToken();
						tData.setAlias(jsonParser.getText());
					} else if ("SMS_UserId".equals(fieldname)) {
						jsonParser.nextToken();
						tData.setUserId(jsonParser.getText());
					} else if ("CHAT_SessionID".equals(fieldname)) {
						jsonParser.nextToken();
						tData.setSessionId(jsonParser.getText());
					} else if ("SMS_AssignedAgentID".equals(fieldname)) {
						jsonParser.nextToken();
						tData.setAssignedAgentId(jsonParser.getText());
					} else if ("service_id".equals(fieldname)) {
						jsonParser.nextToken();
						tData.setServiceId(jsonParser.getText());
					}
				}
				logger.info("Service count : " + tData.getServiceCount() + "|service_id : " + tData.getServiceId()
						+ "|session_id : " + tData.getSessionId() + "|Chat id : " + tData.getChatId()
						+ "|Customer_Id : " + tData.getCustomerId() + "|Phone ID : " + tData.getPhoneId());
			} catch (Exception e) {
				logger.info("#######Exception in getService_C360 : " + e.toString());
			}
		}

		if (tData.getServiceCount() > 0) {
			if (tData.getChatId() == null || tData.getChatId().isEmpty()) {
				tData.setSmsStatus("");
				logger.info("ServiceCount is more than one. Deleting old service in C360...Start");
				this.deleteService_C360(tData);
				logger.info("Deleting old service in C360...End");
			} else {
				tData.setSmsStatus("Existing");
				logger.info("smsStatus : Existing");
			}
		} else {
			tData.setSmsStatus("New");
			logger.info("smsStatus : New");
		}

	}

	public void startService_C360(TransactionData tData) {

		String response = "failure";
		String url = tData.getCM_ServiceURL();
		String msgBody = "{\r\n" + "    \"_CM_ProfileExtensions\": \"ConstituentProfile\",\r\n"
				+ "    \"_CM_Services\": \"SMS\",\r\n" + "    \"_CM_ServiceExtensions\": \"SMS_Extension\",\r\n"
				+ "    \"_CM_ServiceStatus\": \"Active\",\r\n" + "    \"_CreateOnEmpty\": true,\r\n"
				+ "    \"_CM_ContactType\": \"SMS\",\r\n" + "    \"_ANI\": \"" + tData.getAni() + "\",\r\n"
				+ "    \"_Request\": \"" + tData.getCM_RequestURL() + "\",\r\n"
				+ "    \"_MethodName\": \"ServiceStart\",\r\n" + "    \"_CM_CustomerId\": \"" + tData.getCustomerId()
				+ "\",\r\n" + "    \"_service_id\": \"\",\r\n"
				+ "    \"_CM_ServiceExtensionData\": \"{\\\"CHAT_SessionID\\\":\\\"" + tData.getSessionId()
				+ "\\\",\\\"SMS_PhoneID\\\":\\\"" + tData.getPhoneId() + "\\\",\\\"SMS_ProgramID\\\":\\\""
				+ tData.getProgramId() + "\\\",\\\"SMS_SecureKey\\\":\\\"" + tData.getSecureKey()
				+ "\\\",\\\"SMS_ChatId\\\":\\\"" + tData.getChatId() + "\\\",\\\"SMS_Alias\\\":\\\"" + tData.getAlias()
				+ "\\\",\\\"SMS_UserId\\\":\\\"" + tData.getUserId() + "\\\"}\"\r\n" + "}";

		response = doHttpPost(url, msgBody, Integer.parseInt(tData.getC360_request_timeout_secs()), "jsonEncode");

		try {
			JsonNode root = objectMapper.readTree(response.split("::", 2)[1]);
			JsonNode serviceNode = root.path("GateResult").path("ResponseObject").path("Services").path(0);
			JsonNode idNode = serviceNode.get("service_id");
			tData.setServiceId(idNode.asText());
			logger.info("Service Id : " + tData.getServiceId());
		} catch (Exception e) {
			logger.info("#######Exception in startService_C360 : " + e.toString());
		}

	}

	public String deleteService_C360(TransactionData tData) {

		String url = tData.getCM_ServiceURL();
		String msgBody = "{" + " \"_Request\": \"" + tData.CM_RequestURL + "\"," + "\"_MethodName\": \"ServiceClose\","
				+ "\"_service_id\": \"" + tData.getServiceId() + "\"" + "}";

		return doHttpPost(url, msgBody, Integer.parseInt(tData.getC360_request_timeout_secs()), "jsonEncode");
	}

	public String updateService_C360(TransactionData tData) {

		String url = tData.getCM_ServiceURL();
		String msgBody = "{\r\n" + " \"_Request\": \"" + tData.getCM_RequestURL() + "\","
				+ " \"_MethodName\": \"ServiceUpdate\",\r\n" + "    \"_CM_Services\": \"Async_Chat\",\r\n"
				+ "    \"_CM_CustomerId\": \"" + tData.getCustomerId() + "\"," + "    \"_service_id\": \""
				+ tData.getServiceId() + "\",\r\n" + "    \"_CM_ServiceExtensions\": \"AsyncChatExtension\",\r\n"
				+ "    \"_CM_ServiceExtensionData\": {" + "        \"CHAT_AsyncSessionID\": \"" + tData.getSessionId()
				+ "\",\r\n" + "        \"CHAT_AsyncSessionIDContext\": \"Async_Chat Service\",\r\n"
				+ "        \"ENT_AssignedAgentID\": \"" + tData.getAssignedAgentId() + "\"\r\n" + "    }\r\n" + "}";

		return doHttpPost(url, msgBody, Integer.parseInt(tData.getC360_request_timeout_secs()), "jsonEncode");
	}

	public void updateUserData_GMS(TransactionData tData) {

		String chat2SmsServiceUrl = tData.getOmni_sms_service_url() + "/chat2SMS";
		String gmsUDupdateURL = tData.getGmsChatServiceURL() + "/" + tData.getChatId() + "/updateData";
		String msgBody = "";
		if (tData.getMessageType().equalsIgnoreCase("outbound") && tData.getSmsStatus().equalsIgnoreCase("New")) {
			msgBody = "secureKey=" + tData.getSecureKey() + "&alias=" + tData.getAlias() + "&userId="
					+ tData.getUserId() + "&userData%5BSMS_Service_ID%5D=" + tData.getServiceId()
					+ "&userData%5BENT_AssignedAgentID%5D=" + tData.getAssignedAgentId()
					+ "&userData%5BMedia_Type%5D=SMS" + "&userData%5BGMS_Chat_to_SMS_URL%5D=" + chat2SmsServiceUrl
					+ "&userData%5BMessage_Type%5D=" + tData.getMessageType() + "&userData%5BSMS_SecureKeyL%5D="
					+ tData.getSecureKey() + "&userData%5BSMS_Alias%5D=" + tData.getAlias()
					+ "&userData%5BSMS_UserId%5D=" + tData.getUserId() + "&userData%5BSMS_ChatId%5D="
					+ tData.getChatId() + "&userData%5BENT_MediaClassNm%5D=sms"
					+ "&userData%5BDIG_ProviderNm%5D=OptumMessenger";
		} else {
			msgBody = "secureKey=" + tData.getSecureKey() + "&alias=" + tData.getAlias() + "&userId="
					+ tData.getUserId() + "&userData%5BSMS_Service_ID%5D=" + tData.getServiceId()
					// + "&userData%5BENT_AssignedAgentID%5D=" + tData.getAssignedAgentId()
					+ "&userData%5BMedia_Type%5D=SMS" + "&userData%5BGMS_Chat_to_SMS_URL%5D=" + chat2SmsServiceUrl
					+ "&userData%5BMessage_Type%5D=" + tData.getMessageType() + "&userData%5BSMS_SecureKeyL%5D="
					+ tData.getSecureKey() + "&userData%5BSMS_Alias%5D=" + tData.getAlias()
					+ "&userData%5BSMS_UserId%5D=" + tData.getUserId() + "&userData%5BSMS_ChatId%5D="
					+ tData.getChatId() + "&userData%5BENT_MediaClassNm%5D=sms"
					+ "&userData%5BDIG_ProviderNm%5D=OptumMessenger";
		}

		String response = doHttpPost(gmsUDupdateURL, msgBody, Integer.parseInt(tData.getGms_request_timeout_secs()),
				"urlEncode");

		try {
			if (response.contains("::")) {
				JsonNode root = objectMapper.readTree(response.split("::")[1]);
				JsonNode idNode = root.get("chatEnded");
				boolean flag = idNode.asBoolean();
				tData.setChatEnded(flag);
				logger.info("ChatEnded : " + tData.getChatEnded());
			} else {
				logger.info("#######Error getting reply for  in gmsUDupdateURL : " + gmsUDupdateURL);
			}
		} catch (Exception e) {
			logger.info("Response : " + response);
			logger.info("#######Exception in updateUserData_GMS : " + e.toString());
		}

	}

	public String sendMessage_GMS(TransactionData tData) {

		String adviceStr = "";
		String gmsChatSendMessageURL = tData.getGmsChatServiceURL() + "/" + tData.getChatId() + "/send";
		String msgBody = "secureKey=" + tData.getSecureKey() + "&alias=" + tData.getAlias() + "&userId="
				+ tData.getUserId() + "&message=" + tData.getMessageBody();
	//	+ "&userData%5BENT_TargetOverrideFlag%5D=" + tData.getENT_TargetOverrideFlag()
	//	+ "&userData%5BENT_HOOPOverrideFlag%5D=" + tData.getENT_HOOPOverrideFlag();

		// Failure :
		// {"messages":[],"chatEnded":true,"statusCode":2,"alias":"1323","chatId":"0008TaFN3SE300RT","errors":[{"code":249,"advice":"Media
		// server was not able to execute request. referenceId[10016629]"}]}
//Success : {"messages":[],"chatEnded":false,"statusCode":0,"alias":"1323","secureKey":"02f8e3936f763afffcce","userId":"052B5F58EBCF069A","chatId":"0008TaFN3SE300QU","nextPosition":3}
		String response = doHttpPost(gmsChatSendMessageURL, msgBody,
				Integer.parseInt(tData.getGms_request_timeout_secs()), "urlEncode");

		if (response.contains("errors")) {
			logger.info("Response : " + response);
			try {
				JsonNode root = objectMapper.readTree(response.split("::", 2)[1]);
				JsonNode idNode = root.get("errors").get(0).get("advice");
				adviceStr = idNode.asText();
			} catch (Exception e) {
				adviceStr = "Failure in sending SMS";
			}
		} else {
			adviceStr = "SMS Sent Successfully";
		}
		return adviceStr;
	}

	public String requestChat_GMS(TransactionData tData) {

		String response = "failure";
		String gmsChatServiceURL = tData.getGmsChatServiceURL();
		String msgBody = "";
		if (tData.getMessageType().equalsIgnoreCase("outbound")) {

			msgBody = "Subject=" + tData.getSmsSubject() + "&firstName=" + tData.getFirstName() + "&lastName="
					+ tData.getLastName() + "&nickname=" + tData.getNickname() + "&userData%5BENT_ContactPhoneNbr%5D="
					+ tData.getAni() + "&userData%5BSMS_ProgramID%5D=" + tData.getProgramId()
					+ "&userData%5BSMS_PhoneID%5D=" + tData.getPhoneId() + "&userData%5BGCTI_Chat_AsyncMode%5D=true"
					+ "&userData%5BMedia_Type%5D=SMS" + "&userData%5BOrigin%5D=Messaging" + "&userData%5BENT_Unit%5D="
					+ tData.getENT_Unit() + "&userData%5BENT_Segment%5D=" + tData.getENT_Segment()
					+ "&userData%5BENT_AssignedAgentID%5D=" + tData.getAssignedAgentId()
					+ "&userData%5BENT_Function%5D=" + tData.getENT_Function() + "&userData%5BCustomer_ID%5D="
					+ tData.getCustomerId() + "&userData%5BMessage_Type%5D=" + tData.getMessageType()
					+ "&userData%5BContactId%5D=" + tData.getCustomerId();

		} else {

			msgBody = "Subject=" + tData.getSmsSubject() + "&firstName=" + tData.getFirstName() + "&lastName="
					+ tData.getLastName() + "&nickname=" + tData.getNickname() + "&userData%5BENT_ContactPhoneNbr%5D="
					+ tData.getAni() + "&userData%5BSMS_ProgramID%5D=" + tData.getProgramId()
					+ "&userData%5BSMS_PhoneID%5D=" + tData.getPhoneId() + "&userData%5BGCTI_Chat_AsyncMode%5D=true"
					+ "&userData%5BMedia_Type%5D=SMS" + "&userData%5BOrigin%5D=Messaging" + "&userData%5BENT_Unit%5D="
					+ tData.getENT_Unit() + "&userData%5BENT_Segment%5D=" + tData.getENT_Segment()					
					+ "&userData%5BENT_Function%5D=" + tData.getENT_Function() + "&userData%5BCustomer_ID%5D="
					+ tData.getCustomerId() + "&userData%5BMessage_Type%5D=" + tData.getMessageType()
					+ "&userData%5BContactId%5D=" + tData.getCustomerId()
					+ "&userData%5BENT_TargetOverrideFlag%5D=" + tData.getENT_TargetOverrideFlag()
					+ "&userData%5BENT_HOOPOverrideFlag%5D=" + tData.getENT_HOOPOverrideFlag();
		}

		
		response = doHttpPost(gmsChatServiceURL, msgBody, Integer.parseInt(tData.getGms_request_timeout_secs()),
				"urlEncode");

		if (response.startsWith("200::")) {
			try {
				// logger.debug("json " + response.split("::", 2)[1]);
				JsonParser jsonParser = new JsonFactory().createParser(response.split("::", 2)[1]);
				while (jsonParser.nextToken() != null) {
					String fieldname = jsonParser.getCurrentName();
					if ("nickname".equals(fieldname)) {
						jsonParser.nextToken();
						tData.setNickname(jsonParser.getText());
					} else if ("alias".equals(fieldname)) {
						jsonParser.nextToken();
						tData.setAlias(jsonParser.getText());
					} else if ("secureKey".equals(fieldname)) {
						jsonParser.nextToken();
						tData.setSecureKey(jsonParser.getText());
					} else if ("userId".equals(fieldname)) {
						jsonParser.nextToken();
						tData.setUserId(jsonParser.getText());
					} else if ("chatId".equals(fieldname)) {
						jsonParser.nextToken();
						tData.setChatId(jsonParser.getText());
					}
				}
				logger.info("nickname : " + tData.getNickname() + "|chatId : " + tData.getChatId() + "|alias: "
						+ tData.getAlias() + "|secureKey : " + tData.getSecureKey() + "|userId : " + tData.getUserId());

			} catch (Exception e) {
				logger.info("#######Exception in requestChat_GMS : " + e.toString());
			}
			tData.setSessionId(tData.getChatId());
			response = "success";
		}
//		else {
//			return response;
//			//return new ResponseEntity<String>("Failed during initiate chat request", HttpStatus.OK);
//		}

		return response;

	}

//	public String requestOutboundChat_GMS(TransactionData tData) {
////		ObjJSON['_inbound_message_from']=_data._ANI;
////		ObjJSON['_inbound_message_body']='Starting a new outbound SMS...';
////		ObjJSON['_inbound_message_phone_id']=_data._SMS_PhoneID;
////		ObjJSON['_inbound_message_program_id']=_data._SMS_ProgramID;
////		ObjJSON['_ENT_AssignedAgentID']=_data._SMS_AssignedAgentID;
////		ObjJSON['_inbound_message_full_name']=_data._Fname + ' ' + _data._Lname;
////		ObjJSON['_message_type']='outbound';
//
//		String gmsChatServiceURL = tData.getGmsChatServiceURL();
//		String msgBody = "subject=initiateSMS" + "&fullName=" + tData.getFirstName() + " " + tData.getLastName()
//				+ "&userData%5BENT_ContactPhoneNbr%5D=" + tData.getAni() + "&userData%5BSMS_ProgramID%5D="
//				+ tData.getProgramId() + "&userData%5BSMS_PhoneID%5D=" + tData.getPhoneId()
//				+ "&userData%5BGCTI_Chat_AsyncMode%5D=true" + "&userData%5BMedia_Type%5D=SMS"
//				+ "&userData%5BOrigin%5D=Messaging" + "&userData%5BENT_Unit%5D=" + tData.getENT_Unit()
//				+ "&userData%5BENT_Segment%5D=" + tData.getENT_Segment() + "&userData%5BENT_Function%5D="
//				+ tData.getENT_Function() + "&userData%5BCustomer_ID%5D=" + tData.getCustomerId()
//				+ "&userData%5BContactId%5D=" + tData.getCustomerId();
//		// + "&userData%5BGCTI_Chat_PushSubscribe%5D=true"
//		// + "&userData%5BGCTI_GMS_NodeGroup%5D=gms-cluster-CTC"
//		// + "&userData%5BGCTI_GMS_PushDeviceId%5D=sudeerdevice1"
//		// + "&userData%5BGCTI_GMS_PushDeviceType%5D=customhttp"
//		// + "&userData%5BGCTI_GMS_NotifyRequestor%5D=true"
//		// + "&userData%5BGCTI_GMS_PushIncludePayload%5D=true"
//		// + "&userData%5BGCTI_GMS_PushProvider%5D=AsyncSMSChat";
//
//		return doHttpPost(gmsChatServiceURL, msgBody, Integer.parseInt(tData.getGms_request_timeout_secs()),
//				"urlEncode");
//	}

	public String doHttpPost(String serviceUrl, String msgBody, int timeout, String encoding) {
		int responseCode = 0;
		String responseJson = "";

		logger.debug("url : " + serviceUrl);
		logger.debug("urlParameters : " + msgBody);

		defaultRequestConfig = RequestConfig.custom().setConnectTimeout(Timeout.ofSeconds(timeout))
				.setConnectionRequestTimeout(Timeout.ofSeconds(timeout)).build();

		try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig)
				.build()) {
			HttpPost httpPost = new HttpPost(serviceUrl);
			httpPost.addHeader("Cache-Control", "no-cache");
			if (encoding.equalsIgnoreCase("urlEncode")) {
				httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
			} else {
				httpPost.addHeader("Content-Type", "application/json");
			}
			httpPost.addHeader("Accept", "application/json");

			StringEntity stringEntity = new StringEntity(msgBody);
			httpPost.setEntity(stringEntity);

			try (CloseableHttpResponse response1 = httpclient.execute(httpPost)) {
				logger.debug(response1.getCode() + " " + response1.getReasonPhrase());
				responseCode = response1.getCode();
				HttpEntity entity1 = response1.getEntity();
				responseJson = EntityUtils.toString(entity1);
				logger.debug("Response : " + responseJson);
				EntityUtils.consume(entity1);
			} catch (Exception e) {
				logger.info("#######Exception : " + e.toString());
			}
		} catch (Exception e) {
			logger.info(e.toString());
		}
		return responseCode + "::" + responseJson;
	}

	public String doHttpAsyncPost(String serviceUrl, String msgBody, int timeout, String encoding) {
		
		///////////////////////////////////
		// TO BE IMPLEMENTED
		////////////////////////////////////
		
		int responseCode = 0;
		String responseJson = "";

		logger.debug("url : " + serviceUrl);
		logger.debug("urlParameters : " + msgBody);

		defaultRequestConfig = RequestConfig.custom().setConnectTimeout(Timeout.ofSeconds(timeout))
				.setConnectionRequestTimeout(Timeout.ofSeconds(timeout)).build();

		try (final CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom()
				.setDefaultRequestConfig(defaultRequestConfig).build()) {
			// Start the client
			httpclient.start();

			// Execute request
			final SimpleHttpRequest request1 = SimpleHttpRequests.get("http://httpbin.org/get");

			final Future<SimpleHttpResponse> future = httpclient.execute(request1, null);
			// and wait until response is received
			final SimpleHttpResponse response1 = future.get();
			logger.debug(request1.getRequestUri() + "->" + response1.getCode());

			// One most likely would want to use a callback for operation result
			final CountDownLatch latch1 = new CountDownLatch(1);
			final SimpleHttpRequest request2 = SimpleHttpRequests.get("http://httpbin.org/get");
			httpclient.execute(request2, new FutureCallback<SimpleHttpResponse>() {

				@Override
				public void completed(final SimpleHttpResponse response2) {
					latch1.countDown();
					logger.debug(request2.getRequestUri() + "->" + response2.getCode());
				}

				@Override
				public void failed(final Exception ex) {
					latch1.countDown();
					logger.debug(request2.getRequestUri() + "->" + ex);
				}

				@Override
				public void cancelled() {
					latch1.countDown();
					logger.debug(request2.getRequestUri() + " cancelled");
				}

			});
			latch1.await();

			// In real world one most likely would want also want to stream
			// request and response body content
			final CountDownLatch latch2 = new CountDownLatch(1);
			final AsyncRequestProducer producer3 = AsyncRequestBuilder.get("http://httpbin.org/get").build();
			final AbstractCharResponseConsumer<HttpResponse> consumer3 = new AbstractCharResponseConsumer<HttpResponse>() {

				HttpResponse response;

				@Override
				protected void start(final HttpResponse response, final ContentType contentType)
						throws HttpException, IOException {
					this.response = response;
				}

				@Override
				protected int capacityIncrement() {
					return Integer.MAX_VALUE;
				}

				@Override
				protected void data(final CharBuffer data, final boolean endOfStream) throws IOException {
					// Do something useful
				}

				@Override
				protected HttpResponse buildResult() throws IOException {
					return response;
				}

				@Override
				public void releaseResources() {
				}

			};
			httpclient.execute(producer3, consumer3, new FutureCallback<HttpResponse>() {

				@Override
				public void completed(final HttpResponse response3) {
					latch2.countDown();
					logger.debug(request2.getRequestUri() + "->" + response3.getCode());
				}

				@Override
				public void failed(final Exception ex) {
					latch2.countDown();
					logger.debug(request2.getRequestUri() + "->" + ex);
				}

				@Override
				public void cancelled() {
					latch2.countDown();
					logger.debug(request2.getRequestUri() + " cancelled");
				}

			});
			latch2.await();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return responseCode + "::" + responseJson;
	}

}
