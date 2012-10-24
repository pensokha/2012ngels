package org.our.android.ouracademy.dao;

import java.util.ArrayList;

import org.our.android.ouracademy.model.OurContent;

public class StudentDAO extends OurDAO {

	@Override
	public ArrayList<OurContent> getInitContents() throws DAOException {
		return contentDao.getContents();
	}

}
