package org.our.android.ouracademy.ui.view;

import java.util.ArrayList;

import org.our.android.ouracademy.R;
import org.our.android.ouracademy.dao.ContentDAO;
import org.our.android.ouracademy.dao.DAOException;
import org.our.android.ouracademy.model.OurContents;
import org.our.android.ouracademy.ui.adapter.ContentsListAdapter;
import org.our.android.ouracademy.ui.widget.NCHorizontalListView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
*
* @author JiHoon, Moon
*
*/
@SuppressWarnings("deprecation")
public class MainDetailView extends RelativeLayout {
	private ViewGroup detailRootLayout;
	private ViewGroup detailLayout;
	private ImageView decoyImage;
	
	private ViewGroup hideMenuBtn;
	
	private TextView dragLayoutTxt = null;

	private int moveStart;
	private int moveEnd;

	private int aniDuration = 200;
	
	ListView contentsListview;
	ContentsListAdapter contentsListAdapter;
	
	public int dragWidth = 0;				//Drag field witdh
	
	ArrayList<OurContents> contentsList;
	
	public enum TouchStatus {
		START_DRAGGING,
		DRAGGING,
		STOP_DRAGGING
	}

	public TouchStatus touchStatus = TouchStatus.STOP_DRAGGING;

	public enum MenuStatus {
		INVISIBLE_MENU,
		MOVING_MENU,
		VISIBLE_MENU
	}

	public MenuStatus menuStatus = MenuStatus.INVISIBLE_MENU;
	
	public MainDetailView(Context context) {
		super(context);
		initUI();
	}

	public MainDetailView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initUI();
	}
	
	public ArrayList<OurContents> getContentList(){
		return contentsList;
	}
	
	public ContentsListAdapter getListAdapter(){
		return contentsListAdapter;
	}
	
	public ListView getListView(){
		return contentsListview;
	}
	
	private ArrayList<OurContents> getContensListData() {
		ContentDAO contentDao = new ContentDAO();
		try {
			contentsList = contentDao.getContents();
		} catch (DAOException e) {
			e.printStackTrace();
			contentsList = new ArrayList<OurContents>();
		}
		return contentsList;
	}
	
	private void initUI() {
		LayoutInflater.from(getContext()).inflate(R.layout.layout_main_detail, this, true);
		
		detailRootLayout = (ViewGroup)findViewById(R.id.layout_root_detail);
		detailLayout = (ViewGroup)findViewById(R.id.layout_detail);
		
		ViewGroup dragLayout = (ViewGroup)findViewById(R.id.drag_layout);
        dragLayout.setOnTouchListener(dargTouchListener);
        
        dragLayoutTxt = (TextView) dragLayout.findViewById(R.id.drag_layout_txt);
        dragLayoutTxt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickMenu();
			}
		});
        
        hideMenuBtn = (ViewGroup)(ViewGroup)findViewById(R.id.hide_menu_btn);
        hideMenuBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickMenu();
			}
		});
        
        hideMenuBtn.setClickable(false);
		
		//상단화면 CashBitmap이 저장된 iamgeView
        decoyImage = new ImageView(getContext());
        
        NCHorizontalListView listView = (NCHorizontalListView) findViewById(R.id.horizontal_listview);
        contentsListAdapter = new ContentsListAdapter(getContext(), getContensListData());
        listView.setAdapter(contentsListAdapter);
	}
	
	public void onClickMenu() {
		if (menuStatus == MenuStatus.MOVING_MENU) {
			return;
		}
		AbsoluteLayout.LayoutParams params = (AbsoluteLayout.LayoutParams)detailLayout.getLayoutParams();
		setDetailLayoutImageCache(params);

		if (menuStatus == MenuStatus.VISIBLE_MENU) {
			hideManuAnimation(dragWidth);
		} else if (menuStatus == MenuStatus.INVISIBLE_MENU) {
			openMenuAnimation(dragWidth);
		}
	}
	
	private void setDetailLayoutImageCache(android.widget.AbsoluteLayout.LayoutParams params) {
		if (menuStatus == MenuStatus.MOVING_MENU) {
			return;
		}
		detailLayout.destroyDrawingCache();
		detailLayout.buildDrawingCache();
		decoyImage.setImageBitmap(detailLayout.getDrawingCache());
		decoyImage.setLayoutParams(params);

		detailRootLayout.addView(decoyImage);
	}
	
	public void openMenuAnimation(final int aniWidth) {
		if (menuStatus == MenuStatus.MOVING_MENU) {
			return;
		}
		final AnimationSet set = new AnimationSet(true);
		set.setInterpolator(getContext(), android.R.anim.decelerate_interpolator);
		Animation ani = new TranslateAnimation(0.0f, aniWidth, 0.0f, 0.0f);
		ani.setDuration(aniDuration);
		set.addAnimation(ani);
		decoyImage.startAnimation(set);

		set.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				menuStatus = MenuStatus.MOVING_MENU;
				detailLayout.setVisibility(View.INVISIBLE);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				menuStatus = MenuStatus.VISIBLE_MENU;
				setDetailLayoutXPosition(dragWidth);
				detailRootLayout.removeView(decoyImage);

				hideMenuBtn.setClickable(true);
				changeDragLayoutIcon();
			}
		});
	}

	public void hideManuAnimation(final int aniWidth) {
		final AnimationSet set = new AnimationSet(true);
		set.setInterpolator(getContext(), android.R.anim.decelerate_interpolator);
		Animation ani = new TranslateAnimation(0, -aniWidth, 0.0f, 0.0f);
		ani.setDuration(aniDuration);
		set.addAnimation(ani);
		decoyImage.startAnimation(set);

		set.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				menuStatus = MenuStatus.MOVING_MENU;
				detailLayout.setVisibility(View.INVISIBLE);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				menuStatus = MenuStatus.INVISIBLE_MENU;
				setDetailLayoutXPosition(0);
				detailRootLayout.removeView(decoyImage);

				hideMenuBtn.setClickable(false);
				changeDragLayoutIcon();
			}
		});
	}
	
	private void changeDragLayoutIcon() {
		if (dragLayoutTxt == null) {
			return;
		}
		
		if (menuStatus == MenuStatus.VISIBLE_MENU) {
			dragLayoutTxt.setText(R.string.list);
			dragLayoutTxt.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.list_icon_menu02, 0, 0);
		} else if (menuStatus == MenuStatus.INVISIBLE_MENU) {
			dragLayoutTxt.setText(R.string.menu);
			dragLayoutTxt.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.list_icon_menu01, 0, 0);
		}
	}
	
	private void setDetailLayoutXPosition(int XPoint) {
		detailLayout.setVisibility(View.VISIBLE);
		AbsoluteLayout.LayoutParams params;
		params = (AbsoluteLayout.LayoutParams)detailLayout.getLayoutParams();
		params.x = XPoint;
		detailLayout.setLayoutParams(params);
	}
	
	OnTouchListener dargTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				touchStatus = TouchStatus.START_DRAGGING;

				AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT, ((AbsoluteLayout.LayoutParams)detailLayout.getLayoutParams()).x, 0);
				setDetailLayoutImageCache(params);

				detailLayout.setVisibility(View.INVISIBLE);

				moveStart = (int)event.getRawX();
			} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
				touchStatus = TouchStatus.DRAGGING;
				moveEnd = (int)event.getRawX();
				AbsoluteLayout.LayoutParams params;
				params = (AbsoluteLayout.LayoutParams)decoyImage.getLayoutParams();
				int posX = params.x + moveEnd - moveStart;
				if (0 < posX && posX < dragWidth) {
					params.x = posX;
					decoyImage.setLayoutParams(params);
				}
				moveStart = moveEnd;
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				if (touchStatus == TouchStatus.DRAGGING) { //decoyImage가 이동한 경우
					touchStatus = TouchStatus.STOP_DRAGGING;

					AbsoluteLayout.LayoutParams params;
					params = (AbsoluteLayout.LayoutParams)decoyImage.getLayoutParams();
					int posX = params.x;

					if (menuStatus == MenuStatus.VISIBLE_MENU) { //하단화면이 보이는 경우
						if (posX == dragWidth) {
							hideManuAnimation(posX);
						} else if (posX > (dragWidth * 0.7)) {
							openMenuAnimation(dragWidth - posX);
						} else {
							hideManuAnimation(posX);
						}
					} else if (menuStatus == MenuStatus.INVISIBLE_MENU) { //상단화면이 보이는 경우
						if (posX < (dragWidth * 0.3)) {
							hideManuAnimation(posX);
						} else {
							openMenuAnimation(dragWidth - posX);
						}
					}
				} else if (touchStatus == TouchStatus.START_DRAGGING) { //decoyImage가 이동하지 않은 경우
					touchStatus = TouchStatus.STOP_DRAGGING;
					detailLayout.setVisibility(View.VISIBLE);
					detailRootLayout.removeView(decoyImage);
				}
			}
			return true;
		}
	};
}