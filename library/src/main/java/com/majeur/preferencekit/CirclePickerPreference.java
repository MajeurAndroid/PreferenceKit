package com.majeur.preferencekit;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Simple class that provide built-in preference with numberPicker as dialog
 */
public class CirclePickerPreference extends DialogPreference {

    private int mMin;
    private int mMax;
    private int mValue;

    private boolean mShowValueInSummary;

    private CirclePickerView mNumberPicker;

    public CirclePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.preference_circlepicker, 0, 0);
        mMax = typedArray.getInteger(R.styleable.preference_circlepicker_maxValue, 10);
        mMin = typedArray.getInteger(R.styleable.preference_circlepicker_minValue, 1);
        mShowValueInSummary = typedArray.getBoolean(R.styleable.preference_numberpicker_showValueInSummary, false);
        typedArray.recycle();
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
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_circle_picker, null);
        mNumberPicker = (CirclePickerView) view.findViewById(R.id.circlePicker);
        return view;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        mNumberPicker.setBounds(mMin, mMax);
        mNumberPicker.setValue(mValue);
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

    public void setShowValueInSummary(boolean showValueInSummary) {
        mShowValueInSummary = showValueInSummary;
    }
}