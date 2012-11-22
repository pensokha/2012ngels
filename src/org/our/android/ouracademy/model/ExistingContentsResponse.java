package org.our.android.ouracademy.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ExistingContentsResponse extends OurResponse {
	public static final String EXISTING_CONTENTS_JSON_KEY = "existing_contents";
	
	private ArrayList<OurContents> contents;

	public ArrayList<OurContents> getContents() {
		return contents;
	}

	public void setContents(ArrayList<OurContents> contents) {
		this.contents = contents;
	}

	@Override
	public JSONObject getJSONObject() throws JSONException {
		JSONObject jsonObject = super.getJSONObject();
		
		if(contents != null){
			JSONArray jsonArray = new JSONArray();
			for(OurContents content : contents){
				jsonArray.put(content.getJSONObject());
			}
			jsonObject.put(EXISTING_CONTENTS_JSON_KEY, jsonArray);
		}
		
		return jsonObject;
	}

	@Override
	public void setFromJSONObject(JSONObject json) throws JSONException {
		super.setFromJSONObject(json);
		
		if(json.has(EXISTING_CONTENTS_JSON_KEY)){
			if(contents == null){
				contents = new ArrayList<OurContents>();
			}else{
				contents.clear();
			}
			OurContents content;
			JSONArray jsonArray = json.getJSONArray(EXISTING_CONTENTS_JSON_KEY);
			for(int i = 0; i < jsonArray.length(); i++){
				content = new OurContents();
				content.setFromJSONObject(jsonArray.getJSONObject(i));
				
				contents.add(content);
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(super.toString());
		builder.append("ExistingContentsResponse [contents=");
		builder.append(contents);
		builder.append("]");
		return builder.toString();
	}
}
