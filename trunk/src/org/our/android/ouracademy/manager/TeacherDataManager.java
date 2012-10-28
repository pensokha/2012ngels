package org.our.android.ouracademy.manager;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.our.android.ouracademy.asynctask.GetMetaInfoFromFSI;
import org.our.android.ouracademy.asynctask.SyncFileAndDatabase;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class TeacherDataManager extends DataManager {
	private static TeacherDataManager instance = new TeacherDataManager();
	private ExecutorService initExcutor;
	private Future<?> initFuture;
	
	public static DataManager getInstance(){
		return instance;
	}
	
	@Override
	public void onPowerOn(Context context) {
		startService(context);
	}

	@Override
	public void onPowerOff(Context context) {
		stopService(context);
	}

	/********
	 * When : onPowerOn, ChangeMode
	 * Start Service 
	 * 1. enableWifi : FSI connect, get MetaInfo, Db update
	 * 2. sync file and db
	 * 3. start service, register wifi receiver, make wifi group
	 */
	@Override
	public void startService(Context context) {
		ConnectivityManager cManager;
		NetworkInfo wifi;
		cManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		
		initExcutor = Executors.newSingleThreadExecutor();
		Runnable asyncTask;
		if(wifi.isConnected()){
			asyncTask = new GetMetaInfoFromFSI(context);
		}else{
			asyncTask = new SyncFileAndDatabase(context);
		}
		initFuture = initExcutor.submit(asyncTask);
	}

	/*********
	 * When : onPowerOff, ChangeMode
	 * Stop Service
	 * 1. If StartTask is running, Stop
	 * 2. Download File From Youtube Stop
	 * 3. service stop, remove wifi group, unregister wifi receiver  
	 */
	@Override
	public void stopService(Context context) {
		if(initFuture.isDone() == false){
			initExcutor.shutdownNow();
		}
	}

}
