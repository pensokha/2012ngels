package org.our.android.ouracademy.ui.adapter;

import org.our.android.ouracademy.ui.view.PlayerListItemView;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 
 * 
 * @author jyeon
 *
 */
public class PlayListAdapter extends BaseAdapter {

	@Override
	public int getCount() {
		// test code
		return 20;
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
			convertView = new PlayerListItemView(parent.getContext());
		}

		return convertView;
	}

}
