package org.our.android.ouracademy.p2p.client;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.our.android.ouracademy.OurPreferenceManager;
import org.our.android.ouracademy.asynctask.GetMetaInfoFromMetaFile;
import org.our.android.ouracademy.asynctask.SyncAndReloadNoti;
import org.our.android.ouracademy.constants.CommonConstants;
import org.our.android.ouracademy.dao.DAOException;
import org.our.android.ouracademy.manager.FileManager;
import org.our.android.ouracademy.model.OurMetaInfo;
import org.our.android.ouracademy.p2p.P2PClientJSON;
import org.our.android.ouracademy.p2p.P2PManager;
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
	public void onInterrupted() {
	}

	@Override
	protected void onSuccess(JSONObject jsonResponse) throws IOException,
			JSONException {
		OurMetaInfo metaInfo = new OurMetaInfo();
		metaInfo.setFromJSONObject(jsonResponse);

		try {
			GetMetaInfoFromMetaFile.getMetaInfoProcesses(metaInfo, context);

			FileWriter wr = null;
			File metaFile = FileManager.getFile(CommonConstants.META_FILE_NAME);
			if(metaFile.exists() == false)
				if(metaFile.createNewFile() == false)
					return ;
			try {
				wr = new FileWriter(metaFile.getAbsolutePath());
				wr.write(metaInfo.getJSONObject().toString());
				wr.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} finally {
				P2PManager.close(wr);
			}

			new SyncAndReloadNoti(context).run();
		} catch (DAOException e) {
			e.printStackTrace();
		}
	}
}
