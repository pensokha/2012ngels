package org.our.android.ouracademy.manager;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.our.android.ouracademy.p2p.client.GetMetaInfoClient;
import org.our.android.ouracademy.wifidirect.WifiDirectWrapper;

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
		stopService(context);
	}

	/*******
	 * When : start main page, change mode
	 * 1. register wifi receiver, find teacher
	 * 2. if find teacher, get meta info
	 * 3. sync file and db 
	 */
	@Override
	public void startService(Context context) {
		super.startService(context);
		
		WifiDirectWrapper.getInstance().setService();
	}

	/**********
	 * When : onPowerOff, aplication stop, change mode
	 * 1. unregister wifi receiver -> donwload false
	 */
	@Override
	public void stopService(Context context) {
		WifiDirectWrapper.getInstance().unsetService(null);
	}

	@Override
	public void getMetaInfo() {
		String ownerIp = WifiDirectWrapper.getInstance().getOwnerIP();
		
		if(ownerIp != null){
			Executor executor = Executors.newSingleThreadExecutor();
			executor.execute(new GetMetaInfoClient(ownerIp, context));
		}
	}
}
