package org.our.android.ouracademy.ui.view;

import org.our.android.ouracademy.R;
import org.our.android.ouracademy.ui.view.PasswordEditText.EditTextListener;
import org.our.android.ouracademy.ui.view.PasswordEditText.PointColor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @author jyeon
 *
 */
public class SetupCategoryView extends LinearLayout implements View.OnClickListener {

	private final String teacherPassword = "fair";

	/**
	 * 
	 * @author Jyeon
	 */
	public interface SetupCategoryViewListener {
		void onClickBtn(View view);
	};

	LinearLayout closeBtn;

	TextView textLeft, textRight;

	LinearLayout teacherBtn, studentBtn;
	LinearLayout networkBtn, datasyncBtn;
	LinearLayout connectedStudentBtn, deleteBtn;
	LinearLayout letfBtn, rightBtn;
	LinearLayout studentView, teacheView;
	LinearLayout cancelBtn;

	PasswordEditText first, second, third, fourth;

	RelativeLayout dataSyncView;
	LinearLayout passwordView;

//	boolean isPermission = false;

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

		closeBtn = (LinearLayout)findViewById(R.id.closeBtn);
		closeBtn.setOnClickListener(this);
		teacherBtn = (LinearLayout)findViewById(R.id.teacherBtn);
		teacherBtn.setOnClickListener(this);
		studentBtn = (LinearLayout)findViewById(R.id.studentBtn);
		studentBtn.setOnClickListener(this);
		studentBtn.setSelected(true);

		studentView = (LinearLayout)findViewById(R.id.studentView);
		studentView.setVisibility(View.VISIBLE);
		teacheView = (LinearLayout)findViewById(R.id.teacheView);
		teacheView.setVisibility(View.GONE);

		networkBtn = (LinearLayout)findViewById(R.id.networkBtn);
		networkBtn.setOnClickListener(this);
		datasyncBtn = (LinearLayout)findViewById(R.id.datasyncBtn);
		datasyncBtn.setOnClickListener(this);
		connectedStudentBtn = (LinearLayout)findViewById(R.id.connectedStudentBtn);
		connectedStudentBtn.setOnClickListener(this);
		deleteBtn = (LinearLayout)findViewById(R.id.deleteBtn);
		deleteBtn.setOnClickListener(this);

		dataSyncView = (RelativeLayout)findViewById(R.id.data_sync_view);
		cancelBtn = (LinearLayout)findViewById(R.id.cancel);
		cancelBtn.setOnClickListener(this);

		passwordView = (LinearLayout)findViewById(R.id.password_view);

		first = (PasswordEditText)findViewById(R.id.first);
		first.setDrawPointEnabled(true);
		first.setOnEditTextListener(editTextlistener);
		second = (PasswordEditText)findViewById(R.id.second);
		second.setDrawPointEnabled(true);
		second.setOnEditTextListener(editTextlistener);
		third = (PasswordEditText)findViewById(R.id.third);
		third.setDrawPointEnabled(true);
		third.setOnEditTextListener(editTextlistener);
		fourth = (PasswordEditText)findViewById(R.id.fourth);
		fourth.setDrawPointEnabled(true);
		fourth.setOnEditTextListener(editTextlistener);

	}

	@Override
	public void onClick(View view) {
		if (listener == null) {
			return;
		}

		switch (view.getId()) {
			case R.id.studentBtn:
				studentView.setVisibility(View.VISIBLE);
				teacheView.setVisibility(View.GONE);
				passwordView.setVisibility(View.GONE);
				dataSyncView.setVisibility(View.GONE);
				studentBtn.setSelected(true);
				teacherBtn.setSelected(false);

				if (first.isKeyboardVisible()) {
					first.hideKeyboard();
				}
				break;

			case R.id.teacherBtn:
				studentBtn.setSelected(false);
				teacherBtn.setSelected(true);

				passwordView.setVisibility(View.VISIBLE);
				studentView.setVisibility(View.GONE);
				teacheView.setVisibility(View.GONE);
				dataSyncView.setVisibility(View.GONE);

				initPasswordView();
				break;

			case R.id.datasyncBtn:
				studentView.setVisibility(View.GONE);
				dataSyncView.setVisibility(View.VISIBLE);
			case R.id.networkBtn:
			case R.id.connectedStudentBtn:
			case R.id.deleteBtn:
			case R.id.closeBtn:
				if (listener != null) {
					listener.onClickBtn(view);
				}
				break;
			case R.id.cancel:
				studentView.setVisibility(View.VISIBLE);
				dataSyncView.setVisibility(View.GONE);
				if (listener != null) {
					listener.onClickBtn(view);
				}
				break;
		}
	}

	public void setOnSetupCategoryViewListener(SetupCategoryViewListener callback) {
		listener = callback;
	}

	EditTextListener editTextlistener = new EditTextListener() {

		@Override
		public void willKeyboardVisible() {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(PasswordEditText editText, String text) {
			if (editText == first) {
				if (text != null && text.length() == 1) {
					second.requestFocus();
				}
			} else if (editText == second) {
				if (text != null && text.length() == 1) {
					third.requestFocus();
				}

			} else if (editText == third) {
				if (text != null && text.length() == 1) {
					fourth.requestFocus();
				}

			} else if (editText == fourth) {
				if (text != null && text.length() == 1) {
					autoChecking();
				}
			}
		}
	};

	private void autoChecking() {
		first.setEnabled(false);
		second.setEnabled(false);
		third.setEnabled(false);
		fourth.setEnabled(false);

		if (checkPassword()) {
			first.setPointColor(PointColor.green);
			second.setPointColor(PointColor.green);
			third.setPointColor(PointColor.green);
			fourth.setPointColor(PointColor.green);

			postDelayed(new Runnable() {

				@Override
				public void run() {
					passwordView.setVisibility(View.GONE);
					studentView.setVisibility(View.GONE);
					teacheView.setVisibility(View.VISIBLE);
				}
			}, 1000);

		} else {

			first.setPointColor(PointColor.red);
			second.setPointColor(PointColor.red);
			third.setPointColor(PointColor.red);
			fourth.setPointColor(PointColor.red);

			postDelayed(new Runnable() {

				@Override
				public void run() {
					initPasswordView();
				}
			}, 1000);
		}
	}

	private boolean checkPassword() {
		StringBuilder sb = new StringBuilder();
		sb.append(first.getText());
		sb.append(second.getText());
		sb.append(third.getText());
		sb.append(fourth.getText());

		return teacherPassword.compareToIgnoreCase(sb.toString()) == 0 ? true : false;

	}

	private void initPasswordView() {
		first.setPointColor(PointColor.brown);
		second.setPointColor(PointColor.brown);
		third.setPointColor(PointColor.brown);
		fourth.setPointColor(PointColor.brown);

		first.setEnabled(true);
		second.setEnabled(true);
		third.setEnabled(true);
		fourth.setEnabled(true);

		first.setText("");
		second.setText("");
		third.setText("");
		fourth.setText("");

		first.requestFocus();
	}
}