package org.our.android.ouracademy.wifidirect;

import org.our.android.ouracademy.OurPreferenceManager;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.widget.Toast;


public class WifiDirectWrapper {
	private static WifiDirectWrapper instance = new WifiDirectWrapper();

	private final IntentFilter intentFilter = new IntentFilter();
	private WifiP2pManager manager;
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
		channel = manager.initialize(context, context.getMainLooper(),
				channelListener);
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

	public void register() {
		if (context != null) {
			if (receiver != null) {
				unregister();
			}

			receiver = new WiFiDirectBroadcastReceiver(getWifiDirectListner());
			context.registerReceiver(receiver, intentFilter);
		}
	}

	public void unregister() {
		if (context != null && receiver != null) {
			context.unregisterReceiver(receiver);
			receiver = null;
		}
	}

	private class WrapperChannelListener implements ChannelListener {
		private boolean retryChannel = false;

		@Override
		public void onChannelDisconnected() {
			// we will try once more
			if (manager != null && !retryChannel) {
				Toast.makeText(context, "Channel lost. Trying again",
						Toast.LENGTH_LONG).show();
				retryChannel = true;
				manager.initialize(context, context.getMainLooper(), this);
			} else {
				Toast.makeText(
						context,
						"Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
						Toast.LENGTH_LONG).show();
			}
		}
	}
}
