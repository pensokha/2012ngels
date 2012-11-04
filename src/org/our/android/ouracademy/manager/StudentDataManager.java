package org.our.android.ouracademy.manager;

import java.util.HashMap;

import org.our.android.ouracademy.asynctask.CallbackTask;
import org.our.android.ouracademy.asynctask.CallbackTask.OurCallback;
import org.our.android.ouracademy.asynctask.SyncAndContentNoti;
import org.our.android.ouracademy.model.OurContents;
import org.our.android.ouracademy.p2p.client.DownloadClient;
import org.our.android.ouracademy.p2p.client.GetMetaInfoClient;
import org.our.android.ouracademy.service.StudentService;
import org.our.android.ouracademy.wifidirect.WifiDirectWrapper;

import android.content.Context;
import android.content.Intent;

public class StudentDataManager extends DataManager {
	private static HashMap<String, ExecutorPair> downloadMap = new HashMap<String, ExecutorPair>();
	private static StudentDataManager instance = new StudentDataManager();

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
	public void startService(Context context) {
		super.startService(context);

		CallbackTask syncAndContentNoti = new SyncAndContentNoti(context);
		syncAndContentNoti.addCallback(callback);
		executeRunnable(syncAndContentNoti);
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
		CallbackTask downloadTask = new DownloadClient(WifiDirectWrapper
				.getInstance().getOwnerIP(), content, context);
		downloadTask.addCallback(new ExecutorCallbackTask(content));
		ExecutorPair pair = executeRunnable(downloadTask);

		synchronized (downloadMap) {
			downloadMap.put(content.getId(), pair);
		}
	}

	@Override
	public void cancleDownload(OurContents content) {
		synchronized (downloadMap) {
			ExecutorPair pair = downloadMap.get(content.getId());
			if (pair != null) {
				if (pair.future.isDone()) {
					pair.executor.shutdownNow();
				} else {
					downloadMap.remove(content.getId());
				}
			}
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
			synchronized (downloadMap) {
				downloadMap.remove(content.getId());
			}
		}
	}
}
