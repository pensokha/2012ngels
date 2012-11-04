package org.our.android.ouracademy.asynctask;

import java.util.ArrayList;

public abstract class CallbackTask implements Runnable {
	private ArrayList<OurCallback> callbackList;
	
	public CallbackTask() {
		super();
		
		callbackList = new ArrayList<CallbackTask.OurCallback>();
	}

	@Override
	public void run() {
		proceed();
		
		for(OurCallback callback : callbackList){
			callback.callback();
		}
	}
	
	public abstract void onInterrupted();
	public abstract void proceed();
	
	public void addCallback(OurCallback callback){
		callbackList.add(callback);
	}
	
	public interface OurCallback{
		public void callback();
	}
}
