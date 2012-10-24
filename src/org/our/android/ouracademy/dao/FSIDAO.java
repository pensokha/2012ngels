package org.our.android.ouracademy.dao;

import org.json.JSONException;
import org.json.JSONObject;
import org.our.android.ouracademy.model.OurMetaInfo;

import android.util.Log;

/******
 * 
 * @author hyeongseokLim
 * 
 */
public class FSIDAO {

	private String testJsonString = "{ \"ResponseCode\": 0, "
			+ "\"Version\": 1, \"Categories\": "
			+ "[ { \"ID\": \"Math\", \"Depth\": 0, "
			+ "\"TitleEnglish\": \"Math\", \"TitleKhmer\": \"\", "
			+ "\"DescriptionEnglish\": \"Math Math\", "
			+ "\"DescriptionKhmer\": \"\", \"ParentCategoryID\": \"\" }, "
			+ "{ \"ID\": \"TrigonametricFunction\", \"Depth\": 1, "
			+ "\"TitleEnglish\": \"TrigonametricFunction\","
			+ " \"TitleKhmer\": \"\", \"DescriptionEnglish\": "
			+ "\"TrigonametricFunction TrigonametricFunction\", "
			+ "\"DescriptionKhmer\": \"\", \"ParentCategoryID\": "
			+ "\"Math\" }, { \"ID\": \"junior\", \"Depth\": 0, "
			+ "\"TitleEnglish\": \"junior\", \"TitleKhmer\": \"\", "
			+ "\"DescriptionEnglish\": \"juniorjunior\", "
			+ "\"DescriptionKhmer\": \"\", \"ParentCategoryID\": \"\" } ], "
			+ "\"Contents\": [ { \"ID\": \"math-htrea-0032\", "
			+ "\"SubjectEnglish\": \"trigonametricfunctionbasic\", "
			+ "\"SubjectKhmer\": \"\", "
			+ "\"SubTitleFileUrl\": \"http: //fsi.com/contest/image/math/tri_subtitle\", "
			+ "\"ContentUrl\": \"http: //fsi.com/contest/math/tir.avi\", "
			+ "\"Size\": 102402, \"CategoryIDList\": [ \"Math\", \"TrigonametricFunction\", \"junior\" ] }, "
			+ "{ \"ID\": \"math-htrea-0132\", "
			+ "\"SubjectEnglish\": \"trigonametricfunctionnormal\","
			+ "\"SubjectKhmer\": \"trigonametricfunctionnormal\","
			+ " \"SubTitleFileUrl\": \"http://fsi.com/contest/image/math/tri_subtitle2\", "
			+ "\"ContentUrl\": \"http: //fsi.com/contest/math/tir2.avi\", \"Size\": 20484, \"CategoryIDList\": [ \"Math\" ] } ] }";

	public OurMetaInfo getMetaInfo() {
		OurMetaInfo metaInfo = new OurMetaInfo();

		try {
			JSONObject jsonObject = new JSONObject(testJsonString);
			metaInfo.setFromJSONObject(jsonObject);
		} catch (JSONException e) {
			// Error 처리 필요함.
			e.printStackTrace();
		}
		return metaInfo;
	}
}
