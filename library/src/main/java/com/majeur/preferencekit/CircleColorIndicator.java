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
