package org.our.android.ouracademy.ui.view;

import org.our.android.ouracademy.R;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @author jyeon
 *
 */
public class SetupWifiListItemVew extends LinearLayout {

	ImageView userIcon, wifiIcon;
	TextView directId;

	String id;

	public SetupWifiListItemVew(Context context) {
		super(context);

		init();
	}

	public SetupWifiListItemVew(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	private void init() {
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater)getContext().getSystemService(infService);
		li.inflate(R.layout.setup_list_item_wifi, this, true);

		userIcon = (ImageView)findViewById(R.id.userIcon);
		wifiIcon = (ImageView)findViewById(R.id.wifiIcon);
		directId = (TextView)findViewById(R.id.directId);
	}

	public void setData(boolean teacherMode, String id, int intensity) {
		setUserIconImg(teacherMode);
		setDirectIdText(id);
		setWifiIconImg(intensity);
	}

	private void setUserIconImg(boolean teacher) {
		if (teacher) {
			userIcon.setImageResource(R.drawable.setup_icon_connectmem01);
		} else {
			userIcon.setImageResource(R.drawable.setup_icon_connectmem01);
		}
	}

	private void setWifiIconImg(int intensity) {
		int resId = R.drawable.setup_icon_wifi02;

		switch (intensity) {
			case 1:
				resId = R.drawable.setup_icon_wifi03;
				break;
			case 2:
				resId = R.drawable.setup_icon_wifi04;
				break;
			case 3:
				resId = R.drawable.setup_icon_wifi05;
				break;
			default:
				resId = R.drawable.setup_icon_wifi02;
				break;
		}

		wifiIcon.setImageResource(resId);
	}

	private void setDirectIdText(String id) {
		if (!TextUtils.isEmpty(id)) {
			StringBuilder sb = new StringBuilder();
			sb.append("WI-FI Direct ID (");
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
