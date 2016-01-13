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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

/**
 * Preference that allow user to pick a color
 */
public class ColorPickerPreference extends DialogPreference {

    private int mDefaultValue;
    private int mValue;

    private ColorPicker mColorPickerView;

    private CircleColorIndicator mColorIndicator;
    private boolean mAlphaAllowed, mSVAllowed;

    public ColorPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.preference_colorpicker, defStyleAttr, 0);
            mAlphaAllowed = typedArray.getBoolean(R.styleable.preference_colorpicker_alphaAllowed, true);
            mSVAllowed = typedArray.getBoolean(R.styleable.preference_colorpicker_saturationAndValueAllowed, true);
            typedArray.recycle();
        }

        mDefaultValue = Utils.getAttrColor(context, R.attr.colorAccent);
    }

    public ColorPickerPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPickerPreference(Context context) {
        this(context, null, 0);
    }

    public void setAlphaAllowed(boolean allowed) {
        mAlphaAllowed = allowed;
    }

    /**
     * Provide the default value to the system
     */
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getColor(index, mDefaultValue);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        int value = restorePersistedValue ? getPersistedInt(mDefaultValue) : (Integer) defaultValue;
        setNewValue(value);
    }

    private void setNewValue(int newValue) {
        if (isPersistent())
            persistInt(newValue);

        if (getOnPreferenceChangeListener() != null)
            getOnPreferenceChangeListener().onPreferenceChange(this, newValue);

        mValue = newValue;

        if (mColorIndicator != null)
            mColorIndicator.setColor(newValue);
    }

    @Override
    protected View onCreateDialogView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_colorpicker, null);

        mColorPickerView = (ColorPicker) view.findViewById(R.id.pk_colorPicker);
        OpacityBar mOpacityBar = (OpacityBar) view.findViewById(R.id.pk_opacityBar);
        SVBar svBar = (SVBar) view.findViewById(R.id.pk_svBar);

        mOpacityBar.setColorPicker(mColorPickerView);
        svBar.setColorPicker(mColorPickerView);

        mColorPickerView.addOpacityBar(mOpacityBar);
        mColorPickerView.addSVBar(svBar);

        mOpacityBar.setVisibility(mAlphaAllowed ? View.VISIBLE : View.GONE);
        svBar.setVisibility(mSVAllowed ? View.VISIBLE : View.GONE);

        return view;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        mColorPickerView.setOldCenterColor(mValue);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult)
            setNewValue(mColorPickerView.getColor());
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = super.onCreateView(parent);
        mColorIndicator = new CircleColorIndicator(getContext());

        ViewGroup stub = (ViewGroup) view.findViewById(R.id.pk_stub);
        int px = Utils.dpToPx(getContext(), 40);
        stub.addView(mColorIndicator, new ViewGroup.LayoutParams(px, px));
        return view;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        mColorIndicator.setColor(mValue);
    }
}