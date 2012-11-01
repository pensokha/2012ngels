package org.our.android.ouracademy.ui.pages;

import java.util.List;

import org.our.android.ouracademy.R;
import org.our.android.ouracademy.ui.adapter.ContentsListAdapter;
import org.our.android.ouracademy.ui.view.MainDetailView;
import org.our.android.ouracademy.ui.view.MainMenuView;
import org.our.android.ouracademy.youtubedownloader.YoutubeDownloadManager;

import android.app.ActivityManager;
import android.app.DownloadManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
	private IntentFilter intentFilter;
	
	private static boolean closeFlag = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUI();
		
		reciever = new OurDataChangeReceiver();
		intentFilter = new IntentFilter();
		intentFilter.addAction(OurDataChangeReceiver.OUR_DATA_CHANGED);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(reciever, intentFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		closeHandler.removeMessages(0);
		unregisterReceiver(reciever);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
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
		final ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

		// stop running service inside current process.
		List<RunningServiceInfo> serviceList = am.getRunningServices(100);
		for (RunningServiceInfo service : serviceList) {
			if (service.pid == android.os.Process.myPid()) {
				Intent stop = new Intent();
				stop.setComponent(service.service);
				stopService(stop);
			}
		}

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
					break;
				case ACTION_CATEGORY_CHANGED:
					break;
				case ACTION_CONTENT_CHANGED:
					break;
				case ACTION_DOWNLOADING:
					break;
				default:
					break;
				};
			}
		}
	}
}
