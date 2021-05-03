/**
 * @author svanthen
 * Organization : CodeDepot, Optum
 * Created : Aug 18, 2020
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

@JsonIgnoreProperties(ignoreUnknown = true)
public class InboundMsgFromSmsMgrEntity {
	protected static final Logger Log = LoggerFactory.getLogger(InboundMsgFromSmsMgrEntity.class);
	/***
	 * This is how request comes from SMS MGR
	 * 
	 * {"_inbound_message_id":"2875724",
	 * "_inbound_message_body":"Hi from sudeer at 6:19pm est",
	 * "_inbound_message_to":"+19522347414",
	 * "_inbound_message_from":"+18134597263",
	 * "_inbound_message_program_id":"498309",
	 * "_inbound_message_phone_id":"2101192",
	 * "_inbound_message_full_name":"Sudeer Vpalli",
	 * "ENT_Unit":"OPT",
	 * "ENT_Segment":"OptumTech",
	 * "ENT_Function":"EUTS"
	 *optional
	 * ENT_TargetOverrideFlag (Yes / No)
	*  ENT_HOOPOverrideFlag (Yes / No)
	 * 
	 * }
	 * 
	 * 
	 */

	public String get_inbound_message_id() {
		return _inbound_message_id;
	}

	@JsonProperty("_inbound_message_id")
	public void set_inbound_message_id(String _inbound_message_id) {
		this._inbound_message_id = _inbound_message_id;
	}


	public String get_inbound_message_body() {
		return _inbound_message_body;
	}

	@JsonProperty("_inbound_message_body")
	public void set_inbound_message_body(String _inbound_message_body) {
		this._inbound_message_body = _inbound_message_body;
	}


	public String get_inbound_message_to() {
		return _inbound_message_to;
	}

	@JsonProperty("_inbound_message_to")
	public void set_inbound_message_to(String _inbound_message_to) {
		this._inbound_message_to = _inbound_message_to;
	}


	public String get_inbound_message_from() {
		return _inbound_message_from;
	}

	@JsonProperty("_inbound_message_from")
	public void set_inbound_message_from(String _inbound_message_from) {
		this._inbound_message_from = _inbound_message_from;
	}


	public String get_inbound_message_program_id() {
		return _inbound_message_program_id;
	}

	@JsonProperty("_inbound_message_program_id")
	public void set_inbound_message_program_id(String _inbound_message_program_id) {
		this._inbound_message_program_id = _inbound_message_program_id;
	}


	public String get_inbound_message_phone_id() {
		return _inbound_message_phone_id;
	}

	@JsonProperty("_inbound_message_phone_id")
	public void set_inbound_message_phone_id(String _inbound_message_phone_id) {
		this._inbound_message_phone_id = _inbound_message_phone_id;
	}


	public String get_inbound_message_full_name() {
		return _inbound_message_full_name;
	}

	@JsonProperty("_inbound_message_full_name")
	public void set_inbound_message_full_name(String _inbound_message_full_name) {
		this._inbound_message_full_name = _inbound_message_full_name;
	}


	public String getENT_Unit() {
		return ENT_Unit;
	}

	@JsonProperty("ENT_Unit")
	public void setENT_Unit(String eNT_Unit) {
		this.ENT_Unit = eNT_Unit;
	}


	public String getENT_Segment() {
		return ENT_Segment;
	}

	@JsonProperty("ENT_Segment")
	public void setENT_Segment(String eNT_Segment) {
		this.ENT_Segment = eNT_Segment;
	}


	public String getENT_Function() {
		return ENT_Function;
	}

	@JsonProperty("ENT_Function")
	public void setENT_Function(String eNT_Function) {
		this.ENT_Function = eNT_Function;
	}

	
	@JsonInclude(Include.NON_NULL)
	private String _inbound_message_id;

	@JsonInclude(Include.NON_NULL)
	private String _inbound_message_body;
	
	@JsonInclude(Include.NON_NULL)
	private String _inbound_message_to;
	
	@JsonInclude(Include.NON_NULL)
	private String _inbound_message_from;
	
	@JsonInclude(Include.NON_NULL)
	private String _inbound_message_program_id;
	
	@JsonInclude(Include.NON_NULL)
	private String _inbound_message_phone_id;
	
	@JsonInclude(Include.NON_NULL)
	private String _inbound_message_full_name;
	
	@JsonInclude(Include.NON_NULL)
	private String ENT_Unit;
	
	@JsonInclude(Include.NON_NULL)
	private String ENT_Segment;
	
	@JsonInclude(Include.NON_NULL)
	private String ENT_Function;
	
	@JsonInclude(Include.NON_DEFAULT)
	private String ENT_TargetOverrideFlag;
	
	@JsonInclude(Include.NON_DEFAULT)
	private String ENT_HOOPOverrideFlag;

	public String getENT_TargetOverrideFlag() {
		return ENT_TargetOverrideFlag!=null ? ENT_TargetOverrideFlag : "";
	}

	@JsonProperty("ENT_TargetOverrideFlag")
	public void setENT_TargetOverrideFlag(String eNT_TargetOverrideFlag) {
		ENT_TargetOverrideFlag = eNT_TargetOverrideFlag;
	}

	public String getENT_HOOPOverrideFlag() {
			return ENT_HOOPOverrideFlag!=null ? ENT_HOOPOverrideFlag : "";
	}

	@JsonProperty("ENT_HOOPOverrideFlag")
	public void setENT_HOOPOverrideFlag(String eNT_HOOPOverrideFlag) {
		ENT_HOOPOverrideFlag = eNT_HOOPOverrideFlag;
	}

	@Override
	public String toString() {		
		try {
			return JsonHelper.objectToJson(this);
		} catch(Exception e) {
			return "[ _inbound_message_id=" + _inbound_message_id 
					+ ",_inbound_message_body="+_inbound_message_body
					+ ",_inbound_message_to="+_inbound_message_to
				+ ", _inbound_message_from="+_inbound_message_from
				+ ", _inbound_message_program_id="+_inbound_message_program_id
						+ ",_inbound_message_phone_id="+_inbound_message_phone_id
						+ ",_inbound_message_full_name="+_inbound_message_full_name
					+ ", ENT_Unit="+ENT_Unit
					+ ", ENT_Segment="+ENT_Segment
					+ ", ENT_Function="+ENT_Function
					+ ", ENT_HOOPOverrideFlag="+ENT_HOOPOverrideFlag					
					+ ", ENT_TargetOverrideFlag="+ENT_TargetOverrideFlag
					
				+ " ]";
		}
	}
	
}
