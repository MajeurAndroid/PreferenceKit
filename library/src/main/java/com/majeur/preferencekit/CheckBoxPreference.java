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
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CheckBoxPreference extends android.preference.CheckBoxPreference implements Lockable, CommonPreferenceDelegate.Delegatable {

    private CommonPreferenceDelegate mDelegate;

    public CheckBoxPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDelegate = new CommonPreferenceDelegate(this);
        mDelegate.init(context, attrs);
    }

    public CheckBoxPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckBoxPreference(Context context) {
        this(context, null);
    }

    /**
     * We add a checkbox to widget container with android internal checkbox id
     * to allow parent class to modify checkbox state
     */
    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = mDelegate.onCreateView(parent);

        ViewGroup stub = (ViewGroup) view.findViewById(R.id.pk_stub);
        LayoutInflater.from(getContext()).inflate(R.layout.widget_checkbox, stub);

        return view;
    }

    @Override
    protected void onBindView(View view) {
        mDelegate.onBindView(view);
    }

    @Override
    public void setEnabled(boolean enabled) {
        mDelegate.setEnabled(enabled);
    }

    @Override
    public void superSetEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }

    @Override
    public void superOnBindView(View view) {
        super.onBindView(view);
    }

    @Override
    public void notifyChangedInternal() {
        notifyChanged();
    }

    @Override
    public void setLockedIcon(Drawable drawable) {
        mDelegate.setLockedIcon(drawable);
    }

    @Override
    public void setLockedIconResource(int resId) {
        mDelegate.setLockedIconResource(resId);
    }

    @Override
    public void setLocked(boolean locked) {
        mDelegate.setLocked(locked);
    }

    @Override
    public boolean isLocked() {
        return mDelegate.isLocked();
    }
}
