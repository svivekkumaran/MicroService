package com.huskies.model;

public class WidgetModel {

	private String env;
	private String mediaNm;
	private String gmsServiceNm;
	private String stargate;
	public String getEnv() {
		return env;
	}
	public void setEnv(String env) {
		this.env = env;
	}
	public String getMediaNm() {
		return mediaNm;
	}
	public void setMediaNm(String mediaNm) {
		this.mediaNm = mediaNm;
	}

	public String getGmsServiceNm() {
		return gmsServiceNm;
	}
	public void setGmsServiceNm(String gmsServiceNm) {
		this.gmsServiceNm = gmsServiceNm;
	}
	public String getStargate() {
		return stargate;
	}
	public void setStargate(String stargate) {
		this.stargate = stargate;
	}
	@Override
	public String toString() {
		return "WidgetModel [env=" + env + ", mediaNm=" + mediaNm + ", GmsServiceNm=" + gmsServiceNm + ", Stargate="
				+ stargate + "]";
	}
	
}
