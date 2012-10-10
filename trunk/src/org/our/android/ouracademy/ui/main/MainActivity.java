package org.our.android.ouracademy.ui.main;

import org.our.android.ouracademy.OurPreferenceManager;
import org.our.android.ouracademy.R;
import org.our.android.ouracademy.p2p.P2PService;
import org.our.android.ouracademy.ui.common.BaseFragmentActivity;
import org.our.android.ouracademy.util.DbManager;
import org.our.android.ouracademy.wifidirect.WifiDirectWrapper;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends BaseFragmentActivity {
	public static final String OUR_CONTENT_DATA_CHANGED = "org.our.android.ouracademy.ui.main.OUR_CONTENT_DATA_CHANGED";
	
	private static final String TAG = "Main";
	private OurPreferenceManager pref;
	private WifiDirectWrapper wifidirectWrapper;
	private ComponentName serviceName;
	private final IntentFilter intentFilter = new IntentFilter();
	private ContentsDataChangedReciever reciever;
	
	ListView contentsListview;
	ContentsListAdapter contentsListAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUI();
		
		pref = OurPreferenceManager.getInstance();
		wifidirectWrapper = WifiDirectWrapper.getInstance();
		reciever = new ContentsDataChangedReciever();
		
		wifidirectWrapper.init(this);
		intentFilter.addAction(OUR_CONTENT_DATA_CHANGED);
	}
	
	private Cursor readContents(){
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
		
		FrameLayout layout = (FrameLayout)findViewById(R.id.layout_list);
		
		contentsListview = new ListView(this);
		contentsListAdapter = new ContentsListAdapter(this,	readContents(), CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		contentsListview.setAdapter(contentsListAdapter);
		
		contentsListview.setOnItemClickListener(itemClickListener);

		layout.addView(contentsListview);
	}

	// tmp method
	private void startP2pService() {
		if(pref.isTeacher() && serviceName == null){
			serviceName = startService(new Intent(this, P2PService.class));
		}
	}
	
	private void stopP2pService() {
		if(serviceName != null){
			Intent intent = new Intent();
			intent.setComponent(serviceName);
			
			if(stopService(intent))
			{
				Log.d(TAG, "Stop P2p Service");
			}
			serviceName = null;
		}
	}

	// tmp method
	private void alertMode() {
		new AlertDialog.Builder(this).setTitle("Mode Setting")
				.setPositiveButton("teacher", modeClickListenr)
				.setNegativeButton("student", modeClickListenr).show();
	}

	// tmp method
	private DialogInterface.OnClickListener modeClickListenr = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (which == -1) {
				pref.setTeacherMode();
			} else {
				pref.setStudentMode();
			}
			wifidirectWrapper.register();
			startP2pService();
		}
	};

	@Override
	protected void onResume() {
		super.onResume();

		if (pref.isSettingMode() == false) {
			alertMode();
		} else {
			wifidirectWrapper.register();
			startP2pService();
		}
		
		registerReceiver(reciever, intentFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();

		wifidirectWrapper.unregister();
		unregisterReceiver(reciever);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		stopP2pService();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> list, View view, int position, long id) {
			Cursor cursor = contentsListAdapter.getCursor();
			cursor.moveToPosition(position);
			String downloadFilePath = cursor.getString(cursor.getColumnIndex("FilePath"));
			
			if (!TextUtils.isEmpty(downloadFilePath)) {
				Toast.makeText(getBaseContext(), downloadFilePath, Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	
	public class ContentsDataChangedReciever extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			contentsListAdapter.changeCursor(readContents());
			contentsListAdapter.notifyDataSetChanged();
		}
	}
}
