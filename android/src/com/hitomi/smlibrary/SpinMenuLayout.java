package com.hitomi.smlibrary;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by hitomi on 2016/9/13. <br/>
 *
 * github : https://github.com/Hitomis <br/>
 *
 * email : 196425254@qq.com
 */
public class SpinMenuLayout extends ViewGroup implements Runnable,
		View.OnClickListener {

	/**
	 * View between the angle of the interval
	 */
	private static final int ANGLE_SPACE = 45;

	/**
	 * View The speed of the minimum rotation angle when rotating
	 */
	private static final int MIN_PER_ANGLE = ANGLE_SPACE;

	/**
	 * For automatic scrolling speed, no other meaning
	 */
	private static final float ACCELERATE_ANGLE_RATIO = 1.8f;

	/**
	 * Used to extend the radius, no other meaning
	 */
	private static final float RADIUS_HALF_WIDTH_RATIO = 1.2f;

	/**
	 * When the rotation angle is outside the rotatable range, the delay ratio
	 * of the rotation angle
	 */
	private static final float DELAY_ANGLE_RATIO = 5.6f;

	/**
	 * Click and drag the switch threshold
	 */
	private final int touchSlopAngle = 2;

	/**
	 * Minimum and maximum inertia scrolling angle values [-(getChildCount() -
	 * 1) * ANGLE_SPACE, 0]
	 */
	private int minFlingAngle, maxFlingAngle;

	/**
	 * delayAngle: 当前转动的总角度值， perAngle：每次转动的角度值
	 */
	private float delayAngle, perAngle;

	/**
	 * Radius: from the bottom to the midpoint of Child height
	 */
	private float radius;

	/**
	 * Each time the finger is pressed, the coordinate value
	 */
	private float preX, preY;

	/**
	 * The speed of each turn
	 */
	private float anglePerSecond;

	/**
	 * The time value of each finger press
	 */
	private long preTimes;

	/**
	 * Whether it can cycle scrolling
	 */
	private boolean isCyclic;

	/**
	 * Whether it is allowed to turn the menu
	 */
	private boolean enable;

	private Scroller scroller;

	private OnSpinSelectedListener onSpinSelectedListener;

	private OnMenuSelectedListener onMenuSelectedListener;

	public SpinMenuLayout(Context context) {
		this(context, null);
	}

	public SpinMenuLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SpinMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		scroller = new Scroller(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		// The width and height are consistent with the parent container
		ViewGroup parent = ((ViewGroup) getParent());
		int measureWidth = parent.getMeasuredWidth();
		int measureHeight = parent.getMeasuredHeight();
		setMeasuredDimension(measureWidth, measureHeight);

		if (getChildCount() > 0) {
			// Measure the child elements
			measureChildren(widthMeasureSpec, heightMeasureSpec);
		}
	}

	@Override
	protected void onLayout(boolean b, int left, int top, int right, int bottom) {
		final int childCount = getChildCount();
		if (childCount <= 0)
			return;

		isCyclic = getChildCount() == 360 / MIN_PER_ANGLE;
		computeFlingLimitAngle();

		delayAngle %= 360.f;
		float startAngle = delayAngle;

		View child;
		int childWidth, childHeight;
		int centerX = getMeasuredWidth() / 2;
		int centerY = getMeasuredHeight();
		radius = centerX * RADIUS_HALF_WIDTH_RATIO
				+ getChildAt(1).getMeasuredHeight() / 2;

		for (int i = 0; i < childCount; i++) {
			child = getChildAt(i);
			childWidth = child.getMeasuredWidth();
			childHeight = child.getMeasuredHeight();

			left = (int) (centerX + Math.sin(Math.toRadians(startAngle))
					* radius);
			top = (int) (centerY - Math.cos(Math.toRadians(startAngle))
					* radius);

			child.layout(left - childWidth / 2, top - childHeight / 2, left
					+ childWidth / 2, top + childHeight / 2);

			child.setOnClickListener(this);
			child.setRotation(startAngle);
			startAngle += ANGLE_SPACE;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (!enable)
			return super.dispatchTouchEvent(ev);
		float curX = ev.getX();
		float curY = ev.getY();

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			preX = curX;
			preY = curY;
			preTimes = System.currentTimeMillis();
			perAngle = 0;

			if (!scroller.isFinished()) {
				scroller.abortAnimation();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			float diffX = curX - preX;
			float start = computeAngle(preX, preY);
			float end = computeAngle(curX, curY);

			float perDiffAngle;
			if (diffX > 0) {
				perDiffAngle = Math.abs(start - end);
			} else {
				perDiffAngle = -Math.abs(end - start);
			}
			if (!isCyclic
					&& (delayAngle < minFlingAngle || delayAngle > maxFlingAngle)) {
				// When the current is not a circular rolling mode, and the
				// angle of rotation is beyond the range of the rotatable angle
				perDiffAngle /= DELAY_ANGLE_RATIO;
			}
			delayAngle += perDiffAngle;
			perAngle += perDiffAngle;

			preX = curX;
			preY = curY;
			requestLayout();
			break;
		case MotionEvent.ACTION_UP:
			anglePerSecond = perAngle * 1000
					/ (System.currentTimeMillis() - preTimes);
			int startAngle = (int) delayAngle;
			if (Math.abs(anglePerSecond) > MIN_PER_ANGLE
					&& startAngle >= minFlingAngle
					&& startAngle <= maxFlingAngle) {
				scroller.fling(startAngle, 0,
						(int) (anglePerSecond * ACCELERATE_ANGLE_RATIO), 0,
						minFlingAngle, maxFlingAngle, 0, 0);
				scroller.setFinalX(scroller.getFinalX()
						+ computeDistanceToEndAngle(scroller.getFinalX()
								% ANGLE_SPACE));
			} else {
				scroller.startScroll(startAngle, 0,
						computeDistanceToEndAngle(startAngle % ANGLE_SPACE), 0,
						300);
			}

			if (!isCyclic) { // When not rotating, you need to correct the angle
				if (scroller.getFinalX() >= maxFlingAngle) {
					scroller.setFinalX(maxFlingAngle);
				} else if (scroller.getFinalX() <= minFlingAngle) {
					scroller.setFinalX(minFlingAngle);
				}
			}
			// post一个任务，自动滚动
			post(this);
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * Calculate the minimum and maximum inertia scrolling angles
	 */
	private void computeFlingLimitAngle() {
		// Since the center point is at the midpoint of the bottom (the
		// coordinate system is opposite), the min and max are calculated from
		// the opposite
		minFlingAngle = isCyclic ? Integer.MIN_VALUE : -ANGLE_SPACE
				* (getChildCount() - 1);
		maxFlingAngle = isCyclic ? Integer.MAX_VALUE : 0;
	}

	/**
	 * Calculate the angle of rotation based on the current touch point
	 * coordinates
	 * 
	 * @param xTouch
	 * @param yTouch
	 * @return
	 */
	private float computeAngle(float xTouch, float yTouch) {
		// he center point is at the midpoint of the bottom edge and is
		// converted to the corresponding coordinate x, y according to the
		// center point
		float x = Math.abs(xTouch - getMeasuredWidth() / 2);
		float y = Math.abs(getMeasuredHeight() - yTouch);
		return (float) (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
	}

	/**
	 * Calculates the angle value at the end of automatic scrolling
	 * 
	 * @param remainder
	 * @return
	 */
	private int computeDistanceToEndAngle(int remainder) {
		int endAngle;
		if (remainder > 0) {
			if (Math.abs(remainder) > ANGLE_SPACE / 2) {
				if (perAngle < 0) { // Counterclockwise
					endAngle = ANGLE_SPACE - remainder;
				} else { // Clockwise
					endAngle = ANGLE_SPACE - Math.abs(remainder);
				}
			} else {
				endAngle = -remainder;
			}
		} else {
			if (Math.abs(remainder) > ANGLE_SPACE / 2) {
				if (perAngle < 0) {
					endAngle = -ANGLE_SPACE - remainder;
				} else {
					endAngle = Math.abs(remainder) - ANGLE_SPACE;
				}
			} else {
				endAngle = -remainder;
			}
		}
		return endAngle;
	}

	private int computeClickToEndAngle(int clickIndex, int currSelPos) {
		int endAngle;
		if (isCyclic) {
			clickIndex = clickIndex == 0
					&& currSelPos == getMenuItemCount() - 1 ? getMenuItemCount()
					: clickIndex;
			currSelPos = currSelPos == 0 && clickIndex != 1 ? getMenuItemCount()
					: currSelPos;
		}
		endAngle = (currSelPos - clickIndex) * ANGLE_SPACE;
		return endAngle;
	}

	@Override
	public void run() {
		if (scroller.isFinished()) {
			int position = Math.abs(scroller.getCurrX() / ANGLE_SPACE);
			if (onSpinSelectedListener != null) {
				onSpinSelectedListener.onSpinSelected(position);
			}
		}
		if (scroller.computeScrollOffset()) {
			delayAngle = scroller.getCurrX();
			postDelayed(this, 16);
			requestLayout();
		}
	}

	@Override
	public void onClick(View view) {
		int index = indexOfChild(view);
		int selPos = getSelectedPosition();
		if (Math.abs(perAngle) <= touchSlopAngle) {
			if (index != selPos) {
				// The current click on the left and right of a Item, then click
				// the Item of the item to scroll to select the [middle]
				// position
				scroller.startScroll(-getSelectedPosition() * ANGLE_SPACE, 0,
						computeClickToEndAngle(index, selPos), 0, 300);
				post(this);
			} else {
				if (view instanceof SMItemLayout
						&& onMenuSelectedListener != null && enable) {
					onMenuSelectedListener.onMenuSelected((SMItemLayout) view);
				}

			}
		}
	}

	/**
	 * Gets the currently selected location
	 * 
	 * @return
	 */
	public int getSelectedPosition() {
		if (scroller.getFinalX() > 0) {
			return (360 - scroller.getFinalX()) / ANGLE_SPACE;
		} else {
			return (Math.abs(scroller.getFinalX())) / ANGLE_SPACE;
		}
	}

	/**
	 * Gets the true radius of the circular rotation menu<br/>
	 * he radius is based child 的高度加上 SpinMenuLayout 的宽度的一半<br/>
	 * So when there is no child: The radius value is -1
	 * 
	 * @return
	 */
	public int getRealRadius() {
		if (getChildCount() > 0) {
			return getMeasuredWidth() / 2 + getChildAt(0).getHeight();
		} else {
			return -1;
		}
	}

	public int getMaxMenuItemCount() {
		return 360 / ANGLE_SPACE;
	}

	public int getMenuItemCount() {
		return getChildCount();
	}

	public boolean isCyclic() {
		return isCyclic;
	}

	public void postEnable(boolean isEnable) {
		enable = isEnable;
	}

	public void setOnSpinSelectedListener(OnSpinSelectedListener listener) {
		onSpinSelectedListener = listener;
	}

	public void setOnMenuSelectedListener(OnMenuSelectedListener listener) {
		onMenuSelectedListener = listener;
	}
}
