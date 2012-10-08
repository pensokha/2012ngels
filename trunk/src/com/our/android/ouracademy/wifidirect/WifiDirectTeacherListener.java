package com.our.android.ouracademy.wifidirect;

import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;

public class WifiDirectTeacherListener extends WifiDirectDefaultListener {
	private static String TAG = "WifiDirectTeacherListener";

	public WifiDirectTeacherListener(WifiP2pManager manager, Channel channel) {
		super(manager, channel);
	}

	@Override
	public void onEnableP2p() {
		manager.createGroup(channel, new TeacherCreateGroupListener());
	}

	@Override
	public void onPeerChanged() {
		//Noting do
	}

	private class TeacherCreateGroupListener implements ActionListener {
		private boolean retry = false;

		@Override
		public void onSuccess() {
			Log.d(TAG, "Success Create Group");
		}

		@Override
		public void onFailure(int reason) {
			if (retry == false) {
				manager.createGroup(channel, this);
				retry = true;
			}else{
				Log.d(TAG, "False Create Group");
			}
		}
	}
}
