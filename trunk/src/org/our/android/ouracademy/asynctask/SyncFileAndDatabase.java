package org.our.android.ouracademy.asynctask;

import java.util.ArrayList;

import org.our.android.ouracademy.dao.DAOException;
import org.our.android.ouracademy.dao.VideoFileDAO;
import org.our.android.ouracademy.manager.FileManager;
import org.our.android.ouracademy.model.OurVideoFile;
import org.our.android.ouracademy.util.DbManager;

import android.content.Context;


public class SyncFileAndDatabase implements Runnable{
	protected Context context;
	
	public SyncFileAndDatabase(Context context) {
		super();
		
		this.context = context;
	}

	@Override
	public void run() {
		ArrayList<OurVideoFile> videoFiles = FileManager.getVideoFiles();
		
		ArrayList<OurVideoFile> dontExistVideos;
		try{
			DbManager dbManager = DbManager.getInstance();
			VideoFileDAO videoFileDAO = new VideoFileDAO();
			if(dbManager.beginTransaction() == false){
				throw new DAOException("fail begin transaction");
			}
			
			videoFileDAO.deleteFileState();
			dontExistVideos = videoFileDAO.updateFileState(videoFiles);
			
			dbManager.commitTransaction();
			if(dbManager.endTransaction() == false){
				throw new DAOException("fail end transaction");
			}
			
			if(dontExistVideos != null){
				FileManager.removeFiles(dontExistVideos);
			}
			
			if(Thread.currentThread().isInterrupted() == false){
				dbManager.commitTransaction();
			}
		}catch(DAOException e){
			e.printStackTrace();
		}
	}
}
