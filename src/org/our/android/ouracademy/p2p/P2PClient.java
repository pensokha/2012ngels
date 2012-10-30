package org.our.android.ouracademy.p2p;

import java.io.IOException;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;
import org.our.android.ouracademy.OurDefine;

import android.util.Log;

public abstract class P2PClient implements Runnable {
	private static final String TAG = "P2PClient";
	private static final String HEADER_KEY = "header";
	private static final String METHOD_KEY = "method";
	
	private String serverAddress;
	
	public P2PClient(String serverAddress) {
		super();
		this.serverAddress = serverAddress;
	}

	@Override
	public void run() {
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
				
				String jsonResponse = JSONProtocol.read(socket);
				
				parseResponse(jsonResponse);
				
				responseProcess();
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
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
	protected abstract void parseResponse(String jsonResponse) throws IOException, JSONException;
	protected abstract void responseProcess();

	private Socket connectToOwner() {
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
