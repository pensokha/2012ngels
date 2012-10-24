package org.our.android.ouracademy.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OurContent implements OurJSONModel {
	public static final String CONTENTS_ID_JSON_KEY = "ID";
	public static final String SUBJECT_ENG_JSON_KEY = "subjectEnglish";
	public static final String SUBJECT_KMR_JSON_KEY = "subjectKhmer";
	public static final String CONTENT_URL_JSON_KEY = "ContentUrl";
	public static final String SUBTITLE_URL_JSON_KEY = "SubTitleFileUrl";
	public static final String SIZE_JSON_KEY = "size";
	public static final String CATEGORY_ID_LIST_JSON_KEY = "CategoryIDList";

	private String id;
	private String subjectEng;
	private String subjectKmr;
	private String contentUrl;
	private String subtitleUrl;
	private long size;
	private ArrayList<String> categoryIdList;
	private long downloadedSize;
	public boolean isDownloaded;

	public OurContent() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSubjectEng() {
		return subjectEng;
	}

	public void setSubjectEng(String subjectEng) {
		this.subjectEng = subjectEng;
	}

	public String getSubjectKmr() {
		return subjectKmr;
	}

	public void setSubjectKmr(String subjectKmr) {
		this.subjectKmr = subjectKmr;
	}

	public String getContentUrl() {
		return contentUrl;
	}

	public void setContentUrl(String contentsUrl) {
		this.contentUrl = contentsUrl;
	}

	public String getSubtitleUrl() {
		return subtitleUrl;
	}

	public void setSubtitleUrl(String subtitleUrl) {
		this.subtitleUrl = subtitleUrl;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public boolean isDownloaded() {
		return isDownloaded;
	}

	public void setDownloaded(boolean isDownloaded) {
		this.isDownloaded = isDownloaded;
	}

	public ArrayList<String> getCategoryIdList() {
		return categoryIdList;
	}

	public void setCategoryIdList(ArrayList<String> categoryIdList) {
		this.categoryIdList = categoryIdList;
	}

	public long getDownloadedSize() {
		return downloadedSize;
	}

	public void setDownloadedSize(long downloadedSize) {
		this.downloadedSize = downloadedSize;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OurContent [id=");
		builder.append(id);
		builder.append(", subjectEng=");
		builder.append(subjectEng);
		builder.append(", subjectKmr=");
		builder.append(subjectKmr);
		builder.append(", contentsUrl=");
		builder.append(contentUrl);
		builder.append(", subtitleUrl=");
		builder.append(subtitleUrl);
		builder.append(", size=");
		builder.append(size);
		builder.append(", categoryIdList=");
		builder.append(categoryIdList);
		builder.append(", downloadedSize=");
		builder.append(downloadedSize);
		builder.append(", isDownloaded=");
		builder.append(isDownloaded);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public JSONObject getJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();

		return jsonObject;
	}

	@Override
	public void setFromJSONObject(JSONObject json) throws JSONException {
		id = json.getString(CONTENTS_ID_JSON_KEY);
		subjectEng = json.getString(SUBJECT_ENG_JSON_KEY);
		subjectKmr = json.getString(SUBJECT_KMR_JSON_KEY);
		contentUrl = json.getString(CONTENT_URL_JSON_KEY);
		subtitleUrl = json.getString(SUBTITLE_URL_JSON_KEY);
		size = json.getLong(SIZE_JSON_KEY);

		categoryIdList = new ArrayList<String>();
		JSONArray jsonCategoryIdList = json
				.getJSONArray(CATEGORY_ID_LIST_JSON_KEY);
		for (int i = 0; i < jsonCategoryIdList.length(); i++) {
			categoryIdList.add(jsonCategoryIdList.getString(i));
		}
	}
}
