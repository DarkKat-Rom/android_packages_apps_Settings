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

import android.app.UiModeManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.util.Log;

import com.android.internal.util.darkkat.DeviceUtils;
import com.android.internal.util.darkkat.WeatherHelper;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class ThemeColorsSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "ThemeColorsSettings";

    private static final String PREF_THEME =
            "theme";

    private ListPreference mTheme;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.theme_colors_settings);
        mResolver = getContentResolver();

        mTheme = (ListPreference) findPreference(PREF_THEME);
        final UiModeManager uiManager = (UiModeManager) getSystemService(
                Context.UI_MODE_SERVICE);
        final int theme = uiManager.getNightMode();
        mTheme.setValue(String.valueOf(theme));
        mTheme.setOnPreferenceChangeListener(this);


        final boolean isWeatherServiceAvailable =
                WeatherHelper.isWeatherServiceAvailable(getActivity());
        final int weatherServiceAvailability = WeatherHelper.getWeatherServiceAvailability(getActivity());

        Preference detailedWeather =
                findPreference("colors_detailed_weather_view");

        if (weatherServiceAvailability == WeatherHelper.PACKAGE_DISABLED) {
            final CharSequence summary = getResources().getString(DeviceUtils.isPhone(getActivity())
                    ? R.string.weather_service_disabled_summary
                    : R.string.weather_service_disabled_tablet_summary);
            detailedWeather.setSummary(summary);
        } else if (weatherServiceAvailability == WeatherHelper.PACKAGE_MISSING) {
            detailedWeather.setSummary(
                    getResources().getString(R.string.weather_service_missing_summary));
        }
        detailedWeather.setEnabled(isWeatherServiceAvailable);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mTheme) {
            try {
                final int value = Integer.parseInt((String) newValue);
                final UiModeManager uiManager = (UiModeManager) getSystemService(
                        Context.UI_MODE_SERVICE);
                uiManager.setNightMode(value);
            } catch (NumberFormatException e) {
                Log.e(TAG, "could not persist night mode setting", e);
            }
        }
        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.DARKKAT;
    }
}
