package org.our.android.ouracademy.p2p;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.our.android.ouracademy.OurDefine;
import org.our.android.ouracademy.manager.TestFileDbCreate;
import org.our.android.ouracademy.model.OurContents;
import org.our.android.ouracademy.p2p.action.GetNewFileList;
import org.our.android.ouracademy.ui.pages.MainActivityOld;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class P2PClient implements Runnable {
	private static final String TAG = "P2PClient";
	private String serverAddress;
	private Context context;
	
	public P2PClient(Context context, String serverAddress) {
		super();
		this.serverAddress = serverAddress;
		this.context = context;
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
			
			ArrayList<OurContents> contents = new ArrayList<OurContents>();
			try {
				String jsonString = JSONProtocol.read(socket);
				JSONObject jsonContent = new JSONObject(jsonString);
				JSONArray jsonList = jsonContent.getJSONArray("contents");
				
				for(int i = 0; i < jsonList.length(); i++){
					OurContents content = new OurContents();
					content.setFromJSONObject(jsonList.getJSONObject(i));
					contents.add(content);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			TestFileDbCreate.setAllContents(contents);
			context.sendBroadcast(new Intent(MainActivityOld.OUR_CONTENT_DATA_CHANGED));
			P2PManager.close(socket);
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
