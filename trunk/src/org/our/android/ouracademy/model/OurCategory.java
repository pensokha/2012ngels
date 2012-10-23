package org.our.android.ouracademy.model;

import org.json.JSONException;
import org.json.JSONObject;

public class OurCategory implements OurJSONModel {
	public static final String CATEGORY_ID_JSON_KEY = "categoryId";
	public static final String CATEGORY_DEPTH_JSON_KEY = "categoryDepth";
	public static final String CATEGORY_TITLE_ENG_JSON_KEY = "categoryTitleEng";
	public static final String CATEGORY_TITLE_KMR_JSON_KEY = "categoryTitleKmr";
	public static final String CATEGORY_DESCRIPTION_ENG_JSON_KEY = "categoryDescriptionEng";
	public static final String CATEGORY_DESCRIPTION_KMR_JSON_KEY = "categoryDescriptionKmr";
	public static final String CATEGORY_PARENT_ID_JSON_KEY = "categoryParent";
	
	private String categoryId;
	private String categoryDepth;
	private String categoryTitleEng;
	private String categoryTitleKmr;
	private String categoryDescriptionEng;
	private String categoryDescriptionKmr;
	private String categoryParent;
	
	public OurCategory(String categoryId, String categoryDepth ,String categoryTitleEng ,String categoryTitleKmr, 
			String categoryDescriptionEng, String categoryDescriptionKmr, String categoryParent) {
		this.categoryId = categoryId;
		this.categoryDepth = categoryDepth;
		this.categoryTitleEng = categoryTitleEng;
		this.categoryTitleKmr = categoryTitleKmr;
		this.categoryDescriptionEng = categoryDescriptionEng;
		this.categoryDescriptionKmr = categoryDescriptionKmr;
		this.categoryParent = categoryParent;
	}
	
	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryDepth() {
		return categoryDepth;
	}

	public void setCategoryDepth(String categoryDepth) {
		this.categoryDepth = categoryDepth;
	}

	public String getCategoryTitleEng() {
		return categoryTitleEng;
	}

	public void setCategoryTitleEng(String categoryTitleEng) {
		this.categoryTitleEng = categoryTitleEng;
	}

	public String getCategoryTitleKmr() {
		return categoryTitleKmr;
	}

	public void setCategoryTitleKmr(String categoryTitleKmr) {
		this.categoryTitleKmr = categoryTitleKmr;
	}

	public String getCategoryDescriptionEng() {
		return categoryDescriptionEng;
	}

	public void setCategoryDescriptionEng(String categoryDescriptionEng) {
		this.categoryDescriptionEng = categoryDescriptionEng;
	}

	public String getCategoryDescriptionKmr() {
		return categoryDescriptionKmr;
	}

	public void setCategoryDescriptionKmr(String categoryDescriptionKmr) {
		this.categoryDescriptionKmr = categoryDescriptionKmr;
	}

	public String getCategoryParent() {
		return categoryParent;
	}

	public void setCategoryParent(String categoryParent) {
		this.categoryParent = categoryParent;
	}

	@Override
	public JSONObject getJSONObject() throws JSONException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFromJSONObject(JSONObject json) throws JSONException {
		// TODO Auto-generated method stub
	}

}
