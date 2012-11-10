package org.our.android.ouracademy.ui.view;

import org.our.android.ouracademy.R;
import org.our.android.ouracademy.handler.WeakRefHandler;
import org.our.android.ouracademy.manager.DataManagerFactory;
import org.our.android.ouracademy.manager.FileManager;
import org.our.android.ouracademy.model.OurContents;
import org.our.android.ouracademy.model.OurContents.FileStatus;
import org.our.android.ouracademy.ui.pages.MediaPlayerPage;
import org.our.android.ouracademy.util.MatchCategoryColor;
import org.our.android.ouracademy.util.NetworkState;
import org.our.android.ouracademy.youtubedownloader.YoutubeContentsTask;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
	TextView categoryText;
	TextView subjectText;
	ViewGroup progressLayout;
	ProgressBar progressBar;
	TextView progressText;
	Button cancelBtn;
	TextView downloadText;

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
		thumbnail = (ImageView) findViewById(R.id.thumbnail);
		contentsLayout = (RelativeLayout) findViewById(R.id.book);
		categoryText = (TextView) findViewById(R.id.category_text);
		subjectText = (TextView) findViewById(R.id.subject_text);
		progressLayout = (ViewGroup)findViewById(R.id.progress_layout);
		progressText = (TextView) findViewById(R.id.progress_text);
		progressBar = (ProgressBar) findViewById(R.id.progressbar);
		cancelBtn = (Button) findViewById(R.id.cancel_btn);
		downloadText = (TextView) findViewById(R.id.download_text);

		contentsLayout.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);
	}

	public void setAllVisibility(View view, int visibility) {
		if (view != null) {
			view.setVisibility(visibility);
			if (view instanceof ViewGroup) {
				int nrOfChildren = ((ViewGroup)view).getChildCount();
				for (int i = 0; i < nrOfChildren; i++) {
					setAllVisibility(((ViewGroup)view).getChildAt(i), visibility);
				}
			}
		}
	}

	public void setContentsData(OurContents ourContents_) {
		reset();
		this.ourContents = ourContents_;
		if (ourContents.fileStatus == FileStatus.DOWNLOADED) { // 파일이 존재
			contentsLayout.setBackgroundResource(MatchCategoryColor.getCategoryMatchColorId(ourContents.selectedCategory == null ? "" : ourContents.selectedCategory.getCategoryId()));
			setProgressLayoutVisible(false);
			cancelBtn.setVisibility(View.INVISIBLE);
			downloadText.setVisibility(View.INVISIBLE);
		} else if (ourContents.fileStatus == FileStatus.DOWNLOADING) { 
			contentsLayout.setBackgroundResource(R.drawable.book_download02);
			setProgressLayoutVisible(true);
			cancelBtn.setVisibility(View.VISIBLE);
			downloadText.setVisibility(View.INVISIBLE);
			progressBar.setProgress((int) (ourContents.getDownloadedSize() * 100 / ourContents.getSize()));
			progressText.setText((ourContents.getDownloadedSize() * 100 / ourContents.getSize()) + "%");
			
		} else { // 파일이 없는 경우
			contentsLayout.setBackgroundResource(R.drawable.btn_main_book_download_selector);
			setProgressLayoutVisible(false);
			cancelBtn.setVisibility(View.INVISIBLE);
			downloadText.setVisibility(View.VISIBLE);
		}
		categoryText.setText(ourContents.selectedCategory == null ? "" : ourContents.selectedCategory.getCategoryTitle());
		subjectText.setText(ourContents.getSubject());
	}

	// AdapterView에서 데이타를 init
	private void reset() {
		ourContents = null;
		progressBar.setProgress(0);
		if (progressUpdateHandler != null) {
			progressUpdateHandler.removeMessages(0);
		}
		if (contentsTask != null) {
			contentsTask.cancel(true);
		}
	}
	
	private void setProgressLayoutVisible(boolean setVisible) {
		if (setVisible) {
			progressLayout.setVisibility(View.VISIBLE);
			progressText.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.VISIBLE);
		} else {
			progressLayout.setVisibility(View.GONE);
			progressText.setVisibility(View.GONE);
			progressBar.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.book:
			// if exsit file. play
			if (ourContents.fileStatus == FileStatus.DOWNLOADED) {
				String filePath = FileManager.getRealPathFromContentId(ourContents.getId());

				Intent intent = new Intent(getContext(), MediaPlayerPage.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(MediaPlayerPage.INTENTKEY_STR_VIDEO_FILE_PATH, filePath);
				intent.putExtra(MediaPlayerPage.INTENTKEY_STR_VIDEO_FILE_NAME,
						ourContents.getSubject());
				getContext().startActivity(intent);
				// if file downloading
			} else if (ourContents.fileStatus == FileStatus.DOWNLOADING) {
				return;
				// else empty file
			} else {
				Log.d("Test", "Click");
				if (NetworkState.isWifiDirectConnected()) {

					ourContents.fileStatus = FileStatus.DOWNLOADING;
					contentsLayout.setBackgroundResource(R.drawable.book_download02);
					setProgressLayoutVisible(true);

					progressBar.setProgress((int) (ourContents.getDownloadedSize() * 100 / ourContents.getSize()));

					DataManagerFactory.getDataManager().download(ourContents);
				}
			}
			break;
		case R.id.cancel_btn:
			DataManagerFactory.getDataManager().cancelDownload(ourContents);
			break;
		}
	}
}
