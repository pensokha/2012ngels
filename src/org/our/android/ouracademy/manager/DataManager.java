package org.our.android.ouracademy.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.our.android.ouracademy.asynctask.CallbackTask;
import org.our.android.ouracademy.asynctask.GetMetaInfoFromMetaFile;
import org.our.android.ouracademy.asynctask.CallbackTask.OurCallback;
import org.our.android.ouracademy.asynctask.SyncAndContentNoti;
import org.our.android.ouracademy.model.OurContents;
import org.our.android.ouracademy.service.OurP2PService;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public abstract class DataManager {
	private static final String TAG = "DataManager";
	
	private ArrayList<ExecutorPair> taskList = new ArrayList<DataManager.ExecutorPair>();
	protected Context context;
	protected boolean started = false;

	protected ComponentName serviceName;

	abstract public void getMetaInfo();

	public void syncFileAndDatabase() {
		if (context != null) {
			executeRunnable(new SyncAndContentNoti(context));
		}
	}

	public synchronized void startService(Context context) {
		if (this.started == false) {
			this.started = true;
			this.context = context;

			CallbackTask syncAndContentNoti = new GetMetaInfoFromMetaFile(
					context);
			syncAndContentNoti.addCallback(startServiceCallback);
			executeRunnable(syncAndContentNoti);
		}
		
		Log.d(TAG, "started : " + started);
	}

	public void startOnlyService() {
		Log.d(TAG, "Started!!!" + serviceName);
		if (serviceName == null) {
			serviceName = context.startService(new Intent(context,
					OurP2PService.class));
		}
	}

	private OurCallback startServiceCallback = new OurCallback() {
		@Override
		public void callback() {
			startOnlyService();
		}
	};

	public synchronized void stopService(Context context) {
		for (ExecutorPair pair : taskList) {
			if (pair.future.isDone() == false) {
				pair.executor.shutdownNow();
			}
		}

		// Stop Service
		if (serviceName != null) {
			Intent intent = new Intent();
			intent.setComponent(serviceName);

			context.stopService(intent);
		} else {
			final ActivityManager am = (ActivityManager) context
					.getSystemService(Activity.ACTIVITY_SERVICE);

			List<RunningServiceInfo> serviceList = am.getRunningServices(100);
			for (RunningServiceInfo service : serviceList) {
				if (service.pid == android.os.Process.myPid()) {
					Intent stop = new Intent();
					stop.setComponent(service.service);
					context.stopService(stop);
				}
			}
		}
		this.context = null;
		this.started = false;
	}

	public boolean isStarted() {
		return started;
	}

	public void onPowerOn(Context context) {
		this.context = context;

		startOnlyService();
	}

	public void onPowerOff(Context context) {
		stopService(context);
	}

	abstract public void download(OurContents content);

	abstract public void cancelDownload(OurContents content);

	public ExecutorPair executeRunnable(CallbackTask task) {
		Log.d(TAG, "executeRunnable : " + task.getClass().getName());

		ExecutorPair pair = new ExecutorPair();

		synchronized (taskList) {
			taskList.add(pair);
		}

		task.addCallback(new ExecutorCallbackTask(pair));

		pair.executor = Executors.newSingleThreadExecutor();
		pair.future = pair.executor.submit(task);

		return pair;
	}

	private class ExecutorCallbackTask implements OurCallback {
		private ExecutorPair pair;

		public ExecutorCallbackTask(ExecutorPair pair) {
			super();
			this.pair = pair;
		}

		@Override
		public void callback() {
			synchronized (taskList) {
				taskList.remove(this.pair);
			}
		}
	}

	public class ExecutorPair {
		public ExecutorService executor;
		public Future<?> future;
	}
}
