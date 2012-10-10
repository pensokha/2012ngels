package org.our.android.ouracademy.manager;

import java.io.File;

import android.os.Environment;

/**
 * 
 * @author jyeon
 *
 */
public class FileManager {
	public static final String TAG = "FileManager";
	public static final String STRSAVEPATH = Environment.getExternalStorageDirectory() + "/OurAcademy/";

	/**
	 * get Files from Directory
	 * 
	 * @param file
	 * @return
	 */
	public static String[] getList() {
		File dir = new File(STRSAVEPATH);
		if (dir != null && dir.exists()) {
			return dir.list();
		}
		return null;
	}

	/**
	 * get File size
	 * 
	 * @param path
	 * @return
	 */
	public static long geFileSize(String path) {
		File file = new File(path);
		if (file != null && file.exists()) {
			return file.length();
		}
		return -1;
	}
}
