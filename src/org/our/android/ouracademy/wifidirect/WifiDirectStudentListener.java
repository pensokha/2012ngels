package org.our.android.ouracademy.wifidirect;

import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.our.android.ouracademy.p2p.P2PClient;

import android.content.Context;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

public class WifiDirectStudentListener extends WifiDirectDefaultListener
		implements PeerListListener, ConnectionInfoListener {
	private static String TAG = "WifiDirectStudentListener";

	public WifiDirectStudentListener(Context context, WifiP2pManager manager, Channel channel) {
		super(context, manager, channel);
	}

	@Override
	public void onEnableP2p() {
		if (manager != null) {
			manager.discoverPeers(channel, new ActionListener() {
				private boolean retry = false;

				@Override
				public void onSuccess() {
					Log.d(TAG, "DiscoverPeers Success");
				}

				@Override
				public void onFailure(int reason) {
					if (retry == false) {
						manager.discoverPeers(channel, this);
						retry = true;
					} else {
						Log.d(TAG, "DiscoverPeers False");
					}
				}
			});
		}
	}

	@Override
	public void onPeerChanged() {
		if (manager != null && getConnected() == false) {
			manager.requestPeers(channel, this);
		}
	}

	@Override
	public void onConnected() {
		super.onConnected();

		manager.requestConnectionInfo(channel, this);
	}

	@Override
	public void onPeersAvailable(WifiP2pDeviceList peers) {
		if (getConnected() == false) {
			Collection<WifiP2pDevice> devices = peers.getDeviceList();
			for (WifiP2pDevice device : devices) {
				if (device.isGroupOwner()) {
					WifiP2pConfig config = new WifiP2pConfig();
					config.deviceAddress = device.deviceAddress;
					config.wps.setup = WpsInfo.PBC;

					manager.connect(channel, config,
							new StudenetConnectListener(config));
					return;
				}
			}
			Log.d(TAG, "Can't find group owner(teacher)");
			// TODO : Thread로 일정 시간후 다시 discover를 시도해야 된다.
		}
	}

	private class StudenetConnectListener implements ActionListener {
		private boolean retry = false;
		private WifiP2pConfig config;

		public StudenetConnectListener(WifiP2pConfig config) {
			this.config = config;
		}

		@Override
		public void onSuccess() {
			Log.d(TAG, "Success Connect Group");
		}

		@Override
		public void onFailure(int reason) {
			if (retry == false) {
				manager.connect(channel, config, this);
				retry = true;
			} else {
				Log.d(TAG, "False Connect Group");
			}
		}
	}

	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {
		Executor executor = Executors.newSingleThreadExecutor();
		executor.execute(new P2PClient(context, info.groupOwnerAddress.getHostAddress()));
	}
}
