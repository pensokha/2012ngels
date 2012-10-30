package org.our.android.ouracademy.dao;

import java.util.ArrayList;

import org.our.android.ouracademy.model.OurContents;
import org.our.android.ouracademy.util.DbManager;
import org.our.android.ouracademy.util.DbRow;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ContentDAO {
	public final static String CONTENT_TABLE_NAME = "contents";
	public final static String CONTENT_CATEGORY_TABLE_NAME = "content_categories";

	public final static String ID_KEY = "id";
	public final static String SUBJECT_ENG_KEY = "subject_eng";
	public final static String SUBJECT_KMR_KEY = "subject_kmr";
	public final static String CONTENT_URL_KEY = "content_url";
	public final static String SUBTITLE_URL_KEY = "sub_title_url";
	public final static String SIZE_KEY = "size";
	public final static String DOWNLOADED_SIZE_KEY = "downloaded_size";

	public final static String CONTENT_ID_KEY = "content_id";
	public final static String CATEGORY_ID_KEY = "category_id";

	public static final String CONTENT_DDL = "CREATE TABLE "
			+ CONTENT_TABLE_NAME + "(" + ID_KEY + " VARCHAR PRIMARY KEY, "
			+ SUBJECT_ENG_KEY + " VARCHAR, " + SUBJECT_KMR_KEY + " VARCHAR, "
			+ CONTENT_URL_KEY + " VARCHAR, " + SUBTITLE_URL_KEY + " VARCHAR, "
			+ SIZE_KEY + " INTEGER," + DOWNLOADED_SIZE_KEY + " INTEGER);";

	public static final String CONTENT_CATEGORY_DDL = "CREATE TABLE "
			+ CONTENT_CATEGORY_TABLE_NAME + "(" + CONTENT_ID_KEY + " VARCHAR, "
			+ CATEGORY_ID_KEY + " VARCHAR);";

	private static final String[] CONTENT_FIELDS = { ID_KEY, SUBJECT_ENG_KEY,
			SUBJECT_KMR_KEY, CONTENT_URL_KEY, SUBTITLE_URL_KEY, SIZE_KEY,
			DOWNLOADED_SIZE_KEY };

	private DbManager dbManager;

	public ContentDAO() {
		super();

		dbManager = DbManager.getInstance();
	}

	public ArrayList<OurContents> getContents() throws DAOException {
		ArrayList<OurContents> contents = new ArrayList<OurContents>();
		try {
			SQLiteDatabase db = dbManager.getDB();
			Cursor cursor = db.query(CONTENT_TABLE_NAME, CONTENT_FIELDS, null,
					null, null, null, null);

			while (cursor.moveToNext()) {
				OurContents content = new OurContents();
				content.setId(cursor.getString(cursor.getColumnIndex(ID_KEY)));
				content.setSubjectEng(cursor.getString(cursor
						.getColumnIndex(SUBJECT_ENG_KEY)));
				content.setSubjectKmr(cursor.getString(cursor
						.getColumnIndex(SUBJECT_KMR_KEY)));
				content.setContentUrl(cursor.getString(cursor
						.getColumnIndex(CONTENT_URL_KEY)));
				content.setSubtitleUrl(cursor.getString(cursor
						.getColumnIndex(SUBTITLE_URL_KEY)));
				content.setSize(cursor.getLong(cursor.getColumnIndex(SIZE_KEY)));
				content.setDownloadedSize(cursor.getLong(cursor
						.getColumnIndex(DOWNLOADED_SIZE_KEY)));
				
				content.setDownloaded(content.getSize() == content.getDownloadedSize());

				contents.add(content);
			}
			cursor.close();
		} catch (SQLException err) {
			throw new DAOException("Error get contents");
		}

		return contents;
	}

	public void insertContents(ArrayList<OurContents> contents,
			boolean isAddedDownloadedSize) throws DAOException {
		for (OurContents content : contents) {
			DbRow dbRow = new DbRow();
			dbRow.add(ID_KEY, content.getId());
			dbRow.add(SUBJECT_ENG_KEY, content.getSubjectEng());
			dbRow.add(SUBJECT_KMR_KEY, content.getSubjectKmr());
			dbRow.add(CONTENT_URL_KEY, content.getContentUrl());
			dbRow.add(SUBTITLE_URL_KEY, content.getSubtitleUrl());
			dbRow.add(SIZE_KEY, Long.toString(content.getSize()));

			if (isAddedDownloadedSize) {
				dbRow.add(DOWNLOADED_SIZE_KEY,
						Long.toString(content.getDownloadedSize()));
			}
			
			if (dbManager.insertOrReplace(CONTENT_TABLE_NAME, dbRow) != 1) {
				throw new DAOException("Insert content Error");
			}

			if (content.getCategoryIdList().size() > 0) {
				deleteContentCategories(content.getId());
				addContentCategories(content.getId(), content.getCategoryIdList());
			}
		}
	}

	public void deleteAllContent() throws DAOException {
		// 성공 실패 여부를 return 해야됨
		dbManager.deleteTable(CONTENT_TABLE_NAME);
		dbManager.deleteTable(CONTENT_CATEGORY_TABLE_NAME);
	}

	public long deleteContentCategories(String contentId) throws DAOException {
		long result = 0;
		try {
			result = dbManager.delete(CONTENT_CATEGORY_TABLE_NAME,
					CONTENT_ID_KEY + " = ?", new String[] { contentId });
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException("Delete content_categories Error");
		}

		return result;
	}

	public void addContentCategories(String contentId,
			ArrayList<String> categoryIdList) throws DAOException {

		for (String categoryId : categoryIdList) {
			DbRow dbRow = new DbRow();
			dbRow.add(CONTENT_ID_KEY, contentId);
			dbRow.add(CATEGORY_ID_KEY, categoryId);

			if (dbManager.insertOrReplace(CONTENT_CATEGORY_TABLE_NAME, dbRow) != 1) {
				throw new DAOException("Insert content_category Error");
			}
		}

	}
}