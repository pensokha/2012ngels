package org.our.android.ouracademy.wifidirect;

public interface WifiDirectListener {
	public void onEnableP2p();
	public void onDisableP2p();
	public void onConnected();
	public void onDisConnected();
	public void onDeviceInfoChanged();
	public void onPeerChanged();
}
