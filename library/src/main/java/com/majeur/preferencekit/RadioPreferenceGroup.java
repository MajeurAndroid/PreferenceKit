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
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;

/**
 * Preference group that holds a list of linked radio preferences. It is the same purpose as a {@link android.widget.RadioGroup} widget.
 * Persisted value is the index of selected radio button.
 * RadioTitles must be set through xml or code, summaries and icons are optional.
 */
public class RadioPreferenceGroup extends PreferenceGroup {

    private CharSequence[] mRadioTitles;
    private CharSequence[] mRadioSummaries;
    private Drawable[] mRadioIcons;

    private int mSelectedIndex;

    private OnChildRadioCheckedListener mOnChildRadioCheckedListener = new OnChildRadioCheckedListener() {
        @Override
        public void onChildRadioChecked(InternalRadioPreference preference, boolean checked) {
            if (!checked) {
                int newIndex = preference.getOrder();
                if (callChangeListener(newIndex))
                    setCheckedRadio(newIndex);
            }
        }
    };

    public RadioPreferenceGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.preference_radiogroup, defStyleAttr, 0);
            mRadioTitles = typedArray.getTextArray(R.styleable.preference_radiogroup_radioTitles);
            mRadioSummaries = typedArray.getTextArray(R.styleable.preference_radiogroup_radioSummaries);
            int iconArrayResId = typedArray.getResourceId(R.styleable.preference_radiogroup_radioIcons, 0);
            typedArray.recycle();

            if (iconArrayResId != 0) {
                mRadioIcons = Utils.getDrawableArray(context, iconArrayResId);
            }
        }
    }

    public RadioPreferenceGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadioPreferenceGroup(Context context) {
        this(context, null);
    }

    /**
     * Sets radioButton titles. Cannot be null.
     * @param radioTitles titles
     */
    public void setRadioTitles(CharSequence[] radioTitles) {
        if (!Arrays.equals(mRadioTitles, radioTitles)) {
            mRadioTitles = radioTitles;
            syncChildren();
        }
    }

    /**
     * Sets radioButton summaries. Can be null or contains null elements.
     * @param radioSummaries summaries
     */
    public void setRadioSummaries(CharSequence[] radioSummaries) {
        if (!Arrays.equals(mRadioSummaries, radioSummaries)) {
            mRadioSummaries = radioSummaries;
            syncChildren();
        }
    }

    /**
     * Sets radioButton icons. Can be null or contains null elements.
     * @param radioIcons icons
     */
    public void setRadioIcons(Drawable[] radioIcons) {
        if (!Arrays.equals(mRadioIcons, radioIcons)) {
            mRadioIcons = radioIcons;
            syncChildren();
        }
    }

    private void syncChildren() {
        int currentCount = getPreferenceCount();
        final int desiredCount = mRadioTitles.length;

        while (currentCount > desiredCount) {
            currentCount--;
            removePreference(getPreference(currentCount));
        }

        while (currentCount < desiredCount) {
            InternalRadioPreference preference = new InternalRadioPreference(getContext());

            preference.setOrder(currentCount++);
            preference.setListener(mOnChildRadioCheckedListener);

            super.addPreference(preference);
        }

        for (int i = 0; i < desiredCount; i++) {
            Preference preference = getPreference(i);
            preference.setTitle(mRadioTitles[i]);

            if (mRadioSummaries != null)
                preference.setSummary(mRadioSummaries[i]);

            if (mRadioIcons != null)
                preference.setIcon(mRadioIcons[i]);
        }

        notifyChildChanged();
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, 0);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        syncChildren();
        setCheckedRadio(restorePersistedValue ? getPersistedInt(0) : (Integer) defaultValue);
    }

    /**
     * Sets the current checked radioButton. Can throw an {@link IndexOutOfBoundsException}.
     * @param index index
     */
    public void setCheckedRadio(int index) {
        checkIndex(index);

        if (mSelectedIndex != index) {
            mSelectedIndex = index;

            if (isPersistent() && callChangeListener(index))
                persistInt(index);

            notifyChildChanged();
        }
    }

    private void checkIndex(int index) {
        int size = getPreferenceCount();
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("index is " + index + " size is " + size);
    }

    /**
     * Returns the current checked radioButton index.
     * @return current checked index
     */
    public int getCheckedRadio() {
        return mSelectedIndex;
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.preference_category, parent, false);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        TextView textView = (TextView) view.findViewById(android.R.id.title);
        textView.setTypeface(Utils.Typefaces.getRobotoMedium(getContext()));

        if (getContext() instanceof PreferenceHeadersActivity) {
            PreferenceHeadersActivity activity = (PreferenceHeadersActivity) getContext();
            textView.setTextColor(activity.getDecorColor());
        }

        syncChildren();
    }

    private void notifyChildChanged() {
        int size = getPreferenceCount();

        for (int i = 0; i < size; i++) {
            InternalRadioPreference preference = (InternalRadioPreference) getPreference(i);
            preference.setChecked(i == mSelectedIndex);
        }
    }

    /**
     * Throws {@link UnsupportedOperationException}, RadioPreferenceGroup adds child preferences itself
     */
    @Override
    public void addItemFromInflater(Preference preference) {
        throw new UnsupportedOperationException("Cannot add child preferences via xml, RadioPreferenceGroup adds child preferences itself");
    }

    /**
     * Throws {@link UnsupportedOperationException}, RadioPreferenceGroup adds child preferences itself
     */
    @Override
    public boolean addPreference(Preference preference) {
        throw new UnsupportedOperationException("Cannot add child preferences, RadioPreferenceGroup adds child preferences itself");
    }

    @Override
    protected boolean isOnSameScreenAsChildren() {
        return true;
    }

    /**
     * @hide
     */
    @Override
    public boolean isSelectable() {
        return false;
    }

    /**
     * @hide
     */
    private interface OnChildRadioCheckedListener {
        void onChildRadioChecked(InternalRadioPreference preference, boolean checked);
    }

    private static class InternalRadioPreference extends CheckBoxPreference {

        private OnChildRadioCheckedListener mListener;

        public InternalRadioPreference(Context context) {
            super(context);
        }

        /**
         * We add a radio to widget container with android internal checkbox id
         * to allow parent class to modify radio state
         */
        @Override
        protected View onCreateView(ViewGroup parent) {
            View view = super.onCreateView(parent);

            ViewGroup stub = (ViewGroup) view.findViewById(R.id.pk_stub);
            stub.removeAllViews(); // CheckBox has been added in super call
            LayoutInflater.from(getContext()).inflate(R.layout.widget_radio, stub);

            return view;
        }

        @Override
        public boolean isPersistent() {
            return false;
        }

        void setListener(OnChildRadioCheckedListener listener) {
            mListener = listener;
        }

        @Override
        protected void onClick() {
            mListener.onChildRadioChecked(this, isChecked());
        }
    }
}
