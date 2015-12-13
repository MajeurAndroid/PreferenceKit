package com.majeur.preferencekit;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.Arrays;

/**
 * Preference that provide list to the user in form of {@link Spinner}.
 */
public class SpinnerPreference extends Preference {

    /**
     * Key used to retrieve later the real default value,
     * when {@link #onGetDefaultValue(TypedArray, int)} is called, it's to early to access
     * entryValues, so if default value is needed, we return this string, and later we
     * replace it by first entryValues item, on {@link #onSetInitialValue(boolean, Object)}.
     */
    private static final String STRING_DEFAULT = "str_default_none";

    private Spinner mSpinner;
    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;
    private CharSequence mValue;

    public SpinnerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public SpinnerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SpinnerPreference(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.preference_spinner, 0, 0);
            mEntries = typedArray.getTextArray(R.styleable.preference_spinner_entries);
            mEntryValues = typedArray.getTextArray(R.styleable.preference_spinner_entryValues);
            typedArray.recycle();
        }
    }

    /**
     * @see {@link #STRING_DEFAULT}
     */
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        String value = a.getString(index);
        return (value == null) ? STRING_DEFAULT : value;
    }

    /**
     * If default value should be set, here we have access to entry values
     */
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (Utils.isArrayEmpty(mEntries) || Utils.isArrayEmpty(mEntryValues))
            throw new IllegalStateException("entries and entryValues must be set.");

        if (STRING_DEFAULT.equals(defaultValue))
            defaultValue = mEntryValues[0].toString();

        String value = restorePersistedValue ? getPersistedString(mEntryValues[0].toString()) : (String) defaultValue;
        setNewValue(value);
    }

    /**
     * Called to commit value change     *
     *
     * @param newValue New value
     */
    private void setNewValue(String newValue) {
        if (isPersistent())
            persistString(newValue);

        if (getOnPreferenceChangeListener() != null)
            getOnPreferenceChangeListener().onPreferenceChange(this, newValue);

        final int newPosition = Arrays.asList(mEntryValues).indexOf(newValue);

        if (mSpinner != null && mSpinner.getSelectedItemPosition() != newPosition)
            mSpinner.setSelection(newPosition);

        mValue = newValue;
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        return LayoutInflater.from(getContext()).inflate(R.layout.preference_spinner, parent, false);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        mSpinner = (Spinner) view.findViewById(R.id.spinner);
        mSpinner.setAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                android.R.id.text1, mEntries));

        int newPosition = Arrays.asList(mEntryValues).indexOf(mValue);

        if (mSpinner.getSelectedItemPosition() != newPosition)
            mSpinner.setSelection(newPosition);

        // Always set listeners after setting values
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setNewValue(mEntryValues[i].toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
