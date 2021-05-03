package codedepot.sms.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import codedepot.commons.JsonHelper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PostMsgUpdateSmsContactEntity {
	protected static final Logger Log = LoggerFactory.getLogger(PostMsgFromIvrEntity.class);
	private static final ObjectMapper mapper = new ObjectMapper();
	
	@JsonInclude(Include.NON_NULL)
	private String sms_program_id;
	
	public String get_sms_program_id() {
		return sms_program_id;
	}

	@JsonProperty("sms_program_id")
	public void set_sms_program_id(String sms_program_id) {
		this.sms_program_id = sms_program_id;
	}
	
	@JsonInclude(Include.NON_NULL)
	private String ENT_ContactFirstNm;
	
	public String get_ENT_ContactFirstNm() {
		return ENT_ContactFirstNm;
	}

	@JsonProperty("ENT_ContactFirstNm")
	public void set_ENT_ContactFirstNm(String ENT_ContactFirstNm) {
		this.ENT_ContactFirstNm = ENT_ContactFirstNm;
	}
	
	@JsonInclude(Include.NON_NULL)
	private String ENT_ContactLastNm;
	
	public String get_ENT_ContactLastNm() {
		return ENT_ContactLastNm;
	}

	@JsonProperty("ENT_ContactLastNm")
	public void set_ENT_ContactLastNm(String ENT_ContactLastNm) {
		this.ENT_ContactLastNm = ENT_ContactLastNm;
	}
	
	
	@JsonInclude(Include.NON_NULL)
	private String sms_phone_id;
	
	public String get_sms_phone_id() {
		return sms_phone_id;
	}

	@JsonProperty("sms_phone_id")
	public void set_sms_phone_id(String sms_phone_id) {
		this.sms_phone_id = sms_phone_id;
	}
	
	
	@JsonInclude(Include.NON_NULL)
	private String sms_service_id;
	
	public String get_sms_service_id() {
		return sms_service_id;
	}

	@JsonProperty("sms_service_id")
	public void set_sms_service_id(String sms_service_id) {
		this.sms_service_id = sms_service_id;
	}
	
	
	
	
	
	
	@Override
	public String toString() {		
		try {
			return JsonHelper.objectToJson(this);
		} catch(Exception e) {
			return "[ sms_program_id=" + sms_program_id 
					+ ",ENT_ContactFirstNm="+ENT_ContactFirstNm
					+ ",ENT_ContactLastNm="+ENT_ContactLastNm
					+ ",sms_phone_id="+sms_phone_id
				+ ", sms_service_id=sms_service_id ]";
		}
	}

}


//treatmentJSON['contac_first_nm']=getuData('ENT_ContactFirstNm',system.InteractionID);
//treatmentJSON['contac_last_nm']=getuData('ENT_ContactLastNm',system.InteractionID);
//treatmentJSON['sms_phone_id']=getuData('SMS_PhoneID',system.InteractionID);
//treatmentJSON['sms_service_id']=getuData('SMS_Service_ID',system.InteractionID);