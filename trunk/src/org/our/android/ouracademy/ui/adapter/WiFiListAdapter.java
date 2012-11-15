package org.our.android.ouracademy.ui.adapter;

import java.util.ArrayList;

import org.our.android.ouracademy.ui.view.SetupWifiListItemView;

import android.net.wifi.p2p.WifiP2pDevice;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 
 * 
 * @author jyeon
 *
 */
public class WiFiListAdapter extends BaseAdapter {
	
	public ArrayList<WifiP2pDevice> deviceList;
	
	public WiFiListAdapter() {
		super();
		
		this.deviceList = new ArrayList<WifiP2pDevice>();
	}
	
	public WiFiListAdapter(ArrayList<WifiP2pDevice> deviceList) {
		super();
		
		if(deviceList == null){
			this.deviceList = new ArrayList<WifiP2pDevice>();
		}else{
			this.deviceList = deviceList;
		}
	}
	
	public ArrayList<WifiP2pDevice> getDeviceList() {
		return deviceList;
	}

	@Override
	public int getCount() {
		if(deviceList == null)
			return 0;
		
		return deviceList.size();
	}

	@Override
	public Object getItem(int position) {
		if(deviceList == null)
			return null;
		return deviceList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = new SetupWifiListItemView(parent.getContext());
		}

		if (convertView instanceof SetupWifiListItemView) {
			SetupWifiListItemView view = (SetupWifiListItemView)convertView;
			view.setData(true, deviceList.get(position).deviceName, (deviceList.get(position).status == WifiP2pDevice.CONNECTED ? 0 : 3));
		}

		return convertView;
	}
}
