package org.our.android.ouracademy.ui.adapter;

import org.our.android.ouracademy.ui.view.SetupWifiListItemVew;

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

	@Override
	public int getCount() {
		// test code
		return 10;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = new SetupWifiListItemVew(parent.getContext());
		}

		if (convertView instanceof SetupWifiListItemVew) {
			SetupWifiListItemVew view = (SetupWifiListItemVew)convertView;
			String testId = position + " ";
			view.setData(true, testId, 0);
		}

		return convertView;
	}
}
