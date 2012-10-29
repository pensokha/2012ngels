package org.our.android.ouracademy;

import android.os.Environment;


public class OurDefine {
	public static final int[] P2P_SERVER_PORT = { 7777, 7778, 7779, 7780, 7781 };
	
	public static final String CONTENTS_FOLDER_NAME = "OurAcademy";
	
	public static final String getContentFilePath(String fileName) {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + 
		"/" + CONTENTS_FOLDER_NAME + "/" + fileName;
		return path;
	}
}
