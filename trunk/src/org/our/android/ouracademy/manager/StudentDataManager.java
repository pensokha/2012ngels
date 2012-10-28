package org.our.android.ouracademy.manager;

import android.content.Context;

public class StudentDataManager extends DataManager {
	private static StudentDataManager instance = new StudentDataManager();
	
	public static DataManager getInstance(){
		return instance;
	}

	@Override
	public void onPowerOn(Context context) {
		//Do noting
	}

	@Override
	public void onPowerOff(Context context) {
		// TODO: File 전송 Thread Interrupt
	}

	@Override
	public void startService(Context context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopService(Context context) {
		// TODO Auto-generated method stub
		
	}
	
	
}
