package org.our.android.ouracademy.dao;

import java.util.ArrayList;

import org.our.android.ouracademy.OurPreferenceManager;
import org.our.android.ouracademy.model.OurContent;
import org.our.android.ouracademy.model.OurMetaInfo;
import org.our.android.ouracademy.util.DbManager;

import android.util.Log;

public abstract class OurDAO {
	protected OurPreferenceManager pref;
	protected DbManager dbManager;
	protected ContentDAO contentDao;
	protected CategoryDAO categoryDao;

	public OurDAO() {
		super();

		pref = OurPreferenceManager.getInstance();
		dbManager = DbManager.getInstance();

		contentDao = new ContentDAO();
		categoryDao = new CategoryDAO();
	}

	public void sync() {
	}; // 장비에 존재하는 파일과 DB를 Match 시키는 Class

	// 초기화시에 가져올 data
	public abstract ArrayList<OurContent> getInitContents() throws DAOException;

	protected void insertMetaInfo(OurMetaInfo metaInfo) throws DAOException {
		pref.setVersion(metaInfo.getVersion());

		if (dbManager.beginTransaction() != true) {
			throw new DAOException("Error Begin Transaction");
		}

		try {
			categoryDao.deleteAllCategories();
			contentDao.deleteAllContent();
			
			categoryDao.insertCategories(metaInfo.getCategories());
			contentDao.insertContents(metaInfo.getContents(), false);
			
			dbManager.commitTransaction();
		} catch (DAOException err) {
			dbManager.endTransaction();
			throw err;
		}

		if (dbManager.endTransaction() != true) {
			throw new DAOException("Error End Transaction");
		}
	};
}
