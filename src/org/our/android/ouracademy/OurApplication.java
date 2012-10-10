package org.our.android.ouracademy;

import org.our.android.ouracademy.manager.OurDownloadManager;
import org.our.android.ouracademy.manager.TestFileDbCreate;
import org.our.android.ouracademy.util.DbManager;
import org.our.android.ouracademy.util.ScreenInfo;

import android.app.Application;
import android.util.Log;

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
		//테스트할 임시 컨텐츠 테이블을 만든다.
		TestFileDbCreate.createContentsTable();
		TestFileDbCreate.setTestContentsData();
		
		OurDownloadManager downloadManager = new OurDownloadManager();
		downloadManager.updateStorage();
		//화면 정보 
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
