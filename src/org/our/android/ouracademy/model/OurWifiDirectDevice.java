package org.our.android.ouracademy.model;

import android.net.wifi.p2p.WifiP2pDevice;

public class OurWifiDirectDevice {
	public static final int STATE_CONNECTED = 0;
	public static final int STATE_DISCONNECTED = 1;
	public static final int STATE_CONNECTING = 2;
	
	public int connectingState = STATE_DISCONNECTED;
	public WifiP2pDevice device;

	public OurWifiDirectDevice() {
		super();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OurWifiDirectDevice [connectingState=");
		builder.append(connectingState);
		builder.append(", device=");
		builder.append(device);
		builder.append("]");
		return builder.toString();
	}
}
