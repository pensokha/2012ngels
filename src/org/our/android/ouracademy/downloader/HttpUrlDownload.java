package org.our.android.ouracademy.downloader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.util.Log;

public class HttpUrlDownload {

	public static final int PROGRESS_NORMAL = 1;
	public static final int PROGRESS_CANCEL = 2;
	public static final int PROGRESS_FAIL = 3;
	
	private static final int SIZE_INPUT_BUFFER = (100*1024);
	private static final int CONNECT_TIMEOUT = (20*1000);
	private static final int READ_TIMEOUT = (30*1000);
	
	public static interface OnProgressListener {
		int onBegin();
		int onProgress(int progress);	// 0~100
		void onEnd(int result);	// PROGRESS_XXX
	}
	
    /**
     * 파일(이어받기 또는 사용자가 지정한 경우)이 path에 포함된 경우는 그대로 다운로드 진행,
     * 디렉토리 경로만 넘어온 경우에는 url로 부터 파일 이름 생성
     */
	public static String makeUrlToPathFile(String url, String path) {

		String pathFile = path;
		File file = new File(pathFile);

		if ( true == file.isDirectory() ) {

			String name, ext;
			int namePos = url.lastIndexOf("/");
			int extPos = url.lastIndexOf(".");
			if (-1 == extPos || namePos > extPos) {
				name = url.substring(namePos+1);
				ext = "";
			} else {
				name = url.substring(namePos, extPos);	// "/name"
				ext = url.substring(extPos);			// ".ext"
				
				//확장자 가져오기
				if(ext.length() > 4){
//					ext = ext.substring(0,ext.indexOf('&'));
					ext = ext.substring(0,4);		//.jpg, .png
				}
			}
			//미디어스캔 등록시 URL로 전달하기 때문에 특수문자가 있는 파일명은 바꾸어준다
			if(0 < name.indexOf('%') || 0 < name.indexOf('&')){
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
				name = '/'+formatter.format(Calendar.getInstance().getTime());
			}
			
			Log.d("TEST","name : "+name);
			Log.d("TEST","ext : "+ext);
			
			try {
				int suffix = 0;
				String nameSuffix = name;
				File temp = new File(path + nameSuffix + ext);
				//동일한 파일이 존재하면 파일명 뒤에 숫자를 넣어줌
				while ( true == temp.exists() ) {
					suffix++;
					nameSuffix = String.format("%s-%d", name, suffix);
					temp = new File(path + nameSuffix + ext);
				}
				
				temp.createNewFile();
				pathFile = temp.getPath();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Log.d("pathFile","ext : "+pathFile.toString());
		return pathFile;
	}
	
	private static void outputDebugString(HttpURLConnection connected) {
		Log.d("csm", "-------------");
		Log.d("csm", "mConn.getURL() :" + connected.getURL());
		Log.d("csm", "getRequestMethod() :" + connected.getRequestMethod());
		Log.d("csm", "getContentType() :" + connected.getContentType());
		try {
			Log.d("csm", "getResponseCode() :" + connected.getResponseCode());
			Log.d("csm", "getResponseMessage() :" + connected.getResponseMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Map< String, List<String> > m = connected.getHeaderFields();
		String key, val, log;
		for( int i = 0 ; i < m.size() ; i++ ) {
			key = connected.getHeaderFieldKey(i);
				if (null != key) {
				log = "\"" + key + "\"" + " = ";
				List<String> l = (List<String>)m.get(key);
				if (null != l) {
				Iterator<String> it = l.iterator();
					while( it.hasNext() ) {
						val = (String)it.next();
						Log.d("csm", log + "\"" + val + "\", ");
					}
				}
			}
		}
		Log.d("csm", "-------------");		
	}
	
	public static int download(String url, String path, OnProgressListener listener) {
		
		int result = PROGRESS_FAIL;	
		
		URL mUrl = null;
		HttpURLConnection mConn = null;
		BufferedInputStream bis = null;
		RandomAccessFile file = null;
		
		try {
			file = new RandomAccessFile(path, "rw");
			long curSize = file.length();
			file.seek(curSize);
			
			mUrl = new URL(url);
			mConn = (HttpURLConnection)mUrl.openConnection();			
			mConn.setConnectTimeout(CONNECT_TIMEOUT);
			mConn.setReadTimeout(READ_TIMEOUT);
			mConn.setRequestProperty( "range", String.format( "bytes=%s-", Long.toString(curSize) ) );
		
			mConn.connect();
			outputDebugString(mConn);
			
			int resCode = mConn.getResponseCode();
			switch (resCode) {
			case HttpURLConnection.HTTP_OK:
				// we set (range : bytes=1234-) but HTTP_PARTIAL not support
				curSize = 0;
				file.seek(curSize);
				break;
			case HttpURLConnection.HTTP_PARTIAL:	// can inherit download (accept-ranges = bytes)
				break;
			//case HttpURLConnection.HTTP_NOT_MODIFIED:	// etag, last-time
			//	break;
			default:
				return result;
				//break;
			}

			// url redirected (authentic or others)
			if ( false == mUrl.getHost().equals( mConn.getURL().getHost() ) ) {	
				return result;
			}
			
			long remainSize = mConn.getContentLength();
			if (-1 == remainSize) {
				return result;
			}
			
			long fullSize = curSize + remainSize;

			bis = new BufferedInputStream( mConn.getInputStream() );
			byte[] buf = new byte[SIZE_INPUT_BUFFER];
			int readed = 0;
			long progress = curSize;
			
			result = listener.onBegin();
			if ( PROGRESS_NORMAL == result) {				
				while (-1 != readed) {
					file.write(buf, 0, readed);
					readed = bis.read(buf);
					
					progress += readed;
					if (null != listener) {
						result = listener.onProgress( (int)((progress*100/fullSize)) );
						if (PROGRESS_NORMAL != result) {
							break;	// while (-1 != readed)
						}
					}
				}
			}

		} catch (MalformedURLException e2) {
			e2.printStackTrace();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {	
				if (null != bis) {
					bis.close();
				}
				if (null != file) {
					file.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (null != mConn) {
				mConn.disconnect();
			}
			
			listener.onEnd(result);
		}
		
		return result;
	}
}
