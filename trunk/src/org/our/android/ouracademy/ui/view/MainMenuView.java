package org.our.android.ouracademy.ui.view;

import java.util.ArrayList;

import org.our.android.ouracademy.OurPreferenceManager;
import org.our.android.ouracademy.R;
import org.our.android.ouracademy.constants.MatchCategoryColor;
import org.our.android.ouracademy.dao.CategoryDAO;
import org.our.android.ouracademy.dao.DAOException;
import org.our.android.ouracademy.model.OurCategory;
import org.our.android.ouracademy.ui.adapter.CategoryListAdapter;
import org.our.android.ouracademy.ui.pages.MainActivity;
import org.our.android.ouracademy.ui.pages.SettingActivity;
import org.our.android.ouracademy.ui.pages.TutorialActivity;
import org.our.android.ouracademy.ui.widget.NCTextView;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;

/**
 * 
 * @author JiHoon, Moon
 * 
 */
public class MainMenuView extends FrameLayout implements OnClickListener {
	ArrayList<OurCategory> categoryList = null;
	ListView listView;
	NCTextView categoryTxtView;
	// View applyButton;
	View refreshBtn;

	int aniResId = -1;

	CategoryListAdapter adapter;

	private boolean isEnabled = false;

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

	public CategoryListAdapter getAdapter() {
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

		MatchCategoryColor.matchColor(categoryList);
		return categoryList;
	}

	private void initUI() {
		LayoutInflater.from(getContext()).inflate(R.layout.layout_main_menu, this, true);

		listView = (ListView)findViewById(R.id.category_listview);

		View header = LayoutInflater.from(getContext()).inflate(
				R.layout.layout_category_header_item, null);
		header.setTag(false);
		listView.addHeaderView(header);

		adapter = new CategoryListAdapter(getContext(), R.layout.layout_category_list_item, getCategoryListData());

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> list, View view,
					int position, long id) {
				if (categoryList.isEmpty() || position == 0) {
					return;
				}

				for (int i = 0; i < categoryList.size(); i++) {
					OurCategory ourCategory = categoryList.get(i);
					if (position - 1 == i) {
						ourCategory.isChecked = true;

						// Save Selected Item
						OurPreferenceManager.getInstance()
								.setSelecetedCategory(
										ourCategory.getCategoryId());
					} else {
						ourCategory.isChecked = false;
					}
				}
				adapter.notifyDataSetChanged();
				onApplyClickListener.onClick(null);
			}
		});

		// listView.setOnScrollListener(onScrollChangedListener);
		// listView.setOnTouchListener(onTouchListener);

		// applyButton = findViewById(R.id.main_menu_apply_btn);

		View guideBtn = findViewById(R.id.guide_btn);
		refreshBtn = findViewById(R.id.refresh_btn);
		View settingBtn = findViewById(R.id.setting_btn);

		guideBtn.setOnClickListener(this);
		refreshBtn.setOnClickListener(this);
		settingBtn.setOnClickListener(this);

		//set selection saved cateogory
		String laseSeletedCategoryId = OurPreferenceManager.getInstance().getSelecetedCategory();
		for (int i = 0; i < categoryList.size(); i++) {
			if (categoryList.get(i).getCategoryId().equals(laseSeletedCategoryId)) {
				listView.setSelection(i);
				break;
			}
		}
	}
	
	public void setRefreshBtnStatus(boolean isConnected, boolean isEnabled) {
		this.isEnabled = isEnabled;
		
		if(isEnabled && isConnected){
			refreshBtn.setBackgroundResource(R.drawable.btn_main_refresh_selector_01);
		}else{
			refreshBtn.setBackgroundResource(R.drawable.btn_main_refresh_selector_02);
		}
	}

	private void onClickRefreshBtnStatus() {
		if(isEnabled){
			Intent intent = new Intent(getContext(), SettingActivity.class);
			intent.putExtra(SettingActivity.INTENTKEY_WIFI_MODE, true);
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			Activity activity = (Activity)getContext();
			activity.startActivityForResult(intent, 1);
		}else{
			Intent wifiIntent = new Intent("android.settings.WIFI_SETTINGS");
			wifiIntent.addCategory("android.intent.category.DEFAULT");
			wifiIntent.setFlags(0x30800000);
			wifiIntent.setComponent(new ComponentName("com.android.settings",
				"com.android.settings.wifi.WifiSettings"));
			getContext().startActivity(wifiIntent);
		}
	}

	OnClickListener onApplyClickListener;

	public void setApplyBtnListener(OnClickListener onClickListener) {
		if (onClickListener != null) {
			this.onApplyClickListener = onClickListener;
			// applyButton.setOnClickListener(onClickListener);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		Intent intent;
		
		switch (id) {
			case R.id.guide_btn:
				intent = new Intent(getContext(), TutorialActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				getContext().startActivity(intent);
				break;
			case R.id.refresh_btn:
				onClickRefreshBtnStatus();
				break;
			case R.id.setting_btn:
				intent = new Intent(getContext(), SettingActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				Activity activity = (Activity)getContext();
				activity.startActivityForResult(intent, MainActivity.SETTING_ACTIVITY);
				break;
		}
	}

	// boolean isVisibleLastItem;
	//
	// OnTouchListener onTouchListener = new OnTouchListener() {
	// @Override
	// public boolean onTouch(View v, MotionEvent event) {
	// if (event.getAction() == MotionEvent.ACTION_DOWN) {
	// } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
	// if (applyButton != null) {
	// startAnimation(false);
	// }
	// } else if (event.getAction() == MotionEvent.ACTION_UP) {
	// isVisibleLastItem = false;
	// if (listView.getLastVisiblePosition() == listView.getCount() - 1) {
	// isVisibleLastItem = true;
	// }
	// startAnimation(true);
	// }
	// return false;
	// }
	// };
	//
	// OnScrollListener onScrollChangedListener = new OnScrollListener() {
	// @Override
	// public void onScroll(AbsListView view, int firstVisibleItem,
	// int visibleItemCount, int totalItemCount) {
	// }
	//
	// @Override
	// public void onScrollStateChanged(final AbsListView view, int scrollState)
	// {
	// if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
	// if (isVisibleLastItem) {
	// listView.postDelayed(new Runnable() {
	// @Override
	// public void run() {
	// listView.smoothScrollToPosition(listView.getCount() - 1);
	// }
	// }, 100);
	// }
	// } else if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
	//
	// } else if (scrollState == OnScrollListener.SCROLL_STATE_FLING) {
	//
	// }
	// }
	// };

	// public void startAnimation(boolean showView) {
	// if (showView) {
	// // 보이고 있는 데 또 보여라는 요청 들오면 해당 애니메이션 무시.
	// if (aniResId == R.anim.push_up_in) {
	// return;
	// }
	// aniResId = R.anim.push_up_in;
	// } else {
	// // push up out 애니메이션을 수행했는데 또 요청이 들어오면 무시한다.
	// if (this.isShown() == false || aniResId == R.anim.push_up_out) {
	// return;
	// }
	// aniResId = R.anim.push_up_out;
	// }
	//
	// Animation animation = AnimationUtils.loadAnimation(getContext(),
	// aniResId);
	// animation.setAnimationListener(animationListener);
	// animation.setDuration(300);
	//
	// applyButton.startAnimation(animation);
	// }
	//
	// public void beforeAnimation() {
	// applyButton.setVisibility(View.VISIBLE);
	// }
	//
	// public void afterAnimation() {
	// if (aniResId == R.anim.push_up_out) {
	// applyButton.setVisibility(View.GONE);
	// }
	// }
	//
	// AnimationListener animationListener = new AnimationListener() {
	// @Override
	// public void onAnimationStart(Animation animation) {
	// beforeAnimation();
	// }
	//
	// @Override
	// public void onAnimationEnd(Animation animation) {
	// afterAnimation();
	// }
	//
	// @Override
	// public void onAnimationRepeat(Animation animation) {
	//
	// }
	// };
}
