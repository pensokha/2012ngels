package org.our.android.ouracademy.model;

import org.json.JSONException;
import org.json.JSONObject;

public class OurResponse implements OurJSONModel {
	public static final int RES_CODE_SUCCESS = 0;
	public static final int RES_CODE_DONT_NEED_UPDATE = -100;
	public static final int RES_CODE_INVALID_REQUEST = -200;
	public static final int RES_CODE_GET_DATA_FAIL = -300;
	
	public static final String RESPONSE_JSON_KEY = "ResponseCode";
	
	private int responseCode;
	
	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	
	@Override
	public JSONObject getJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put(RESPONSE_JSON_KEY, responseCode);
		
		return jsonObject;
	}

	@Override
	public void setFromJSONObject(JSONObject json) throws JSONException {
		responseCode = json.getInt(RESPONSE_JSON_KEY);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OurResponse [responseCode=");
		builder.append(responseCode);
		builder.append("]");
		return builder.toString();
	}
}
