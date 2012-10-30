package org.our.android.ouracademy.p2p.action;

import java.io.IOException;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;
import org.our.android.ouracademy.p2p.JSONProtocol;
import org.our.android.ouracademy.p2p.P2PManager;

import android.util.Log;

public abstract class OurJSONAction implements OurP2PAction {
	private static final String TAG = "OurJSONAction";

	@Override
	public void excute(Socket socket, JSONObject data) {
		try {
			JSONProtocol.write(socket, getResponseJSONString(data));
		} catch (IOException e) {
			Log.e(TAG, "IO Exception");
		} catch (JSONException e) {
			Log.e(TAG, "JSON Exception");
		} finally {
			P2PManager.close(socket);
		}
	}
	
	public abstract String getResponseJSONString(JSONObject data) throws JSONException;
}
