package org.our.android.ouracademy.ui.pages;

import org.our.android.ouracademy.ui.view.MediaPlayerView;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

public class MediaPlayerPage extends Activity implements MediaPlayer.OnPreparedListener, OnCompletionListener,
	OnErrorListener {

	private MediaPlayerView mediaView;
	private VideoView mediaVideo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mediaView = new MediaPlayerView(this);
		setContentView(mediaView);

		initVideo();
	}

	private void initVideo() {
		mediaVideo = mediaView.getVideo();
		if (mediaVideo == null) {
			finish();
			return;
		}
		mediaVideo.setOnPreparedListener(this);
		mediaVideo.setOnCompletionListener(this);
		mediaVideo.setOnErrorListener(this);

		// test
		Uri videoUri = Uri.parse("http://cctvsec.ktict.co.kr/78/u/FtAQITFukDkiiMvDe87iI4p13U/Ro4QMfGPvWABZg4YNkfb8pbmz5+ku01i0/c");

		if (mediaVideo.isPlaying()) {
			mediaVideo.stopPlayback();
		}
		mediaVideo.setVideoURI(videoUri);
		mediaVideo.start();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mediaVideo.isPlaying()) {
			mediaVideo.stopPlayback();
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
		if (mediaVideo.isPlaying()) {
			mediaVideo.stopPlayback();
		}

		this.finish();
	}
}
