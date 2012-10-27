package org.our.android.ouracademy.ui.view;

import java.util.ArrayList;

import org.our.android.ouracademy.R;
import org.our.android.ouracademy.model.OurCategory;
import org.our.android.ouracademy.ui.adapter.CategoryListAdapter;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;

/**
*
* @author JiHoon, Moon
*
*/
public class MainMenuView extends FrameLayout {
	ArrayList<OurCategory> categoryList = null;
	
	Button applyButton;
	
	public MainMenuView(Context context) {
		super(context);
		initUI();
	}

	public MainMenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initUI();
	}
	
	public ArrayList<OurCategory> getCategoryListData() {
		if (categoryList == null) {
			categoryList = new ArrayList<OurCategory>();
		}
		categoryList.clear();
		
		String[] temp = {"English", "Math", "Korean", " Social Study", "science", "Music", "Art",
				 "Physical education", "Practical course", "Ethics"};
		OurCategory ourCategory;
		for (String tt : temp) {
			ourCategory = new OurCategory();
			ourCategory.setCategoryTitleEng(tt);
			categoryList.add(ourCategory);
		}
		
		return categoryList;
	}

	private void initUI() {
		LayoutInflater.from(getContext()).inflate(R.layout.layout_main_menu, this, true);
		
		ListView listView = (ListView)findViewById(R.id.category_listview);
		final CategoryListAdapter adapter = new CategoryListAdapter(getContext(), R.layout.layout_category_list_item, getCategoryListData());
		listView.setAdapter(adapter);
		listView.setDivider(null);
		listView.setCacheColorHint(Color.TRANSPARENT);
		listView.setSelector(android.R.color.transparent);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> list, View view, int position, long id) {
				OurCategory ourCategory = categoryList.get(position);
				if (ourCategory.isChecked) {
					ourCategory.isChecked = false;
				} else {
					ourCategory.isChecked = true;
				}
				adapter.notifyDataSetChanged();
			}
		});
		
		applyButton = (Button) findViewById(R.id.main_menu_apply_btn);
	}
	
	public void setApplyBtnListener(OnClickListener onClickListener) {
		if (applyButton != null) {
			applyButton.setOnClickListener(onClickListener);
		}
	}
}
