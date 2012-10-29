package org.our.android.ouracademy.ui.view;

import org.our.android.ouracademy.R;
import org.our.android.ouracademy.ui.view.SetupCategoryView.SetupCategoryViewListener;
import org.our.android.ouracademy.ui.view.SetupWiFiListView.SetupWiFiListViewListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
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
		viewFlipper.addView(categoryView);

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

	private void previousView() {
		if (viewFlipper == null) {
			return;
		}

		viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.push_right_in));
		viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.push_right_out));
		viewFlipper.showPrevious();
	}

	SetupCategoryViewListener categoryListener = new SetupCategoryViewListener() {

		@Override
		public void onClickBtn(View view) {
			switch (view.getId()) {
				case R.id.closeBtn:
					listener.onClickCloseBtn();
					break;
				case R.id.teacherBtn:

					break;
				case R.id.studentBtn:

					break;
				case R.id.network:
					nextView();
					break;
				case R.id.delete:

					break;
				case R.id.dataSync:

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
