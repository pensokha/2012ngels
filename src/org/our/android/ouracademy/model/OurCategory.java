package org.our.android.ouracademy.model;

import org.json.JSONException;
import org.json.JSONObject;

public class OurCategory implements OurJSONModel {
	public static final String CATEGORY_ID_JSON_KEY = "ID";
	public static final String CATEGORY_DEPTH_JSON_KEY = "Depth";
	public static final String CATEGORY_TITLE_ENG_JSON_KEY = "TitleEnglish";
	public static final String CATEGORY_TITLE_KMR_JSON_KEY = "TitleKhmer";
	public static final String CATEGORY_DESCRIPTION_ENG_JSON_KEY = "DescriptionEnglish";
	public static final String CATEGORY_DESCRIPTION_KMR_JSON_KEY = "DescriptionKhmer";
	public static final String CATEGORY_PARENT_ID_JSON_KEY = "ParentCategoryID";
	
	private String categoryId;
	private int categoryDepth;
	private String categoryTitleEng;
	private String categoryTitleKmr;
	private String categoryDescriptionEng;
	private String categoryDescriptionKmr;
	private String categoryParent;

	public boolean isChecked = false;
	
	public OurCategory() {
		
	}
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

	public int getCategoryDepth() {
		return categoryDepth;
	}

	public void setCategoryDepth(int categoryDepth) {
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
		categoryId = json.getString(CATEGORY_ID_JSON_KEY);
		categoryDepth = json.getInt(CATEGORY_DEPTH_JSON_KEY);
		categoryTitleEng = json.getString(CATEGORY_TITLE_ENG_JSON_KEY);
		categoryTitleKmr = json.getString(CATEGORY_TITLE_KMR_JSON_KEY);
		categoryDescriptionEng = json.getString(CATEGORY_DESCRIPTION_ENG_JSON_KEY);
		categoryDescriptionKmr = json.getString(CATEGORY_DESCRIPTION_KMR_JSON_KEY);
		categoryParent = json.getString(CATEGORY_PARENT_ID_JSON_KEY);
	}
}
