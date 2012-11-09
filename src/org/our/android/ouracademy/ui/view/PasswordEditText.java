package org.our.android.ouracademy.ui.view;

import org.our.android.ouracademy.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class PasswordEditText extends EditText implements TextWatcher {

	enum PointColor {
		brown,
		red,
		green
	}

	public interface EditTextListener {
		void willKeyboardVisible();

		void afterTextChanged(PasswordEditText editText, String text);
	}

	int pointWidth;
	int pointHeight;

	Drawable imgPoint;

	RectF rect;

	boolean drawPoint;
	int pointCount = 1;

	PointColor color = PointColor.brown;

	private InputMethodManager imm;

	private EditTextListener listener;

	public PasswordEditText(Context context) {
		super(context);
		init();
	}

	public PasswordEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		this.addTextChangedListener(this);
		this.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					imm.showSoftInput(PasswordEditText.this, 0);
				}
			}
		});

		pointWidth = getPixel(21.78f, getContext());
		pointHeight = getPixel(21.78f, getContext());

		int offsetX = (getPixel(69.86f, getContext()) / 2) - (pointWidth / 2);
		int offsetY = (getPixel(70.61f, getContext()) / 2) - (pointHeight / 2);

		rect = new RectF(offsetX, offsetY, offsetX + pointWidth, offsetY + pointHeight);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		String text = getText().toString();
		if (drawPoint && text != null && text.length() == 1) {
			imgPoint = getContext().getResources().getDrawable(getPointDrawable());
			imgPoint.setBounds((int)rect.left, (int)rect.top, ((int)rect.left + (int)(rect.right - rect.left)),
				((int)rect.top + (int)(rect.bottom - rect.top)));
			imgPoint.draw(canvas);
		}
	}

	public boolean isDrawPoint() {
		return drawPoint;
	}

	public void setDrawPointEnabled(boolean draw) {
		this.drawPoint = draw;
	}

	public void setOnEditTextListener(EditTextListener listener) {
		this.listener = listener;
	}

	private static int getPixel(float dp, Context context) {
		float mScale = context.getResources().getDisplayMetrics().density;
		return (int)(dp * mScale + 0.5f);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence sequence, int start, int before, int count) {
	}

	@Override
	public void afterTextChanged(Editable string) {
		String str = string.toString();
		if (listener != null) {
			listener.afterTextChanged(this, str);
		}
	}

	public void setPointColor(PointColor color) {
		this.color = color;
	}

	private int getPointDrawable() {
		int id = R.drawable.setup_password02;
		switch (color) {
			case brown:
				id = R.drawable.setup_password02;
				break;
			case red:
				id = R.drawable.setup_password03;
				break;
			case green:
				id = R.drawable.setup_password04;
				break;
		}
		return id;
	}

	public boolean isKeyboardVisible() {
		boolean ret = false;
		if (imm != null) {
			ret = imm.isActive();
		}
		return ret;
	}

	public void hideKeyboard() {
		imm.hideSoftInputFromWindow(this.getWindowToken(), 0);
	}
}
