package org.our.android.ouracademy.ui.pages;

import org.our.android.ouracademy.OurPreferenceManager;
import org.our.android.ouracademy.manager.DataManager;
import org.our.android.ouracademy.manager.DataManagerFactory;
import org.our.android.ouracademy.ui.adapter.WiFiListAdapter;
import org.our.android.ouracademy.ui.view.SetupMainView;
import org.our.android.ouracademy.ui.view.SetupMainView.SetupMainViewListener;
import org.our.android.ouracademy.ui.view.SetupWifiListItemVew;
import org.our.android.ouracademy.wifidirect.WifiDirectWrapper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class SettingActivity extends BaseActivity {
	private Context context;

	public static String INTENTKEY_DELETE_MODE = "delete";
	public static String INTENTKEY_ACTION_DATA_SYNC = "dataSync";

	SetupMainView mainView;

	ListView wifiListView;
	WiFiListAdapter listAdapter;
	
	WifiDirectWrapper wifiDirectWrapper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUI();
		
		context = this;
		wifiDirectWrapper = WifiDirectWrapper.getInstance();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		wifiDirectWrapper.register(context);
	}

	@Override
	protected void onPause() {
		super.onPause();

		wifiDirectWrapper.unregister();
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
		wifiListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (view instanceof SetupWifiListItemVew) {
					SetupWifiListItemVew item = (SetupWifiListItemVew)view;
					mainView.setNetworkIdText(item.getDirectId());
					mainView.previousView();
				}
			}
		});
	}

	public void onClickMode(View view) {
		showSingleShortToast("모드 설정");
	}

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
			Intent intent = new Intent();
			intent.putExtra(INTENTKEY_ACTION_DATA_SYNC, true);
			setResult(RESULT_OK, intent);

			finish();
		}

		@Override
		public void onClickModeBtn(boolean teacher) {
			OurPreferenceManager pref = OurPreferenceManager.getInstance();
			boolean change = false;
			if (teacher && pref.isStudent()) {
				change = true;
			} else if(teacher == false && pref.isTeacher()){
				change = true;
			}
			
			if(change){
				DataManager dataManager = DataManagerFactory.getDataManager();
				dataManager.stopService(context);
				
				if(teacher){
					pref.setTeacherMode();
				}else{
					pref.setStudentMode();
				}
				dataManager = DataManagerFactory.getDataManager();
				dataManager.startService(context);
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
}