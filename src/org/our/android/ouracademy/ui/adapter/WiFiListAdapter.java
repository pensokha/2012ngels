package org.our.android.ouracademy.ui.adapter;

import java.util.ArrayList;

import org.our.android.ouracademy.model.OurWifiDirectDevice;
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
	
	public ArrayList<OurWifiDirectDevice> deviceList;
	
	public WiFiListAdapter() {
		super();
		
		this.deviceList = new ArrayList<OurWifiDirectDevice>();
	}
	
	public WiFiListAdapter(ArrayList<OurWifiDirectDevice> deviceList) {
		super();
		
		if(deviceList == null){
			this.deviceList = new ArrayList<OurWifiDirectDevice>();
		}else{
			this.deviceList = deviceList;
		}
	}
	
	public ArrayList<OurWifiDirectDevice> getDeviceList() {
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
			WifiP2pDevice device = deviceList.get(position).device;
			
			view.setData(true, device.deviceName, deviceList.get(position).connectingState);
		}

		return convertView;
	}
}
