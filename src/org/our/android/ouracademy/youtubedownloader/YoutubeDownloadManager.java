package org.our.android.ouracademy.youtubedownloader;

import java.util.HashMap;

import org.our.android.ouracademy.OurApplication;
import org.our.android.ouracademy.downloader.DownloadRequest;
import org.our.android.ouracademy.model.OurContents;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

/**
*
* @author JiHoon, Moon
*
*/
public class YoutubeDownloadManager {
	public DownloadRequest downloadManager;
	private Context appContext = null;
	
	private static YoutubeDownloadManager youtoubeDownloadManager = null;
	
	public static HashMap<Integer, OurContents> downloadMap = new HashMap<Integer, OurContents>();
	
	
	private YoutubeDownloadManager(Context applicationContext) {
		appContext = applicationContext;
		downloadManager = DownloadRequest.getInstance(OurApplication.getInstance(), false);
	}
	
	public synchronized static YoutubeDownloadManager getInstance(Context applicationContext) {
		if (applicationContext == null) {
			return null;
		}
		if (youtoubeDownloadManager == null) {
			youtoubeDownloadManager = new YoutubeDownloadManager(applicationContext);
		}
		return youtoubeDownloadManager;
	}
	
	public void add(String url, OurContents ourContents) {
		int id = downloadManager.add(url, Environment.getExternalStorageDirectory() + "/OurAcademy/", ourContents.getId(),  true, 0);
		if (id == 0) {
			Log.d("TEST", "Main add fail!");
		} else {
			downloadMap.put(id, ourContents);
		}
	}
	
	public void cancel(int id) {
		downloadManager.cancel(id);
	}
	
	public void removeId(OurContents ourContents) {
		downloadMap.remove(ourContents);
	}
	
	public OurContents getOurContents(int id) {
		OurContents ourContents = downloadMap.get(id);
		return ourContents;
	}
}
