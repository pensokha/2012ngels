package org.our.android.ouracademy;

import org.our.android.ouracademy.manager.DataManagerFactory;
import org.our.android.ouracademy.util.DbManager;
import org.our.android.ouracademy.util.ScreenInfo;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

/**
 * 
 * @author JiHoon, Moon on NTS
 * 
 */
public class OurApplication extends Application {
	private static boolean isTestMode = true;
	private static OurApplication mOurApplication;
	
	private String localeLangueage; 

	@Override
	public void onCreate() {
		super.onCreate();

		mOurApplication = this;

		OurPreferenceManager pref = OurPreferenceManager.getInstance();
		pref.initPreferenceData(this);
//		pref.setTeacherMode();
		DbManager.getInstance().open(this);

		// 화면 정보
		ScreenInfo.create(this);
		
		Log.d("Test", "onCreate");
		DataManagerFactory.getDataManager().startService(this);
		
		updateLocaleLangueage();
	}

	public static boolean isTestMode() {
		return isTestMode;
	}
	
	@Override
	public void onTerminate() {
		DataManagerFactory.getDataManager().stopService(this);
		
		super.onTerminate();
	}

	public static OurApplication getInstance() {
		if (null == mOurApplication) {
			Log.d("Application", "Application was killed!!!");
		}
		return mOurApplication;
	}
	
	public void updateLocaleLangueage() {
		localeLangueage = getResources().getConfiguration().locale.getLanguage();
//		Locale.getDefault().getCountry();
//		Locale.getDefault().getLanguage();
		
	}
	
	public String getLocaleLangueage() {
		if (TextUtils.isEmpty(localeLangueage)) {
			updateLocaleLangueage();
		}
		return localeLangueage;
	}
}
