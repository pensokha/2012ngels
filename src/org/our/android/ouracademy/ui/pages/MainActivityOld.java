package org.our.android.ouracademy.ui.pages;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;
import org.our.android.ouracademy.OurDefine;
import org.our.android.ouracademy.OurPreferenceManager;
import org.our.android.ouracademy.R;
import org.our.android.ouracademy.manager.FileManager;
import org.our.android.ouracademy.manager.OurDownloadManager;
import org.our.android.ouracademy.p2p.JSONProtocol;
import org.our.android.ouracademy.p2p.P2PManager;
import org.our.android.ouracademy.p2p.P2PService;
import org.our.android.ouracademy.p2p.action.DownloadFile;
import org.our.android.ouracademy.ui.adapter.ContentsListAdapter;
import org.our.android.ouracademy.ui.adapter.ContentsListAdapterOld;
import org.our.android.ouracademy.util.DbManager;
import org.our.android.ouracademy.wifidirect.WifiDirectWrapper;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class MainActivityOld extends BaseActivity {
	private ViewGroup menuLayout;
    private ViewGroup detailRootView;
    private ViewGroup detailLayout;
    
    private ViewGroup hideMenuBtn;
    
    private ImageView decoyImage;
    
    private int moveStart;
	private int moveEnd;
	
	private int aniDuration = 200;
    
	public static final String OUR_CONTENT_DATA_CHANGED = "org.our.android.ouracademy.ui.main.OUR_CONTENT_DATA_CHANGED";

	private static final String TAG = "Main";
	private OurPreferenceManager pref;
	private WifiDirectWrapper wifidirectWrapper;
	private ComponentName serviceName;
	private final IntentFilter intentFilter = new IntentFilter();
	private ContentsDataChangedReciever reciever;

	ListView contentsListview;
	ContentsListAdapterOld contentsListAdapter;
	
	enum TouchStatus {
		START_DRAGGING, DRAGGING, STOP_DRAGGING
	}
	TouchStatus touchStatus = TouchStatus.STOP_DRAGGING;
	
	enum MenuStatus {
		INVISIBLE_MENU, MOVING_MENU, VISIBLE_MENU
	}
	MenuStatus menuStatus = MenuStatus.INVISIBLE_MENU;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUI();

		pref = OurPreferenceManager.getInstance();
		wifidirectWrapper = WifiDirectWrapper.getInstance();
		reciever = new ContentsDataChangedReciever();

		wifidirectWrapper.init(this);
		intentFilter.addAction(OUR_CONTENT_DATA_CHANGED);
	}

	private Cursor readContents() {
		StringBuilder query = new StringBuilder();
		query.append("select t1._id, t1.ContentId AS ContentId, t2.FileId AS FileId, t2.FilePath AS FilePath ");
		query.append("from CONTENTS_TBL t1 ");
		query.append("left join DOWNLOAD_TBL t2 on t1.ContentId = t2.FileId");
		// load File, Download table data
		Cursor cursor = DbManager.getInstance().getDB()
				.rawQuery(query.toString(), null);
		return cursor;
	}

	private void initUI() {
		setContentView(R.layout.activity_main);
		
		menuLayout		= (ViewGroup)findViewById(R.id.layout_menu);				//하단화면 View
        detailRootView	= (ViewGroup)findViewById(R.id.layout_root_detail);			//상단화면 최상단 View
        detailLayout	= (ViewGroup)findViewById(R.id.layout_detail);				//상단화면 View
        
        hideMenuBtn = (ViewGroup)(ViewGroup)findViewById(R.id.hide_menu_btn);
        hideMenuBtn.setClickable(false);
        
        ViewGroup dragLayout = (ViewGroup)findViewById(R.id.drag_layout);	//상단화면의 메뉴를 Drag할 수 있는 여역
        dragLayout.setOnTouchListener(dargTouchListener);					//onTouchListener 지정
        
        //상단화면 CashBitmap이 저장된 iamgeView
        decoyImage = new ImageView(this);
        
        
		
//		FrameLayout layout = (FrameLayout) findViewById(R.id.layout_list);

		contentsListview = new ListView(this);
		contentsListAdapter = new ContentsListAdapterOld(this, readContents(),
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		contentsListview.setAdapter(contentsListAdapter);

		contentsListview.setOnItemClickListener(itemClickListener);

//		layout.addView(contentsListview);
		
	}

	// tmp method
	private void startP2pService() {
		if (pref.isTeacher() && serviceName == null) {
			serviceName = startService(new Intent(this, P2PService.class));
		}
	}

	private void stopP2pService() {
		if (serviceName != null) {
			Intent intent = new Intent();
			intent.setComponent(serviceName);

			if (stopService(intent)) {
				Log.d(TAG, "Stop P2p Service");
			}
			serviceName = null;
		}
	}

//	// tmp method
//	private void alertMode() {
//		new AlertDialog.Builder(this).setTitle("Mode Setting")
//				.setPositiveButton("teacher", modeClickListenr)
//				.setNegativeButton("student", modeClickListenr).show();
//	}

//	// tmp method
//	private DialogInterface.OnClickListener modeClickListenr = new DialogInterface.OnClickListener() {
//		@Override
//		public void onClick(DialogInterface dialog, int which) {
//			if (which == -1) {
//				pref.setTeacherMode();
//			} else {
//				pref.setStudentMode();
//			}
//			wifidirectWrapper.register();
//			startP2pService();
//		}
//	};
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//
//		if (pref.isSettingMode() == false) {
//			alertMode();
//		} else {
//			wifidirectWrapper.register();
//			startP2pService();
//		}
//
//		registerReceiver(reciever, intentFilter);
//	}
//
//	@Override
//	protected void onPause() {
//		super.onPause();
//
//		wifidirectWrapper.unregister();
//		unregisterReceiver(reciever);
//	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		stopP2pService();
	}
	
	private void setDetailLayoutImageCache(LayoutParams params) {
		//detailLayout의 DrawingCache를 가져와 imageView에 넣은다음 datailRootView에 add해준다.
    	detailLayout.destroyDrawingCache();
    	detailLayout.buildDrawingCache();			//detailLayout의 최신 Drawing Cache를 업데이트 해준다.
    	decoyImage.setImageBitmap(detailLayout.getDrawingCache());
    	decoyImage.setLayoutParams(params);
    	
		detailRootView.addView(decoyImage);
	}
	
	public void onClickMenu(View view) {
		AbsoluteLayout.LayoutParams params = (AbsoluteLayout.LayoutParams) detailLayout.getLayoutParams();
		setDetailLayoutImageCache(params);
		
    	if (menuStatus == MenuStatus.VISIBLE_MENU) {
    		hideManuAnimation(menuLayout.getWidth());
    	} else if (menuStatus == MenuStatus.INVISIBLE_MENU) {
    		openMenuAnimation(menuLayout.getWidth());
    	}
    }
	
	public void openMenuAnimation(final int aniWidth) {
		if (menuStatus == MenuStatus.MOVING_MENU) { return; }
		
	    final AnimationSet set = new AnimationSet(true);
		set.setInterpolator(getBaseContext(), android.R.anim.decelerate_interpolator);
		Animation ani = new TranslateAnimation(0.0f, aniWidth, 0.0f, 0.0f);
		ani.setDuration(aniDuration);
		set.addAnimation(ani);
		decoyImage.startAnimation(set);
		
		set.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				menuStatus = MenuStatus.MOVING_MENU;
				detailLayout.setVisibility(View.INVISIBLE);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				menuStatus = MenuStatus.VISIBLE_MENU;
				
				detailLayout.setVisibility(View.VISIBLE);
				AbsoluteLayout.LayoutParams params;
				params = (AbsoluteLayout.LayoutParams) detailLayout.getLayoutParams();
				params.x = (int) (menuLayout.getWidth());
				detailLayout.setLayoutParams(params);
				
				detailRootView.removeView(decoyImage);
				
				hideMenuBtn.setClickable(true);
			}
		});
    }
	
    public void hideManuAnimation(final int aniWidth) {
    	if (menuStatus == MenuStatus.MOVING_MENU) { return; }
    	
	    final AnimationSet set = new AnimationSet(true);
		set.setInterpolator(getBaseContext(), android.R.anim.decelerate_interpolator);
		Animation ani = new TranslateAnimation(0, -aniWidth, 0.0f, 0.0f);
		ani.setDuration(aniDuration);
		set.addAnimation(ani);
		decoyImage.startAnimation(set);
		
		set.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				menuStatus = MenuStatus.MOVING_MENU;
				detailLayout.setVisibility(View.INVISIBLE);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				menuStatus = MenuStatus.INVISIBLE_MENU;

				detailLayout.setVisibility(View.VISIBLE);
				AbsoluteLayout.LayoutParams params;
				params = (AbsoluteLayout.LayoutParams) detailLayout.getLayoutParams();
				params.x = 0;
				detailLayout.setLayoutParams(params);
				
				detailRootView.removeView(decoyImage);
				
				hideMenuBtn.setClickable(false);
			}
		});
    }
	
	OnTouchListener dargTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				touchStatus = TouchStatus.START_DRAGGING;
//				Log.d("TEST","Down X " + (int) event.getX());
//				Log.d("TEST","Down RawX " + (int) event.getRawX());
				
				AbsoluteLayout.LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, ((AbsoluteLayout.LayoutParams)detailLayout.getLayoutParams()).x, 0);
				setDetailLayoutImageCache(params);

				detailLayout.setVisibility(View.INVISIBLE);
				
				moveStart = (int) event.getRawX();
			} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
				touchStatus = TouchStatus.DRAGGING;
				moveEnd = (int) event.getRawX();
				//decoyImage 위치 이동
				AbsoluteLayout.LayoutParams params;
				params = (AbsoluteLayout.LayoutParams) decoyImage.getLayoutParams();
				int posX = params.x + moveEnd - moveStart;
				if (0 < posX && posX < menuLayout.getWidth()) {
					params.x = posX;
					decoyImage.setLayoutParams(params);
				}
				moveStart = moveEnd;
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				if (touchStatus == TouchStatus.DRAGGING) {						//decoyImage가 이동한 경우
					touchStatus = TouchStatus.STOP_DRAGGING;
					
					AbsoluteLayout.LayoutParams params;
					params = (AbsoluteLayout.LayoutParams) decoyImage.getLayoutParams();
					int posX = params.x;
					
					if (menuStatus == MenuStatus.VISIBLE_MENU) {				//하단화면이 보이는 경우
						if (posX == menuLayout.getWidth()) {
							hideManuAnimation(posX);
						} else if (posX > (menuLayout.getWidth() * 0.7)) {
							openMenuAnimation(menuLayout.getWidth() - posX);
						} else {
							hideManuAnimation(posX);						
						}
					} else if (menuStatus == MenuStatus.INVISIBLE_MENU) {		//상단화면이 보이는 경우
						if (posX < (menuLayout.getWidth() * 0.3)) {
							hideManuAnimation(posX);
						} else {
							openMenuAnimation(menuLayout.getWidth() - posX);
						}
					}
				} else if (touchStatus == TouchStatus.START_DRAGGING) {			//decoyImage가 이동하지 않은 경우
					touchStatus = TouchStatus.STOP_DRAGGING;
					detailLayout.setVisibility(View.VISIBLE);
					detailRootView.removeView(decoyImage);
					
					//Detail 화면에서 터치후 Drag 하지 않고 땐경우
					if (menuStatus == MenuStatus.VISIBLE_MENU) {
						onClickMenu(null);
					} else if (menuStatus == MenuStatus.INVISIBLE_MENU) {
						onClickMenu(null);
					}
				}
			}
			return true;
		}
	};
	
	private class DownloadFileAsyncTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			if (params == null || params[0] == null)
				return null;

			String fileId = params[0];
			String address = wifidirectWrapper.getOwnerIP();
			
			if(address == null)
				return null;

			Socket socket = connectToOwner(address);
			if (socket == null)
				return null;

			JSONObject json = new JSONObject();
			JSONObject header = new JSONObject();

			try {
				header.put("method", DownloadFile.methodName);
				json.put("header", header);
				json.put("id", fileId);
				
				JSONProtocol.write(socket, json.toString());

				File file = FileManager.getFile(fileId);
				file.createNewFile();

				InputStream inputStream = socket.getInputStream();
				FileManager.copyFile(inputStream, new FileOutputStream(file));

				OurDownloadManager downloadManager = new OurDownloadManager();
				downloadManager.addRow(fileId, file.getTotalSpace(),
						file.getTotalSpace(), fileId, FileManager.STRSAVEPATH
								+ fileId);

			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} finally {
				P2PManager.close(socket);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			contentsListAdapter.changeCursor(readContents());
			contentsListAdapter.notifyDataSetChanged();
			
			sendBroadcast(new Intent(MainActivityOld.OUR_CONTENT_DATA_CHANGED));
		}

		private Socket connectToOwner(String serverAddress) {
			Socket sock = null;
			int portIdx = 0;

			for (int i = 0; i < OurDefine.P2P_SERVER_PORT.length; i++) {
				try {
					sock = new Socket(serverAddress,
							OurDefine.P2P_SERVER_PORT[portIdx++]);

				} catch (IOException e) {
					continue;
				}
				return sock;
			}
			return sock;
		}
	}

	OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> list, View view, int position,
				long id) {
			Cursor cursor = contentsListAdapter.getCursor();
			cursor.moveToPosition(position);
			String downloadFilePath = cursor.getString(cursor
					.getColumnIndex("FilePath"));

			if (!TextUtils.isEmpty(downloadFilePath)) {
				Toast.makeText(getBaseContext(), downloadFilePath,
						Toast.LENGTH_SHORT).show();
			} else {
				DownloadFileAsyncTask downloadTask = new DownloadFileAsyncTask();
				downloadTask.execute(cursor.getString(cursor
						.getColumnIndex("ContentId")));
			}
		}
	};

	public class ContentsDataChangedReciever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			contentsListAdapter.changeCursor(readContents());
			contentsListAdapter.notifyDataSetChanged();
		}
	}
}
