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
    protected View onCreateBottomWidgetView(ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.preference_spinner, parent, false);

        mSpinner = (Spinner) view.findViewById(R.id.pk_spinner);

        return view;
    }

    @Override
    protected void onBindBottomWidgetView(View widgetView) {
        CharSequence[] entries = getEntries();
        final CharSequence[] entryValues = getEntryValues();
        CharSequence value = getValue();

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
