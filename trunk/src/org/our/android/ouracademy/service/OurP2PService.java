package org.our.android.ouracademy.service;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.our.android.ouracademy.OurPreferenceManager;
import org.our.android.ouracademy.p2p.P2PServer;
import org.our.android.ouracademy.wifidirect.WifiDirectWrapper;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;

public class OurP2PService extends Service {
	private P2PServer sever;

	private WifiDirectWrapper wifiDirect;

	@Override
	public void onCreate() {
		super.onCreate();

		if(OurPreferenceManager.getInstance().isStudent()){
			WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			wifi.disconnect();
		}
		
		wifiDirect = WifiDirectWrapper.getInstance();
		wifiDirect.setService(this);
		
		sever = new P2PServer();
		Executor executor = Executors.newSingleThreadExecutor();
		executor.execute(sever);
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
		
		if(sever != null){
			sever.stopServer();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
