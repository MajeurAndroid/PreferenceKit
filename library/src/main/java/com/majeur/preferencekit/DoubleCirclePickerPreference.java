package com.majeur.preferencekit;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Simple class that provide built-in preference with numberPicker as dialog
 */
public class DoubleCirclePickerPreference extends DialogPreference {

    private int mMin1, mMin2;
    private int mMax1, mMax2;
    private int mValue1, mValue2;

    private boolean mShowValueInSummary;

    private CirclePickerView mNumberPicker1, mNumberPicker2;

    public DoubleCirclePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.preference_doublecirclepicker, 0, 0);
        mMax1 = typedArray.getInteger(R.styleable.preference_doublecirclepicker_maxValue1, 10);
        mMin1 = typedArray.getInteger(R.styleable.preference_doublecirclepicker_minValue1, 1);
        mMax2 = typedArray.getInteger(R.styleable.preference_doublecirclepicker_maxValue2, 10);
        mMin2 = typedArray.getInteger(R.styleable.preference_doublecirclepicker_minValue2, 1);
        mShowValueInSummary = typedArray.getBoolean(R.styleable.preference_numberpicker_showValueInSummary, false);
        typedArray.recycle();
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        String data = restorePersistedValue ? getPersistedString((String) defaultValue) : (String) defaultValue;

        Pair<Integer, Integer> values = getValuesFromPersistedData(data);
        setNewValue(values.first, values.second);
    }

    private void setNewValue(int newValue1, int newValue2) {
        if (isPersistent())
            persistString(createDataToPersist(newValue1, newValue2));

        if (getOnPreferenceChangeListener() != null)
            getOnPreferenceChangeListener().onPreferenceChange(this, createDataToPersist(newValue1, newValue2));

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
    protected void onBindDialogView(@NonNull View view) {
        super.onBindDialogView(view);

        mNumberPicker1.setBounds(mMin1, mMax1);
        mNumberPicker1.setValue(mValue1);

        mNumberPicker2.setBounds(mMin2, mMax2);
        mNumberPicker2.setValue(mValue2);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult)
            setNewValue(mNumberPicker1.getValue(), mNumberPicker2.getValue());

    }

    /**
     * Set the value of each picker
     * @param firstPickerValue value to apply to first picker
     * @param secondPickerValue value to apply to second picker
     */
    public void setPickersValues(int firstPickerValue, int secondPickerValue) {
        setNewValue(firstPickerValue, secondPickerValue);
    }

    /**
     * Provide the first picker current value
     * @return first picker current value
     */
    public int getFirstPickerValue() {
        return mValue1;
    }

    /**
     * Set the min and max bounds of the first picker. If the current value is not include
     * in the new interval, it will be assigned to max or min value.
     * @param min min value of the first picker
     * @param max max value of the first picker
     */
    public void setFirstPickerBounds(int min, int max) {
        mMin1 = min;
        mMax1 = max;

        if (mValue1 < min)
            setNewValue(min, mValue2);

        if (mValue1 > max)
            setNewValue(max, mValue2);
    }

    /**
     * Provide the second picker current value
     * @return second picker current value
     */
    public int getSecondPickerValue() {
        return mValue1;
    }

    /**
     * Set the min and max bounds of the second picker. If the current value is not include
     * in the new interval, it will be assigned to max or min value.
     * @param min min value of the second picker
     * @param max max value of the second picker
     */
    public void setSecondPickerBounds(int min, int max) {
        mMin2 = min;
        mMax2 = max;

        if (mValue2 < min)
            setNewValue(mValue1, min);

        if (mValue2 > max)
            setNewValue(mValue1, max);
    }

    public void setShowValueInSummary(boolean showValueInSummary) {
        mShowValueInSummary = showValueInSummary;
    }

    /**
     * Create a string to be persisted that contains the two values.
     * @param value1 first value (corresponds to the first picker)
     * @param value2 second value (corresponds to the second picker)
     * @return string to be persisted
     */
    public static String createDataToPersist(int value1, int value2) {
        return value1 + "x" + value2;
    }

    /**
     * Decode the persisted value to provide the two integers that had been persisted
     * @param data persisted data
     * @return a {@link android.util.Pair} which contains the two values
     */
    public static Pair<Integer, Integer> getValuesFromPersistedData(String data) {
        String[] strings = data.split("x");
        if (strings.length != 2) return null;
        return new Pair<>(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]));
    }

}