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

package com.android.settings.darkkat.fragments.button;

import android.content.ContentResolver;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.provider.Settings;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class PowerMenuSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_ADVANCED_REBOOT_MODE = "power_menu_advanced_reboot_mode";
    private static final String PREF_CONFIRM_REBOOT       = "power_menu_confirm_reboot";
    private static final String PREF_CONFIRM_POWER_OFF    = "power_menu_confirm_power_off";

    private ContentResolver mResolver;

    private ListPreference mAdvancedRebootMode;
    private SwitchPreference mConfirmReboot;
    private SwitchPreference mConfirmPowerOff;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.power_menu_settings);

        mResolver = getContentResolver();

        mAdvancedRebootMode = (ListPreference) findPreference(PREF_ADVANCED_REBOOT_MODE);
        final int advancedRebootMode = Settings.System.getInt(mResolver,
                    Settings.System.POWER_MENU_ADVANCED_REBOOT_MODE, 2);
        mAdvancedRebootMode.setValue(String.valueOf(advancedRebootMode));
        mAdvancedRebootMode.setSummary(getAdvancedRebootModeSummary(advancedRebootMode));
        mAdvancedRebootMode.setOnPreferenceChangeListener(this);

        mConfirmReboot =
                (SwitchPreference) findPreference(PREF_CONFIRM_REBOOT);
        mConfirmReboot.setChecked((Settings.System.getInt(mResolver,
                Settings.System.POWER_MENU_CONFIRM_REBOOT, 0) == 1));
        mConfirmReboot.setOnPreferenceChangeListener(this);

        mConfirmPowerOff =
                (SwitchPreference) findPreference(PREF_CONFIRM_POWER_OFF);
        mConfirmPowerOff.setChecked((Settings.System.getInt(mResolver,
                Settings.System.POWER_MENU_CONFIRM_POWER_OFF, 0) == 1));
        mConfirmPowerOff.setOnPreferenceChangeListener(this);
    }

    private String getAdvancedRebootModeSummary(int mode) {
        String[] titles = getResources().getStringArray(
                R.array.power_menu_advanced_reboot_mode_summary_titles);
        return titles[mode];
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean value;

        if (preference == mAdvancedRebootMode) {
            int intValue = Integer.valueOf((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.POWER_MENU_ADVANCED_REBOOT_MODE, intValue);
            mAdvancedRebootMode.setSummary(getAdvancedRebootModeSummary(intValue));
            return true;
        } else if (preference == mConfirmReboot) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.POWER_MENU_CONFIRM_REBOOT, value ? 1 : 0);
            return true;
        } else if (preference == mConfirmPowerOff) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.POWER_MENU_CONFIRM_POWER_OFF, value ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.DARKKAT;
    }
}
