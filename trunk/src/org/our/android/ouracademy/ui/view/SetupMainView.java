package org.our.android.ouracademy.ui.view;

import org.our.android.ouracademy.OurPreferenceManager;
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
		
		void onClickFindConnectedStudent();
		
		void onClickCacelSync();

		void onClickFindTeacher(boolean isViewChange);
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
				case R.id.networkBtn:
					if(listener != null){
						listener.onClickFindTeacher(true);
					}
					break;
				case R.id.connectedStudentBtn:
					if(listener != null){
						listener.onClickFindConnectedStudent();
					}
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
				case R.id.cancel:
					if (listener != null) {
						listener.onClickCacelSync();
					}
					break;
			}
		}

		@Override
		public void onClickModeBtn(boolean teacher) {
			if (listener != null) {
				listener.onClickModeBtn(teacher);
			}
		}
	};
	
	public void viewTeacherOnNetwork(){
		wifiListView.setTitleText(getContext().getResources().getString(R.string.wi_fi));
		wifiListView.setTitleImgResource(R.drawable.setup_icon_title02);
		nextView();
	}
	
	public void viewConnectedStudent(){
		wifiListView.setTitleText(getContext().getResources().getString(R.string.connected_student));
		wifiListView.setTitleImgResource(R.drawable.setup_icon_tab01_nor);
		nextView();
	}
	
	public void viewDataSync(){
		categoryView.viewDataSync();
	}

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
				case R.id.titleIcon:
					if(OurPreferenceManager.getInstance().isStudent()){
						listener.onClickFindTeacher(false);
					}
					break;
			}
		}
	};
	
	public void viewList(){
		wifiListView.viewList();
	}
	
	public void viewNoList(){
		wifiListView.viewNoList();
	}

	public void setOnSetupMainViewListener(SetupMainViewListener callback) {
		listener = callback;
	}
	
	public void setProgress(int percent){
		categoryView.setProgress(percent);
	}
}
