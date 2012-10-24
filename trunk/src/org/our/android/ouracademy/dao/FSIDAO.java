package org.our.android.ouracademy.dao;

import org.json.JSONException;
import org.json.JSONObject;
import org.our.android.ouracademy.model.OurMetaInfo;

/******
 * 
 * @author hyeongseokLim
 *
 */
public class FSIDAO {
	private String testJsonString = "{ ‘ResponseCode’ : 0, ‘Version’ : 1, "
			+ "‘Categories’ : [{ ‘ID’ : ‘Math’, ‘Depth’ : 0, ‘TitleEnglish’ : ‘Math’, ‘TitleKhmer’ : ‘’, "
			+ "‘DescriptionEnglish’ : ‘Math Math’, ‘DescriptionKhmer’ : ‘’, "
			+ "‘ParentCategoryID’ : ‘’}, { ‘ID’ : ‘TrigonametricFunction’, "
			+ "‘Depth’ : 1, ‘TitleEnglish’ : ‘TrigonametricFunction’, ‘TitleKhmer’ : ‘’, "
			+ "‘DescriptionEnglish’ : ‘TrigonametricFunction TrigonametricFunction’, ‘DescriptionKhmer’ : ‘’, "
			+ "‘ParentCategoryID’ : ‘Math’}, { ‘ID’ : ‘junior, ‘Depth’ : 0, ‘TitleEnglish’ : ‘junior, ‘TitleKhmer’ : ‘’, "
			+ "‘DescriptionEnglish’ : ‘junior junior, ‘DescriptionKhmer’ : ‘’, ‘ParentCategoryID’ : ‘’}],"
			+ "‘Contents’ : [{‘ID’ : ‘math-htrea-0032’, ‘Subject’ : ‘trigonametricfunction basic’, "
			+ "‘SubTitleFileUrl’ : ‘http://fsi.com/contest/image/math/tri_subtitle’, ‘ContentUrl’ : ‘http://fsi.com/contest/math/tir.avi’, "
			+ "‘Size’ : 102402, ‘CategoryIDList’ : [‘math’, ‘TrigonametricFunction’, ‘junior’]},{‘ID’ : ‘math-htrea-0132’, "
			+ "‘Subject’ : ‘trigonametricfunction normal’, ‘SubTitleFileUrl’ : ‘http://fsi.com/contest/image/math/tri_subtitle2’, "
			+ "ContentUrl’ : ‘http://fsi.com/contest/math/tir2.avi’, ‘Size’ : 20484, ‘CategoryIDList’ : [‘math’]}]}";

	public OurMetaInfo getMetaInfo() {
		OurMetaInfo metaInfo = new OurMetaInfo();

		try {
			JSONObject jsonObject = new JSONObject(testJsonString);
			metaInfo.setFromJSONObject(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return metaInfo;
	}
}
