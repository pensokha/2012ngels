package org.our.android.ouracademy.ui.adapter;

import java.util.ArrayList;

import org.our.android.ouracademy.R;
import org.our.android.ouracademy.model.OurCategory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
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
		
//		Spanned category = Html.fromHtml(ourCategory.getCategoryTitleEng() + "<img src=\"icon\" width=50 height=50>", imageGetter, null);
		CharSequence category = Html.fromHtml(context.getResources().getString(R.string.category_name_num, 
				ourCategory.getCategoryTitleEng(), ourCategory.getNumOfContents()));
		itemHolder.txt.setText(category);
		itemHolder.icon.setImageResource(R.drawable.subject_icon_math);
		
		if (ourCategory.isChecked) {
			itemHolder.check.setVisibility(View.VISIBLE);
		} else {
			itemHolder.check.setVisibility(View.INVISIBLE);
		}
		
		return convertView;
	}
	
	
	ImageGetter imageGetter = new ImageGetter() {
		@Override
		public Drawable getDrawable(String source) {
			if (("icon").equals(source)) {
//				TextView text = new TextView(context);
//				text.setText("7");
//				text.setBackgroundResource(R.drawable.book_download_progress01);
//				text.buildDrawingCache();
//				text.requestLayout();
//				Bitmap bitmap = text.getDrawingCache();
//				Drawable drawable = (Drawable)new BitmapDrawable(text.getResources(), bitmap); 
				
				Drawable drawable = getContext().getResources().getDrawable(R.drawable.book_download_progress01);
				drawable.setBounds( 0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight() );
				return drawable;
			}
			return null;
		}
	};
}
