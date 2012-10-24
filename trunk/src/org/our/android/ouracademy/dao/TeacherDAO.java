package org.our.android.ouracademy.dao;

import java.util.ArrayList;

import org.our.android.ouracademy.model.OurContent;
import org.our.android.ouracademy.model.OurMetaInfo;

public class TeacherDAO extends OurDAO {

	@Override
	public ArrayList<OurContent> getInitContents() throws DAOException {
		FSIDAO fsiDao = new FSIDAO();
		OurMetaInfo metaInfo = fsiDao.getMetaInfo();
		ArrayList<OurContent> contents;
		switch (metaInfo.getVersion()) {
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
