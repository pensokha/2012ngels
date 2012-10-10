package org.our.android.ouracademy.model;

import org.json.JSONException;
import org.json.JSONObject;

public interface OurJSONModel {
	public JSONObject getJSONObject() throws JSONException;
	public void setFromJSONObject(JSONObject json) throws JSONException;
}

