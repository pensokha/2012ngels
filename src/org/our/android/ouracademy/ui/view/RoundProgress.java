package org.our.android.ouracademy.ui.view;

import org.our.android.ouracademy.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ClipDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * custom horizontal progressbar using nine-patch.
 * 
 * 출처 : http://blog.mediarain.com/2011/04/android-custom-progressbar-with-rounded-corners/
 * 
 * @author hurims
 */
public class RoundProgress extends RelativeLayout {

	/**
	 * 
	 * progress bar 의 이미지 색상
	 * 
	 * @author jyeon
	 *
	 */
	public enum BarColor {
		green,
		red,
		brown,
		gray;
	}

	private ImageView progressDrawableImageView;
	private ImageView trackDrawableImageView;
	private int max = 100;

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public RoundProgress(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.round_progress, this);
		setup(context, attrs);
	}

	protected void setup(Context context, AttributeSet attrs) {

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NCRoundProgress);

		final String xmlns = "http://schemas.android.com/apk/res/org.our.android.ouracademy";
		int bgResource = attrs.getAttributeResourceValue(xmlns, "progressDrawable", 0);
		progressDrawableImageView = (ImageView)findViewById(R.id.progress_drawable_image_view);
		progressDrawableImageView.setBackgroundResource(bgResource);

		int trackResource = attrs.getAttributeResourceValue(xmlns, "track", 0);
		trackDrawableImageView = (ImageView)findViewById(R.id.track_image_view);
		trackDrawableImageView.setBackgroundResource(trackResource);

		int progress = attrs.getAttributeIntValue(xmlns, "progress", 0);
		setProgress(progress);
		int max = attrs.getAttributeIntValue(xmlns, "max", 100);
		setMax(max);

		a.recycle();
	}

	public void setProgress(int value) {
		ClipDrawable drawable = (ClipDrawable)progressDrawableImageView.getBackground();
		double percent = (double)value / max;
		int level = (int)Math.floor(percent * 10000);

		drawable.setLevel(level);
	}
}
