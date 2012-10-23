package org.our.android.ouracademy.manager;

import java.util.ArrayList;

import org.our.android.ouracademy.model.OurContent;
import org.our.android.ouracademy.util.DbManager;
import org.our.android.ouracademy.util.DbRow;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TestFileDbCreate {
	public static final String CONTENTS_TABLE = "CONTENTS_TBL";

	public static final String KEY_CONTENT_ID = "ContentId";
	public static final String KEY_CONTENT_NAME = "ContentName";
	public static final String KEY_CONTENT_SUBJECT = "ContentSubject";
	public static final String KEY_CONTENT_SIZE = "ContentSize";
	public static final String KEY_CONTENT_CATEGORY = "ContentCategory";

	private static final String[] FIELDS = { KEY_CONTENT_ID, KEY_CONTENT_NAME,
			KEY_CONTENT_SUBJECT, KEY_CONTENT_SIZE, KEY_CONTENT_CATEGORY };

	static final String[] contentId = { "Boa.flv", "EpikHigh.flv", "HOT.flv",
			"Secret.flv", "WonderGirls.flv" };
	static final String[] contentName = { "Boa", "EpikHigh", "HOT", "Secret",
			"WonderGirls" };
	static final String[] contentSubject = { "No1", "it's cold", "TTT",
			"love is move", "like this" };
	static final Integer[] contentSize = { 10, 22, 2352352, 235232, 242523 };
	static final String[] contentCategory = { "음악1", "음악2", "음악3", "음악4", "음악5" };

	public TestFileDbCreate() {

	}

	static public boolean createContentsTable() {
		ContentValues contentValues = new ContentValues();
		// contentValues.put("_id","INTEGER PRIMARY KEY");
		contentValues.put(KEY_CONTENT_ID, "TEXT PRIMARY KEY");
		contentValues.put(KEY_CONTENT_NAME, "TEXT");
		contentValues.put(KEY_CONTENT_SUBJECT, "TEXT");
		contentValues.put(KEY_CONTENT_CATEGORY, "TEXT");
		contentValues.put(KEY_CONTENT_SIZE, "INTEGER");
		contentValues.put("_id", "INTEGER");

		boolean result = DbManager.getInstance().createTable(CONTENTS_TABLE,
				contentValues);
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

	static public ArrayList<OurContent> getAllContents() {
		ArrayList<OurContent> contents = new ArrayList<OurContent>();
//		SQLiteDatabase db = DbManager.getInstance().getDB();
//		Cursor cursor = db.query(CONTENTS_TABLE, FIELDS, null, null, null,
//				null, null);
//
//		while (cursor.moveToNext()) {
//			OurContent content = new OurContent();
//			content.setId(cursor.getString(cursor
//					.getColumnIndex(KEY_CONTENT_ID)));
//			content.setName(cursor.getString(cursor
//					.getColumnIndex(KEY_CONTENT_NAME)));
//			content.setSubject(cursor.getString(cursor
//					.getColumnIndex(KEY_CONTENT_SUBJECT)));
//			content.setSize(cursor.getLong(cursor
//					.getColumnIndex(KEY_CONTENT_SIZE)));
//			content.setCategory(cursor.getString(cursor
//					.getColumnIndex(KEY_CONTENT_CATEGORY)));
//
//			contents.add(content);
//		}
//		cursor.close();
//
		return contents;
	}

	static public void setAllContents(ArrayList<OurContent> contents) {
//		for (OurContent content : contents) {
//			DbRow dbRow = new DbRow();
//			dbRow.add(KEY_CONTENT_ID, content.getId());
//			dbRow.add(KEY_CONTENT_NAME, content.getName());
//			dbRow.add(KEY_CONTENT_SUBJECT, content.getSubject());
//			dbRow.add(KEY_CONTENT_SIZE, Long.valueOf(content.getSize()).toString());
//			dbRow.add(KEY_CONTENT_CATEGORY, content.getCategory());
//
//			DbManager.getInstance().insertOrReplace(CONTENTS_TABLE, dbRow);
//		}
	}
}
