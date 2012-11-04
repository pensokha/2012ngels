package org.our.android.ouracademy.manager;

import java.util.ArrayList;
import java.util.HashMap;

import org.our.android.ouracademy.asynctask.CallbackTask;
import org.our.android.ouracademy.asynctask.CallbackTask.OurCallback;
import org.our.android.ouracademy.asynctask.SyncAndContentNoti;
import org.our.android.ouracademy.model.OurContents;
import org.our.android.ouracademy.p2p.client.DownloadClient;
import org.our.android.ouracademy.p2p.client.GetMetaInfoClient;
import org.our.android.ouracademy.service.StudentService;
import org.our.android.ouracademy.ui.pages.MainActivity.OurDataChangeReceiver;
import org.our.android.ouracademy.util.NetworkState;
import org.our.android.ouracademy.wifidirect.WifiDirectWrapper;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

public class StudentDataManager extends DataManager {
	private static StudentDataManager instance = new StudentDataManager();
	private DownloadManager downloadManager = new DownloadManager();

	public static DataManager getInstance() {
		return instance;
	}

	@Override
	public void onPowerOn(Context context) {
		// Do noting
	}

	@Override
	public void onPowerOff(Context context) {
		stopService(context);
	}

	/*******
	 * When : start main page, change mode 1. register wifi receiver, find
	 * teacher 2. if find teacher, get meta info 3. sync file and db
	 */
	@Override
	public synchronized void startService(Context context) {
		if (isStarted() == false) {
			super.startService(context);
			
			WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			wifi.disconnect();
			

			CallbackTask syncAndContentNoti = new SyncAndContentNoti(context);
			syncAndContentNoti.addCallback(callback);
			executeRunnable(syncAndContentNoti);
		}
	}

	/**********
	 * When : onPowerOff, aplication stop, change mode 1. unregister wifi
	 * receiver -> donwload false
	 */
	@Override
	public void stopService(Context context) {
		super.stopService(context);
	}

	@Override
	public void getMetaInfo() {
		String ownerIp = WifiDirectWrapper.getInstance().getOwnerIP();

		if (ownerIp != null) {
			executeRunnable(new GetMetaInfoClient(ownerIp, context));
		}
	}

	private OurCallback callback = new OurCallback() {
		@Override
		public void callback() {
			serviceName = context.startService(new Intent(context,
					StudentService.class));
		}
	};

	@Override
	public void download(OurContents content) {
		if (NetworkState.isWifiDirectConnected()) {
			downloadManager.addContent(content);
			downloadManager.executeDownload();
		}
	}

	@Override
	public void cancelDownload(OurContents content) {
		downloadManager.cancelDownload(content);
	}

	private class DownloadManager {
		private HashMap<String, OurContents> downloadCotents;
		private ArrayList<String> contentIdList;
		private ExecutorPair pair;

		public DownloadManager() {
			super();
			pair = null;
			contentIdList = new ArrayList<String>();
			downloadCotents = new HashMap<String, OurContents>();
		}

		public synchronized void addContent(OurContents content) {
			if (content != null) {
				if (downloadCotents.containsKey(content.getId()) == false) {
					contentIdList.add(content.getId());
					downloadCotents.put(content.getId(), content);
				}
			}
		}

		public synchronized void cancelDownload(OurContents content) {
			if (contentIdList.size() > 0 && content != null) {
				if (contentIdList.get(0).equals(content.getId())) {
					if (pair != null) {
						if (pair.future.isDone()) {
							pair = null;

							downloadCotents.remove(content.getId());
							contentIdList.remove(0);

							notiCancel(content.getId());
						} else {
							pair.executor.shutdownNow();
						}
					}
				} else {
					downloadCotents.remove(content.getId());
					contentIdList.remove(content.getId());

					notiCancel(content.getId());
				}
			}
		}

		public void notiCancel(String contentId) {
			Intent intent = new Intent(OurDataChangeReceiver.OUR_DATA_CHANGED);
			intent.putExtra(OurDataChangeReceiver.ACTION,
					OurDataChangeReceiver.ACTION_CANCEL_DOWNLOADING);
			intent.putExtra(OurDataChangeReceiver.CONTENT_ID, contentId);

			context.sendBroadcast(intent);
		}

		public synchronized void executeDownload() {
			if (pair == null && contentIdList.size() > 0) {
				String contentId = contentIdList.get(0);
				OurContents content = downloadCotents.get(contentId);
				if (content != null) {
					CallbackTask downloadTask = new DownloadClient(
							WifiDirectWrapper.getInstance().getOwnerIP(),
							content, context);
					downloadTask.addCallback(new ExecutorCallbackTask(content));
					pair = executeRunnable(downloadTask);
				}else{
					contentIdList.remove(0);
				}
			}
		}
		
		public synchronized void endDownload(OurContents content){
			if(contentIdList.size() > 0){
				contentIdList.remove(content.getId());
				downloadCotents.remove(content.getId());
			}
			pair = null;
			
			if (NetworkState.isWifiDirectConnected()) {
				executeDownload();
			}
		}
		
		private class ExecutorCallbackTask implements OurCallback {
			private OurContents content;

			public ExecutorCallbackTask(OurContents content) {
				super();
				this.content = content;
			}

			@Override
			public void callback() {
				endDownload(content);
			}
		}
	}
}
