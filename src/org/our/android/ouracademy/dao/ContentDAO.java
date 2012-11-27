package org.our.android.ouracademy.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.our.android.ouracademy.model.OurCategory;
import org.our.android.ouracademy.model.OurContentCategory;
import org.our.android.ouracademy.model.OurContents;
import org.our.android.ouracademy.util.DbManager;
import org.our.android.ouracademy.util.DbRow;

import android.content.ContentValues;
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
	public final static String TOPIC_ID_KEY = "topic_id";
	public final static String TOPIC_TITLE_ENG_KEY = "topic_title_eng";
	public final static String TOPIC_TITLE_KMR_KEY = "topic_title_kmr";
	public final static String DESCRIPTION_ENG_KEY = "description_eng";
	public final static String DESCRIPTION_KMR_KEY = "description_kmr";
	public final static String DOWNLOADED_SIZE_KEY = "downloaded_size";
	public final static String FILE_STATUS = ""; // 데이터베이스 테이블에서 파일 상태 표시하는 이름
													// 넣어야 함. -sung-chul park.

	public final static String CONTENT_ID_KEY = "content_id";
	public final static String CATEGORY_ID_KEY = "category_id";

	public static final String CONTENT_DDL = "CREATE TABLE "
			+ CONTENT_TABLE_NAME + "(" + ID_KEY + " VARCHAR PRIMARY KEY, "
			+ SUBJECT_ENG_KEY + " VARCHAR, " + SUBJECT_KMR_KEY + " VARCHAR, "
			+ CONTENT_URL_KEY + " VARCHAR, " + SUBTITLE_URL_KEY + " VARCHAR, "
			+ SIZE_KEY + " INTEGER," + DOWNLOADED_SIZE_KEY + " INTEGER, "
			+ TOPIC_ID_KEY + " VARCHAR, " + TOPIC_TITLE_ENG_KEY + " VARCHAR, "
			+ TOPIC_TITLE_KMR_KEY + " VARCHAR, " + DESCRIPTION_ENG_KEY
			+ " VARCHAR, " + DESCRIPTION_KMR_KEY + " VARCHAR);";

	public static final String CONTENT_CATEGORY_DDL = "CREATE TABLE "
			+ CONTENT_CATEGORY_TABLE_NAME + "(" + CONTENT_ID_KEY + " VARCHAR, "
			+ CATEGORY_ID_KEY + " VARCHAR, PRIMARY KEY(" + CATEGORY_ID_KEY
			+ " ASC," + CONTENT_ID_KEY + ")); ";

	private static final String[] CONTENT_FIELDS = {ID_KEY, SUBJECT_ENG_KEY,
			SUBJECT_KMR_KEY, CONTENT_URL_KEY, SUBTITLE_URL_KEY, SIZE_KEY,
			DOWNLOADED_SIZE_KEY, TOPIC_ID_KEY, TOPIC_TITLE_ENG_KEY,
			TOPIC_TITLE_KMR_KEY, DESCRIPTION_ENG_KEY, DESCRIPTION_KMR_KEY};

	private static final String[] CONTENT_CATEGORY_FIELDS = {CONTENT_ID_KEY,
			CATEGORY_ID_KEY};

	private DbManager dbManager;

	public ContentDAO() {
		super();

		dbManager = DbManager.getInstance();
	}

	public ArrayList<OurContents> getDownloadedContents() throws DAOException {
		return getOnlyContents(DOWNLOADED_SIZE_KEY + " = " + SIZE_KEY, null);
	}

	public ArrayList<OurContents> getExistingContents(
			ArrayList<OurContents> contents) throws DAOException {
		StringBuffer contentIdQuery = new StringBuffer(ID_KEY);
		contentIdQuery.append(" IN (");
		for (OurContents content : contents) {
			contentIdQuery.append('\'');
			contentIdQuery.append(content.getId());
			contentIdQuery.append("',");
		}
		int lastComma = contentIdQuery.lastIndexOf(",");
		if (lastComma == -1) {
			return null;
		}
		contentIdQuery.setCharAt(lastComma, ')');
		contentIdQuery.append(" AND ");
		contentIdQuery.append(DOWNLOADED_SIZE_KEY);
		contentIdQuery.append(" != ");
		contentIdQuery.append(SIZE_KEY);

		return getOnlyContents(contentIdQuery.toString(), null);
	}

	public ArrayList<OurContents> getOnlyContents(String selection,
			String[] selectionArgs) throws DAOException {
		ArrayList<OurContents> contents = new ArrayList<OurContents>();
		Cursor cursor = null;
		try {
			SQLiteDatabase db = dbManager.getDB();
			cursor = db.query(CONTENT_TABLE_NAME, CONTENT_FIELDS, selection,
					selectionArgs, null, null, null);

			if (cursor == null) {
				throw new DAOException("Error get contents");
			}

			while (cursor.moveToNext()) {
				OurContents content = new OurContents();

				setContentDataFromCursor(cursor, content);

				contents.add(content);
			}
		} catch (SQLException err) {
			throw new DAOException("Error get contents");
		} finally {
			if (cursor != null)
				cursor.close();
		}

		return contents;
	}

	public void setContentDataFromCursor(Cursor cursor, OurContents content)
			throws SQLException {
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
		content.setTopicId(cursor.getString(cursor.getColumnIndex(TOPIC_ID_KEY)));
		content.setTopicTitleEng(cursor.getString(cursor
				.getColumnIndex(TOPIC_TITLE_ENG_KEY)));
		content.setTopicTitleKmr(cursor.getString(cursor
				.getColumnIndex(TOPIC_TITLE_KMR_KEY)));
		content.setDescriptionEng(cursor.getString(cursor
				.getColumnIndex(DESCRIPTION_ENG_KEY)));
		content.setDescriptionKmr(cursor.getString(cursor
				.getColumnIndex(DESCRIPTION_KMR_KEY)));

		content.setDownloaded(content.getSize() == content.getDownloadedSize());

	}

	public void updateDownloadedSize(OurContents content) throws DAOException {
		try {
			SQLiteDatabase db = dbManager.getDB();
			ContentValues values = new ContentValues();
			values.put(DOWNLOADED_SIZE_KEY, content.getDownloadedSize());
			db.update(CONTENT_TABLE_NAME, values, ID_KEY + "= ?",
					new String[] {content.getId()});
		} catch (SQLException err) {
			throw new DAOException("Error update downloaded size");
		}
	}

	/**
	 * @author Sung-Chul Park
	 * @param content
	 * @throws DAOException
	 */
	// public void updateFileStatus(OurContents content) throws DAOException {
	// try {
	// SQLiteDatabase db = dbManager.getDB();
	// ContentValues values = new ContentValues();
	//
	// values.put(DOWNLOADED_SIZE_KEY, OurContents.FileStatus.NONE);
	// db.update(CONTENT_TABLE_NAME, values, ID_KEY + "= ?", new String[] {
	// content.getId() });
	// } catch (SQLException err) {
	// throw new DAOException("Error update downloaded size");
	// }
	// }

	public ArrayList<OurContentCategory> getContentCategories()
			throws DAOException {
		ArrayList<OurContentCategory> contentCategories = new ArrayList<OurContentCategory>();
		Cursor cursor = null;
		try {
			SQLiteDatabase db = dbManager.getDB();
			cursor = db.query(CONTENT_CATEGORY_TABLE_NAME,
					CONTENT_CATEGORY_FIELDS, null, null, null, null, null);

			if (cursor == null)
				throw new DAOException("Error get contents");

			while (cursor.moveToNext()) {
				OurContentCategory contentCategory = new OurContentCategory();
				contentCategory.setContentId(cursor.getString(cursor
						.getColumnIndex(CONTENT_ID_KEY)));
				contentCategory.setCategoryId(cursor.getString(cursor
						.getColumnIndex(CATEGORY_ID_KEY)));

				contentCategories.add(contentCategory);
			}
		} catch (SQLException err) {
			throw new DAOException("Error get contents");
		} finally {
			if (cursor != null)
				cursor.close();
		}

		return contentCategories;
	}

	public ArrayList<OurContents> getContents() throws DAOException {
		HashMap<String, Integer> contentIdMap = new HashMap<String, Integer>();

		ArrayList<OurContents> contents = getOnlyContents(null, null);
		for (int i = 0; i < contents.size(); i++) {
			contentIdMap.put(contents.get(i).getId(), i);
		}

		Integer contentIndex;
		ArrayList<OurContentCategory> contentCategories = getContentCategories();
		for (OurContentCategory contentCategory : contentCategories) {
			contentIndex = contentIdMap.get(contentCategory.getContentId());

			if (contentIndex != null) {
				contents.get(contentIndex).getCategoryIdList()
						.add(contentCategory.getCategoryId());
			}
		}

		return contents;
	}

	public String getDuplicatedContentsQuery(
			HashMap<String, OurCategory> selectedCategories) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT ");
		for (int i = 0; i < CONTENT_FIELDS.length; i++) {
			sql.append(CONTENT_FIELDS[i]);
			sql.append(",");
		}
		sql.append(CATEGORY_ID_KEY);
		sql.append(" FROM ");
		sql.append(CONTENT_TABLE_NAME);
		sql.append(" LEFT JOIN ");
		sql.append(CONTENT_CATEGORY_TABLE_NAME);
		sql.append(" ON ");
		sql.append(ID_KEY);
		sql.append("=");
		sql.append(CONTENT_ID_KEY);
		if (selectedCategories != null && selectedCategories.size() > 0) {
			sql.append(" WHERE ");
			sql.append(CATEGORY_ID_KEY);
			sql.append(" IN ('");
			Iterator<String> categoryIds = selectedCategories.keySet()
					.iterator();
			sql.append(categoryIds.next());
			sql.append("'");
			while (categoryIds.hasNext()) {
				sql.append(",'");
				sql.append(categoryIds.next());
				sql.append("'");
			}
			sql.append(")");
		}
		sql.append(" ORDER BY ");
		sql.append(CATEGORY_ID_KEY);
		sql.append(" ASC");

		return sql.toString();
	}

	public ArrayList<OurContents> getDuplicatedContents(
			HashMap<String, OurCategory> selectedCategories)
			throws DAOException {

		ArrayList<OurContents> contents = new ArrayList<OurContents>();
		if (selectedCategories == null || selectedCategories.size() == 0) {
			return contents;
		}

		Cursor cursor = null;
		try {
			SQLiteDatabase db = dbManager.getDB();

			cursor = db.rawQuery(
					getDuplicatedContentsQuery(selectedCategories), null);

			if (cursor == null)
				throw new DAOException("Error get contents");

			while (cursor.moveToNext()) {
				OurContents content = new OurContents();

				setContentDataFromCursor(cursor, content);

				content.selectedCategory = selectedCategories.get(cursor
						.getString(cursor.getColumnIndex(CATEGORY_ID_KEY)));

				contents.add(content);
			}

		} catch (Exception e) {
			throw new DAOException("Error get contents");
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return contents;
	}

	public String getDuplicatedContentsQuery(String selectedCategories) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT ");
		for (int i = 0; i < CONTENT_FIELDS.length; i++) {
			sql.append(CONTENT_FIELDS[i]);
			sql.append(",");
		}
		sql.append(CATEGORY_ID_KEY);
		sql.append(" FROM ");
		sql.append(CONTENT_TABLE_NAME);
		sql.append(" LEFT JOIN ");
		sql.append(CONTENT_CATEGORY_TABLE_NAME);
		sql.append(" ON ");
		sql.append(ID_KEY);
		sql.append("=");
		sql.append(CONTENT_ID_KEY);
		if (selectedCategories != null) {
			sql.append(" WHERE ");
			sql.append(CATEGORY_ID_KEY);
			sql.append(" IN ('");
			sql.append(selectedCategories);
			sql.append("'");
			sql.append(")");
		}
		sql.append(" ORDER BY ");
		sql.append(CATEGORY_ID_KEY);
		sql.append(" ASC");

		return sql.toString();
	}

	public ArrayList<OurContents> getDuplicatedContents(String selectedCategory) throws DAOException {

		ArrayList<OurContents> contents = new ArrayList<OurContents>();

		Cursor cursor = null;
		try {
			SQLiteDatabase db = dbManager.getDB();

			cursor = db.rawQuery(getDuplicatedContentsQuery(selectedCategory), null);

			if (cursor == null)
				throw new DAOException("Error get contents");

			while (cursor.moveToNext()) {
				OurContents content = new OurContents();
				setContentDataFromCursor(cursor, content);
				contents.add(content);
			}

		} catch (Exception e) {
			throw new DAOException("Error get contents");
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return contents;
	}

	public ArrayList<OurContents> getUndownloadedContents() throws DAOException {

		ArrayList<OurContents> contents = new ArrayList<OurContents>();

		SQLiteDatabase db = dbManager.getDB();

		Cursor cursor = null;

		try {
			cursor = db.query(CONTENT_TABLE_NAME, CONTENT_FIELDS,
					DOWNLOADED_SIZE_KEY + "!=" + SIZE_KEY, null, null, null,
					null);

			if (cursor == null) {
				throw new DAOException("Error get contents");
			}

			OurContents content;
			while (cursor.moveToNext()) {
				content = new OurContents();

				setContentDataFromCursor(cursor, content);

				contents.add(content);
			}
		} catch (SQLException e) {
			throw new DAOException("Error get contents");
		} finally {
			if (cursor != null)
				cursor.close();
		}

		return contents;
	}

	public void insertContents(ArrayList<OurContents> contents,
			boolean isAddedDownloadedSize) throws DAOException {
		DbRow dbRow;
		for (OurContents content : contents) {
			dbRow = new DbRow();
			dbRow.add(ID_KEY, content.getId());
			dbRow.add(SUBJECT_ENG_KEY, content.getSubjectEng());
			dbRow.add(SUBJECT_KMR_KEY, content.getSubjectKmr());
			dbRow.add(CONTENT_URL_KEY, content.getContentUrl());
			dbRow.add(SUBTITLE_URL_KEY, content.getSubtitleUrl());
			dbRow.add(SIZE_KEY, Long.toString(content.getSize()));
			dbRow.add(TOPIC_ID_KEY, content.getTopicId());
			dbRow.add(TOPIC_TITLE_ENG_KEY, content.getTopicTitleEng());
			dbRow.add(TOPIC_TITLE_KMR_KEY, content.getTopicTitleKmr());
			dbRow.add(DESCRIPTION_ENG_KEY, content.getDescriptionEng());
			dbRow.add(DESCRIPTION_KMR_KEY, content.getDescriptionKmr());

			if (isAddedDownloadedSize) {
				dbRow.add(DOWNLOADED_SIZE_KEY,
						Long.toString(content.getDownloadedSize()));
			}

			if (dbManager.insertOrReplace(CONTENT_TABLE_NAME, dbRow) != 1) {
				throw new DAOException("Insert content Error");
			}

			if (content.getCategoryIdList().size() > 0) {
				deleteContentCategories(content.getId());
				addContentCategories(content.getId(),
						content.getCategoryIdList());
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
					CONTENT_ID_KEY + " = ?", new String[] {contentId});
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
