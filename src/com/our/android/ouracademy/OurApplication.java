package com.our.android.ouracademy;

import android.app.Application;
import android.util.Log;

import com.our.android.ouracademy.util.DbManager;
import com.our.android.ouracademy.util.ScreenInfo;

/**
 * 
 * @author JiHoon, Moon on NTS
 * 
 */
public class OurApplication extends Application {
	private static boolean isTestMode = true;
	private static OurApplication mOurApplication;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mOurApplication = this;

		OurPreferenceManager.getInstance().initPreferenceData(this);
		
		//DbManager open
		DbManager.getInstance().open(getApplicationContext());
		
		ScreenInfo.create(this);
	}
	
	public static boolean isTestMode() {
		return isTestMode;
	}
	
	public static OurApplication getInstance() {
		if (null == mOurApplication) {
			Log.d("AeonApplication", "Application was killed!!!");
		}
		
		return mOurApplication;
	}
}
