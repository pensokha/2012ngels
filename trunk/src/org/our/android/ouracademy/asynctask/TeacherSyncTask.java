package org.our.android.ouracademy.asynctask;

import android.content.Context;
import android.os.Handler;

public class TeacherSyncTask extends SyncAndReloadNoti {
	private Handler handler;
	private TeacherSyncCallback callback;

	public TeacherSyncTask(Context context, Handler handler, TeacherSyncCallback callback) {
		super(context);
		
		this.handler = handler;
		this.callback = callback;
	}

	@Override
	public void run() {
		super.run();

		this.handler.post(new Runnable() {
			@Override
			public void run() {
				callback.endSync();
			}
		});
	}
	
	public interface TeacherSyncCallback{
		public void endSync();
	}
}
