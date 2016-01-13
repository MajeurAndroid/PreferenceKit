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
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Preference that provide extra buttons, it does not provide
 * any persist data feature, it just provide buttons.
 * Can be useful for opening intents or whatever that don't need to
 * persist some data.
 * Buttons clicks can be listened through {@link OnExtraButtonClickListener}
 */
public class ExtraButtonsPreference extends Preference {

    private CharSequence[] mLabels;
    private LinearLayout mContainer;
    private LayoutInflater mInflater;
    private OnExtraButtonClickListener mListener;

    public ExtraButtonsPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public ExtraButtonsPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ExtraButtonsPreference(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.preference_extrabuttons, 0, 0);
            mLabels = typedArray.getTextArray(R.styleable.preference_extrabuttons_buttonLabels);
            typedArray.recycle();
        }

        mInflater = LayoutInflater.from(context);
    }

    /**
     * Manually set button labels
     *
     * @param labels Button labels
     */
    public void setButtonLabels(CharSequence... labels) {
        mLabels = labels;
        setButtons();
    }

    /**
     * Manually set button labels
     *
     * @param labelsResId Button labels resource id
     */
    public void setButtonLabels(int labelsResId) {
        mLabels = getContext().getResources().getTextArray(labelsResId);
        setButtons();
    }

    /**
     * Set the extra buttons listener
     */
    public void setOnExtraButtonClickListener(OnExtraButtonClickListener l) {
        mListener = l;
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = super.onCreateView(parent);

        ViewGroup container = (ViewGroup) view.findViewById(R.id.pk_bottom_container);
        mInflater.inflate(R.layout.preference_extrabuttons, container);

        return view;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        mContainer = (LinearLayout) view.findViewById(R.id.pk_extra_container);
        setButtons();
    }

    private void setButtons() {
        if (!Utils.isArrayEmpty(mLabels) && mContainer != null) {
            for (int i = 0; i < mLabels.length; i++) {
                Button button = (Button) mInflater.inflate(R.layout.simple_button, mContainer, false);
                button.setTag(i);
                button.setEnabled(isEnabled());
                button.setOnClickListener(mButtonsClickListener);
                button.setText(mLabels[i]);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.weight = 1;
                mContainer.addView(button, lp);
            }
        }
    }

    private View.OnClickListener mButtonsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mListener != null)
                mListener.onExtraButtonClick((Integer) view.getTag());
        }
    };

    public interface OnExtraButtonClickListener {
        public void onExtraButtonClick(int which);
    }
}