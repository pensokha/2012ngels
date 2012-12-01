package org.our.android.ouracademy.ui.view;

import org.our.android.ouracademy.R;
import org.our.android.ouracademy.ui.widget.NCTextView;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * 
 * @author jyeon
 *
 */
public class SetupWiFiListView extends LinearLayout implements View.OnClickListener {

	/**
	 * 
	 * @author Jyeon
	 */
	public interface SetupWiFiListViewListener {
		void onClickBtn(View view);
	};

	ImageView closeBtn;
	NCTextView titleText;
	ImageView titleIcon;
	LinearLayout backBtn, noList;

	ListView listView;

	SetupWiFiListViewListener listener;

	public SetupWiFiListView(Context context) {
		super(context);

		init();
	}

	public SetupWiFiListView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	private void init() {
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater)getContext().getSystemService(infService);
		li.inflate(R.layout.setup_wifi_list_view, this, true);

		backBtn = (LinearLayout)findViewById(R.id.backBtn);
		backBtn.setVisibility(View.VISIBLE);
		backBtn.setOnClickListener(this);
		closeBtn = (ImageView)findViewById(R.id.closeBtn);
		closeBtn.setOnClickListener(this);
		titleText = (NCTextView)findViewById(R.id.titleText);
		titleText.setText(getContext().getResources().getString(R.string.wi_fi));
		titleIcon = (ImageView)findViewById(R.id.titleIcon);
		titleIcon.setImageResource(R.drawable.setup_icon_title02);
		titleIcon.setOnClickListener(this);

		listView = (ListView)findViewById(R.id.list);
		listView.setVisibility(View.GONE);
		noList = (LinearLayout)findViewById(R.id.no_list);
		noList.setVisibility(View.VISIBLE);
	}

	public ListView getListView() {
		return listView;
	}
	
	public void viewNoList(){
		listView.setVisibility(View.GONE);
		noList.setVisibility(View.VISIBLE);
	}
	
	public void viewList(){
		listView.setVisibility(View.VISIBLE);
		noList.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View view) {
		if (listener == null) {
			return;
		}

		listener.onClickBtn(view);
	}

	public void setOnSetupWiFiListViewListener(SetupWiFiListViewListener callback) {
		listener = callback;
	}

	public void setTitleText(String title) {
		if (titleText != null && !TextUtils.isEmpty(title)) {
			titleText.setText(title);
		}
	}

	public void setTitleImgResource(int id) {
		titleIcon.setImageResource(id);
	}
}
