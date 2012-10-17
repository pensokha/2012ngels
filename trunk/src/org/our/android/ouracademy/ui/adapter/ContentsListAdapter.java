package org.our.android.ouracademy.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;


public class ContentsListAdapter extends CursorAdapter {

	public ContentsListAdapter(Context context, Cursor c, int flag) {
		super(context, c, flag);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void bindView(View view, final Context context, Cursor cursor) {
		TextView textView = (TextView)view.findViewById(android.R.id.text1);
		final String contentFile = cursor.getString(cursor.getColumnIndex("ContentId"));
		final String downloadFile = cursor.getString(cursor.getColumnIndex("FileId"));
//		final String downloadFilePath = cursor.getString(cursor.getColumnIndex("FilePath"));
		if (TextUtils.isEmpty(downloadFile)) {
			textView.setTextColor(Color.LTGRAY);
		}
		textView.setText(contentFile);
		
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
		return view;
	}

}
