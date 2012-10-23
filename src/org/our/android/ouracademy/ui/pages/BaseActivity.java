/*
 * CommonBaseFragmentActivity.java $version 2012. 06. 03
 *
 * Copyright 2010 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.our.android.ouracademy.ui.pages;

import org.our.android.ouracademy.util.ScreenInfo;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * 
 * @author
 */
public class BaseActivity extends Activity {
	private Toast toast;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			// ScreenInfo 를 전체적으로 많이 사용하기 때문에 만약 복구가 되는 순간이면 다시 값을 구해서 설정해준다.
			ScreenInfo.create(getBaseContext());
		}
	}

	@Override
	public void onStart() {
//    	Logger.d("TEST", "Activity Life cycle -> onStart");
    	super.onStart();
    }
    
    @Override
	protected void onResume() {
//    	Logger.d("TEST", "Activity Life cycle -> onResume");
		super.onResume();
		
    }
    
    @Override
	protected void onPause() {
//    	Logger.d("TEST", "Activity Life cycle -> onPause");
    	super.onPause();
    }
    
    @Override
	protected void onStop() {
//    	Logger.d("TEST", "Activity Life cycle -> onStop");
		super.onStop();
    }
    
    @Override
	public void onDestroy() {
//    	Logger.d("TEST", "Activity Life cycle -> onDestroy");
		super.onDestroy();
	}
	
	@Override
	public boolean onSearchRequested() {
		return false;
	};

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		return super.dispatchKeyEvent(event);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			System.exit(MODE_PRIVATE);
//			return (true); //finish back key action
//		}
		return super.onKeyUp(keyCode, event);
	}
	
//	@Override
//	public void onBackPressed() {
//		return;
//	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	public void showSingleShortToast(String message) {
		if (toast == null) {
			toast = Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT);
		} else {
			toast.setText(message);
			toast.setDuration(Toast.LENGTH_SHORT);
		}
		
		toast.show();
	}
	public void showSilngleLongToast(String message) {
		if (toast == null) {
			toast = Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG);
		} else {
			toast.setText(message);
			toast.setDuration(Toast.LENGTH_LONG);
		}
		
		toast.show();
	}
	
	public boolean showShortToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		return true;
	}
	public boolean showLongToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		return true;
	}
}
