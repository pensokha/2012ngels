package org.our.android.ouracademy.ui.view;

import java.io.File;

import org.our.android.ouracademy.R;
import org.our.android.ouracademy.constants.CommonConstants;
import org.our.android.ouracademy.constants.MatchCategoryColor;
import org.our.android.ouracademy.handler.WeakRefHandler;
import org.our.android.ouracademy.manager.DataManagerFactory;
import org.our.android.ouracademy.manager.FileManager;
import org.our.android.ouracademy.model.OurContents;
import org.our.android.ouracademy.model.OurContents.FileStatus;
import org.our.android.ouracademy.ui.pages.MediaPlayerPage;
import org.our.android.ouracademy.ui.widget.NCTextView;
import org.our.android.ouracademy.util.NetworkState;
import org.our.android.ouracademy.youtubedownloader.YoutubeContentsTask;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * 
 * @author JiHoon, Moon
 * 
 */
public class ContentsView extends RelativeLayout implements OnClickListener {
	ImageView thumbnail;
	RelativeLayout contentsLayout;
	NCTextView categoryText;
	NCTextView subjectText;
	ViewGroup progressLayout;
	ProgressBar progressBar;
	NCTextView progressText;
	Button cancelBtn;
	NCTextView downloadText;
	NCTextView fullText;

	OurContents ourContents = null;

	YoutubeContentsTask contentsTask = null;

	WeakRefHandler progressUpdateHandler;

	Context context;

	DeleteCallBack deleteCallBack;

	public interface DeleteCallBack {
		abstract void onDeleteSuccessfully(OurContents ourContent);
	}

	public void setCallBack(DeleteCallBack listener) {
		this.deleteCallBack = listener;
	}

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
		categoryText = (NCTextView)findViewById(R.id.category_text);
		subjectText = (NCTextView)findViewById(R.id.subject_text);
		progressLayout = (ViewGroup)findViewById(R.id.progress_layout);
		progressText = (NCTextView)findViewById(R.id.progress_text);
		progressBar = (ProgressBar)findViewById(R.id.progressbar);
		cancelBtn = (Button)findViewById(R.id.cancel_btn);
		downloadText = (NCTextView)findViewById(R.id.download_text);
		fullText = (NCTextView)findViewById(R.id.fullText);

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
			if (ourContents.isDeleteMode()) {
				contentsLayout.setBackgroundResource(MatchCategoryColor.getCategoryMatchColorId(ourContents.selectedCategory == null
					? "" : ourContents.selectedCategory.getCategoryId()));
				setProgressLayoutVisible(false);
				cancelBtn.setVisibility(View.VISIBLE);
				downloadText.setVisibility(View.INVISIBLE);
			} else {
				contentsLayout.setBackgroundResource(MatchCategoryColor.getCategoryMatchColorId(ourContents.selectedCategory == null
					? "" : ourContents.selectedCategory.getCategoryId()));
				setProgressLayoutVisible(false);
				cancelBtn.setVisibility(View.INVISIBLE);
				downloadText.setVisibility(View.INVISIBLE);
			}

			if (ourContents.isFullTextMode()) {
				fullText.setVisibility(View.VISIBLE);
				fullText.setText(ourContents.getSubject());
			}

			final String existingFile = CommonConstants.getContentImagePath(ourContents.getId());
			if (new File(existingFile).exists()) {
				thumbnail.setImageBitmap(BitmapFactory.decodeFile(existingFile));
			}
		} else if (ourContents.fileStatus == FileStatus.DOWNLOADING) {
			contentsLayout.setBackgroundResource(R.drawable.book_download02);
			setProgressLayoutVisible(true);
			cancelBtn.setVisibility(View.VISIBLE);
			downloadText.setVisibility(View.INVISIBLE);
			progressBar.setProgress((int)(ourContents.getDownloadedSize() * 100 / ourContents.getSize()));
			progressText.setText((ourContents.getDownloadedSize() * 100 / ourContents.getSize()) + "%");
		} else { // 파일이 없는 경우
			contentsLayout.setBackgroundResource(R.drawable.btn_main_book_download_selector);
			setProgressLayoutVisible(false);
			cancelBtn.setVisibility(View.INVISIBLE);
			downloadText.setVisibility(View.VISIBLE);
		}
		categoryText.setText(ourContents.selectedCategory == null ? ""
			: ourContents.selectedCategory.getCategoryTitle());
		categoryText.setAlpha(0.6f);
		subjectText.setText(ourContents.getSubject());
	}

	// AdapterView에서 데이타를 init
	private void reset() {
		ourContents = null;
		progressBar.setProgress(0);
		thumbnail.setImageDrawable(null);
		if (progressUpdateHandler != null) {
			progressUpdateHandler.removeMessages(0);
		}
		if (contentsTask != null) {
			contentsTask.cancel(true);
		}
		fullText.setVisibility(View.GONE);
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
//					Log.d("Test", "Click");
					if (NetworkState.isWifiDirectConnected()) {
						ourContents.fileStatus = FileStatus.DOWNLOADING;
						contentsLayout.setBackgroundResource(R.drawable.book_download02);
						setProgressLayoutVisible(true);

						progressBar.setProgress((int)(ourContents.getDownloadedSize() * 100 / ourContents.getSize()));

						DataManagerFactory.getDataManager().download(ourContents);
					} else {
						ViewGroup layout = (LinearLayout)LayoutInflater.from(getContext()).inflate(
							R.layout.layout_connect_teacher, null);
						;
						Builder builder = new AlertDialog.Builder(getContext());
						builder.setView(layout);
						builder.setPositiveButton(R.string.ok, null);
						builder.setCancelable(true);
						builder.create().show();
					}
				}
				break;
			case R.id.cancel_btn:
				if (ourContents.isDeleteMode()) {
					showDeleteConfirmDialog(ourContents);
				} else {
					DataManagerFactory.getDataManager().cancelDownload(ourContents);
				}
				break;
		}
	}

	/**
	 * 삭제 확인 다이어로그창.
	 * @author Sung-Chul Park.
	 */
	void showDeleteConfirmDialog(final OurContents deleteContents) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(R.string.delete_dialog_body);
		builder.setPositiveButton(R.string.delete_ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//실제 데이터 지움.
				if (!deleteFile(deleteContents.getId())) {
					Toast.makeText(context, "Delete operation failed", Toast.LENGTH_LONG).show();
					return;
				}

				if (deleteCallBack != null) {
					deleteCallBack.onDeleteSuccessfully(deleteContents);
				}
			}
		});
		builder.setNegativeButton(R.string.delete_cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	boolean deleteFile(String deleteFileId) {
		//실제 데이터 지움.
		String deleteFilePath = CommonConstants.getContentFilePathPlusMP4(deleteFileId);
		File deleteFile = new File(deleteFilePath);

		return deleteFile.delete();
	}
}
