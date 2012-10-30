package org.our.android.ouracademy.p2p.client;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.our.android.ouracademy.OurPreferenceManager;
import org.our.android.ouracademy.asynctask.GetMetaInfoFromFSI;
import org.our.android.ouracademy.dao.DAOException;
import org.our.android.ouracademy.model.OurMetaInfo;
import org.our.android.ouracademy.p2p.P2PClient;
import org.our.android.ouracademy.p2p.action.GetMetaInfo;
import org.our.android.ouracademy.ui.pages.MainActivity.OurDataChangeReceiver;

import android.content.Context;
import android.content.Intent;

public class GetMetaInfoClient extends P2PClient {
	private Context context;
	private OurMetaInfo metaInfo;

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
		metaInfo.setFromJSONObject(jsonObject);
	}

	@Override
	protected void responseProcess() {
		try {
			GetMetaInfoFromFSI.getMetaInfoProcesses(metaInfo);

			if (metaInfo.getResponseCode() == OurMetaInfo.RES_CODE_SUCCESS) {
				Intent intent = new Intent(
						OurDataChangeReceiver.OUR_DATA_CHANGED);
				intent.putExtra(OurDataChangeReceiver.ACTION,
						OurDataChangeReceiver.ACTION_RELOAD);
				context.sendBroadcast(intent);
			}
		} catch (DAOException e) {
			e.printStackTrace();
		}
	}
}
