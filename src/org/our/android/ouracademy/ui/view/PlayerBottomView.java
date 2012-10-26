package org.our.android.ouracademy.ui.view;

import org.our.android.ouracademy.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

/**
 * 
 * 
 * @author jyeon
 *
 */
public class PlayerBottomView extends LinearLayout {

	private final static int INVALID_ANIMATION_RESOURCE_ID = -1;

	int aniResId = INVALID_ANIMATION_RESOURCE_ID;

	public PlayerBottomView(Context context) {
		super(context);

		init();
	}

	public PlayerBottomView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	private void init() {
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater)getContext().getSystemService(infService);
		li.inflate(R.layout.media_player_bottom, this, true);
	}

	public void startAnimation(boolean showView) {

		if (showView) {
			// 보이고 있는 데 또 보여라는 요청 들오면 해당 애니메이션 무시.
			if (aniResId == R.anim.push_up_in) {
				return;
			}
			aniResId = R.anim.push_up_in;
		} else {
			// push up out 애니메이션을 수행했는데 또 요청이 들어오면 무시한다.
			if (this.isShown() == false || aniResId == R.anim.push_up_out) {
				return;
			}
			aniResId = R.anim.push_up_out;
		}

		Animation animation = AnimationUtils.loadAnimation(getContext(), aniResId);
		animation.setAnimationListener(animationListener);
		animation.setDuration(300);

		startAnimation(animation);
	}

	public void beforeAnimation() {
		setVisibility(View.VISIBLE);
	}

	public void afterAnimation() {
		if (aniResId == R.anim.push_up_out) {
			setVisibility(View.GONE);
		}
	}

	AnimationListener animationListener = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			beforeAnimation();
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			afterAnimation();
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

	};
}
