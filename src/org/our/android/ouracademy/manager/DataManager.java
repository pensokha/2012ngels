package org.our.android.ouracademy.manager;

import android.content.Context;



public abstract class DataManager {
	public void syncFileAndDatabase(){
		
	}
	
	abstract public void startService(Context context);
	abstract public void stopService(Context context);
	
	abstract public void onPowerOn(Context context);
	abstract public void onPowerOff(Context context);
}
