/*
 * NameValuePair.java $version 2010. 12. 03
 *
 * Copyright 2010 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.our.android.ouracademy.util;

/** 
 * String Pair object 
 * 
 * @author DK SHIN
 *
 */
public class NameValuePair {
	public NameValuePair(String name, String value) {
		mName = name;
		mValue = value;
	}

	public static String[] getNames(NameValuePair[] nameValues) {
		String[] names = new String[nameValues.length];
		int i;
		for (i = 0; i < nameValues.length; i++) {
			names[i] = nameValues[i].mName;
		}
		return (names);
	}

	public static String[] getValues(NameValuePair[] nameValues) {
		String[] values = new String[nameValues.length];
		int i;
		for (i = 0; i < nameValues.length; i++) {
			values[i] = nameValues[i].mValue;
		}
		return (values);
	}

	public String mName;
	public String mValue;
}
