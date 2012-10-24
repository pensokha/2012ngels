package org.our.android.ouracademy.ui.pages;

import java.util.ArrayList;

import org.our.android.ouracademy.OurPreferenceManager;
import org.our.android.ouracademy.R;
import org.our.android.ouracademy.dao.DAOException;
import org.our.android.ouracademy.dao.OurDAO;
import org.our.android.ouracademy.dao.StudentDAO;
import org.our.android.ouracademy.dao.TeacherDAO;
import org.our.android.ouracademy.model.OurCategory;
import org.our.android.ouracademy.model.OurContent;
import org.our.android.ouracademy.ui.adapter.ContentsListAdapter;
import org.our.android.ouracademy.ui.view.HorizontalListView;
import org.our.android.ouracademy.util.ScreenInfo;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

/**
*
* @author JiHoon, Moon
*
*/
@SuppressWarnings("deprecation")
public class MainActivity extends BaseActivity {
	private ViewGroup menuLayout;
	private ViewGroup detailRootView;
	private ViewGroup detailLayout;

	private ViewGroup hideMenuBtn;

	private ImageView decoyImage;

	private int moveStart;
	private int moveEnd;

	private int aniDuration = 200;

	ListView contentsListview;
	ContentsListAdapter contentsListAdapter;

	ArrayList<OurContent> contentsList;
	ArrayList<OurCategory> categoryList;

	enum TouchStatus {
		START_DRAGGING,
		DRAGGING,
		STOP_DRAGGING
	}

	TouchStatus touchStatus = TouchStatus.STOP_DRAGGING;

	enum MenuStatus {
		INVISIBLE_MENU,
		MOVING_MENU,
		VISIBLE_MENU
	}

	MenuStatus menuStatus = MenuStatus.INVISIBLE_MENU;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		initData();
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
	
	private OurDAO getOurDataDAO() {
		if (OurPreferenceManager.getInstance().isTeacher()) {
			return new TeacherDAO();
		} else {
			return new StudentDAO();
		}
	}

	private void initData() {
		OurDAO dataDao = getOurDataDAO();
		// TODO : 모든 경우 : 현존하는 파일과 DB정보를 일치 시킨다. - File에서 Meta 정보를 추출하는 기능을 개발 후
		// 작업을 한다.
		dataDao.sync();

		// 선생님일 경우 : FSI로 부터 Data를 로딩한다.
		// 학생일 경우 : DB로 부터 Data를 로딩한다.
		try {
			contentsList = dataDao.getInitContents();
		} catch (DAOException e) {
//			e.printStackTrace();
		}
	}

	private void initUI() {
		setContentView(R.layout.activity_main);
		
		menuLayout		= (ViewGroup)findViewById(R.id.layout_menu);				//하단화면 View
        detailRootView	= (ViewGroup)findViewById(R.id.layout_root_detail);			//상단화면 최상단 View
        detailLayout	= (ViewGroup)findViewById(R.id.layout_detail);				//상단화면 View
        
        hideMenuBtn = (ViewGroup)(ViewGroup)findViewById(R.id.hide_menu_btn);
        hideMenuBtn.setClickable(false);
        
        ViewGroup dragLayout = (ViewGroup)findViewById(R.id.drag_layout);	//상단화면의 메뉴를 Drag할 수 있는 여역
        dragLayout.setOnTouchListener(dargTouchListener);					//onTouchListener 지정
        
        //상단화면 CashBitmap이 저장된 iamgeView
        decoyImage = new ImageView(this);
        
        initContentsLayout();
        initMenuLayout();
	}
	
	private void initContentsLayout() {
		contentsList = new ArrayList<OurContent>();
        
        String[] temp = {"우와","이야","오오","우우","하하","대박","중박","쪽박","말박","이야","오오","우우","하하","대박","중박","쪽박","말박"
        		,"이야","오오","우우","하하","대박","중박","쪽박","말박"
        		,"이야","오오","우우","하하","대박","중박","쪽박","말박"
        		,"이야","오오","우우","하하","대박","중박","쪽박","말박"};
 
        OurContent ourContent;
        for (String tt : temp) {
        	ourContent = new OurContent();
        	ourContent.setSubjectEng(tt);
        	contentsList.add(ourContent);
        }
        
        HorizontalListView listView = (HorizontalListView) findViewById(R.id.horizontal_listview);
        contentsListAdapter = new ContentsListAdapter(this, contentsList);
        listView.setAdapter(contentsListAdapter);
	}
	
	private void initMenuLayout() {
		ViewGroup menuListLayout = (ViewGroup)menuLayout.findViewById(R.id.menu_category_listview);
		
		categoryList = new ArrayList<OurCategory>();
		String[] temp = {"English", "Math", "Korean", " Social Study", "science", "Music", "Art",
				 "Physical education", "Practical course", "Ethics"};
		OurCategory ourCategory;
		for (String tt : temp) {
			ourCategory = new OurCategory();
			ourCategory.setCategoryTitleEng(tt);
			categoryList.add(ourCategory);
		}
		ListView listView = new ListView(this);
		final CategoryListAdapter adapter = new CategoryListAdapter(this, R.layout.layout_category_list_item, categoryList);
		listView.setAdapter(adapter);
		listView.setDivider(null);
		listView.setCacheColorHint(Color.TRANSPARENT);
		listView.setSelector(android.R.color.transparent);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> list, View view, int position, long id) {
				OurCategory ourCategory = categoryList.get(position);
				if (ourCategory.isChecked) {
					ourCategory.isChecked = false;
				} else {
					ourCategory.isChecked = true;
				}
				adapter.notifyDataSetChanged();
			}
		});
		
		menuListLayout.addView(listView, new ViewGroup.LayoutParams(ScreenInfo.dp2px(249.33f), LayoutParams.FILL_PARENT));
		
		Button button = (Button) menuLayout.findViewById(R.id.menu_apply_btn);
		
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickMenu(null);
			}
		});
	}

	private void setDetailLayoutImageCache(LayoutParams params) {
		//detailLayout의 DrawingCache를 가져와 imageView에 넣은다음 datailRootView에 add해준다.
		detailLayout.destroyDrawingCache();
		detailLayout.buildDrawingCache(); //detailLayout의 최신 Drawing Cache를 업데이트 해준다.
		decoyImage.setImageBitmap(detailLayout.getDrawingCache());
		decoyImage.setLayoutParams(params);

		detailRootView.addView(decoyImage);
	}

	public void onClickMenu(View view) {
		AbsoluteLayout.LayoutParams params = (AbsoluteLayout.LayoutParams)detailLayout.getLayoutParams();
		setDetailLayoutImageCache(params);

		if (menuStatus == MenuStatus.VISIBLE_MENU) {
			hideManuAnimation(menuLayout.getWidth());
		} else if (menuStatus == MenuStatus.INVISIBLE_MENU) {
			openMenuAnimation(menuLayout.getWidth());
		}
	}

	public void openMenuAnimation(final int aniWidth) {
		if (menuStatus == MenuStatus.MOVING_MENU) {
			return;
		}

		final AnimationSet set = new AnimationSet(true);
		set.setInterpolator(getBaseContext(), android.R.anim.decelerate_interpolator);
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

				detailLayout.setVisibility(View.VISIBLE);
				AbsoluteLayout.LayoutParams params;
				params = (AbsoluteLayout.LayoutParams)detailLayout.getLayoutParams();
				params.x = (int)(menuLayout.getWidth());
				detailLayout.setLayoutParams(params);

				detailRootView.removeView(decoyImage);

				hideMenuBtn.setClickable(true);
			}
		});
	}

	public void hideManuAnimation(final int aniWidth) {
		if (menuStatus == MenuStatus.MOVING_MENU) {
			return;
		}

		final AnimationSet set = new AnimationSet(true);
		set.setInterpolator(getBaseContext(), android.R.anim.decelerate_interpolator);
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

				detailLayout.setVisibility(View.VISIBLE);
				AbsoluteLayout.LayoutParams params;
				params = (AbsoluteLayout.LayoutParams)detailLayout.getLayoutParams();
				params.x = 0;
				detailLayout.setLayoutParams(params);

				detailRootView.removeView(decoyImage);

				hideMenuBtn.setClickable(false);
			}
		});
	}

	OnTouchListener dargTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				touchStatus = TouchStatus.START_DRAGGING;
//				Log.d("TEST","Down X " + (int) event.getX());
//				Log.d("TEST","Down RawX " + (int) event.getRawX());

				AbsoluteLayout.LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT, ((AbsoluteLayout.LayoutParams)detailLayout.getLayoutParams()).x, 0);
				setDetailLayoutImageCache(params);

				detailLayout.setVisibility(View.INVISIBLE);

				moveStart = (int)event.getRawX();
			} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
				touchStatus = TouchStatus.DRAGGING;
				moveEnd = (int)event.getRawX();
				//decoyImage 위치 이동
				AbsoluteLayout.LayoutParams params;
				params = (AbsoluteLayout.LayoutParams)decoyImage.getLayoutParams();
				int posX = params.x + moveEnd - moveStart;
				if (0 < posX && posX < menuLayout.getWidth()) {
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
						if (posX == menuLayout.getWidth()) {
							hideManuAnimation(posX);
						} else if (posX > (menuLayout.getWidth() * 0.7)) {
							openMenuAnimation(menuLayout.getWidth() - posX);
						} else {
							hideManuAnimation(posX);
						}
					} else if (menuStatus == MenuStatus.INVISIBLE_MENU) { //상단화면이 보이는 경우
						if (posX < (menuLayout.getWidth() * 0.3)) {
							hideManuAnimation(posX);
						} else {
							openMenuAnimation(menuLayout.getWidth() - posX);
						}
					}
				} else if (touchStatus == TouchStatus.START_DRAGGING) { //decoyImage가 이동하지 않은 경우
					touchStatus = TouchStatus.STOP_DRAGGING;
					detailLayout.setVisibility(View.VISIBLE);
					detailRootView.removeView(decoyImage);

					//Detail 화면에서 터치후 Drag 하지 않고 땐경우
					if (menuStatus == MenuStatus.VISIBLE_MENU) {
						onClickMenu(null);
					} else if (menuStatus == MenuStatus.INVISIBLE_MENU) {
						onClickMenu(null);
					}
				}
			}
			return true;
		}
	};

}
