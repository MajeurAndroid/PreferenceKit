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
import android.content.res.TypedArray;
import android.util.AttributeSet;

import java.util.Arrays;

/**
 * Base class for a Preference which works with entries and entryValues arrays.
 * @see SpinnerPreference
 * @see RadioPreference
 */
abstract class EntrySetPreference extends Preference {

    /**
     * Key used to retrieve later the real default values,
     * when {@link #onGetDefaultValue(TypedArray, int)} is called, it's to early to access
     * entryValues, so if default values is needed, we return this string, and later we
     * replace it by first entryValues item, on {@link #onSetInitialValue(boolean, Object)}.
     */
    private static final String STRING_DEFAULT = "str_default_none";

    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;
    private CharSequence mValue;

    private boolean mShowValueInSummary;

    public EntrySetPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public EntrySetPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EntrySetPreference(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.preference_entryset, 0, 0);
            mEntries = typedArray.getTextArray(R.styleable.preference_entryset_entries);
            mEntryValues = typedArray.getTextArray(R.styleable.preference_entryset_entryValues);
            mShowValueInSummary = typedArray.getBoolean(R.styleable.preference_entryset_showValueInSummary, false);
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

        if (STRING_DEFAULT.equals(defaultValue) || defaultValue == null)
            defaultValue = mEntryValues[0].toString();

        String value = restorePersistedValue ? getPersistedString((String) defaultValue) : (String) defaultValue;

        setValue(value);
    }

    public void setSelectedItem(int index) {
        if (index < 0 || index >= mEntryValues.length)
            throw new IndexOutOfBoundsException("index is " + index + ", size is " + mEntryValues.length);

        setValue(mEntryValues[index]);
    }

    public int getSelectedItem() {
        return Arrays.asList(mEntryValues).indexOf(mValue);
    }

    protected void setValue(CharSequence value) {
        if (isPersistent())
            persistString(value.toString());

        if (getOnPreferenceChangeListener() != null)
            getOnPreferenceChangeListener().onPreferenceChange(this, value);

        mValue = value;

        if (mShowValueInSummary)
            setSummary(mEntries[Arrays.asList(mEntryValues).indexOf(value)]);

        onSetNewValue(value);
    }

    protected CharSequence getValue() {
        return mValue;
    }

    protected abstract void onSetNewValue(CharSequence newValue);

    public CharSequence[] getEntries() {
        return mEntries;
    }

    public void setEntries(CharSequence[] entries) {
        mEntries = entries;
    }

    public CharSequence[] getEntryValues() {
        return mEntryValues;
    }

    public void setEntryValues(CharSequence[] entryValues) {
        mEntryValues = entryValues;
    }
}
