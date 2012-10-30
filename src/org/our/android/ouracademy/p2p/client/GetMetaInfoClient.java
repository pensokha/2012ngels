package org.our.android.ouracademy.p2p.client;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.our.android.ouracademy.p2p.P2PClient;

import android.content.Context;

public class GetMetaInfoClient extends P2PClient {
	private Context context;

	public GetMetaInfoClient(String serverAddress, Context context) {
		super(serverAddress);
		
		this.context = context;
	}

	@Override
	protected String getMethod() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setBody(JSONObject request) throws JSONException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void parseResponse(String jsonResponse) throws IOException,
			JSONException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void responseProcess() {
		// TODO Auto-generated method stub

	}
}
