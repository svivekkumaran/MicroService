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
public class PostMsgChatSmsEntity {
	
	protected static final Logger Log = LoggerFactory.getLogger(PostMsgFromIvrEntity.class);
	private static final ObjectMapper mapper = new ObjectMapper();

	
	@JsonInclude(Include.NON_NULL)
	private String message_body;
	
	@JsonInclude(Include.NON_NULL)
	private String message_interaction_id;
	
	public String getInteractionId() {
		return message_interaction_id;
	}

	@JsonProperty("message_interaction_id")
	public void setInteractionId(String message_interaction_id) {
		message_interaction_id = message_interaction_id;
	}

	public String get_message_body() {
		return message_body;
	}

	@JsonProperty("message_body")
	public void set_message_body(String message_body) {
		this.message_body = message_body;
	}

	@JsonInclude(Include.NON_NULL)
	private String message_phone_number;
	
	public String get_message_phone_number() {
		return message_phone_number;
	}

	@JsonProperty("message_phone_number")
	public void set_message_phone_number(String message_phone_number) {
		this.message_phone_number = message_phone_number;
	}
//////////////	
	@JsonInclude(Include.NON_NULL)
	private String message_program_id;
	
	public String get_message_program_id() {
		return message_program_id;
	}

	@JsonProperty("message_program_id")
	public void set_message_program_id(String message_program_id) {
		this.message_program_id = message_program_id;
	}
/////////////////	
	@JsonInclude(Include.NON_NULL)
	private String message_phone_id;
	
	public String get_message_phone_id() {
		return message_phone_id;
	}

	@JsonProperty("message_phone_id")
	public void set_message_phone_id(String message_phone_id) {
		this.message_phone_id = message_phone_id;
	}
	
	@Override
	public String toString() {		
		try {
			return JsonHelper.objectToJson(this);
		} catch(Exception e) {
			return "[ message_body=" + message_body 
					+ ",message_phone_id="+message_phone_id
					+ ",message_program_id="+message_program_id
					+ ",message_interaction_id="+message_interaction_id
				+ ", message_phone_number="+message_phone_number+" ]";
		}
	}
	
	
}
