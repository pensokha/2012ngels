package org.our.android.ouracademy.ui.pages;

import java.util.ArrayList;

import org.our.android.ouracademy.R;
import org.our.android.ouracademy.ui.adapter.TutorialPagerAdapter;
import org.our.android.ouracademy.util.ScreenInfo;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 
 * @author JiHoon, Moon
 * 
 */
public class TutorialActivity extends BaseActivity {
	private ViewPager viewPager;
	private TutorialPagerAdapter viewPagerAdapter;
	private LinearLayout pageMark;
	
	private Drawable activePoint;
	private Drawable inactivePoint;
	private int prevPosition = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tutorial);
		initUI();
	}
	
	private void initUI() {
		Button closeBtn = (Button)findViewById(R.id.close_button);
		closeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		viewPager = (ViewPager)findViewById(R.id.view_pager);
		
		viewPagerAdapter = new TutorialPagerAdapter(this);
		
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(R.drawable.tutorial1);
		list.add(R.drawable.tutorial2);
		list.add(R.drawable.tutorial3);
		list.add(R.drawable.tutorial4);
		viewPagerAdapter.setDataList(list);
		
		viewPager.setAdapter(viewPagerAdapter);
		viewPager.setCurrentItem(viewPagerAdapter.getRealCount() * 1000);
		
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				((ImageView)pageMark.getChildAt(prevPosition)).setImageDrawable(inactivePoint);
				((ImageView)pageMark.getChildAt(position % viewPagerAdapter.getRealCount())).setImageDrawable(activePoint);
				prevPosition = position % viewPagerAdapter.getRealCount();
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
		
		initPageMark();
	}
	
	private void initPageMark() {
		pageMark = (LinearLayout)findViewById(R.id.page_mark);
		
		activePoint = getResources().getDrawable(R.drawable.tutorial_point1);
		inactivePoint = getResources().getDrawable(R.drawable.tutorial_point2);
		
		pageMark.removeAllViews();
		
		ImageView iv;
		
		for (int i = 0; i < viewPagerAdapter.getRealCount(); i++) {
			iv = new ImageView(this);
			
			int pointGap = ScreenInfo.dp2px(7);
			
			MarginLayoutParams margin = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			margin.setMargins(pointGap, 0, pointGap, 0);
			iv.setLayoutParams(new LinearLayout.LayoutParams(margin));

			if (i == prevPosition) {
				iv.setImageDrawable(activePoint);
			} else {
				iv.setImageDrawable(inactivePoint);
			}
			
			pageMark.addView(iv);
		}
	}
}
