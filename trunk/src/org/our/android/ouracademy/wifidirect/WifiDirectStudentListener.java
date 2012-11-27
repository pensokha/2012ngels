package org.our.android.ouracademy.wifidirect;

import java.util.Collection;
import java.util.HashMap;

import org.our.android.ouracademy.manager.DataManagerFactory;
import org.our.android.ouracademy.wifidirect.WifiDirectWrapper.FindDeviceListener;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

/*********
 * 
 * @author hyeongseokLim
 * 
 */
public class WifiDirectStudentListener extends WifiDirectDefaultListener
		implements PeerListListener, ConnectionInfoListener {
	private static final String TAG = "WifiDirectStudentListener";
	private FindDeviceListener foundListener;

	public WifiDirectStudentListener(Context context, WifiP2pManager manager,
			Channel channel) {
		super(context, manager, channel);
	}

	/******
	 * 
	 * @author hyeongseokLim
	 * 
	 */
	public class DiscoverListener implements ActionListener {

		@Override
		public void onSuccess() {
			Log.d(TAG, "DiscoverPeers Success");
			manager.requestPeers(channel, WifiDirectStudentListener.this);
		}

		@Override
		public void onFailure(int reason) {
			Log.d(TAG, "DiscoverPeers False");
		}
	}

	@Override
	public void onPeerChanged() {
		Log.d(TAG, "onPeerChange");
		if (manager != null) {
			manager.requestPeers(channel, this);
		}
	}

	public void setFoundListener(FindDeviceListener foundListener) {
		this.foundListener = foundListener;
	}

	@Override
	public void onConnected() {
		super.onConnected();

		manager.requestConnectionInfo(channel, this);
	}

	@Override
	public void onDisConnected() {
		super.onDisConnected();

		WifiDirectWrapper.getInstance().setInfo(null);
		if (manager != null) {
			manager.discoverPeers(channel, new DiscoverListener());
		}
	}

	@Override
	public void onPeersAvailable(WifiP2pDeviceList peers) {
		Log.d(TAG, "onPeersAvailable :" + peers.toString());
		Collection<WifiP2pDevice> devices = peers.getDeviceList();

		HashMap<String, WifiP2pDevice> groupOwnerDevices = new HashMap<String, WifiP2pDevice>();
		for (WifiP2pDevice device : devices) {
			// if (device.isGroupOwner()) {
			groupOwnerDevices.put(device.deviceAddress, device);
			// }
		}

		if (foundListener != null) {
			foundListener.onFindDevice(groupOwnerDevices);
		}
	}

	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {
		Log.d(TAG, "ConnectionInfoAvailable : " + info);
		WifiDirectWrapper.getInstance().setInfo(info);

		DataManagerFactory.getDataManager().getMetaInfo();
	}
}
