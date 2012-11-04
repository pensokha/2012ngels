package org.our.android.ouracademy.wifidirect;

import java.util.ArrayList;

import org.our.android.ouracademy.OurPreferenceManager;
import org.our.android.ouracademy.wifidirect.WifiDirectStudentListener.GroupOwnerFoundListener;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
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
	private ArrayList<GroupOwnerFoundListener> groupOwnerFoundListenr;

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

	public void setInfo(WifiP2pInfo info) {
		this.info = info;
	}

	public ArrayList<GroupOwnerFoundListener> getGroupOwnerFoundListenr() {
		return groupOwnerFoundListenr;
	}

	public void addGroupOwnerFoundListenr(
			GroupOwnerFoundListener groupOwnerFoundListenr) {
		this.groupOwnerFoundListenr.add(groupOwnerFoundListenr);
	}

	public void removeGroupOwnerFoundListenr(
			GroupOwnerFoundListener groupOwnerFoundListenr) {
		this.groupOwnerFoundListenr.remove(groupOwnerFoundListenr);
	}

	public String getOwnerIP() {
		if (info != null && info.groupOwnerAddress != null) {
			return info.groupOwnerAddress.getHostAddress();
		} else {
			return null;
		}
	}

	public void discoverPeers() {
		manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

			@Override
			public void onSuccess() {
				Toast.makeText(context, "Discovery Initiated",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(int reasonCode) {
				Toast.makeText(context, "Discovery Failed : " + reasonCode,
						Toast.LENGTH_SHORT).show();
			}
		});
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
