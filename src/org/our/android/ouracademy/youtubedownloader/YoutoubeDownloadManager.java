package org.our.android.ouracademy.youtubedownloader;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.our.android.ouracademy.OurDefine;
import org.our.android.ouracademy.model.OurContents;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

public class YoutoubeDownloadManager {
	public DownloadManager downloadManager;
	private Context appContext = null;
	
	private static YoutoubeDownloadManager youtoubeDownloadManager = null;
	
	long lastId;
	
	HashMap<String, String> downloadMap = new HashMap<String, String>();
	
	private YoutoubeDownloadManager(Context applicationContext) {
		appContext = applicationContext;
		downloadManager = (DownloadManager)appContext.getSystemService(Context.DOWNLOAD_SERVICE);
	}
	
	public synchronized static YoutoubeDownloadManager getInstance(Context applicationContext) {
		if (applicationContext == null) {
			return null;
		}
		if (youtoubeDownloadManager == null) {
			youtoubeDownloadManager = new YoutoubeDownloadManager(applicationContext);
		}
		return youtoubeDownloadManager;
	}
	
	public long add(OurContents ourContents) {
		URL url;
		ArrayList<Videos> videoList = null;
		try {
			url = new URL(ourContents.getContentUrl());
			YoutubeDownloader downloader = new YoutubeDownloader();
			videoList = downloader.getUrl(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String ursl = null;
		for (Videos video : videoList) {
			String dd = video.url;
			String type = video.type;
			String resolution = video.resolution;
			if ("mp4".equals(type) && "medium".equals(resolution)) {
				ursl = dd;
			}
		}
		
		ursl = encodeURI(ursl);
		Uri urlToDownload = Uri.parse(ursl);
		DownloadManager.Request request = new DownloadManager.Request(urlToDownload);
		
		request.setTitle(ourContents.getSubjectEng());
		request.setDescription("Downloading");
		request.setDestinationInExternalPublicDir(OurDefine.CONTENTS_FOLDER_NAME, ourContents.getSubjectEng());
		Environment.getExternalStoragePublicDirectory(OurDefine.CONTENTS_FOLDER_NAME).mkdirs();
		
		lastId = downloadManager.enqueue(request);
		
		return lastId;
	}
	
	public void remove(String url) {
		downloadManager.remove(lastId);
	}
	
	public String encodeURI(String url) {
	    String[] findList = {" ",   "\""};
	    String[] replList = {"%20", "%22"};
	    for (int i = 0; i < findList.length; i++) {
	    	url = url.replace(findList[i], replList[i]);
	    }
	    return url;
	} 

}
