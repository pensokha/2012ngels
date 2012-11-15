package org.our.android.ouracademy.dao;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.our.android.ouracademy.constants.CommonConstants;
import org.our.android.ouracademy.manager.FileManager;
import org.our.android.ouracademy.model.OurMetaInfo;
import org.our.android.ouracademy.p2p.P2PManager;

/******
 * 
 * @author hyeongseokLim
 * 
 */
public class FSIDAO {
	
	public OurMetaInfo getMetaInfo() throws DAOException {
		OurMetaInfo metaInfo = new OurMetaInfo();
		
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(FileManager.STRSAVEPATH + CommonConstants.META_FILE_NAME);
			br = new BufferedReader(fr);
			
			StringBuffer sb = new StringBuffer();
			String line = null;
			while((line = br.readLine()) != null){
				sb.append(line);
			}
			
			JSONObject jsonObject = new JSONObject(sb.toString());
			metaInfo.setFromJSONObject(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new DAOException("Fail Getting MetaInfo from File");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new DAOException("Fail Getting MetaInfo from File");
		} catch (IOException e) {
			e.printStackTrace();
			throw new DAOException("Fail Getting MetaInfo from File");
		} finally {
			P2PManager.close(br);
			P2PManager.close(fr);
		}
		
		return metaInfo;
	}
}
