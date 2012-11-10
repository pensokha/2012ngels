package org.our.android.ouracademy.service;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.our.android.ouracademy.p2p.P2PServer;

import android.content.Intent;
import android.util.Log;

public class TeacherService extends OurService {
	private P2PServer sever;
		
	@Override
	public void onCreate() {
		super.onCreate();
		
		Log.d("Test", "StartTeacherService");
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
		
		if(sever != null){
			sever.stopServer();
		}
		Log.d("Test", "StopTeacherService");
	}	
}