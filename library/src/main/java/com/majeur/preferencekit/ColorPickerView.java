package com.majeur.preferencekit;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;

class ColorPickerView extends View {

    private static final int[] COLORS = new int[]{0xFFFF0000, 0xFFFF00FF,
            0xFF0000FF, 0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00, 0xFFFF0000};

    private Paint mCirclePaint, mAlphaCirclePaint, mHandlePaint,
            mAlphaHandlePaint, mCenterPaint, mShadowPaint;
    private RectF mCircleRect, mAlphaCircleRect, mHandleRect,
            mAlphaHandleRect, mCenterRect, mShadowRect;

    private int mOffset, mHandleSize;
    private int mColor, mAlpha, mFinalColor;
    private float mColorRadius, mAlphaRadius;

    private boolean mTouchPriorityForColorCircle, mAlphaAllowed = true;

    public ColorPickerView(Context context) {
        super(context);

        int strokeSize = Utils.dpToPx(context, 8);

        // Color circle
        Shader colorShader = new SweepGradient(0, 0, COLORS, null);
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setShader(colorShader);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(strokeSize);

        mHandlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHandlePaint.setColor(Color.GREEN);

        mCircleRect = new RectF();
        mHandleRect = new RectF();

        // Alpha circle
        mAlphaCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAlphaCirclePaint.setStyle(Paint.Style.STROKE);
        mAlphaCirclePaint.setStrokeWidth(strokeSize);

        mAlphaHandlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAlphaHandlePaint.setColor(Color.GREEN);

        mAlphaCircleRect = new RectF();
        mAlphaHandleRect = new RectF();

        // Center
        mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCenterRect = new RectF();

        // Shadows
        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint.setColor(Color.DKGRAY);
        mShadowPaint.setAlpha(100);

        mShadowRect = new RectF();

        mHandleSize = Utils.dpToPx(context, 30);
    }

    /**
     * Set if view should display an alpha slider
     *
     * @param allowed Yes or no
     */
    public void setAlphaAllowed(boolean allowed) {
        mAlphaAllowed = allowed;
    }

    public int getColor() {
        return mFinalColor;
    }

    /**
     * Return current selected color
     *
     * @param color Current selected color
     */
    public void setColor(int color) {
        mFinalColor = color;
        mAlpha = Color.alpha(color);
        mColor = Color.rgb(Color.red(color), Color.green(color), Color.blue(color));

        post(new Runnable() {
            @Override
            public void run() {
                // This needs to be done after measuring process

                updateColor();
                updateAlpha();

                updateColorHandleForAngle(colorToAngle(mColor));
                updateAlphaHandleForAngle(alphaToAngle(mAlpha));

                invalidate();
            }
        });
    }

    /**
     * This update all things to update when color changes
     */
    private void updateColor() {
        Shader alphaShader = new SweepGradient(0, 0, new int[]{mColor, 0x00FFFFFF, mColor}, null);
        mAlphaCirclePaint.setShader(alphaShader);
        mHandlePaint.setColor(mColor);

        updateFinalColor();
        mAlphaHandlePaint.setColor(mFinalColor);
    }

    /**
     * This update all things to update when alpha changes
     */
    private void updateAlpha() {
        mAlphaHandlePaint.setAlpha(mAlpha);
        updateFinalColor();
    }

    /**
     * This update center circle which displays final color (color + alpha)
     */
    private void updateFinalColor() {
        mFinalColor = Color.argb(mAlpha, Color.red(mColor), Color.green(mColor), Color.blue(mColor));
        mCenterPaint.setColor(mFinalColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int size = Math.min(width, height);
        mOffset = size / 2;

        setMeasuredDimension(size, size);

        float strokeMargin = mCirclePaint.getStrokeWidth();

        // By taking max padding, we ensure that mCircleRect will stay a square
        int maxPadding = Utils.max(getSupportPaddingEnd(), getSupportPaddingStart(),
                getPaddingBottom(), getPaddingTop());

        mColorRadius = size / 2 - strokeMargin - maxPadding;

        mCircleRect.set(strokeMargin + maxPadding,
                strokeMargin + maxPadding,
                size - strokeMargin - maxPadding,
                size - strokeMargin - maxPadding);
        mCircleRect.offset(-mOffset, -mOffset);
        mHandleRect.offset(-mOffset, -mOffset);
        mAlphaHandleRect.offset(-mOffset, -mOffset);

        int alphaInset = Utils.dpToPx(getContext(), 50);
        mAlphaCircleRect.set(mCircleRect);
        mAlphaCircleRect.inset(alphaInset, alphaInset);

        mAlphaRadius = mColorRadius - alphaInset;

        int centerInset = Utils.dpToPx(getContext(), 30);
        mCenterRect.set(mAlphaCircleRect);
        mCenterRect.inset(centerInset, centerInset);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        float x = event.getX() - mOffset;
        float y = event.getY() - mOffset;

        if (action == MotionEvent.ACTION_DOWN) {
            // If ACTION_DOWN is outside of the alpha circle, all following ACTION_MOVE will apply
            // to color circle. If ACTION_DOWN is inside the alpha circle, all following ACTION_MOVE
            // will apply to alpha circle
            float radius = (float) Math.sqrt(x * x + y * y);
            mTouchPriorityForColorCircle = radius > getRadiusForRect(mAlphaCircleRect);

            return true;
        } else if (action == MotionEvent.ACTION_MOVE) {
            touchMove(x, y);

            return true;
        }

        return false;
    }

    private void touchMove(float x, float y) {

        if (mTouchPriorityForColorCircle || !mAlphaAllowed) { // Color circle
            float colorAngle = (float) Math.atan2(y, x);
            mColor = calculateColor(colorAngle);
            updateColorHandleForAngle(colorAngle);
            updateColor();
        } else { // Alpha circle
            float alphaAngle = (float) Math.atan2(y, x);
            mAlpha = calculateAlpha(alphaAngle);
            updateAlphaHandleForAngle(alphaAngle);
            updateAlpha();
        }

        invalidate();
    }

    private void updateColorHandleForAngle(float colorAngle) {
        float handleX = (float) (mColorRadius * Math.cos(colorAngle));
        float handleY = (float) (mColorRadius * Math.sin(colorAngle));

        mHandleRect.set(handleX - mHandleSize / 2, handleY - mHandleSize / 2, handleX + mHandleSize / 2, handleY + mHandleSize / 2);
    }

    private void updateAlphaHandleForAngle(float alphaAngle) {
        float handleX = (float) (mAlphaRadius * Math.cos(alphaAngle));
        float handleY = (float) (mAlphaRadius * Math.sin(alphaAngle));

        mAlphaHandleRect.set(handleX - mHandleSize / 2, handleY - mHandleSize / 2, handleX + mHandleSize / 2, handleY + mHandleSize / 2);
    }

    private float getRadiusForRect(RectF rectF) {
        float x = rectF.width() / 2;
        float y = rectF.height() / 2;
        return (float) Math.sqrt(x * x + y * y);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(mOffset, mOffset);

        // Center
        canvas.drawOval(mCenterRect, mCenterPaint);

        // Color circle
        canvas.drawOval(mCircleRect, mCirclePaint);
        drawShadow(canvas, mHandleRect);
        canvas.drawOval(mHandleRect, mHandlePaint);

        if (mAlphaAllowed) { // Alpha circle
            canvas.drawOval(mAlphaCircleRect, mAlphaCirclePaint);
            drawShadow(canvas, mAlphaHandleRect);
            canvas.drawOval(mAlphaHandleRect, mAlphaHandlePaint);
        }
    }

    private void drawShadow(Canvas canvas, RectF rectF) {
        int o = Utils.dpToPx(getContext(), 3.5f);
        mShadowRect.set(rectF.left - o, rectF.top - o, rectF.right + o, rectF.bottom + o);

        canvas.drawOval(mShadowRect, mShadowPaint);
    }

    private int calculateAlpha(float angle) {
        int alpha = (int) ((Math.abs(angle) * 255) / Math.PI);
        return 255 - alpha;
    }

    private int calculateColor(float angle) {
        float unit = (float) (angle / (2 * Math.PI));
        if (unit < 0) {
            unit += 1;
        }

        if (unit <= 0) {
            return COLORS[0];
        }
        if (unit >= 1) {
            return COLORS[COLORS.length - 1];
        }

        float p = unit * (COLORS.length - 1);
        int i = (int) p;
        p -= i;

        int c0 = COLORS[i];
        int c1 = COLORS[i + 1];
        int a = ave(Color.alpha(c0), Color.alpha(c1), p);
        int r = ave(Color.red(c0), Color.red(c1), p);
        int g = ave(Color.green(c0), Color.green(c1), p);
        int b = ave(Color.blue(c0), Color.blue(c1), p);

        return Color.argb(a, r, g, b);
    }

    private int ave(int s, int d, float p) {
        return s + Math.round(p * (d - s));
    }

    private float colorToAngle(int color) {
        float[] colors = new float[3];
        Color.colorToHSV(color, colors);

        return (float) Math.toRadians(-colors[0]);
    }

    private float alphaToAngle(int alpha) {
        return (float) (-(255 - alpha) * Math.PI / 255);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSupportPaddingStart() {
        return Utils.isApi17() ? getPaddingStart() : getPaddingLeft();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSupportPaddingEnd() {
        return Utils.isApi17() ? getPaddingEnd() : getPaddingRight();
    }
}
