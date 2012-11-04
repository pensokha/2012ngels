package org.our.android.ouracademy.manager;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.our.android.ouracademy.OurPreferenceManager;
import org.our.android.ouracademy.asynctask.CallbackTask;
import org.our.android.ouracademy.asynctask.CallbackTask.OurCallback;
import org.our.android.ouracademy.model.OurContents;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public abstract class DataManager {
	private ArrayList<ExecutorPair> taskList = new ArrayList<DataManager.ExecutorPair>();
	protected Context context;
	
	protected ComponentName serviceName;

	abstract public void getMetaInfo();

	public void startService(Context context) {
		Toast.makeText(
				context,
				"StartService"
						+ (OurPreferenceManager.getInstance().isTeacher() ? "Teacher"
								: "Student"), Toast.LENGTH_SHORT).show();
		this.context = context;
	}

	public void stopService(Context context) {
		Toast.makeText(
				context,
				"StopService"
						+ (OurPreferenceManager.getInstance().isTeacher() ? "Teacher"
								: "Student"), Toast.LENGTH_SHORT).show();
		for (ExecutorPair pair : taskList) {
			if (pair.future.isDone() == false) {
				pair.executor.shutdownNow();
			}
		}
		
		if (serviceName != null) {
			Intent intent = new Intent();
			intent.setComponent(serviceName);

			context.stopService(intent);
		}
	}

	abstract public void onPowerOn(Context context);

	abstract public void onPowerOff(Context context);

	abstract public void download(OurContents content);

	abstract public void cancleDownload(OurContents content);

	public ExecutorPair executeRunnable(CallbackTask task) {
		Log.d("Test", "executeRunnable1");
		ExecutorPair pair = new ExecutorPair();

		synchronized (taskList) {
			taskList.add(pair);
		}

		task.addCallback(new ExecutorCallbackTask(pair));

		pair.executor = Executors.newSingleThreadExecutor();
		pair.future = pair.executor.submit(task);

		Log.d("Test", "executeRunnable2");
		
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
