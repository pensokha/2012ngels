package org.our.android.ouracademy.model;

public class OurContentCategory {
	private String contentId;
	private String categoryId;
	public String getContentId() {
		return contentId;
	}
	public void setContentId(String contentId) {
		this.contentId = contentId;
	}
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OurContentCategory [contentId=");
		builder.append(contentId);
		builder.append(", categoryId=");
		builder.append(categoryId);
		builder.append("]");
		return builder.toString();
	}
}
