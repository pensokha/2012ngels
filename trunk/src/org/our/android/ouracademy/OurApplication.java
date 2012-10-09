package org.our.android.ouracademy;

import org.our.android.ouracademy.util.DbManager;
import org.our.android.ouracademy.util.ScreenInfo;

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
		//ㅇㅇs

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
