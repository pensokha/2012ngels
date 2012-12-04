package org.our.android.ouracademy.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.our.android.ouracademy.OurApplication;
import org.our.android.ouracademy.constants.CommonConstants;

public class OurContents implements OurJSONModel {
	public static final String CONTENTS_ID_JSON_KEY = "ID";
	public static final String SUBJECT_ENG_JSON_KEY = "SubjectEnglish";
	public static final String SUBJECT_KMR_JSON_KEY = "SubjectKhmer";
	public static final String TOPIC_ID_JSON_KEY = "TopicID";
	public static final String TOPIC_TITLE_ENG_JSON_KEY = "TopicTitleEnglish";
	public static final String TOPIC_TITLE_KMR_JSON_KEY = "TopicTitleKhmer";
	public static final String CONTENTS_DESCRIPTION_ENG_JSON_KEY = "DescriptionEnglish";
	public static final String CONTENTS_DESCRIPTION_KMR_JSON_KEY = "DescriptionKhmer";
	public static final String CONTENT_URL_JSON_KEY = "ContentUrl";
	public static final String SUBTITLE_URL_JSON_KEY = "SubTitleFileUrl";
	public static final String CATEGORY_ID_LIST_JSON_KEY = "CategoryIDList";
	public static final String SIZE_JSON_KEY = "ContentFileSize";
	public static final String DOWNLOAD_POINT_JSON_KEY = "DownloadPoint";

	private String id;
	private String subjectEng;
	private String subjectKmr;
	private String contentUrl;
	private String subtitleUrl;
	private long size;
	private ArrayList<String> categoryIdList;
	private long downloadedSize;

	private String topicId;
	private String topicTitleEng;
	private String topicTitleKmr;
	private String descriptionEng;
	private String descriptionKmr;

	private boolean isDeleteMode = false;
	private boolean isFullTextMode = false;

	public OurCategory selectedCategory;

	private long prevDownloadedSize = 0;

	public enum FileStatus {
		NONE,
		DOWNLOADING,
		DOWNLOADED
	}

	public FileStatus fileStatus = FileStatus.NONE;

	public OurContents() {
		super();
	}

	public void setDeleteMode(boolean isDeleteMode) {
		this.isDeleteMode = isDeleteMode;
	}

	public boolean isDeleteMode() {
		return this.isDeleteMode;
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

	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}

	public String getTopicTitleEng() {
		return topicTitleEng;
	}

	public void setTopicTitleEng(String topicTitleEng) {
		this.topicTitleEng = topicTitleEng;
	}

	public String getTopicTitleKmr() {
		return topicTitleKmr;
	}

	public void setTopicTitleKmr(String topicTitleKmr) {
		this.topicTitleKmr = topicTitleKmr;
	}

	public String getDescriptionEng() {
		return descriptionEng;
	}

	public void setDescriptionEng(String descriptionEng) {
		this.descriptionEng = descriptionEng;
	}

	public String getDescriptionKmr() {
		return descriptionKmr;
	}

	public void setDescriptionKmr(String descriptionKmr) {
		this.descriptionKmr = descriptionKmr;
	}

	public ArrayList<String> getCategoryIdList() {
		if (categoryIdList == null) {
			categoryIdList = new ArrayList<String>();
		}

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

	public void setDownloaded(boolean isDownloaded) {
		if (isDownloaded) {
			fileStatus = FileStatus.DOWNLOADED;
		} else {
			fileStatus = FileStatus.NONE;
		}
	}

	public long getPrevDownloadedSize() {
		return prevDownloadedSize;
	}

	public long getCurrentDownloadedSize(long downloadedSize) {
		long diff = downloadedSize - prevDownloadedSize;
		if (diff >= 0) {
			prevDownloadedSize = downloadedSize;
		}
		return diff;
	}

	public String getSubject() {
		if (CommonConstants.LOCALE_LANGUAGE_KHMER.equals(OurApplication.getInstance().getLocaleLangueage())) {
			return subjectKmr;
		}
		return subjectEng;
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

		builder.append(", topicId=");
		builder.append(topicId);
		builder.append(", topicTitleEng=");
		builder.append(topicTitleEng);
		builder.append(", topicTitleKmr=");
		builder.append(topicTitleKmr);
		builder.append(", descriptionEng=");
		builder.append(descriptionEng);
		builder.append(", descriptionKmr=");
		builder.append(descriptionKmr);

		builder.append(", size=");
		builder.append(size);
		builder.append(", categoryIdList=");
		builder.append(categoryIdList);
		builder.append(", downloadedSize=");
		builder.append(downloadedSize);
		builder.append(", isDownloaded=");
//		builder.append(isDownloaded);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public JSONObject getJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(CONTENTS_ID_JSON_KEY, id);
		jsonObject.put(SUBJECT_ENG_JSON_KEY, subjectEng);
		jsonObject.put(SUBJECT_KMR_JSON_KEY, subjectKmr);

		jsonObject.put(TOPIC_ID_JSON_KEY, topicId);
		jsonObject.put(TOPIC_TITLE_ENG_JSON_KEY, topicTitleEng);
		jsonObject.put(TOPIC_TITLE_KMR_JSON_KEY, topicTitleKmr);
		jsonObject.put(CONTENTS_DESCRIPTION_ENG_JSON_KEY, descriptionEng);
		jsonObject.put(CONTENTS_DESCRIPTION_KMR_JSON_KEY, descriptionKmr);

		jsonObject.put(CONTENT_URL_JSON_KEY, contentUrl);
		jsonObject.put(SUBTITLE_URL_JSON_KEY, subtitleUrl);
		jsonObject.put(SIZE_JSON_KEY, size);

		JSONArray jsonCategoryIdList = new JSONArray();
		if (categoryIdList != null) {
			for (String categoryId : categoryIdList) {
				jsonCategoryIdList.put(categoryId);
			}
		}
		jsonObject.put(CATEGORY_ID_LIST_JSON_KEY, jsonCategoryIdList);

		return jsonObject;
	}

	@Override
	public void setFromJSONObject(JSONObject json) throws JSONException {
		id = json.getString(CONTENTS_ID_JSON_KEY);
		subjectEng = json.getString(SUBJECT_ENG_JSON_KEY);
		subjectKmr = json.getString(SUBJECT_KMR_JSON_KEY);

		topicId = json.getString(TOPIC_ID_JSON_KEY);
		topicTitleEng = json.getString(TOPIC_TITLE_ENG_JSON_KEY);
		topicTitleKmr = json.getString(TOPIC_TITLE_KMR_JSON_KEY);
		descriptionEng = json.getString(CONTENTS_DESCRIPTION_ENG_JSON_KEY);
		descriptionKmr = json.getString(CONTENTS_DESCRIPTION_KMR_JSON_KEY);

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

	public void setFullTextMode(boolean isFullTextMode) {
		this.isFullTextMode = isFullTextMode;
	}

	public boolean isFullTextMode() {
		return isFullTextMode;
	}
}
