package org.our.android.ouracademy.ui.main;

import org.our.android.ouracademy.OurPreferenceManager;
import org.our.android.ouracademy.R;
import org.our.android.ouracademy.p2p.P2PService;
import org.our.android.ouracademy.ui.common.BaseFragmentActivity;
import org.our.android.ouracademy.wifidirect.WifiDirectWrapper;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends BaseFragmentActivity {
	private static final String TAG = "Main";
	
	private OurPreferenceManager pref;
	private WifiDirectWrapper wifidirectWrapper;
	private ComponentName serviceName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		pref = OurPreferenceManager.getInstance();
		wifidirectWrapper = WifiDirectWrapper.getInstance();

		wifidirectWrapper.init(this);
	}

	// tmp method
	private void startP2pService() {
		if(pref.isTeacher() && serviceName == null){
			Log.d(TAG, "start service from main!");
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
	}

	@Override
	protected void onPause() {
		super.onPause();

		wifidirectWrapper.unregister();
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
}
