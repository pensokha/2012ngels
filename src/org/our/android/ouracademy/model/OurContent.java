package org.our.android.ouracademy.model;

import org.json.JSONException;
import org.json.JSONObject;

public class OurContent implements OurJSONModel {
	private String id;
	private String name;
	private String subject;
	private long size;
	private String category;
	
	public OurContent() {
		super();
	}

	public OurContent(String id, String name, String subject, long size,
			String category) {
		super();
		this.id = id;
		this.name = name;
		this.subject = subject;
		this.size = size;
		this.category = category;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	@Override
	public String toString() {
		return "OurContent [id=" + id + ", name=" + name + ", subject="
				+ subject + ", size=" + size + ", category=" + category + "]";
	}
	
	private static final String ID_JSON_KEY = "id";
	private static final String NAME_JSON_KEY = "name";
	private static final String SUBJECT_JSON_KEY = "subject";
	private static final String SIZE_JSON_KEY = "size";
	private static final String CATEGORY_JSON_KEY = "category";

	@Override
	public JSONObject getJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ID_JSON_KEY, id);
		jsonObject.put(NAME_JSON_KEY, name);
		jsonObject.put(SUBJECT_JSON_KEY, subject);
		jsonObject.put(SIZE_JSON_KEY, size);
		jsonObject.put(CATEGORY_JSON_KEY, category);
		
		return jsonObject;
	}

	@Override
	public void setFromJSONObject(JSONObject json) throws JSONException {
		id = json.getString(ID_JSON_KEY);
		name = json.getString(NAME_JSON_KEY);
		subject = json.getString(SUBJECT_JSON_KEY);
		size = json.getLong(SIZE_JSON_KEY);
		category = json.getString(CATEGORY_JSON_KEY);
	}
}
