package org.our.android.ouracademy.dao;

import java.util.ArrayList;

import org.our.android.ouracademy.model.OurContents;
import org.our.android.ouracademy.model.OurMetaInfo;

import android.util.Log;

public class TeacherDAO extends OurDAO {

	@Override
	public ArrayList<OurContents> getInitContents() throws DAOException {
		FSIDAO fsiDao = new FSIDAO();
		OurMetaInfo metaInfo = fsiDao.getMetaInfo();
		
		ArrayList<OurContents> contents;
		switch (metaInfo.getResponseCode()) {
			case OurMetaInfo.RES_CODE_SUCCESS:
				insertMetaInfo(metaInfo);
				break;
			case OurMetaInfo.RES_CODE_DONT_NEED_UPDATE:
				break;
			default:
				new DAOException("Fail to get data");
		}
		
		contents = contentDao.getContents();

		return contents;
	}
}