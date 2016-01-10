package com.majeur.preferencekit;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Preference that allow user to pick a color
 */
public class ColorPickerPreference extends DialogPreference {

    private int DEFAULT_VALUE = Utils.COLOR_ACCENT;
    private int mValue;
    private ColorPickerView mColorPickerView;
    private CircleView mColorIndicator;
    private boolean mAlphaAllowed;

    public ColorPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.preference_colorpicker, 0, 0);
        mAlphaAllowed = typedArray.getBoolean(R.styleable.preference_colorpicker_alphaAllowed, true);
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
        return a.getInt(index, DEFAULT_VALUE);
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
        mColorPickerView = new ColorPickerView(getContext());

        int p = Utils.dpToPx(getContext(), 20);
        mColorPickerView.setPadding(p, p, p, p);

        return mColorPickerView;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        ((ColorPickerView) view).setColor(mValue);
        ((ColorPickerView) view).setAlphaAllowed(mAlphaAllowed);
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