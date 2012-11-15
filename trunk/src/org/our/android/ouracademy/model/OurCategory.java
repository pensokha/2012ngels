package org.our.android.ouracademy.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.our.android.ouracademy.OurApplication;
import org.our.android.ouracademy.constants.CommonConstants;

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
	private int numOfContents = 0;

	public int getNumOfContents() {
		return numOfContents;
	}

	public void setNumOfContents(int numOfContents) {
		this.numOfContents = numOfContents;
	}

	public boolean isChecked = false;

	public OurCategory() {

	}

	public OurCategory(String categoryId, int categoryDepth,
			String categoryTitleEng, String categoryTitleKmr,
			String categoryDescriptionEng, String categoryDescriptionKmr,
			String categoryParent) {
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
	
	public String getCategoryTitle() {
		if (CommonConstants.LOCALE_LANGUAGE_KHMER.equals(OurApplication.getInstance().getLocaleLangueage())) {
			return categoryTitleKmr;
		}
		return categoryTitleEng;
	}
	
	public String getCategoryDescription() {
		if (CommonConstants.LOCALE_LANGUAGE_KHMER.equals(OurApplication.getInstance().getLocaleLangueage())) {
			return categoryDescriptionKmr;
		}
		return categoryDescriptionEng;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OurCategory [categoryId=");
		builder.append(categoryId);
		builder.append(", categoryDepth=");
		builder.append(categoryDepth);
		builder.append(", categoryTitleEng=");
		builder.append(categoryTitleEng);
		builder.append(", categoryTitleKmr=");
		builder.append(categoryTitleKmr);
		builder.append(", categoryDescriptionEng=");
		builder.append(categoryDescriptionEng);
		builder.append(", categoryDescriptionKmr=");
		builder.append(categoryDescriptionKmr);
		builder.append(", categoryParent=");
		builder.append(categoryParent);
		builder.append(", isChecked=");
		builder.append(isChecked);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public JSONObject getJSONObject() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(CATEGORY_ID_JSON_KEY, categoryId);
		json.put(CATEGORY_DEPTH_JSON_KEY, categoryDepth);
		json.put(CATEGORY_TITLE_ENG_JSON_KEY, categoryTitleEng);
		json.put(CATEGORY_TITLE_KMR_JSON_KEY, categoryTitleKmr);
		json.put(CATEGORY_DESCRIPTION_ENG_JSON_KEY, categoryDescriptionEng);
		json.put(CATEGORY_DESCRIPTION_KMR_JSON_KEY, categoryDescriptionKmr);
		json.put(CATEGORY_PARENT_ID_JSON_KEY, categoryParent);
		
		return json;
	}

	@Override
	public void setFromJSONObject(JSONObject json) throws JSONException {
		categoryId = json.getString(CATEGORY_ID_JSON_KEY);
		categoryDepth = json.getInt(CATEGORY_DEPTH_JSON_KEY);
		categoryTitleEng = json.getString(CATEGORY_TITLE_ENG_JSON_KEY);
		categoryTitleKmr = json.getString(CATEGORY_TITLE_KMR_JSON_KEY);
		categoryDescriptionEng = json
				.getString(CATEGORY_DESCRIPTION_ENG_JSON_KEY);
		categoryDescriptionKmr = json
				.getString(CATEGORY_DESCRIPTION_KMR_JSON_KEY);
		categoryParent = json.getString(CATEGORY_PARENT_ID_JSON_KEY);
	}
}
