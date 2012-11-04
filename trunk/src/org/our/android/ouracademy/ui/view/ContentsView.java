package org.our.android.ouracademy.ui.view;

import org.our.android.ouracademy.R;
import org.our.android.ouracademy.handler.IOnHandlerMessage;
import org.our.android.ouracademy.handler.WeakRefHandler;
import org.our.android.ouracademy.manager.DataManagerFactory;
import org.our.android.ouracademy.manager.FileManager;
import org.our.android.ouracademy.model.OurContents;
import org.our.android.ouracademy.model.OurContents.FileStatus;
import org.our.android.ouracademy.ui.pages.MediaPlayerPage;
import org.our.android.ouracademy.youtubedownloader.YoutubeContentsTask;
import org.our.android.ouracademy.youtubedownloader.YoutubeContentsTaskCallback;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
*
* @author JiHoon, Moon
*
*/
public class ContentsView extends RelativeLayout implements OnClickListener {
	ImageView thumbnail;
	RelativeLayout contentsLayout;
	TextView title;
	ProgressBar progressBar;
	Button cancelBtn;

	OurContents ourContents = null;

	YoutubeContentsTask contentsTask = null;

	WeakRefHandler progressUpdateHandler;
	
	Context context;
	
	public ContentsView(Context context) {
		super(context);
		this.context = context;
		initUI();
	}

	public ContentsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initUI();
	}
	
	private void initUI() {
		LayoutInflater.from(getContext()).inflate(R.layout.layout_contents_book_view, this, true);
		thumbnail = (ImageView)findViewById(R.id.thumbnail);
		contentsLayout = (RelativeLayout)findViewById(R.id.book);
		title = (TextView)findViewById(R.id.title);
		progressBar = (ProgressBar)findViewById(R.id.progressbar);
		cancelBtn = (Button)findViewById(R.id.cancel_btn);

		contentsLayout.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);
	}

	public void setVisibility(int visibility) {
		thumbnail.setVisibility(visibility);
		contentsLayout.setVisibility(visibility);
		title.setVisibility(visibility);
		progressBar.setVisibility(visibility);
		cancelBtn.setVisibility(visibility);
	}

	public void setContentsData(OurContents ourContents_) {
		reset();
		this.ourContents = ourContents_;
		if (ourContents.fileStatus == FileStatus.DOWNLOADED) { //파일이 존재
			contentsLayout.setBackgroundResource(R.drawable.btn_main_book_selector);
			progressBar.setVisibility(View.INVISIBLE);
			cancelBtn.setVisibility(View.INVISIBLE);
		} else if (ourContents.fileStatus == FileStatus.DOWNLOADING) { //파일 다운로드 중
			contentsLayout.setBackgroundResource(R.drawable.book_download02);
			progressBar.setVisibility(View.VISIBLE);
			cancelBtn.setVisibility(View.VISIBLE);

//			progressBar.setProgress((int)(ourContents_.getDownloadedSize() * 100 / ourContents_.getSize()));
		} else { //파일이 없는 경우

			contentsLayout.setBackgroundResource(R.drawable.btn_main_book_download_selector);
			progressBar.setVisibility(View.INVISIBLE);
			cancelBtn.setVisibility(View.INVISIBLE);
		}
		title.setText(ourContents.getSubjectEng());
	}

	//AdapterView에서 데이타를 init
	private void reset() {
		ourContents = null;
//		progressBar.setProgress(0);
		if (progressUpdateHandler != null) {
			progressUpdateHandler.removeMessages(0);
		}
		if (contentsTask != null) {
			contentsTask.cancel(true);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.book:
				//if exsit file. play
				if (ourContents.fileStatus == FileStatus.DOWNLOADED) {
					String filePath = FileManager.getRealPathFromContentId(ourContents.getId());

					Intent intent = new Intent(getContext(), MediaPlayerPage.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra(MediaPlayerPage.INTENTKEY_STR_VIDEO_FILE_PATH, filePath);
					intent.putExtra(MediaPlayerPage.INTENTKEY_STR_VIDEO_FILE_NAME, ourContents.getSubjectEng());
					getContext().startActivity(intent);
					// if file downloading	
				} else if (ourContents.fileStatus == FileStatus.DOWNLOADING) {
					return;
					// else empty file
				} else {
					ourContents.fileStatus = FileStatus.DOWNLOADING;
					contentsLayout.setBackgroundResource(R.drawable.book_download02);
					progressBar.setVisibility(View.VISIBLE);

					DataManagerFactory.getDataManager().download(ourContents);
					
					//get Download Url
//					YoutubeContentsTask contentsTask = new YoutubeContentsTask(cotentsTaskCallback);
//					contentsTask.execute(ourContents.getContentUrl());
				}
				break;
			case R.id.cancel_btn:
				DataManagerFactory.getDataManager().cancleDownload(ourContents);
				break;
		}
	}

	YoutubeContentsTaskCallback cotentsTaskCallback = new YoutubeContentsTaskCallback() {
		@Override
		public void onCompletedContentResult(String url) {
			ourContents.fileStatus = FileStatus.DOWNLOADING;

			progressUpdateHandler = new WeakRefHandler(iOnHandlerMessage);
			progressUpdateHandler.sendEmptyMessageDelayed(0, 1000);
		}
	};

	IOnHandlerMessage iOnHandlerMessage = new IOnHandlerMessage() {
		@Override
		public void handleMessage(Message msg) {
			updatingProgressbar();
			progressUpdateHandler.sendEmptyMessageDelayed(0, 1000);
		}
	};

	//update progressbar
	private void updatingProgressbar() {
	}

	//complete download contents
	public void setCompleteDownload() {
		progressBar.setProgress(100); //set progressbar to max
		ourContents.fileStatus = FileStatus.DOWNLOADED;
		setContentsData(ourContents);
	}
}
