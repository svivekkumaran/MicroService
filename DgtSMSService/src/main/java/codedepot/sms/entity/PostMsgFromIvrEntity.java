/**
 * @author svanthen
 * Organization : CodeDepot, Optum
 * Created : Aug 12, 2020
 * 
 */
package codedepot.sms.entity;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.optum.omni.secure.msg.JsonHelper;

import codedepot.commons.JsonHelper;

/**
 * @author Sudeer V
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostMsgFromIvrEntity {
	
	protected static final Logger Log = LoggerFactory.getLogger(PostMsgFromIvrEntity.class);
	private static final ObjectMapper mapper = new ObjectMapper();

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
	private String _outbound_message_from;
	
	public String get_outbound_message_from() {
		return _outbound_message_from;
	}

	@JsonProperty("_outbound_message_from")
	public void set_outbound_message_from(String _outbound_message_from) {
		this._outbound_message_from = _outbound_message_from;
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
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {		
		try {
			return JsonHelper.objectToJson(this);
		} catch(Exception e) {
			return "[ _outbound_message_body=" + _outbound_message_body 
					+ ",_outbound_message_from="+_outbound_message_from
					+ ",_outbound_message_program_id="+_outbound_message_program_id
				+ ", _outbound_message_kvps=_outbound_message_kvps ]";
		}
	}

} // class