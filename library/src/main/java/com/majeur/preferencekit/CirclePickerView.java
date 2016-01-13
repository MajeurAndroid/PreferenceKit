/*
 *  Copyright 2016 MajeurAndroid
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.majeur.preferencekit;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import static java.lang.Math.PI;

class CirclePickerView extends View {

    private static Rect sTmpRect = new Rect();
    private static Interpolator sSharedInterpolator = new AccelerateDecelerateInterpolator();

    private int mMinValue = 0;
    private int mMaxValue = 10;

    private Paint mPaint;
    private int mCircleRadius;
    private int mSelectionCircleRadius;
    private int mDefaultCircleRadius;
    private int mDefaultSelectionCircleRadius;
    private Paint mTextPaint;
    private int mSelectorColor, mWheelColor;

    private float mAngleStep;
    private float mIndicatorAngle;
    private int mPosition;

    private boolean mIsOnWheel;

    private ValueAnimator mIndicatorAnimator;

    public CirclePickerView(Context context) {
        super(context);
        init(context);
    }

    public CirclePickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CirclePickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSelectorColor = Utils.getAttrColor(getContext(), R.attr.colorAccent);
        mWheelColor = getResources().getColor(R.color.pk_window_light);
        mDefaultCircleRadius = getResources().getDimensionPixelSize(R.dimen.pk_circlepicker_radius);
        mDefaultSelectionCircleRadius = Utils.dpToPx(context, 25);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.BLACK);//Utils.getAttrColor(context, android.R.attr.textColor));
        mTextPaint.setTextSize(Utils.spToPx(context, 18));//Utils.getAttrDimen(context, android.R.attr.textSize));

        mIndicatorAnimator = new ValueAnimator();
        mIndicatorAnimator.setDuration(200);
        mIndicatorAnimator.setInterpolator(sSharedInterpolator);
        mIndicatorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float angle = (float) valueAnimator.getAnimatedValue();
                moveIndicatorTo(angle);
            }
        });
    }

    public void setBounds(int min, int max) {
        if (max <= min)
            throw new IllegalArgumentException("max value must be greater than min value");

        mMinValue = min;
        mMaxValue = max;

        mAngleStep = (float) ((2 * PI) / getCount());
        invalidate();
    }

    public void setValue(int value) {
        setPosition(getIndexForValue(value));
    }

    public int getValue() {
        return getValueForIndex(mPosition);
    }

    public int getCount() {
        return mMaxValue - mMinValue + 1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int defaultSize = (mDefaultCircleRadius + mDefaultSelectionCircleRadius) * 2;

        if (widthMode == MeasureSpec.UNSPECIFIED) {
            widthSize = defaultSize + getPaddingLeft() + getPaddingRight();

        } else if (widthMode == MeasureSpec.AT_MOST) {
            int reqWidth = defaultSize + getPaddingLeft() + getPaddingRight();

            widthSize = Math.min(reqWidth, widthSize);
        } else if (widthMode == MeasureSpec.EXACTLY) {
            // Do nothing
        }

        if (heightMode == MeasureSpec.UNSPECIFIED) {
            heightSize = defaultSize + getPaddingTop() + getPaddingBottom();
        } else if (heightMode == MeasureSpec.AT_MOST) {
            int reqHeight = defaultSize + getPaddingTop() + getPaddingBottom();

            heightSize = Math.min(reqHeight, heightSize);
        } else if (heightMode == MeasureSpec.EXACTLY) {
            // Do nothing
        }

        int minSize = Math.min(widthSize - getPaddingLeft() - getPaddingRight(),
                heightSize - getPaddingTop() - getPaddingBottom());

        mCircleRadius = (int) ((0.75 * minSize) / 2);
        mSelectionCircleRadius = (int) ((0.25 * minSize) / 2);

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float angle = calculateAngle(x, y);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                x -= getWidth() / 2;
                y -= getHeight() / 2;
                float touchRadius = (float) Math.sqrt(x * x + y * y);
                mIsOnWheel = touchRadius >= (mCircleRadius - mSelectionCircleRadius)
                        && touchRadius <= (mCircleRadius + mSelectionCircleRadius);

                if (mIsOnWheel) {
                    animateIndicatorTo(angle);
                    return true;
                }
                return false;

            case MotionEvent.ACTION_MOVE:
                if (mIsOnWheel) {
                    moveIndicatorTo(angle);
                    return true;
                }
                return false;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mIsOnWheel) {
                    final int nearestElementIndex = calculateNearestElementIndex(angle);
                    mIndicatorAnimator.cancel();
                    mIndicatorAnimator.setFloatValues(mIndicatorAngle, nearestElementIndex * mAngleStep);
                    mIndicatorAnimator.addListener(new AnimatorListener() {
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            setPosition(nearestElementIndex);
                        }
                    });
                    mIndicatorAnimator.start();
                    return true;
                }
                return false;

            default:
                return false;
        }
    }

    private float calculateAngle(float x, float y) {
        return (float) Utils.polarAngle(x - getWidth() / 2., y - getHeight() / 2.);
    }

    private int calculateNearestElementIndex(float angle) {
        float div = angle / mAngleStep;
        int intDiv = (int) div;
        float r = div - intDiv;

        return (r < 0.5f) ? intDiv : intDiv + 1;
    }

    private void setPosition(int position) {
        mPosition = position;
        moveIndicatorTo(position * mAngleStep);
    }

    private void moveIndicatorTo(float angle) {
        mIndicatorAngle = angle;
        invalidate();
    }

    private void animateIndicatorTo(float angle) {
        mIndicatorAnimator.cancel();
        mIndicatorAnimator.removeAllListeners();
        mIndicatorAnimator.setFloatValues(mIndicatorAngle, angle);
        mIndicatorAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int canvasCenterX = canvas.getWidth() / 2;
        int canvasCenterY = canvas.getHeight() / 2;

        mPaint.setColor(mWheelColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mSelectionCircleRadius * 1.5f);
        canvas.drawCircle(canvasCenterX, canvasCenterY, mCircleRadius, mPaint);

        mPaint.setColor(mSelectorColor);
        mPaint.setStyle(Paint.Style.FILL);

        float indicatorX = (float) (canvasCenterX + mCircleRadius * Math.cos(mIndicatorAngle));
        float indicatorY = (float) (canvasCenterY + mCircleRadius * Math.sin(mIndicatorAngle));

        canvas.drawCircle(indicatorX, indicatorY, mSelectionCircleRadius, mPaint);

        int count = getCount();

        for (int i = 0; i < count; i++) {
            double angle = i * mAngleStep;
            float x = (float) (canvasCenterX + mCircleRadius * Math.cos(angle));
            float y = (float) (canvasCenterY + mCircleRadius * Math.sin(angle));

            String text = Integer.toString(getValueForIndex(i));
            mTextPaint.getTextBounds(text, 0, text.length(), sTmpRect);
            canvas.drawText(text,
                    x - (sTmpRect.width() / 2 + sTmpRect.left),
                    y - (sTmpRect.height() / 2 + sTmpRect.top),
                    mTextPaint);
        }


    }

    private int getValueForIndex(int index) {
        return mMinValue + index;
    }

    private int getIndexForValue(int value) {
        if (value > mMaxValue)
            value = mMaxValue;
        else if (value < mMinValue)
            value = mMinValue;

        return value - mMinValue;
    }
}
