package org.our.android.ouracademy.p2p;

import java.io.IOException;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;
import org.our.android.ouracademy.model.OurResponse;

public abstract class P2PClientJSON extends P2PClient {


	public P2PClientJSON(String serverAddress) {
		super(serverAddress);
	}
	
	@Override
	protected void responseProcess(Socket socket) throws IOException {
		OurResponse response = new OurResponse();
		try {
			String jsonResponse = JSONProtocol.read(socket);
			
			JSONObject jsonObject = new JSONObject(jsonResponse);
			
			response.setFromJSONObject(jsonObject);
			
			if(response.getResponseCode() == OurResponse.RES_CODE_SUCCESS){
				onSuccess(jsonObject);
			}else{
				onFailure(jsonObject);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

	protected abstract void onSuccess(JSONObject jsonResponse) throws IOException, JSONException;
	protected void onFailure(JSONObject jsonResponse){}
}