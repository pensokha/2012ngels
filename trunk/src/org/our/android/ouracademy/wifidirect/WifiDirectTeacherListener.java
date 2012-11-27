package org.our.android.ouracademy.wifidirect;

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

/********
 * 
 * @author hyeongseokLim
 * 
 */
public class WifiDirectTeacherListener extends WifiDirectDefaultListener {
	private static final String TAG = "WifiDirectTeacherListener";
	private Timer timerDiscover;
	private ActionListener discoverPeerListener = new ActionListener() {

		@Override
		public void onSuccess() {
			Log.d(TAG, "discoverPeers onSuccess");
		}

		@Override
		public void onFailure(int reason) {
			Log.d(TAG, "discoverPeers onFailure");
		}
	};

	public WifiDirectTeacherListener(Context context, WifiP2pManager manager,
			Channel channel) {
		super(context, manager, channel);
	}

	@Override
	public void onEnableP2p() {
		super.onEnableP2p();

		if (timerDiscover == null) {
			timerDiscover = new Timer();
			timerDiscover.schedule(new TimerTask() {
				@Override
				public void run() {
					manager.stopPeerDiscovery(channel, new ActionListener() {
						
						@Override
						public void onSuccess() {
							manager.discoverPeers(channel, discoverPeerListener);
						}
						
						@Override
						public void onFailure(int reason) {
							manager.discoverPeers(channel, discoverPeerListener);
						}
					});
				}
			}, 0, 15000);
		}
	}

	@Override
	public void onDisableP2p() {
		super.onDisableP2p();
		
		if(timerDiscover != null)
			timerDiscover.purge();
	}

	@Override
	public void onPeerChanged() {
		Log.d("Test", "onPeerChanged");

		if (manager != null) {
			manager.requestPeers(channel, new PeerListListener() {

				@Override
				public void onPeersAvailable(WifiP2pDeviceList peers) {
					Collection<WifiP2pDevice> devices = peers.getDeviceList();

					int connected_count = 0;
					for (WifiP2pDevice device : devices) {
						if (device.status == WifiP2pDevice.CONNECTED) {
							connected_count++;
						}
					}

					WifiDirectWrapper.getInstance().startRegistration(
							connected_count);
				}

			});
		}
	}
}
