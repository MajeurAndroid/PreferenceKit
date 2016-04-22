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

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Lockable and material dialog preference
 */
public class DialogPreference extends android.preference.DialogPreference implements CommonPreferenceDelegate.Delegatable, Lockable {

    private Context mContext;
    private AlertDialog mDialog;

    private CommonPreferenceDelegate mDelegate;

    public DialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mDelegate = new CommonPreferenceDelegate(this);
        mDelegate.init(context, attrs);
    }

    public DialogPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DialogPreference(Context context) {
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
    public void setLockedText(String s) {
        mDelegate.setLockedText(s);
    }

    @Override
    public void setLockedTextResource(int resId) {
        mDelegate.setLockedTextResource(resId);
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

    @Override
    public Dialog getDialog() {
        return mDialog;
    }

    @Override
    public CharSequence getPositiveButtonText() {
        CharSequence superText = super.getPositiveButtonText();
        return superText != null ? superText : getContext().getText(android.R.string.ok);
    }

    @Override
    public CharSequence getNegativeButtonText() {
        CharSequence superText = super.getNegativeButtonText();
        return superText != null ? superText : getContext().getText(android.R.string.cancel);
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
            scrollView.addView(textView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
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
