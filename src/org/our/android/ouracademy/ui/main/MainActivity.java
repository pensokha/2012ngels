package org.our.android.ouracademy.ui.main;

import org.our.android.ouracademy.OurPreferenceManager;
import org.our.android.ouracademy.R;
import org.our.android.ouracademy.ui.pages.BaseFragmentActivity;
import org.our.android.ouracademy.util.DbManager;
import org.our.android.ouracademy.wifidirect.WifiDirectWrapper;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends BaseFragmentActivity {
	public static final String OUR_CONTENT_DATA_CHANGED = "org.our.android.ouracademy.ui.main.OUR_CONTENT_DATA_CHANGED";

	private static final String TAG = "Main";
	private OurPreferenceManager pref;
	private Context context;

	ListView contentsListview;
	ContentsListAdapter contentsListAdapter;

	// Wifi Direct variable
	private ContentsDataChangedReciever reciever;
	private final IntentFilter intentFilter = new IntentFilter();
	private WifiDirectWrapper wifidirectWrapper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUI();

		pref = OurPreferenceManager.getInstance();
		wifidirectWrapper = WifiDirectWrapper.getInstance();
		context = this;
		reciever = new ContentsDataChangedReciever();

		wifidirectWrapper.init(this);
		intentFilter.addAction(OUR_CONTENT_DATA_CHANGED);
	}

	private Cursor readContents() {
		StringBuilder query = new StringBuilder();
		query.append("select t1._id, t1.ContentId AS ContentId, t2.FileId AS FileId, t2.FilePath AS FilePath ");
		query.append("from CONTENTS_TBL t1 ");
		query.append("left join DOWNLOAD_TBL t2 on t1.ContentId = t2.FileId");
		// load File, Download table data
		Cursor cursor = DbManager.getInstance().getDB()
				.rawQuery(query.toString(), null);
		return cursor;
	}

	private void initUI() {
		setContentView(R.layout.activity_main);

		// FrameLayout layout = (FrameLayout) findViewById(R.id.layout_list);
		//
		// contentsListview = new ListView(this);
		// contentsListAdapter = new ContentsListAdapter(this, readContents(),
		// CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		// contentsListview.setAdapter(contentsListAdapter);
		//
		// contentsListview.setOnItemClickListener(itemClickListener);
		//
		// layout.addView(contentsListview);
	}

	private void settingMode() {
		new AlertDialog.Builder(this).setTitle("Mode Setting")
				.setPositiveButton("teacher", modeClickListenr)
				.setNegativeButton("student", modeClickListenr).show();
	}

	ActionListener disconnectListener = new ActionListener() {

		@Override
		public void onSuccess() {
			wifidirectWrapper.setting();
		}

		@Override
		public void onFailure(int reason) {
			Toast.makeText(context, "Fail to diconnect previous connection",
					Toast.LENGTH_SHORT).show();
		}
	};

	private DialogInterface.OnClickListener modeClickListenr = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (which == -1) {
				pref.setTeacherMode();
			} else {
				pref.setStudentMode();
			}
			wifidirectWrapper.resetService(disconnectListener);
//			wifidirectWrapper.setting();
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		if (wifidirectWrapper.isInitSuccess() == false) {
			wifidirectWrapper.init(this);
		}
		
		if (wifidirectWrapper.isInitSuccess() == true) {
			wifidirectWrapper.setting();
		}

		registerReceiver(reciever, intentFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (wifidirectWrapper.isInitSuccess() == true) {
			wifidirectWrapper.unsetting();
		}

		unregisterReceiver(reciever);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_settings:
				settingMode();
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> list, View view, int position,
				long id) {
			Cursor cursor = contentsListAdapter.getCursor();
			cursor.moveToPosition(position);
			String downloadFilePath = cursor.getString(cursor
					.getColumnIndex("FilePath"));

			if (!TextUtils.isEmpty(downloadFilePath)) {
				Toast.makeText(getBaseContext(), downloadFilePath,
						Toast.LENGTH_SHORT).show();
			} else {
				wifidirectWrapper.download(cursor.getString(cursor
						.getColumnIndex("ContentId")));
			}
		}
	};

	/******
	 * 
	 * @author hyeongseokLim
	 * 
	 */
	public class ContentsDataChangedReciever extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			contentsListAdapter.changeCursor(readContents());
			contentsListAdapter.notifyDataSetChanged();
		}
	}
}
