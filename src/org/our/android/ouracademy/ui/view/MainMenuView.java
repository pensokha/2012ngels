package org.our.android.ouracademy.ui.view;

import java.util.ArrayList;

import org.our.android.ouracademy.R;
import org.our.android.ouracademy.dao.CategoryDAO;
import org.our.android.ouracademy.dao.DAOException;
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
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
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

	int aniResId = -1;
	

	CategoryListAdapter adapter;

	public MainMenuView(Context context) {
		super(context);
		initUI();
	}

	public MainMenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initUI();
	}
	
	public ArrayList<OurCategory> getCategories() {
		return categoryList;
	}
	
	public CategoryListAdapter getAdapter(){
		return adapter;
	}

	public ArrayList<OurCategory> getCategoryListData() {
		CategoryDAO categoryDao = new CategoryDAO();
		try {
			categoryList = categoryDao.getCategories();
		} catch (DAOException e) {
			e.printStackTrace();
			categoryList = new ArrayList<OurCategory>();
		}

		return categoryList;
	}

	private void initUI() {
		LayoutInflater.from(getContext()).inflate(R.layout.layout_main_menu, this, true);

		listView = (ListView)findViewById(R.id.category_listview);

		View apply = LayoutInflater.from(getContext()).inflate(R.layout.main_menu_dummy_item, null);
		listView.addFooterView(apply);

		adapter = new CategoryListAdapter(getContext(), R.layout.layout_category_list_item,
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
//					applyButton.setVisibility(View.GONE);
					startAnimation(false);
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				isVisibleLastItem = false;
				if (listView.getLastVisiblePosition() == listView.getCount() - 1) {
					isVisibleLastItem = true;
				}
//				applyButton.setVisibility(View.VISIBLE);
				startAnimation(true);
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

	public void startAnimation(boolean showView) {

		if (showView) {
			// 보이고 있는 데 또 보여라는 요청 들오면 해당 애니메이션 무시.
			if (aniResId == R.anim.push_up_in) {
				return;
			}
			aniResId = R.anim.push_up_in;
		} else {
			// push up out 애니메이션을 수행했는데 또 요청이 들어오면 무시한다.
			if (this.isShown() == false || aniResId == R.anim.push_up_out) {
				return;
			}
			aniResId = R.anim.push_up_out;
		}

		Animation animation = AnimationUtils.loadAnimation(getContext(), aniResId);
		animation.setAnimationListener(animationListener);
		animation.setDuration(300);

		applyButton.startAnimation(animation);
	}

	public void beforeAnimation() {
		applyButton.setVisibility(View.VISIBLE);
	}

	public void afterAnimation() {
		if (aniResId == R.anim.push_up_out) {
			applyButton.setVisibility(View.GONE);
		}
	}

	AnimationListener animationListener = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			beforeAnimation();
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			afterAnimation();
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}
	};
}
