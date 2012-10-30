package org.our.android.ouracademy.manager;

import android.content.Context;



public abstract class DataManager {
	protected Context context;
	
	public void syncFileAndDatabase(){
		
	}
	
	abstract public void getMetaInfo();
	
	public void startService(Context context){
		this.context = context;
	}
	abstract public void stopService(Context context);
	
	abstract public void onPowerOn(Context context);
	abstract public void onPowerOff(Context context);
}
