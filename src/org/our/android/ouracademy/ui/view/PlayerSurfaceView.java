package org.our.android.ouracademy.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;

/**
 * 
 * 
 * @author jyeon
 *
 */
public class PlayerSurfaceView extends SurfaceView {

	public interface OnPlayerSurfaceViewListener {
		void pinchZoomIn();
	};

	private int baseDist = -1;

	OnPlayerSurfaceViewListener listener;

	public PlayerSurfaceView(Context context) {
		super(context);
	}

	public PlayerSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setOnPlayerSurfaceViewListViewListener(OnPlayerSurfaceViewListener callback) {
		listener = callback;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getPointerCount() == 2) {
			int action = event.getAction();
			int pureaction = action & MotionEvent.ACTION_MASK;
			if (pureaction == MotionEvent.ACTION_POINTER_DOWN) {
				baseDist = getDistance(event);
			} else if (pureaction == MotionEvent.ACTION_POINTER_UP) {
				if (baseDist - getDistance(event) > 100 && baseDist != -1) {
					if (listener != null) {
						listener.pinchZoomIn();
					}
					return true;
				}
			}
		} else {
			baseDist = -1;
		}

		return super.onTouchEvent(event);
	}

	int getDistance(MotionEvent event) {
		int dx = (int)(event.getX(0) - event.getX(1));
		int dy = (int)(event.getY(0) - event.getY(1));
		return (int)(Math.sqrt(dx * dx + dy * dy));
	}
}
