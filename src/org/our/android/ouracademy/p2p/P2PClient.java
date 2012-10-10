package org.our.android.ouracademy.p2p;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.our.android.ouracademy.OurDefine;
import org.our.android.ouracademy.manager.TestFileDbCreate;
import org.our.android.ouracademy.model.OurContent;
import org.our.android.ouracademy.p2p.action.GetNewFileList;

import android.util.Log;

public class P2PClient implements Runnable {
	private static final String TAG = "P2PClient";
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
			StringBuilder json =  new StringBuilder();
			json.append("{ 'header' : { 'method' : '");
			json.append(GetNewFileList.methodName);
			json.append("' } }");
			
			try {
				JSONProtocol.write(socket, json.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			ArrayList<OurContent> contents = new ArrayList<OurContent>();
			try {
				String jsonString = JSONProtocol.read(socket);
				JSONObject jsonContent = new JSONObject(jsonString);
				JSONArray jsonList = jsonContent.getJSONArray("contents");
				
				for(int i = 0; i < jsonList.length(); i++){
					OurContent content = new OurContent();
					content.setFromJSONObject(jsonList.getJSONObject(i));
					contents.add(content);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			TestFileDbCreate.setAllContents(contents);
		}
	}

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
