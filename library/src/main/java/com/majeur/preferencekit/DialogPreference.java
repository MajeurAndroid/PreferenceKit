package com.majeur.preferencekit;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class DialogPreference extends android.preference.DialogPreference {

    private Context mContext;
    private AlertDialog mDialog;

    private boolean mLocked;
    private Drawable mLockedIconDrawable;

    public DialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public DialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.preference_base, 0, 0);
            mLocked = typedArray.getBoolean(R.styleable.preference_base_locked, false);
            mLockedIconDrawable = typedArray.getDrawable(R.styleable.preference_base_lockedIcon);
            typedArray.recycle();
        }
        setLocked(mLocked);

        if (mLockedIconDrawable == null)
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

    @Override
    public Dialog getDialog() {
        return mDialog;
    }

    @Override
    protected void showDialog(Bundle state) {
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DialogPreference.this.onClick(dialogInterface, i);
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setTitle(getDialogTitle())
                .setIcon(getDialogIcon())
                .setOnDismissListener(this)
                .setPositiveButton(getPositiveButtonText(), onClickListener)
                .setNegativeButton(getNegativeButtonText(), onClickListener);

        final View contentView = onCreateDialogView();
        if (contentView != null) {
            onBindDialogView(contentView);
            builder.setView(contentView);
        } else {
            TextView textView = new TextView(getContext(), null, R.style.TextAppearance_AppCompat);
            textView.setText(getDialogMessage());
            ScrollView scrollView = new ScrollView(getContext());
            scrollView.addView(textView, new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            builder.setView(scrollView);
        }

        Utils.registerOnActivityDestroyListener(this, this);

        mDialog = builder.create();
        if (state != null)
            mDialog.onRestoreInstanceState(state);
        mDialog.show();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Utils.unregisterOnActivityDestroyListener(this, this);
    }

    @Override
    public void onActivityDestroy() {
        super.onActivityDestroy();
        if (mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        Dialog dialog = getDialog();
        if (dialog == null || !dialog.isShowing()) {
            return superState;
        }

        final SavedState myState = new SavedState(superState);
        myState.isDialogShowing = true;
        myState.dialogBundle = dialog.onSaveInstanceState();
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        if (myState.isDialogShowing) {
            showDialog(myState.dialogBundle);
        }
    }

    // From DialogPreference
    private static class SavedState extends BaseSavedState {
        boolean isDialogShowing;
        Bundle dialogBundle;

        public SavedState(Parcel source) {
            super(source);
            isDialogShowing = source.readInt() == 1;
            dialogBundle = source.readBundle();
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(isDialogShowing ? 1 : 0);
            dest.writeBundle(dialogBundle);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public static final Creator<SavedState> CREATOR =
                new Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}
