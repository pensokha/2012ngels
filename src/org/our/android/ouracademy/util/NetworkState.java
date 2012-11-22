package org.our.android.ouracademy.util;

import org.our.android.ouracademy.wifidirect.WifiDirectWrapper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pInfo;
import android.provider.Settings;

public class NetworkState {

	static public boolean isAirplainMode(Context context) {
		boolean isEnabled = Settings.System.getInt(
				context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON,
				0) == 1;
		return (isEnabled);
	}

	// 사용자에 의해서 Disabled 된 경우
	static public boolean isNetworkDisabled(Context context) {
		return (isAirplainMode(context) == false && isWifiConnected(context) == false);
	}

	static public boolean isDataConnected(Context context) {
		boolean connected = false;
		try {
			ConnectivityManager manager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = manager.getActiveNetworkInfo();
			if (info != null) {
				connected = manager.getActiveNetworkInfo().isConnected();
			}
			/*
			 * NetworkInfo mobile = null; NetworkInfo wifi = null; return
			 * cm.getActiveNetworkInfo().isConnectedOrConnecting(); if (manager
			 * != null) { wifi =
			 * manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI); if
			 * (wifi.isConnected()) { return (true); }
			 * 
			 * mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			 * if (mobile != null && mobile.isConnected()) { return (true); }
			 * 
			 * mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);
			 * //for if (mobile != null && mobile.isConnected()) { return
			 * (true); } }
			 */
		} catch (Exception err) {
			err.printStackTrace();
		}
		return connected;
	}

	static public boolean is3GConnected(Context context) {
		NetworkInfo mobile = null;
		NetworkInfo wifi = null;
		try {
			ConnectivityManager manager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (manager != null) {
				wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				if (wifi.isConnected()) {
					return (true);
				}

				mobile = manager
						.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				if (mobile != null && mobile.isConnected()) {
					return (true);
				}
			}
		} catch (Exception err) {
			err.printStackTrace();
		}
		return (false);
	}
	
	static public boolean isWifiAvailable(Context context) {
		// NetworkInfo mobile = null;
		NetworkInfo wifi = null;
		try {
			ConnectivityManager manager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (manager != null) {
				wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				if (wifi.isAvailable()) {
					return (true);
				}
			}
		} catch (Exception err) {
			err.printStackTrace();
		}
		return (false);
	}

	static public boolean isWifiConnected(Context context) {
		// NetworkInfo mobile = null;
		NetworkInfo wifi = null;
		try {
			ConnectivityManager manager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (manager != null) {
				wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				if (wifi.isConnected()) {
					return (true);
				}
			}
		} catch (Exception err) {
			err.printStackTrace();
		}
		return (false);
	}

	static public boolean isWifiDirectConnected() {
		WifiP2pInfo info = WifiDirectWrapper.getInstance().getInfo();
		if(info == null){
			return false;
		}else{
			return info.groupOwnerAddress != null ? true : false; 
		}
	}
	
	static public boolean isWifiDirectEnabled(){
		return WifiDirectWrapper.getInstance().isWifidirectEnable;
	}
}
