package org.our.android.ouracademy.ui.view;

import org.our.android.ouracademy.R;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

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
	public interface OnMediaPlayerViewCb {
		void onClickBtn(View view);
	};

	private SurfaceView preview;

	SeekBar progress;
	VerticalSeekBar soundVolume;

	ImageView playBtn, soundIcon;
	LinearLayout closeBtn, soundBtn, moreBtn, list, soundControll;
	LinearLayout frame;

	TextView title, runTime, remainingTime;

	OnMediaPlayerViewCb mediaPlayerViewCb;

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

		preview = (SurfaceView)findViewById(R.id.surface);
		preview.setOnClickListener(this);

		frame = (LinearLayout)findViewById(R.id.frame);

		closeBtn = (LinearLayout)findViewById(R.id.closeBtn);
		closeBtn.setOnClickListener(this);
		playBtn = (ImageView)findViewById(R.id.play);
		playBtn.setOnClickListener(this);
		soundBtn = (LinearLayout)findViewById(R.id.soundBtn);
		soundBtn.setOnClickListener(this);
		moreBtn = (LinearLayout)findViewById(R.id.moreBtn);
		moreBtn.setOnClickListener(this);

		title = (TextView)findViewById(R.id.title);
		runTime = (TextView)findViewById(R.id.runTime);
		remainingTime = (TextView)findViewById(R.id.remainingTime);

		list = (LinearLayout)findViewById(R.id.list);
		soundControll = (LinearLayout)findViewById(R.id.soundControll);

		progress = (SeekBar)findViewById(R.id.progress);

		soundVolume = (VerticalSeekBar)findViewById(R.id.soundVolume);
		soundIcon = (ImageView)findViewById(R.id.soundIcon);
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

		postDelayed(new Runnable() {

			@Override
			public void run() {
				frame.setVisibility(show);

			}
		}, 3000);
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

		if (view.getId() != R.id.soundBtn) {
			showSoundControll(false);
		}

		switch (view.getId()) {
			case R.id.surface:
				if (frame.getVisibility() == View.GONE) {
					frame.setVisibility(View.VISIBLE);
					showVideoList(false);
				} else {
					frame.setVisibility(View.GONE);
				}
				break;
			case R.id.closeBtn:
			case R.id.play:
				if (mediaPlayerViewCb != null) {
					mediaPlayerViewCb.onClickBtn(view);
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

	private void showVideoList(boolean show) {
		if (show) {
			list.setVisibility(View.VISIBLE);
		} else {
			list.setVisibility(View.GONE);
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
		if (TextUtils.isEmpty(text) || title == null) {
			return;
		}
		title.setText(text);
	}

	public void setRunTimeText(int milliseconds) {
		runTime.setText(getTimeText(milliseconds));
	}

	public void setRemainingTimeText(int milliseconds) {
		remainingTime.setText(getTimeText(milliseconds));
	}

	public void setOnProgressBarClickCb(OnMediaPlayerViewCb callback) {
		mediaPlayerViewCb = callback;
	}

}
