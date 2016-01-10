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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Arrays;

/**
 * Preference that shows multiple single choice items in form of radio buttons.
 */
public class RadioPreference extends EntrySetPreference {

    private RadioGroup mContainer;
    private LayoutInflater mInflater;

    public RadioPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public RadioPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RadioPreference(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    protected void onSetNewValue(CharSequence newValue) {
        CharSequence[] entryValues = getEntryValues();

        final int newPosition = Arrays.asList(entryValues).indexOf(newValue);
        int radioId = intToRadioId(newPosition);

        if (mContainer != null && mContainer.getCheckedRadioButtonId() != radioId)
            mContainer.check(radioId);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        return mInflater.inflate(R.layout.preference_radio, parent, false);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        final CharSequence[] entryValues = getEntryValues();

        mContainer = (RadioGroup) view.findViewById(R.id.radio_group);
        setRadioButtons();

        mContainer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                String newValue = entryValues[radioIdToInt(id)].toString();
                setValue(newValue);
            }
        });
    }

    private void setRadioButtons() {
        if (mContainer != null) {
            CharSequence[] entries = getEntries();
            final CharSequence[] entryValues = getEntryValues();
            CharSequence value = getValue();

            mContainer.removeAllViews();

            for (int i = 0; i < entries.length; i++) {
                final int id = intToRadioId(i);

                RadioButton button = (RadioButton) mInflater.inflate(R.layout.simple_radio_button, mContainer, false);
                button.setText(entries[i]);
                button.setEnabled(isEnabled());
                button.setId(id); // Needed for the RadioGroup

                mContainer.addView(button);

                if (entryValues[i].equals(value))
                    mContainer.check(id);
            }
        }
    }

    // Used to avoid any 0 as id
    private int intToRadioId(int i) {
        return i + 20;
    }

    private int radioIdToInt(int i) {
        return i - 20;
    }
}