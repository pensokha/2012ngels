package org.our.android.ouracademy.p2p.action;

import org.json.JSONException;
import org.json.JSONObject;
import org.our.android.ouracademy.dao.ContentDAO;
import org.our.android.ouracademy.dao.DAOException;
import org.our.android.ouracademy.model.ExistingContentsResponse;
import org.our.android.ouracademy.model.OurResponse;

public class GetExistingContents extends OurJSONAction {

	@Override
	public String getResponseJSONString(JSONObject data) throws JSONException {
		ContentDAO contentDao = new ContentDAO();
		
		ExistingContentsResponse response = new ExistingContentsResponse();
		try{
			response.setContents(contentDao.getDownloadedContents());
			response.setResponseCode(OurResponse.RES_CODE_SUCCESS);
		} catch(DAOException e){
			e.printStackTrace();
			response.setResponseCode(OurResponse.RES_CODE_GET_DATA_FAIL);
		}
		return response.getJSONObject().toString();
	}
}
