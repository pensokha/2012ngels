package org.our.android.ouracademy.youtubedownloader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

import org.our.android.ouracademy.constants.CommonConstants;
import org.our.android.ouracademy.model.OurContents;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;

/**
*
* @author JiHoon, Moon
*
*/
public class YoutubeDownloadManagerOld {
	public DownloadManager downloadManager;
	private Context appContext = null;
	
	private static YoutubeDownloadManagerOld youtoubeDownloadManager = null;
	
	HashMap<String, Long> downloadMap = new HashMap<String, Long>();
	
	private YoutubeDownloadManagerOld(Context applicationContext) {
		appContext = applicationContext;
		downloadManager = (DownloadManager)appContext.getSystemService(Context.DOWNLOAD_SERVICE);
	}
	
	public synchronized static YoutubeDownloadManagerOld getInstance(Context applicationContext) {
		if (applicationContext == null) {
			return null;
		}
		if (youtoubeDownloadManager == null) {
			youtoubeDownloadManager = new YoutubeDownloadManagerOld(applicationContext);
		}
		return youtoubeDownloadManager;
	}
	
	public long add(String url, OurContents ourContents) {
		Uri urlToDownload = Uri.parse(url);
		DownloadManager.Request request = new DownloadManager.Request(urlToDownload);
		request.setAllowedNetworkTypes(Request.NETWORK_WIFI);
		request.setAllowedOverRoaming(false);
		request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		
		request.setTitle(ourContents.getSubjectEng());
		request.setDescription("Downloading...");
		request.setDestinationInExternalPublicDir(CommonConstants.CONTENTS_FOLDER_NAME, ourContents.getId());
		Environment.getExternalStoragePublicDirectory(CommonConstants.CONTENTS_FOLDER_NAME).mkdirs();
		
		long downloadID = downloadManager.enqueue(request);
		downloadMap.put(ourContents.getId(), downloadID);
		return downloadID;
	}
	
	public void remove(String contentsId) {
		long id = getDownloadId(contentsId);
		downloadManager.remove(id);
		downloadMap.remove(contentsId);
	}
	
	public long getDownloadId(String contentsId) {
		if (downloadMap.isEmpty()) {
			return -1;
		}
		long id = downloadMap.get(contentsId);
		return id;
	}
	
	public boolean checkDownloadComplete(String contentsId) {
		long downloadID = getDownloadId(contentsId);
		if (downloadID == -1) {
			return false;
		}
	    DownloadManager.Query query = new DownloadManager.Query();
	    query.setFilterById(downloadID);

	    Cursor downloadCursor = downloadManager.query(query);
	    if (downloadCursor != null) {
	        downloadCursor.moveToFirst();
	        int statusKey = downloadCursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
	        int reasonKey = downloadCursor.getColumnIndex(DownloadManager.COLUMN_REASON);

	        int status = downloadCursor.getInt(statusKey);
	        int reason = downloadCursor.getInt(reasonKey);
	        if (status == DownloadManager.STATUS_SUCCESSFUL || reason == DownloadManager.ERROR_FILE_ALREADY_EXISTS) {
	            return true;
	        }
	    }
	    return false;
	}
	
	public void CheckDwnloadStatus(OurContents contentsId){
		DownloadManager.Query query = new DownloadManager.Query();
		return;
//		long id = getDownloadId(contentsId.getId());
//		query.setFilterById(id);
//		Cursor cursor = downloadManager.query(query);
//		if (cursor.moveToFirst()) {
//			int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
//			int status = cursor.getInt(columnIndex);
//			int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
//			int reason = cursor.getInt(columnReason);
//
//			switch (status) {
//			case DownloadManager.STATUS_FAILED:
//				String failedReason = "";
//				switch (reason) {
//				case DownloadManager.ERROR_CANNOT_RESUME:
//					failedReason = "ERROR_CANNOT_RESUME";
//					break;
//				case DownloadManager.ERROR_DEVICE_NOT_FOUND:
//					failedReason = "ERROR_DEVICE_NOT_FOUND";
//					break;
//				case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
//					failedReason = "ERROR_FILE_ALREADY_EXISTS";
//					break;
//				case DownloadManager.ERROR_FILE_ERROR:
//					failedReason = "ERROR_FILE_ERROR";
//					break;
//				case DownloadManager.ERROR_HTTP_DATA_ERROR:
//					failedReason = "ERROR_HTTP_DATA_ERROR";
//					break;
//				case DownloadManager.ERROR_INSUFFICIENT_SPACE:
//					failedReason = "ERROR_INSUFFICIENT_SPACE";
//					break;
//				case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
//					failedReason = "ERROR_TOO_MANY_REDIRECTS";
//					break;
//				case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
//					failedReason = "ERROR_UNHANDLED_HTTP_CODE";
//					break;
//				case DownloadManager.ERROR_UNKNOWN:
//					failedReason = "ERROR_UNKNOWN";
//					break;
//				}
//				Toast.makeText(appContext, "FAILED: " + failedReason, Toast.LENGTH_LONG).show();
//				break;
//			case DownloadManager.STATUS_PAUSED:
//				String pausedReason = "";
//				switch (reason) {
//				case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
//					pausedReason = "PAUSED_QUEUED_FOR_WIFI";
//					break;
//				case DownloadManager.PAUSED_UNKNOWN:
//					pausedReason = "PAUSED_UNKNOWN";
//					break;
//				case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
//					pausedReason = "PAUSED_WAITING_FOR_NETWORK";
//					break;
//				case DownloadManager.PAUSED_WAITING_TO_RETRY:
//					pausedReason = "PAUSED_WAITING_TO_RETRY";
//					break;
//				}
//				Toast.makeText(appContext, "PAUSED: " + pausedReason, Toast.LENGTH_LONG).show();
//				break;
//			case DownloadManager.STATUS_PENDING:
//				Toast.makeText(appContext, "PENDING", Toast.LENGTH_LONG).show();
//				break;
//			case DownloadManager.STATUS_RUNNING:
//				Toast.makeText(appContext, "RUNNING", Toast.LENGTH_LONG).show();
//				break;
//			case DownloadManager.STATUS_SUCCESSFUL:
//				Toast.makeText(appContext, "SUCCESSFUL", Toast.LENGTH_LONG).show();
//				GetFile(id);
//				downloadManager.remove(id);
//				break;
//			}
//		}
	}

	private void GetFile(long downloadID) {
		// Retrieve the saved request id
		ParcelFileDescriptor file;
		try {
			file = downloadManager.openDownloadedFile(downloadID);
			FileInputStream fileInputStream = new ParcelFileDescriptor.AutoCloseInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
