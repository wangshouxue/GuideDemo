package com.example.sxguide;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import androidx.annotation.AnimatorRes;
import androidx.annotation.DrawableRes;
import androidx.viewpager.widget.ViewPager;

/**
 * @auther：wangshouxue
 * @qq:1556090086@qq.com
 * @date： 2019/10/29 10:26
 * @description：类作用描述
 */
public class CircleIndicator extends LinearLayout implements ViewPager.OnPageChangeListener {
    private final static int DEFAULT_INDICATOR_WIDTH = 5;
    private ViewPager mViewpager;
    private int mIndicatorMargin = -1;
    private int mIndicatorWidth = -1;
    private int mIndicatorHeight = -1;
    private int mAnimatorResId = R.anim.scale_with_alpha;
    private int mAnimatorReverseResId = 0;
    private int mIndicatorBackgroundResId = R.mipmap.white_circle;
    private int mIndicatorUnselectedBackgroundResId = R.mipmap.white_circle;
    private Drawable mIndicatorBackgroundDrawable;
    private Drawable mIndicatorUnselectedBackgroundDrawable;
    private int mCurrentPosition = 0;
    private float mUnselectedIndicatorAlpha = 1.0f;
    private float mSelectedIndicatorAlpha = 1.0f;
    private Animator mAnimationOut;
    private Animator mAnimationIn;

    public CircleIndicator(Context context) {
        super(context);
        init(context, null);
    }

    public CircleIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER);
        handleTypedArray(context, attrs);
        checkIndicatorConfig(context);
    }

    private void handleTypedArray(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleIndicator);
        mIndicatorWidth =
                typedArray.getDimensionPixelSize(R.styleable.CircleIndicator_ci_width, -1);
        mIndicatorHeight =
                typedArray.getDimensionPixelSize(R.styleable.CircleIndicator_ci_height, -1);
        mIndicatorMargin =
                typedArray.getDimensionPixelSize(R.styleable.CircleIndicator_ci_margin, -1);
        mUnselectedIndicatorAlpha =
                typedArray.getFloat(R.styleable.CircleIndicator_ci_alpha_unselected, 1.0f);
        mSelectedIndicatorAlpha =
                typedArray.getFloat(R.styleable.CircleIndicator_ci_alpha_selected, 1.0f);
        mAnimatorResId = typedArray.getResourceId(R.styleable.CircleIndicator_ci_animator,
                R.anim.scale_with_alpha);
        mAnimatorReverseResId =
                typedArray.getResourceId(R.styleable.CircleIndicator_ci_animator_reverse, 0);
        mIndicatorBackgroundResId =
                typedArray.getResourceId(R.styleable.CircleIndicator_ci_drawable,
                        R.mipmap.white_circle);
        mIndicatorUnselectedBackgroundResId =
                typedArray.getResourceId(R.styleable.CircleIndicator_ci_drawable_unselected,
                        mIndicatorBackgroundResId);
        typedArray.recycle();
    }

    /**
     * Create and configure Indicator in Java code.
     */
    @SuppressLint("ResourceType")
    public void configureIndicator(int indicatorWidth, int indicatorHeight, int indicatorMargin) {
        configureIndicator(indicatorWidth, indicatorHeight, indicatorMargin,
                R.anim.scale_with_alpha, 0, R.mipmap.white_circle, R.mipmap.white_circle);
    }

    public void configureIndicator(int indicatorWidth, int indicatorHeight, int indicatorMargin,
                                   @AnimatorRes int animatorId, @AnimatorRes int animatorReverseId,
                                   @DrawableRes int indicatorBackgroundId,
                                   @DrawableRes int indicatorUnselectedBackgroundId) {

        mIndicatorWidth = indicatorWidth;
        mIndicatorHeight = indicatorHeight;
        mIndicatorMargin = indicatorMargin;

        mAnimatorResId = animatorId;
        mAnimatorReverseResId = animatorReverseId;
        mIndicatorBackgroundResId = indicatorBackgroundId;
        mIndicatorUnselectedBackgroundResId = indicatorUnselectedBackgroundId;

        checkIndicatorConfig(getContext());
    }

    @SuppressLint("ResourceType")
    private void checkIndicatorConfig(Context context) {
        mIndicatorWidth = (mIndicatorWidth < 0) ? dip2px(DEFAULT_INDICATOR_WIDTH) : mIndicatorWidth;
        mIndicatorHeight =
                (mIndicatorHeight < 0) ? dip2px(DEFAULT_INDICATOR_WIDTH) : mIndicatorHeight;
        mIndicatorMargin =
                (mIndicatorMargin < 0) ? dip2px(DEFAULT_INDICATOR_WIDTH) : mIndicatorMargin;

        mAnimatorResId = (mAnimatorResId == 0) ? R.anim.scale_with_alpha : mAnimatorResId;
        mAnimationOut = AnimatorInflater.loadAnimator(context, mAnimatorResId);
        if (mAnimatorReverseResId == 0) {
            mAnimationIn = AnimatorInflater.loadAnimator(context, mAnimatorResId);
            mAnimationIn.setInterpolator(new ReverseInterpolator());
        } else {
            mAnimationIn = AnimatorInflater.loadAnimator(context, mAnimatorReverseResId);
        }
        mIndicatorBackgroundResId = (mIndicatorBackgroundResId == 0) ? R.mipmap.white_circle
                : mIndicatorBackgroundResId;
        mIndicatorUnselectedBackgroundResId =
                (mIndicatorUnselectedBackgroundResId == 0) ? mIndicatorBackgroundResId
                        : mIndicatorUnselectedBackgroundResId;
    }

    public void setViewPager(ViewPager viewPager) {
        mViewpager = viewPager;
        mCurrentPosition = mViewpager.getCurrentItem();
        createIndicators(viewPager);
        mViewpager.removeOnPageChangeListener(this);
        mViewpager.addOnPageChangeListener(this);
        onPageSelected(mCurrentPosition);
    }


    /**
     * @deprecated User ViewPager addOnPageChangeListener
     */
    @Deprecated
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        if (mViewpager == null) {
            throw new NullPointerException("can not find Viewpager , setViewPager first");
        }
        mViewpager.removeOnPageChangeListener(onPageChangeListener);
        mViewpager.addOnPageChangeListener(onPageChangeListener);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {

        if (mViewpager.getAdapter() == null || mViewpager.getAdapter().getCount() <= 0) {
            return;
        }

        if (mAnimationIn.isRunning()) mAnimationIn.end();
        if (mAnimationOut.isRunning()) mAnimationOut.end();

        View currentIndicator = getChildAt(mCurrentPosition);
        setIndicatorBackground(currentIndicator, false);
        mAnimationIn.setTarget(currentIndicator);
        mAnimationIn.start();

        View selectedIndicator = getChildAt(position);
        selectedIndicator.setAlpha(mSelectedIndicatorAlpha);
        setIndicatorBackground(selectedIndicator, true);
        mAnimationOut.setTarget(selectedIndicator);
        mAnimationOut.start();

        mCurrentPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public void setIndicatorBackgroundDrawable(Drawable mIndicatorBackgroundDrawable) {
        this.mIndicatorBackgroundDrawable = mIndicatorBackgroundDrawable;
    }

    public void setIndicatorUnselectedBackgroundDrawable(Drawable mIndicatorUnselectedBackgroundDrawable) {
        this.mIndicatorUnselectedBackgroundDrawable = mIndicatorUnselectedBackgroundDrawable;
    }

    private void createIndicators(ViewPager viewPager) {
        removeAllViews();
        if (viewPager.getAdapter() == null) {
            return;
        }

        int count = viewPager.getAdapter().getCount();
        if (count <= 0) {
            return;
        }
        addIndicator(true, mAnimationOut);
        for (int i = 1; i < count; i++) {
            addIndicator(false, mAnimationIn);
        }
    }

    private void addIndicator(boolean selected, Animator animator) {
        if (animator.isRunning()) animator.end();

        View Indicator = new View(getContext());
        setIndicatorBackground(Indicator, selected);

        Indicator.setAlpha(mUnselectedIndicatorAlpha);
        addView(Indicator, mIndicatorWidth, mIndicatorHeight);
        LayoutParams lp = (LayoutParams) Indicator.getLayoutParams();
        lp.leftMargin = mIndicatorMargin;
        lp.rightMargin = mIndicatorMargin;
        Indicator.setLayoutParams(lp);


        animator.setTarget(Indicator);
        animator.start();
    }

    private void setIndicatorBackground(View indicator, boolean selected) {
        if (selected) {
            if (mIndicatorBackgroundDrawable != null) {
                indicator.setBackground(mIndicatorBackgroundDrawable);
            } else {
                indicator.setBackgroundResource(mIndicatorBackgroundResId);
            }
        } else {
            if (mIndicatorUnselectedBackgroundDrawable != null) {
                indicator.setBackground(mIndicatorUnselectedBackgroundDrawable);
            } else {
                indicator.setBackgroundResource(mIndicatorUnselectedBackgroundResId);
            }
        }
    }

    private class ReverseInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float value) {
            return Math.abs(1.0f - value);
        }
    }

    public int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setViewPager1(ViewPager viewPager) {
        mViewpager = viewPager;
        mCurrentPosition = mViewpager.getCurrentItem();
        createIndicators1(viewPager);
        mViewpager.removeOnPageChangeListener(this);
        mViewpager.addOnPageChangeListener(this);
        onPageSelected(mCurrentPosition);
    }

    private void createIndicators1(ViewPager viewPager) {
        removeAllViews();
        if (viewPager.getAdapter() == null) {
            return;
        }

        int count = viewPager.getAdapter().getCount();
        if (count <= 0) {
            return;
        }
        for (int i = 0; i < count; i++) {
            if (count > 1) {
                if (i == 0 || i == count - 1) {
                    addIndicator1(false, mAnimationIn).setVisibility(View.INVISIBLE);
                } else {
                    addIndicator1(false, mAnimationIn);
                }
            }
        }
    }

    private View addIndicator1(boolean selected, Animator animator) {
        if (animator.isRunning()) animator.end();

        View Indicator = new View(getContext());
        setIndicatorBackground(Indicator, selected);
        addView(Indicator, mIndicatorWidth, mIndicatorHeight);
        LayoutParams lp = (LayoutParams) Indicator.getLayoutParams();
        lp.leftMargin = mIndicatorMargin;
        lp.rightMargin = mIndicatorMargin;
        Indicator.setLayoutParams(lp);


        animator.setTarget(Indicator);
        animator.start();
        return Indicator;
    }
}
