package org.our.android.ouracademy.ui.pages;

import java.util.ArrayList;

import org.our.android.ouracademy.OurPreferenceManager;
import org.our.android.ouracademy.R;
import org.our.android.ouracademy.manager.DataManager;
import org.our.android.ouracademy.manager.DataManagerFactory;
import org.our.android.ouracademy.ui.adapter.WiFiListAdapter;
import org.our.android.ouracademy.ui.view.SetupMainView;
import org.our.android.ouracademy.ui.view.SetupMainView.SetupMainViewListener;
import org.our.android.ouracademy.ui.view.SetupWifiListItemView;
import org.our.android.ouracademy.util.NetworkState;
import org.our.android.ouracademy.wifidirect.WifiDirectWrapper;
import org.our.android.ouracademy.wifidirect.WifiDirectWrapper.FindDeviceListener;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
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

	public static String INTENTKEY_DELETE_MODE = "delete";
	public static String INTENTKEY_ACTION_DATA_SYNC = "dataSync";

	SetupMainView mainView;

	ListView wifiListView;
	WiFiListAdapter listAdapter;

	ProgressDialog progressDialog = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUI();

		context = this;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
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
		wifiListView.setSelector(new ColorDrawable(Color.parseColor("#00000000")));
		wifiListView.setCacheColorHint(Color.parseColor("#00000000"));
		listAdapter = new WiFiListAdapter();
		wifiListView.setAdapter(listAdapter);
		wifiListView.setDividerHeight(0);
		wifiListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (view instanceof SetupWifiListItemView) {
					WifiP2pDevice device = listAdapter.getDeviceList().get(
							position);

					if (NetworkState.isWifiDirectConnected()) {
						if (device.status == WifiP2pDevice.CONNECTED) {
							WifiDirectWrapper.getInstance().disconnect(null);
						}
					}else{
						if (device.status != WifiP2pDevice.CONNECTED
								&& device.status == WifiP2pDevice.AVAILABLE) {
							WifiDirectWrapper.getInstance().connectAfterCancel(device);
						}
					}
				}
			}
		});
	}

//	public void onClickMode(View view) {
//		showSingleShortToast("모드 설정");
//	}

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
			// Intent intent = new Intent();
			// intent.putExtra(INTENTKEY_ACTION_DATA_SYNC, true);
			// setResult(RESULT_OK, intent);
			//
			// finish();
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
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			progressDialog = ProgressDialog.show(SettingActivity.this,
					context.getResources().getString(R.string.finding_student),
					context.getResources().getString(R.string.finding),
					true, false,
					null);

			WifiDirectWrapper.getInstance().findConnectedStudent(
					findWifiDirectPeerListener);
		}

		@Override
		public void onClickFindTeacher() {
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			progressDialog = ProgressDialog.show(SettingActivity.this,
					context.getResources().getString(R.string.finding_student),
					context.getResources().getString(R.string.finding),
					true, true, null);

			listAdapter.deviceList.clear();
			listAdapter.notifyDataSetChanged();

			WifiDirectWrapper.getInstance().findTeacher(
					findWifiDirectPeerListener);
		}
	};

	private FindDeviceListener findWifiDirectPeerListener = new FindDeviceListener() {

		@Override
		public void onFindDevice(ArrayList<WifiP2pDevice> devices) {
			if (listAdapter.deviceList != null && devices != null) {
				listAdapter.deviceList.clear();
				listAdapter.deviceList.addAll(devices);
				listAdapter.notifyDataSetChanged();
			}
			progressDialog.dismiss();
		}
	};

	@Override
	public void onBackPressed() {
		if (mainView.onBackPressed()) {
			return;
		}
		super.onBackPressed();
	};
}
