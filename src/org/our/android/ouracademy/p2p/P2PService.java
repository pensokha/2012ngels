package org.our.android.ouracademy.p2p;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.Log;

public class P2PService extends Service {

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("P2PService", "start!!");
		P2PManager.runServer();
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
