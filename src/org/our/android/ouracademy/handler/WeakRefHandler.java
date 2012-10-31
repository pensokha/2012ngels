package org.our.android.ouracademy.handler;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;

public class WeakRefHandler extends Handler {
	private final WeakReference<IOnHandlerMessage> mHandlerActivity;

	public WeakRefHandler(IOnHandlerMessage activity) {
		mHandlerActivity = new WeakReference<IOnHandlerMessage>(activity);
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		IOnHandlerMessage activity = (IOnHandlerMessage) mHandlerActivity.get();
		if (activity == null) {
			return;
		}
		activity.handleMessage(msg);
	}
}
