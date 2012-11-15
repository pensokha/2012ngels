package org.our.android.ouracademy.constants;

import java.util.ArrayList;
import java.util.HashMap;

import org.our.android.ouracademy.R;
import org.our.android.ouracademy.model.OurCategory;

import android.text.TextUtils;

/**
 * 
 * @author JiHoon, Moon
 * 
 */
public class MatchCategoryColor {
	static HashMap<String, Integer> categoryColorMap = new HashMap<String, Integer>();
	static int[] bookSelectorIds = { 
			R.drawable.btn_main_book_selector_01,
			R.drawable.btn_main_book_selector_02,
			R.drawable.btn_main_book_selector_03,
			R.drawable.btn_main_book_selector_04,
			R.drawable.btn_main_book_selector_05,
			R.drawable.btn_main_book_selector_06,
			R.drawable.btn_main_book_selector_07,
			R.drawable.btn_main_book_selector_08,
			R.drawable.btn_main_book_selector_09,
			R.drawable.btn_main_book_selector_10 };
	
	public static synchronized void matchColor(ArrayList<OurCategory> categoryList) {
		if (categoryList == null || categoryList.isEmpty()) {
			return;
		}
		categoryColorMap.clear();
		int k = 0;
		for (int i = 0; i < categoryList.size(); i++) {
			if (k > bookSelectorIds.length) {
				k = 0;
			}
			categoryColorMap.put(categoryList.get(i).getCategoryId(), bookSelectorIds[k]);
			k++;
		}
	}
	
	public static int getCategoryMatchColorId(String categoryId) {
		if (TextUtils.isEmpty(categoryId)) {
			return bookSelectorIds[0];
		}
		if (categoryColorMap == null) {
			return bookSelectorIds[0];
		}
		return categoryColorMap.containsKey(categoryId) ? categoryColorMap.get(categoryId) : bookSelectorIds[0];
	}

}
