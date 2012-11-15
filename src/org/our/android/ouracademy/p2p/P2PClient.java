package org.our.android.ouracademy.p2p;

import java.io.IOException;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;
import org.our.android.ouracademy.asynctask.CallbackTask;
import org.our.android.ouracademy.constants.CommonConstants;

import android.util.Log;

public abstract class P2PClient extends CallbackTask {
	private static final String TAG = "P2PClient";
	private static final String HEADER_KEY = "header";
	private static final String METHOD_KEY = "method";
	
	private String serverAddress;
	
	public P2PClient(String serverAddress) {
		super();
		this.serverAddress = serverAddress;
	}
	
	@Override
	public void proceed() {
		
		Socket socket = connectToOwner();
		if (socket == null) {
			Log.d(TAG, "Fail Connect Owner");
		} else {
			JSONObject requestJson = new JSONObject();
			
			try {
				
				requestJson.put(HEADER_KEY, makeHeader()); 
				
				setBody(requestJson);
				
				JSONProtocol.write(socket, requestJson.toString());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			try {
				responseProcess(socket);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			P2PManager.close(socket);
		}
	}
	
	private JSONObject makeHeader() throws JSONException{
		JSONObject header = new JSONObject();
		header.put(METHOD_KEY, getMethod());
		return header;
	}
	
	protected abstract String getMethod();
	protected abstract void setBody(JSONObject request) throws JSONException;
	protected abstract void responseProcess(Socket socket) throws IOException;
	

	private Socket connectToOwner() {
		Socket sock = null;
		int portIdx = 0;

		for (int i = 0; i < CommonConstants.P2P_SERVER_PORT.length; i++) {
			try {
				sock = new Socket(serverAddress,
						CommonConstants.P2P_SERVER_PORT[portIdx++]);

			} catch (IOException e) {
				continue;
			}
			return sock;
		}
		return sock;
	}
}
