package org.our.android.ouracademy.manager;

import org.our.android.ouracademy.asynctask.CallbackTask;
import org.our.android.ouracademy.asynctask.GetMetaInfoFromMetaFile;
import org.our.android.ouracademy.model.OurContents;

public class TeacherDataManager extends DataManager {
	private static TeacherDataManager instance = new TeacherDataManager();

	public static DataManager getInstance() {
		return instance;
	}

	@Override
	public void getMetaInfo() {
		CallbackTask syncAndContentNoti = new GetMetaInfoFromMetaFile(context);
		executeRunnable(syncAndContentNoti);
	}

	@Override
	public void download(OurContents content) {
		// download from youtube
	}

	@Override
	public void cancelDownload(OurContents content) {
		// TODO Auto-generated method stub

	}
}
