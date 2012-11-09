package org.our.android.ouracademy.ui.view;

import org.our.android.ouracademy.R;
import org.our.android.ouracademy.ui.view.SetupCategoryView.SetupCategoryViewListener;
import org.our.android.ouracademy.ui.view.SetupWiFiListView.SetupWiFiListViewListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ViewFlipper;

/**
 * 
 * @author jyeon
 *
 */
public class SetupMainView extends LinearLayout {

	/**
	 * 
	 * @author Jyeon
	 */
	public interface SetupMainViewListener {
		void onClickCloseBtn();

		void onClickModeBtn(boolean teacher);

		void onClickDeleteCell();

		void onClickDataSyncCell();
	};

	ViewFlipper viewFlipper;
	SetupCategoryView categoryView;
	SetupWiFiListView wifiListView;

	SetupMainViewListener listener;

	public SetupMainView(Context context) {
		super(context);

		init();
	}

	public SetupMainView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	private void init() {
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater)getContext().getSystemService(infService);
		li.inflate(R.layout.activity_setting, this, true);

		viewFlipper = (ViewFlipper)findViewById(R.id.viewFlipper);

		if (categoryView == null) {
			categoryView = new SetupCategoryView(getContext());
			categoryView.setOnSetupCategoryViewListener(categoryListener);
		}

		LinearLayout view = new LinearLayout(getContext());
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		view.setLayoutParams(params);
		view.setGravity(Gravity.CENTER);
		view.addView(categoryView);

		viewFlipper.addView(view);

		if (wifiListView == null) {
			wifiListView = new SetupWiFiListView(getContext());
			wifiListView.setOnSetupWiFiListViewListener(wifiListViewListener);
		}
		viewFlipper.addView(wifiListView);
	}

	public ListView getListView() {
		if (wifiListView != null) {
			return wifiListView.getListView();
		}
		return null;
	}

	private void nextView() {
		if (viewFlipper == null) {
			return;
		}

		viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.push_left_in));
		viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.push_left_out));
		viewFlipper.showNext();
	}

	public void previousView() {
		if (viewFlipper == null) {
			return;
		}

		viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.push_right_in));
		viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.push_right_out));
		viewFlipper.showPrevious();
	}

	public boolean onBackPressed() {
		View currentView = viewFlipper.getCurrentView();
		if (currentView != null && currentView == wifiListView) {
			previousView();
			return true;
		}
		return false;
	}

	SetupCategoryViewListener categoryListener = new SetupCategoryViewListener() {

		@Override
		public void onClickBtn(View view) {
			switch (view.getId()) {
				case R.id.closeBtn:
					if (listener != null) {
						listener.onClickCloseBtn();
					}
					break;
				case R.id.teacherBtn:
					if (listener != null) {
						listener.onClickModeBtn(true);
					}
					break;
				case R.id.studentBtn:
					if (listener != null) {
						listener.onClickModeBtn(false);
					}
					break;
				case R.id.networkBtn:
					wifiListView.setTitleText("WI-FI");
					wifiListView.setTitleImgResource(R.drawable.setup_icon_title02);
					nextView();
					break;
				case R.id.connectedStudentBtn:
					wifiListView.setTitleText("Connected Student");
					wifiListView.setTitleImgResource(R.drawable.setup_icon_tab01_nor);
					nextView();
					break;
				case R.id.deleteBtn:
					if (listener != null) {
						listener.onClickDeleteCell();
					}
					break;
				case R.id.datasyncBtn:
					if (listener != null) {
						listener.onClickDataSyncCell();
					}
					break;
			}
		}
	};

	SetupWiFiListViewListener wifiListViewListener = new SetupWiFiListViewListener() {

		@Override
		public void onClickBtn(View view) {
			switch (view.getId()) {
				case R.id.backBtn:
					previousView();
					break;
				case R.id.closeBtn:
					listener.onClickCloseBtn();
					break;
			}
		}
	};

	public void setOnSetupMainViewListener(SetupMainViewListener callback) {
		listener = callback;
	}
}
