package codedepot.sms.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PostMsgMarkdownSmsEntity {
	protected static final Logger Log = LoggerFactory.getLogger(PostMsgFromIvrEntity.class);

	
	@JsonInclude(Include.NON_NULL)
	private String sms_service_id;
	
	public String get_sms_service_id() {
		return sms_service_id;
	}

	@JsonProperty("sms_service_id")
	public void set_sms_service_id(String sms_service_id) {
		this.sms_service_id = sms_service_id;
	}

}
