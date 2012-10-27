package org.our.android.ouracademy.ui.view;


import org.our.android.ouracademy.R;
import org.our.android.ouracademy.model.OurContent;
import org.our.android.ouracademy.model.OurContent.FileStatus;
import org.our.android.ouracademy.ui.pages.MediaPlayerPage;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
*
* @author JiHoon, Moon
*
*/
public class ContentsView extends RelativeLayout implements OnClickListener{
	public ImageView thumbnail;
	public RelativeLayout contentsLayout;
	public TextView title;
	public ProgressBar progressBar;
	
	OurContent ourContents = null;
	
	ProgressAsyncTask progressAsyncTask = null;
	
	public ContentsView(Context context) {
		super(context);
		initUI();
	}

	public ContentsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initUI();
	}

	private void initUI() {
		LayoutInflater.from(getContext()).inflate(R.layout.layout_contents_book_view, this, true);
		thumbnail = (ImageView) findViewById(R.id.thumbnail);
		contentsLayout = (RelativeLayout) findViewById(R.id.book);
		title = (TextView) findViewById(R.id.title);
		progressBar = (ProgressBar) findViewById(R.id.progressbar);
	}
	
	public void setVisibility(int visibility) {
		thumbnail.setVisibility(visibility);
		contentsLayout.setVisibility(visibility);
		title.setVisibility(visibility);
		progressBar.setVisibility(visibility);
	}
	
	public void setContentsData(OurContent ourContents_) {
		this.ourContents = ourContents_;
		
		if (ourContents.fileStatus == FileStatus.DOWNLOADED) {	//파일이 존재
			contentsLayout.setBackgroundResource(R.drawable.btn_main_book_selector);
			progressBar.setVisibility(View.INVISIBLE);
		} else if (ourContents.fileStatus == FileStatus.DOWNLOADING) {	//파일 다운로드 중
			contentsLayout.setBackgroundResource(R.drawable.book_download02);
			progressBar.setVisibility(View.VISIBLE);
			//다운로드 진행 % 셋팅
			
		} else {	//파일이 없는 경우
			contentsLayout.setBackgroundResource(R.drawable.btn_main_book_download_selector);
			progressBar.setVisibility(View.INVISIBLE);
		}
		
		title.setText(ourContents.getSubjectEng());
		contentsLayout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (ourContents.fileStatus == FileStatus.DOWNLOADED) {	//파일이 존재
			String sd = Environment.getExternalStorageDirectory().getAbsolutePath();
			String filePath = sd + "/OurAcademy/HYUNA - 'Ice Cream'.mp4";
			
			Intent intent = new Intent(getContext(), MediaPlayerPage.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(MediaPlayerPage.INTENTKEY_STR_VIDEO_FILE_PATH, filePath);
			getContext().startActivity(intent);
		} else if (ourContents.fileStatus == FileStatus.DOWNLOADING) {	//파일 다운로드 중
			return;
		} else {	//파일이 없는 경우
			//다운로드 시작
			ourContents.fileStatus = FileStatus.DOWNLOADING;
			contentsLayout.setBackgroundResource(R.drawable.book_download02);
			progressBar.setVisibility(View.VISIBLE);
			if (progressAsyncTask == null) {
				progressAsyncTask = new ProgressAsyncTask(progressBar);
			} else {
//				progressAsyncTask.
			}
			progressAsyncTask.execute();
		}
	}
	
//	private void rotateContent(final View view) {
//		ObjectAnimator  animator =  ObjectAnimator.ofFloat(view, "rotationY", 180);
//		animator.setDuration(500);
//		animator.addListener(new AnimatorListenerAdapter() {
//			@Override
//			public void onAnimationEnd(Animator animation) {
//			}
//		});
//		
//		animator.start();
//	}
	
	class ProgressAsyncTask extends AsyncTask<Void, Integer, Long> {
		ProgressBar progressBar;
		public ProgressAsyncTask(ProgressBar progressBar) {
			this.progressBar = progressBar;
		}
		
		@Override
		protected void onPreExecute() {
			publishProgress(0);
		}

		@Override
		protected Long doInBackground(Void... params) {
			for(int i = 1 ; i <= 100; i++) {
	          try {
	            publishProgress(i);
	            Thread.sleep(500);
	          } catch(InterruptedException e) {
	            e.printStackTrace();
	          }
	        }
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... progress) {
			progressBar.setProgress(progress[0]);
		}
		
		@Override
		protected void onCancelled() {

		}
		
		@Override
		protected void onPostExecute(Long result) {
			
		}
	}
	
}
