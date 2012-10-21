package org.our.android.ouracademy.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;
import android.util.Log;

/**
 * 
 * @author jyeon
 * 
 */
public class FileManager {
	public static final String TAG = "FileManager";
	public static final String STRSAVEPATH = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/OurAcademy/";

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

	public static File getFile(String fileName) throws FileNotFoundException {
		File file = new File(STRSAVEPATH + fileName);

		File dirs = new File(file.getParent());
		if (!dirs.exists())
			dirs.mkdirs();
		return file;
	}

	public static boolean copyFile(InputStream inputStream, OutputStream out) {
		byte buf[] = new byte[1024 * 1024];
		int len;
		try {
			Log.d(TAG, "File Download");
			while ((len = inputStream.read(buf)) != -1) {
				out.write(buf, 0, len);
			}
			out.close();
			inputStream.close();
		} catch (IOException e) {
			return false;
		}
		return true;
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
