package org.our.android.ouracademy.ui.adapter;

import java.util.ArrayList;

import org.our.android.ouracademy.model.OurContents;
import org.our.android.ouracademy.ui.view.PlayerListItemView;
import org.our.android.ouracademy.ui.view.PlayerListItemView.PlayerListItemViewListener;

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

	ArrayList<OurContents> contentsList = null;

	private PlayerListItemViewListener listener;

	@Override
	public int getCount() {
		if (contentsList == null || contentsList.isEmpty()) {
			return 0;
		}

		return contentsList.size();
	}

	@Override
	public Object getItem(int position) {
		if (contentsList == null) {
			return null;
		}
		return contentsList.get(position);
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

		if (convertView instanceof PlayerListItemView) {
			PlayerListItemView item = (PlayerListItemView)convertView;
			item.setData(contentsList.get(position));
			item.setOnPlayerListViewListener(listener);
		}

		return convertView;
	}

	public void setData(ArrayList<OurContents> contentsList) {
		this.contentsList = contentsList;
	}

	public void setOnPlayerListViewListener(PlayerListItemViewListener listener) {
		this.listener = listener;
	}

}
