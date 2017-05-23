package com.hitomi.smlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import org.appcelerator.kroll.KrollDict;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by hitomi on 2016/9/18. <br/>
 *
 * github : https://github.com/Hitomis <br/>
 *
 * email : 196425254@qq.com
 */
public class SpinMenu extends FrameLayout {
	static final String TAG = "SpinMenu";
	static final String TAG_ITEM_CONTAINER = "tag_item_container";
	static final String TAG_ITEM_PAGER = "tag_item_pager";
	static final String TAG_ITEM_HINT = "tag_item_hint";
	static final int MENU_STATE_CLOSE = -2;
	static final int MENU_STATE_CLOSED = -1;
	static final int MENU_STATE_OPEN = 1;
	static final int MENU_STATE_OPENED = 2;

	/**
	 * The distance between the moving items
	 */
	static final float TRAN_SKNEW_VALUE = 160;

	/**
	 * Hint is the top margin of the page
	 */
	static final int HINT_TOP_MARGIN = 15;

	/**
	 * Rotate, rotate the layout
	 */
	private SpinMenuLayout spinMenuLayout;

	/**
	 * The menu opens to close the animation help class
	 */
	private SpinMenuAnimator spinMenuAnimator;

	/**
	 * Page adapter
	 */
	private PagerAdapter pagerAdapter;

	/**
	 * Gesture recognizer
	 */
	private GestureDetectorCompat menuDetector;

	/**
	 * Menu status change listener
	 */
	private OnSpinMenuStateChangeListener onSpinMenuStateChangeListener;

	/**
	 * Cache the collection of Fragments for {@link #pagerAdapter} Recycling use
	 */
	private List pagerObjects;

	/**
	 * Menu item collection
	 */
	private List<SMItemLayout> smItemLayoutList;

	/**
	 * Page title character set
	 */
	private List<String> hintStrList;

	/**
	 * Page title character size
	 */
	private int hintTextSize = 14;

	/**
	 * 页面标题字符颜色
	 */
	private int hintTextColor = Color.parseColor("#666666");

	/**
	 * The default ratio of the page when the menu is opened
	 */
	private float scaleRatio = .36f;

	/**
	 * Whether the control is initializing the tag variable
	 */
	private boolean init = true;

	/**
	 * Whether gesture recognition is enabled
	 */
	private boolean enableGesture;

	/**
	 * The current menu status, which is turned off by default
	 */
	private int menuState = MENU_STATE_CLOSED;

	/**
	 * The threshold between sliding and touch
	 */
	private int touchSlop = 8;

	private OnSpinSelectedListener onSpinSelectedListener = new OnSpinSelectedListener() {
		@Override
		public void onSpinSelected(int position) {
			log("SpinMenu position:" + position);
		}
	};

	private OnMenuSelectedListener onMenuSelectedListener = new OnMenuSelectedListener() {
		@Override
		public void onMenuSelected(SMItemLayout smItemLayout) {
			closeMenu(smItemLayout);
		}
	};

	private GestureDetector.SimpleOnGestureListener menuGestureListener = new GestureDetector.SimpleOnGestureListener() {

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if (Math.abs(distanceX) < touchSlop && distanceY < -touchSlop * 3) {
				openMenu();
			}
			return true;
		}
	};

	public SpinMenu(Context context) {
		this(context, new KrollDict());
	}

	public SpinMenu(Context context, KrollDict attrs) {
		this(context, attrs, 0);
	}

	public SpinMenu(Context context, KrollDict attrs, int defStyleAttr) {
		super(context, null, defStyleAttr);
		/*
		 * TypedArray typedArray = context.obtainStyledAttributes(attrs,
		 * R.styleable.SpinMenu); scaleRatio =
		 * typedArray.getFloat(R.styleable.SpinMenu_scale_ratio, scaleRatio);
		 * hintTextSize =
		 * typedArray.getDimensionPixelSize(R.styleable.SpinMenu_hint_text_size,
		 * hintTextSize); hintTextSize = px2Sp(hintTextColor); hintTextColor =
		 * typedArray.getColor(R.styleable.SpinMenu_hint_text_color,
		 * hintTextColor); typedArray.recycle();
		 */
		pagerObjects = new ArrayList();
		smItemLayoutList = new ArrayList<>();
		menuDetector = new GestureDetectorCompat(context, menuGestureListener);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
			ViewConfiguration conf = ViewConfiguration.get(getContext());
			touchSlop = conf.getScaledTouchSlop();
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		@IdRes
		final int smLayoutId = 0x6F060505;
		ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
				MATCH_PARENT, MATCH_PARENT);
		spinMenuLayout = new SpinMenuLayout(getContext());
		spinMenuLayout.setId(smLayoutId);
		spinMenuLayout.setLayoutParams(layoutParams);
		spinMenuLayout.setOnSpinSelectedListener(onSpinSelectedListener);
		spinMenuLayout.setOnMenuSelectedListener(onMenuSelectedListener);
		addView(spinMenuLayout);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		if (init && smItemLayoutList.size() > 0) {
			// 根据 scaleRatio 去调整菜单中 item 视图的整体大小
			int pagerWidth = (int) (getMeasuredWidth() * scaleRatio);
			int pagerHeight = (int) (getMeasuredHeight() * scaleRatio);
			SMItemLayout.LayoutParams containerLayoutParams = new SMItemLayout.LayoutParams(
					pagerWidth, pagerHeight);
			SMItemLayout smItemLayout;
			FrameLayout frameContainer;
			TextView tvHint;
			for (int i = 0; i < smItemLayoutList.size(); i++) {
				smItemLayout = smItemLayoutList.get(i);
				frameContainer = (FrameLayout) smItemLayout
						.findViewWithTag(TAG_ITEM_CONTAINER);
				frameContainer.setLayoutParams(containerLayoutParams);
				if (i == 0) { // 初始菜单的时候，默认显示第一个 Fragment
					FrameLayout pagerLayout = (FrameLayout) smItemLayout
							.findViewWithTag(TAG_ITEM_PAGER);
					// 先移除第一个包含 Fragment 的布局
					frameContainer.removeView(pagerLayout);
					// 创建一个用来占位的 FrameLayout
					FrameLayout holderLayout = new FrameLayout(getContext());
					LinearLayout.LayoutParams pagerLinLayParams = new LinearLayout.LayoutParams(
							MATCH_PARENT, MATCH_PARENT);
					holderLayout.setLayoutParams(pagerLinLayParams);

					// 将占位的 FrameLayout 添加到布局中的 frameContainer 中
					frameContainer.addView(holderLayout, 0);

					// 添加 第一个包含 Fragment 的布局添加到 SpinMenu 中
					FrameLayout.LayoutParams pagerFrameParams = new FrameLayout.LayoutParams(
							MATCH_PARENT, MATCH_PARENT);
					pagerLayout.setLayoutParams(pagerFrameParams);
					addView(pagerLayout);
				}

				// 位于菜单中当前显示 Fragment 两边的 SMItemlayout 左右移动 TRAN_SKNEW_VALUE 个距离
				if (spinMenuLayout.getSelectedPosition() + 1 == i
						|| (spinMenuLayout.isCyclic() && spinMenuLayout
								.getMenuItemCount() - i == spinMenuLayout
								.getSelectedPosition() + 1)) { // 右侧 ItemMenu
					smItemLayout.setTranslationX(TRAN_SKNEW_VALUE);
				} else if (spinMenuLayout.getSelectedPosition() - 1 == i
						|| (spinMenuLayout.isCyclic() && spinMenuLayout
								.getMenuItemCount() - i == 1)) { // 左侧 ItemMenu
					smItemLayout.setTranslationX(-TRAN_SKNEW_VALUE);
				} else {
					smItemLayout.setTranslationX(0);
				}
			}
			spinMenuAnimator = new SpinMenuAnimator(this, spinMenuLayout,
					onSpinMenuStateChangeListener);
			init = false;
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (enableGesture)
			menuDetector.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (enableGesture) {
			menuDetector.onTouchEvent(event);
			return true;
		} else {
			return super.onTouchEvent(event);
		}
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位转成为 sp
	 * 
	 * @param pxValue
	 * @return
	 */
	private int px2Sp(float pxValue) {
		final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	private void log(String log) {
		Log.d(TAG, log);
	}

	public void setFragmentAdapter(PagerAdapter adapter) {
		if (pagerAdapter != null) {
			pagerAdapter.startUpdate(spinMenuLayout);
			for (int i = 0; i < adapter.getCount(); i++) {
				ViewGroup pager = (ViewGroup) spinMenuLayout.getChildAt(i)
						.findViewWithTag(TAG_ITEM_PAGER);
				pagerAdapter.destroyItem(pager, i, pagerObjects.get(i));
			}
			pagerAdapter.finishUpdate(spinMenuLayout);
		}

		int pagerCount = adapter.getCount();
		if (pagerCount > spinMenuLayout.getMaxMenuItemCount())
			throw new RuntimeException(String.format(
					"Fragment number can't be more than %d",
					spinMenuLayout.getMaxMenuItemCount()));

		pagerAdapter = adapter;

		SMItemLayout.LayoutParams itemLinLayParams = new SMItemLayout.LayoutParams(
				WRAP_CONTENT, WRAP_CONTENT);
		LinearLayout.LayoutParams containerLinlayParams = new LinearLayout.LayoutParams(
				MATCH_PARENT, MATCH_PARENT);
		FrameLayout.LayoutParams pagerFrameParams = new FrameLayout.LayoutParams(
				MATCH_PARENT, MATCH_PARENT);
		LinearLayout.LayoutParams hintLinLayParams = new LinearLayout.LayoutParams(
				WRAP_CONTENT, WRAP_CONTENT);
		hintLinLayParams.topMargin = HINT_TOP_MARGIN;
		pagerAdapter.startUpdate(spinMenuLayout);
		for (int i = 0; i < pagerCount; i++) {
			// 创建菜单父容器布局
			SMItemLayout smItemLayout = new SMItemLayout(getContext());
			smItemLayout.setId(i + 1);
			smItemLayout.setGravity(Gravity.CENTER);
			smItemLayout.setLayoutParams(itemLinLayParams);

			// 创建包裹FrameLayout
			FrameLayout frameContainer = new FrameLayout(getContext());
			frameContainer.setId(pagerCount + i + 1);
			frameContainer.setTag(TAG_ITEM_CONTAINER);
			frameContainer.setLayoutParams(containerLinlayParams);

			// 创建 Fragment 容器
			FrameLayout framePager = new FrameLayout(getContext());
			framePager.setId(pagerCount * 2 + i + 1);
			framePager.setTag(TAG_ITEM_PAGER);
			framePager.setLayoutParams(pagerFrameParams);
			Object object = pagerAdapter.instantiateItem(framePager, i);

			// 创建菜单标题 TextView
			TextView tvHint = new TextView(getContext());
			tvHint.setId(pagerCount * 3 + i + 1);
			tvHint.setTag(TAG_ITEM_HINT);
			tvHint.setLayoutParams(hintLinLayParams);

			frameContainer.addView(framePager);
			smItemLayout.addView(frameContainer);
			smItemLayout.addView(tvHint);
			spinMenuLayout.addView(smItemLayout);

			pagerObjects.add(object);
			smItemLayoutList.add(smItemLayout);
		}
		pagerAdapter.finishUpdate(spinMenuLayout);
	}

	public void openMenu() {
		if (menuState == MENU_STATE_CLOSED) {
			spinMenuAnimator.openMenuAnimator();
		}
	}

	public void closeMenu(SMItemLayout chooseItemLayout) {
		if (menuState == MENU_STATE_OPENED) {
			spinMenuAnimator.closeMenuAnimator(chooseItemLayout);
		}
	}

	public int getMenuState() {
		return menuState;
	}

	public void updateMenuState(int state) {
		menuState = state;
	}

	public void setEnableGesture(boolean enable) {
		enableGesture = enable;
	}

	public void setMenuItemScaleValue(float scaleValue) {
		scaleRatio = scaleValue;
	}

	public void setHintTextSize(int textSize) {
		hintTextSize = textSize;
	}

	public void setHintTextColor(int textColor) {
		hintTextColor = textColor;
	}

	public void setHintTextStrList(List<String> hintTextList) {
		hintStrList = hintTextList;
	}

	public void setOnSpinMenuStateChangeListener(
			OnSpinMenuStateChangeListener listener) {
		onSpinMenuStateChangeListener = listener;
	}

	public float getScaleRatio() {
		return scaleRatio;
	}
}
