package org.our.android.ouracademy.ui.view;

import org.our.android.ouracademy.R;
import org.our.android.ouracademy.model.OurWifiDirectDevice;
import org.our.android.ouracademy.ui.widget.NCTextView;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

/**
 * 
 * @author jyeon
 *
 */
public class SetupWifiListItemView extends LinearLayout {

	ImageView userIcon, wifiIcon;
	NCTextView directId;
	ProgressBar connectingIcon;

	String id;

	public SetupWifiListItemView(Context context) {
		super(context);

		init();
	}

	public SetupWifiListItemView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	private void init() {
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater)getContext().getSystemService(infService);
		li.inflate(R.layout.setup_list_item_wifi, this, true);

		userIcon = (ImageView)findViewById(R.id.userIcon);
		wifiIcon = (ImageView)findViewById(R.id.wifiIcon);
		directId = (NCTextView)findViewById(R.id.directId);
		connectingIcon = (ProgressBar)findViewById(R.id.wifiConnecting);
	}

	public void setData(boolean teacherMode, String id, int connectedState) {
		setUserIconImg(teacherMode);
		setDirectIdText(id);
		setWifiIconImg(connectedState);
	}

	private void setUserIconImg(boolean teacher) {
		if (teacher) {
			userIcon.setImageResource(R.drawable.setup_icon_connectmem01);
		} else {
			userIcon.setImageResource(R.drawable.setup_icon_connectmem01);
		}
	}

	private void setWifiIconImg(int connectedState) {

		if (connectedState == OurWifiDirectDevice.STATE_CONNECTING) {
			connectingIcon.setVisibility(View.VISIBLE);
			wifiIcon.setVisibility(View.GONE);
		} else {
			int resId = R.drawable.setup_icon_wifi05;
			if (connectedState == OurWifiDirectDevice.STATE_CONNECTED) {
				resId = R.drawable.setup_icon_wifi01;
			}
			connectingIcon.setVisibility(View.GONE);
			wifiIcon.setVisibility(View.VISIBLE);
			wifiIcon.setImageResource(resId);
		}
	}

	private void setDirectIdText(String id) {
		if (!TextUtils.isEmpty(id)) {
			StringBuilder sb = new StringBuilder();
			sb.append(getContext().getResources().getString(R.string.wi_fi_direct_id));
			sb.append("(");
			sb.append(id);
			sb.append(")");
			directId.setText(sb.toString());

			this.id = id;
		}
	}

	public String getDirectId() {
		return id;
	}
}
