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

package com.android.settings.darkkat.fragments.lockscreen;

import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.preference.Preference;
import android.support.v14.preference.SwitchPreference;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class BatteryInfoSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_SHOW_BATTERY_INFO =
            "lock_screen_show_battery_info";
    private static final String PREF_SHOW_BATTERY_TEMP =
            "lock_screen_show_battery_temp";
    private static final String PREF_SHOW_BATTERY_CHARGING_INFO =
            "lock_screen_show_battery_charging_info";
    private static final String PREF_SHOW_ADVANCED_BATTERY_CHARGING_INFO =
            "lock_screen_show_advanced_battery_charging_info";

    private SwitchPreference mShowBatteryInfo;
    private SwitchPreference mShowBatteryTemp;
    private SwitchPreference mShowBatteryChargingInfo;
    private SwitchPreference mShowAdvancedBatteryChargingInfo;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.lock_screen_battery_info_settings);

        mResolver = getContentResolver();

        mShowBatteryInfo =
                (SwitchPreference) findPreference(PREF_SHOW_BATTERY_INFO);
        mShowBatteryInfo.setChecked(Settings.System.getInt(mResolver,
                Settings.System.LOCK_SCREEN_SHOW_BATTERY_INFO, 0) == 1);
        mShowBatteryInfo.setOnPreferenceChangeListener(this);

        mShowBatteryTemp =
                (SwitchPreference) findPreference(PREF_SHOW_BATTERY_TEMP);
        mShowBatteryTemp.setChecked(Settings.System.getInt(mResolver,
                Settings.System.LOCK_SCREEN_SHOW_BATTERY_TEMP, 0) == 1);
        mShowBatteryTemp.setOnPreferenceChangeListener(this);

        mShowBatteryChargingInfo =
                (SwitchPreference) findPreference(PREF_SHOW_BATTERY_CHARGING_INFO);
        mShowBatteryChargingInfo.setChecked(Settings.System.getInt(mResolver,
                Settings.System.LOCK_SCREEN_SHOW_BATTERY_CHARGING_INFO, 1) == 1);
        mShowBatteryChargingInfo.setOnPreferenceChangeListener(this);

        mShowAdvancedBatteryChargingInfo =
                (SwitchPreference) findPreference(PREF_SHOW_ADVANCED_BATTERY_CHARGING_INFO);
        mShowAdvancedBatteryChargingInfo.setChecked(Settings.System.getInt(mResolver,
                Settings.System.LOCK_SCREEN_SHOW_ADVANCED_BATTERY_CHARGING_INFO, 0) == 1);
        mShowAdvancedBatteryChargingInfo.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        boolean value;

        if (preference == mShowBatteryInfo) {
            value = (Boolean) objValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_SHOW_BATTERY_INFO, value ? 1 : 0);
            return true;
        } else if (preference == mShowBatteryTemp) {
            value = (Boolean) objValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_SHOW_BATTERY_TEMP, value ? 1 : 0);
            return true;
        } else if (preference == mShowBatteryChargingInfo) {
            value = (Boolean) objValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_SHOW_BATTERY_CHARGING_INFO, value ? 1 : 0);
            return true;
        } else if (preference == mShowAdvancedBatteryChargingInfo) {
            value = (Boolean) objValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_SHOW_ADVANCED_BATTERY_CHARGING_INFO, value ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.DARKKAT;
    }
}
