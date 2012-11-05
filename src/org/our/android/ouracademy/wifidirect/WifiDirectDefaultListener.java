package org.our.android.ouracademy.wifidirect;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;

/**********
 * 
 * @author hyeongseokLim
 * 
 */
public abstract class WifiDirectDefaultListener implements WifiDirectListener {
	private boolean connnect = false;
	protected WifiP2pManager manager;
	protected Channel channel;
	protected Context context;

	public WifiDirectDefaultListener(Context context, WifiP2pManager manager,
			Channel channel) {
		super();
		this.manager = manager;
		this.channel = channel;
		this.context = context;
	}

	@Override
	public void onDisableP2p() {
		Log.d("Test", "onDisableP2p");
	}

	@Override
	public void onConnected() {
		Log.d("Test", "onConnected");
		connnect = true;
	}

	@Override
	public void onDisConnected() {
		Log.d("Test", "onDisConnected");
		connnect = false;
	}

	public boolean getConnected() {
		return connnect;
	}

	@Override
	public void onDeviceInfoChanged() {
		Log.d("Test", "onDeviceInfoChanged");
	}
}
