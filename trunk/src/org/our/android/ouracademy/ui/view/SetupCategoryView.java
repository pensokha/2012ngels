package org.our.android.ouracademy.ui.view;

import org.our.android.ouracademy.OurPreferenceManager;
import org.our.android.ouracademy.R;
import org.our.android.ouracademy.ui.view.PasswordEditText.EditTextListener;
import org.our.android.ouracademy.ui.view.PasswordEditText.PointColor;
import org.our.android.ouracademy.ui.widget.NCTextView;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * 
 * @author jyeon
 * 
 */
public class SetupCategoryView extends LinearLayout implements
		View.OnClickListener {

	private final String teacherPassword = "fair";

	/**
	 * 
	 * @author Jyeon
	 */
	public interface SetupCategoryViewListener {
		void onClickBtn(View view);

		void onClickModeBtn(boolean teacher);
	};

	LinearLayout closeBtn;

	NCTextView textLeft, textRight;

	LinearLayout teacherBtn, studentBtn;
	LinearLayout networkBtn, datasyncBtn;
	LinearLayout connectedStudentBtn, deleteBtn;
	LinearLayout letfBtn, rightBtn;
	LinearLayout studentView, teacheView;
	LinearLayout cancelBtn;
	LinearLayout modeChangeView;
	
	AlertDialog modeChangeDialog;

	PasswordEditText first, second, third, fourth;

	RelativeLayout dataSyncView;
	LinearLayout passwordView;

	// boolean isPermission = false;
	RoundProgress progress;
	NCTextView progressPercent, syncText;
	ImageView syncComplete;

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
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(
				infService);
		li.inflate(R.layout.setup_category_view, this, true);

		closeBtn = (LinearLayout) findViewById(R.id.closeBtn);
		closeBtn.setOnClickListener(this);

		modeChangeView = (LinearLayout) li.inflate(
				R.layout.setup_mode_change_dialog, null);

		teacherBtn = (LinearLayout) findViewById(R.id.teacherBtn);
		teacherBtn.setOnClickListener(this);
		studentBtn = (LinearLayout) findViewById(R.id.studentBtn);
		studentBtn.setOnClickListener(this);

		studentView = (LinearLayout) findViewById(R.id.studentView);
		teacheView = (LinearLayout) findViewById(R.id.teacheView);

		if (OurPreferenceManager.getInstance().isTeacher()) {
			studentView.setVisibility(View.GONE);
			teacheView.setVisibility(View.VISIBLE);
			teacherBtn.setSelected(true);
		} else {
			studentView.setVisibility(View.VISIBLE);
			teacheView.setVisibility(View.GONE);
			studentBtn.setSelected(true);
		}

		networkBtn = (LinearLayout) findViewById(R.id.networkBtn);
		networkBtn.setOnClickListener(this);
		datasyncBtn = (LinearLayout) findViewById(R.id.datasyncBtn);
		datasyncBtn.setOnClickListener(this);
		connectedStudentBtn = (LinearLayout) findViewById(R.id.connectedStudentBtn);
		connectedStudentBtn.setOnClickListener(this);
		deleteBtn = (LinearLayout) findViewById(R.id.deleteBtn);
		deleteBtn.setOnClickListener(this);

		dataSyncView = (RelativeLayout)findViewById(R.id.data_sync_view);
		progress = (RoundProgress)dataSyncView.findViewById(R.id.progress);
		progressPercent = (NCTextView)dataSyncView.findViewById(R.id.percent);
		syncText = (NCTextView)dataSyncView.findViewById(R.id.sync_text);
		syncComplete = (ImageView)dataSyncView.findViewById(R.id.complete);

		cancelBtn = (LinearLayout)findViewById(R.id.cancel);
		cancelBtn.setOnClickListener(this);

		passwordView = (LinearLayout) findViewById(R.id.password_view);

		first = (PasswordEditText) findViewById(R.id.first);
		first.setDrawPointEnabled(true);
		first.setOnEditTextListener(editTextlistener);
		second = (PasswordEditText) findViewById(R.id.second);
		second.setDrawPointEnabled(true);
		second.setOnEditTextListener(editTextlistener);
		third = (PasswordEditText) findViewById(R.id.third);
		third.setDrawPointEnabled(true);
		third.setOnEditTextListener(editTextlistener);
		fourth = (PasswordEditText) findViewById(R.id.fourth);
		fourth.setDrawPointEnabled(true);
		fourth.setOnEditTextListener(editTextlistener);

		Builder builder = new AlertDialog.Builder(getContext());
		builder.setView(modeChangeView);
		builder.setTitle(R.string.mode_change);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				modeChangeToStudent();
			}
		});
		builder.setNegativeButton(R.string.cancel, null);
		modeChangeDialog = builder.create();
	}

	private void modeChangeToStudent() {
		studentView.setVisibility(View.VISIBLE);
		teacheView.setVisibility(View.GONE);
		passwordView.setVisibility(View.GONE);
		dataSyncView.setVisibility(View.GONE);
		studentBtn.setSelected(true);
		teacherBtn.setSelected(false);

		if (first.isKeyboardVisible()) {
			first.hideKeyboard();
		}
		listener.onClickModeBtn(false);
	}

	@Override
	public void onClick(View view) {
		if (listener == null) {
			return;
		}

		switch (view.getId()) {
		case R.id.studentBtn:
			if (OurPreferenceManager.getInstance().isTeacher()) {
				modeChangeDialog.show();
			}else{
				modeChangeToStudent();
			}
			break;

		case R.id.teacherBtn:
			studentBtn.setSelected(false);
			teacherBtn.setSelected(true);

			studentView.setVisibility(View.GONE);
			dataSyncView.setVisibility(View.GONE);
			if (OurPreferenceManager.getInstance().isTeacher()) {
				passwordView.setVisibility(View.GONE);
				teacheView.setVisibility(View.VISIBLE);
			} else {
				passwordView.setVisibility(View.VISIBLE);
				teacheView.setVisibility(View.GONE);
				initPasswordView();
			}
			break;
			
		case R.id.datasyncBtn:
		case R.id.networkBtn:
		case R.id.connectedStudentBtn:
		case R.id.deleteBtn:
			// park
			if (listener != null) {
				listener.onClickBtn(view);
			}
			break;
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

	public void setOnSetupCategoryViewListener(
			SetupCategoryViewListener callback) {
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
					listener.onClickModeBtn(true);
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

	public void viewDataSync() {
		setProgress(0);
		studentView.setVisibility(View.GONE);
		dataSyncView.setVisibility(View.VISIBLE);
	}

	public void setProgress(int percent) {
		progress.setProgress(percent);

		if (percent == 100) {
			progressPercent.setVisibility(View.GONE);
			syncComplete.setVisibility(View.VISIBLE);
			syncText.setText(R.string.close);
		} else {
			progressPercent.setText(percent + "%");
		}
	}

	private boolean checkPassword() {
		StringBuilder sb = new StringBuilder();
		sb.append(first.getText());
		sb.append(second.getText());
		sb.append(third.getText());
		sb.append(fourth.getText());

		return teacherPassword.compareToIgnoreCase(sb.toString()) == 0 ? true
				: false;

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
