/**
 * @author svanthen
 * Organization : CodeDepot, Optum
 * Created : Aug 27, 2020
 * 
 */
package codedepot.sms.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import codedepot.commons.JsonHelper;

/**
 * {
"_outbound_message_program_id":"498309", 
 "_outbound_message_phone_id": "2101192",
 "_outbound_message_body": " from agent to sms service"
 }
 * 
 * 
 * @author svanthen
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Chat2SMSfromAgentEntity {
	
	protected static final Logger Log = LoggerFactory.getLogger(Chat2SMSfromAgentEntity.class);

	@JsonInclude(Include.NON_NULL)
	private String _outbound_message_body;
	
	public String get_outbound_message_body() {
		return _outbound_message_body;
	}

	@JsonProperty("_outbound_message_body")
	public void set_outbound_message_body(String _outbound_message_body) {
		this._outbound_message_body = _outbound_message_body;
	}


	@JsonInclude(Include.NON_NULL)
	private String _outbound_message_program_id;
		
	public String get_outbound_message_program_id() {
		return _outbound_message_program_id;
	}

	@JsonProperty("_outbound_message_program_id")
	public void set_outbound_message_program_id(String _outbound_message_program_id) {
		this._outbound_message_program_id = _outbound_message_program_id;
	}

	
	@JsonInclude(Include.NON_NULL)
	private String _outbound_message_phone_id;
	
	public String get_outbound_message_phone_id() {
		return _outbound_message_phone_id;
	}

	@JsonProperty("_outbound_message_phone_id")
	public void set_outbound_message_phone_id(String _outbound_message_phone_id) {
		this._outbound_message_phone_id = _outbound_message_phone_id;
	}

	
	@Override
	public String toString() {		
		try {
			return JsonHelper.objectToJson(this);
		} catch(Exception e) {
			return "[ _outbound_message_body=" + _outbound_message_body 
					+ ",_outbound_message_program_id="+_outbound_message_program_id
				+ ", _outbound_message_phone_id=_outbound_message_phone_id ]";
		}
	}

}
