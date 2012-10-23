package org.our.android.ouracademy.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import org.our.android.ouracademy.R;
import org.our.android.ouracademy.model.OurCategory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
*
* @author JiHoon, Moon
*
*/
public class CategoryListAdapter extends ArrayAdapter<OurCategory> {
	public Context context = null;
	
	class ItemHolder {
    	TextView txt;
    	ImageView icon;
    	ImageView check;
    }
	
	public CategoryListAdapter(Context context, int textViewResourceId, ArrayList<OurCategory> menuList) {
		super(context, textViewResourceId, menuList);
		this.context = context;
	}
	
	@Override
    public View getView(final int position, View convertView, ViewGroup parent) {
		ItemHolder itemHolder = new ItemHolder();
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_category_list_item, parent, false);
			itemHolder.txt = (TextView) convertView.findViewById(R.id.category_txt);
			itemHolder.icon = (ImageView) convertView.findViewById(R.id.category_icon);
			itemHolder.check = (ImageView) convertView.findViewById(R.id.category_check);
			convertView.setTag(itemHolder);
		} else {
			itemHolder = (ItemHolder)convertView.getTag();
		}
		
		OurCategory ourCategory = getItem(position);
		
	itemHolder.txt.setText(ourCategory.getCategoryTitleEng());
		itemHolder.icon.setImageResource(R.drawable.subject_icon_math);
		
		return convertView;
	}

}
