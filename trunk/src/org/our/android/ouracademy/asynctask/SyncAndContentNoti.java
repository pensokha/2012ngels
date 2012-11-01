package org.our.android.ouracademy.asynctask;

import org.our.android.ouracademy.ui.pages.MainActivity.OurDataChangeReceiver;

import android.content.Context;
import android.content.Intent;

public class SyncAndContentNoti extends SyncFileAndDatabase {

	public SyncAndContentNoti(Context context) {
		super(context);
	}

	@Override
	public void proceed() {
		super.proceed();
		
		Intent intent = new Intent(OurDataChangeReceiver.OUR_DATA_CHANGED);
		intent.putExtra(OurDataChangeReceiver.ACTION, OurDataChangeReceiver.ACTION_CONTENT_CHANGED);
		context.sendBroadcast(intent);
	}
}
