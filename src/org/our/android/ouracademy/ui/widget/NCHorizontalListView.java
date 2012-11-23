package org.our.android.ouracademy.ui.widget;

import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Scroller;

public class NCHorizontalListView extends AdapterView<ListAdapter> {
	Context context;

	protected ListAdapter listAdapter;
	private int leftViewIndex = -1;
	private int rightViewIndex = 0;
	protected int currentX;
	protected int nextX;
	private int maxX = Integer.MAX_VALUE;
	private int displayOffset = 0;
	protected Scroller scroller;
	private GestureDetector gesture;
	private Queue<View> removedViewQueue = new LinkedList<View>();
	private OnItemSelectedListener onItemSelected;
	private OnItemClickListener onItemClickListener;

	boolean isFling = false;
	boolean isFinished = false;
	boolean completeFinishAction = true;
	private Handler handler;
	private Runnable finished;

	private boolean dataChanged = false;

	boolean isBounceEnabled = false;
	int action = -1;
	float x = -1;

	public NCHorizontalListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initView();
	}

	private void initView() {
		leftViewIndex = -1;
		rightViewIndex = 0;
		displayOffset = 0;
		currentX = 0;
		nextX = 0;
		maxX = Integer.MAX_VALUE;
		scroller = new Scroller(getContext());
		gesture = new GestureDetector(getContext(), mOnGesture);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int childHeight = 0;

		if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
			View child = listAdapter.getView(0, null, this);
			if (child != null) {
				child.measure(MeasureSpec.makeMeasureSpec(getWidth(),
						MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(
						getHeight(), MeasureSpec.AT_MOST));

				childHeight = child.getMeasuredHeight();
				setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
						childHeight);
			}
		}

		if (childHeight == 0) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	@Override
	public void setOnItemSelectedListener(
			AdapterView.OnItemSelectedListener listener) {
		onItemSelected = listener;
	}

	@Override
	public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
		onItemClickListener = listener;
	}

	private DataSetObserver mDataObserver = new DataSetObserver() {

		@Override
		public void onChanged() {
			synchronized (NCHorizontalListView.this) {
				dataChanged = true;
			}
			invalidate();
			requestLayout();
		}

		@Override
		public void onInvalidated() {
			// TODO Auto-generated method stub
			super.onInvalidated();
		}

	};

	@Override
	public ListAdapter getAdapter() {
		return listAdapter;
	}

	@Override
	public View getSelectedView() {
		// TODO: implement
		return null;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		if (listAdapter != null) {
			listAdapter.unregisterDataSetObserver(mDataObserver);
		}
		listAdapter = adapter;
		listAdapter.registerDataSetObserver(mDataObserver);
		initView();
		removeAllViewsInLayout();
		requestLayout();
	}

	@Override
	public void setSelection(int position) {
		Log.d("park", "setSelection : " + position);
	}

	private void addAndMeasureChild(final View child, int viewPos) {

		LayoutParams params = child.getLayoutParams();
		int childWidth;
		int childWidthMeasureSpec;
		int childHeight;
		int childHeightMeasureSpec;

		if (params == null) {
			params = new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT);
		}

		childWidth = getWidth();
		childWidthMeasureSpec = MeasureSpec.AT_MOST;

		if (params.height == LayoutParams.WRAP_CONTENT) {
			childHeight = getHeight();
			childHeightMeasureSpec = MeasureSpec.AT_MOST;

		} else if (params.height == LayoutParams.FILL_PARENT) {
			childHeight = getHeight();
			childHeightMeasureSpec = MeasureSpec.EXACTLY;

		} else {
			childHeight = params.height;
			childHeightMeasureSpec = MeasureSpec.EXACTLY;
		}

		addViewInLayout(child, viewPos, params, true);
		child.measure(MeasureSpec.makeMeasureSpec(childWidth,
				childWidthMeasureSpec), MeasureSpec.makeMeasureSpec(
				childHeight, childHeightMeasureSpec));
	}

	@Override
	protected synchronized void onLayout(boolean changed, int left, int top,
			int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		if (listAdapter == null) {
			return;
		}

		if (dataChanged) {
			int oldCurrentX = currentX;
			initView();
			removeAllViewsInLayout();
			nextX = oldCurrentX;
			dataChanged = false;
		}

		if (scroller.computeScrollOffset()) {
			int scrollx = scroller.getCurrX();
			nextX = scrollx;
		}

		if (nextX < 0) {
			nextX = 0;
			scroller.forceFinished(true);
		}
		if (nextX > maxX) {
			nextX = maxX;
			scroller.forceFinished(true);
		}

		int dx = currentX - nextX;

		removeNonVisibleItems(dx);
		fillList(dx);
		positionItems(dx);

		currentX = nextX;

		isFinished = scroller.isFinished();

		if (!isFinished) {
			post(new Runnable() {
				@Override
				public void run() {
					requestLayout();
				}
			});

		} else {

			if (completeFinishAction && isFling == false) {
				return;
			}

			if (handler == null) {
				handler = new Handler();
			}

			removeFinishRunnableCallback();

			finished = new Runnable() {
				@Override
				public void run() {

					if (isFling) {
						return;
					}

					if (!isFinished) {
						handler.postDelayed(this, 100);
						return;
					}

					if (currentX == maxX) {
						return;
					}

					View child = getChildAt(0);
					if (child != null) {
						int halfWidth = child.getWidth() / 2;

						if (Math.abs(displayOffset) < halfWidth) {
							scrollTo(currentX + displayOffset, 500);
						} else {
							scrollTo(currentX
									+ (child.getWidth() + displayOffset), 500);
						}
					}

					completeFinishAction = true;
				}
			};
			handler.postDelayed(finished, 100);

			isFling = false;
		}
	}

	private void removeFinishRunnableCallback() {
		if (finished != null) {
			handler.removeCallbacks(finished);
			finished = null;
		}
	}

	private void fillList(final int dx) {
		int edge = 0;
		View child = getChildAt(getChildCount() - 1);
		if (child != null) {
			edge = child.getRight();
		}

		fillListRight(edge, dx);

		edge = 0;
		child = getChildAt(0);
		if (child != null) {
			edge = child.getLeft();
		}
		fillListLeft(edge, dx);

	}

	private void fillListRight(int rightEdge, final int dx) {

		if (isBounceEnabled) {
			while (rightEdge + dx < getWidth()
					&& rightViewIndex < listAdapter.getCount() + 2) {

				View child;
				if (rightViewIndex == 0) {
					child = getDummyButtonView();
				} else if (rightViewIndex == listAdapter.getCount() + 1) {
					child = getDummyButtonView();
				} else {
					child = listAdapter.getView(rightViewIndex - 1,
							removedViewQueue.poll(), this);
				}
				addAndMeasureChild(child, -1);
				rightEdge += child.getMeasuredWidth();

				if (rightViewIndex == listAdapter.getCount() + 1) {
					maxX = currentX + rightEdge - getWidth();
				}
				rightViewIndex++;
			}
			if (maxX < 0) {
				maxX = 0;
			}
		} else {
			while (rightEdge + dx < getWidth()
					&& rightViewIndex < listAdapter.getCount()) {
				View child = listAdapter.getView(rightViewIndex,
						removedViewQueue.poll(), this);
				addAndMeasureChild(child, -1);
				rightEdge += child.getMeasuredWidth();

				if (rightViewIndex == listAdapter.getCount() - 1) {
					maxX = currentX + rightEdge - getWidth();
				}

				rightViewIndex++;
			}

			if (maxX < 0) {
				maxX = 0;
			}
		}
	}

	private void fillListLeft(int leftEdge, final int dx) {

		if (isBounceEnabled) {
			while (leftEdge + dx > 0 && leftViewIndex >= 0) {
				View child;
				if (leftViewIndex == 0) {
					child = getDummyButtonView();
				} else {
					child = listAdapter.getView(leftViewIndex - 1,
							removedViewQueue.poll(), this);
				}
				addAndMeasureChild(child, 0);

				leftEdge -= child.getMeasuredWidth();
				leftViewIndex--;
				displayOffset -= child.getMeasuredWidth();
			}
		} else {
			while (leftEdge + dx > 0 && leftViewIndex >= 0) {
				View child = listAdapter.getView(leftViewIndex,
						removedViewQueue.poll(), this);
				addAndMeasureChild(child, 0);
				leftEdge -= child.getMeasuredWidth();
				leftViewIndex--;
				displayOffset -= child.getMeasuredWidth();
			}
		}
	}

	private Button getDummyButtonView() {
		Button v = new Button(context);
		LinearLayout.LayoutParams parm = new LinearLayout.LayoutParams(
				getDummyWidth(), LinearLayout.LayoutParams.WRAP_CONTENT);
		parm.setMargins(0, 0, 0, 0);
		v.setWidth((getWidth() / 2 - 5));
		v.setHeight(30);
		v.setVisibility(View.INVISIBLE);
		return v;
	}

	private int getDummyWidth() {
		return (getWidth() / 2 - 5);
	}

	private void removeNonVisibleItems(final int dx) {
		View child = getChildAt(0);
		while (child != null && child.getRight() + dx <= 0) {
			displayOffset += child.getMeasuredWidth();
			removedViewQueue.offer(child);
			removeViewInLayout(child);
			leftViewIndex++;
			child = getChildAt(0);

		}

		child = getChildAt(getChildCount() - 1);
		while (child != null && child.getLeft() + dx >= getWidth()) {
			removedViewQueue.offer(child);
			removeViewInLayout(child);
			rightViewIndex--;
			child = getChildAt(getChildCount() - 1);
		}
	}

	private void positionItems(final int dx) {
		if (getChildCount() > 0) {
			displayOffset += dx;
			int left = displayOffset;
			for (int i = 0; i < getChildCount(); i++) {
				View child = getChildAt(i);
				int childWidth = child.getMeasuredWidth();
				child.layout(left, 0, left + childWidth,
						child.getMeasuredHeight());
				left += childWidth;
			}
		}
	}

	public synchronized void scrollTo(int x) {
		scroller.startScroll(nextX, 0, x - nextX, 0);
		requestLayout();
	}

	public synchronized void scrollTo(int x, int duration) {
		scroller.startScroll(nextX, 0, x - nextX, 0, duration);
		requestLayout();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		boolean handled = gesture.onTouchEvent(ev);

		if (ev.getAction() == 1) {
			if (action == 0 || Math.abs(ev.getX() - x) < 10) {
				super.dispatchTouchEvent(ev);
			} else {
				ev.setAction(MotionEvent.ACTION_CANCEL);
				super.dispatchTouchEvent(ev);
			}
		} else if (ev.getAction() == 0) {
			super.dispatchTouchEvent(ev);
			x = ev.getX();
		}

		action = ev.getAction();

		return handled;
	}

	protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {

		synchronized (NCHorizontalListView.this) {
			scroller.fling(nextX, 0, (int)-velocityX, 0, 0, maxX, 0, 0);
		}
		requestLayout();

		return true;
	}

	protected boolean onDown(MotionEvent e) {
		scroller.forceFinished(true);
		return true;
	}

	private OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {

		@Override
		public boolean onDown(MotionEvent e) {
			return NCHorizontalListView.this.onDown(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			isFling = true;
			return NCHorizontalListView.this.onFling(e1, e2, velocityX,
					velocityY);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {

			completeFinishAction = false;

			synchronized (NCHorizontalListView.this) {
				nextX += (int)distanceX;
			}
			requestLayout();

			return true;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {

			Rect viewRect = new Rect();
			for (int i = 0; i < getChildCount(); i++) {
				View child = getChildAt(i);
				int left = child.getLeft();
				int right = child.getRight();
				int top = child.getTop();
				int bottom = child.getBottom();
				viewRect.set(left, top, right, bottom);
				if (viewRect.contains((int)e.getX(), (int)e.getY())) {
					if (onItemClickListener != null) {
						onItemClickListener.onItemClick(NCHorizontalListView.this,
								child, leftViewIndex + 1 + i, 0);
					}
					if (onItemSelected != null) {
						onItemSelected.onItemSelected(
								NCHorizontalListView.this, child, leftViewIndex
										+ 1 + i, 0);
					}
					break;
				}

			}
			return true;
		}

	};

	public void smoothScrollToFirstView() {
		scrollTo(0, 500);
	}
}