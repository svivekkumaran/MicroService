/**
 * @author svanthen
 * Organization : CodeDepot, Optum
 * Created : Aug 12, 2020
 * 
 */
package codedepot.sms.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import codedepot.sms.controller.DgtSMSServiceController;



/**
 * Servlet implementation class OmniSmsServiceController
 */
@Component
public class SmsMgrHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(DgtSMSServiceController.class);
	private static final ObjectMapper objectMapper = new ObjectMapper();

	RequestConfig defaultRequestConfig;

	private String token="";



	//private SmsAppConfig config;
//    @Autowired
//    public void setSmsAppConfig(SmsAppConfig config) {
//        this.config = config;
//    }

//	@Autowired
	public SmsMgrHandler() {
		super();

	}

	/***
	 * Setting timeout in seconds for each HttpClient Requests
	 * @param timeout will be read from application.properties file
	 */
	public void setRequestConfig(int timeout) {
		defaultRequestConfig = RequestConfig.custom().setConnectTimeout(Timeout.ofSeconds(timeout))
				.setConnectionRequestTimeout(Timeout.ofSeconds(timeout)).build();
	}

	public String getToken(String smsMgrUrl, String clientId, String clientSecret, String grantType) {

		int responseCode = 500;
		String responseJson = "";
		token = "";

	//	smsMgrUrl = config.getOauth2_sms_url();
		logger.debug("getToken-URL : " + smsMgrUrl);

		try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig)
				.build()) {

			HttpPost httpPost = new HttpPost(smsMgrUrl);
			HttpEntity entity = MultipartEntityBuilder.create().addTextBody("client_id", clientId)
					.addTextBody("client_secret", clientSecret)
					.addTextBody("grant_type", grantType).build();

			httpPost.setEntity(entity);

			try (CloseableHttpResponse response1 = httpclient.execute(httpPost)) {
				logger.info(response1.getCode() + " " + response1.getReasonPhrase());
				responseCode = response1.getCode();
				HttpEntity entity1 = response1.getEntity();
				responseJson = EntityUtils.toString(entity1);
				logger.info("RESPONSE : " + responseJson);
				EntityUtils.consume(entity1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			logger.info("Exception in getting token : "+e1.getMessage());
		}
		
		
		if (responseCode != 200) { //try again
			try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig)
					.build()) {

				HttpPost httpPost = new HttpPost(smsMgrUrl);
				HttpEntity entity = MultipartEntityBuilder.create().addTextBody("client_id", clientId)
						.addTextBody("client_secret", clientSecret)
						.addTextBody("grant_type", grantType).build();

				httpPost.setEntity(entity);

				try (CloseableHttpResponse response1 = httpclient.execute(httpPost)) {
					logger.info(response1.getCode() + " " + response1.getReasonPhrase());
					responseCode = response1.getCode();
					HttpEntity entity1 = response1.getEntity();
					responseJson = EntityUtils.toString(entity1);
					logger.info("RESPONSE : " + responseJson);
					EntityUtils.consume(entity1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e1) {
				logger.info("Exception in getting token : "+e1.getMessage());
			}	
		} else {
			try {
				JsonNode rNode = objectMapper.readTree(responseJson);
				JsonNode tNode = rNode.path("access_token");
				token = tNode.asText();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return token;
	}

	/***
	 * { "number": "+18134597263", "country_code": "US", "line_type": "mobile",
	 * "carrier_name": "Verizon Wireless", "created_at": "2020-07-23 12:17:45 UTC",
	 * "updated_at": "2020-07-23 23:13:44 UTC", "lookedup_at": "2020-07-23 23:13:44
	 * UTC" }
	 * ph_lookup_url=https://ocp.optum.com/smsmgrstage/v1/phones/number/{tfn}/lookup
	 * 
	 * @param tfn
	 * @return
	 */

	public String doPhoneLookup(String smsMgrUrl, String tfn) {

		int responseCode = 500;
		String responseJson = "";
		String lineType = "failure";
		String countryCode="failure";

		smsMgrUrl = smsMgrUrl + "/phones/number/" + tfn + "/lookup";

		logger.debug("doPhoneLookup-URL : " + smsMgrUrl);

		try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig)
				.build()) {

			HttpGet httpget = new HttpGet(smsMgrUrl.trim());
			httpget.addHeader("Authorization", "Bearer " + token);
			httpget.addHeader("Cache-Control", "no-cache");
			httpget.addHeader("Accept", "application/json");
			try (CloseableHttpResponse response1 = httpclient.execute(httpget)) {
				logger.debug(response1.getCode() + " " + response1.getReasonPhrase());
				responseCode = response1.getCode();
				HttpEntity entity1 = response1.getEntity();
				responseJson = EntityUtils.toString(entity1);
				logger.debug("RESPONSE : " + responseJson);
				EntityUtils.consume(entity1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (responseCode == 200) {
				try {
					JsonNode rootNode = objectMapper.readTree(responseJson);
					JsonNode tokenNode = rootNode.path("line_type");
					lineType = tokenNode.asText();
					logger.info("Line type is : " + lineType);
					JsonNode ccNode = rootNode.path("country_code");
					countryCode = ccNode.asText();
					logger.info("country code is : " + countryCode);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} 			
		} catch (Exception e) {
			logger.info("exception " + e.toString());
		}


		return lineType+"::"+countryCode;
	}
	
	public int postPhone(String smsMgrUrl,String tfn, String countryCode, String name) {
		int responseCode = 500;
		boolean result = false;
		String responseJson = "";
		String lineType = "";
		int phoneId=0;
		
		smsMgrUrl = smsMgrUrl + "/phones";
		
			String	urlParameters ="{\r\n" + 
					"  \"name\": \""+name+"\",\r\n" + 
					"  \"number\": \""+tfn+"\",\r\n" + 
					"  \"country-code\": \""+countryCode+"\",\r\n" + 
					"  \"timezone\": \"America/Denver\"\r\n" + 
					"}";
		
			logger.debug("postPhone-URL : " + smsMgrUrl);
			logger.debug("urlParameters : "+urlParameters);
			try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig)
					.build()) {

				HttpPost httpPost = new HttpPost(smsMgrUrl);
				httpPost.addHeader("Authorization", "Bearer " + token);
				httpPost.addHeader("Cache-Control", "no-cache");
				httpPost.addHeader("Content-Type", "application/json");
				httpPost.addHeader("Accept", "application/json");

				StringEntity stringEntity = new StringEntity(urlParameters);
				httpPost.setEntity(stringEntity);

				try (CloseableHttpResponse response1 = httpclient.execute(httpPost)) {
					logger.debug(response1.getCode() + " " + response1.getReasonPhrase());
					responseCode = response1.getCode();
					HttpEntity entity1 = response1.getEntity();
					responseJson = EntityUtils.toString(entity1);
					logger.debug("RESPONSE : " + responseJson);
					EntityUtils.consume(entity1);
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (responseCode == 200) {
					try {
						JsonNode rootNode = objectMapper.readTree(responseJson);
						JsonNode tokenNode = rootNode.path("id");
						phoneId = tokenNode.asInt();
						logger.info("Phone ID : " + phoneId);
					} catch (Exception e) {
						logger.info("Exception parsing for PhoneID : "+e.toString());
						e.printStackTrace();
					}
					
				}
			} catch (Exception e1) {
				logger.info("Exception in postPhone : "+e1.toString());
				e1.printStackTrace();
			}
		return phoneId;		
	}

	// get_phone_url=https://ocp.optum.com/smsmgrstage/v1/phones?number={tfn}

	public String getPhoneDetails(String smsMgrUrl,String tfn) {

		int responseCode = 500;
		boolean result = false;
		String responseJson = "";
		String lineType = "";
		int phoneId=0;
		String countryCode="failure";
	

		smsMgrUrl = smsMgrUrl + "/phones?number=" + tfn;

		logger.debug("getPhoneDetails-URL : " + smsMgrUrl);
		try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig)
				.build()) {
			HttpGet httpget = new HttpGet(smsMgrUrl.trim());
			httpget.addHeader("Authorization", "Bearer " + token);
			httpget.addHeader("Cache-Control", "no-cache");
			httpget.addHeader("Accept", "application/json");

			try (CloseableHttpResponse response1 = httpclient.execute(httpget)) {
				logger.debug(response1.getCode() + " " + response1.getReasonPhrase());
				responseCode = response1.getCode();
				HttpEntity entity1 = response1.getEntity();
				responseJson = EntityUtils.toString(entity1);
				logger.debug("RESPONSE : " + responseJson);
				EntityUtils.consume(entity1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			logger.info("exception " + e.toString());
		}

//		if (responseCode == 200) {
//
//			try {  //country-code
//				JsonNode rootNode = objectMapper.readTree(responseJson);
//				JsonNode tokenNode = rootNode.path("phone");
//				JsonNode phNode = tokenNode.get(0);
//				JsonNode idNode = phNode.path("id");
//				phoneId = idNode.asInt();
//				JsonNode ccNode = phNode.path("country-code");
//				countryCode = ccNode.asText();
//				logger.info("Phone ID : " + phoneId);
//				logger.info("country-code : " + countryCode);
//
//				result = true;
//			} catch (Exception e) {
//				logger.info("Error in gettingPhone Details : "+e.toString());
//				e.printStackTrace();
//			}
//		}

		//return phoneId+"::"+countryCode;
		return responseCode+"::"+responseJson;
	}

	// put_phone_url=https://ocp.optum.com/smsmgrstage/v1/phones/{phId}

	public boolean putPhoneTimezone(String smsMgrUrl, int phId) {
		int responseCode = 500;
		boolean result = false;
		String responseJson = "";

		smsMgrUrl = smsMgrUrl + "/phones/" + phId;

		logger.debug("putPhoneTimezone-URL : " + smsMgrUrl);
		try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig)
				.build()) {
			HttpPut httpput = new HttpPut(smsMgrUrl.trim());
			httpput.addHeader("Authorization", "Bearer " + token);
			httpput.addHeader("Cache-Control", "no-cache");
			httpput.addHeader("Content-Type", "application/json");
			httpput.addHeader("Accept", "application/json");

			String inputJson = "{\r\n" + "  \"timezone\": \"America/Denver\"\r\n" + "}";
			logger.debug("putPhoneTimeZone inputJson : " + inputJson);
			StringEntity stringEntity = new StringEntity(inputJson);
			httpput.setEntity(stringEntity);

			try (CloseableHttpResponse response1 = httpclient.execute(httpput)) {
				logger.debug(response1.getCode() + " " + response1.getReasonPhrase());
				responseCode = response1.getCode();
				HttpEntity entity1 = response1.getEntity();
				responseJson = EntityUtils.toString(entity1);
				logger.debug("RESPONSE : " + responseJson);
				EntityUtils.consume(entity1);
				if (responseCode == 200) {
					result = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			logger.info("exception " + e.toString());
		}
		return result;
	}
	
	public boolean putPhone(String smsMgrUrl, int phId, String urlJson) {
		int responseCode = 500;
		boolean result = false;
		String responseJson = "";

		smsMgrUrl = smsMgrUrl + "/phones/" + phId;

		logger.debug("putPhone-URL : " + smsMgrUrl);
		try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig)
				.build()) {
			HttpPut httpput = new HttpPut(smsMgrUrl.trim());
			httpput.addHeader("Authorization", "Bearer " + token);
			httpput.addHeader("Cache-Control", "no-cache");
			httpput.addHeader("Accept", "application/json");
			httpput.addHeader("Content-Type", "application/json");
			
			logger.debug("putPhone urlJson : " + urlJson);
			StringEntity stringEntity = new StringEntity(urlJson);
			httpput.setEntity(stringEntity);

			try (CloseableHttpResponse response1 = httpclient.execute(httpput)) {
				logger.debug(response1.getCode() + " " + response1.getReasonPhrase());
				responseCode = response1.getCode();
				HttpEntity entity1 = response1.getEntity();
				responseJson = EntityUtils.toString(entity1);
				logger.debug("RESPONSE : " + responseJson);
				EntityUtils.consume(entity1);
				if (responseCode == 200) {
					result = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			logger.info("exception " + e.toString());
		}
		return result;
	}

//get_subscription_url=https://ocp.optum.com/smsmgrstage/v1/subscriptions?phone-id={phId}&program-id={prgrmId}

	public String getSubscription(String smsMgrUrl, int phoneId, String programId) {
		int responseCode = 0;
		String responseJson = "";
		boolean result = false;
		String subscriptionStatus = "failure";
		int subscriptionId = 0 ;

		smsMgrUrl = smsMgrUrl + "/subscriptions?phone-id=" + phoneId + "&program-id=" + programId;

		logger.debug("getSubscription-URL : " + smsMgrUrl);
		try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig)
				.build()) {
			HttpGet httpget = new HttpGet(smsMgrUrl.trim());
			httpget.addHeader("Authorization", "Bearer " + token);
			httpget.addHeader("Cache-Control", "no-cache");
			httpget.addHeader("Accept", "application/json");


			try (CloseableHttpResponse response1 = httpclient.execute(httpget)) {
				logger.debug(response1.getCode() + " " + response1.getReasonPhrase());
				responseCode = response1.getCode();
				HttpEntity entity1 = response1.getEntity();
				responseJson = EntityUtils.toString(entity1);
				logger.debug("RESPONSE : " + responseJson);
				EntityUtils.consume(entity1);
				result = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			logger.info("exception " + e.toString());
		}

		if (responseCode == 200) {

			try {
				JsonNode rootNode = objectMapper.readTree(responseJson);
				JsonNode tokenNode = rootNode.path("subscription");

				JsonNode phNode = tokenNode.get(0);
				JsonNode idNode = phNode.path("status");
				subscriptionStatus = idNode.asText();
				logger.info("subscriptionStatus : " + subscriptionStatus);
				subscriptionId = phNode.path("id").asInt();
				logger.info("Subscription Id : " + subscriptionId);
				result = true;
			} catch (Exception e) {
				// e.printStackTrace();
				logger.info("subscriptionStatus is empty.");
			}
		}
		return subscriptionId+"::"+subscriptionStatus;
	}

//subscribe_sms_url=https://ocp.optum.com/smsmgrstage/v1/phones/{phId}/subscribe

	public String subscriprPhoneId(String smsMgrUrl, int phId, String prmId) {

		int responseCode = 500;
		String responseJson = "";
		// boolean result = false;

		smsMgrUrl = smsMgrUrl + "/phones/" + phId + "/subscribe";

		logger.info("subscriprPhoneId-URL : " + smsMgrUrl);

		try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig)
				.build()) {

			HttpPost httpPost = new HttpPost(smsMgrUrl);
			httpPost.addHeader("Authorization", "Bearer " + token);
			httpPost.addHeader("Cache-Control", "no-cache");
			httpPost.addHeader("Content-Type", "application/json");
			httpPost.addHeader("Accept", "application/json");

			String smsMsg = "{\"program-id\": "+ prmId +"}";
			StringEntity stringEntity = new StringEntity(smsMsg);
			httpPost.setEntity(stringEntity);

			try (CloseableHttpResponse response1 = httpclient.execute(httpPost)) {
				logger.debug(response1.getCode() + " " + response1.getReasonPhrase());
				responseCode = response1.getCode();
				HttpEntity entity1 = response1.getEntity();
				responseJson = EntityUtils.toString(entity1);
				logger.debug("RESPONSE : " + responseJson);
				EntityUtils.consume(entity1);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (responseCode != 200) {
				responseJson = "FAILURE";
			}
		} catch (Exception e1) {
			logger.info("Exception in subscribePhoneId : "+e1.getMessage());
		}

		return responseJson;

	}
//post_unsubscribe_url=https://ocp.optum.com/smsmgrstage/v1/phones/{phId}/unsubscribe

	public String postUnsubscribe(String smsMgrUrl, String phId, String prgmId) {

		int responseCode = 500;
		String responseJson = "";
		// boolean result = false;

		smsMgrUrl = smsMgrUrl + "/phones/" + phId + "/unsubscribe";

		logger.debug("postUnsubscribe-URL : " + smsMgrUrl);

		try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig)
				.build()) {

			HttpPost httpPost = new HttpPost(smsMgrUrl);
			httpPost.addHeader("Authorization", "Bearer " + token);
			httpPost.addHeader("Cache-Control", "no-cache");
			httpPost.addHeader("Content-Type", "application/json");
			httpPost.addHeader("Accept", "application/json");

			String smsMsg = "{\"program-id\": "+prgmId+"}";
			StringEntity stringEntity = new StringEntity(smsMsg);
			httpPost.setEntity(stringEntity);

			try (CloseableHttpResponse response1 = httpclient.execute(httpPost)) {
				logger.debug(response1.getCode() + " " + response1.getReasonPhrase());
				responseCode = response1.getCode();
				HttpEntity entity1 = response1.getEntity();
				responseJson = EntityUtils.toString(entity1);
				logger.debug("RESPONSE : " + responseJson);
				EntityUtils.consume(entity1);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (responseCode != 200) {
				responseJson = "FAILURE";
			}
		} catch (Exception e1) {
			logger.info("Exception in postUnsubscribe : "+e1.getMessage());
		}

		return responseJson;

	}

//outbound_msg_url=https://ocp.optum.com/smsmgrstage/v1/programs/{prgrmId}/phones/{phId}/outbound-messages

	public String sendSmsToSmsMgr(String smsMgrUrl,  String prgmId, String phId, String sms) {
		int responseCode = 0;
		String responseJson = "";
		// boolean result = false;

		smsMgrUrl = smsMgrUrl + "/programs/" + prgmId + "/phones/" + phId + "/outbound-messages";
		String smsMsg = " {\r\n" + "    \"body\": \"" + sms + "\",\r\n" + "     \"send-immediately\": true\r\n"
				+ "  }";
		
		logger.debug("sendSMS to sms manager-URL : " + smsMgrUrl);
		logger.debug("sendSMS to sms message : " + smsMsg);
		try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig)
				.build()) {

			HttpPost httpPost = new HttpPost(smsMgrUrl);
			httpPost.addHeader("Authorization", "Bearer " + token);
			httpPost.addHeader("Cache-Control", "no-cache");
			httpPost.addHeader("Content-Type", "application/json");
			httpPost.addHeader("Accept", "application/json");

			
			StringEntity stringEntity = new StringEntity(smsMsg);
			httpPost.setEntity(stringEntity);

			try (CloseableHttpResponse response1 = httpclient.execute(httpPost)) {
				logger.debug(response1.getCode() + " " + response1.getReasonPhrase());
				responseCode = response1.getCode();
				logger.debug("ResponseCode : " + responseCode);
				HttpEntity entity1 = response1.getEntity();
				responseJson = EntityUtils.toString(entity1);
				logger.debug("RESPONSE : " + responseJson);
				EntityUtils.consume(entity1);
			} catch (Exception e) {
				logger.info("Exception in sendSmsToSmsmgr : "+e.getMessage());
			}

			if (responseCode != 200) {
				responseJson = "FAILURE";
			}
		} catch (SocketTimeoutException e1) {
			logger.info("Exception in sendSmsToSmsMgr : "+e1.getMessage());
			responseJson = "FAILURE";

		} catch (Exception e1) {
			logger.info("Exception in sendSmsToSmsMgr : "+e1.getMessage());
			responseJson = "FAILURE";
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
	
	public String doHttpPost4SmsMgr(String url, String msgBody, int timeout) {
		int responseCode = 0;
		String responseJson = "";
		
		logger.debug("url : "+url);
		logger.debug("urlParameters : "+msgBody);
		
		defaultRequestConfig = RequestConfig.custom().setConnectTimeout(Timeout.ofSeconds(timeout))
				.setConnectionRequestTimeout(Timeout.ofSeconds(timeout)).build();
		try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig)
				.build()) {

			HttpPost httpPost = new HttpPost(url);
			httpPost.addHeader("Authorization", "Bearer " + token);
			httpPost.addHeader("Cache-Control", "no-cache");
			httpPost.addHeader("Content-Type", "application/json");
			httpPost.addHeader("Accept", "application/json");

			StringEntity stringEntity = new StringEntity(msgBody);
			httpPost.setEntity(stringEntity);

			try (CloseableHttpResponse response1 = httpclient.execute(httpPost)) {
				logger.debug(response1.getCode() + " " + response1.getReasonPhrase());
				responseCode = response1.getCode();
				HttpEntity entity1 = response1.getEntity();
				responseJson = EntityUtils.toString(entity1);
				logger.debug("RESPONSE : " + responseJson);
				EntityUtils.consume(entity1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		return responseCode + "::" + responseJson;
	} catch(Exception e) {
		
	}
		return responseJson;
}
}