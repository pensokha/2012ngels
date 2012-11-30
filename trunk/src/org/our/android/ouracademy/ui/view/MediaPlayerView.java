package org.our.android.ouracademy.ui.view;

import org.our.android.ouracademy.R;
import org.our.android.ouracademy.ui.view.PlayerSurfaceView.OnPlayerSurfaceViewListener;
import org.our.android.ouracademy.ui.widget.NCHorizontalListView;
import org.our.android.ouracademy.ui.widget.NCTextView;
import org.our.android.ouracademy.ui.widget.VerticalSeekBar;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

/**
 * Media Player View
 * 
 * @author jyeon
 *
 */
public class MediaPlayerView extends LinearLayout implements View.OnClickListener {

	/**
	 * 
	 * @author Jyeon
	 */
	public interface MediaPlayerViewListener {
		void onClickBtn(View view);
	};

	private PlayerSurfaceView preview;

	SeekBar progress;
	VerticalSeekBar soundVolume;

	ImageView playBtn, soundIcon;
	LinearLayout closeBtn, soundBtn, moreBtn, soundControll;
	RelativeLayout playerCenter;

	NCTextView runTime, remainingTime;

	MediaPlayerViewListener listener;

	PlayerTopView topView;
	PlayerBottomView bottomView;
	NCHorizontalListView listView;

	private Handler handler;
	private Runnable fullScreen;

	public MediaPlayerView(Context context) {
		super(context);

		init();
	}

	public MediaPlayerView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	private void init() {
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater)getContext().getSystemService(infService);
		li.inflate(R.layout.media_player, this, true);

		preview = (PlayerSurfaceView)findViewById(R.id.surface);
		preview.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		preview.setOnClickListener(this);
		preview.setOnPlayerSurfaceViewListViewListener(new OnPlayerSurfaceViewListener() {

			@Override
			public void pinchZoomIn() {
				if (listener != null) {
					listener.onClickBtn(closeBtn);
				}
			}
		});

		closeBtn = (LinearLayout)findViewById(R.id.closeBtn);
		closeBtn.setOnClickListener(this);
		playBtn = (ImageView)findViewById(R.id.play);
		playBtn.setOnClickListener(this);
		soundBtn = (LinearLayout)findViewById(R.id.soundBtn);
		soundBtn.setOnClickListener(this);
		moreBtn = (LinearLayout)findViewById(R.id.moreBtn);
		moreBtn.setOnClickListener(this);

		runTime = (NCTextView)findViewById(R.id.runTime);
		remainingTime = (NCTextView)findViewById(R.id.remainingTime);

		listView = (NCHorizontalListView)findViewById(R.id.list);
		soundControll = (LinearLayout)findViewById(R.id.soundControll);

		progress = (SeekBar)findViewById(R.id.progress);

		soundVolume = (VerticalSeekBar)findViewById(R.id.soundVolume);
		soundIcon = (ImageView)findViewById(R.id.soundIcon);

		topView = (PlayerTopView)findViewById(R.id.titleBar);
		bottomView = (PlayerBottomView)findViewById(R.id.bottomBar);
		playerCenter = (RelativeLayout)findViewById(R.id.playerCenter);
	}

	public SurfaceView getSurfaceView() {
		return preview;
	}

	public SeekBar getSoundSeekBar() {
		return soundVolume;
	}

	public SeekBar getVideoSeekBar() {
		return progress;
	}

	public void postShowFrame(final int show) {
		if (handler == null) {
			handler = new Handler();
		}

		if (fullScreen == null) {
			fullScreen = new Runnable() {
				@Override
				public void run() {
					showFrameView(false);
				}
			};
		}
		handler.postDelayed(fullScreen, 3000);
	}

	public String getTimeText(int milliseconds) {
		int second = milliseconds / 1000;
		int minutes = second / 60;

		StringBuilder sb = new StringBuilder();
		if (minutes < 10) {
			sb.append("0");
		}
		sb.append(minutes);
		sb.append(":");
		int snd = second % 60;
		if (snd < 10) {
			sb.append("0");
		}
		sb.append(snd);

//		Log.e("", "time = " + sb.toString());

		return sb.toString();
	}

	@Override
	public void onClick(View view) {

		removeFullScreenCallback();

		if (view.getId() != R.id.soundBtn) {
			showSoundControll(false);
		}

		switch (view.getId()) {
			case R.id.surface:
				showFrameView(bottomView.getVisibility() == View.GONE);
				break;
			case R.id.closeBtn:
			case R.id.play:
				if (listener != null) {
					listener.onClickBtn(view);
				}
				break;
			case R.id.soundBtn:
				showSoundControll(!soundBtn.isSelected());
				break;

			case R.id.moreBtn:
				showVideoList(!moreBtn.isSelected());
				break;
		}
	}

	private void showFrameView(boolean show) {
		if (show) {
			topView.startAnimation(true);
			bottomView.startAnimation(true);

			postDelayed(new Runnable() {

				@Override
				public void run() {
					playerCenter.setVisibility(View.VISIBLE);
				}
			}, 300);

			showVideoList(false);
		} else {
			topView.startAnimation(false);
			bottomView.startAnimation(false);
			playerCenter.setVisibility(View.GONE);
		}
	}

	public void showVideoList(boolean show) {
		if (show) {
			listView.setVisibility(View.VISIBLE);
		} else {
			listView.setVisibility(View.GONE);
		}
		moreBtn.setSelected(show);
	}

	private void showSoundControll(boolean show) {
		if (show) {
			soundControll.setVisibility(View.VISIBLE);
			soundBtn.setSelected(true);
		} else {
			soundControll.setVisibility(View.GONE);
			soundBtn.setSelected(false);
		}
	}

	public void setSoundIconResource(int size) {
		if (size == 0) {
			soundIcon.setImageResource(R.drawable.player_icon_sound01);
		} else if (size == soundVolume.getMax()) {
			soundIcon.setImageResource(R.drawable.player_icon_sound02);
		} else {
			soundIcon.setImageResource(R.drawable.player_icon_sound03);
		}
	}

	public void setPlayerResource(boolean play) {
		if (play) {
			playBtn.setBackgroundResource(R.drawable.btn_state_play_normal);
		} else {
			playBtn.setBackgroundResource(R.drawable.btn_state_play_stop);
		}
	}

	public void setTitleText(String text) {
		if (TextUtils.isEmpty(text) || topView == null) {
			return;
		}
		topView.setTitleText(text);
	}

	public void setRunTimeText(int milliseconds) {
		runTime.setText(getTimeText(milliseconds));
	}

	public void setRemainingTimeText(int milliseconds) {
		remainingTime.setText(getTimeText(milliseconds));
	}

	public void setOnMediaPlayerViewListener(MediaPlayerViewListener callback) {
		listener = callback;
	}

	public NCHorizontalListView getListView() {
		return listView;
	}

	public void removeFullScreenCallback() {
		if (fullScreen != null) {
			handler.removeCallbacks(fullScreen);
			fullScreen = null;
		}
	}
}
