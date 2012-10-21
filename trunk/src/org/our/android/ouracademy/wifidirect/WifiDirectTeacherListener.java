package org.our.android.ouracademy.wifidirect;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;

/********
 * 
 * @author hyeongseokLim
 * 
 */
public class WifiDirectTeacherListener extends WifiDirectDefaultListener {
	private static final String TAG = "WifiDirectTeacherListener";

	public WifiDirectTeacherListener(Context context, WifiP2pManager manager,
			Channel channel) {
		super(context, manager, channel);
	}

	@Override
	public void onEnableP2p() {
		manager.createGroup(channel, new TeacherCreateGroupListener());
	}

	@Override
	public void onPeerChanged() {
		// Noting do
	}

	/********
	 * 
	 * @author hyeongseokLim
	 * 
	 */
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
			} else {
				Log.d(TAG, "False Create Group");
			}
		}
	}
}
