package org.our.android.ouracademy.service;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.our.android.ouracademy.OurPreferenceManager;
import org.our.android.ouracademy.p2p.P2PServer;
import org.our.android.ouracademy.wifidirect.WifiDirectWrapper;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class OurP2PService extends Service {
	private P2PServer sever;

	private WifiDirectWrapper wifiDirect;
	private OurPreferenceManager pref;

	@Override
	public void onCreate() {
		super.onCreate();

		pref = OurPreferenceManager.getInstance();
		pref.initPreferenceData(this);

		if (pref.isTeacher()) {
			sever = new P2PServer();
			Executor executor = Executors.newSingleThreadExecutor();
			executor.execute(sever);
		}

		wifiDirect = WifiDirectWrapper.getInstance();
		wifiDirect.setService(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		wifiDirect.unsetService(null);

		if (sever != null) {
			sever.stopServer();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
