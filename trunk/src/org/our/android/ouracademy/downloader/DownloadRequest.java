package org.our.android.ouracademy.downloader;

import java.io.File;
import java.io.IOException;

import org.our.android.ouracademy.downloader.IDownloadService;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class DownloadRequest {

	private static final String PATH_DEFAULT_DOWNLOAD = "Download";
 
	private static DownloadRequest mThis = null;
	
	private Context mAppContext = null;
	private boolean mLocal = false;
	
	private DownloadService mService = null;
	private IDownloadService mIService = null;
	
	//private final ThreadLocal<DownloadServiceConnection> mTlsConnection = new ThreadLocal<DownloadServiceConnection>();
	//private final ThreadLocal<PrepareCallback> mTlsPrepareCallback = new ThreadLocal<PrepareCallback>();
	private DownloadServiceConnection mConnection = null;
	private PrepareCallback mPrepareCallback;
	
	public interface PrepareCallback {
		void onStart();
		void onStop();
	}

	private DownloadRequest(Context applicationContext, boolean local) {
		mAppContext = applicationContext;
		mLocal = local;
	}
	 
	public synchronized static DownloadRequest getInstance(Context applicationContext, boolean local) {
		if (null == applicationContext) {
			return null;
		}
		
		if (null == mThis) {
			mThis = new DownloadRequest(applicationContext, local);
		}

		return mThis;
	}
	
	public synchronized boolean start(PrepareCallback cb) {
		mPrepareCallback = cb;
		if ( false == isStartCompleted() ) {
			Intent intent = new Intent(DownloadService.DOWNLOAD_SERVICE);
			if ( null == mAppContext.startService(intent) ) {
				return false;
			}
			
			if (true == mLocal) {
				intent.putExtra(DownloadService.BOOLEANEXTRA_LOCAL, true);
			}
			mConnection = new DownloadServiceConnection();
			mAppContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

		} else {
			cb.onStart();
		}

		return true;
	}

	public synchronized void stop() {
		if ( true == isStartCompleted() ) {
			if (null != mConnection) {
				mAppContext.unbindService(mConnection);
				mConnection = null;
				mService = null;
				mIService = null;
			}
		}
		mPrepareCallback.onStop();
	}

	public int add(String url, boolean visibility, int reserved) {

		// default path
		final String pathName = Environment.getExternalStorageDirectory() + "/" + PATH_DEFAULT_DOWNLOAD;

		File path = new File(pathName);
		if (false == path.exists()) {
			path.mkdirs();
		}

		return addService(url, pathName, visibility, reserved);
	}
	
	public int add(String url, String pathName, String fileName, boolean visibility, int reserved) {

		File path = new File(pathName);
		if (false == path.exists()) {
			path.mkdirs();
		}
		
		String pathFile = pathName + fileName;
		File file = new File(pathFile);
		if (false == file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}	
		}

		return addService(url, pathFile, visibility, reserved);
	}	

	private synchronized int addService(String url, String path, boolean visibility, int reserved) {
		int id = 0;
		if ( true == isStartCompleted() ) {	
			if (true == mLocal) {
				id =  mService.add(url, path, visibility, reserved);
			} else {
				try {
					id =  mIService.add(url, path, visibility, reserved);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
		Log.d("csm", "DownloadRequest addService : " + id);
		return id;
	}

	public synchronized boolean cancel(int id) {
		Log.d("csm", "DownloadRequest cancel : " + id);
		boolean result = false;
		if ( true == isStartCompleted() ) {
			if (true == mLocal) {
				result =  mService.cancel(id);
			} else {
				try {
					result =  mIService.cancel(id);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}
	
	private boolean isStartCompleted() {
		boolean ret = false;
		if (true == mLocal) {
			if (null != mService) {
				ret = true;
			}
		} else {
			if (null != mIService) {
				ret = true;
			}
		}
		
		return ret;
	}
	
	private class DownloadServiceConnection implements ServiceConnection {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d("csm", "onServiceConnected");
			synchronized(DownloadRequest.this) {
				if (true == mLocal) {
					mService = ((DownloadService.LocalBinder)service).getService();
				} else {
					mIService = IDownloadService.Stub.asInterface(service);
				}
				mPrepareCallback.onStart();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d("csm", "onServiceDisconnected");
			synchronized(DownloadRequest.this) {
				mService = null;
				mIService = null;
				mPrepareCallback.onStop();
			}
		}
	}
}
