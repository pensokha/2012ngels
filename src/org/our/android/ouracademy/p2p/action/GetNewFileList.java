package org.our.android.ouracademy.p2p.action;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.our.android.ouracademy.manager.TestFileDbCreate;
import org.our.android.ouracademy.model.OurContent;
import org.our.android.ouracademy.p2p.JSONProtocol;

public class GetNewFileList implements OurP2PAction {
	public static final String methodName = "GetNewFileList"; 

	@Override
	public void excute(Socket socket, JSONObject data) {
		String json = getContentsJsonString();
		try {
			JSONProtocol.write(socket, json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getContentsJsonString(){
		ArrayList<OurContent> contents = TestFileDbCreate.getAllContents();
		
		JSONObject responseJson = new JSONObject();
		JSONArray jsonContents = new JSONArray();
		for(OurContent content : contents){
			try {
				jsonContents.put(content.getJSONObject());
			} catch (JSONException e) {
				e.printStackTrace();
			} 
		}
		try {
			responseJson.put("contents", jsonContents);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return responseJson.toString();
	}
}
