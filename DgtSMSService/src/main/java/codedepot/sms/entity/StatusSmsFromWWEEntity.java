package codedepot.sms.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import codedepot.commons.JsonHelper;

//{
//    "_outbound_message_id" : "1627501",
//    "_current_poll_attempt" : "1",
//    "_max_poll_attempts" : "2",
//    "_outbound_message_program_id":"498309"
//}


@JsonIgnoreProperties(ignoreUnknown = true)
public class StatusSmsFromWWEEntity {

	protected static final Logger Log = LoggerFactory.getLogger(StatusSmsFromWWEEntity.class);

	
	@JsonInclude(Include.NON_NULL)
	private String _outbound_message_id;
	@JsonInclude(Include.NON_NULL)
	private String _current_poll_attempt;
	@JsonInclude(Include.NON_NULL)
	private String _max_poll_attempts;
	@JsonInclude(Include.NON_NULL)
	private String _outbound_message_program_id;
	
	public String get_outbound_message_id() {
		return _outbound_message_id;
	}
	
	@JsonProperty("_outbound_message_id")
	public void set_outbound_message_id(String _outbound_message_id) {
		this._outbound_message_id = _outbound_message_id;
	}
	
	public String get_current_poll_attempt() {
		return _current_poll_attempt;
	}
	
	@JsonProperty("_current_poll_attempt")
	public void set_current_poll_attempt(String _current_poll_attempt) {
		this._current_poll_attempt = _current_poll_attempt;
	}
	
	public String get_max_poll_attempts() {
		return _max_poll_attempts;
	}
	
	@JsonProperty("_max_poll_attempts")
	public void set_max_poll_attempts(String _max_poll_attempts) {
		this._max_poll_attempts = _max_poll_attempts;
	}
	
	public String get_outbound_message_program_id() {
		return _outbound_message_program_id;
	}
	
	@JsonProperty("_outbound_message_program_id")
	public void set_outbound_message_program_id(String _outbound_message_program_id) {
		this._outbound_message_program_id = _outbound_message_program_id;
	}
	
	@Override
	public String toString() {		
		try {
			return JsonHelper.objectToJson(this);
		} catch(Exception e) {
			return "[ _outbound_message_id=" + _outbound_message_id 
					+ ",_current_poll_attempt="+_current_poll_attempt
					+ ",_max_poll_attempts="+_max_poll_attempts
				+ ", _outbound_message_program_id=_outbound_message_program_id ]";
		}
	}
	
	
	
}
