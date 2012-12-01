package org.our.android.ouracademy.ui.pages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.our.android.ouracademy.OurPreferenceManager;
import org.our.android.ouracademy.R;
import org.our.android.ouracademy.dao.ContentDAO;
import org.our.android.ouracademy.dao.DAOException;
import org.our.android.ouracademy.manager.DataManager;
import org.our.android.ouracademy.manager.DataManagerFactory;
import org.our.android.ouracademy.manager.StudentDataManager;
import org.our.android.ouracademy.model.OurContents;
import org.our.android.ouracademy.model.OurWifiDirectDevice;
import org.our.android.ouracademy.p2p.client.GetExistingContentsClient.GetExistingContentsListener;
import org.our.android.ouracademy.ui.adapter.WiFiListAdapter;
import org.our.android.ouracademy.ui.pages.MainActivity.OurDataChangeReceiver;
import org.our.android.ouracademy.ui.view.SetupMainView;
import org.our.android.ouracademy.ui.view.SetupMainView.SetupMainViewListener;
import org.our.android.ouracademy.ui.view.SetupWifiListItemView;
import org.our.android.ouracademy.util.NetworkState;
import org.our.android.ouracademy.wifidirect.WifiDirectWrapper;
import org.our.android.ouracademy.wifidirect.WifiDirectWrapper.FindDeviceListener;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * 
 * 
 * @author jyeon
 * 
 */
public class SettingActivity extends BaseActivity {
	private Context context;

	public static final String INTENTKEY_DELETE_MODE = "delete";
	public static final String INTENTKEY_ACTION_DATA_SYNC = "dataSync";
	public static final String INTENTKEY_WIFI_MODE = "wifiMode";

	SetupMainView mainView;

	ListView wifiListView;
	WiFiListAdapter listAdapter;

	ProgressDialog progressDialog = null;

	boolean introWifiMode;

	private long totalSize = 0;
	private long totalDownloadedSize = 0;
	private HashMap<String, OurContents> downloadMap = new HashMap<String, OurContents>();

	private BroadcastReceiver reciever;
	private final IntentFilter intentFilter = new IntentFilter();

	final Handler handler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = this;

		initUI();

		getIntentData();

		if (introWifiMode) {
			OurPreferenceManager pref = OurPreferenceManager.getInstance();
			if (pref.isStudent()) {
				processFindTeacher(true);
			} else {
				processFindConnectedStudent();
			}
		}

		reciever = new SettingBroadCastReciever();
		intentFilter.addAction(OurDataChangeReceiver.OUR_DATA_CHANGED);

	}

	private void getIntentData() {
		Intent intent = getIntent();
		introWifiMode = intent.getBooleanExtra(INTENTKEY_WIFI_MODE, false);
	}

	@Override
	protected void onResume() {
		super.onResume();

		WifiDirectWrapper.getInstance().addFindDeviceListener(
				findWifiDirectPeerListener);

		registerReceiver(reciever, intentFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();

		WifiDirectWrapper.getInstance().removeFindDeviceListener(
				findWifiDirectPeerListener);

		unregisterReceiver(reciever);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void initUI() {
		mainView = new SetupMainView(this);
		mainView.setOnSetupMainViewListener(listener);
		setContentView(mainView);

		createListView();
	}

	private void createListView() {
		wifiListView = mainView.getListView();
		wifiListView.setSelector(new ColorDrawable(Color
				.parseColor("#00000000")));
		wifiListView.setCacheColorHint(Color.parseColor("#00000000"));
		listAdapter = new WiFiListAdapter();
		wifiListView.setAdapter(listAdapter);
		wifiListView.setDividerHeight(0);
		wifiListView.setOnItemClickListener(wifiListClickListener);
	}
	
	private void notifyToAdapter(){
		listAdapter.notifyDataSetChanged();
		
		if(listAdapter.getCount() == 0){
			mainView.viewNoList();
		}else{
			mainView.viewList();
		}
	}

	// public void onClickMode(View view) {
	// showSingleShortToast("모드 설정");
	// }

	SetupMainViewListener listener = new SetupMainViewListener() {

		@Override
		public void onClickCloseBtn() {
			finish();
		}

		@Override
		public void onClickDeleteCell() {
			Intent intent = new Intent();
			intent.putExtra(INTENTKEY_DELETE_MODE, true);
			setResult(RESULT_OK, intent);

			finish();
		}

		@Override
		public void onClickDataSyncCell() {
			if (NetworkState.isWifiDirectConnected()) {
				showProgress(R.string.finding_syncdata);

				DataManager dataManager = DataManagerFactory.getDataManager();

				if (dataManager instanceof StudentDataManager) {
					((StudentDataManager)dataManager)
							.getTeacherContents(existingContentsListener);
				}
			} else {
				showAlert(R.string.error_wifi_direct_disconnected_title,
						R.string.error_wifi_direct_disconnected_message);
			}
		}

		@Override
		public void onClickModeBtn(boolean teacher) {
			OurPreferenceManager pref = OurPreferenceManager.getInstance();
			boolean change = false;
			if (teacher && pref.isStudent()) {
				change = true;
			} else if (teacher == false && pref.isTeacher()) {
				change = true;
			}

			if (change) {
				DataManager dataManager = DataManagerFactory.getDataManager();
				dataManager.stopService(context);

				if (teacher) {
					pref.setTeacherMode();
				} else {
					pref.setStudentMode();
				}
				dataManager = DataManagerFactory.getDataManager();
				dataManager.startService(context);
			}
		}

		@Override
		public void onClickFindConnectedStudent() {
			processFindConnectedStudent();
		}

		@Override
		public void onClickFindTeacher(boolean isViewChange) {
			processFindTeacher(isViewChange);
		}

		@Override
		public void onClickCacelSync() {
			if (downloadMap != null) {
				Collection<OurContents> contents = downloadMap.values();

				DataManager dataManager = DataManagerFactory.getDataManager();
				for (OurContents content : contents) {
					dataManager.cancelDownload(content);
				}
			}
		}
	};

	private void processFindTeacher(boolean isViewChange) {
		if (NetworkState.isWifiDirectEnabled()) {
			showProgress(R.string.finding_teacher);

			WifiDirectWrapper.getInstance().findTeacher();

			if(isViewChange){
				mainView.viewTeacherOnNetwork();
			}
		} else {
			showAlert(R.string.error_wifi_direct_disable_title,
					R.string.error_wifi_direct_disable_message);
		}
	}

	private void processFindConnectedStudent() {
		if (NetworkState.isWifiDirectEnabled()) {
			showProgress(R.string.finding_student);

			WifiDirectWrapper.getInstance().findConnectedStudent();

			mainView.viewConnectedStudent();
		} else {
			showAlert(R.string.error_wifi_direct_disable_title,
					R.string.error_wifi_direct_disable_message);
		}
	}

	private void showAlert(int titleResourceId, int messageResourceId) {
		Builder alertBuilder = new Builder(context);
		alertBuilder
				.setTitle(context.getResources().getString(titleResourceId));
		alertBuilder.setMessage(context.getResources().getString(
				messageResourceId));
		alertBuilder.setPositiveButton(R.string.ok, null);
		alertBuilder.create().show();
	}

	private void showProgress(int titleResourceId) {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		progressDialog = ProgressDialog.show(SettingActivity.this, context
				.getResources().getString(titleResourceId), context
				.getResources().getString(R.string.finding), true, true, null);

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
			}
		}, 5000);
	}

	private FindDeviceListener findWifiDirectPeerListener = new FindDeviceListener() {

		@Override
		public void onFindDevice(HashMap<String, WifiP2pDevice> devices) {
			if (listAdapter.deviceList != null && devices != null) {
				listAdapter.deviceList.clear();
				OurWifiDirectDevice ourDevice;
				for (WifiP2pDevice device : devices.values()) {
					ourDevice = new OurWifiDirectDevice();
					ourDevice.device = device;

					if (device.status == WifiP2pDevice.CONNECTED) {
						ourDevice.connectingState = OurWifiDirectDevice.STATE_CONNECTED;
					}else if(device.status == WifiP2pDevice.INVITED){
						ourDevice.connectingState = OurWifiDirectDevice.STATE_CONNECTING;
					}else{
						ourDevice.connectingState = OurWifiDirectDevice.STATE_DISCONNECTED;
					}

					listAdapter.deviceList.add(ourDevice);
				}

				notifyToAdapter();
			}
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		}
	};

	@Override
	public void onBackPressed() {
		if (mainView.onBackPressed()) {
			return;
		}
		super.onBackPressed();
	};

	private OnItemClickListener wifiListClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (view instanceof SetupWifiListItemView) {
				OurWifiDirectDevice ourDevice = listAdapter.getDeviceList()
						.get(position);

				WifiDirectWrapper wifidirectWrapper = WifiDirectWrapper
						.getInstance();
				switch (ourDevice.connectingState) {
					case OurWifiDirectDevice.STATE_CONNECTED:
					wifidirectWrapper.disconnect(new ActionListener() {
						@Override
						public void onSuccess() {
							handler.postDelayed(new Runnable() {

								@Override
								public void run() {
									WifiDirectWrapper.getInstance()
											.findTeacher();
								}
							}, 2500);
						}

						@Override
						public void onFailure(int reason) {

						}
					});
					ourDevice.connectingState = OurWifiDirectDevice.STATE_DISCONNECTED;
					break;
				case OurWifiDirectDevice.STATE_DISCONNECTED:
					ourDevice.connectingState = OurWifiDirectDevice.STATE_CONNECTING;
					wifidirectWrapper.connectAfterChecking(ourDevice.device);
					break;
				case OurWifiDirectDevice.STATE_CONNECTING:
					wifidirectWrapper.cancelConnect();

					ourDevice.connectingState = OurWifiDirectDevice.STATE_DISCONNECTED;
					break;
				default:
					break;
				}

				notifyToAdapter();
			}
		}
	};

	private GetExistingContentsListener existingContentsListener = new GetExistingContentsListener() {

		@Override
		public void onSuccess(ArrayList<OurContents> existingContents) {
			totalSize = 0;
			totalDownloadedSize = 0;
			downloadMap.clear();

			ContentDAO contentDao = new ContentDAO();

			try {
				ArrayList<OurContents> downloadContents = contentDao
						.getExistingContents(existingContents);

				if (downloadContents != null) {
					DataManager dataManager = DataManagerFactory
							.getDataManager();

					ArrayList<String> contentId = new ArrayList<String>();
					OurContents downloadContent;
					for (int i = 0; i < downloadContents.size(); i++) {
						downloadContent = downloadContents.get(i);

						downloadMap.put(downloadContent.getId(),
								downloadContent);
						contentId.add(downloadContent.getId());
						dataManager.download(downloadContent);

						totalSize += downloadContent.getSize();
						totalDownloadedSize += downloadContent
								.getDownloadedSize();
					}

					if (downloadContents.size() == 0) {
						handler.post(new Runnable() {

							@Override
							public void run() {
								if (progressDialog != null
										&& progressDialog.isShowing())
									progressDialog.dismiss();

								showAlert(R.string.datasync,
										R.string.dontneed_datasync);
							}
						});
					} else {
						Intent intent = new Intent(
								OurDataChangeReceiver.OUR_DATA_CHANGED);
						intent.putExtra(OurDataChangeReceiver.ACTION,
								OurDataChangeReceiver.ACTION_SYNC_DATA);
						intent.putExtra(OurDataChangeReceiver.CONTENT_ID,
								contentId);
						context.sendBroadcast(intent);

						handler.post(new Runnable() {

							@Override
							public void run() {
								if (progressDialog != null
										&& progressDialog.isShowing())
									progressDialog.dismiss();
								mainView.viewDataSync();
							}
						});
					}
				}
			} catch (DAOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onFailure() {
			handler.post(new Runnable() {

				@Override
				public void run() {
					if (progressDialog != null && progressDialog.isShowing())
						progressDialog.dismiss();

					showAlert(R.string.error, R.string.error_datasync);
				}
			});
		}
	};

	private class SettingBroadCastReciever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (OurDataChangeReceiver.OUR_DATA_CHANGED.equals(intent
					.getAction())) {
				OurContents content;
				switch (intent.getIntExtra(OurDataChangeReceiver.ACTION, -1)) {
					case OurDataChangeReceiver.ACTION_DOWNLOADING:
						content = downloadMap.get(intent
							.getStringExtra(OurDataChangeReceiver.CONTENT_ID));
						if (content != null) {
							long downloadedSize = intent.getLongExtra(
								OurDataChangeReceiver.DOWNLAD_SIZE, 0);
							if (downloadedSize != 0) {
								downloadedSize = content
									.getCurrentDownloadedSize(downloadedSize);
								if (downloadedSize > 0) {
									totalDownloadedSize += downloadedSize;
								}
							}
						}
						break;
					case OurDataChangeReceiver.ACTION_ERROR_DOWNLOADING:
					case OurDataChangeReceiver.ACTION_CANCEL_DOWNLOADING:
						content = downloadMap.get(intent
							.getStringExtra(OurDataChangeReceiver.CONTENT_ID));
						if (content != null) {
							totalDownloadedSize += content.getSize()
								- content.getPrevDownloadedSize();
						}
						break;
				}

				if (totalSize != 0) {
					mainView.setProgress((int)(totalDownloadedSize * 100 / totalSize));
				}
			}
		}
	}
}
