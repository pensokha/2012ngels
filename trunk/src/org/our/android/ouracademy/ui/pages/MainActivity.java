package org.our.android.ouracademy.ui.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.our.android.ouracademy.OurApplication;
import org.our.android.ouracademy.R;
import org.our.android.ouracademy.constants.CommonConstants;
import org.our.android.ouracademy.dao.CategoryDAO;
import org.our.android.ouracademy.dao.ContentDAO;
import org.our.android.ouracademy.dao.DAOException;
import org.our.android.ouracademy.manager.DataManagerFactory;
import org.our.android.ouracademy.model.OurCategory;
import org.our.android.ouracademy.model.OurContents;
import org.our.android.ouracademy.ui.view.MainDetailView;
import org.our.android.ouracademy.ui.view.MainMenuView;
import org.our.android.ouracademy.wifidirect.WiFiDirectBroadcastReceiver;
import org.our.android.ouracademy.wifidirect.WifiDirectListener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author JiHoon, Moon
 * 
 */
public class MainActivity extends BaseActivity {
	private ViewGroup menuLayout;
	private ViewGroup detailLayout;

	MainDetailView detailView;
	MainMenuView menuView;

	private OurDataChangeReceiver reciever;
	private WiFiDirectBroadcastReceiver wifiReciever;
	private final IntentFilter intentFilter = new IntentFilter();

	private static boolean closeFlag = false;

	private HashMap<String, OurCategory> selectedCategories = new HashMap<String, OurCategory>();
	private HashMap<String, ArrayList<OurContents>> contentsHashMap = new HashMap<String, ArrayList<OurContents>>();

	public static final int SETTING_ACTIVITY = 101;

	public static boolean isKhmerLanguage = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//force set khmer
		callSwitchLang(CommonConstants.LOCALE_LANGUAGE_KHMER);
		OurApplication.getInstance().updateLocaleLangueage();

		initUI();

		if (reciever == null) {
			reciever = new OurDataChangeReceiver();
			intentFilter.addAction(OurDataChangeReceiver.OUR_DATA_CHANGED);
			registerReceiver(reciever, intentFilter);
		}

		if (wifiReciever == null) {
			IntentFilter intentFilter = new IntentFilter();
			intentFilter
					.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
			intentFilter
					.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
			intentFilter
					.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
			intentFilter
					.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
			wifiReciever = new WiFiDirectBroadcastReceiver(wifidirectListener);
			registerReceiver(wifiReciever, intentFilter);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		DataManagerFactory.getDataManager().syncFileAndDatabase();

	}

	@Override
	protected void onPause() {
		super.onPause();
		closeHandler.removeMessages(0);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		closeFlag = false;

		unregisterReceiver(reciever);
		unregisterReceiver(wifiReciever);
	}

	private void initUI() {
		setContentView(R.layout.activity_main);

		menuLayout = (ViewGroup)findViewById(R.id.layout_main_menu);

		detailLayout = (ViewGroup)findViewById(R.id.layout_main_detail);

		initMenuLayout();
		initContentsLayout();

		// road saved last selected category.
		applyBtnClickListener.onClick(null);
	}

	private void initMenuLayout() {
		menuView = new MainMenuView(this);
		menuView.setApplyBtnListener(applyBtnClickListener);
		menuLayout.addView(menuView);
	}

	private void initContentsLayout() {
		detailView = new MainDetailView(this);
		detailLayout.addView(detailView);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			event.startTracking();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking()
				&& !event.isCanceled()) {
			if (detailView.menuStatus == MainDetailView.MenuStatus.VISIBLE_MENU) {
				detailView.onClickMenu();
				return true;
			} else if (closeFlag == false) {
//				String message = getResources().getString(R.string.finish_application); 
//				showShortToast(message);
				Toast toast = Toast.makeText(this, R.string.finish_application, Toast.LENGTH_SHORT);
				TextView textView = (TextView)toast.getView().findViewById(android.R.id.message);
				textView.setTypeface(Typeface.createFromAsset(getAssets(), CommonConstants.KHMER_FONT_FILE));
				toast.show();
				closeFlag = true;
				closeHandler.sendEmptyMessageDelayed(0, 3000);
				return true;
			} else {
				finishApp();
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	private void finishApp() {
		// final ActivityManager am = (ActivityManager)
		// getSystemService(ACTIVITY_SERVICE);

		// stop running service inside current process.
		// List<RunningServiceInfo> serviceList = am.getRunningServices(100);
		// for (RunningServiceInfo service : serviceList) {
		// if (service.pid == android.os.Process.myPid()) {
		// Intent stop = new Intent();
		// stop.setComponent(service.service);
		// stopService(stop);
		// }
		// }

		this.finish();
		// process kill~~!!
		// android.os.Process.killProcess(android.os.Process.myPid());
		// System.gc();
	}

	private static Handler closeHandler = new Handler() {
		public void handleMessage(Message msg) {
			closeFlag = false;
		}
	};

	OnClickListener applyBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			ArrayList<OurCategory> categories = menuView.getCategories();
			if (categories != null) {
				selectedCategories.clear();
				for (OurCategory category : categories) {
					if (category.isChecked) {
						selectedCategories.put(category.getCategoryId(), category);
					}
				}
			}
			reloadContents();
			//(Temporary)listview position set First
			detailView.getList().setAdapter(detailView.getListAdapter());
			// detailView.onClickMenu();
		}
	};

	public class OurDataChangeReceiver extends BroadcastReceiver {
		public static final String OUR_DATA_CHANGED = "org.our.android.ouracademy.broadreceiver.OurDataChanged";

		public static final String ACTION = "action";
		public static final String CONTENT_ID = "content_id";
		public static final String DOWNLAD_SIZE = "download_size";

		public static final int ACTION_RELOAD = 0;
		public static final int ACTION_CATEGORY_CHANGED = 1;
		public static final int ACTION_CONTENT_CHANGED = 2;
		public static final int ACTION_DOWNLOADING = 3;
		public static final int ACTION_CANCEL_DOWNLOADING = 4;
		public static final int ACTION_SYNC_DATA = 5;
		public static final int ACTION_ERROR_DOWNLOADING = 6;

		@Override
		public void onReceive(Context context, Intent intent) {
			if (OUR_DATA_CHANGED.equals(intent.getAction())) {
				switch (intent.getIntExtra(ACTION, -1)) {
					case ACTION_RELOAD:
						reloadContents();
						reloadCategories();
						break;
					case ACTION_CATEGORY_CHANGED:
						reloadCategories();
						break;
					case ACTION_CONTENT_CHANGED:
						reloadContents();
						break;
					case ACTION_DOWNLOADING:
						updateDownloadSize(intent.getStringExtra(CONTENT_ID),
							intent.getLongExtra(DOWNLAD_SIZE, 0));
						break;
					case ACTION_CANCEL_DOWNLOADING:
					case ACTION_ERROR_DOWNLOADING:
						cancelDownloading(intent.getStringExtra(CONTENT_ID));
						break;
					case ACTION_SYNC_DATA:
						syncData(intent.getStringArrayListExtra(CONTENT_ID));
						break;
					default:
						break;
				}
			}
		}
	}

	private void syncData(ArrayList<String> contentId) {
		ArrayList<OurContents> contents = detailView.getContentList();
		if (contents != null) {
			for (OurContents content : contents) {
				if (content.fileStatus == OurContents.FileStatus.NONE
						&& contentId.contains(content.getId())) {
					content.fileStatus = OurContents.FileStatus.DOWNLOADING;
				}
			}

			if (detailView.getListAdapter() != null) {
				detailView.getList().setAdapter(detailView.getListAdapter());
				detailView.getListAdapter().notifyDataSetChanged();
			}
		}
	}

	private ArrayList<OurContents> getContentsFromHashMap(String contentId) {
		if (contentsHashMap.containsKey(contentId)) {
			return contentsHashMap.get(contentId);
		}
		return null;
	}

	private void cancelDownloading(String contentId) {
		ArrayList<OurContents> contents = getContentsFromHashMap(contentId);
		if (contents != null) {
			for (OurContents content : contents) {
				content.fileStatus = OurContents.FileStatus.NONE;
			}

			if (detailView.getListAdapter() != null) {
				detailView.getListAdapter().notifyDataSetChanged();
			}
		}
	}

	private void updateDownloadSize(String contentId, long downloadedSize) {
		ArrayList<OurContents> contents = getContentsFromHashMap(contentId);
		if (contents != null) {
			for (OurContents content : contents) {
				content.fileStatus = OurContents.FileStatus.DOWNLOADING;

				if (content.getDownloadedSize() <= downloadedSize) {
					content.setDownloadedSize(downloadedSize);

					if (content.getDownloadedSize() == content.getSize()) {
						content.fileStatus = OurContents.FileStatus.DOWNLOADED;
					}
				}
			}

			if (detailView.getListAdapter() != null) {
				detailView.getListAdapter().notifyDataSetChanged();
			}
		}
	}

	private void reloadCategories() {
		ArrayList<OurCategory> categories = menuView.getCategories();
		if (categories != null) {
			try {
				ArrayList<OurCategory> categoriesFromDB = new CategoryDAO()
						.getCategories();
				categories.clear();
				categories.addAll(categoriesFromDB);
				if (menuView.getAdapter() != null) {
					menuView.getAdapter().notifyDataSetChanged();
				}

				// road last selected category.
				applyBtnClickListener.onClick(null);
			} catch (DAOException e) {
				e.printStackTrace();
			}
		}
	}

	private void reloadContents() {
		ArrayList<OurContents> contents = detailView.getContentList();

		// deleteMode일 때는 DB데이터를 업데이트 안 한다.
		// @author Sung-Chul Park
		for (OurContents content : contents) {
			if (content.isDeleteMode()) {
				return;
			}
		}

		if (contents != null) {
			try {
				ArrayList<OurContents> contentsFromDB = new ContentDAO()
						.getDuplicatedContents(selectedCategories);
				HashMap<String, Long> downloadingContents = new HashMap<String, Long>();
				for (OurContents content : contents) {
					if (content.fileStatus == OurContents.FileStatus.DOWNLOADING) {
						downloadingContents.put(content.getId(),
								content.getDownloadedSize());
					}
				}
				contents.clear();
				contentsHashMap.clear();

				for (OurContents content : contentsFromDB) {
					if (contentsHashMap.containsKey(content.getId()) == false) {
						contentsHashMap.put(content.getId(),
								new ArrayList<OurContents>());
					}
					if (downloadingContents.containsKey(content.getId())) {
						content.fileStatus = OurContents.FileStatus.DOWNLOADING;
						content.setDownloadedSize(downloadingContents
								.get(content.getId()));
					}

					if (content.fileStatus == OurContents.FileStatus.DOWNLOADED) {
						content.setFullTextMode(detailView.isFullTextMode());
					}

					contentsHashMap.get(content.getId()).add(content);
					contents.add(content);
				}

				if (detailView.getListAdapter() != null) {
//					detailView.getList().setAdapter(detailView.getListAdapter());
					detailView.getListAdapter().notifyDataSetChanged();
				}
			} catch (DAOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SETTING_ACTIVITY) {
			if (resultCode == RESULT_OK) {
				boolean isDeleteMode = data.getBooleanExtra(
						SettingActivity.INTENTKEY_DELETE_MODE, false);

				if (isDeleteMode == true) {
					detailView.goIntoDeleteMode();
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.language:
				isKhmerLanguage = !isKhmerLanguage;
				if (isKhmerLanguage) {
					callSwitchLang(CommonConstants.LOCALE_LANGUAGE_KHMER);
				} else {
					callSwitchLang(CommonConstants.LOCALE_LANGUAGE_ENG);
				}

				initUI();

				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void callSwitchLang(String langCode) {
		Locale locale = new Locale(langCode);
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());
	}

	public WifiDirectListener wifidirectListener = new WifiDirectListener() {
		public boolean isConnected = false;
		public boolean isEnabled = false;

		@Override
		public void onEnableP2p() {
			isEnabled = true;
			menuView.setRefreshBtnStatus(isConnected, isEnabled);
		}

		@Override
		public void onDisableP2p() {
			isEnabled = false;
			menuView.setRefreshBtnStatus(isConnected, isEnabled);
		}

		@Override
		public void onConnected() {
			isConnected = true;
			menuView.setRefreshBtnStatus(isConnected, isEnabled);
		}

		@Override
		public void onDisConnected() {
			isConnected = false;
			menuView.setRefreshBtnStatus(isConnected, isEnabled);
		}

		@Override
		public void onDeviceInfoChanged() {
		}

		@Override
		public void onPeerChanged() {
		}
	};
}
