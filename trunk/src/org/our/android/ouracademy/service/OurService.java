package org.our.android.ouracademy.service;

import org.our.android.ouracademy.wifidirect.WifiDirectWrapper;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class OurService extends Service {

	private WifiDirectWrapper wifiDirect;

	@Override
	public void onCreate() {
		super.onCreate();
		
		wifiDirect = WifiDirectWrapper.getInstance();
		wifiDirect.setService(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		wifiDirect.unsetService(null);
	}

}
