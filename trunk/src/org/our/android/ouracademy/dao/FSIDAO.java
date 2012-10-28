package org.our.android.ouracademy.dao;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.our.android.ouracademy.model.OurCategory;
import org.our.android.ouracademy.model.OurContent;
import org.our.android.ouracademy.model.OurMetaInfo;

import android.util.Log;

/******
 * 
 * @author hyeongseokLim
 * 
 */
public class FSIDAO {
	
	public OurMetaInfo getMetaInfo() throws DAOException {
		OurMetaInfo metaInfo = new OurMetaInfo();
		OurMetaInfo testData = new OurMetaInfo();
		//TEST 로 MetaInfo 객체에 데이터를 넣고 json변환후 역변환 한다.
		ArrayList<OurCategory> categories = new ArrayList<OurCategory>();
		for(int i = 0; i < 10; i++){
			OurCategory category = new OurCategory();
			category.setCategoryId("CategoryId"+i);
			category.setCategoryDepth(i%2);
			category.setCategoryTitleEng("TitleEng"+i);
			category.setCategoryTitleKmr("TitleKmr"+i);
			category.setCategoryDescriptionEng("DescEng"+i);
			category.setCategoryDescriptionKmr("DescKmr"+i);
			if(category.getCategoryDepth() == 0){
				category.setCategoryParent("");
			}else{
				category.setCategoryParent(categories.get(i-1).getCategoryId());
			}
			categories.add(category);
		}
		ArrayList<OurContent> contents = new ArrayList<OurContent>();
		for(int i = 0; i < 20; i++){
			OurContent content = new OurContent();
			content.setId("ContentId"+i);
			content.setSubjectEng("SubjectEng"+i);
			content.setSubjectKmr("SubjectKmr"+i);
			content.setSubtitleUrl("SubTitleUrl"+i);
			content.setContentUrl("ContentUrl"+i);
			content.setSize(1024*1024);
			
			ArrayList<String> categoryIdList = new ArrayList<String>();
			for(int j = 0; j < 3; j++){
				categoryIdList.add(categories.get(j).getCategoryId());
			}
			content.setCategoryIdList(categoryIdList);
			contents.add(content);
		}
		
		testData.setVersion(1);
		testData.setErrorCode(OurMetaInfo.RES_CODE_SUCCESS);
		testData.setCategories(categories);
		testData.setContents(contents);
		
		try {
			JSONObject jsonObject = new JSONObject(testData.getJSONObject().toString());
			metaInfo.setFromJSONObject(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new DAOException("Fail Getting MetaInfo from FSI");
		}
		return metaInfo;
	}
}
