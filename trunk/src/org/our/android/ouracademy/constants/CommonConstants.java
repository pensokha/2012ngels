package org.our.android.ouracademy.constants;

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
	
	public static final String LOCALE_LANGUAGE_KHMER = "";
	
	public static final String META_FILE_NAME = "our.txt";
	
	public static final String getContentFilePath(String fileName) {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + 
		"/" + CONTENTS_FOLDER_NAME + "/" + fileName;
		return path;
	}
}
