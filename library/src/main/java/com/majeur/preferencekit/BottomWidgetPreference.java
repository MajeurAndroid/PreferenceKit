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
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Preference that provide a way to add a widget as a second row of the preference.
 * @see {@link SeekBarPreference}
 * @see {@link SpinnerPreference}
 */
public class BottomWidgetPreference extends Preference {

    private View mWidgetView;

    public BottomWidgetPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BottomWidgetPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomWidgetPreference(Context context) {
        super(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        ViewGroup rootView = (ViewGroup) super.onCreateView(parent);

        ViewGroup widgetContainer = new FrameLayout(getContext());
        rootView.addView(widgetContainer, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        mWidgetView = onCreateBottomWidgetView(widgetContainer);

        if (mWidgetView != null)
            widgetContainer.addView(mWidgetView);

        return rootView;
    }

    /**
     * Called to create the bottom widget view
     * @param parent {@link ViewGroup} that will contain bottom widget
     * @return The bottom widget view or null
     */
    @Nullable
    protected View onCreateBottomWidgetView(ViewGroup parent) {
        return null;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        if (mWidgetView != null)
            onBindBottomWidgetView(mWidgetView);
    }

    /**
     * Called to bind the bottom appwidget view to its data. If {@link #onCreateBottomWidgetView(ViewGroup)} returned null,
     * this method will not be called
     * @param widgetView
     */
    protected void onBindBottomWidgetView(View widgetView) {

    }
}
