package org.our.android.ouracademy.ui.pages;

import org.our.android.ouracademy.R;
import org.our.android.ouracademy.ui.adapter.ContentsListAdapter;
import org.our.android.ouracademy.ui.view.MainDetailView;
import org.our.android.ouracademy.ui.view.MainMenuView;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

/**
*
* @author JiHoon, Moon
*
*/
public class MainActivity extends BaseActivity {
	private ViewGroup menuLayout;
	private ViewGroup detailLayout;
	
	MainDetailView detailView;
	MainMenuView mainMenuView;

	ListView contentsListview;
	ContentsListAdapter contentsListAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUI();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	private void initUI() {
		setContentView(R.layout.activity_main);
		
		menuLayout		= (ViewGroup)findViewById(R.id.layout_main_menu);				//하단화면 View
        detailLayout	= (ViewGroup)findViewById(R.id.layout_main_detail);				//상단화면 View
        
        initMenuLayout();
        initContentsLayout();
	}
	
	private void initMenuLayout() {
		mainMenuView = new MainMenuView(this);
		mainMenuView.setApplyBtnListener(applyBtnClickListener);
		menuLayout.addView(mainMenuView);
	}
	
	private void initContentsLayout() {
		detailView = new MainDetailView(this);
		detailView.dragWidth = 513; 		//
		detailLayout.addView(detailView);
	}
	
	OnClickListener applyBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			detailView.onClickMenu();
		}
	};
}
