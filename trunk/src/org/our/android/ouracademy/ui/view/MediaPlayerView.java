package org.our.android.ouracademy.ui.view;

import org.our.android.ouracademy.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.VideoView;

public class MediaPlayerView extends LinearLayout {

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
	}

	public VideoView getVideo() {
		VideoView cctvVideo = (VideoView)findViewById(R.id.video);
		return cctvVideo;
	}

}
