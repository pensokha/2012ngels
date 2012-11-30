package org.our.android.ouracademy.ui.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class NCTextView extends TextView {

	Context context;

	public NCTextView(Context context) {
		super(context);

		this.context = context;

		init();
	}

	public NCTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.context = context;

		init();
	}

	private void init() {
		this.setTypeface(Typeface.createFromAsset(context.getAssets(), "Khmer.ttf"));
	}
}
