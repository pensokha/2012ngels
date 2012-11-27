package org.our.android.ouracademy.manager;

import java.util.ArrayList;
import java.util.HashMap;

import org.our.android.ouracademy.asynctask.CallbackTask;
import org.our.android.ouracademy.asynctask.CallbackTask.OurCallback;
import org.our.android.ouracademy.model.OurContents;
import org.our.android.ouracademy.p2p.client.DownloadClient;
import org.our.android.ouracademy.p2p.client.GetExistingContentsClient;
import org.our.android.ouracademy.p2p.client.GetExistingContentsClient.GetExistingContentsListener;
import org.our.android.ouracademy.p2p.client.GetMetaInfoClient;
import org.our.android.ouracademy.ui.pages.MainActivity.OurDataChangeReceiver;
import org.our.android.ouracademy.util.NetworkState;
import org.our.android.ouracademy.wifidirect.WifiDirectWrapper;

import android.content.Intent;

public class StudentDataManager extends DataManager {
	private static StudentDataManager instance = new StudentDataManager();
	private DownloadManager downloadManager = new DownloadManager();

	public static DataManager getInstance() {
		return instance;
	}

	@Override
	public void getMetaInfo() {
		String ownerIp = WifiDirectWrapper.getInstance().getOwnerIP();

		if (ownerIp != null) {
			executeRunnable(new GetMetaInfoClient(ownerIp, context));
		}
	}

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

	public void getTeacherContents(GetExistingContentsListener listener) {
		String ownerIp = WifiDirectWrapper.getInstance().getOwnerIP();

		if (ownerIp != null) {
			executeRunnable(new GetExistingContentsClient(ownerIp, listener));
		}
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
			if (content != null) {
				if (contentIdList.size() > 0) {
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
				}else{
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
				} else {
					contentIdList.remove(0);
				}
			}
		}

		public synchronized void endDownload(OurContents content) {
			if (contentIdList.size() > 0) {
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
