package org.our.android.ouracademy.manager;

import org.our.android.ouracademy.util.DbManager;
import org.our.android.ouracademy.util.DbRow;

import android.content.ContentValues;

/**
 * 
 * @author jyeon
 * 
 */
public class OurDownloadManager {

	public static final String Table_NAME = "DOWNLOAD_TBL";

	public static final String KEY_FILE_ID = "FileId";
	public static final String KEY_DOWNLOAD_FILE_SIZE = "DownladedFileSize";
	public static final String KEY_FILE_SIZE = "FileSize";
	public static final String KEY_FILE_NAME = "FileName";
	public static final String KEY_FILE_PATH = "FilePath";

	public OurDownloadManager() {
		// test code
		// DbManager.getInstance().dropTable(tableName);

		if (!DbManager.getInstance().isTableExist(Table_NAME)) {

			ContentValues value = new ContentValues();
			value.put(KEY_FILE_ID, "TEXT PRIMARY KEY");
			value.put(KEY_DOWNLOAD_FILE_SIZE, "LONG");
			value.put(KEY_FILE_SIZE, "LONG");
			value.put(KEY_FILE_NAME, "TEXT");
			value.put(KEY_FILE_PATH, "TEXT");

			DbManager.getInstance().createTable(Table_NAME, value);
		}

		// updateStorage();
	}

	public void updateStorage() {
		String[] list = FileManager.getList();
		StringBuilder sb = new StringBuilder();
		if (list != null) {
			for (int i = 0; i < list.length; i++) {
				String fileName = list[i];
				sb.setLength(0);
				sb.append(FileManager.STRSAVEPATH);
				sb.append(fileName);
				addRow(fileName, FileManager.getFileSize(sb.toString()), -1,
						fileName, sb.toString());
			}
		}
	}

	public void addRow(String id, long downladedFileSize, long fileSize,
			String fileName, String filePath) {
		DbRow dbRow = new DbRow();
		if (id != null) {
			dbRow.add(KEY_FILE_ID, id);
		}
		if (downladedFileSize != -1) {
			dbRow.add(KEY_DOWNLOAD_FILE_SIZE, Long.toString(downladedFileSize));
		}
		if (fileSize != -1) {
			dbRow.add(KEY_FILE_SIZE, Long.toString(fileSize));
		}
		if (fileName != null) {
			dbRow.add(KEY_FILE_NAME, fileName);
		}
		if (filePath != null) {
			dbRow.add(KEY_FILE_PATH, filePath);
		}
		DbManager.getInstance().insertOrReplace(Table_NAME, dbRow);
	}
}
