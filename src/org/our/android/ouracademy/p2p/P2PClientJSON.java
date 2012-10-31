package org.our.android.ouracademy.p2p;

import java.io.IOException;
import java.net.Socket;

import org.json.JSONException;

public abstract class P2PClientJSON extends P2PClient {


	public P2PClientJSON(String serverAddress) {
		super(serverAddress);
	}
	
	@Override
	protected void responseProcess(Socket socket) throws IOException {
		try {
			String jsonResponse = JSONProtocol.read(socket);
			
			parseResponse(jsonResponse);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

	protected abstract void parseResponse(String jsonResponse) throws IOException, JSONException;
}