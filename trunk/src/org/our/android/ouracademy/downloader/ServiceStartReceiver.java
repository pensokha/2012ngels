package org.our.android.ouracademy.downloader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ServiceStartReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//부팅과 동시에 백그라운드로 서비스 실행.
		Intent i = new Intent(DownloadService.DOWNLOAD_SERVICE);
		context.startService(i);
	}
}
