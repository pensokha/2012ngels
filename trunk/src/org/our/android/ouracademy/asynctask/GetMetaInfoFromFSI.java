package org.our.android.ouracademy.asynctask;

import org.our.android.ouracademy.OurPreferenceManager;
import org.our.android.ouracademy.dao.CategoryDAO;
import org.our.android.ouracademy.dao.ContentDAO;
import org.our.android.ouracademy.dao.DAOException;
import org.our.android.ouracademy.dao.FSIDAO;
import org.our.android.ouracademy.model.OurMetaInfo;
import org.our.android.ouracademy.util.DbManager;

import android.content.Context;


public class GetMetaInfoFromFSI extends SyncAndReloadNoti{
	public GetMetaInfoFromFSI(Context context){
		super(context);
	}

	@Override
	public void proceed() {
		FSIDAO fsiDao = new FSIDAO();
		try {
			OurMetaInfo metaInfo = fsiDao.getMetaInfo();
			
			if(Thread.currentThread().isInterrupted() == false){
				GetMetaInfoFromFSI.getMetaInfoProcesses(metaInfo);
			}
			
			super.proceed();
		} catch (DAOException e) {
			e.printStackTrace();
		}
	}
	
	public static void getMetaInfoProcesses(OurMetaInfo metaInfo) throws DAOException{
		switch (metaInfo.getResponseCode()) {
		case OurMetaInfo.RES_CODE_SUCCESS:
			OurPreferenceManager.getInstance().setVersion(metaInfo.getVersion());
			
			DbManager dbManager = DbManager.getInstance();
			CategoryDAO categoryDao = new CategoryDAO();
			ContentDAO contentDao = new ContentDAO();
			
			if(dbManager.beginTransaction() == false){
				throw new DAOException("fail begin transaction");
			}
			contentDao.deleteAllContent();
			categoryDao.deleteAllCategories();
			
			categoryDao.insertCategories(metaInfo.getCategories());
			contentDao.insertContents(metaInfo.getContents(), false);
			
			if(Thread.currentThread().isInterrupted() == false){
				dbManager.commitTransaction();
			}
			if(dbManager.endTransaction() == false){
				throw new DAOException("fail end transaction");
			}
			
			break;
		case OurMetaInfo.RES_CODE_DONT_NEED_UPDATE:
			break;
		default:
			throw new DAOException("ResponseCode : "+metaInfo.getResponseCode());
		}
	}
}
