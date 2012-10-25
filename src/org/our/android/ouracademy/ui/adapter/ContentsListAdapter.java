package org.our.android.ouracademy.ui.adapter;

import java.util.ArrayList;

import org.our.android.ouracademy.R;
import org.our.android.ouracademy.model.OurContent;
import org.our.android.ouracademy.model.OurContent.FileStatus;
import org.our.android.ouracademy.ui.pages.MediaPlayerPage;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
*
* @author JiHoon, Moon
*
*/
public class ContentsListAdapter extends BaseAdapter {
	private Context context;
	ArrayList<OurContent> contentsList = null;
	
	private LayoutInflater inflater;
	
	public static final int CELL_PER_ITEM = 2;
	
	class ObjViewHolder {
		TextView tagTitle01;
		ItemHolder[] itemHolderList = new ItemHolder[CELL_PER_ITEM];
	}


	int layoutIds[] = {
		R.id.layout_content_01,
		R.id.layout_content_02
	};

	int contentTitleIds[] = {
		R.id.txt_content_01,
		R.id.txt_content_02
	};
	
	int contentProgressBarIds[] = {
		R.id.progressbar_01,
		R.id.progressbar_02
	};
	
	class ItemHolder {
    	RelativeLayout itemCellLayout;
        TextView contentTitle;
        ProgressBar progressbar;
    }
	
	public ContentsListAdapter(Context context, ArrayList<OurContent> contentsList) {
		this.context = context;
		this.contentsList = contentsList;
		
		inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		if (contentsList == null) {
			return 0;	
		}
		int count = contentsList.size() / CELL_PER_ITEM;
		count = count + (contentsList.size() % CELL_PER_ITEM > 0 ? 1 : 0);
		return count;
	}

	@Override
	public OurContent getItem(int position) {
		if (contentsList == null) {
			return null;	
		}
		if (position < 0 && getCount()-1 > position ) {
			return null;
		}
		return (OurContent)contentsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int itemSize = contentsList.size();
		if (position * CELL_PER_ITEM >= itemSize) {
			return convertView;
		}
		
		ObjViewHolder holder = new ObjViewHolder();;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.layout_contents_list_item, null);
			
			for (int idx = 0; idx < CELL_PER_ITEM; idx++) {
				ItemHolder itemHolder = new ItemHolder();
				itemHolder.itemCellLayout = (RelativeLayout)convertView.findViewById(layoutIds[idx]);
				itemHolder.contentTitle = (TextView)convertView.findViewById(contentTitleIds[idx]);
				itemHolder.progressbar = (ProgressBar)convertView.findViewById(contentProgressBarIds[idx]);
				holder.itemHolderList[idx] = itemHolder;
			}
			
			holder.tagTitle01 = (TextView)convertView.findViewById(R.id.txt_list_tag_01);
			
			convertView.setTag(holder);
		} else {
			holder = (ObjViewHolder)convertView.getTag();
		}
		
		for (int i = 0; i < CELL_PER_ITEM; i++) {
			final int currentPositionOfItem = position * CELL_PER_ITEM + i;
			if (currentPositionOfItem < itemSize) {
				final ItemHolder itemHolder = holder.itemHolderList[i];
				itemHolder.itemCellLayout.setVisibility(View.VISIBLE);
				final OurContent model = contentsList.get(currentPositionOfItem);
				if (model.fileStatus == FileStatus.DOWNLOADED) {	//파일이 존재
					itemHolder.itemCellLayout.setBackgroundResource(R.drawable.btn_main_book_selector);
					itemHolder.progressbar.setVisibility(View.INVISIBLE);
				} else if (model.fileStatus == FileStatus.DOWNLOADING) {	//파일 다운로드 중
					itemHolder.itemCellLayout.setBackgroundResource(R.drawable.book_download02);
					itemHolder.progressbar.setVisibility(View.VISIBLE);
					//다운로드 진행 % 셋팅
					
				} else {	//파일이 없는 경우
					itemHolder.itemCellLayout.setBackgroundResource(R.drawable.btn_main_book_download_selector);
				}
				
//				holder.itemHolderList[i].itemCellLayout.setBackgroundColor(Color.BLUE);
				holder.itemHolderList[i].contentTitle.setText(model.getSubjectEng());
				holder.itemHolderList[i].itemCellLayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						if (model.fileStatus == FileStatus.DOWNLOADED) {	//파일이 존재
							String sd = Environment.getExternalStorageDirectory().getAbsolutePath();
							String filePath = sd + "/OurAcademy/HYUNA - 'Ice Cream'.mp4";
							
							Intent intent = new Intent(context, MediaPlayerPage.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra(MediaPlayerPage.INTENTKEY_STR_VIDEO_FILE_PATH, filePath);
							context.startActivity(intent);
						} else if (model.fileStatus == FileStatus.DOWNLOADING) {	//파일 다운로드 중
							return;
						} else {	//파일이 없는 경우
							//다운로드 시작
							model.fileStatus = FileStatus.DOWNLOADING;
							itemHolder.itemCellLayout.setBackgroundResource(R.drawable.book_download02);
							itemHolder.progressbar.setVisibility(View.VISIBLE);
						}
						
					}
				});
			} else {
				holder.itemHolderList[i].itemCellLayout.setVisibility(View.GONE);
			}
		}
		return convertView;
	}
}
