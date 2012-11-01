package org.our.android.ouracademy.manager;

import org.our.android.ouracademy.asynctask.CallbackTask;
import org.our.android.ouracademy.asynctask.CallbackTask.OurCallback;
import org.our.android.ouracademy.asynctask.GetMetaInfoFromFSI;
import org.our.android.ouracademy.asynctask.SyncAndContentNoti;
import org.our.android.ouracademy.model.OurContents;
import org.our.android.ouracademy.p2p.P2PService;
import org.our.android.ouracademy.wifidirect.WifiDirectWrapper;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class TeacherDataManager extends DataManager {
	private static TeacherDataManager instance = new TeacherDataManager();

	private ComponentName serviceName;

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
	 * When : onPowerOn, ChangeMode, start application
	 * 1. enableWifi : FSI connect, get MetaInfo, Db update 
	 * 2. sync file and db 
	 * 3. start service, register wifi receiver, make wifi group
	 */
	@Override
	public void startService(Context ctx) {
		super.startService(ctx);

		ConnectivityManager cManager;
		NetworkInfo wifi;
		cManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (wifi.isConnected()) {
			getMetaInfo();
		} else {
			CallbackTask syncAndContentNoti = new SyncAndContentNoti(context);
			syncAndContentNoti.addCallback(callback);
			executeRunnable(syncAndContentNoti);
		}
	}

	/*********
	 * When : onPowerOff, ChangeMode Stop Service 
	 * 1. If StartTask is running, Stop 
	 * 2. Download File From Youtube Stop 
	 * 3. service stop, remove wifi group, unregister wifi receiver
	 */
	@Override
	public void stopService(Context context) {
		super.stopService(context);

		if (serviceName != null) {
			Intent intent = new Intent();
			intent.setComponent(serviceName);

			context.stopService(intent);
		}

		WifiDirectWrapper.getInstance().unsetService(null);
	}

	@Override
	public void getMetaInfo() {
		CallbackTask syncAndContentNoti = new GetMetaInfoFromFSI(context);
		syncAndContentNoti.addCallback(callback);
		executeRunnable(syncAndContentNoti);
	}
	
	private OurCallback callback = new OurCallback() {
		@Override
		public void callback() {
			serviceName = context.startService(new Intent(context,
					P2PService.class));
			WifiDirectWrapper.getInstance().setService();
		}
	};

	@Override
	public void download(OurContents content) {
		//download from youtube
	}

	@Override
	public void cancleDownload(OurContents content) {
		// TODO Auto-generated method stub
		
	}
}