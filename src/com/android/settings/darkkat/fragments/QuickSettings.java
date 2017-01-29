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

import android.content.ContentResolver;
import android.provider.Settings;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class QuickSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener { 

    private static final String PREF_BRIGHTNESS_SLIDER_VISIBILITY =
            "quick_settings_brightness_slider_visibility";

    private ListPreference mBrightnessSliderVisibility;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.quick_settings);
        mResolver = getActivity().getContentResolver();

        mBrightnessSliderVisibility = (ListPreference) findPreference(PREF_BRIGHTNESS_SLIDER_VISIBILITY);
        int brightnessSliderVisibility = Settings.System.getInt(mResolver,
                Settings.System.QS_BRIGHTNESS_SLIDER_VISIBILITY, 2);
        mBrightnessSliderVisibility.setValue(String.valueOf(brightnessSliderVisibility));
        mBrightnessSliderVisibility.setSummary(mBrightnessSliderVisibility.getEntry());
        mBrightnessSliderVisibility.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mBrightnessSliderVisibility) {
            int intValue = Integer.valueOf((String) newValue);
            int index = mBrightnessSliderVisibility.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.QS_BRIGHTNESS_SLIDER_VISIBILITY, intValue);
            mBrightnessSliderVisibility.setSummary(mBrightnessSliderVisibility.getEntries()[index]);
            return true;
        }
        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.DARKKAT;
    }
}
