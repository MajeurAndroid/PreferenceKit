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
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

/**
 * Simple class that provide built-in preference with numberPicker as dialog
 */
public class NumberPickerPreference extends CirclePickerPreference {

    private int mDividersColor;

    private boolean mWrapSelectorWheel, mEditableValue;

    private NumberPicker mNumberPicker;

    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.preference_numberpicker, defStyleAttr, 0);
            mWrapSelectorWheel = typedArray.getBoolean(R.styleable.preference_numberpicker_wrapSelectorWheel, false);
            mEditableValue = typedArray.getBoolean(R.styleable.preference_numberpicker_editableValue, false);
            mDividersColor = typedArray.getColor(R.styleable.preference_numberpicker_selectionIndicatorsColor,
                    Utils.getAttrColor(context, R.attr.colorAccent));
            typedArray.recycle();
        }
    }

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberPickerPreference(Context context) {
        this(context, null, 0);
    }

    @Override
    protected View onCreateDialogView() {
        mNumberPicker = new NumberPicker(getContext());

        Drawable indicator = Utils.getDividerDrawable(mNumberPicker);
        if (indicator != null)
            indicator.setColorFilter(mDividersColor, PorterDuff.Mode.SRC_IN);

        return mNumberPicker;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        mNumberPicker.setMaxValue(mMax);
        mNumberPicker.setMinValue(mMin);
        mNumberPicker.setValue(mValue);
        mNumberPicker.setWrapSelectorWheel(mWrapSelectorWheel);
        mNumberPicker.setDescendantFocusability(mEditableValue ?
                NumberPicker.FOCUS_AFTER_DESCENDANTS : NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult)
            setNewValue(mNumberPicker.getValue());

    }

    /**
     * Sets whether the selector wheel shown during flinging/scrolling should wrap around the getMinValue() and getMaxValue() values.
     * By default if the range (max - min) is more than the number of items shown on the selector wheel the selector wheel wrapping is enabled.
     * Note: If the number of items, i.e. the range ( getMaxValue() - getMinValue()) is less than the number of items shown on the selector wheel, the selector wheel will not wrap. Hence, in such a case calling this method is a NOP.
     * @param wrapSelectorWheel Whether to wrap
     */
    public void setWrapSelectorWheel(boolean wrapSelectorWheel) {
        mWrapSelectorWheel = wrapSelectorWheel;
    }

    /**
     * Set if the value in {@link NumberPicker} should be editable through soft input
     * @param editableValue Whether to be editable
     */
    public void setEditableValue(boolean editableValue) {
        mEditableValue = editableValue;
    }
}