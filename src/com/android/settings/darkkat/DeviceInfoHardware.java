/*
 * Copyright (C) 2016 DarkKat
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

package com.android.settings.darkkat;

import android.os.Build;
import android.os.Bundle;

import com.android.internal.logging.MetricsProto.MetricsEvent;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.DeviceInfoUtils;

public class DeviceInfoHardware extends SettingsPreferenceFragment {

    private static final String PREF_DEVICE_MODEL  = "device_model";
    private static final String PREF_DEVICE_CPU    = "device_cpu";
    private static final String PREF_DEVICE_MEMORY = "device_memory";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.device_info_hardware);

        setStringSummary(PREF_DEVICE_MODEL, Build.MODEL);
        setStringSummary(PREF_DEVICE_MODEL, Build.MODEL + DeviceInfoUtils.getMsvSuffix());

        final String cpuInfo = DeviceInfoUtils.getCPUInfo();
        final String memInfo = DeviceInfoUtils.getMemInfo();

        if (cpuInfo != null) {
            setStringSummary(PREF_DEVICE_CPU, cpuInfo);
        } else {
            getPreferenceScreen().removePreference(findPreference(PREF_DEVICE_CPU));
        }

        if (memInfo != null) {
            setStringSummary(PREF_DEVICE_MEMORY, memInfo);
        } else {
            getPreferenceScreen().removePreference(findPreference(PREF_DEVICE_MEMORY));
        }
    }

    private void setStringSummary(String preference, String value) {
        try {
            findPreference(preference).setSummary(value);
        } catch (RuntimeException e) {
            findPreference(preference).setSummary(
                getResources().getString(R.string.device_info_default));
        }
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.DEVICEINFO;
    }
}
