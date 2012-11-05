package org.our.android.ouracademy.service;

import android.content.Intent;

public class StudentService extends OurService {

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return START_NOT_STICKY;
	}
	
}
