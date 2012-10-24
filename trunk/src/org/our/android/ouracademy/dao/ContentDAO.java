package org.our.android.ouracademy.dao;

import java.util.ArrayList;

import org.our.android.ouracademy.model.OurContent;
import org.our.android.ouracademy.util.DbManager;
import org.our.android.ouracademy.util.DbRow;

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

	private DbManager dbManager;

	public ContentDAO() {
		super();

		dbManager = DbManager.getInstance();
	}

	public ArrayList<OurContent> getContents() throws DAOException {
		return null;
	}

	public void insertContents(ArrayList<OurContent> contents,
			boolean isAddedDownloadedSize) throws DAOException {
		for (OurContent content : contents) {
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

			if (content.getCategoryIdList().size() > 0) {
				deleteContentCategories(content.getId());
			}

			addContentCategories(content.getId(), content.getCategoryIdList());

			if (dbManager.insertOrReplace(CONTENT_TABLE_NAME, dbRow) != 1) {
				throw new DAOException("Insert content Error");
			}
		}
	}
	
	public void deleteAllContent() throws DAOException{
		//성공 실패 여부를 return 해야됨 
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
