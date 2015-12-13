package com.majeur.preferencekit;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Simple class that provide built-in preference with numberPicker as dialog
 */
public class DoubleCirclePickerPreference extends DialogPreference {

    private int mMin;
    private int mMax;
    private int mValue1, mValue2;

    private boolean mShowValueInSummary;

    private CirclePickerView mNumberPicker1, mNumberPicker2;

    public DoubleCirclePickerPreference(Context context, AttributeSet attrs) {
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
    protected void onSetInitialValue(boolean restorePersistedValue, Object ignored) {
        String value = restorePersistedValue ? getPersistedString(createPersistValue(mMin, mMin)) : createPersistValue(0, 0);
        Pair<Integer, Integer> values = getValuesFromPersistedData(value);
        setNewValue(values.first, values.second);
    }

    private void setNewValue(int newValue1, int newValue2) {
        if (isPersistent())
            persistString(createPersistValue(newValue1, newValue2));

        if (getOnPreferenceChangeListener() != null)
            getOnPreferenceChangeListener().onPreferenceChange(this, createPersistValue(newValue1, newValue2));

        mValue1 = newValue1;
        mValue2 = newValue2;

        if (mShowValueInSummary)
            setSummary(mValue1 + " - " + mValue2);
    }

    @Override
    protected View onCreateDialogView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.double_dialog_circle_picker, null);
        mNumberPicker1 = (CirclePickerView) view.findViewById(R.id.circlePicker1);
        mNumberPicker2 = (CirclePickerView) view.findViewById(R.id.circlePicker2);
        return view;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        mNumberPicker1.setBounds(mMin, mMax);
        mNumberPicker1.setValue(mValue1);

        mNumberPicker2.setBounds(mMin, mMax);
        mNumberPicker2.setValue(mValue2);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult)
            setNewValue(mNumberPicker1.getValue(), mNumberPicker2.getValue());

    }

    public void setMin(int min) {
        mMin = min;
        if (mValue1 < min)
            setNewValue(min, mValue2);

        if (mValue2 < min)
            setNewValue(mValue1, min);
    }

    public void setMax(int max) {
        mMax = max;
        if (mValue1 > max)
            setNewValue(max, mValue2);

        if (mValue2 > max)
            setNewValue(mValue1, max);
    }

    public void setShowValueInSummary(boolean showValueInSummary) {
        mShowValueInSummary = showValueInSummary;
    }

    public static String createPersistValue(int value1, int value2) {
        return value1 + "x" + value2;
    }

    public static Pair<Integer, Integer> getValuesFromPersistedData(String data) {
        String[] strings = data.split("x");
        if (strings.length != 2) return null;
        return new Pair<>(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]));
    }

}