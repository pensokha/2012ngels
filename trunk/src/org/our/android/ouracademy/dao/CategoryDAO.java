package org.our.android.ouracademy.dao;

import java.util.ArrayList;
import java.util.HashMap;

import org.our.android.ouracademy.OurPreferenceManager;
import org.our.android.ouracademy.model.OurCategory;
import org.our.android.ouracademy.model.OurContentCategory;
import org.our.android.ouracademy.util.DbManager;
import org.our.android.ouracademy.util.DbRow;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class CategoryDAO {
	public static final String CATEGORY_TABLE_NAME = "categories";

	public static final String ID_KEY = "id";
	public static final String DEPTH_KEY = "depth";
	public static final String TITLE_ENG_KEY = "title_eng";
	public static final String TITLE_KMR_KEY = "title_kmr";
	public static final String DESCRIPTION_ENG_KEY = "description_eng";
	public static final String DESCRIPTION_KMR_KEY = "description_kmr";
	public static final String PARENT_ID_KEY = "parent_category_id";
	public static final String ICON_KEY = "icon";

	public static final String CATEGORY_DDL = "CREATE TABLE "
			+ CATEGORY_TABLE_NAME + "(" + ID_KEY + " VARCHAR PRIMARY KEY, "
			+ DEPTH_KEY + " INTEGER, " + TITLE_ENG_KEY + " VARCHAR, "
			+ TITLE_KMR_KEY + " VARCHAR, " + DESCRIPTION_ENG_KEY + " TEXT, "
			+ DESCRIPTION_KMR_KEY + " TEXT," + PARENT_ID_KEY + " VARCHAR, "
			+ ICON_KEY + " VARCHAR);";

	private static final String[] CATEGORY_FIELDS = { ID_KEY, DEPTH_KEY,
			TITLE_ENG_KEY, TITLE_KMR_KEY, DESCRIPTION_ENG_KEY,
			DESCRIPTION_KMR_KEY, PARENT_ID_KEY, ICON_KEY };

	private DbManager dbManager;

	public CategoryDAO() {
		super();

		dbManager = DbManager.getInstance();
	}

	public void deleteAllCategories() throws DAOException {
		// 성공 실패 여부 Check 해야됨
		dbManager.deleteTable(CATEGORY_TABLE_NAME);
	}

	public void insertCategories(ArrayList<OurCategory> categories)
			throws DAOException {
		for (OurCategory category : categories) {
			DbRow dbrow = new DbRow();
			dbrow.add(ID_KEY, category.getCategoryId());
			dbrow.add(DEPTH_KEY, category.getCategoryDepth());
			dbrow.add(TITLE_ENG_KEY, category.getCategoryTitleEng());
			dbrow.add(TITLE_KMR_KEY, category.getCategoryTitleKmr());
			dbrow.add(DESCRIPTION_ENG_KEY, category.getCategoryDescriptionEng());
			dbrow.add(DESCRIPTION_KMR_KEY, category.getCategoryDescriptionKmr());
			dbrow.add(PARENT_ID_KEY, category.getCategoryParent());

			if (dbManager.insertOrReplace(CATEGORY_TABLE_NAME, dbrow) != 1) {
				throw new DAOException("Insert category Error");
			}
		}
	}

	public ArrayList<OurCategory> getOnlyCategories() throws DAOException {
		ArrayList<OurCategory> categories = new ArrayList<OurCategory>();

		Cursor cursor = null;
		try {
			SQLiteDatabase db = dbManager.getDB();
			cursor = db.query(CATEGORY_TABLE_NAME, CATEGORY_FIELDS,
					null, null, null, null, null);
			
			if(cursor == null)
				throw new DAOException("Error get contents");
			
			//get last Selected category
			String laseSeletedCategoryId = OurPreferenceManager.getInstance().getSelecetedCategory();

			while (cursor.moveToNext()) {
				OurCategory category = new OurCategory();
				category.setCategoryId(cursor.getString(cursor
						.getColumnIndex(ID_KEY)));
				category.setCategoryDepth(cursor.getInt(cursor
						.getColumnIndex(DEPTH_KEY)));
				category.setCategoryDescriptionEng(cursor.getString(cursor
						.getColumnIndex(DESCRIPTION_ENG_KEY)));
				category.setCategoryDescriptionKmr(cursor.getString(cursor
						.getColumnIndex(DESCRIPTION_KMR_KEY)));
				category.setCategoryTitleEng(cursor.getString(cursor
						.getColumnIndex(TITLE_ENG_KEY)));
				category.setCategoryTitleKmr(cursor.getString(cursor
						.getColumnIndex(TITLE_KMR_KEY)));
				category.setCategoryParent(cursor.getString(cursor
						.getColumnIndex(PARENT_ID_KEY)));
				
				if (cursor.getString(cursor.getColumnIndex(ID_KEY)).equals(laseSeletedCategoryId)) {
					category.isChecked = true;
				}

				categories.add(category);
			}
			cursor.close();
		} catch (SQLException err) {
			throw new DAOException("Error get contents");
		} finally{
			if(cursor != null){
				cursor.close();
			}
		}
		return categories;
	}

	public ArrayList<OurCategory> getCategories() throws DAOException {
		HashMap<String, Integer> categoryIdMap = new HashMap<String, Integer>();

		ArrayList<OurCategory> categories = getOnlyCategories();
		for (int i = 0; i < categories.size(); i++) {
			categoryIdMap.put(categories.get(i).getCategoryId(), i);
		}

		Integer categoryIndex;
		ContentDAO contentDao = new ContentDAO();
		ArrayList<OurContentCategory> contentCategories = contentDao
				.getContentCategories();
		OurCategory category;
		for (OurContentCategory contentCategory : contentCategories) {
			categoryIndex = categoryIdMap.get(contentCategory.getCategoryId());

			if (categoryIndex != null) {
				category = categories.get(categoryIndex);
				category.setNumOfContents(category.getNumOfContents() + 1);
			}
		}

		return categories;
	}
}
