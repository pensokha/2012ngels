package org.our.android.ouracademy.constants;

import java.util.HashMap;

import org.our.android.ouracademy.R;

import android.text.TextUtils;

/**
 * 
 * @author JiHoon, Moon
 * 
 */
public class MatchCategoryIcon {
	private static MatchCategoryIcon instance;
	static HashMap<String, Integer> categoryIconMap = new HashMap<String, Integer>();
	
	static int elseIcon = R.drawable.subject_icon;
	
	static final int[] categoryIconIds = {
		R.drawable.subject_icon_math,
		R.drawable.subject_icon_english,
		R.drawable.subject_icon_art,
		R.drawable.subject_icon_history,
		R.drawable.subject_icon_khmer,
		R.drawable.subject_icon_music,
		R.drawable.subject_icon_science,
		R.drawable.subject_icon_writing,
	};
	
	static final public String[] categoryIds = {
		"001",			//math
		"002",			//english
		"003",			//art
		"004",			//history
		"005",			//khmer
		"006",			//music
		"007",			//sceince
		"008",			//write
	};
	
	private MatchCategoryIcon() {
		for (int i = 0; i < categoryIconIds.length; i++) {
			categoryIconMap.put(categoryIds[i], categoryIconIds[i]);
		}
	}
	
	public synchronized static int getMatchIcon(String categoryId) {
		if (TextUtils.isEmpty(categoryId)) {
			return elseIcon;
		}
		if (instance == null) {
			instance = new MatchCategoryIcon();
		}
		if (categoryIconMap.containsKey(categoryId)) {
			return categoryIconMap.get(categoryId);
		}
		return elseIcon;
	}
	
}
