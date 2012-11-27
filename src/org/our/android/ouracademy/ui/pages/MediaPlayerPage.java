package org.our.android.ouracademy.ui.pages;

import java.util.ArrayList;

import org.our.android.ouracademy.OurPreferenceManager;
import org.our.android.ouracademy.R;
import org.our.android.ouracademy.dao.CategoryDAO;
import org.our.android.ouracademy.dao.ContentDAO;
import org.our.android.ouracademy.dao.DAOException;
import org.our.android.ouracademy.manager.FileManager;
import org.our.android.ouracademy.model.OurCategory;
import org.our.android.ouracademy.model.OurContents;
import org.our.android.ouracademy.ui.adapter.PlayListAdapter;
import org.our.android.ouracademy.ui.view.MediaPlayerView;
import org.our.android.ouracademy.ui.view.PlayerListItemView.PlayerListItemViewListener;
import org.our.android.ouracademy.ui.widget.NCHorizontalListView;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
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
	public static final String INTENTKEY_STR_VIDEO_FILE_NAME = "videoFileName";
//	public static final String INTENTKEY_STR_SELECTED_CATEGORY = "selectedCategories";

	private MediaPlayerView mediaView;

	MediaPlayer player;
	AudioManager audio;
	SeekBar soundVolume;
	SeekBar videoSeekBar;

	SurfaceView preview;
	SurfaceHolder holder;

	NCHorizontalListView listView;
	PlayListAdapter playListAdapter;

	boolean wasPlaying;

	String filePath;

//	String selectedCategories;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mediaView = new MediaPlayerView(this);
		mediaView.setOnMediaPlayerViewListener(mediaPlayerViewListener);
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

		listView = mediaView.getListView();
		playListAdapter = new PlayListAdapter();
		listView.setAdapter(playListAdapter);

		getIntentData();

		loadContents();
	}

	private void getIntentData() {
		Intent intent = getIntent();
		filePath = intent.getStringExtra(INTENTKEY_STR_VIDEO_FILE_PATH);
		String title = intent.getStringExtra(INTENTKEY_STR_VIDEO_FILE_NAME);
//		selectedCategories = intent.getStringExtra(INTENTKEY_STR_SELECTED_CATEGORY);
		if (mediaView != null) {
			mediaView.setTitleText(title);
		}
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
			mediaView.removeFullScreenCallback();
			wasPlaying = player.isPlaying();
			if (wasPlaying) {
				player.pause();
			}
		}

		public void onStopTrackingTouch(SeekBar seekBar) {
			wasPlaying = player.isPlaying();
			if (!wasPlaying) {
				startPlayer();
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
	protected void onDestroy() {
		super.onDestroy();
//		if (player != null) {
//			player.release();
//		}
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
		}

		player.setDisplay(holder);
		player.setOnCompletionListener(complete);
		player.setOnVideoSizeChangedListener(sizeChange);

		preparePlayer();

		startPlayer();
	}

	private void preparePlayer() {
		if (filePath == null) {
			return;
		}

		try {
			player.reset();
			player.setDataSource(filePath);
			player.prepare();

		} catch (Exception e) {
			Toast.makeText(this, "error : " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}

		mediaView.setRunTimeText(0);
		mediaView.setRemainingTimeText(player.getDuration());
		videoSeekBar.setProgress(0);
		videoSeekBar.setMax(player.getDuration());
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

	MediaPlayerView.MediaPlayerViewListener mediaPlayerViewListener = new MediaPlayerView.MediaPlayerViewListener() {

		@Override
		public void onClickBtn(View view) {
			switch (view.getId()) {
				case R.id.closeBtn:
					finishPage();
					break;
				case R.id.play:
					if (player != null && player.isPlaying() == false) {
						startPlayer();
					} else {
						pausePlayer();
					}
					break;
			}
		}
	};

	public void startPlayer() {
		if (player != null) {
			player.start();
		}

		if (mediaView != null) {
			mediaView.setPlayerResource(false);
			mediaView.postShowFrame(View.GONE);
		}
	}

	public void pausePlayer() {
		if (player != null) {
			player.pause();
		}

		if (mediaView != null) {
			mediaView.setPlayerResource(true);
		}
	}

	private void loadContents() {
		String selectedCategoryId = OurPreferenceManager.getInstance().getSelecetedCategory();
		if (selectedCategoryId == null) {
			return;
		}

		OurCategory selectedCategory = null;
		ArrayList<OurCategory> categoryList = null;
		try {
			categoryList = new CategoryDAO().getCategories();
		} catch (DAOException e) {
			e.printStackTrace();
		}

		for (OurCategory category : categoryList) {
			if (category.getCategoryId().compareTo(selectedCategoryId) == 0) {
				selectedCategory = category;
				break;
			}
		}

		ArrayList<OurContents> contents = new ArrayList<OurContents>();

		try {

			ArrayList<OurContents> contentsFromDB = new ContentDAO().getDuplicatedContents(selectedCategoryId);

			for (OurContents content : contentsFromDB) {
				if (content.fileStatus == OurContents.FileStatus.DOWNLOADED) {
					if (selectedCategory != null) {
						content.selectedCategory = selectedCategory;
					}
					contents.add(content);
				}
			}

			if (playListAdapter != null) {
				playListAdapter.setData(contents);
				playListAdapter.setOnPlayerListViewListener(listListener);
			}

		} catch (DAOException e) {
			e.printStackTrace();
		}
	}

	PlayerListItemViewListener listListener = new PlayerListItemViewListener() {

		@Override
		public void onClickBook(OurContents contents) {
			if (contents != null) {
				pausePlayer();
				filePath = FileManager.getRealPathFromContentId(contents.getId());
				if (mediaView != null) {
					mediaView.setTitleText(contents.getSubject());
					mediaView.showVideoList(false);
				}
				preparePlayer();
				startPlayer();
			}
		}
	};
}
