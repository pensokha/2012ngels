package org.our.android.ouracademy.constants;

import java.io.File;

import android.os.Environment;


public class CommonConstants {
	public static final int[] P2P_SERVER_PORT = { 7777, 7778, 7779, 7780, 7781 };
	
	public static final String CONTENTS_FOLDER_NAME = "OurAcademy";
	
	public static final String VIDEO_FORMAT_MP4 = "mp4";
	public static final String VIDEO_RESOLUTION_MIDIUM = "medium";
	
	public static final int SOCKET_BUFFER_SIZE = 512 * 1024;
	
	public static final int DETAIL_ANI_START_X = 0;
	public static final int DETAIL_ANI_END_X = 513;
	public static final int DETAIL_ANI_WIDTH = DETAIL_ANI_END_X - DETAIL_ANI_START_X;
	
	public static final String LOCALE_LANGUAGE_ENG = "en";
	public static final String LOCALE_LANGUAGE_KHMER = "km";
	public static final String KHMER_FONT_FILE = "Khmer.ttf";
	public static final String KHMER_FONT_BOLD_FILE = "Khmerb.ttf";
	
	public static final String META_FILE_NAME = "our.txt";
	
	public static final String DEFAULT_LOAD_CATEGORY_ID = MatchCategoryIcon.categoryIds[0];
	
	public static final String getContentFilePath(String fileName) {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + 
		File.separator + CONTENTS_FOLDER_NAME + File.separator + fileName;
		return path;
	}
	
	public static final String getContentImagePath(String fileName) {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + 
		File.separator + CONTENTS_FOLDER_NAME + File.separator + "Images" + File.separator + 
		fileName;
		return path;
	}
	
	/**
	 * @author Sung-Chul Park
	 * @param fileName
	 * @return mp4 확장자가 붙은 파일명.
	 */
	public static final String getContentFilePathPlusMP4(String fileName) {
		StringBuilder path = new StringBuilder();
	
		path.append(Environment.getExternalStorageDirectory().getAbsolutePath());
		path.append(File.separator);
		path.append(CONTENTS_FOLDER_NAME);
		path.append(File.separator);
		path.append(fileName);
		path.append(".mp4");
		
		return path.toString();
	}
	
	public static final int MAX_CONNECTION = 7;
}
