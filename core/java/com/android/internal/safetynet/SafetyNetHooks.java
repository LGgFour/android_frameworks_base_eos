/*
 * Copyright (C) 2022 The Android Open Source Project
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

package com.android.internal.safetynet;

import java.lang.reflect.Field;
import java.util.Arrays;

import android.app.Application;
import android.os.Build;
import android.util.Log;

public final class SafetyNetHooks {

    private static final String TAG = "SafetyNetHooks";
    private static final String GMS_PACKAGE_NAME = "com.google.android.gms";

    private static void setBuildField(String key, String value) {
        try {
            Field field = Build.class.getDeclaredField(key);
            field.setAccessible(true);
            field.set(null, value);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e(TAG, "Failed to fake Build." + key, e);
        }
    }

    public static void init(Application app) {
        if (GMS_PACKAGE_NAME.equals(app.getPackageName())) {
            setBuildField("MODEL", Build.MODEL + " ");
        }
    }
}
