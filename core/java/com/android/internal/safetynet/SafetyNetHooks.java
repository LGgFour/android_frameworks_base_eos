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
    private static final String SPOOFED_MODEL = "Pixel 2";
    private static final String SPOOFED_DEVICE_PRODUCT = "walleye";
    private static final String SPOOFED_FINGERPRINT = "google/walleye/walleye:8.1.0/OPM1.171019.011/4448085:user/release-keys";

    private static volatile boolean sIsGms = false;

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
            sIsGms = true;
            setBuildField("MODEL", SPOOFED_MODEL);
            setBuildField("DEVICE", SPOOFED_DEVICE_PRODUCT);
            setBuildField("PRODUCT", SPOOFED_DEVICE_PRODUCT);
            setBuildField("FINGERPRINT", SPOOFED_FINGERPRINT);
        }
    }

    private static boolean isCallerSafetyNet() {
        return Arrays.stream(Thread.currentThread().getStackTrace())
                .anyMatch(elem -> elem.getClassName().contains("DroidGuard"));
    }

    public static void onEngineGetCertificateChain() {
        // Check stack for SafetyNet
        if (sIsGms && isCallerSafetyNet()) {
            throw new UnsupportedOperationException();
        }
    }
}
