package org.our.android.ouracademy.dao;

import java.util.ArrayList;

import org.our.android.ouracademy.model.OurCategory;
import org.our.android.ouracademy.util.DbManager;
import org.our.android.ouracademy.util.DbRow;

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
			dbrow.add(TITLE_KMR_KEY, category.getCategoryDescriptionKmr());
			dbrow.add(PARENT_ID_KEY, category.getCategoryParent());

			if (dbManager.insertOrReplace(CATEGORY_TABLE_NAME, dbrow) != 1) {
				throw new DAOException("Insert category Error");
			}
		}
	}
	
	public ArrayList<OurCategory> getCategories(){
		ArrayList<OurCategory> categories = new ArrayList<OurCategory>();
		
		return categories;
	}
}
