package com.majeur.preferencekit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * View that shows a color in a circle with shadow
 */
class CircleColorIndicator extends View {

    private Paint mPaint, mShadowPaint;

    public CircleColorIndicator(Context context) {
        super(context);
        init();
    }

    public CircleColorIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleColorIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint.setColor(Color.DKGRAY);
        mShadowPaint.setAlpha(100);
    }

    public void setColor(int color) {
        mPaint.setColor(color);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int px = Math.min(canvas.getWidth(), canvas.getHeight()) / 2;
        canvas.drawCircle(px, px, px, mShadowPaint);
        canvas.drawCircle(px, px, px - Utils.dpToPx(getContext(), 3.5f), mPaint);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setAlpha(enabled ? 1f : 0.4f);
    }
}
