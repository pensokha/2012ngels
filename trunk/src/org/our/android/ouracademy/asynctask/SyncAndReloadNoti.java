package org.our.android.ouracademy.asynctask;

import org.our.android.ouracademy.ui.pages.MainActivity.OurDataChangeReceiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SyncAndReloadNoti extends SyncFileAndDatabase {
	public SyncAndReloadNoti(Context context) {
		super(context);
	}
	
	@Override
	public void proceed() {
		super.proceed();
		
		Log.d("Test", "Send BroadCast");
		Intent intent = new Intent(OurDataChangeReceiver.OUR_DATA_CHANGED);
		intent.putExtra(OurDataChangeReceiver.ACTION, OurDataChangeReceiver.ACTION_RELOAD);
		context.sendBroadcast(intent);
	}
}
