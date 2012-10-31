package org.our.android.ouracademy.p2p.client;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.our.android.ouracademy.OurPreferenceManager;
import org.our.android.ouracademy.asynctask.GetMetaInfoFromFSI;
import org.our.android.ouracademy.asynctask.SyncAndReloadNoti;
import org.our.android.ouracademy.dao.DAOException;
import org.our.android.ouracademy.model.OurMetaInfo;
import org.our.android.ouracademy.p2p.P2PClientJSON;
import org.our.android.ouracademy.p2p.action.GetMetaInfo;

import android.content.Context;

public class GetMetaInfoClient extends P2PClientJSON {
	private Context context;

	public GetMetaInfoClient(String serverAddress, Context context) {
		super(serverAddress);

		this.context = context;
	}

	@Override
	protected String getMethod() {
		return GetMetaInfo.class.getName();
	}

	@Override
	protected void setBody(JSONObject request) throws JSONException {
		request.put(OurMetaInfo.VERSION_JSON_KEY, OurPreferenceManager
				.getInstance().getVersion());
	}

	@Override
	protected void parseResponse(String jsonResponse) throws IOException,
			JSONException {
		JSONObject jsonObject = new JSONObject(jsonResponse);
		OurMetaInfo metaInfo = new OurMetaInfo();
		metaInfo.setFromJSONObject(jsonObject);

		try {
			GetMetaInfoFromFSI.getMetaInfoProcesses(metaInfo);

			if (metaInfo.getResponseCode() == OurMetaInfo.RES_CODE_SUCCESS) {
				new SyncAndReloadNoti(context).run();
			}
		} catch (DAOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onInterrupted() {
		
	}
}
