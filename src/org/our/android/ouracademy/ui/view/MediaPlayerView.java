package org.our.android.ouracademy.ui.view;

import org.our.android.ouracademy.R;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MediaPlayerView extends LinearLayout implements SurfaceHolder.Callback {

	private SurfaceView preview;
	private SurfaceHolder holder;
	MediaPlayer player;
	Button playBtn;

	TextView runTime, remainingTime;

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
		holder = preview.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		playBtn = (Button)findViewById(R.id.play);
		playBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (player.isPlaying() == false) {
					player.start();
					playBtn.setText("Pause");
				} else {
					player.pause();
					playBtn.setText("Play");
				}
			}
		});

		runTime = (TextView)findViewById(R.id.runTime);
		remainingTime = (TextView)findViewById(R.id.remainingTime);
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
		try {
			String sd = Environment.getExternalStorageDirectory().getAbsolutePath();
			player.setDataSource(sd + "/OurAcademy/link_v03_9_24_2012_0.mp4");
			player.setDisplay(holder);
			player.prepare();
			player.setOnCompletionListener(mComplete);
			player.setOnVideoSizeChangedListener(mSizeChange);

		} catch (Exception e) {
			Toast.makeText(getContext(), "error : " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}

		runTime.setText("00:00");
		remainingTime.setText(getTimeText(player.getDuration()));
	}

	MediaPlayer.OnCompletionListener mComplete =
		new MediaPlayer.OnCompletionListener() {
			public void onCompletion(MediaPlayer arg0) {
				playBtn.setText("Play");
			}
		};

	MediaPlayer.OnVideoSizeChangedListener mSizeChange =
		new MediaPlayer.OnVideoSizeChangedListener() {
			public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
			}
		};

//	protected void onDestroy() {
//		super.onDestroy();
//		if (mPlayer != null) {
//			mPlayer.release();
//		}
//	}

	public String getTimeText(int milliseconds) {

		int second = milliseconds / 1000;
		int minutes = second / 60;

		StringBuilder sb = new StringBuilder();
		sb.append(minutes);
		sb.append(":");
		sb.append(second % 60);

//		Log.e("", "time = " + sb.toString());

		return sb.toString();
	}
}
