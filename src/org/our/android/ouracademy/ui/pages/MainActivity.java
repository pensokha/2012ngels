package org.our.android.ouracademy.ui.pages;

import java.util.ArrayList;

import org.our.android.ouracademy.OurPreferenceManager;
import org.our.android.ouracademy.R;
import org.our.android.ouracademy.dao.CategoryDAO;
import org.our.android.ouracademy.dao.ContentDAO;
import org.our.android.ouracademy.dao.DAOException;
import org.our.android.ouracademy.manager.DataManagerFactory;
import org.our.android.ouracademy.model.OurCategory;
import org.our.android.ouracademy.model.OurContents;
import org.our.android.ouracademy.ui.adapter.ContentsListAdapter;
import org.our.android.ouracademy.ui.view.MainDetailView;
import org.our.android.ouracademy.ui.view.MainMenuView;
import org.our.android.ouracademy.wifidirect.WifiDirectWrapper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
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
	MainMenuView mainMenuView;

	ListView contentsListview;
	ContentsListAdapter contentsListAdapter;

	private OurDataChangeReceiver reciever;
	private final IntentFilter intentFilter = new IntentFilter();;

	private static boolean closeFlag = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUI();

		reciever = new OurDataChangeReceiver();
		intentFilter.addAction(OurDataChangeReceiver.OUR_DATA_CHANGED);
		registerReceiver(reciever, intentFilter);

		if (OurPreferenceManager.getInstance().isStudent()) {
			DataManagerFactory.getDataManager().startService(this);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		WifiDirectWrapper.getInstance().register(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		closeHandler.removeMessages(0);

		WifiDirectWrapper.getInstance().unregister();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		unregisterReceiver(reciever);
		if (OurPreferenceManager.getInstance().isStudent()) {
			DataManagerFactory.getDataManager().stopService(this);
		}
	}

	private void initUI() {
		setContentView(R.layout.activity_main);

		menuLayout = (ViewGroup) findViewById(R.id.layout_main_menu);

		detailLayout = (ViewGroup) findViewById(R.id.layout_main_detail);

		initMenuLayout();
		initContentsLayout();
	}

	private void initMenuLayout() {
		mainMenuView = new MainMenuView(this);
		mainMenuView.setApplyBtnListener(applyBtnClickListener);
		menuLayout.addView(mainMenuView);
	}

	private void initContentsLayout() {
		detailView = new MainDetailView(this);
		detailView.dragWidth = 513; //
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
				showShortToast(getResources().getString(
						R.string.finish_application));
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
			detailView.onClickMenu();
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
				default:
					break;
				}
			}
		}
	}

	private void updateDownloadSize(String contentId, long downloadedSize) {
		ArrayList<OurContents> contents = detailView.getContentList();
		if (contents != null) {
			for (OurContents content : contents) {
				if (content.getId().equals(contentId)) {
					content.fileStatus = OurContents.FileStatus.DOWNLOADING;
					if (content.getDownloadedSize() < downloadedSize) {
						Log.d("Test", ""+content.getDownloadedSize());
						content.setDownloadedSize(downloadedSize);
						if (content.getDownloadedSize() == content.getSize()) {
							content.fileStatus = OurContents.FileStatus.DOWNLOADED;
						}
						if (detailView.getListAdapter() != null) { // && detailView.getListView() != null
							detailView.getListAdapter().notifyDataSetChanged();
//							detailView.getListView().setAdapter(detailView.getListAdapter());
						}
					}
					break;
				}
			}
		}
	}

	private void reloadCategories() {
		ArrayList<OurCategory> categories = mainMenuView.getCategories();
		if (categories != null) {
			try {
				ArrayList<OurCategory> categoriesFromDB = new CategoryDAO()
						.getCategories();
				categories.clear();
				categories.addAll(categoriesFromDB);
				if (mainMenuView.getAdapter() != null) {
					mainMenuView.getAdapter().notifyDataSetChanged();
				}
			} catch (DAOException e) {
				e.printStackTrace();
			}
		}
	}

	private void reloadContents() {
		ArrayList<OurContents> contents = detailView.getContentList();
		if (contents != null) {
			try {
				ArrayList<OurContents> contentsFromDB = new ContentDAO()
						.getOnlyContents();
				contents.clear();
				contents.addAll(contentsFromDB);
				if (detailView.getListAdapter() != null) { // && detailView.getListView() != null
//					detailView.getListView().setAdapter(new ContentsListAdapter(this, contentsFromDB));
					detailView.getListAdapter().notifyDataSetChanged();
				}
			} catch (DAOException e) {
				e.printStackTrace();
			}
		}
	}
}
