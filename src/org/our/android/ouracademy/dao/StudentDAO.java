package org.our.android.ouracademy.dao;

import java.util.ArrayList;

import org.our.android.ouracademy.model.OurContents;

public class StudentDAO extends OurDAO {

	@Override
	public ArrayList<OurContents> getInitContents() throws DAOException {
		return contentDao.getContents();
	}

}
