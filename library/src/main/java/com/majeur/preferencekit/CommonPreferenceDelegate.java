package com.majeur.preferencekit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.preference.Preference;

/**
 * Class used to keep one piece of code when we extend multiple framework classes.
 * For example, we extend {@link Preference} and {@link android.preference.DialogPreference}, but
 * the same code has to be present in these to classes.
 */
class CommonPreferenceDelegate implements Lockable {

    private Preference mPreference;

    private boolean mLocked;
    private Drawable mLockedIconDrawable;

    CommonPreferenceDelegate(Preference preference) {
        if (!(preference instanceof Delegatable))
            throw new IllegalArgumentException("Preference " + preference.getClass().getSimpleName()
                    + " doesn't implements Delegatable interface");
        mPreference = preference;
    }

    public void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.preference_base, 0, 0);
            mLocked = typedArray.getBoolean(R.styleable.preference_base_locked, false);
            mLockedIconDrawable = typedArray.getDrawable(R.styleable.preference_base_lockedIcon);
            typedArray.recycle();
        }
        setLocked(mLocked);

        if (mLockedIconDrawable == null) // We set the default icon
            mLockedIconDrawable = mPreference.getContext().getResources().getDrawable(R.drawable.lock24);
    }

    public View onCreateView(ViewGroup parent) {
        return LayoutInflater.from(mPreference.getContext()).inflate(R.layout.preference_base, parent, false);
    }

    public void onBindView(View view) {
        ((Delegatable) mPreference).superOnBindView(view);
        ImageView imageView = (ImageView) view.findViewById(R.id.locked_icon);
        imageView.setImageDrawable(mLocked ? mLockedIconDrawable : null);
    }

    @Override
    public void setLockedIcon(Drawable drawable) {
        mLockedIconDrawable = drawable;
    }

    @Override
    public void setLockedIconResource(int resId) {
        mLockedIconDrawable = mPreference.getContext().getResources().getDrawable(resId);
    }

    @Override
    public void setLocked(boolean locked) {
        mLocked = locked;
        ((Delegatable) mPreference).superSetEnabled(!locked);
        ((Delegatable) mPreference).notifyChangedInternal();
    }

    @Override
    public boolean isLocked() {
        return mLocked;
    }


    public void setEnabled(boolean enabled) {
        if (!mLocked)
            ((Delegatable) mPreference).superSetEnabled(enabled);
    }

    interface Delegatable {

        void superSetEnabled(boolean enabled);

        void superOnBindView(View view);

        void notifyChangedInternal();
    }
}
