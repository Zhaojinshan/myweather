package com.myweather.app.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class SlidingMenu extends HorizontalScrollView {

	// �������뼰����
	private float xDistance, yDistance, xLast, yLast;

	private LinearLayout mWapper;
	private ViewGroup mMenu;
	private ViewGroup mContent;
	/**
	 * ��Ļ�Ŀ��
	 */
	private int mScreenWidth;
	/**
	 * �˵��Ŀ��
	 */
	private int mMenuWidth;
	/**
	 * �˵����Ҳ���
	 */
	private int mMenuRightPadding = 50;
	private boolean once;

	private boolean isOpen;

	public SlidingMenu(Context context, AttributeSet attrs) {
		super(context, attrs);

		// ��ȡ��Ļ���ֵ
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		mScreenWidth = outMetrics.widthPixels;

		// ��dpת��Ϊpx
		mMenuRightPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
				context.getResources().getDisplayMetrics());
	}

	/**
	 * ������view�Ŀ�͸� �����Լ��Ŀ�͸�
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (!once) {
			// ��ʼ���Ӳ���
			mWapper = (LinearLayout) getChildAt(0);
			mMenu = (ViewGroup) mWapper.getChildAt(0);
			mContent = (ViewGroup) mWapper.getChildAt(1);

			mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth - mMenuRightPadding;
			mContent.getLayoutParams().width = mScreenWidth;
			once = true;

		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * ͨ������ƫ��������menu����
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed) {
			this.scrollTo(mMenuWidth, 0);
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_UP:
			// ��������߲˵��Ŀ��
			int scrollX = getScrollX();

			int x = (int) ev.getX();
			if (scrollX > mMenuWidth / 2) {
				this.smoothScrollTo(mMenuWidth, 0);
				isOpen = false;
			} else if (x > mMenuWidth) {
				this.smoothScrollTo(mMenuWidth, 0);
				isOpen = false;
			} else if (scrollX <= mMenuWidth / 2) {
				this.smoothScrollTo(0, 0);
				isOpen = true;
			}
			return true;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * �򿪲˵�
	 */
	public void openMenu() {
		if (isOpen)
			return;
		this.smoothScrollTo(0, 0);
		isOpen = true;
	}

	/**
	 * �رղ˵�
	 */
	public void closeMenu() {
		if (!isOpen)
			return;
		this.smoothScrollTo(mMenuWidth, 0);
		isOpen = false;
	}

	/**
	 * �л��˵�
	 */
	public void toggle() {
		if (isOpen) {
			closeMenu();
		} else {
			openMenu();
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDistance = yDistance = 0f;
			xLast = ev.getX();
			yLast = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float curX = ev.getX();
			final float curY = ev.getY();

			xDistance += Math.abs(curX - xLast);
			yDistance += Math.abs(curY - yLast);
			xLast = curX;
			yLast = curY;

			if (xDistance > yDistance) {
				return false;
			}
		}
		return super.onInterceptTouchEvent(ev);
	}

}
