package org.our.android.ouracademy.manager;

import org.our.android.ouracademy.util.DbManager;
import org.our.android.ouracademy.util.DbRow;

import android.content.ContentValues;

public class TestFileDbCreate {
	public static final String CONTENTS_TABLE = "CONTENTS_TBL";

	public static final String KEY_CONTENT_ID = "ContentId";
	public static final String KEY_CONTENT_NAME = "ContentName";
	public static final String KEY_CONTENT_SUBJECT = "ContentSubject";
	public static final String KEY_CONTENT_SIZE = "ContentSize";
	public static final String KEY_CONTENT_CATEGORY = "ContentCategory";
	
	static final String[] contentId = {"Boa.flv", "EpikHigh.flv", "HOT.flv", "Secret.flv", "WonderGirls.flv"};
	static final String[] contentName = {"Boa", "EpikHigh", "HOT", "Secret", "WonderGirls"};
	static final String[] contentSubject = {"No1", "it's cold", "TTT", "love is move", "like this"};
	static final String[] contentSize = {"10", "22", "2352352", "235232", "242523"};
	static final String[] contentCategory = {"음악1", "음악2","음악3", "음악4", "음악5" };
	
	public TestFileDbCreate() {
		
	}
	
	static public boolean createContentsTable() {
		ContentValues contentValues = new ContentValues();
//		contentValues.put("_id","INTEGER PRIMARY KEY");
		contentValues.put(KEY_CONTENT_ID, "TEXT PRIMARY KEY");
		contentValues.put(KEY_CONTENT_NAME, "TEXT");
		contentValues.put(KEY_CONTENT_SUBJECT, "TEXT");
		contentValues.put(KEY_CONTENT_CATEGORY, "TEXT");
		contentValues.put(KEY_CONTENT_SIZE, "TEXT");
		contentValues.put("_id", "INTEGER");
		
		boolean result = DbManager.getInstance().createTable(CONTENTS_TABLE, contentValues);
		return (result);
	}
	
	static public void setTestContentsData() {
		for (int i = 0; i < contentId.length; i++) {
			DbRow dbRow = new DbRow();
			dbRow.add(KEY_CONTENT_ID, contentId[i]);
			dbRow.add(KEY_CONTENT_NAME, contentName[i]);
			dbRow.add(KEY_CONTENT_SUBJECT, contentSubject[i]);
			dbRow.add(KEY_CONTENT_SIZE, contentSize[i]);
			dbRow.add(KEY_CONTENT_CATEGORY, "");
			
			DbManager.getInstance().insertOrReplace(CONTENTS_TABLE, dbRow);
		}
	}
}
