package org.our.android.ouracademy.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

/**
 * 
 * 
 * @author jyeon
 *
 */
public class VerticalTextView extends NCTextView {

	public VerticalTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public VerticalTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(heightMeasureSpec, widthMeasureSpec);
		setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
	}

	protected void onDraw(Canvas c) {
		c.rotate(-90);
		c.translate(-getHeight(), 0);

		super.onDraw(c);
	}
}
