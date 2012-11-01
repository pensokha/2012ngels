package org.our.android.ouracademy.ui.view;

import java.util.ArrayList;

import org.our.android.ouracademy.OurPreferenceManager;
import org.our.android.ouracademy.R;
import org.our.android.ouracademy.dao.OurDAO;
import org.our.android.ouracademy.dao.StudentDAO;
import org.our.android.ouracademy.dao.TeacherDAO;
import org.our.android.ouracademy.model.OurContents;
import org.our.android.ouracademy.model.OurContents.FileStatus;
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
	
	private ListView contentsListview;
	private ContentsListAdapter contentsListAdapter;
	
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
	
	private ArrayList<OurContents> getContensListData() {
		OurPreferenceManager.getInstance().setTeacherMode();
		OurDAO dataDao = getOurDataDAO();
		// TODO : 모든 경우 : 현존하는 파일과 DB정보를 일치 시킨다. - File에서 Meta 정보를 추출하는 기능을 개발 후
		// 작업을 한다.
		dataDao.sync();

		// 선생님일 경우 : FSI로 부터 Data를 로딩한다.
		// 학생일 경우 : DB로 부터 Data를 로딩한다.
//		try {
//			contentsList = dataDao.getInitContents();
//			Log.d("Test", ""+contentsList.size());
//		} catch (DAOException e) {
//			e.printStackTrace();
//		}
		
		//임시 추가
		String[] temp = { "아이스크림", "버블팝", "강남스타일",
				"1,2,3,4", "shy boy", "Magic Girl",
				"MY MY MV", "Good-bye Baby", "Breathe",
				"Like this", "So Cool", "Electric Shock"};
		String[] url = {"http://www.youtube.com/watch?v=QlWZluzBNxM&feature=g-all-xit",
				"http://www.youtube.com/watch?v=bw9CALKOvAI&feature=relmfu",
				"http://www.youtube.com/watch?v=kpZhZAr1cQU&feature=related",
				"http://www.youtube.com/watch?v=KOORTO1byzY",
				"http://www.youtube.com/watch?v=JHzSd26gB-c&feature=b-vrec",
				"http://www.youtube.com/watch?v=MouuWhlmFO0&feature=related",
				"http://www.youtube.com/watch?v=YDI2G9jzN-U&feature=related",
				"http://www.youtube.com/watch?v=X8FXUq4HBkw&feature=related",
				"http://www.youtube.com/watch?v=wTnwjlDhAY4&feature=related",
				"http://www.youtube.com/watch?v=7EZTUYwjWBs&feature=fvwrel",
				"http://www.youtube.com/watch?v=Pj3q0ZChgFE&feature=related",
				"http://www.youtube.com/watch?v=n8I8QGFA1oM&feature=related"};
		
		contentsList = new ArrayList<OurContents>();
		OurContents ourContent;
		for (int i = 0; i < temp.length; i++) {
			ourContent = new OurContents();
			ourContent.setId(temp[i]);
			ourContent.setSubjectEng(temp[i]);
			ourContent.setContentUrl(url[i]);
			ourContent.fileStatus = FileStatus.NONE;
			contentsList.add(ourContent);
		}
		return contentsList;
	}
	
	private OurDAO getOurDataDAO() {
		if (OurPreferenceManager.getInstance().isTeacher()) {
			return new TeacherDAO();
		} else {
			return new StudentDAO();
		}
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
	
	public void notifyDataSetChanged() {
		contentsListAdapter.notifyDataSetChanged();
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