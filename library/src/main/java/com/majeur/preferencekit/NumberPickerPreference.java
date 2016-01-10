package com.majeur.preferencekit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

/**
 * Simple class that provide built-in preference with numberPicker as dialog
 */
public class NumberPickerPreference extends DialogPreference {

    private int mMin;
    private int mMax;
    private int mValue;
    private int mDividersColor;

    private boolean mWrapSelectorWheel, mEditableValue, mShowValueInSummary;

    private NumberPicker mNumberPicker;

    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.preference_numberpicker, defStyleAttr, 0);
            mMax = typedArray.getInteger(R.styleable.preference_numberpicker_maxValue, 100);
            mMin = typedArray.getInteger(R.styleable.preference_numberpicker_minValue, 0);
            mWrapSelectorWheel = typedArray.getBoolean(R.styleable.preference_numberpicker_wrapSelectorWheel, false);
            mEditableValue = typedArray.getBoolean(R.styleable.preference_numberpicker_editableValue, false);
            mShowValueInSummary = typedArray.getBoolean(R.styleable.preference_numberpicker_showValueInSummary, false);
            mDividersColor = typedArray.getColor(R.styleable.preference_numberpicker_selectionIndicatorsColor, Utils.COLOR_ACCENT);
            typedArray.recycle();
        }
    }

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberPickerPreference(Context context) {
        this(context, null, 0);
    }

    /**
     * Provide the default value to the system
     */
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, mMin);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        int value = restorePersistedValue ? getPersistedInt(mMin) : (Integer) defaultValue;
        setNewValue(value);
    }

    private void setNewValue(int newValue) {
        if (isPersistent())
            persistInt(newValue);

        if (getOnPreferenceChangeListener() != null)
            getOnPreferenceChangeListener().onPreferenceChange(this, newValue);

        mValue = newValue;

        if (mShowValueInSummary)
            setSummary(String.valueOf(mValue));
    }

    @Override
    protected View onCreateDialogView() {
        mNumberPicker = new NumberPicker(getContext());

        Drawable indicator = Utils.getDividerDrawable(mNumberPicker);
        if (indicator != null)
            indicator.setColorFilter(mDividersColor, PorterDuff.Mode.SRC_IN);

        return mNumberPicker;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        mNumberPicker.setMaxValue(mMax);
        mNumberPicker.setMinValue(mMin);
        mNumberPicker.setValue(mValue);
        mNumberPicker.setWrapSelectorWheel(mWrapSelectorWheel);
        mNumberPicker.setDescendantFocusability(mEditableValue ?
                NumberPicker.FOCUS_AFTER_DESCENDANTS : NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult)
            setNewValue(mNumberPicker.getValue());

    }

    public void setMin(int min) {
        mMin = min;
        if (mValue < min)
            setNewValue(min);
    }

    public void setMax(int max) {
        mMax = max;
        if (mValue > max)
            setNewValue(max);
    }

    public void setWrapSelectorWheel(boolean wrapSelectorWheel) {
        mWrapSelectorWheel = wrapSelectorWheel;
    }

    public void setEditableValue(boolean editableValue) {
        mEditableValue = editableValue;
    }

    public void setShowValueInSummary(boolean showValueInSummary) {
        mShowValueInSummary = showValueInSummary;
    }
}