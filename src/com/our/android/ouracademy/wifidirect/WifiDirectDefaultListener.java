package com.our.android.ouracademy.wifidirect;

import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;

public abstract class WifiDirectDefaultListener implements WifiDirectListener {
	private boolean connnect = false;
	protected WifiP2pManager manager;
	protected Channel channel;
	
	public WifiDirectDefaultListener(WifiP2pManager manager, Channel channel) {
		super();
		this.manager = manager;
		this.channel = channel;
	}

	@Override
	public void onDisableP2p() {
		// TODO Auto-generated method stub
		// Dialog PopUp
		// yes - config page intent, no - no action
	}

	@Override
	public void onConnected() {
		connnect = true;
	}

	@Override
	public void onDisConnected() {
		connnect = false;
	}
	
	public boolean getConnected() {
		return connnect;
	}

	@Override
	public void onDeviceInfoChanged() {
	}
}
