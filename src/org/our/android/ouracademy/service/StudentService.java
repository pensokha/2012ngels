package org.our.android.ouracademy.service;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

public class StudentService extends OurService {

	@Override
	public void onCreate() {
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wifi.disconnect();
		
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return START_NOT_STICKY;
	}
	
}
