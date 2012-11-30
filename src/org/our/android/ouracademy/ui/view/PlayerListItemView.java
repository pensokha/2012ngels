package org.our.android.ouracademy.ui.view;

import java.io.File;

import org.our.android.ouracademy.R;
import org.our.android.ouracademy.constants.CommonConstants;
import org.our.android.ouracademy.constants.MatchCategoryColor;
import org.our.android.ouracademy.model.OurCategory;
import org.our.android.ouracademy.model.OurContents;
import org.our.android.ouracademy.ui.widget.NCTextView;
import org.our.android.ouracademy.ui.widget.VerticalTextView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class PlayerListItemView extends LinearLayout implements OnClickListener {

	public interface PlayerListItemViewListener {
		void onClickBook(OurContents contents);
	}

	OurContents contents;

	RelativeLayout book;
	VerticalTextView subject;
	NCTextView title;
	ImageView thumbnail;

	private PlayerListItemViewListener listener;

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

		book = (RelativeLayout)findViewById(R.id.book);
		subject = (VerticalTextView)findViewById(R.id.subject);
		title = (NCTextView)findViewById(R.id.title);
		thumbnail = (ImageView)findViewById(R.id.thumb);

		this.setOnClickListener(this);
	}

	public void setData(OurContents contents) {
		if (contents != null) {
			this.contents = contents;

			OurCategory category = contents.selectedCategory;
			if (category != null) {
				setSubjectText(category.getCategoryTitle());
				setBookBackgroundResource(MatchCategoryColor.getCategoryMatchColorId(contents.selectedCategory == null
					? "" : contents.selectedCategory.getCategoryId()));
			}

			setTitleText(contents.getSubject());

			final String existingFile = CommonConstants.getContentImagePath(contents.getId());
			if (new File(existingFile).exists()) {
				setThumbnailImage(BitmapFactory.decodeFile(existingFile));
			}
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

	public void setThumbnailImage(Bitmap bm) {
		if (thumbnail != null && bm != null) {
			thumbnail.setImageBitmap(bm);
		}
	}

	public void setBookBackgroundResource(int resid) {
		if (book != null) {
			book.setBackgroundResource(resid);
		}
	}

	public void setOnPlayerListViewListener(PlayerListItemViewListener listener) {
		this.listener = listener;
	}

	@Override
	public void onClick(View v) {
		int h = getHeight();
		if (listener != null) {
			listener.onClickBook(contents);
		}
	}
}
