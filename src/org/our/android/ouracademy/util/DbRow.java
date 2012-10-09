package org.our.android.ouracademy.util;

import java.util.ArrayList;

import android.content.ContentValues;

public class DbRow {
	public DbRow(String[] columnNames) {
		mColumnNames = columnNames;
	}

	public DbRow() {
		mData = new ArrayList<NameValuePair>();
	}

	public int add(String name, String value) {
		NameValuePair pair = new NameValuePair();
		pair.name = name;
		pair.value = value;

		mData.add(pair);
		return mData.size();
	}
	public int add(String name, int value) {
		try {
			add(name,Integer.toString(value));
		} catch (Exception err) {
			err.printStackTrace();
			return (-1);
		}
		
		return mData.size();
	}
	public String getValue(int index) {
		NameValuePair pair = mData.get(index);
		if (pair == null) {
			return (null);
		}
		return (pair.value);
	}

	public String getValue(String id) {
		for (int i = 0; i < mData.size(); i++) {
			if (mData.get(i).name.equalsIgnoreCase(id)) {
				return mData.get(i).value;
			}
		}
		return null;
	}

	public void setValue(String id, String value) {
		for (int i = 0; i < mData.size(); i++) {
			if (mData.get(i).name.equalsIgnoreCase(id)) {
				mData.get(i).value = value;
				return;
			}
		}

	}

	public boolean replaceName(String name, String newName) {
		for (int i = 0; i < mData.size(); i++) {
			if (mData.get(i).name.equalsIgnoreCase(name)) {
				mData.get(i).name = newName;
				return (true);
			}
		}
		return (false);
	}
	
	public boolean remove(String name) {
		for (int i = 0; i < mData.size(); i++) {
			if (mData.get(i).name.equalsIgnoreCase(name)) {
				mData.remove(i);
				return (true);
			}
		}
		return (false);
	}
	public int getLength() {
		return mData.size();
	}

	String getColumnNames(String delimitor, String prefix, String suffix) {
		String names = "";
		int i;

		if (prefix != null) {
			names = prefix;
		}
		for (i = 0; i < mData.size() - 1; i++) {
			names += mData.get(i).name;
			names += delimitor;
		}
		names += mData.get(i).name;

		if (suffix != null) {
			names += suffix;
		}
		return (names);
	}

	public ContentValues getConentValues() {
		ContentValues values = new ContentValues();
		NameValuePair pair = null;
		int i;
		for (i = 0; i < mData.size(); i++) {
			pair = mData.get(i);
			values.put(pair.name, pair.value);
		}
		return (values);
	}


	public class NameValuePair {
		public String name;
		public String value;

		public NameValuePair() {

		}
	}

	String[] mColumnNames = null;
	protected ArrayList<NameValuePair> mData = null;

}
