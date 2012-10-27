package org.our.android.ouracademy.ui.pages;

import org.our.android.ouracademy.R;

import android.os.Bundle;
import android.view.View;

public class SettingActivity extends BaseActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUI();
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
		setContentView(R.layout.activity_setting);
	}
	
	public void onClickMode(View view) {
		showSingleShortToast("모드 설정");
	}
}
