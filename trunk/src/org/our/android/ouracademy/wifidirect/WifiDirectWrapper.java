package org.our.android.ouracademy.wifidirect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.our.android.ouracademy.OurPreferenceManager;
import org.our.android.ouracademy.R;
import org.our.android.ouracademy.constants.CommonConstants;
import org.our.android.ouracademy.util.NetworkState;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.util.Log;
import android.widget.Toast;

/*********
 * 
 * @author hyeongseokLim
 * 
 */
public class WifiDirectWrapper {
	private static final String CONNECTED_COUNT_KEY = "connected_count";

	private static WifiDirectWrapper instance = new WifiDirectWrapper();
	private HashMap<String, WifiP2pDevice> teacherMap = new HashMap<String, WifiP2pDevice>();
	private ArrayList<FindDeviceListener> findDeviceListenerList = new ArrayList<FindDeviceListener>();

	private final IntentFilter intentFilter = new IntentFilter();
	private WifiP2pManager manager;
	private WifiP2pInfo info;
	private Channel channel;
	private Context context;

	private WrapperChannelListener channelListener = new WrapperChannelListener();
	private WiFiDirectBroadcastReceiver receiver;
	private WifiDirectDefaultListener wifidirectListener;

	public boolean isWifidirectEnable = false;

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

	public void findConnectedStudent() {
		manager.requestPeers(channel, new PeerListListener() {
			@Override
			public void onPeersAvailable(WifiP2pDeviceList peers) {
				Collection<WifiP2pDevice> devices = peers.getDeviceList();

				HashMap<String, WifiP2pDevice> connectedStudents = new HashMap<String, WifiP2pDevice>();
				for (WifiP2pDevice device : devices) {
					if (device.status == WifiP2pDevice.CONNECTED) {
						connectedStudents.put(device.deviceAddress, device);
					}
				}
				notiListener(connectedStudents);
			}
		});
	}

	public void addFindDeviceListener(FindDeviceListener listener) {
		findDeviceListenerList.add(listener);
	}

	public void removeFindDeviceListener(FindDeviceListener listener) {
		findDeviceListenerList.remove(listener);
	}

	private void notiListener(HashMap<String, WifiP2pDevice> data) {
		for (FindDeviceListener listener : findDeviceListenerList) {
			listener.onFindDevice(data);
		}
	}

	private DnsSdTxtRecordListener txtListener;

	public void findTeacher() {

		synchronized (WifiDirectWrapper.this) {
			if (txtListener == null) {
				txtListener = new DnsSdTxtRecordListener() {

					@Override
					public void onDnsSdTxtRecordAvailable(String fullDomain,
							Map<String, String> record, WifiP2pDevice device) {
						int connected_count = Integer.parseInt(record
								.get(CONNECTED_COUNT_KEY));
						if (CommonConstants.MAX_CONNECTION >= connected_count) {
							if (teacherMap.containsKey(device.deviceAddress) == false) {
								teacherMap.put(device.deviceAddress, device);
							}
						} else {
							if (teacherMap.containsKey(device.deviceAddress)) {
								teacherMap.remove(device.deviceAddress);
							}
						}
						notiListener(teacherMap);
					}
				};

				manager.setDnsSdResponseListeners(channel, null, txtListener);
				manager.addServiceRequest(channel,
						WifiP2pDnsSdServiceRequest.newInstance(),
						new ActionListener() {

							@Override
							public void onSuccess() {
								Log.d("Test", "addServiceRequest success");
							}

							@Override
							public void onFailure(int reason) {
								Log.d("Test", "addServiceRequest failure");
							}
						});
			}
		}

		manager.discoverServices(channel, new ActionListener() {

			@Override
			public void onSuccess() {
				Log.d("Test", "discoverServices success");
			}

			@Override
			public void onFailure(int reason) {
				Log.d("Test", "discoverServices failure");
			}
		});
	}

	public interface FindDeviceListener {
		public void onFindDevice(HashMap<String, WifiP2pDevice> devices);
	}

	public void discoverPeers() {
		manager.discoverPeers(channel, null);
	}

	private WifiDirectDefaultListener getWifiDirectListner() {
		if (OurPreferenceManager.getInstance().isTeacher()) {
			wifidirectListener = new WifiDirectTeacherListener(context,
					manager, channel);
		} else {
			WifiDirectStudentListener studentListener = new WifiDirectStudentListener(
					context, manager, channel);

			studentListener.setFoundListener(new FindDeviceListener() {
				@Override
				public void onFindDevice(HashMap<String, WifiP2pDevice> devices) {
					synchronized (WifiDirectWrapper.this.teacherMap) {
						Iterator<String> teacherAddressesIt = teacherMap
								.keySet().iterator();

						String address;
						ArrayList<String> dontFindAddress = new ArrayList<String>();
						while (teacherAddressesIt.hasNext()) {
							address = teacherAddressesIt.next();
							if (devices.containsKey(address)) {
								teacherMap.get(address).status = devices
										.get(address).status;
							} else {
								dontFindAddress.add(address);
							}
						}

						for (String removeAddress : dontFindAddress) {
							teacherMap.remove(removeAddress);
						}
					}

					notiListener(teacherMap);
				}
			});
			wifidirectListener = studentListener;
		}
		return wifidirectListener;
	}

	public void setService(Context context) {
		register(context);
	}

	public void unsetService(ActionListener listener) {
		unregister();
		disconnect(listener);
	}

	public void connectAfterChecking(final WifiP2pDevice device) {
		if (NetworkState.isWifiDirectConnected()) {
			disconnect(new ActionListener() {
				@Override
				public void onSuccess() {
					connectAfterCancel(device);
				}

				@Override
				public void onFailure(int reason) {
				}
			});
		} else {
			connectAfterCancel(device);
		}
	}
	
	public void cancelConnect() {
		manager.cancelConnect(channel, null);
	}

	public void connectAfterCancel(final WifiP2pDevice device) {
		manager.cancelConnect(channel, new ActionListener() {

			@Override
			public void onSuccess() {
				connect(device);
			}

			@Override
			public void onFailure(int reason) {
				connect(device);
			}
		});
	}

	public void connect(WifiP2pDevice device) {
		WifiP2pConfig config = new WifiP2pConfig();
		config.deviceAddress = device.deviceAddress;
		config.wps.setup = WpsInfo.PBC;
		config.groupOwnerIntent = 0;

		manager.connect(channel, config, new ActionListener() {

			@Override
			public void onSuccess() {
				manager.requestConnectionInfo(channel,
						(WifiDirectStudentListener) wifidirectListener);
				if(OurPreferenceManager.getInstance().isStudent())
					findTeacher();
			}

			@Override
			public void onFailure(int reason) {
			}
		});
	}

	public void disconnect(ActionListener listener) {
		if (isInitSuccess()) {
			manager.removeGroup(channel, listener);
		}
	}

	public void register(Context context) {
		if (context != null) {
			if (isInitSuccess() == false) {
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
						context.getResources().getString(
								R.string.channerl_disconnected_message),
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void startRegistration(final int connected_count) {
		manager.clearLocalServices(channel, new ActionListener() {

			@Override
			public void onSuccess() {
				Map record = new HashMap();
				record.put(CONNECTED_COUNT_KEY,
						Integer.toString(connected_count));

				WifiP2pDnsSdServiceInfo serviceInfo = WifiP2pDnsSdServiceInfo
						.newInstance("_our_p2p", "_ipp._tcp", record);

				manager.addLocalService(channel, serviceInfo,
						new ActionListener() {

							@Override
							public void onSuccess() {
								Log.d("AddService", "addLocalService success");
							}

							@Override
							public void onFailure(int reason) {
								Log.d("AddService", "addLocalService false");
							}
						});

			}

			@Override
			public void onFailure(int reason) {
				Log.d("Test", "startRegistration onFailure");
			}
		});

	}
}
