package org.our.android.ouracademy.wifidirect;

import java.util.ArrayList;
import java.util.Collection;

import org.our.android.ouracademy.OurPreferenceManager;
import org.our.android.ouracademy.manager.DataManagerFactory;

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
import android.os.Handler;
import android.util.Log;

/*********
 * 
 * @author hyeongseokLim
 * 
 */
public class WifiDirectStudentListener extends WifiDirectDefaultListener
		implements PeerListListener, ConnectionInfoListener {
	private static final String TAG = "WifiDirectStudentListener";
	private static final int MAX_RETRY_COUNT = 0; // 자동으로 선생님을 찾 하는 기능 제거
	private int retryCount = 0;
	private Handler handler;

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
	public void onEnableP2p() {
		Log.d(TAG, "onEnableP2p");
		if (manager != null) {
			manager.discoverPeers(channel, new DiscoverListener());
		}
	}

	@Override
	public void onPeerChanged() {
		Log.d(TAG, "onPeerChange");
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
	public void onDisConnected() {
		super.onDisConnected();

		WifiDirectWrapper.getInstance().setInfo(null);
		if (manager != null) {
			manager.discoverPeers(channel, new DiscoverListener());
		}
	}

	@Override
	public void onPeersAvailable(WifiP2pDeviceList peers) {
		Log.d(TAG, "onPeersAvailable");
		if (getConnected() == false) {
			Collection<WifiP2pDevice> devices = peers.getDeviceList();

			ArrayList<WifiP2pDevice> groupOwnerDevices = new ArrayList<WifiP2pDevice>();
			for (WifiP2pDevice device : devices) {
				if (device.isGroupOwner()) {
					groupOwnerDevices.add(device);
				}
			}

			Log.d("Test", "FindGroupOwners : " + groupOwnerDevices.size());

			if (OurPreferenceManager.getInstance().isStudent() == true
					&& getConnected() == false) {
				if (groupOwnerDevices.size() == 1) {
					WifiP2pConfig config = new WifiP2pConfig();
					config.deviceAddress = groupOwnerDevices.get(0).deviceAddress;
					config.wps.setup = WpsInfo.PBC;

					manager.connect(channel, config,
							new StudenetConnectListener(config));
				} else if (groupOwnerDevices.size() == 0
						&& retryCount <= MAX_RETRY_COUNT) {
					if (handler == null) {
						retryCount++;

						handler = new Handler();
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								handler = null;
								if (getConnected() == true && manager != null
										&& channel != null) {
									manager.discoverPeers(channel,
											new DiscoverListener());
								}
							}
						}, 30000); // 5 Minute
					}
				}

				if (groupOwnerDevices.size() > 0) {
					retryCount = 0;
				}
			}
		}
	}

	/**********
	 * 
	 * @author hyeongseokLim
	 * 
	 */
	private class StudenetConnectListener implements ActionListener {
		private boolean retry = false;
		private WifiP2pConfig config;

		public StudenetConnectListener(WifiP2pConfig config) {
			this.config = config;
		}

		@Override
		public void onSuccess() {
			Log.d(TAG, "Success Connect Group");

			manager.requestConnectionInfo(channel,
					WifiDirectStudentListener.this);
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
		Log.d(TAG, "ConnectionInfoAvailable : " + info);
		WifiDirectWrapper.getInstance().setInfo(info);

		DataManagerFactory.getDataManager().getMetaInfo();
	}
}
