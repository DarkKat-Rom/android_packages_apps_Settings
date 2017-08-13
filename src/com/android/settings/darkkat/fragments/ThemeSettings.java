/*
 * Copyright (C) 2017 DarkKat
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
import android.support.v7.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class ThemeSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "ThemeSettings";

    private static final String PREF_NIGHT_MODE = "night_mode";
    private static final String PREF_NIGHT_AUTO_MODE = "night_auto_mode";
    private static final String PREF_DAY_AUTO_MODE = "day_auto_mode";

    private UiModeManager mUiModeManager;
    private ContentResolver mResolver;

    private ListPreference mNightMode;
    private ListPreference mNightAutoMode;
    private ListPreference mDayAutoMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshSettings();
    }

    public void refreshSettings() {
        PreferenceScreen prefs = getPreferenceScreen();
        if (prefs != null) {
            prefs.removeAll();
        }

        addPreferencesFromResource(R.xml.theme_settings);

        mUiModeManager = (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);
        mResolver = getContentResolver();

        mNightMode = (ListPreference) findPreference(PREF_NIGHT_MODE);
        final int nightMode = mUiModeManager.getNightMode();
        mNightMode.setValue(String.valueOf(nightMode));
        mNightMode.setOnPreferenceChangeListener(this);
        if (nightMode == UiModeManager.MODE_NIGHT_AUTO) {
            mNightMode.setSummary(getAutoNightModeSummary(
                    nightMode, mUiModeManager.getCurrentNightMode()));
        }

        if (nightMode == mUiModeManager.MODE_NIGHT_AUTO) {
            mNightAutoMode = (ListPreference) findPreference(PREF_NIGHT_AUTO_MODE);
            final int nightAutoMode = Settings.Secure.getInt(mResolver,
                    Settings.Secure.UI_NIGHT_AUTO_MODE, UiModeManager.MODE_NIGHT_YES);
            mNightAutoMode.setValue(String.valueOf(nightAutoMode));
            mNightAutoMode.setOnPreferenceChangeListener(this);

            mDayAutoMode = (ListPreference) findPreference(PREF_DAY_AUTO_MODE);
            final int dayAutoMode = Settings.Secure.getInt(mResolver,
                    Settings.Secure.UI_DAY_AUTO_MODE, UiModeManager.MODE_NIGHT_NO);
            mDayAutoMode.setValue(String.valueOf(dayAutoMode));
            mDayAutoMode.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_NIGHT_AUTO_MODE);
            removePreference(PREF_DAY_AUTO_MODE);
        }
    }

    private String getAutoNightModeSummary(int nightMode, int themeAuto) {
        String[] themeTitles = getResources().getStringArray(R.array.theme_summary_titles);
        String summary = themeTitles[nightMode] + " (" + themeTitles[themeAuto] + ")";
        return summary;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int intValue;

        if (preference == mNightMode) {
            try {
                intValue = Integer.parseInt((String) newValue);
                mUiModeManager.setNightMode(intValue);
            } catch (NumberFormatException e) {
                Log.e(TAG, "could not persist night mode setting", e);
            }
            refreshSettings();
        } else if (preference == mNightAutoMode) {
            intValue = Integer.valueOf((String) newValue);
            Settings.Secure.putInt(mResolver,
                    Settings.Secure.UI_NIGHT_AUTO_MODE, intValue);
            return true;
        } else if (preference == mDayAutoMode) {
            intValue = Integer.valueOf((String) newValue);
            Settings.Secure.putInt(mResolver,
                    Settings.Secure.UI_DAY_AUTO_MODE, intValue);
            return true;
        }
        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.DARKKAT;
    }
}
