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

    private int DEFAULT_VALUE = Utils.COLOR_ACCENT;
    private int mValue;

    private ColorPicker mColorPickerView;

    private CircleView mColorIndicator;
    private boolean mAlphaAllowed, mSVAllowed;

    public ColorPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.preference_colorpicker, 0, 0);
        mAlphaAllowed = typedArray.getBoolean(R.styleable.preference_colorpicker_alphaAllowed, true);
        mSVAllowed = typedArray.getBoolean(R.styleable.preference_colorpicker_saturationAndValueAllowed, true);
        typedArray.recycle();
    }

    public void setAlphaAllowed(boolean allowed) {
        mAlphaAllowed = allowed;
    }

    /**
     * Provide the default value to the system
     */
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getColor(index, DEFAULT_VALUE);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        int value = restorePersistedValue ? getPersistedInt(DEFAULT_VALUE) : (Integer) defaultValue;
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

        mColorPickerView = (ColorPicker) view.findViewById(R.id.colorPicker);
        OpacityBar mOpacityBar = (OpacityBar) view.findViewById(R.id.opacityBar);
        SVBar svBar = (SVBar) view.findViewById(R.id.svBar);

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
        mColorIndicator = new CircleView(getContext());

        ViewGroup stub = (ViewGroup) view.findViewById(R.id.stub);
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