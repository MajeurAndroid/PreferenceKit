package com.majeur.preferencekit;

import android.graphics.drawable.Drawable;

/**
 * Interface to define the contract to let a preference to be locked.
 * For example, locked because of a Premium version of your app.
 */
interface Lockable {

    void setLockedIcon(Drawable drawable);

    void setLockedIconResource(int resId);

    void setLocked(boolean locked);

    boolean isLocked();
}
