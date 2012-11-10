package org.our.android.ouracademy.downloader;

import java.io.File;

import org.our.android.ouracademy.R;
import org.our.android.ouracademy.downloader.IDownloadService;
import org.our.android.ouracademy.downloader.JobManager.Entry;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.RemoteViews;



public class DownloadService extends Service {

	public static final String DOWNLOAD_SERVICE = "org.our.android.ouracademy.downloader.DOWNLOAD_SERVICE";
	public static final String DATATYPE_ID = "id://myhost/id";
	public static final String ACTION_DOWNLOAD_COMPLETED = "org.our.android.ouracademy.downloader.ACTION_DOWNLOAD_COMPLETED";
	public static final String ACTION_DOWNLOAD_FAILED = "org.our.android.ouracademy.downloader.ACTION_DOWNLOAD_FAILED";
	public static final String ACTION_NOTIFICATION_CLICKED = "org.our.android.ouracademy.downloader.ACTION_NOTIFICATION_CLICKED";
	public static final String ACTION_NOTIFICATION_CLEARED = "org.our.android.ouracademy.downloader.ACTION_NOTIFICATION_CLEARED";
	
	public static final String INTEXTRA_ID = "id";
	static final String BOOLEANEXTRA_LOCAL = "local";
	
	private static final int N_WORKER_THREAD = 2;
	private static final int WAITTIME_THREAD = (60*1000);
	
	private final LocalBinder mLocalBinder = new LocalBinder();
	private final RemoteBinder mRemoteBinder = new RemoteBinder();
	
	//private final WatchdogThread mWatchdog = new WatchdogThread(N_WORKER_THREAD);	
	private final JobManager mJobQueue = new JobManager(this);
	
	private int mDownloadThreadsCount = N_WORKER_THREAD;
	private DownloadThread[] mDownloadThreads = new DownloadThread[N_WORKER_THREAD];
	private boolean mAllUnbounded = false;
	
	@Override
	public void onCreate() {
		Log.d("csm", "Service onCreate ");
		super.onCreate();

		mJobQueue.loadQueue();
		startThreads();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("csm", "Service onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d("csm", "Service onBind");
		mAllUnbounded = false;
		if ( true == intent.getBooleanExtra(BOOLEANEXTRA_LOCAL, false) ) {
			return mLocalBinder;
		} else {
			return mRemoteBinder;
		}
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		Log.d("csm", "Service onUnbind");
		mAllUnbounded = true;
		return super.onUnbind(intent);
	}
	
	@Override
	public void onDestroy() {
		Log.d("csm", "Service onDestroy");
		
		// stop service from Running services panel by user
		for(int i = 0 ; i < mDownloadThreadsCount ; i++) {
			mDownloadThreads[i].exit();
		}
		mJobQueue.notifyForAll();
		
		//mJobQueue.savaQueue();
		super.onDestroy();
	}

    class LocalBinder extends Binder {
    	public DownloadService getService() {
            return DownloadService.this;
        }
    }

	private class RemoteBinder extends IDownloadService.Stub {
		@Override
		public int add(String url, String path, boolean visibility, int reserved)
				throws RemoteException {
			return DownloadService.this.add(url, path, visibility, reserved);
		}

		@Override
		public boolean cancel(int id) throws RemoteException {
			boolean ret = DownloadService.this.cancel(id);
			NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
			if (null != nm) {
				nm.cancel(id);
			}
			return ret;
		}
	}

	public int add(String url, String path, boolean visibility, int reserved) {
		
		int id = 0;
		Entry e = new Entry();
		e.mUrl = url;
		e.mPath = path;
		e.mVisibility = visibility;
		e.mReserved = reserved;
		
		id = mJobQueue.add(e);
		mJobQueue.notifyForEntry();
		
		return id;
	}	

	public boolean cancel(int id) {
		mJobQueue.cancel(id);
		return true;
	}
	
	private void startThreads() {
		for(int i = 0 ; i < mDownloadThreadsCount ; i++) {
			mDownloadThreads[i] = new DownloadThread();
			mDownloadThreads[i].start();
		}
	}
	
	private class NotificationProgress implements HttpUrlDownload.OnProgressListener {
		
		private Entry mEntry;
		private NotificationManager mNotiManager;
		private RemoteViews mRemoteView;
		private Notification mNotification;
		private int mPreviousProgress = 0;
		private boolean mStopProgress = false;
		private String titleFile;
		
		public NotificationProgress(Entry e) {
			mEntry = e;
			
			mNotiManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
			if (null != mNotiManager) {
				//status_bar_ongoing_event_progress_bar
				
				int namePos = e.mPath.lastIndexOf("/");
				if (-1 != namePos) {
					titleFile = e.mPath.substring(namePos+1);
				} else {
					titleFile = e.mPath;
				}
				
				Log.d("csm", "NotificationProgress : " + mEntry.mId);

				Intent clicked = new Intent(ACTION_NOTIFICATION_CLICKED);
				Uri u = Uri.parse(DATATYPE_ID + "/" + String.valueOf(mEntry.mId));
				clicked.setData(u);
				//clicked.putExtra(INTEXTRA_ID, mEntry.mId);
				PendingIntent clickedPIntent = PendingIntent.getBroadcast(DownloadService.this, 0, clicked, 0);
				
				Intent cleared = new Intent(ACTION_NOTIFICATION_CLEARED);
				u = Uri.parse(DATATYPE_ID + "/" + String.valueOf(mEntry.mId));
				cleared.setData(u);
				//cleared.putExtra(INTEXTRA_ID, mEntry.mId);
				PendingIntent clearedPIntent = PendingIntent.getBroadcast(DownloadService.this, 0, cleared, 0);
				
				mNotification = new Notification();
				mNotification.icon = android.R.drawable.stat_sys_download;
				mNotification.when = System.currentTimeMillis();
				mNotification.flags |= (Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR);
				mNotification.contentIntent = clickedPIntent;
				mNotification.deleteIntent = clearedPIntent;
				
				mRemoteView = new RemoteViews(getPackageName(), R.layout.status_bar_ongoing_download_notification);
				mRemoteView.setTextViewText(R.id.title, titleFile);
				mRemoteView.setProgressBar(R.id.progress_bar, 100, 0, false);
				mNotification.contentView = mRemoteView;
			}
		}
		
		public void stopManually() {
			mStopProgress = true;
		}
		
		public boolean isManuallyStoped() {
			return mStopProgress;
		}
		
		@Override
		public int onBegin() {
			return HttpUrlDownload.PROGRESS_NORMAL;
		}
		
		@Override
		public int onProgress(int progress) {
			// job canceled or service canceled by user
			if ( JobManager.STATE_CANCEL == mEntry.mState || true == isManuallyStoped() ) {
				Log.d("csm", "Service onProgress CANCEL : " + mEntry.mId);
				return HttpUrlDownload.PROGRESS_CANCEL;	// stop download
			}
			
			if (true == mEntry.mVisibility) {
				// notification
				if (mPreviousProgress < progress) {
					mPreviousProgress = progress;
					mRemoteView.setTextViewText( R.id.progress_text, String.valueOf(progress) + "%" );
					mRemoteView.setProgressBar(R.id.progress_bar, 100, progress, false);
					mNotiManager.notify(mEntry.mId, mNotification);
				}
			}

			return HttpUrlDownload.PROGRESS_NORMAL;
		}
		
		@Override
		public void onEnd(int result) {
			// job canceled or service canceled by user
			if ( JobManager.STATE_CANCEL == mEntry.mState || true == isManuallyStoped() ) {
				return;
			}
			
			if (true == mEntry.mVisibility) {
				mNotiManager.cancel(mEntry.mId);	// delete ongoning

				String resultMsg = "";
				if (HttpUrlDownload.PROGRESS_NORMAL == result) {
					resultMsg = getString(R.string.download_complete);
				} else if (HttpUrlDownload.PROGRESS_FAIL == result) {
					resultMsg = getString(R.string.download_unsuccessful);
				}
				
				Intent clicked = new Intent(ACTION_NOTIFICATION_CLICKED);
				Uri u = Uri.parse(DATATYPE_ID + "/" + String.valueOf(mEntry.mId));
				clicked.setData(u);
				//clicked.putExtra(INTEXTRA_ID, mEntry.mId);
				PendingIntent clickedPIntent = PendingIntent.getBroadcast(DownloadService.this, 0, clicked, 0);
				
				Intent cleared = new Intent(ACTION_NOTIFICATION_CLEARED);
				u = Uri.parse(DATATYPE_ID + "/" + String.valueOf(mEntry.mId));
				cleared.setData(u);
				//cleared.putExtra(INTEXTRA_ID, mEntry.mId);
				PendingIntent clearedPIntent = PendingIntent.getBroadcast(DownloadService.this, 0, cleared, 0);
				
				mNotification = new Notification();
				mNotification.icon = android.R.drawable.stat_sys_download_done;
				mNotification.when = System.currentTimeMillis();
				mNotification.flags |= (Notification.FLAG_AUTO_CANCEL);
				mNotification.contentIntent = clickedPIntent;
				mNotification.deleteIntent = clearedPIntent;
				mNotification.setLatestEventInfo(DownloadService.this, titleFile, resultMsg, clickedPIntent);
				
				mNotiManager.notify(mEntry.mId, mNotification);
			}

			Intent intent = null;
			switch(result) {
			case HttpUrlDownload.PROGRESS_NORMAL:
				Log.d("csm", "SCAN Path ---> "+mEntry.mPath);
				Uri uri = Uri.parse("file://"+mEntry.mPath);
				Intent broadIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri);
				sendBroadcast(broadIntent);
				
				intent = new Intent(ACTION_DOWNLOAD_COMPLETED);
				break;
			case HttpUrlDownload.PROGRESS_CANCEL:
				break;
			case HttpUrlDownload.PROGRESS_FAIL:
				intent = new Intent(ACTION_DOWNLOAD_FAILED);
				break;
			default:
				break;
			}
			
			if (null != intent) {
				//intent.putExtra(INTEXTRA_ID, mEntry.mId);
				Uri u = Uri.parse(DATATYPE_ID + "/" + String.valueOf(mEntry.mId));
				intent.setData(u);
				getApplication().sendBroadcast(intent);
			}
		}
	}

	private class DownloadThread extends Thread {

		private boolean mLoop = true;
		NotificationProgress mProgress = null;
		
		@Override
		public void run() {

			Entry e = null;
			while (true == mLoop) {
				
				e = mJobQueue.getNext();
				if (null != e) {
					String target = HttpUrlDownload.makeUrlToPathFile(e.mUrl, e.mPath);
					e.mPath = target;
					mJobQueue.savaQueue();	// Save mState(getNext) & mPath
					
					Log.d("csm", "HttpUrlDownload.download : " + e.mPath);

					boolean removeJob = false;
					boolean deleteFile = false;
					
					mProgress = new NotificationProgress(e);
					int result = HttpUrlDownload.download(e.mUrl, e.mPath, mProgress);
					switch (result) {
					case HttpUrlDownload.PROGRESS_NORMAL:
						removeJob = true;
						break;
					case HttpUrlDownload.PROGRESS_CANCEL:
						// 스레드 실행중에 종료(서비스 종료시)된 것이라면 파일과 job을 남겨놓아서 다음에 이어받기 가능하게 한다.
						if ( false == mProgress.isManuallyStoped() ) {
							deleteFile = true;
							removeJob = true;
						}
						break;
					case HttpUrlDownload.PROGRESS_FAIL:
						deleteFile = true;
						removeJob = true;
						break;
					default:
						removeJob = true;
						break;
					}
					
					if (true == removeJob) {
						mJobQueue.remove(e.mId);
					}
					
					if (true == deleteFile) {
						File file = new File(e.mPath);
						if ( true == file.exists() ) {
							file.delete();	
						}
					}
					
				} else {
					
					// blocking
					mJobQueue.waitForEntry(WAITTIME_THREAD);
					
					// 바인딩 상태가 아니고 다운로드(진행중이거나 남아있는)가 없으면 종료.
					if ( true == mAllUnbounded && 0 == mJobQueue.size() ) {
						mJobQueue.notifyForAll();

						Log.d("csm", "service - stopSelf");
						stopSelf();
						break;
					}
				}
			}	// while (true == mLoop)

		}
		
		public void exit() {
			mLoop = false;
			if (null != mProgress) {
				mProgress.stopManually();
			}
		}
	}
}




