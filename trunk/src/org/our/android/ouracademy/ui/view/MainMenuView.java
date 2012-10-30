package org.our.android.ouracademy.ui.view;

import java.util.ArrayList;

import org.our.android.ouracademy.R;
import org.our.android.ouracademy.model.OurCategory;
import org.our.android.ouracademy.ui.adapter.CategoryListAdapter;
import org.our.android.ouracademy.ui.pages.SettingActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
*
* @author JiHoon, Moon
*
*/
public class MainMenuView extends FrameLayout implements OnClickListener {
	ArrayList<OurCategory> categoryList = null;
	ListView listView;
	TextView categoryTxtView;
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

		String[] temp = {"English", "Math", "Korean", "Social Study", "science", "Music", "Art",
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

		listView = (ListView)findViewById(R.id.category_listview);
		final CategoryListAdapter adapter = new CategoryListAdapter(getContext(), R.layout.layout_category_list_item,
			getCategoryListData());
		listView.setAdapter(adapter);
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

		listView.setOnScrollListener(onScrollChangedListener);
		listView.setOnTouchListener(onTouchListener);

		applyButton = (Button)findViewById(R.id.main_menu_apply_btn);

		View settingBtn = findViewById(R.id.setting_btn);
		settingBtn.setOnClickListener(this);
	}

	public void setApplyBtnListener(OnClickListener onClickListener) {
		if (applyButton != null) {
			applyButton.setOnClickListener(onClickListener);
		}
	}

	boolean isVisibleLastItem;

	OnTouchListener onTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {

			} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
				if (applyButton != null) {
					applyButton.setVisibility(View.GONE);
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				isVisibleLastItem = false;
				if (listView.getLastVisiblePosition() == listView.getCount() - 1) {
					isVisibleLastItem = true;
				}
				applyButton.setVisibility(View.VISIBLE);
			}
			return false;
		}
	};

	OnScrollListener onScrollChangedListener = new OnScrollListener() {
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
		}

		@Override
		public void onScrollStateChanged(final AbsListView view, int scrollState) {
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				if (isVisibleLastItem) {
					listView.postDelayed(new Runnable() {
						@Override
						public void run() {
							listView.smoothScrollToPosition(listView.getCount() - 1);
						}
					}, 100);
				}
			} else if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {

			} else if (scrollState == OnScrollListener.SCROLL_STATE_FLING) {

			}
		}
	};

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(getContext(), SettingActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		Activity activity = (Activity)getContext();
		activity.startActivityForResult(intent, 1);
	}
}
