package org.our.android.ouracademy.ui.view;

import org.our.android.ouracademy.R;
import org.our.android.ouracademy.ui.widget.VerticalTextView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PlayerListItemView extends LinearLayout {

	VerticalTextView subject;
	TextView title;
	ImageView thumbnail;

	public PlayerListItemView(Context context) {
		super(context);

		init();
	}

	public PlayerListItemView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	private void init() {
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater)getContext().getSystemService(infService);
		li.inflate(R.layout.player_list_item, this, true);

		subject = (VerticalTextView)findViewById(R.id.subject);
		title = (TextView)findViewById(R.id.title);
		thumbnail = (ImageView)findViewById(R.id.thumbnail);
	}

	public void setData(String subject, String title, Drawable imageDrawable) {
		setSubjectText(subject);
		setTitleText(title);

		if (imageDrawable != null) {
			setThumbnailImage(imageDrawable);
		}
	}

	private void setSubjectText(String text) {
		if (!TextUtils.isEmpty(text)) {
			subject.setText(text);
		}
	}

	private void setTitleText(String text) {
		if (!TextUtils.isEmpty(text)) {
			title.setText(text);
		}
	}

	public void setThumbnailImage(Drawable imageDrawable) {
		thumbnail.setBackground(imageDrawable);
	}
}
