/**
 * @author svanthen
 * Organization : CodeDepot, Optum
 * Created : Aug 30, 2020
 * 
 */
package codedepot.sms.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import codedepot.commons.JsonHelper;

/***
 * 
 * {"_outbound_first_name":"SUDEER",
 *  "_outbound_last_name":"V",
 *  "_outbound_message_program_id":"498309",
 *   "_outbound_message_from":"+18134597263",
 *    "_outbound_contact_id": "00089bFEMBD706WC", 
 *    "_outbound_agent_id": "EUTS_SMSAgent01"}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OutboundMsgFromWWEEntity {
	
	protected static final Logger Log = LoggerFactory.getLogger(OutboundMsgFromWWEEntity.class);

	@JsonInclude(Include.NON_NULL)
	private String _outbound_first_name;
	@JsonInclude(Include.NON_NULL)
	private String _outbound_last_name;
	@JsonInclude(Include.NON_NULL)
	private String _outbound_message_program_id;
	@JsonInclude(Include.NON_NULL)
	private String _outbound_message_from;
	@JsonInclude(Include.NON_NULL)
	private String _outbound_contact_id;
	@JsonInclude(Include.NON_NULL)
	private String _outbound_agent_id;
	
	
	public String get_outbound_first_name() {
		return _outbound_first_name;
	}

	@JsonProperty("_outbound_first_name")
	public void set_outbound_first_name(String _outbound_first_name) {
		this._outbound_first_name = _outbound_first_name;
	}

	public String get_outbound_last_name() {
		return _outbound_last_name;
	}

	@JsonProperty("_outbound_last_name")
	public void set_outbound_last_name(String _outbound_last_name) {
		this._outbound_last_name = _outbound_last_name;
	}

	public String get_outbound_message_program_id() {
		return _outbound_message_program_id;
	}

	@JsonProperty("_outbound_message_program_id")
	public void set_outbound_message_program_id(String _outbound_message_program_id) {
		this._outbound_message_program_id = _outbound_message_program_id;
	}

	public String get_outbound_message_from() {
		return _outbound_message_from;
	}

	@JsonProperty("_outbound_message_from")
	public void set_outbound_message_from(String _outbound_message_from) {
		this._outbound_message_from = _outbound_message_from;
	}

	public String get_outbound_contact_id() {
		return _outbound_contact_id;
	}

	@JsonProperty("_outbound_contact_id")
	public void set_outbound_contact_id(String _outbound_contact_id) {
		this._outbound_contact_id = _outbound_contact_id;
	}

	public String get_outbound_agent_id() {
		return _outbound_agent_id;
	}

	@JsonProperty("_outbound_agent_id")
	public void set_outbound_agent_id(String _outbound_agent_id) {
		this._outbound_agent_id = _outbound_agent_id;
	}


	public OutboundMsgFromWWEEntity() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String toString() {		
		try {
			return JsonHelper.objectToJson(this);
		} catch(Exception e) {
			return "[ _outbound_first_name=" + _outbound_first_name 
					+ ",_outbound_last_name="+_outbound_last_name
					+ ",_outbound_message_program_id="+_outbound_message_program_id
					+ ",_outbound_message_from="+_outbound_message_from
					+ ",_outbound_contact_id="+_outbound_contact_id
				+ ", _outbound_agent_id=_outbound_agent_id ]";
		}
	}

}
