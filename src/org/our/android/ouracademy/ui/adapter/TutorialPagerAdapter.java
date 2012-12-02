package org.our.android.ouracademy.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * 
 * @author JiHoon, Moon
 * 
 */
public class TutorialPagerAdapter extends PagerAdapter {

	private PagerAdapter adapter;
	private ArrayList<Integer> dataList;
	
	private boolean isAfterInstantiate;
	
	final Context context;
	
	public TutorialPagerAdapter(final Context context) {
		this.context = context;
		this.adapter = new PagerAdapter() {
			@Override
			public int getCount() {
				if (dataList != null) {
					return dataList.size();
				} else {
					return 0;
				}
			}
			
			@Override
			public boolean isViewFromObject(View view, Object object) {
				return view == (View)object;
			}
			
			@Override
			public Object instantiateItem(View collection, int position) {
				ViewPager pager = (ViewPager) collection;
				ImageView imageView = new ImageView(context);
				
				imageView.setImageResource(dataList.get(position));
				pager.addView(imageView);
				return imageView;
			}
			
			@Override
			public void destroyItem(View collection, int postion, Object view) {
				((ViewPager) collection).removeView((View)view);
			}
		};
	}

	@Override
	public int getCount() {
		if (adapter.getCount() == 0) {
			return 0;
		} else {
			return adapter.getCount() * 2000;
		}
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		int virtualPosition = position % getRealCount();
		
		isAfterInstantiate = true;

		return adapter.instantiateItem(container, virtualPosition);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		int virtualPosition = position % getRealCount();

		adapter.destroyItem(container, virtualPosition, object);
		
		if (getRealCount() == 3 && isAfterInstantiate) {
			instantiateItem(container, position - getRealCount());
		}
	}

	@Override
	public void finishUpdate(ViewGroup container) {
		adapter.finishUpdate(container);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return adapter.isViewFromObject(view, object);
	}

	@Override
	public void restoreState(Parcelable bundle, ClassLoader classLoader) {
		adapter.restoreState(bundle, classLoader);
	}

	@Override
	public void startUpdate(ViewGroup container) {
		isAfterInstantiate = false;
		adapter.startUpdate(container);
	}

	public int getRealCount() {
		return adapter.getCount();
	}
	
	public void setDataList(ArrayList<Integer> dataList) {
		this.dataList = dataList;
	}

}