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

package com.android.settings.darkkat.fragments.deviceinfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SELinux;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.settings.DevelopmentSettings;
import com.android.settings.InstrumentedFragment;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.search.Index;
import com.android.settingslib.DeviceInfoUtils;
import com.android.settingslib.RestrictedLockUtils;

import static com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;

public class Software extends SettingsPreferenceFragment {
    private static final String LOG_TAG = "DeviceInfoSoftware";

    private static final String PROPERTY_SELINUX_STATUS =
            "ro.build.selinux";
    private static final String PROPERTY_EQUIPMENT_ID =
            "ro.ril.fccid";

    private static final String PREF_FIRMWARE_VERSION =
            "firmware_version";
    private static final String PREF_BUILD_ID =
            "build_id";
    private static final String PREF_SECURITY_PATCH =
            "security_patch";
    private static final String PREF_KERNEL_VERSION =
            "kernel_version";
    private static final String PREF_EQUIPMENT_ID =
            "fcc_equipment_id";
    private static final String PREF_BASEBAND_VERSION =
            "baseband_version";
    private static final String PREF_BUILD_NUMBER =
            "build_number";
    private static final String PREF_SELINUX_STATUS =
            "selinux_status";

    static final int TAPS_TO_BE_A_DEVELOPER = 7;

    long[] mHits = new long[3];
    int mDevHitCountdown;
    Toast mDevHitToast;

    private UserManager mUm;

    private EnforcedAdmin mFunDisallowedAdmin;
    private boolean mFunDisallowedBySystem;
    private EnforcedAdmin mDebuggingFeaturesDisallowedAdmin;
    private boolean mDebuggingFeaturesDisallowedBySystem;

    @Override
    protected int getHelpResource() {
        return R.string.help_uri_about;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        mUm = UserManager.get(getActivity());

        addPreferencesFromResource(R.xml.device_info_software);

        setStringSummary(PREF_FIRMWARE_VERSION, Build.VERSION.RELEASE);
        findPreference(PREF_FIRMWARE_VERSION).setEnabled(true);
        setStringSummary(PREF_BUILD_ID, Build.ID);

        final String patch = DeviceInfoUtils.getSecurityPatch();
        if (!TextUtils.isEmpty(patch)) {
            setStringSummary(PREF_SECURITY_PATCH, patch);
        } else {
            getPreferenceScreen().removePreference(findPreference(PREF_SECURITY_PATCH));
        }

        findPreference(PREF_KERNEL_VERSION).setSummary(DeviceInfoUtils.getFormattedKernelVersion());

        setValueSummary(PREF_EQUIPMENT_ID, PROPERTY_EQUIPMENT_ID);
        // Remove Equipment id preference if FCC ID is not set by RIL
        removePreferenceIfPropertyMissing(getPreferenceScreen(), PREF_EQUIPMENT_ID,
                PROPERTY_EQUIPMENT_ID);

        setValueSummary(PREF_BASEBAND_VERSION, "gsm.version.baseband");
        // Remove Baseband version if wifi-only device
        if (Utils.isWifiOnly(getActivity())) {
            getPreferenceScreen().removePreference(findPreference(PREF_BASEBAND_VERSION));
        }

        setStringSummary(PREF_BUILD_NUMBER, Build.DISPLAY);
        findPreference(PREF_BUILD_NUMBER).setEnabled(true);

        if (!SELinux.isSELinuxEnabled()) {
            String status = getResources().getString(R.string.selinux_status_disabled);
            setStringSummary(PREF_SELINUX_STATUS, status);
        } else if (!SELinux.isSELinuxEnforced()) {
            String status = getResources().getString(R.string.selinux_status_permissive);
            setStringSummary(PREF_SELINUX_STATUS, status);
        }
        // Remove selinux information if property is not present
        removePreferenceIfPropertyMissing(getPreferenceScreen(), PREF_SELINUX_STATUS,
                PROPERTY_SELINUX_STATUS);
    }

    @Override
    public void onResume() {
        super.onResume();
        mDevHitCountdown = getActivity().getSharedPreferences(DevelopmentSettings.PREF_FILE,
                Context.MODE_PRIVATE).getBoolean(DevelopmentSettings.PREF_SHOW, true)
                        ? -1 : TAPS_TO_BE_A_DEVELOPER;
        mDevHitToast = null;
        mFunDisallowedAdmin = RestrictedLockUtils.checkIfRestrictionEnforced(
                getActivity(), UserManager.DISALLOW_FUN, UserHandle.myUserId());
        mFunDisallowedBySystem = RestrictedLockUtils.hasBaseUserRestriction(
                getActivity(), UserManager.DISALLOW_FUN, UserHandle.myUserId());
        mDebuggingFeaturesDisallowedAdmin = RestrictedLockUtils.checkIfRestrictionEnforced(
                getActivity(), UserManager.DISALLOW_DEBUGGING_FEATURES, UserHandle.myUserId());
        mDebuggingFeaturesDisallowedBySystem = RestrictedLockUtils.hasBaseUserRestriction(
                getActivity(), UserManager.DISALLOW_DEBUGGING_FEATURES, UserHandle.myUserId());
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.getKey().equals(PREF_FIRMWARE_VERSION)) {
            System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
            mHits[mHits.length-1] = SystemClock.uptimeMillis();
            if (mHits[0] >= (SystemClock.uptimeMillis()-500)) {
                if (mUm.hasUserRestriction(UserManager.DISALLOW_FUN)) {
                    if (mFunDisallowedAdmin != null && !mFunDisallowedBySystem) {
                        RestrictedLockUtils.sendShowAdminSupportDetailsIntent(getActivity(),
                                mFunDisallowedAdmin);
                    }
                    Log.d(LOG_TAG, "Sorry, no fun for you!");
                    return false;
                }

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setClassName("android",
                        com.android.internal.app.PlatLogoActivity.class.getName());
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Unable to start activity " + intent.toString());
                }
            }
        } else if (preference.getKey().equals(PREF_BUILD_NUMBER)) {
            // Don't enable developer options for secondary users.
            if (!mUm.isAdminUser()) return true;

            // Don't enable developer options until device has been provisioned
            if (!Utils.isDeviceProvisioned(getActivity())) {
                return true;
            }

            if (mUm.hasUserRestriction(UserManager.DISALLOW_DEBUGGING_FEATURES)) {
                if (mDebuggingFeaturesDisallowedAdmin != null &&
                        !mDebuggingFeaturesDisallowedBySystem) {
                    RestrictedLockUtils.sendShowAdminSupportDetailsIntent(getActivity(),
                            mDebuggingFeaturesDisallowedAdmin);
                }
                return true;
            }

            if (mDevHitCountdown > 0) {
                mDevHitCountdown--;
                if (mDevHitCountdown == 0) {
                    getActivity().getSharedPreferences(DevelopmentSettings.PREF_FILE,
                            Context.MODE_PRIVATE).edit().putBoolean(
                                    DevelopmentSettings.PREF_SHOW, true).apply();
                    if (mDevHitToast != null) {
                        mDevHitToast.cancel();
                    }
                    mDevHitToast = Toast.makeText(getActivity(), R.string.show_dev_on,
                            Toast.LENGTH_LONG);
                    mDevHitToast.show();
                    // This is good time to index the Developer Options
                    Index.getInstance(
                            getActivity().getApplicationContext()).updateFromClassNameResource(
                                    DevelopmentSettings.class.getName(), true, true);

                } else if (mDevHitCountdown > 0
                        && mDevHitCountdown < (TAPS_TO_BE_A_DEVELOPER-2)) {
                    if (mDevHitToast != null) {
                        mDevHitToast.cancel();
                    }
                    mDevHitToast = Toast.makeText(getActivity(), getResources().getQuantityString(
                            R.plurals.show_dev_countdown, mDevHitCountdown, mDevHitCountdown),
                            Toast.LENGTH_SHORT);
                    mDevHitToast.show();
                }
            } else if (mDevHitCountdown < 0) {
                if (mDevHitToast != null) {
                    mDevHitToast.cancel();
                }
                mDevHitToast = Toast.makeText(getActivity(), R.string.show_dev_already,
                        Toast.LENGTH_LONG);
                mDevHitToast.show();
            }
        }
        return super.onPreferenceTreeClick(preference);
    }

    private void removePreferenceIfPropertyMissing(PreferenceGroup preferenceGroup,
            String preference, String property ) {
        if (SystemProperties.get(property).equals("")) {
            // Property is missing so remove preference from group
            try {
                preferenceGroup.removePreference(findPreference(preference));
            } catch (RuntimeException e) {
                Log.d(LOG_TAG, "Property '" + property + "' missing and no '"
                        + preference + "' preference");
            }
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

    private void setValueSummary(String preference, String property) {
        try {
            findPreference(preference).setSummary(
                    SystemProperties.get(property,
                            getResources().getString(R.string.device_info_default)));
        } catch (RuntimeException e) {
            // No recovery
        }
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.DARKKAT;
    }
}
