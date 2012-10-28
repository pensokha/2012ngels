package org.our.android.ouracademy.dao;

import java.util.ArrayList;

import org.our.android.ouracademy.model.OurVideoFile;
import org.our.android.ouracademy.util.DbManager;

import android.content.ContentValues;

public class VideoFileDAO {
	private DbManager dbManager;
	
	public VideoFileDAO(){
		super();
		
		dbManager =  DbManager.getInstance();
	}
	
	public void deleteFileState() throws DAOException{
		ContentValues args = new ContentValues();
		args.put(ContentDAO.DOWNLOADED_SIZE_KEY, 0);
		
		long affectedRows = dbManager.update(ContentDAO.CONTENT_TABLE_NAME, args, null, null);
		
		if(affectedRows < 0){
			throw new DAOException("Fail delete file state");
		}
	}
	
	public ArrayList<OurVideoFile> updateFileState(ArrayList<OurVideoFile> videoFiles) throws DAOException{
		ArrayList<OurVideoFile> dontExistVideos = new ArrayList<OurVideoFile>();
		if(videoFiles != null){
			String whereClause = ContentDAO.ID_KEY + "= ?";
			long affectedRows = 0;
			ContentValues args;
			String[] whereArgs = new String[1];
			for(OurVideoFile videoFile : videoFiles){
				args = new ContentValues();
				args.put(ContentDAO.DOWNLOADED_SIZE_KEY, videoFile.getSize());
				
				whereArgs[0] = videoFile.getContentId();
				
				affectedRows = dbManager.update(ContentDAO.CONTENT_TABLE_NAME, args, 
						whereClause, whereArgs);
				
				if(affectedRows < 0){
					throw new DAOException("Fail delete file state");
				}else if(affectedRows == 0){
					dontExistVideos.add(videoFile);
				}
			}
		}
		return dontExistVideos;
	}
}
