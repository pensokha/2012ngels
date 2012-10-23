package org.our.android.ouracademy.model;

import org.json.JSONException;
import org.json.JSONObject;

public class OurContent implements OurJSONModel {
	public static final String ID_JSON_KEY = "id";
	public static final String SUBJECT_ENG_JSON_KEY = "subjectEng";
	public static final String SUBJECT_KMR_JSON_KEY = "subjectKmr";
	public static final String CATEGIRY_ID_JSON_KEY = "categoryId";
	public static final String CATEGIRY_ENG_JSON_KEY = "categoryEng";
	public static final String CATEGIRY_KRM_JSON_KEY = "categoryKmr";
	public static final String SUBCATEGIRY_ID_JSON_KEY = "subCategoryId";
	public static final String SUBCATEGIRY_ENG_JSON_KEY = "subCategoryEng";
	public static final String SUBCATEGIRY_KRM_JSON_KEY = "subCategoryKmr";
	public static final String DESCRIPTION_ENG_JSON_KEY = "descriptionEng";
	public static final String DESCRIPTION_KMR_JSON_KEY = "descriptionKmr";
	public static final String CONTENT_URL_JSON_KEY = "contentUrl";
	public static final String SUBTITLE_URL_JSON_KEY = "subtitleUrl";
	
	private String id;
	private String subjectEng;
	private String subjectKmr;
	private String categoryId;
	private String categoryEng;
	private String categoryKmr;
	private String subCategoryId;
	private String subCategoryEng;
	private String subCategoryKmr;
	private String descriptionEng;
	private String descriptionKmr;
	private String contentUrl;
	private String subtitleUrl;
	private long size;
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

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryEng() {
		return categoryEng;
	}

	public void setCategoryEng(String categoryEng) {
		this.categoryEng = categoryEng;
	}

	public String getCategoryKmr() {
		return categoryKmr;
	}

	public void setCategoryKmr(String categoryKmr) {
		this.categoryKmr = categoryKmr;
	}

	public String getSubCategoryId() {
		return subCategoryId;
	}

	public void setSubCategoryId(String subCategoryId) {
		this.subCategoryId = subCategoryId;
	}

	public String getSubCategoryEng() {
		return subCategoryEng;
	}

	public void setSubCategoryEng(String subCategoryEng) {
		this.subCategoryEng = subCategoryEng;
	}

	public String getSubCategoryKmr() {
		return subCategoryKmr;
	}

	public void setSubCategoryKmr(String subCategoryKmr) {
		this.subCategoryKmr = subCategoryKmr;
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

	public String getContentUrl() {
		return contentUrl;
	}

	public void setContentUrl(String contentUrl) {
		this.contentUrl = contentUrl;
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

	public OurContent(String id, String subjectEng, String subjectKmr, String categoryId,
			String categoryEng, String categoryKmr, String subCategoryId, String subCategoryEng,
			String subCategoryKmr, String descriptionEng, String descriptionKmr, String contentUrl,
			String subtitleUrl, long size, boolean isDownloaded) {
		this.id = id;            
		this.subjectEng = subjectEng;    
		this.subjectKmr = subjectKmr;    
		this.categoryId = categoryId;    
		this.categoryEng = categoryEng;   
		this.categoryKmr = categoryKmr;   
		this.subCategoryId = subCategoryId; 
		this.subCategoryEng = subCategoryEng;
		this.subCategoryKmr = subCategoryKmr;
		this.descriptionEng = descriptionEng;
		this.descriptionKmr = descriptionKmr;
		this.contentUrl = contentUrl;    
		this.subtitleUrl = subtitleUrl; 
		this.size = size;
		this.isDownloaded = isDownloaded; 
	}
	
	@Override
	public String toString() {
		return "OurContent [id=" + id + ", subjectEng=" + subjectEng + ", subjectKmr="
				+ subjectKmr + ", categoryId=" + categoryId + ", categoryEng=" + categoryEng + 
				", categoryKmr=" + categoryKmr +  ", subCategoryId=" + subCategoryId +
				", subCategoryEng=" + subCategoryEng + ", subCategoryKmr=" + subCategoryKmr +
				", descriptionEng=" + descriptionEng + ", descriptionKmr=" + descriptionKmr + 
				", contentUrl=" + contentUrl + ", subtitleUrl=" + subtitleUrl + "]";
	}

	@Override
	public JSONObject getJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ID_JSON_KEY, id);                             
		jsonObject.put(SUBJECT_ENG_JSON_KEY, subjectEng);            
		jsonObject.put(SUBJECT_KMR_JSON_KEY, subjectKmr);            
		jsonObject.put(CATEGIRY_ID_JSON_KEY, categoryId);            
		jsonObject.put(CATEGIRY_ENG_JSON_KEY, categoryEng);          
		jsonObject.put(CATEGIRY_KRM_JSON_KEY, categoryKmr);          
		jsonObject.put(SUBCATEGIRY_ID_JSON_KEY, subCategoryId);      
		jsonObject.put(SUBCATEGIRY_ENG_JSON_KEY, subCategoryEng);    
		jsonObject.put(SUBCATEGIRY_KRM_JSON_KEY, subCategoryKmr);    
		jsonObject.put(DESCRIPTION_ENG_JSON_KEY, descriptionEng);    
		jsonObject.put(DESCRIPTION_KMR_JSON_KEY, descriptionKmr);    
		jsonObject.put(CONTENT_URL_JSON_KEY, contentUrl);            
		jsonObject.put(SUBTITLE_URL_JSON_KEY, subtitleUrl);          
		
		return jsonObject;
	}

	@Override
	public void setFromJSONObject(JSONObject json) throws JSONException {
		id = json.getString(ID_JSON_KEY);            
		subjectEng = json.getString(SUBJECT_ENG_JSON_KEY);    
		subjectKmr = json.getString(SUBJECT_KMR_JSON_KEY);    
		categoryId = json.getString(CATEGIRY_ID_JSON_KEY);    
		categoryEng = json.getString(CATEGIRY_ENG_JSON_KEY);   
		categoryKmr = json.getString(CATEGIRY_KRM_JSON_KEY);   
		subCategoryId = json.getString(SUBCATEGIRY_ID_JSON_KEY); 
		subCategoryEng = json.getString(SUBCATEGIRY_ENG_JSON_KEY);
		subCategoryKmr = json.getString(SUBCATEGIRY_KRM_JSON_KEY);
		descriptionEng = json.getString(DESCRIPTION_ENG_JSON_KEY);
		descriptionKmr = json.getString(DESCRIPTION_KMR_JSON_KEY);
		contentUrl = json.getString(CONTENT_URL_JSON_KEY);    
		subtitleUrl = json.getString(SUBTITLE_URL_JSON_KEY);   
	}
}
