package org.our.android.ouracademy.manager;

import org.our.android.ouracademy.OurPreferenceManager;

public class DataManagerFactory {
	public final static DataManager getDataManager() {
		if (OurPreferenceManager.getInstance().isTeacher()) {
			return TeacherDataManager.getInstance();
		} else {
			return StudentDataManager.getInstance();
		}

	}
}
