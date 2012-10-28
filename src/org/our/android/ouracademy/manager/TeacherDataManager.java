package org.our.android.ouracademy.manager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.our.android.ouracademy.asynctask.GetMetaInfoFromFSI;
import org.our.android.ouracademy.asynctask.TeacherSyncTask;
import org.our.android.ouracademy.asynctask.TeacherSyncTask.TeacherSyncCallback;
import org.our.android.ouracademy.p2p.P2PService;
import org.our.android.ouracademy.wifidirect.WifiDirectWrapper;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

public class TeacherDataManager extends DataManager {
	private static TeacherDataManager instance = new TeacherDataManager();

	private ExecutorService initExcutor;
	private Future<?> initFuture;
	private Context context;
	private ComponentName serviceName;
	private final Handler initHandler = new Handler();

	private boolean reqStop = false;

	public static DataManager getInstance() {
		return instance;
	}

	@Override
	public void onPowerOn(Context context) {
		startService(context);
	}

	@Override
	public void onPowerOff(Context context) {
		stopService(context);
	}

	/********
	 * When : onPowerOn, ChangeMode Start Service 1. enableWifi : FSI connect,
	 * get MetaInfo, Db update 2. sync file and db 3. start service, register
	 * wifi receiver, make wifi group
	 */
	@Override
	public void startService(Context ctx) {
		context = ctx;
		reqStop = false; 

		ConnectivityManager cManager;
		NetworkInfo wifi;
		cManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		initExcutor = Executors.newSingleThreadExecutor();
		Runnable asyncTask;

		TeacherSyncCallback callback = new TeacherSyncCallback() {
			@Override
			public void endSync() {
				// start service, register wifi receiver, make wifi group
				if (reqStop == false) {
					serviceName = context.startService(new Intent(context,
							P2PService.class));
					WifiDirectWrapper.getInstance().setService();
				}
			}
		};

		if (wifi.isConnected()) {
			asyncTask = new GetMetaInfoFromFSI(context, initHandler, callback);
		} else {
			asyncTask = new TeacherSyncTask(context, initHandler, callback);
		}
		initFuture = initExcutor.submit(asyncTask);
	}

	/*********
	 * When : onPowerOff, ChangeMode Stop Service 1. If StartTask is running,
	 * Stop 2. Download File From Youtube Stop 3. service stop, remove wifi
	 * group, unregister wifi receiver
	 */
	@Override
	public void stopService(Context context) {
		reqStop = true;
		if (initFuture.isDone() == false) {
			initExcutor.shutdownNow();
		}

		if (serviceName != null) {
			Intent intent = new Intent();
			intent.setComponent(serviceName);

			context.stopService(intent);
		}

		WifiDirectWrapper.getInstance().unsetService(null);
	}
}
