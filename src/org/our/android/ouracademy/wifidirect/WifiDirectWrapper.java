package org.our.android.ouracademy.wifidirect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.our.android.ouracademy.OurDefine;
import org.our.android.ouracademy.OurPreferenceManager;
import org.our.android.ouracademy.manager.FileManager;
import org.our.android.ouracademy.manager.OurDownloadManager;
import org.our.android.ouracademy.p2p.JSONProtocol;
import org.our.android.ouracademy.p2p.P2PManager;
import org.our.android.ouracademy.p2p.P2PService;
import org.our.android.ouracademy.p2p.action.DownloadFile;
import org.our.android.ouracademy.ui.pages.MainActivity;
import org.our.android.ouracademy.ui.pages.MainActivityOld;
import org.our.android.ouracademy.wifidirect.WifiDirectStudentListener.GroupOwnerFoundListener;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/*********
 * 
 * @author hyeongseokLim
 * 
 */
public class WifiDirectWrapper {
	private static final String TAG = "WifiDirectWrapper";

	private static WifiDirectWrapper instance = new WifiDirectWrapper();

	private final IntentFilter intentFilter = new IntentFilter();
	private WifiP2pManager manager;
	private WifiP2pInfo info;
	private Channel channel;
	private Context context;

	private WrapperChannelListener channelListener = new WrapperChannelListener();
	private WiFiDirectBroadcastReceiver receiver;
	private ArrayList<GroupOwnerFoundListener> groupOwnerFoundListenr;
	private ComponentName serviceName;

	public void stopP2pService() {
		if (serviceName != null) {
			Intent intent = new Intent();
			intent.setComponent(serviceName);

			if (context.stopService(intent)) {
				Log.d(TAG, "Stop P2p Service");
			}
			serviceName = null;
		}
	}

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
		if (info != null) {
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

	public void setting() {
		register();

//		if (OurPreferenceManager.getInstance().isTeacher() == true) {
			serviceName = context.startService(new Intent(context,
					P2PService.class));
			Log.d(TAG, serviceName.toString());
			serviceName = context.startService(new Intent(context,
					P2PService.class));
			Log.d(TAG, serviceName.toString());
//		}
	}

	public void resetService(ActionListener listener) {
		if (serviceName != null) {
			stopP2pService();
		}
		unsetting();
		disconnect(listener);
	}

	public void disconnect(ActionListener listener) {
		if (isInitSuccess()) {
			manager.removeGroup(channel, listener);
		}
	}

	public void unsetting() {
		unregister();
	}

	private void register() {
		if (context != null) {
			if (receiver != null) {
				unregister();
			}
			receiver = new WiFiDirectBroadcastReceiver(getWifiDirectListner());
			context.registerReceiver(receiver, intentFilter);
		}
	}

	private void unregister() {
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

	/********
	 * 
	 * @author hyeongseokLim
	 * 
	 */
	private class DownloadFileAsyncTask extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... params) {
			if (params == null || params[0] == null) {
				return null;
			}

			String fileId = params[0];
			String address = getOwnerIP();

			if (address == null) {
				return null;
			}

			Socket socket = connectToOwner(address);
			if (socket == null) {
				return null;
			}

			JSONObject json = new JSONObject();
			JSONObject header = new JSONObject();

			try {
				header.put("method", DownloadFile.methodName);
				json.put("header", header);
				json.put("id", fileId);

				JSONProtocol.write(socket, json.toString());

				File file = FileManager.getFile(fileId);
				file.createNewFile();

				InputStream inputStream = socket.getInputStream();
				FileManager.copyFile(inputStream, new FileOutputStream(file));

				OurDownloadManager downloadManager = new OurDownloadManager();
				downloadManager.addRow(fileId, file.getTotalSpace(),
						file.getTotalSpace(), fileId, FileManager.STRSAVEPATH
								+ fileId);

			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} finally {
				P2PManager.close(socket);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			context.sendBroadcast(new Intent(
					MainActivityOld.OUR_CONTENT_DATA_CHANGED));
		}

		private Socket connectToOwner(String serverAddress) {
			Socket sock = null;
			int portIdx = 0;

			for (int i = 0; i < OurDefine.P2P_SERVER_PORT.length; i++) {
				try {
					sock = new Socket(serverAddress,
							OurDefine.P2P_SERVER_PORT[portIdx++]);

				} catch (IOException e) {
					continue;
				}
				return sock;
			}
			return sock;
		}
	}

	public void download(String contentId) {
		new DownloadFileAsyncTask().execute(contentId);
	}
}
