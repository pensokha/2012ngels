package org.our.android.ouracademy.p2p.client;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.our.android.ouracademy.model.ExistingContentsResponse;
import org.our.android.ouracademy.model.OurContents;
import org.our.android.ouracademy.p2p.P2PClientJSON;
import org.our.android.ouracademy.p2p.action.GetExistingContents;

public class GetExistingContentsClient extends P2PClientJSON {
	private GetExistingContentsListener listener;

	public GetExistingContentsClient(String serverAddress, GetExistingContentsListener listener) {
		super(serverAddress);
		
		this.listener = listener;
	}

	@Override
	protected String getMethod() {
		return GetExistingContents.class.getName();
	}

	@Override
	protected void setBody(JSONObject request) throws JSONException {
	}

	@Override
	public void onInterrupted() {
	}

	@Override
	protected void onSuccess(JSONObject jsonResponse) throws IOException,
			JSONException {
		ExistingContentsResponse response = new ExistingContentsResponse();
		response.setFromJSONObject(jsonResponse);
		
		listener.onSuccess(response.getContents());
	}
	
	@Override
	protected void onFailure(JSONObject jsonResponse) {
		super.onFailure(jsonResponse);
		
		listener.onFailure();
	}

	public interface GetExistingContentsListener {
		public void onSuccess(ArrayList<OurContents> existingContents);
		public void onFailure();
	}
}
