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

import android.content.ContentResolver;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;
import android.view.Gravity;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;


public class RecentsSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener { 

    private static final String PREF_CAT_APPEARENCE =
            "recents_cat_appearance";
    private static final String PREF_CAT_APPS =
            "recents_cat_apps";
    private static final String PREF_USE_SLIM_RECENTS =
            "recents_use_slim_recents";
    private static final String PREF_SLIM_RECENTS_THUMBNAIL_ASPECT_RATIO =
            "slim_recents_thumbnail_aspect_ratio";
    private static final String PREF_SLIM_RECENTS_EXPANDED_MODE =
            "slim_recents_expanded_mode";
    private static final String PREF_SLIM_RECENTS_SHOW_ACTIONS_WHEN_COLLAPSED =
            "slim_recents_show_actions_when_collapsed";
    private static final String PREF_SLIM_RECENTS_LEFTY_MODE =
            "slim_recents_lefty_mode";
    private static final String PREF_SLIM_RECENTS_SHOW_ONLY_RUNNING_TASKS =
            "slim_recents_only_show_running_tasks";
    private static final String PREF_SLIM_RECENTS_SHOW_TOPMOST =
            "slim_recents_show_topmost";

    private SwitchPreference mUseSlimRecents;
    private ListPreference mSlimRecentsThumbnaiAspectRatio;
    private ListPreference mSlimRecentsExpandedMode;
    private SwitchPreference mShowActionsWhenCollapsed;
    private SwitchPreference mSlimRecentsLeftyMode;
    private SwitchPreference mSlimRecentsOnlyShowRunningTasks;
    private SwitchPreference mSlimRecentsShowTopmost;

    private ContentResolver mResolver;

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

        addPreferencesFromResource(R.xml.recents_settings);

        mResolver = getContentResolver();

        boolean useSlimRecents = Settings.System.getInt(mResolver,
               Settings.System.USE_SLIM_RECENTS, 0) == 1;

        mUseSlimRecents = (SwitchPreference) findPreference(PREF_USE_SLIM_RECENTS);
        mUseSlimRecents.setChecked(useSlimRecents);
        mUseSlimRecents.setOnPreferenceChangeListener(this);

        PreferenceCategory catAppearance =
                (PreferenceCategory) findPreference(PREF_CAT_APPEARENCE);
        PreferenceCategory catApps =
                (PreferenceCategory) findPreference(PREF_CAT_APPS);

        if (useSlimRecents) {
            mSlimRecentsThumbnaiAspectRatio =
                    (ListPreference) findPreference(PREF_SLIM_RECENTS_THUMBNAIL_ASPECT_RATIO);
            int scale = Settings.System.getInt(mResolver,
                   Settings.System.SLIM_RECENTS_THUMBNAIL_ASPECT_RATIO, 0);
            mSlimRecentsThumbnaiAspectRatio.setValue(String.valueOf(scale));
            mSlimRecentsThumbnaiAspectRatio.setSummary(mSlimRecentsThumbnaiAspectRatio.getEntry());
            mSlimRecentsThumbnaiAspectRatio.setOnPreferenceChangeListener(this);

            mSlimRecentsExpandedMode =
                    (ListPreference) findPreference(PREF_SLIM_RECENTS_EXPANDED_MODE);
            int expandedMode = Settings.System.getInt(mResolver,
                   Settings.System.SLIM_RECENTS_PANEL_EXPANDED_MODE, 0);
            mSlimRecentsExpandedMode.setValue(String.valueOf(expandedMode));
            mSlimRecentsExpandedMode.setSummary(mSlimRecentsExpandedMode.getEntry());
            mSlimRecentsExpandedMode.setOnPreferenceChangeListener(this);

            mShowActionsWhenCollapsed =
                    (SwitchPreference) findPreference(PREF_SLIM_RECENTS_SHOW_ACTIONS_WHEN_COLLAPSED);
            mShowActionsWhenCollapsed.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.SLIM_RECENTS_SHOW_ACTIONS_WHEN_COLLAPSED, 0) == 1);
            mShowActionsWhenCollapsed.setOnPreferenceChangeListener(this);

            mSlimRecentsLeftyMode =
                    (SwitchPreference) findPreference(PREF_SLIM_RECENTS_LEFTY_MODE);
            mSlimRecentsLeftyMode.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.SLIM_RECENTS_PANEL_GRAVITY, Gravity.RIGHT) == Gravity.LEFT);
            mSlimRecentsLeftyMode.setOnPreferenceChangeListener(this);

            mSlimRecentsOnlyShowRunningTasks =
                    (SwitchPreference) findPreference(PREF_SLIM_RECENTS_SHOW_ONLY_RUNNING_TASKS);
            mSlimRecentsOnlyShowRunningTasks.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.SLIM_RECENTS_SHOW_RUNNING_TASKS, 0) == 1);
            mSlimRecentsOnlyShowRunningTasks.setOnPreferenceChangeListener(this);

            mSlimRecentsShowTopmost = (SwitchPreference) findPreference(PREF_SLIM_RECENTS_SHOW_TOPMOST);
            mSlimRecentsShowTopmost.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.SLIM_RECENTS_PANEL_SHOW_TOPMOST, 0) == 1);
            mSlimRecentsShowTopmost.setOnPreferenceChangeListener(this);

            boolean screenPinningEnabled = Settings.System.getInt(mResolver,
                    Settings.System.LOCK_TO_APP_ENABLED, 0) == 1;
            if (screenPinningEnabled) {
                mSlimRecentsShowTopmost.setSummary(
                        R.string.slim_recents_show_topmost_pinning_enabled_summary);
                mSlimRecentsShowTopmost.setEnabled(false);
            }
        } else {
            catAppearance.removePreference(findPreference(PREF_SLIM_RECENTS_THUMBNAIL_ASPECT_RATIO));
            catAppearance.removePreference(findPreference(PREF_SLIM_RECENTS_EXPANDED_MODE));
            catAppearance.removePreference(findPreference(PREF_SLIM_RECENTS_SHOW_ACTIONS_WHEN_COLLAPSED));
            catAppearance.removePreference(findPreference(PREF_SLIM_RECENTS_LEFTY_MODE));
            catApps.removePreference(findPreference(PREF_SLIM_RECENTS_SHOW_ONLY_RUNNING_TASKS));
            catApps.removePreference(findPreference(PREF_SLIM_RECENTS_SHOW_TOPMOST));
            removePreference(PREF_CAT_APPEARENCE);
            removePreference(PREF_CAT_APPS);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean value ;
        int intValue;
        int index;

        if (preference == mUseSlimRecents) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.USE_SLIM_RECENTS,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mSlimRecentsThumbnaiAspectRatio) {
            intValue = Integer.valueOf((String) newValue);
            index = mSlimRecentsThumbnaiAspectRatio.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.SLIM_RECENTS_THUMBNAIL_ASPECT_RATIO, intValue);
            mSlimRecentsThumbnaiAspectRatio.setSummary(
                    mSlimRecentsThumbnaiAspectRatio.getEntries()[index]);
            return true;
        } else if (preference == mSlimRecentsExpandedMode) {
            intValue = Integer.valueOf((String) newValue);
            index = mSlimRecentsExpandedMode.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.SLIM_RECENTS_PANEL_EXPANDED_MODE, intValue);
            mSlimRecentsExpandedMode.setSummary(mSlimRecentsExpandedMode.getEntries()[index]);
            return true;
        } else if (preference == mShowActionsWhenCollapsed) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.SLIM_RECENTS_SHOW_ACTIONS_WHEN_COLLAPSED,
                    value ? 1 : 0);
            return true;
        } else if (preference == mSlimRecentsLeftyMode) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.SLIM_RECENTS_PANEL_GRAVITY,
                    value ? Gravity.LEFT : Gravity.RIGHT);
            return true;
        } else if (preference == mSlimRecentsOnlyShowRunningTasks) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.SLIM_RECENTS_SHOW_RUNNING_TASKS,
                    value ? 1 : 0);
            return true;
        } else if (preference == mSlimRecentsShowTopmost) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.SLIM_RECENTS_PANEL_SHOW_TOPMOST,
                    value ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.DARKKAT;
    }
}
