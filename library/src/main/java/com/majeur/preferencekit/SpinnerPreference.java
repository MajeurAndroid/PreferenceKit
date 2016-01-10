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
public class SpinnerPreference extends EntrySetPreference {

    private Spinner mSpinner;

    public SpinnerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SpinnerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SpinnerPreference(Context context) {
        super(context);
    }

    @Override
    protected void onSetNewValue(CharSequence newValue) {
        CharSequence[] entryValues = getEntryValues();
        final int newPosition = Arrays.asList(entryValues).indexOf(newValue);

        if (mSpinner != null && mSpinner.getSelectedItemPosition() != newPosition)
            mSpinner.setSelection(newPosition);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        return LayoutInflater.from(getContext()).inflate(R.layout.preference_spinner, parent, false);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        CharSequence[] entries = getEntries();
        final CharSequence[] entryValues = getEntryValues();
        CharSequence value = getValue();

        mSpinner = (Spinner) view.findViewById(R.id.spinner);
        mSpinner.setAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                android.R.id.text1, entries));

        int newPosition = Arrays.asList(entryValues).indexOf(value);

        if (mSpinner.getSelectedItemPosition() != newPosition)
            mSpinner.setSelection(newPosition);

        // Always set listeners after setting values
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setValue(entryValues[i].toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
