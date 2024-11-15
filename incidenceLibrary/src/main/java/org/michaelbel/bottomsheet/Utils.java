/*
 * Copyright 2016 Michael Bely
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.michaelbel.bottomsheet;

import android.content.Context;

import androidx.annotation.NonNull;

import com.e510.incidencelibrary.R;

/**
 * Date: 17 FEB 2018
 * Time: 00:32 MSK
 *
 * @author Michael Bel
 */

@SuppressWarnings("all")
public class Utils {

    public static int dp(@NonNull Context context, float value) {
        return (int) Math.ceil(context.getResources().getDisplayMetrics().density * value);
    }

    public static boolean isTablet(@NonNull Context context) {
        return context.getResources().getBoolean(R.bool.tablet);
    }
}