package org.our.android.ouracademy.ui.view;

import java.util.ArrayList;

import org.our.android.ouracademy.R;
import org.our.android.ouracademy.constants.CommonConstants;
import org.our.android.ouracademy.model.OurContents;
import org.our.android.ouracademy.ui.adapter.ContentsListAdapter;
import org.our.android.ouracademy.ui.widget.NCBoldTextView;
import org.our.android.ouracademy.ui.widget.NCHorizontalListView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
*
* @author JiHoon, Moon
*
*/
@SuppressWarnings("deprecation")
public class MainDetailView extends RelativeLayout implements OnItemClickListener {
	private ViewGroup detailRootLayout;
	private ViewGroup detailLayout;
	private ImageView decoyImage, textModeBtn;

	private ViewGroup hideMenuBtn;

	private NCBoldTextView dragLayoutTxt;

	private View emptyView;

	private int moveStart;
	private int moveEnd;

	private int aniDuration = 200;

	NCHorizontalListView horizontalListView;
	ContentsListAdapter contentsListAdapter;

	private boolean isFristLoad = true;
	private boolean isFullTextMode = false;

	ArrayList<OurContents> contentsList = new ArrayList<OurContents>();

	public enum TouchStatus {
		START_DRAGGING,
		DRAGGING,
		STOP_DRAGGING
	}

	public TouchStatus touchStatus = TouchStatus.STOP_DRAGGING;

	public enum MenuStatus {
		INVISIBLE_MENU,
		MOVING_MENU,
		VISIBLE_MENU,
		DELETE_MODE_VIEW
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

	public ArrayList<OurContents> getContentList() {
		return contentsList;
	}

	public NCHorizontalListView getList() {
		return horizontalListView;
	}

	public ContentsListAdapter getListAdapter() {
		return contentsListAdapter;
	}

	private void initUI() {
		LayoutInflater.from(getContext()).inflate(R.layout.layout_main_detail, this, true);

		emptyView = findViewById(R.id.empty_guide_text);

		detailRootLayout = (ViewGroup)findViewById(R.id.layout_root_detail);
		detailLayout = (ViewGroup)findViewById(R.id.layout_detail);

		ViewGroup dragLayout = (ViewGroup)findViewById(R.id.drag_layout);
		dragLayout.setOnTouchListener(dargTouchListener);

		dragLayoutTxt = (NCBoldTextView)dragLayout.findViewById(R.id.drag_layout_txt);
		dragLayoutTxt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickMenu();
			}
		});

		hideMenuBtn = (ViewGroup)findViewById(R.id.hide_menu_btn);
		hideMenuBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickMenu();
			}
		});

		textModeBtn = (ImageView)findViewById(R.id.textModeBtn);
		textModeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isFullTextMode = !isFullTextMode;

				if (isFullTextMode) {
					textModeBtn.setBackgroundResource(R.drawable.btn_state_book_title_view_mode);
				} else {
					textModeBtn.setBackgroundResource(R.drawable.btn_state_book_thumb_view_mode);
				}
				changeTextMode(isFullTextMode);
			}
		});

		hideMenuBtn.setClickable(true);

		decoyImage = new ImageView(getContext());

		horizontalListView = (NCHorizontalListView)findViewById(R.id.horizontal_listview);
		contentsListAdapter = new ContentsListAdapter(getContext(), contentsList);
		contentsListAdapter.setEmptyView(emptyView);
		contentsListAdapter.setHorizontalListView(horizontalListView);
		horizontalListView.setAdapter(contentsListAdapter);
		horizontalListView.setOnItemClickListener(this);

		setDetailLayoutXPosition(CommonConstants.DETAIL_ANI_END_X);
		menuStatus = MenuStatus.VISIBLE_MENU;
	}

//	@Override
//	public void onWindowFocusChanged(boolean hasFocus) {
//		super.onWindowFocusChanged(hasFocus);
//	    if (isFristLoad) {
//	    	isFristLoad = false;
//	    	//시작히 카테고리 보여주고 0.3초뒤에 메뉴를 닫는다.
//	        setDetailLayoutXPosition(OurDefine.DETAIL_ANI_END_X);
//	        Handler aniHandler = new Handler();
//	        aniHandler.postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					hideManuAnimation(OurDefine.DETAIL_ANI_WIDTH);
//				}
//			}, 300);
//	    }
//	}

	public void onClickMenu() {
		if (menuStatus == MenuStatus.MOVING_MENU) {
			return;
		}
		AbsoluteLayout.LayoutParams params = (AbsoluteLayout.LayoutParams)detailLayout.getLayoutParams();
		setDetailLayoutImageCache(params);

		if (menuStatus == MenuStatus.VISIBLE_MENU) {
			hideManuAnimation(CommonConstants.DETAIL_ANI_WIDTH);
		} else if (menuStatus == MenuStatus.INVISIBLE_MENU) {
			openMenuAnimation(CommonConstants.DETAIL_ANI_WIDTH);
		} else if (menuStatus == MenuStatus.DELETE_MODE_VIEW) {
			changeToMenuIcon();
		}
	}

	/**
	 * @author Sung-Chul Park
	 * ok버튼 아이콘을 메뉴 아이콘으로 변경
	 */
	public void changeToMenuIcon() {
		for (OurContents contents : contentsList) {
			contents.setDeleteMode(false);
		}

		contentsListAdapter.notifyDataSetChanged(contentsList);

		// 메뉴 아이콘 변경.
		menuStatus = MenuStatus.INVISIBLE_MENU;
		setDetailLayoutXPosition(CommonConstants.DETAIL_ANI_START_X);
		detailRootLayout.removeView(decoyImage);

		hideMenuBtn.setClickable(false);
		changeDragLayoutIcon();
	}

	/**
	 * 셋팅 화면 선생님 모드에서 삭제 버튼 클릭 후 삭제 모드로 전환할 때 호출.
	 * @author Sung-Chul Park
	 */
	public void goIntoDeleteMode() {
		if (menuStatus == MenuStatus.MOVING_MENU) {
			return;
		}
		AbsoluteLayout.LayoutParams params = (AbsoluteLayout.LayoutParams)detailLayout.getLayoutParams();
		setDetailLayoutImageCache(params);

		for (OurContents contents : contentsList) {
			if (contents.fileStatus == OurContents.FileStatus.DOWNLOADED) {
				contents.setDeleteMode(true);
			}
		}

		contentsListAdapter.notifyDataSetChanged(contentsList);

		hideManuAnimationOnDeleteMode(CommonConstants.DETAIL_ANI_WIDTH);
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
				menuStatus = MenuStatus.VISIBLE_MENU;
				setDetailLayoutXPosition(CommonConstants.DETAIL_ANI_END_X);
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
				setDetailLayoutXPosition(CommonConstants.DETAIL_ANI_START_X);
				detailRootLayout.removeView(decoyImage);

				hideMenuBtn.setClickable(false);
				changeDragLayoutIcon();
			}
		});
	}

	public void hideManuAnimationOnDeleteMode(final int aniWidth) {
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
				menuStatus = MenuStatus.DELETE_MODE_VIEW;
				setDetailLayoutXPosition(CommonConstants.DETAIL_ANI_START_X);
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
		} else if (menuStatus == MenuStatus.DELETE_MODE_VIEW) {
			dragLayoutTxt.setText(R.string.menu_ok);
			dragLayoutTxt.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.list_icon_menu03, 0, 0);
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
			if (menuStatus == MenuStatus.DELETE_MODE_VIEW) {
				return true;
			}
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
				if (CommonConstants.DETAIL_ANI_START_X < posX && posX < CommonConstants.DETAIL_ANI_END_X) {
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
						if (posX == CommonConstants.DETAIL_ANI_END_X) {
							hideManuAnimation(posX);
						} else if (posX > (CommonConstants.DETAIL_ANI_END_X * 0.7)) {
							openMenuAnimation(CommonConstants.DETAIL_ANI_END_X - posX);
						} else {
							hideManuAnimation(posX - CommonConstants.DETAIL_ANI_START_X);
						}
					} else if (menuStatus == MenuStatus.INVISIBLE_MENU) { //상단화면이 보이는 경우
						if (posX < (CommonConstants.DETAIL_ANI_END_X * 0.3)) {
							hideManuAnimation(posX - CommonConstants.DETAIL_ANI_START_X);
						} else {
							openMenuAnimation(CommonConstants.DETAIL_ANI_END_X - posX);
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d("park", "onItem: " + position);
	}

	public void changeTextMode(boolean isFullTextMode) {
		if (menuStatus == MenuStatus.MOVING_MENU) {
			return;
		}
//		AbsoluteLayout.LayoutParams params = (AbsoluteLayout.LayoutParams)detailLayout.getLayoutParams();
//		setDetailLayoutImageCache(params);

		for (OurContents contents : contentsList) {
			if (contents.fileStatus == OurContents.FileStatus.DOWNLOADED) {
				contents.setFullTextMode(isFullTextMode);
			}
		}

		contentsListAdapter.notifyDataSetChanged(contentsList);

//		hideManuAnimationOnDeleteMode(CommonConstants.DETAIL_ANI_WIDTH);
	}

	public boolean isFullTextMode() {
		return isFullTextMode;
	}
}
