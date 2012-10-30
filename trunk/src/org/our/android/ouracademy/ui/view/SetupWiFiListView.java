package org.our.android.ouracademy.ui.view;

import org.our.android.ouracademy.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
	TextView titleText;
	ImageView titleIcon;
	LinearLayout backBtn;

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
		titleText = (TextView)findViewById(R.id.titleText);
		titleText.setText("WI-FI");
		titleIcon = (ImageView)findViewById(R.id.titleIcon);
		titleIcon.setImageResource(R.drawable.setup_icon_title02);

		listView = (ListView)findViewById(R.id.list);
	}

	public ListView getListView() {
		return listView;
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
}