package org.our.android.ouracademy.asynctask;

import java.util.ArrayList;

import org.our.android.ouracademy.dao.DAOException;
import org.our.android.ouracademy.dao.VideoFileDAO;
import org.our.android.ouracademy.manager.FileManager;
import org.our.android.ouracademy.model.OurVideoFile;
import org.our.android.ouracademy.util.DbManager;

import android.content.Context;

public class SyncFileAndDatabase extends CallbackTask {
	protected Context context;

	public SyncFileAndDatabase(Context context) {
		super();
		this.context = context;
	}

	@Override
	public void onInterrupted() {
	}

	@Override
	public void proceed() {
		ArrayList<OurVideoFile> videoFiles = FileManager.getVideoFiles();

		ArrayList<OurVideoFile> dontExistVideos;
		try {
			DbManager dbManager = DbManager.getInstance();
			VideoFileDAO videoFileDAO = new VideoFileDAO();
			if (dbManager.beginTransaction(context) == false) {
				throw new DAOException("fail begin transaction");
			}

			videoFileDAO.deleteFileState();
			dontExistVideos = videoFileDAO.updateFileState(videoFiles);

			if (Thread.currentThread().isInterrupted() == false) {
				dbManager.commitTransaction();
				
				if (dontExistVideos != null) {
//					FileManager.removeFiles(dontExistVideos);
				}
			}
			if (dbManager.endTransaction() == false) {
				throw new DAOException("fail end transaction");
			}
		} catch (DAOException e) {
			e.printStackTrace();
		}
	}
}
