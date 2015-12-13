package com.majeur.preferencekit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class Preference extends android.preference.Preference {

    private boolean mLocked;
    private Drawable mLockedIconDrawable;

    public Preference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public Preference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Preference(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.preference_base, 0, 0);
            mLocked = typedArray.getBoolean(R.styleable.preference_base_locked, false);
            mLockedIconDrawable = typedArray.getDrawable(R.styleable.preference_base_lockedIcon);
            typedArray.recycle();
        }
        setLocked(mLocked);

        if (mLockedIconDrawable == null) // We set the default icon
            mLockedIconDrawable = getContext().getResources().getDrawable(R.drawable.lock24);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        return LayoutInflater.from(getContext()).inflate(R.layout.preference_base, parent, false);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        ImageView imageView = (ImageView) view.findViewById(R.id.locked_icon);
        imageView.setImageDrawable(mLocked ? mLockedIconDrawable : null);
    }

    public void setLockedIcon(Drawable drawable) {
        mLockedIconDrawable = drawable;
    }

    public void setLockedIconResource(int resId) {
        mLockedIconDrawable = getContext().getResources().getDrawable(resId);
    }

    public void setLocked(boolean locked) {
        mLocked = locked;
        super.setEnabled(!locked);
        notifyChanged();
    }

    public boolean isLocked() {
        return mLocked;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (!mLocked)
            super.setEnabled(enabled);
    }
}
