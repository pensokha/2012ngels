package org.our.android.ouracademy.ui.adapter;

import java.util.ArrayList;

import org.our.android.ouracademy.R;
import org.our.android.ouracademy.dao.ContentDAO;
import org.our.android.ouracademy.dao.DAOException;
import org.our.android.ouracademy.model.OurContents;
import org.our.android.ouracademy.ui.view.ContentsView;
import org.our.android.ouracademy.ui.widget.NCHorizontalListView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
*
* @author JiHoon, Moon
*
*/
public class ContentsListAdapter extends BaseAdapter  {
	private Context context;
	ArrayList<OurContents> contentsList = null;
	
	private LayoutInflater inflater;
	
	private View emptyView;
	
	public static final int CELL_PER_ITEM = 2;
	
	public static final int FOOTER = 1;
	
	NCHorizontalListView horizontalListView;
	
	class ObjViewHolder {
		ContentsView[] itemHolderList = new ContentsView[CELL_PER_ITEM];
	}
	
	int layoutIds[] = { R.id.layout_content_01, R.id.layout_content_02 };
	
	public ContentsListAdapter(Context context, ArrayList<OurContents> contentsList) {
		this.context = context;
		this.contentsList = contentsList;
		
		inflater = LayoutInflater.from(context);
	}
	
	public void setEmptyView(View view) {
		emptyView = view;
	}
	
	public void setHorizontalListView(NCHorizontalListView horizontalListView) {
		this.horizontalListView = horizontalListView;
	}
	
	@Override
	public int getCount() {
		if (contentsList == null || contentsList.isEmpty()) {
			return 0;	
		}
		int count = contentsList.size() / CELL_PER_ITEM;
		count = count + (contentsList.size() % CELL_PER_ITEM > 0 ? 1 : 0);
		return count + FOOTER;
	}

	@Override
	public OurContents getItem(int position) {
		if (contentsList == null) {
			return null;	
		}
		if (position < 0 && getCount()-1 > position ) {
			return null;
		}
		return (OurContents)contentsList.get(position * CELL_PER_ITEM);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		Log.d("TEST", "!!!!!!!!!!!!!! : " + position + " getCount() : " + getCount() + " contentsList.size() : " + contentsList.size());
		//footer 
		//Temporary~~!!
		//add back button if contents list number more than 9 
		if (getCount() > 9) {
			if (position == getCount() - 1) {
				convertView = inflater.inflate(R.layout.layout_contents_list_footer, null);
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (horizontalListView != null) {
							horizontalListView.smoothScrollToFirstView();
						}
					}
				});
				return convertView;
			}
		}
		
		//set view holder pattern
		ObjViewHolder holder = new ObjViewHolder();;
		if (convertView == null || convertView.getTag() == null) {
			convertView = inflater.inflate(R.layout.layout_contents_list_item, null);
			
			for (int idx = 0; idx < CELL_PER_ITEM; idx++) {
				ContentsView contentsView = new ContentsView(context);
				contentsView.setCallBack(deletCallBack);
				holder.itemHolderList[idx] = contentsView;
				
				ViewGroup viewGroup = (ViewGroup)convertView.findViewById(layoutIds[idx]);
				viewGroup.addView(contentsView);
			}
			
			convertView.setTag(holder);
		} else {
			holder = (ObjViewHolder)convertView.getTag();
		}
		
		int itemSize = contentsList.size();
		
		//set Layout data & ui
		for (int i = 0; i < CELL_PER_ITEM; i++) {
			int currentPositionOfItem = position * CELL_PER_ITEM + i;
			ContentsView contentsView = holder.itemHolderList[i];
			
			if (currentPositionOfItem < itemSize) {
				OurContents curModel = contentsList.get(currentPositionOfItem);
				contentsView.setAllVisibility(contentsView, View.VISIBLE);
				contentsView.setContentsData(curModel);
			} else {
				contentsView.setAllVisibility(contentsView, View.GONE);
			}
		}
		
		return convertView;
	}
	
	/**
	 * @author Sung-Chul Park
	 */
	ContentsView.DeleteCallBack deletCallBack = new ContentsView.DeleteCallBack() {
		@Override
		public void onDeleteSuccessfully(OurContents ourContent) {
			ourContent.fileStatus = OurContents.FileStatus.NONE;
			ourContent.setDownloadedSize(0);
			// Todo : DB update하는 로직 필요.
			ContentDAO contentDao = new ContentDAO();
			try {
				contentDao.updateDownloadedSize(ourContent);
			} catch (DAOException e) {
				e.printStackTrace();
			}
			notifyDataSetChanged();	
		}
	};
	
	@Override
	public void notifyDataSetChanged() {
		checkItemList();
		super.notifyDataSetChanged();
	}
	
	public void notifyDataSetChanged(ArrayList<OurContents> contentsList) {
		this.contentsList = contentsList;
		checkItemList();
		super.notifyDataSetChanged();
	}
	
	public void checkItemList() {
		if (emptyView != null) {
			if (contentsList.isEmpty()) {
				emptyView.setVisibility(View.VISIBLE);
			} else {
				emptyView.setVisibility(View.INVISIBLE);
			}
		}
	}
}
