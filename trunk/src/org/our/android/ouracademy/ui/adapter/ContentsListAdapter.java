package org.our.android.ouracademy.ui.adapter;

import java.util.ArrayList;

import org.our.android.ouracademy.R;
import org.our.android.ouracademy.model.OurContent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class ContentsListAdapter extends BaseAdapter {
	 private Context context;
	ArrayList<OurContent> contentsList = null;
	
	private LayoutInflater inflater;
	
	class ViewHolder {
    	RelativeLayout currencyListLayout;
        TextView text; //통화코드
        TextView description; //통화설명
        ImageView icon; //라디오버튼
    }
	
	public ContentsListAdapter(Context context, ArrayList<OurContent> contentsList) {
		this.context = context;
		this.contentsList = contentsList;
		
		inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		if (contentsList == null) {
			return 0;	
		}
		return contentsList.size();
	}

	@Override
	public OurContent getItem(int position) {
		if (contentsList == null) {
			return null;	
		}
		if (position < 0 && getCount()-1 > position ) {
			return null;
		}
		return (OurContent)contentsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.layout_contents_list_item, null);
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.text);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.text.setText(contentsList.get(position).getId());
		
		return convertView;
	}
}
