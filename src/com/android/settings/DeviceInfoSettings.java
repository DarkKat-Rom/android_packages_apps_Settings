/*
 * Copyright (C) 2008 The Android Open Source Project
 *
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

package com.android.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.support.v7.preference.Preference;
import android.text.TextUtils;

import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.settings.dashboard.SummaryLoader;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Index;
import com.android.settings.search.Indexable;
import com.android.settingslib.DeviceInfoUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeviceInfoSettings extends SettingsPreferenceFragment implements Indexable {
    private static final String PROP_DK_VERSION               = "ro.dk.version";

    private static final String PREF_MANUAL          = "manual";
    private static final String PREF_REGULATORY_INFO = "regulatory_info";
    private static final String PREF_DEVICE_FEEDBACK = "device_feedback";
    private static final String PREF_ABOUT_HARDWARE  = "about_hardware";
    private static final String PREF_ABOUT_SOFTWARE  = "about_software";
    private static final String PREF_ABOUT_DARKKAT   = "about_darkkat";

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.DEVICEINFO;
    }

    @Override
    protected int getHelpResource() {
        return R.string.help_uri_about;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.device_info_settings);

        /*
         * Settings is a generic app and should not contain any device-specific
         * info.
         */

        // Remove manual entry if none present.
        removePreferenceIfBoolFalse(PREF_MANUAL, R.bool.config_show_manual);

        // Remove regulatory information if none present.
        final Intent intent = new Intent(Settings.ACTION_SHOW_REGULATORY_INFO);
        if (getPackageManager().queryIntentActivities(intent, 0).isEmpty()) {
            Preference pref = findPreference(PREF_REGULATORY_INFO);
            if (pref != null) {
                getPreferenceScreen().removePreference(pref);
            }
        }

        // Dont show feedback option if there is no reporter.
        if (TextUtils.isEmpty(DeviceInfoUtils.getFeedbackReporterPackage(getActivity()))) {
            getPreferenceScreen().removePreference(findPreference(PREF_DEVICE_FEEDBACK));
        }

        final String summaryHardware = getResources().getString(
                R.string.about_hardware_summary, Build.MODEL);
        final String summarySoftware = Utils.isWifiOnly(getActivity())
                ? getResources().getString(R.string.about_software_wifi_only_summary)
                : getResources().getString(R.string.about_software_summary);
        final String summaryDarkKat = getResources().getString(
                R.string.about_darkkat_summary, getPropValue(PROP_DK_VERSION));

        setStringSummary(PREF_ABOUT_HARDWARE, summaryHardware);
        setStringSummary(PREF_ABOUT_SOFTWARE, summarySoftware);
        setStringSummary(PREF_ABOUT_DARKKAT, summaryDarkKat);
    }

    private void setStringSummary(String preference, String value) {
        try {
            findPreference(preference).setSummary(value);
        } catch (RuntimeException e) {
            findPreference(preference).setSummary(
                getResources().getString(R.string.device_info_default));
        }
    }

    private static String getPropValue(String property) {
        return SystemProperties.get(property);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.getKey().equals(PREF_DEVICE_FEEDBACK)) {
            sendFeedback();
        }
        return super.onPreferenceTreeClick(preference);
    }

    private void removePreferenceIfBoolFalse(String preference, int resId) {
        if (!getResources().getBoolean(resId)) {
            Preference pref = findPreference(preference);
            if (pref != null) {
                getPreferenceScreen().removePreference(pref);
            }
        }
    }

    private void sendFeedback() {
        String reporterPackage = DeviceInfoUtils.getFeedbackReporterPackage(getActivity());
        if (TextUtils.isEmpty(reporterPackage)) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_BUG_REPORT);
        intent.setPackage(reporterPackage);
        startActivityForResult(intent, 0);
    }

    private static class SummaryProvider implements SummaryLoader.SummaryProvider {

        private final Context mContext;
        private final SummaryLoader mSummaryLoader;

        public SummaryProvider(Context context, SummaryLoader summaryLoader) {
            mContext = context;
            mSummaryLoader = summaryLoader;
        }

        @Override
        public void setListening(boolean listening) {
            if (listening) {
                mSummaryLoader.setSummary(this, mContext.getString(R.string.about_summary_darkkat,
                        Build.MODEL, Build.VERSION.RELEASE, getPropValue(PROP_DK_VERSION)));
            }
        }
    }

    public static final SummaryLoader.SummaryProviderFactory SUMMARY_PROVIDER_FACTORY
            = new SummaryLoader.SummaryProviderFactory() {
        @Override
        public SummaryLoader.SummaryProvider createSummaryProvider(Activity activity,
                                                                   SummaryLoader summaryLoader) {
            return new SummaryProvider(activity, summaryLoader);
        }
    };

    /**
     * For Search.
     */
    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
        new BaseSearchIndexProvider() {

            @Override
            public List<SearchIndexableResource> getXmlResourcesToIndex(
                    Context context, boolean enabled) {
                final SearchIndexableResource sir = new SearchIndexableResource(context);
                sir.xmlResId = R.xml.device_info_settings;
                return Arrays.asList(sir);
            }

            @Override
            public List<String> getNonIndexableKeys(Context context) {
                final List<String> keys = new ArrayList<String>();
                // Dont show feedback option if there is no reporter.
                if (TextUtils.isEmpty(DeviceInfoUtils.getFeedbackReporterPackage(context))) {
                    keys.add(PREF_DEVICE_FEEDBACK);
                }
                return keys;
            }
        };
}
