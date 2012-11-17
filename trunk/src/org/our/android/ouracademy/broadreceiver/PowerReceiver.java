package org.our.android.ouracademy.broadreceiver;

import org.our.android.ouracademy.manager.DataManager;
import org.our.android.ouracademy.manager.DataManagerFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PowerReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		
		DataManager dataManager = DataManagerFactory.getDataManager();
		if (action.equals(Intent.ACTION_REBOOT)
				|| action.equals(Intent.ACTION_SHUTDOWN)) {
			dataManager.onPowerOff(context);
		} else if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			dataManager.onPowerOn(context);
		} else {
			//Do noting
		}
	}

}
