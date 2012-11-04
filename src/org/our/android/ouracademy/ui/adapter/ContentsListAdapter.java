package org.our.android.ouracademy.ui.adapter;

import java.util.ArrayList;

import org.our.android.ouracademy.R;
import org.our.android.ouracademy.model.OurContents;
import org.our.android.ouracademy.ui.view.ContentsView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
*
* @author JiHoon, Moon
*
*/
public class ContentsListAdapter extends BaseAdapter  {
	private Context context;
	ArrayList<OurContents> contentsList = null;
	
	private LayoutInflater inflater;
	
	public static final int CELL_PER_ITEM = 2;
	
	class ObjViewHolder {
		ContentsView[] itemHolderList = new ContentsView[CELL_PER_ITEM];
	}
	
	int layoutIds[] = { R.id.layout_content_01, R.id.layout_content_02 };
	
	public ContentsListAdapter(Context context, ArrayList<OurContents> contentsList) {
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
	public OurContents getItem(int position) {
		if (contentsList == null) {
			return null;	
		}
		if (position < 0 && getCount()-1 > position ) {
			return null;
		}
		return (OurContents)contentsList.get(position * CELL_PER_ITEM);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//set view holder pattern
		ObjViewHolder holder = new ObjViewHolder();;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.layout_contents_list_item, null);
			
			for (int idx = 0; idx < CELL_PER_ITEM; idx++) {
				ContentsView contentsView = new ContentsView(context);
				holder.itemHolderList[idx] = contentsView;
				
				ViewGroup dd = (ViewGroup)convertView.findViewById(layoutIds[idx]);
				dd.addView(contentsView);
			}
			
			convertView.setTag(holder);
		} else {
			holder = (ObjViewHolder)convertView.getTag();
		}
		
		int itemSize = contentsList.size();
		
		//set Layout data & ui
		for (int i = 0; i < CELL_PER_ITEM; i++) {
			final int currentPositionOfItem = position * CELL_PER_ITEM + i;
			final ContentsView contentsView = holder.itemHolderList[i];
			OurContents curModel = contentsList.get(currentPositionOfItem);
			
			if (currentPositionOfItem < itemSize) {
				contentsView.setVisibility(View.VISIBLE);
				contentsView.setContentsData(curModel);
			} else {
				contentsView.setVisibility(View.GONE);
			}
		}
		return convertView;
	}
	
	public void notifyDataSetChanged(ArrayList<OurContents> contentsList) {
		this.contentsList = contentsList;
		super.notifyDataSetChanged();
	}
}
