package org.our.android.ouracademy.ui.pages;

import org.our.android.ouracademy.R;
import org.our.android.ouracademy.ui.view.MediaPlayerView;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

/**
 * Media Player Page
 * 
 * @author jyeon
 *
 */
public class MediaPlayerPage extends Activity implements MediaPlayer.OnPreparedListener, OnCompletionListener,
	OnErrorListener, SurfaceHolder.Callback {

	public static final String INTENTKEY_STR_VIDEO_FILE_PATH = "videoFilePath";

	private MediaPlayerView mediaView;

	MediaPlayer player;
	AudioManager audio;
	SeekBar soundVolume;
	SeekBar videoSeekBar;

	SurfaceView preview;
	SurfaceHolder holder;

	boolean wasPlaying;

	String filePath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mediaView = new MediaPlayerView(this);
		mediaView.setOnProgressBarClickCb(mediaPlayerViewCb);
		setContentView(mediaView);

		preview = mediaView.getSurfaceView();
		holder = preview.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		audio = (AudioManager)getSystemService(AUDIO_SERVICE);
		soundVolume = mediaView.getSoundSeekBar();
		if (soundVolume != null) {
			soundVolume.setMax(audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
			soundVolume.setOnSeekBarChangeListener(onSeekSound);
			int volume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
			soundVolume.setProgress(volume);
			mediaView.setSoundIconResource(volume);
		}

		videoSeekBar = mediaView.getVideoSeekBar();
		videoSeekBar.setOnSeekBarChangeListener(onSeek);
		progressHandler.sendEmptyMessageDelayed(0, 200);

		Intent intent = getIntent();
		filePath = intent.getStringExtra(INTENTKEY_STR_VIDEO_FILE_PATH);
		// test Code 
//		String sd = Environment.getExternalStorageDirectory().getAbsolutePath();
//		filePath = sd + "/OurAcademy/PSY-GANGNAM_STYLE_M_V.mp4";
	}

	Handler progressHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (player == null)
				return;
			if (player.isPlaying()) {
				int time = player.getCurrentPosition();
				mediaView.setRunTimeText(time);
				mediaView.setRemainingTimeText(player.getDuration() - time);
				videoSeekBar.setProgress(time);
			}
			progressHandler.sendEmptyMessageDelayed(0, 200);
		}
	};

	SeekBar.OnSeekBarChangeListener onSeek = new SeekBar.OnSeekBarChangeListener() {
		public void onProgressChanged(SeekBar seekBar,
				int progress, boolean fromUser) {
			if (fromUser) {
				player.seekTo(progress);
			}
		}

		public void onStartTrackingTouch(SeekBar seekBar) {
			wasPlaying = player.isPlaying();
			if (wasPlaying) {
				player.pause();
			}
		}

		public void onStopTrackingTouch(SeekBar seekBar) {
			wasPlaying = player.isPlaying();
			if (!wasPlaying) {
				player.start();
				mediaView.postShowFrame(View.GONE);
			}
		}
	};

	SeekBar.OnSeekBarChangeListener onSeekSound = new SeekBar.OnSeekBarChangeListener() {
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			audio.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
			mediaView.setSoundIconResource(progress);
		}

		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		public void onStopTrackingTouch(SeekBar seekBar) {
		}
	};

	@Override
	protected void onPause() {
		super.onPause();

		if (player != null && player.isPlaying()) {
			player.stop();
		}
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub

	}

	public void finishPage() {
		if (player != null) {
			if (player.isPlaying()) {
				player.stop();
			}
			player.release();
			player = null;
		}

		this.finish();
	}

	public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {
	}

	public void surfaceDestroyed(SurfaceHolder surfaceholder) {
	}

	public void surfaceCreated(SurfaceHolder holder) {

		if (player == null) {
			player = new MediaPlayer();
		} else {
			player.reset();
		}

		if (filePath == null) {
			return;
		}

		try {
			player.setDataSource(filePath);
			player.setDisplay(holder);
			player.prepare();
			player.setOnCompletionListener(complete);
			player.setOnVideoSizeChangedListener(sizeChange);

		} catch (Exception e) {
			Toast.makeText(this, "error : " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}

		mediaView.setRunTimeText(0);
		mediaView.setRemainingTimeText(player.getDuration());
		videoSeekBar.setProgress(0);
		videoSeekBar.setMax(player.getDuration());

		mediaView.postShowFrame(View.GONE);
	}

	MediaPlayer.OnCompletionListener complete = new MediaPlayer.OnCompletionListener() {
		public void onCompletion(MediaPlayer arg0) {
			mediaView.setPlayerResource(true);
		}
	};

	MediaPlayer.OnVideoSizeChangedListener sizeChange = new MediaPlayer.OnVideoSizeChangedListener() {
		public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
		}
	};

	MediaPlayerView.OnMediaPlayerViewCb mediaPlayerViewCb = new MediaPlayerView.OnMediaPlayerViewCb() {

		@Override
		public void onClickBtn(View view) {
			switch (view.getId()) {
				case R.id.closeBtn:
					finishPage();
					break;
				case R.id.play:
					if (player.isPlaying() == false) {
						player.start();
						mediaView.setPlayerResource(false);
					} else {
						player.pause();
						mediaView.setPlayerResource(true);
					}
					break;
			}
		}
	};
}
