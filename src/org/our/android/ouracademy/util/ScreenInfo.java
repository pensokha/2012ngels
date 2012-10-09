package org.our.android.ouracademy.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.ViewConfiguration;

public class ScreenInfo {
	static public void create(Context context) {
		if (mWidth == 0) { // 값이 없을 때만 초기화 시켜준다.
			DisplayMetrics metrics = context.getResources().getDisplayMetrics();
			mDensity = metrics.density;
			mWidth = metrics.widthPixels;
			mHeight = metrics.heightPixels;
			mDensityDPI = metrics.densityDpi;
			mSlop = ViewConfiguration.get(context).getScaledTouchSlop();
			//2011.07.27문지훈 알람창 높이 필요해서 추가
			mNotificationBarHeight = getNotificationBarHeight(context);
		}
	}

	static public int dp2px(float dp) {
		int px = (int)(mDensity * dp);
		if (0 < dp && px == 0) {
			px = 1;
		}
		return px;
	}

	static public float px2dp(int px) {
		float dp = (px / mDensity);
		return (dp);
	}

	static public boolean isMedDPI() {
		return (mDensityDPI == DisplayMetrics.DENSITY_MEDIUM);
	}

	static public boolean isLandscape(Context context) {
		if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			return (true);
		}
		return (false);
	}

	static public boolean isPortrait(Context context) {
		if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			return (true);
		}
		return (false);
	}

	static public int getWidth(Context context) {
		return (context.getResources().getDisplayMetrics().widthPixels);
	}

	static public int getNotificationBarHeight(Context context) {
		int notificationBarResources[] = {android.R.drawable.stat_sys_phone_call, android.R.drawable.stat_notify_call_mute, android.R.drawable.stat_notify_sdcard, android.R.drawable.stat_notify_sync, android.R.drawable.stat_notify_missed_call, android.R.drawable.stat_sys_headset, android.R.drawable.stat_sys_warning};
		int notificationBarHeight = -1;
		for (int i = 0; i < notificationBarResources.length; i++) {
			try {
				Drawable phoneCallIcon = context.getResources().getDrawable(notificationBarResources[i]);
				if ((notificationBarHeight = phoneCallIcon.getIntrinsicHeight()) != -1) {
					break;
				}
			} catch (Resources.NotFoundException e) {

			}
		}
		return notificationBarHeight;
	}

	static public float mDensity = 1.5f;

	static public int mWidth = 0;
	static public int mHeight = 0;
	static public int mSlop;
	static public int mNotificationBarHeight;

	static public int mDensityDPI = DisplayMetrics.DENSITY_HIGH;
}
