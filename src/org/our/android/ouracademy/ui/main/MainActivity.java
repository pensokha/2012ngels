package org.our.android.ouracademy.ui.main;

import org.our.android.ouracademy.OurPreferenceManager;
import org.our.android.ouracademy.R;
import org.our.android.ouracademy.ui.common.BaseFragmentActivity;
import org.our.android.ouracademy.wifidirect.WifiDirectWrapper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends BaseFragmentActivity {
	private OurPreferenceManager pref;
	private WifiDirectWrapper wifidirectWrapper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		pref = OurPreferenceManager.getInstance();
		wifidirectWrapper = WifiDirectWrapper.getInstance();

		wifidirectWrapper.init(this);
	}

	//tmp method
	private void alertMode() {
		new AlertDialog.Builder(this).setTitle("Mode Setting")
				.setPositiveButton("teacher", modeClickListenr)
				.setNegativeButton("student", modeClickListenr).show();
	}

	//tmp method
	private DialogInterface.OnClickListener modeClickListenr = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if(which == -1){
				pref.setTeacherMode();
			}else{
				pref.setStudentMode();
			}
			wifidirectWrapper.register();
		}
	};

	@Override
	protected void onResume() {
		super.onResume();

		if (pref.isSettingMode() == false) {
			alertMode();
		} else {
			wifidirectWrapper.register();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		wifidirectWrapper.unregister();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
