package org.our.android.ouracademy.ui.widget;

import org.our.android.ouracademy.constants.CommonConstants;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class NCBoldTextView extends TextView {
	private static Typeface typeface = null; 
	Context context;
	
	public NCBoldTextView(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public NCBoldTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}
	
	public NCBoldTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		
		init();
	}

	private void init() {
		if (typeface == null) {
			typeface = Typeface.createFromAsset(context.getAssets(), CommonConstants.KHMER_FONT_BOLD_FILE);
		}
		this.setTypeface(typeface);
		
	}
}
