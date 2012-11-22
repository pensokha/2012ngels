package org.our.android.ouracademy.p2p.action;

import org.json.JSONException;
import org.json.JSONObject;
import org.our.android.ouracademy.OurPreferenceManager;
import org.our.android.ouracademy.dao.CategoryDAO;
import org.our.android.ouracademy.dao.ContentDAO;
import org.our.android.ouracademy.dao.DAOException;
import org.our.android.ouracademy.model.OurMetaInfo;
import org.our.android.ouracademy.model.OurResponse;

public class GetMetaInfo extends OurJSONAction {

	@Override
	public String getResponseJSONString(JSONObject data) throws JSONException {
		OurMetaInfo responseMetaInfo = new OurMetaInfo();
		int clientVersion = 0;
		try {
			clientVersion = data.getInt(OurMetaInfo.VERSION_JSON_KEY);
		} catch (JSONException e) {
			responseMetaInfo
					.setResponseCode(OurResponse.RES_CODE_INVALID_REQUEST);
		}

		int serverVersion = OurPreferenceManager.getInstance().getVersion();
		if (serverVersion > clientVersion) {
			responseMetaInfo.setVersion(serverVersion);
			responseMetaInfo.setResponseCode(OurResponse.RES_CODE_SUCCESS);
			CategoryDAO categoryDao = new CategoryDAO();
			ContentDAO contentDao = new ContentDAO();
			try {
				responseMetaInfo.setCategories(categoryDao.getCategories());
				responseMetaInfo.setContents(contentDao.getContents());
			} catch (DAOException e) {
				responseMetaInfo
						.setResponseCode(OurResponse.RES_CODE_GET_DATA_FAIL);
			}

		} else {
			responseMetaInfo
					.setResponseCode(OurResponse.RES_CODE_DONT_NEED_UPDATE);
		}

		return responseMetaInfo.getJSONObject().toString();
	}
}
