package org.our.android.ouracademy.ui.view;

import org.our.android.ouracademy.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

/**
 * 
 * @author jyeon
 *
 */
public class SetupWifiListItemVew extends LinearLayout {

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
	}
}
