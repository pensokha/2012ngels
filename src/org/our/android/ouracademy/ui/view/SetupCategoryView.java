package org.our.android.ouracademy.ui.view;

import org.our.android.ouracademy.R;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @author jyeon
 *
 */
public class SetupCategoryView extends LinearLayout implements View.OnClickListener {

	/**
	 * 
	 * @author Jyeon
	 */
	public interface SetupCategoryViewListener {
		void onClickBtn(View view);
	};

	ImageView closeBtn;

	TextView netText;

	LinearLayout teacherBtn, studentBtn;
	LinearLayout network, delete, dataSync;

	SetupCategoryViewListener listener;

	public SetupCategoryView(Context context) {
		super(context);

		init();
	}

	public SetupCategoryView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	private void init() {
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater)getContext().getSystemService(infService);
		li.inflate(R.layout.setup_category_view, this, true);

		closeBtn = (ImageView)findViewById(R.id.closeBtn);
		closeBtn.setOnClickListener(this);
		teacherBtn = (LinearLayout)findViewById(R.id.teacherBtn);
		teacherBtn.setOnClickListener(this);
		studentBtn = (LinearLayout)findViewById(R.id.studentBtn);
		studentBtn.setOnClickListener(this);
		network = (LinearLayout)findViewById(R.id.network);
		network.setOnClickListener(this);
		delete = (LinearLayout)findViewById(R.id.delete);
		delete.setOnClickListener(this);
		dataSync = (LinearLayout)findViewById(R.id.dataSync);
		dataSync.setOnClickListener(this);

		netText = (TextView)findViewById(R.id.netText);
	}

	@Override
	public void onClick(View view) {
		if (listener == null) {
			return;
		}

		listener.onClickBtn(view);
	}

	public void setOnSetupCategoryViewListener(SetupCategoryViewListener callback) {
		listener = callback;
	}

	public void setNetworkIdText(String id) {
		if (!TextUtils.isEmpty(id)) {
			StringBuilder sb = new StringBuilder();
			sb.append("WI-FI Direct ID (");
			sb.append(id);
			sb.append(")");
			netText.setText(sb.toString());
		}
	}
}
