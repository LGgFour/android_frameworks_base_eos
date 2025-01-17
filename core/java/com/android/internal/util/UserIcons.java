/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.internal.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;

import com.android.internal.R;

/**
 * Helper class that generates default user icons.
 */
public class UserIcons {

    private static final int[] USER_ICON_DRAWABLES = {
        R.drawable.ic_user_icon_0,
        R.drawable.ic_user_icon_1,
        R.drawable.ic_user_icon_2,
        R.drawable.ic_user_icon_3,
        R.drawable.ic_user_icon_4,
        R.drawable.ic_user_icon_5,
        R.drawable.ic_user_icon_6,
        R.drawable.ic_user_icon_7
    };

    /**
     * Converts a given drawable to a bitmap.
     */
    public static Bitmap convertToBitmap(Drawable icon) {
        if (icon == null) {
            return null;
        }
        final int width = icon.getIntrinsicWidth();
        final int height = icon.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        icon.setBounds(0, 0, width, height);
        icon.draw(canvas);
        return bitmap;
    }

    /**
     * Returns a default user icon for the given user.
     *
     * Note that for guest users, you should pass in {@code UserHandle.USER_NULL}.
     *
     * @param resources resources object to fetch user icon / color.
     * @param userId the user id or {@code UserHandle.USER_NULL} for a non-user specific icon
     * @param light whether we want a light icon (suitable for a dark background)
     */
    public static Drawable getDefaultUserIcon(Resources resources, int userId, boolean light) {
        int drawableResId = R.drawable.ic_user_icon_gray;
        if (userId != UserHandle.USER_NULL) {
            // Return colored icon instead
            drawableResId = USER_ICON_DRAWABLES[userId % USER_ICON_DRAWABLES.length];
        }
        Drawable icon = resources.getDrawable(drawableResId, null);
        icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
        return icon;
    }
}
