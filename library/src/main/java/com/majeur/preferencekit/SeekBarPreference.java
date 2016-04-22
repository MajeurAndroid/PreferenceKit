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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Preference that allows user to input an value through a {@link SeekBar}.
 */
public class SeekBarPreference extends BottomWidgetPreference implements SeekBar.OnSeekBarChangeListener {

    private static final int DEFAULT_VALUE = 0;

    private SeekBar mSeekBar;
    private TextView mValueIndicator;
    private int mKnobColor;
    private boolean mKnobCustomColor;
    private int mValue;

    private int mMaxValue;
    private boolean mShowValue;

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SeekBarPreference(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.preference_seekbar, 0, 0);
            mMaxValue = typedArray.getInteger(R.styleable.preference_seekbar_maxValue, 100);
            mShowValue = typedArray.getBoolean(R.styleable.preference_seekbar_showValue, true);
            mKnobCustomColor = typedArray.hasValue(R.styleable.preference_seekbar_knobColor);
            mKnobColor = typedArray.getColor(R.styleable.preference_seekbar_knobColor, 0);
            typedArray.recycle();
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, DEFAULT_VALUE);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        int value = restorePersistedValue ? getPersistedInt(DEFAULT_VALUE) : (Integer) defaultValue;
        setNewValue(value);
    }

    private void setNewValue(int newValue) {
        if (isPersistent() && callChangeListener(newValue))
            persistInt(newValue);

        mValue = newValue;

        if (mValueIndicator != null && mShowValue)
            mValueIndicator.setText(String.valueOf(mValue));

        if (mSeekBar != null && mSeekBar.getProgress() != mValue)
            mSeekBar.setProgress(mValue);
    }

    @Override
    protected View onCreateBottomWidgetView(ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_widget_seekbar, parent, false);

        mSeekBar = (SeekBar) view.findViewById(R.id.pk_seekbar);
        mValueIndicator = (TextView) view.findViewById(R.id.pk_indicator);

        return view;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void onBindBottomWidgetView(View widgetView) {
        mSeekBar.setMax(mMaxValue);
        mSeekBar.setProgress(mValue);
        // Always set listeners after setting values
        mSeekBar.setOnSeekBarChangeListener(this);

        mValueIndicator.setVisibility(mShowValue ? View.VISIBLE : View.GONE);

        if (mShowValue)
            mValueIndicator.setText(String.valueOf(mValue));

        if (mKnobCustomColor && Utils.isApi16()) {
            mSeekBar.getProgressDrawable().setColorFilter(mKnobColor, PorterDuff.Mode.SRC_IN);
            mSeekBar.getThumb().setColorFilter(mKnobColor, PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        setNewValue(i);
    }

    /**
     * Set progressbar max value. If max value is lower than current value, current value will be new maximum
     * @param maxValue Value to be the maximum
     */
    public void setMaxValue(int maxValue) {
        mMaxValue = maxValue;
        if (mValue > maxValue)
            setNewValue(maxValue);
    }

    /**
     * Set if the value should be shown next to the progress bar
     * @param showValue Whether to show value
     */
    public void setShowValue(boolean showValue) {
        mShowValue = showValue;
    }

    /**
     * Set progress bar color (API16+ only)
     * @param color Progress bar color
     */
    public void setProgressBarColor(int color) {
        mKnobCustomColor = true;
        mKnobColor = color;
        notifyChanged();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
