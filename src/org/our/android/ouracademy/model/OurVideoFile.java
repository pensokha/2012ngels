package org.our.android.ouracademy.model;

public class OurVideoFile {
	private String name;
	private String contentId;
	private long size;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContentId() {
		return contentId;
	}
	public void setContentId(String contentId) {
		this.contentId = contentId;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OurVideoFile [name=");
		builder.append(name);
		builder.append(", contentId=");
		builder.append(contentId);
		builder.append(", size=");
		builder.append(size);
		builder.append("]");
		return builder.toString();
	}
}
