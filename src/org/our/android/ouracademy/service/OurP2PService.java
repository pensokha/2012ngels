package org.our.android.ouracademy.service;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.our.android.ouracademy.OurPreferenceManager;
import org.our.android.ouracademy.p2p.P2PServer;
import org.our.android.ouracademy.wifidirect.WifiDirectWrapper;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class OurP2PService extends Service {
	private final int SERVER_THREAD_POOL_COUNT = 5;
	private Executor executor = Executors.newFixedThreadPool(SERVER_THREAD_POOL_COUNT);
	private ArrayList<P2PServer> serverPool = new ArrayList<P2PServer>();

	private WifiDirectWrapper wifiDirect;
	private OurPreferenceManager pref;

	@Override
	public void onCreate() {
		super.onCreate();

		pref = OurPreferenceManager.getInstance();
		pref.initPreferenceData(this);

		if (pref.isTeacher()) {
			serverPool.clear();
			P2PServer server;
			for(int i = 0; i < SERVER_THREAD_POOL_COUNT; i ++){
				server = new P2PServer();
				serverPool.add(server);
				executor.execute(server);
			}
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

		if (serverPool != null) {
			for(P2PServer server : serverPool)
				server.stopServer();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
