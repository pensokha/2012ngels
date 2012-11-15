package org.our.android.ouracademy;

import org.our.android.ouracademy.constants.CommonConstants;
import org.our.android.ouracademy.constants.MatchCategoryIcon;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 
 * @author JiHoon, Moon on NTS
 * 
 */
public class OurPreferenceManager {
	private static OurPreferenceManager mSelfInstance = new OurPreferenceManager();
	private SharedPreferences mPreference = null;

	public final static String PREFERENCE_KEY = "our";
	private static final String MODE = "MODE";
	private static final String VERSION = "VERSION";
	private static final String LAST_SELECETED_CATEGORY = "LAST_SELECETED_CATEGORY";

	/***
	 * 
	 * @author hyeongseokLim
	 * 
	 */
	private static enum Mode {
		TEACHER, STUDENT
	};

	private OurPreferenceManager() {
	}

	public static OurPreferenceManager getInstance() {
		return mSelfInstance;
	}

	public void initPreferenceData(ContextWrapper ctxWrapper) {
		mPreference = ctxWrapper.getSharedPreferences(PREFERENCE_KEY,
				Activity.MODE_PRIVATE);
	}

	public int getIntValue(String prefKey) {
		if (mPreference != null) {
			return mPreference.getInt(prefKey, 0);
		}
		return 0;
	}

	public int getIntValue(String prefKey, int value) {
		if (mPreference != null) {
			return mPreference.getInt(prefKey, value);
		}
		return value;
	}

	public boolean setIntValue(String prefKey, int value) {
		if (mPreference != null) {
			SharedPreferences.Editor editor = mPreference.edit();
			editor.putInt(prefKey, value);
			return editor.commit();
		}
		return false;
	}

	public long getLongValue(String prefKey) {
		if (mPreference != null) {
			return mPreference.getLong(prefKey, 0);
		}
		return 0;
	}

	public boolean setLongValue(String prefKey, long value) {
		if (mPreference != null) {
			SharedPreferences.Editor editor = mPreference.edit();
			editor.putLong(prefKey, value);
			return editor.commit();
		}
		return false;
	}

	/**
	 * Sets the String value in given key.
	 */
	public boolean setStringValue(String key, String value) {
		if (mPreference != null) {
			Editor editor = mPreference.edit();
			editor.putString(key, value);
			return editor.commit();
		}
		return false;
	}

	/**
	 * Sets the boolean value in given key.
	 */
	public boolean setBooleanValue(String key, Boolean value) {
		if (mPreference != null) {
			Editor editor = mPreference.edit();
			editor.putBoolean(key, value);
			return editor.commit();
		}
		return false;
	}

	/**
	 * Gets the boolean value in given key.
	 */
	public boolean getBooleanValue(String key, boolean defValue) {
		if (mPreference != null) {
			return mPreference.getBoolean(key, defValue);
		}
		return defValue;
	}

	/**
	 * Gets the String value in given key.
	 */
	public String getStringValue(String key, String defValue) {
		if (mPreference != null) {
			return mPreference.getString(key, defValue);
		}
		return defValue;
	}

	/**
	 * Check the value availability of the data for the given key.
	 */
	public boolean containsValue(String key) {
		if (mPreference != null) {
			return mPreference.contains(key);
		}
		return false;
	}

	/**
	 * 키와 값을 지운다
	 */
	public boolean setRemoveValue(String key) {
		if (mPreference != null) {
			Editor editor = mPreference.edit();
			editor.remove(key);
			return editor.commit();
		}
		return false;
	}

	public boolean isTeacher() {
		int ordinal = getIntValue(MODE, Mode.STUDENT.ordinal());
		Mode mode = Mode.values()[ordinal];

		return (mode == Mode.TEACHER ? true : false);
	}

	public boolean isStudent() {
		int ordinal = getIntValue(MODE, Mode.STUDENT.ordinal());
		Mode mode = Mode.values()[ordinal];

		return (mode == Mode.STUDENT ? true : false);
	}

	public boolean isSettingMode() {
		int empty = -1;
		int ordinal = getIntValue(MODE, empty);

		return (ordinal != empty ? true : false);
	}

	public boolean setTeacherMode() {
		return setIntValue(MODE, Mode.TEACHER.ordinal());
	}

	public boolean setStudentMode() {
		return setIntValue(MODE, Mode.STUDENT.ordinal());
	}

	public boolean setVersion(int version) {
		return setIntValue(VERSION, version);
	}

	public int getVersion() {
		return getIntValue(VERSION);
	}
	
	public boolean setSelecetedCategory(String categoryId) {
		return setStringValue(LAST_SELECETED_CATEGORY, categoryId);
	}
	
	public String getSelecetedCategory() {
		return getStringValue(LAST_SELECETED_CATEGORY, CommonConstants.DEFAULT_LOAD_CATEGORY_ID);
	}
}
