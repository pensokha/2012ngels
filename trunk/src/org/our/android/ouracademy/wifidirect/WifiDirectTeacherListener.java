package org.our.android.ouracademy.wifidirect;

import java.util.Collection;

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
		Log.d("Test", "onPeerChanged");
		
		if(manager != null){
			manager.requestPeers(channel, new PeerListListener() {
				
				@Override
				public void onPeersAvailable(WifiP2pDeviceList peers) {
					Collection<WifiP2pDevice> devices = peers.getDeviceList();
					
					int connected_count = 0;
					for(WifiP2pDevice device : devices){
						if(device.status == WifiP2pDevice.CONNECTED){
							connected_count++;
						}
					}
					
					WifiDirectWrapper.getInstance().startRegistration(connected_count);
				}
				
			});
		}
	}

	/********
	 * 
	 * @author hyeongseokLim
	 * 
	 */
	private class TeacherCreateGroupListener implements ActionListener {
		private static final int MAX_RETRY_COUNT = 10;
		private int retry = 0;
		
		private ActionListener removeListener = new ActionListener() {
			
			@Override
			public void onSuccess() {
				manager.createGroup(channel, new ActionListener() {
					
					@Override
					public void onSuccess() {
						Log.d(TAG, "Success Create Group");
						WifiDirectWrapper.getInstance().startRegistration(0);
					}
					
					@Override
					public void onFailure(int reason) {
						if(retry < MAX_RETRY_COUNT){
							retry++;
							manager.createGroup(channel, TeacherCreateGroupListener.this);
						}
					}
				});
			}
			
			@Override
			public void onFailure(int reason) {
				if(retry < MAX_RETRY_COUNT){
					retry--;
					manager.removeGroup(channel, removeListener);
				}
			}
		};
		
		@Override
		public void onSuccess() {
			Log.d(TAG, "Success Create Group");
			WifiDirectWrapper.getInstance().startRegistration(0);
		}
		
		@Override
		public void onFailure(int reason) {
			manager.requestConnectionInfo(channel, new ConnectionInfoListener() {
				@Override
				public void onConnectionInfoAvailable(WifiP2pInfo info) {
					if(info.isGroupOwner == false){
						manager.removeGroup(channel, removeListener);
					}
				}
			});
		}
	}
}
