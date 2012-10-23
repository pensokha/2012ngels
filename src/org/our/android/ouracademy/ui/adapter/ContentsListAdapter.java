package org.our.android.ouracademy.ui.adapter;

import java.util.ArrayList;

import org.our.android.ouracademy.R;
import org.our.android.ouracademy.model.OurContent;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class ContentsListAdapter extends BaseAdapter {
	private Context context;
	ArrayList<OurContent> contentsList = null;
	
	private LayoutInflater inflater;
	
	public static final int CELL_PER_ITEM = 2;
	
	class ObjViewHolder {
		TextView tagTitle01;
		TextView tagTitle02;
		ItemHolder[] itemHolderList = new ItemHolder[CELL_PER_ITEM];
	}


	int layoutIds[] = {
		R.id.layout_content_01,
		R.id.layout_content_02
	};

	int contentTitleIds[] = {
		R.id.txt_content_01,
		R.id.txt_content_02
	};
	
	class ItemHolder {
    	RelativeLayout itemCellLayout;
        TextView contentTitle; 
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
		int count = contentsList.size() / CELL_PER_ITEM;
		count = count + (contentsList.size() % CELL_PER_ITEM > 0 ? 1 : 0);
		return count;
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
		int itemSize = contentsList.size();
		if (position * CELL_PER_ITEM >= itemSize) {
			return convertView;
		}
		
		ObjViewHolder holder = new ObjViewHolder();;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.layout_contents_list_item, null);
			
			for (int idx = 0; idx < CELL_PER_ITEM; idx++) {
				ItemHolder itemHolder = new ItemHolder();
				itemHolder.itemCellLayout = (RelativeLayout)convertView.findViewById(layoutIds[idx]);
				itemHolder.contentTitle = (TextView)convertView.findViewById(contentTitleIds[idx]);
				holder.itemHolderList[idx] = itemHolder;
			}
			
			holder.tagTitle01 = (TextView)convertView.findViewById(R.id.txt_list_tag_01);
			
			convertView.setTag(holder);
		} else {
			holder = (ObjViewHolder)convertView.getTag();
		}
		
		for (int i = 0; i < CELL_PER_ITEM; i++) {
			final int currentPositionOfItem = position * CELL_PER_ITEM + i;
			if (currentPositionOfItem < itemSize) {
				holder.itemHolderList[i].itemCellLayout.setVisibility(View.VISIBLE);
				OurContent model = contentsList.get(currentPositionOfItem);
				
//				holder.itemHolderList[i].itemCellLayout.setBackgroundResource();
				holder.itemHolderList[i].contentTitle.setText(model.getSubjectEng());
			} else {
				
				holder.itemHolderList[i].itemCellLayout.setVisibility(View.GONE);
			}
		}
		return convertView;
	}
}
