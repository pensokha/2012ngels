package org.our.android.ouracademy.manager;

import org.our.android.ouracademy.asynctask.CallbackTask;
import org.our.android.ouracademy.asynctask.CallbackTask.OurCallback;
import org.our.android.ouracademy.asynctask.GetMetaInfoFromFSI;
import org.our.android.ouracademy.model.OurContents;
import org.our.android.ouracademy.service.TeacherService;

import android.content.Context;
import android.content.Intent;

public class TeacherDataManager extends DataManager {
	private static TeacherDataManager instance = new TeacherDataManager();

	public static DataManager getInstance() {
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
	 * When : onPowerOn, ChangeMode, start application 1. get MetaInfo, Db
	 * update 2. sync file and db 3. start service, register wifi receiver, make
	 * wifi group
	 */
	@Override
	public void startService(Context ctx) {
		super.startService(ctx);

		getMetaInfo();
	}

	/*********
	 * When : onPowerOff, ChangeMode Stop Service 1. If StartTask is running,
	 * Stop 2. Download File From Youtube Stop 3. service stop, remove wifi
	 * group, unregister wifi receiver
	 */
	@Override
	public void stopService(Context context) {
		super.stopService(context);
	}

	@Override
	public void getMetaInfo() {
		CallbackTask syncAndContentNoti = new GetMetaInfoFromFSI(context);
		syncAndContentNoti.addCallback(callback);
		executeRunnable(syncAndContentNoti);
	}

	private OurCallback callback = new OurCallback() {
		@Override
		public void callback() {
			serviceName = context.startService(new Intent(context,
					TeacherService.class));
		}
	};

	@Override
	public void download(OurContents content) {
		// download from youtube
	}

	@Override
	public void cancleDownload(OurContents content) {
		// TODO Auto-generated method stub

	}
}
