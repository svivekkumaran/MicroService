package codedepot.sms.controller;

/**
 * 
 * <h1>OmniSmsServiceController<h1>
 * 
 * This is the main Rest Controller which handles requests from SMSManager, Agent WWE and IVR.
 * 
 * @author  Sudeer V Palli
 * Organization : CodeDepot, Optum
 * @version 1.0
 * @since   2020-08-11
 * 
 * 
 */

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import codedepot.sms.AppConfig;

import codedepot.sms.entity.Chat2SMSfromAgentEntity;
import codedepot.sms.entity.InboundMsgFromSmsMgrEntity;
import codedepot.sms.entity.OutboundMsgFromWWEEntity;
import codedepot.sms.entity.PostMsgChatSmsEntity;
import codedepot.sms.entity.PostMsgFromIvrEntity;
import codedepot.sms.entity.PostMsgMarkdownSmsEntity;
import codedepot.sms.utils.HttpReqHandler;
import codedepot.sms.utils.SmsMgrHandler;
import codedepot.sms.utils.TransactionData;
import codedepot.commons.JsonHelper;
import codedepot.commons.UrlHelper;

@RestController
@RequestMapping("${app.profile_env}/${app.version}")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DgtSMSServiceController {

	private static final Logger logger = LoggerFactory.getLogger(DgtSMSServiceController.class);
	private static final ObjectMapper objectMapper = new ObjectMapper();

//	private AppConfig appConfig;
	Map<String, String> appConfigMap;

	private SmsMgrHandler smsHandler;
	private HttpReqHandler httpReqHandler;
	
	private int smsHandler_timeout;
//	private CmC360Handler cmC360Handler;
	private int cmC360Handler_timeout;
//	private GmsHandler gmsHandler;
	private int gmsHandler_timeout;
	
	String json_params_url="";
	String profile_env="";
	
	private boolean isLoadTesting = false;

	@Autowired
	public DgtSMSServiceController(AppConfig configuration, SmsMgrHandler smsHandler,
			HttpReqHandler httpReqHandler) {
//		this.appConfig = configuration;
		appConfigMap = configuration.getApp();
		this.smsHandler = smsHandler;
		this.httpReqHandler = httpReqHandler;
		this.initConfig();

	}

	public void initConfig() {		
		logger.info("Initializing Config.");
		String jsonFile = "";
		json_params_url=appConfigMap.get("json_params_url");
		profile_env=appConfigMap.get("profile_env");
		
		try {
			jsonFile = json_params_url + profile_env + "-appConfig.json";			
			logger.info("json config File : "+jsonFile);
			if (UrlHelper.exists(jsonFile)) {
				appConfigMap.putAll(JsonHelper.getMapFromJsonFileUrl(jsonFile));
				logger.info("Overriding parameters from json config file.");
			}else {
				logger.info("No json appConfig file. Ignoring overriding parameters.");
			}			
		} catch (Exception e) {
			logger.info("Exception in reading json appConfig file : " + jsonFile + " - " + e.toString());
		}
		logger.info("application properties :");
		appConfigMap.forEach((key, value) -> logger.info(key + " | " + value));

		smsHandler.setRequestConfig(Integer.parseInt(appConfigMap.get("sms_request_timeout_secs")));
		smsHandler_timeout = Integer.parseInt(appConfigMap.get("sms_request_timeout_secs"));
		gmsHandler_timeout = Integer.parseInt(appConfigMap.get("gms_request_timeout_secs"));
		cmC360Handler_timeout = Integer.parseInt(appConfigMap.get("c360_request_timeout_secs"));

		
		// check whether Load Testing in Progress
		try {
			Path path = Paths.get("/apps/gcti/backup/LOAD_TESTING_IN_PROGRESS.file");
			if (Files.exists(path)) {
				isLoadTesting = true;
			} else {
				isLoadTesting = false;
			}
		} catch (Exception e) {
			//ignore.No Big Deal.
		}
	}

	@RequestMapping(value = "/ping", method = RequestMethod.GET)
	public String ping() {

		LocalDateTime dateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
		logger.info(
				"Responded to ping at " + dateTime.format(formatter)   + " with appConfig : " + appConfigMap);
		return "Responded to ping at " + dateTime.format(formatter) + " with appConfig : " + appConfigMap;
	}

	@RequestMapping(value = "/refresh", method = RequestMethod.GET)
	public String refresh() {

		this.initConfig();
		logger.info("Responded to refresh -  " + " with appConfig : " + appConfigMap);
		return "Responded to refresh -  " + " with appConfig : " + appConfigMap;
	}

	@RequestMapping(value = "/pushNoteFromGMS", method = RequestMethod.POST)
	public ResponseEntity<?> pushNoteFromGMS(@RequestBody String pushMsg) {
		logger.info("###############################################Received request - pushNoteFrom GMS : " + pushMsg);

		return new ResponseEntity<String>("success", HttpStatus.OK);
	}

	@RequestMapping(value = "/statusSms", method = RequestMethod.POST)
	public ResponseEntity<?> statusSms4WWE(@RequestBody String pushMsg) {
		logger.info("#######Received statusSms from WWE : " + pushMsg);

		return new ResponseEntity<String>("success", HttpStatus.OK);
	}

	/**
	 * <h1>/chat2SMS</h1>
	 * 
	 * From : WWE
	 * 
	 * @param _outbound_message_phone_id
	 * @param _outbound_message_program_id
	 * @param _outbound_message_body
	 * 
	 * @return String
	 * 
	 */
	@RequestMapping(value = "/chat2Sms", method = RequestMethod.POST)
	public ResponseEntity<?> chat2Sms(@RequestBody Chat2SMSfromAgentEntity chat2smsMsg) {
		logger.info("#######Received request - chat2SMS from Agent : " + chat2smsMsg);

		// Added to handle the load testing scenario, to bypass the SMS Manager during
		// Load Testing.
		if (isLoadTesting) {
			logger.info("#######Bypassing SMS mgr call ...LOAD TESTING is true");
		return new ResponseEntity<String>("success", HttpStatus.OK);
		}
		
		TransactionData tData = new TransactionData();
		tData.setRequestFrom("wwe");
		tData.setChat2SmsParams(chat2smsMsg);
		tData.setAppConfigMap(appConfigMap);
		tData.setProgramIdMap(getMapFromJson(
				json_params_url+appConfigMap.get("profile_env")+ "-" + tData.getProgramId() + ".json"));
			//	appProps.getJson_params_url() + appProps.getSms_env() + "-" + tData.getProgramId() + ".json"));

		String oAuth_url = tData.getSms_oauth2_url();
		String clientId = tData.get_tokenClientID();
		String clientSecret = tData.get_tokenClientSecret();
		String grantType = tData.get_tokenGrantType();
		String smsMgrUrl = tData.getSms_mgr_url();

		String token = "";

		tData.setSubscriptionId(0);
		tData.setSubscriptionStatus("");
		int outboundMsgId;

		try {
			token = smsHandler.getToken(oAuth_url, clientId, clientSecret, grantType);

			if (token != null && !token.isEmpty()) {

				dealWithSubscription(tData);

				if (tData.getSubscriptionId() == 0) {
					return new ResponseEntity<String>("Error in getting Subscription", HttpStatus.OK);
				} else {
					String resJson = "";
						resJson = smsHandler.sendSmsToSmsMgr(smsMgrUrl, tData.getProgramId(), tData.getPhoneId(),
								tData.getMessageBody());

					try {
						if (resJson != null && !resJson.isEmpty()) {

							if (resJson.equalsIgnoreCase("TIMEOUT")) {
								// response timeout

							} else if (resJson.equalsIgnoreCase("FAILURE")) {
								// internal error

							} else {
								JsonNode rNode = objectMapper.readTree(resJson);
								logger.info("OutboundMessage Status : " + rNode.path("status").asText());
								outboundMsgId = rNode.path("id").asInt();
								logger.info("OutboundMessage Id : " + outboundMsgId);
							}
						} else {
							logger.info("response from sendSmsToSmsMgr : " + resJson);
						}
					} catch (Exception e) {
						logger.info("Exception in sendSmsToSmsMgr : " + e.getMessage());
					}
				}

			} else {
				return new ResponseEntity<String>("Error in getting oAuth Token", HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.info("Exception in chat2Sms : " + e.getMessage());
		}

		return new ResponseEntity<String>("success", HttpStatus.OK);
	}

	private void dealWithSubscription(TransactionData tData) {
		String result;
		String smsMgrUrl = tData.getSms_mgr_url();
		result = smsHandler.getSubscription(smsMgrUrl, Integer.parseInt(tData.getPhoneId()), tData.getProgramId());
		if (result.contains("::")) {
			tData.setSubscriptionId(Integer.parseInt(result.split("::")[0]));
			tData.setSubscriptionStatus(result.split("::")[1]);
		}

		if (tData.getSubscriptionStatus().equalsIgnoreCase("subscribed")
				|| tData.getSubscriptionStatus().equalsIgnoreCase("confirmed")) {
			// do nothing
		} else {
			String json = smsHandler.subscriprPhoneId(smsMgrUrl, Integer.parseInt(tData.getPhoneId()),
					tData.getProgramId());
			try {
				JsonNode rNode = objectMapper.readTree(json);
				tData.setSubscriptionStatus(rNode.path("status").asText());
				logger.info("Subscription Status : " + tData.getSubscriptionStatus());
				tData.setSubscriptionId(rNode.path("id").asInt());
				logger.info("Subscription Id : " + tData.getSubscriptionId());
			} catch (Exception e) {
				logger.info("Exception in dealWithSubscription : " + e.getMessage());
			}
		}
	}

	/**
	 * From : WWE to : DgtSMSService
	 * 
	 * New Outbound SMS
	 */
	@RequestMapping(value = "/sendSms", method = RequestMethod.POST)
	public ResponseEntity<?> sendSmsFromWWE(@RequestBody OutboundMsgFromWWEEntity outboundSmsReq) {
		logger.info("#######Received request - sendOutboundSMS from WWE : " + outboundSmsReq);

		TransactionData tData = new TransactionData();
		tData.setRequestFrom("wwe");
		tData.setSmsOutboundParams(outboundSmsReq);
		tData.setAppConfigMap(appConfigMap);
		tData.setProgramIdMap(getMapFromJson(
				json_params_url + profile_env + "-" + tData.getProgramId() + ".json"));

		tData.setCustomerId(tData.getContactId());

		tData.setMessageType("outbound");
		tData.setSmsSubject(outboundSmsReq.get_outbound_message_from());
		tData.setMessageBody("New Outbound SMS");

		String token = "";
//		String url = "";
//		String urlParameters = "";
/// get Phone Id and populate tData with it.
		String smsMgrUrl = tData.getSms_mgr_url();
		try {
			logger.debug("Getting token...Start.");
			token = smsHandler.getToken(tData.getSms_oauth2_url(), tData.get_tokenClientID(),
					tData.get_tokenClientSecret(), tData.get_tokenGrantType());
			logger.info("Token : " + token);
			logger.debug("Getting token...End.");
		} catch (Exception e) {
			logger.info("Exception in Getting token : " + e.toString());
		}
		dealWithPhoneID(tData);

/////////////////////////////Get Stored SMS sesssion  ENT_action is 'get'
		String response = "";
		try {
			logger.info("Getting stored SMS session from C360...Start.");
			httpReqHandler.getService_C360(tData);
			logger.info("Getting stored SMS session from C360...End.");
		} catch (Exception e1) {
			logger.info("#######Exception in calling getService_C360 : " + e1.toString());
		}

		if (tData.getSmsStatus().equalsIgnoreCase("New")) {
			dealWithSubscription(tData);
		}
		response = this.doSms2Chat(tData);

		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

	private void dealWithPhoneID(TransactionData tData) {
		String response;
		String smsMgrUrl = tData.getSms_mgr_url();
		try {
			logger.debug("Getting phoneDetails...Start.");
			response = smsHandler.getPhoneDetails(smsMgrUrl, tData.getMessageFrom());
			logger.debug("Getting phoneDetails...End.");

			if (response.startsWith("200::")) {
				String responseJson = response.split("::")[1];

				JsonNode rootNode = objectMapper.readTree(responseJson);
				JsonNode tokenNode = rootNode.path("phone");
				JsonNode phNode = tokenNode.get(0);
				JsonNode idNode = phNode.path("id");
				tData.setPhoneId(idNode.asText());
				JsonNode ccNode = phNode.path("country-code");
				tData.setCountryCode(ccNode.asText());
				JsonNode tzNode = phNode.path("timezone");
				tData.setTimezone(tzNode.asText());
				JsonNode nameNode = phNode.path("name");
				String fullName = nameNode.asText();
				if (fullName != null && !fullName.isEmpty()) {
					tData.setFullName(fullName);
				}
			}

		} catch (Exception e) {
			logger.info("#######Exception in getPhoneDetails : " + e.toString());
		}
		logger.info("Phone ID : " + tData.getPhoneId());
		logger.info("country-code : " + tData.getCountryCode());
		logger.info("timeZone : " + tData.getTimezone());
		logger.info("fullname : " + tData.getFullName());

		int phId = 0;

		if (tData.getPhoneId().isEmpty()) {
			tData.setCountryCode("US");
			try {
				logger.info("PhoneID is not set. Calling postPhone  get the Phone Id.");
				phId = smsHandler.postPhone(smsMgrUrl, tData.getMessageFrom(), tData.getCountryCode(),
						tData.getFullName());
				tData.setPhoneId(phId + "");
			} catch (Exception e) {
				logger.info("#######Exception in postPhone : " + e.toString());
			}
		}

		try {
			if (tData.getTimezone() == null || tData.getTimezone().equals("failure")) {
				String urlJson = "";
				if (tData.getFullName() == null || tData.getFullName().isEmpty()) {
					urlJson = "{\r\n" + "  \"timezone\": \"America/Denver\"\r\n" + "}";
				} else {
					urlJson = "{" + "  \"timezone\": \"America/Denver\"" + "  \"name\": \"" + tData.getFullName() + "\""
							+ "}";
				}
				smsHandler.putPhone(smsMgrUrl, Integer.parseInt(tData.getPhoneId()), urlJson);
				logger.info("timezone and fullname are set  now for this phoneID.");
			}
		} catch (Exception e) {
			logger.info("#######Exception in timeZone : " + e.toString());
		}
	}

	private void dealWithGetService_C3602(TransactionData tData) {
		String response = "";
		try {
			logger.info("Getting stored SMS session from C360...Start.");
			httpReqHandler.getService_C360(tData);
			logger.info("Getting stored SMS session from C360...End.");
		} catch (Exception e1) {
			logger.info("#######Exception in calling getService_C360 : " + e1.toString());
		}

		if (tData.getServiceCount() > 0) {

			if (tData.getChatId() == null || tData.getChatId().isEmpty()) {
				tData.setSmsStatus("");
				logger.info("No ChatId associated with existing service.");
				logger.info("Deleting old service in C360...Start");
				httpReqHandler.deleteService_C360(tData);
				logger.info("Deleting old service in C360...End");
			} else {
				tData.setSmsStatus("Existing");
				logger.info("smsStatus : Existing");
			}
		} else if (tData.getServiceCount() == 0) {
			tData.setSmsStatus("New");
			logger.info("smsStatus : New");
		}
	}

	/**
	 * From : SMS Manager to : DgtSMSService
	 * 
	 */
	@RequestMapping(value = "/sms2Chat", method = RequestMethod.POST)
	public ResponseEntity<?> sms2Chat(@RequestBody InboundMsgFromSmsMgrEntity inboundSmsReq) {

		logger.info("#######Received Inbound message from SMS Manager...." + inboundSmsReq);

//		boolean failureEnable = true;
//		boolean resendFlag = true;
//		boolean updateContactName = false;
//		boolean rapidSmsCheck = false;

		TransactionData tData = new TransactionData();
		tData.setRequestFrom("smsmgr");
		tData.setSmsInboundParams(inboundSmsReq);
		tData.setAppConfigMap(appConfigMap);
		tData.setProgramIdMap(getMapFromJson(
				json_params_url + profile_env + "-" + tData.getProgramId() + ".json"));
		tData.setMessageType("inbound");
		tData.setSmsSubject("InboundSMS");

		String response = doSms2Chat(tData);

		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

	private String doSms2Chat(TransactionData tData) {

		///////////////////////////// Get Stored SMS sesssion
		String response = "";

		if (!tData.getMessageType().equalsIgnoreCase("outbound")) {
			try {
				logger.info("Getting stored SMS session from C360...Start.");
				httpReqHandler.getService_C360(tData);
				logger.info("Getting stored SMS session from C360...End.");
			} catch (Exception e1) {
				logger.info("#######Exception in calling getService_C360 : " + e1.toString());
			}
		}

		// if firstname and last name is not in Inbound SMS request,
		// update in SMSMgr

		try {
			if (tData.isUpdateContactName()) {
				if (tData.getFirstName() != null && !tData.getFirstName().isEmpty()) {
					logger.info("Updating the Contact name.");
					String oAuth_url = tData.getSms_oauth2_url();
					String clientId = tData.get_tokenClientID();
					String clientSecret = tData.get_tokenClientSecret();
					String grantType = tData.get_tokenGrantType();
					String smsMgrUrl = tData.getSms_mgr_url();

					smsHandler.getToken(oAuth_url, clientId, clientSecret, grantType);
					String urlJson = "{ \"name\" : \"" + tData.getFirstName() + " " + tData.getLastName() + "\"}";
					smsHandler.putPhone(smsMgrUrl, Integer.parseInt(tData.getPhoneId()), urlJson);
				}
			}
		} catch (Exception e2) {
			logger.info("Exception in updating Contact Name in SMSMgr : " + e2.getMessage());
		}

// there is logic in case of message type is inbound.
		if (tData.getCustomerId() == null || tData.getCustomerId().isEmpty()) {
			logger.info("Customer ID does not exist.");
			// if (tData.getMessageType().equalsIgnoreCase("outbound")) {
			return "Customer ID does not exist.";
		}

		// If Service already exists

		if (tData.getSmsStatus().equalsIgnoreCase("Existing")) {
			response = "";

			try {
				logger.info("Update user data in GMS...Start.");
				httpReqHandler.updateUserData_GMS(tData);
				logger.info("Update user data in GMS...End.");
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (tData.getChatEnded()) {
				try {
					logger.info("Deleting stored SMS session from C360...Start.");
					httpReqHandler.deleteService_C360(tData);
					logger.info("Deleting stored SMS session from C360...End.");
				} catch (Exception e1) {
					logger.info("#######Exception in calling deleteService_C360 : " + e1.toString());
				}
				//////////
			}
		}

		if (tData.getSmsStatus().equalsIgnoreCase("New") || tData.getSmsStatus().isEmpty() || tData.getChatEnded()) {
			///////////////////////////// Send auto Ack SMS to user
			try {
				if (tData.getMessageType().equalsIgnoreCase("inbound")) {
					if (tData.get_auto_ack_message_sendflag().equalsIgnoreCase("true")) {
						// Added to handle the load testing scenario, to bypass the SMS Manager during
						// Load Testing.
						if (!isLoadTesting) {
							logger.info("LOAD TESTING FLAG is enabled. Not Sending request to SMS Manager.");
							logger.info("Sending Auto Ack SMS message...Start.");
							doSms2SmsMgr(tData, tData.getAuto_ack_message_text());
						}
						logger.info("Sending Auto Ack SMS message...End.");
					} else {
						logger.debug("_auto_ack_message_sendflag is not set. So, not sending any SMS auto Ack.");
					}
				}

			} catch (Exception e1) {
				logger.info("#######Exception in calling sendSMS2SmsMgr : " + e1.toString());
			}

			///////////////////////////// Request chat initiate now..
			try {
				response = "";
				logger.info("Request to initiate chat with GMS server...Start.");
				response = httpReqHandler.requestChat_GMS(tData);
				logger.info("Request to initiate chat with GMS server...End.");
			} catch (Exception e1) {
				logger.info("Error in calling requestChat_GMS : " + e1.toString());
			}

			if (response.equalsIgnoreCase("failure")) {
				return "Failed during initiate chat request";
			}
			///////////////////////////// Store new session in C360....ENT_Action=start

			logger.info("Store new SMS session in C360...Start.");
			httpReqHandler.startService_C360(tData);
			logger.info("Store new SMS session in C360...End.");

			///////////////////////////// Update UserData in GMS

			response = "";
			// tData.setMessageType("inbound");
			try {
				logger.info("Update user data in GMS...Start.");
				httpReqHandler.updateUserData_GMS(tData);
				logger.info("Update user data in GMS...End.");
			} catch (Exception e) {
				logger.info("#######Exception in updateUserData_GMS : " + e.toString());
			}
		}
		///////////////////////////// Send chat message using GMS

		response = "";
		try {
			logger.info("Send message to GMS...Start.");
			response = httpReqHandler.sendMessage_GMS(tData);
			logger.info("Send message to GMS...End.");
		} catch (Exception e) {
			logger.info("#######Exception in sendMessage_GMS : " + e.toString());
		}
		return response;
	}

	/**
	 * From : IVR to : DgtSMSService
	 * 
	 */
	@RequestMapping(value = "/sendSms4Ivr", method = RequestMethod.POST)
	public ResponseEntity<?> sendSms4Ivr(@RequestBody PostMsgFromIvrEntity ivrMsgSummary) {

		logger.info("IVR POST data...." + ivrMsgSummary);

		String response = doSendSms4Ivr(ivrMsgSummary);
		logger.info("Response Sent to IVR : " + response);
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

	/**
	 * From : ORS chat workflow to : DgtSMSService purpose : to send welcome
	 * greeting or HOOP closed SMS back to customers
	 * 
	 */
	@RequestMapping(value = "/sendCSms", method = RequestMethod.POST)
	public ResponseEntity<?> sendCSms4Ors(@RequestBody PostMsgChatSmsEntity orsMsgSummary) {

		logger.info("#######ORS Chat Sms data...." + orsMsgSummary);
		
		// Added to handle the load testing scenario, to bypass the SMS Manager during
		// Load Testing.
		if (isLoadTesting) {
			logger.info("#######Bypassing SMS mgr call ...LOAD TESTING is true");
		return new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
		}
		
		
		
		TransactionData tData = new TransactionData();
		tData.setRequestFrom("ors");
		tData.setchatSMSParams(orsMsgSummary);
		tData.setAppConfigMap(appConfigMap);
		tData.setProgramIdMap(getMapFromJson(json_params_url + profile_env + "-"
				+ orsMsgSummary.get_message_program_id() + ".json"));

		String resJson = this.doSms2SmsMgr(tData, orsMsgSummary.get_message_body());

		if (!resJson.equalsIgnoreCase("FAILURE")) {
			resJson = "SUCCESS";
		}
		logger.info("#######ORS Chat Sms data Response : " + resJson);
		return new ResponseEntity<String>(resJson, HttpStatus.OK);
	}

	/**
	 * From : ORS chat workflow to : DgtSMSService purpose : to send welcome or hoop
	 * closed SMS back to customers
	 * 
	 */
	@RequestMapping(value = "/markDoneSms", method = RequestMethod.POST)
	public ResponseEntity<?> markDoneSms(@RequestBody PostMsgMarkdownSmsEntity orsMsgSummary) {

		logger.info("markDoneSms data...." + orsMsgSummary);

		String response = "Failure";
		TransactionData tData = new TransactionData();
		tData.setRequestFrom("ors");
		tData.setMarkdownSMSParams(orsMsgSummary);
		tData.setAppConfigMap(appConfigMap);

		try {
			logger.info("Deleting stored SMS session from C360...Start.");
			httpReqHandler.deleteService_C360(tData);
			logger.info("Deleting stored SMS session from C360...End.");
			response = "Success";
		} catch (Exception e1) {
			logger.info("#######Exception in calling deleteService_C360 : " + e1.toString());
		}

		logger.info("markDoneSms data Response : " + response);
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

	/***
	 * Service Request Implementation methods
	 */
	public Map<String, String> getMapFromJson(String jsonFileName) {

		Map<String, String> map = null;
		ObjectMapper mapper = new ObjectMapper();

		logger.debug("Json file : " + jsonFileName);
		try (InputStream inputStream = new URL(jsonFileName).openStream()) {
			String text = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines()
					.collect(Collectors.joining("\n"));
			map = mapper.readValue(text, Map.class);
			logger.debug("Json File parameters : " + map);
		} catch (Exception ex) {
			logger.info("Error reading json parameters from : " + jsonFileName);
			ex.printStackTrace();
		}
		return map;
	}

	// smsMsgStatus msgId and ProgramID

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	public String doSendSms4Ivr(PostMsgFromIvrEntity ivrPostMsg) {

		String programId = ivrPostMsg.get_outbound_message_program_id();
		String outboundMsg = ivrPostMsg.get_outbound_message_body();
		String phNumber = ivrPostMsg.get_outbound_message_from();

		// init values
		String lineType = null;
		int phoneId = 0;
		String timezone = "";
		String countryCode = "US";
		String subscriptionStatus = "";
		int subscriptionId = 0;
		int outboundMsgId = 0;
		String token;
		String responseJson = "{}";
		String firstName = "";
		String lastName = "";
		String fullName = "";

		Map<String, String> prgrmIdMap = this
				.getMapFromJson(json_params_url + profile_env + "-" + programId + ".json");
		// Map<String, String> appConfigMap= this.getMapFromJson("appConfig.json");

		// smsHandler.setRequestConfig(Integer.parseInt(appConfigMap.get("http_request_timeout_secs")));
		// "sms_oauth2_url"
		String url = appConfigMap.get("sms_oauth2_url");
		String clientId = prgrmIdMap.get("_tokenClientID");
		String clientSecret = prgrmIdMap.get("_tokenClientSecret");
		String grantType = prgrmIdMap.get("_tokenGrantType");

		// "optumMessengerURL":"https://ocp.optum.com/smsmgrstage/v1",
		String smsMgrUrl = appConfigMap.get("sms_mgr_url");

		try {
			token = smsHandler.getToken(url, clientId, clientSecret, grantType);

			if (token != null && !token.isEmpty()) {
				// Find out whether it is a mobile or not
				String lineType_countryCode = smsHandler.doPhoneLookup(smsMgrUrl, phNumber);
				logger.info("linetype-countrycode : " + lineType_countryCode);
				lineType = lineType_countryCode.split("::")[0];
				countryCode = lineType_countryCode.split("::")[1];
				if (lineType != null && lineType.isEmpty()) {
					responseJson = "{\r\n" + "\"_outbound_message_status\" : \"failure\",\r\n"
							+ "\"_outbound_message_status_reason\" : \"Not A Valid PhoneNumber\",\r\n"
							+ "\"_outbound_message_from\":\"" + phoneId + "\",\r\n"
							+ "\"_outbound_message_program_id\":\"" + programId + "\"\r\n" + "}";
				} else if (lineType != null && (lineType.equals("mobile") || lineType.equals("voip"))) {
					// getPhoneID and timezone
					String result = smsHandler.getPhoneDetails(smsMgrUrl, phNumber);
					logger.info("Return Result : " + result);

					try {
						if (result.startsWith("200::")) {

							String responseJs = result.split("::")[1];

							JsonNode rootNode = objectMapper.readTree(responseJs);
							JsonNode tokenNode = rootNode.path("phone");
							JsonNode phNode = tokenNode.get(0);
							JsonNode idNode = phNode.path("id");
							phoneId = idNode.asInt();
							JsonNode ccNode = phNode.path("country-code");
							countryCode = ccNode.asText();
							JsonNode tzNode = phNode.path("timezone");
							timezone = tzNode.asText();
							JsonNode nameNode = phNode.path("name");
							fullName = nameNode.asText();
							if (fullName != null && !fullName.isEmpty()) {
								if (fullName.split(" ").length > 1) {
									firstName = fullName.split(" ")[0];
									lastName = fullName.split(" ")[1];
								} else {
									firstName = fullName;
								}
							}

							logger.info("Phone ID : " + phoneId);
							logger.info("country-code : " + countryCode);
							logger.info("timeZone : " + timezone);
							logger.info("fullname : " + fullName);
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}

					// if there is no phone ID, need to create a new Phone ID..
					if (phoneId == 0) {
						phoneId = smsHandler.postPhone(smsMgrUrl, phNumber, countryCode, fullName);
					}

					if (timezone == null || timezone.equals("failure")) {
						logger.info("timezone is not set. Setting it to America/Denver.");
						smsHandler.putPhoneTimezone(smsMgrUrl, phoneId);
					}

					result = smsHandler.getSubscription(smsMgrUrl, phoneId, programId);
					if (result.contains("::")) {
						subscriptionId = Integer.parseInt(result.split("::")[0]);
						subscriptionStatus = result.split("::")[1];
					}
					// if (subscriptionId > 0 && !subscriptionStatus.equals("failure")) {
					// if not subscribed
					if (subscriptionStatus.equalsIgnoreCase("subscribed")
							|| subscriptionStatus.equalsIgnoreCase("confirmed")) {

					} else {
						String json = smsHandler.subscriprPhoneId(smsMgrUrl, phoneId, programId);
						try {
							JsonNode rNode = objectMapper.readTree(json);
							logger.info("Subscription Status : " + rNode.path("status").asText());
							subscriptionId = rNode.path("id").asInt();
							logger.info("Subscription Id : " + subscriptionId);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					// }

					if (subscriptionId == 0) {
						responseJson = "{\r\n" + "\"_outbound_message_status\" : \"failure\",\r\n"
								+ "\"_outbound_message_status_reason\" : \"SubscriptionError\",\r\n"
								+ "\"_outbound_message_from\":\"" + phoneId + "\",\r\n"
								+ "\"_outbound_message_program_id\":\"" + programId + "\"\r\n" + "}";
					} else {
						String resJson = smsHandler.sendSmsToSmsMgr(smsMgrUrl, programId, phoneId + "", outboundMsg);
						try {
							if (resJson != null && !resJson.isEmpty()) {

								if (resJson.equalsIgnoreCase("TIMEOUT")) {
									responseJson = "{\r\n" + "\"_outbound_message_status\" : \"failure\",\r\n"
											+ "\"_outbound_message_status_reason\" : \"ResponseTimeout\",\r\n"
											+ "\"_outbound_message_from\":\"" + phoneId + "\",\r\n"
											+ "\"_outbound_message_program_id\":\"" + programId + "\"\r\n" + "}";

								} else if (resJson.equalsIgnoreCase("FAILURE")) {
									responseJson = "{\r\n" + "\"_outbound_message_status\" : \"failure\",\r\n"
											+ "\"_outbound_message_status_reason\" : \"InternalError\",\r\n"
											+ "\"_outbound_message_from\":\"" + phoneId + "\",\r\n"
											+ "\"_outbound_message_program_id\":\"" + programId + "\"\r\n" + "}";

								} else {
									JsonNode rNode = objectMapper.readTree(resJson);
									logger.info("OutboundMessage Status : " + rNode.path("status").asText());
									outboundMsgId = rNode.path("id").asInt();
									logger.info("OutboundMessage Id : " + outboundMsgId);
									if (outboundMsgId > 0) {
										responseJson = "{\r\n" + "\"_outbound_message_status\" : \"success\",\r\n"
												+ "\"_outbound_message_id\" : \"" + outboundMsgId + "\",\r\n"
												+ "\"_outbound_message_from\":\"" + phoneId + "\",\r\n"
												+ "\"_outbound_message_program_id\":\"" + programId + "\"\r\n" + "}";
									}
								}
							} else {
								responseJson = "{\r\n" + "\"_outbound_message_status\" : \"failure\",\r\n"
										+ "\"_outbound_message_status_reason\" : \"ResponseError\",\r\n"
										+ "\"_outbound_message_from\":\"" + phoneId + "\",\r\n"
										+ "\"_outbound_message_program_id\":\"" + programId + "\"\r\n" + "}";
							}
						} catch (Exception e) {
							responseJson = "{\r\n" + "\"_outbound_message_status\" : \"failure\",\r\n"
									+ "\"_outbound_message_status_reason\" : \"ResponseError\",\r\n"
									+ "\"_outbound_message_from\":\"" + phoneId + "\",\r\n"
									+ "\"_outbound_message_program_id\":\"" + programId + "\"\r\n" + "}";
						}
					}
				} else { // phone is not a mobile
					responseJson = "{\r\n" + "\"_outbound_message_status\" : \"failure\",\r\n"
							+ "\"_outbound_message_status_reason\" : \"NotAValidMobile\",\r\n"
							+ "\"_outbound_message_from\":\"" + phoneId + "\",\r\n"
							+ "\"_outbound_message_program_id\":\"" + programId + "\"\r\n" + "}";
				}
			} else {
				responseJson = "{\r\n" + "\"_outbound_message_status\" : \"failure\",\r\n"
						+ "\"_outbound_message_status_reason\" : \"TokenError\",\r\n" + "\"_outbound_message_from\":\""
						+ phoneId + "\",\r\n" + "\"_outbound_message_program_id\":\"" + programId + "\"\r\n" + "}";
			}
		} catch (Exception e) {
			logger.info(e.toString());
			responseJson = "{\r\n" + "\"_outbound_message_status\" : \"failure\",\r\n"
					+ "\"_outbound_message_status_reason\" : \"" + e.toString() + "\",\r\n"
					+ "\"_outbound_message_from\":\"" + phoneId + "\",\r\n" + "\"_outbound_message_program_id\":\""
					+ programId + "\"\r\n" + "}";
		}

		return responseJson;
	}

	public String encodeUrlString(String url) {
		String encodedString = url;
		try {
			encodedString = URLEncoder.encode(url, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encodedString;
	}

	public String doSms2SmsMgr(TransactionData tData, String smsMsg) {

		String url = tData.getSms_oauth2_url();
		String clientId = tData.get_tokenClientID();
		String clientSecret = tData.get_tokenClientSecret();
		String grantType = tData.get_tokenGrantType();
		String smsMgrUrl = tData.getSms_mgr_url();
		int subscriptionId = 0;
		String subscriptionStatus = "";
		String resJson = "FAILURE";

		String token = smsHandler.getToken(url, clientId, clientSecret, grantType);
		String result = smsHandler.getSubscription(smsMgrUrl, Integer.parseInt(tData.getPhoneId()),
				tData.getProgramId());
		if (result.contains("::")) {
			subscriptionId = Integer.parseInt(result.split("::")[0]);
			subscriptionStatus = result.split("::")[1];
		}
		// logger.info("subscriptionId : " + subscriptionId);
		// logger.info("subscriptionStatus : " + subscriptionStatus);

		if (subscriptionId > 0 && !subscriptionStatus.equals("failure")) {
			// if not subscribed
			try {
				if (subscriptionStatus.equalsIgnoreCase("subscribed")
						|| subscriptionStatus.equalsIgnoreCase("confirmed")) {
					// do nothing
				} else {
					logger.info("Phone not subscribed. Subscribing now.");
					String json = smsHandler.subscriprPhoneId(smsMgrUrl, Integer.parseInt(tData.getPhoneId()),
							tData.getProgramId());
					try {
						JsonNode rNode = objectMapper.readTree(json);
						subscriptionStatus = rNode.path("status").asText();
						logger.info("Subscription Status : " + subscriptionStatus);
						subscriptionId = rNode.path("id").asInt();
						logger.info("Subscription Id : " + subscriptionId);
					} catch (Exception e) {
						logger.info("#######Exception in sendSMS2SmsMgr : " + e.toString());
					}
				}
			} catch (Exception e) {
				logger.info("Exception in subscribing : " + e.getMessage());
			}
			// send autoAcknowledge message
			resJson = smsHandler.sendSmsToSmsMgr(smsMgrUrl, tData.getProgramId(), tData.getPhoneId(), smsMsg);
		}
		return resJson;
	}
}