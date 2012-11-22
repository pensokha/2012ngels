package org.our.android.ouracademy.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OurMetaInfo extends OurResponse {
	public static final String VERSION_JSON_KEY = "Version";
	public static final String CONTENTS_JSON_KEY = "Contents";
	public static final String CATEGORIES_JSON_KEY = "Categories";

	private ArrayList<OurContents> contents;
	private ArrayList<OurCategory> categories;
	private int version;

	public ArrayList<OurContents> getContents() {
		return contents;
	}

	public void setContents(ArrayList<OurContents> contents) {
		this.contents = contents;
	}

	public ArrayList<OurCategory> getCategories() {
		return categories;
	}

	public void setCategories(ArrayList<OurCategory> categories) {
		this.categories = categories;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public JSONObject getJSONObject() throws JSONException {
		JSONObject jsonObject = super.getJSONObject();

		if (getResponseCode() == OurResponse.RES_CODE_SUCCESS) {
			jsonObject.put(VERSION_JSON_KEY, version);
			
			JSONArray jsonCatogories = new JSONArray();
			JSONArray jsonContents = new JSONArray();
			
			for(OurCategory category : categories){
				jsonCatogories.put(category.getJSONObject());
			}
			
			for(OurContents content : contents){
				jsonContents.put(content.getJSONObject());
			}
			
			jsonObject.put(CATEGORIES_JSON_KEY, jsonCatogories);
			jsonObject.put(CONTENTS_JSON_KEY, jsonContents);
		}

		return jsonObject;
	}

	@Override
	public void setFromJSONObject(JSONObject json) throws JSONException {
		super.setFromJSONObject(json);
		
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
			contents = new ArrayList<OurContents>();
			JSONArray jsonContents = json.getJSONArray(CONTENTS_JSON_KEY);
			for (int i = 0; i < jsonContents.length(); i++) {
				OurContents content = new OurContents();
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
		builder.append(", version=");
		builder.append(version);
		builder.append("]");
		return builder.toString();
	}
}
