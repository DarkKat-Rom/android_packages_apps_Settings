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

package com.android.settings.darkkat.fragments;

import android.os.Bundle;
import android.support.v7.preference.Preference;

import com.android.internal.util.darkkat.DeviceUtils;
import com.android.internal.util.darkkat.WeatherHelper;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class StatusBarSettings extends SettingsPreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.status_bar_settings);

        final boolean isWeatherServiceAvailable =
                WeatherHelper.isWeatherServiceAvailable(getActivity());
        final int weatherServiceAvailability =
                WeatherHelper.getWeatherServiceAvailability(getActivity());

        Preference weather = findPreference("status_bar_weather_settings");

        if (weatherServiceAvailability == WeatherHelper.PACKAGE_DISABLED) {
            final CharSequence summary = getResources().getString(DeviceUtils.isPhone(getActivity())
                    ? R.string.weather_service_disabled_summary
                    : R.string.weather_service_disabled_tablet_summary);
            weather.setSummary(summary);
        } else if (weatherServiceAvailability == WeatherHelper.PACKAGE_MISSING) {
            weather.setSummary(getResources().getString(R.string.weather_service_missing_summary));
        }
        weather.setEnabled(isWeatherServiceAvailable);
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.DARKKAT;
    }
}
