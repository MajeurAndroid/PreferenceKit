package com.majeur.preferencekit;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class Preference extends android.preference.Preference implements CommonPreferenceDelegate.Delegatable, Lockable {

    private CommonPreferenceDelegate mDelegate;

    public Preference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDelegate = new CommonPreferenceDelegate(this);
        mDelegate.init(context, attrs);
    }

    public Preference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Preference(Context context) {
        this(context, null);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        return mDelegate.onCreateView(parent);
    }

    @Override
    protected void onBindView(View view) {
        mDelegate.onBindView(view);
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
}
