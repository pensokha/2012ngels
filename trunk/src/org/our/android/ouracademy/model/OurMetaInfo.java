package org.our.android.ouracademy.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class OurMetaInfo implements OurJSONModel {
	public static final int RES_CODE_SUCCESS = 0;
	public static final int RES_CODE_DONT_NEED_UPDATE = -100;
	
	private static final String VERSION_JSON_KEY = "Version";
	private static final String RESPONSE_JSON_KEY = "ResponseCode";
	private static final String CONTENTS_JSON_KEY = "Contents";
	private static final String CATEGORIES_JSON_KEY = "Categories";

	private ArrayList<OurContent> contents;
	private ArrayList<OurCategory> categories;
	private int responseCode;
	private int version;

	public ArrayList<OurContent> getContents() {
		return contents;
	}

	public void setContents(ArrayList<OurContent> contents) {
		this.contents = contents;
	}

	public ArrayList<OurCategory> getCategories() {
		return categories;
	}

	public void setCategories(ArrayList<OurCategory> categories) {
		this.categories = categories;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setErrorCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public JSONObject getJSONObject() throws JSONException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFromJSONObject(JSONObject json) throws JSONException {
		responseCode = json.getInt(RESPONSE_JSON_KEY);
		if (json.has(VERSION_JSON_KEY)) {
			version = json.getInt(VERSION_JSON_KEY);
		}
		if (json.has(CATEGORIES_JSON_KEY)) {
			categories = new ArrayList<OurCategory>();
			JSONArray jsonCategories = json.getJSONArray(CATEGORIES_JSON_KEY);
			for (int i = 0; i < jsonCategories.length(); i++) {
				OurCategory category = new OurCategory();
				category.setFromJSONObject(jsonCategories.getJSONObject(i));
				categories.add(category);
			}
		}
		if (json.has(CONTENTS_JSON_KEY)) {
			contents = new ArrayList<OurContent>();
			JSONArray jsonContents = json.getJSONArray(CONTENTS_JSON_KEY);
			for (int i = 0; i < jsonContents.length(); i++) {
				OurContent content = new OurContent();
				content.setFromJSONObject(jsonContents.getJSONObject(i));
				contents.add(content);
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OurMetaInfo [contents=");
		builder.append(contents);
		builder.append(", categories=");
		builder.append(categories);
		builder.append(", responseCode=");
		builder.append(responseCode);
		builder.append(", version=");
		builder.append(version);
		builder.append("]");
		return builder.toString();
	}
}
