package org.our.android.ouracademy.wifidirect;

import java.util.ArrayList;
import java.util.Collection;

import org.our.android.ouracademy.OurPreferenceManager;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.widget.Toast;

/*********
 * 
 * @author hyeongseokLim
 * 
 */
public class WifiDirectWrapper {

	private static WifiDirectWrapper instance = new WifiDirectWrapper();

	private final IntentFilter intentFilter = new IntentFilter();
	private WifiP2pManager manager;
	private WifiP2pInfo info;
	private Channel channel;
	private Context context;

	private WrapperChannelListener channelListener = new WrapperChannelListener();
	private WiFiDirectBroadcastReceiver receiver;

	private WifiDirectWrapper() {
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		intentFilter
				.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		intentFilter
				.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
	};

	public static WifiDirectWrapper getInstance() {
		return instance;
	}

	public void init(Context context) {
		this.context = context;
		manager = (WifiP2pManager) context
				.getSystemService(Context.WIFI_P2P_SERVICE);
		if (manager != null) {
			channel = manager.initialize(context, context.getMainLooper(),
					channelListener);
		}
	}

	public boolean isInitSuccess() {
		if (manager == null || channel == null) {
			return false;
		} else {
			return true;
		}
	}

	public WifiP2pInfo getInfo() {
		return info;
	}

	public synchronized void setInfo(WifiP2pInfo info) {
		this.info = info;
	}

	public String getOwnerIP() {
		if (info != null && info.groupOwnerAddress != null) {
			return info.groupOwnerAddress.getHostAddress();
		} else {
			return null;
		}
	}
	
	public void findConnectedStudent(final FindDeviceListner listener){
		manager.requestPeers(channel, new PeerListListener() {
			@Override
			public void onPeersAvailable(WifiP2pDeviceList peers) {
				Collection<WifiP2pDevice> devices = peers.getDeviceList();
				ArrayList<WifiP2pDevice> connectedStudents = new ArrayList<WifiP2pDevice>();
				for (WifiP2pDevice device : devices) {
					if(device.status == WifiP2pDevice.CONNECTED){
						connectedStudents.add(device);
					}
				}
				listener.onFindDevice(connectedStudents);
			}
		});
	}
	
	public interface FindDeviceListner{
		public void onFindDevice(ArrayList<WifiP2pDevice> devices);
	}

	public void discoverPeers() {
		manager.discoverPeers(channel, null);
	}

	private WifiDirectDefaultListener getWifiDirectListner() {
		if (OurPreferenceManager.getInstance().isTeacher()) {
			return new WifiDirectTeacherListener(context, manager, channel);
		} else {
			return new WifiDirectStudentListener(context, manager, channel);
		}

	}
	
	public void setService(Context context){
		register(context);
	}

	public void unsetService(ActionListener listener) {
		unregister();
		disconnect(listener);
	}

	public void disconnect(ActionListener listener) {
		if (isInitSuccess()) {
			manager.removeGroup(channel, listener);
		}
	}

	public void register(Context context) {
		if (context != null) {
			if(isInitSuccess() == false){
				init(context);
			}
			
			if (receiver != null) {
				unregister();
			}
			receiver = new WiFiDirectBroadcastReceiver(getWifiDirectListner());
			context.registerReceiver(receiver, intentFilter);
			
			this.context = context;
		}
	}

	public void unregister() {
		if (context != null && receiver != null) {
			context.unregisterReceiver(receiver);
			receiver = null;
		}
	}

	/********
	 * 
	 * @author hyeongseokLim
	 * 
	 */
	private class WrapperChannelListener implements ChannelListener {
		private boolean retryChannel = false;

		@Override
		public void onChannelDisconnected() {
			// we will try once more
			if (manager != null && !retryChannel) {
				retryChannel = true;
				channel = manager.initialize(context, context.getMainLooper(),
						this);
			} else {
				Toast.makeText(
						context,
						"Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
						Toast.LENGTH_LONG).show();
			}
		}
	}
}
