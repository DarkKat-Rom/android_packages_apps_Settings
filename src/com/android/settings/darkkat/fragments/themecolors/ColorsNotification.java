/*
 * Copyright (C) 2015 DarkKat
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

package com.android.settings.darkkat.fragments.themecolors;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.internal.util.darkkat.ColorConstants;
import com.android.internal.util.darkkat.NotificationColorHelper;
import com.android.internal.util.darkkat.ThemeHelper;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import net.darkkatrom.colorpicker.preference.ColorPickerPreference;

public class ColorsNotification extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_CAT_DISMISS_ALL =
            "colors_notification_cat_dismiss_all";
    private static final String PREF_USE_THEME_COLORS =
            "colors_notification_use_theme_colors";
    private static final String PREF_PRIMARY_BACKGROUND_COLOR =
            "colors_notification_primary_background_color";
    private static final String PREF_SECONDARY_BACKGROUND_COLOR =
            "colors_notification_secondary_background_color";
    private static final String PREF_ACCENT_COLOR =
            "colors_notification_accent_color";
    private static final String PREF_TEXT_COLOR =
            "colors_notification_text_color";
    private static final String PREF_ICON_COLOR =
            "colors_notification_icon_color";
    private static final String PREF_RIPPLE_COLOR =
            "colors_notification_ripple_color";
    private static final String PREF_DISMISS_ALL_COLOR =
            "colors_notification_dismiss_all_text_color";

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET  = 0;

    private SwitchPreference mUseThemeColors;
    private ColorPickerPreference mPrimaryBackgroundColor;
    private ColorPickerPreference mSecondaryBackgroundColor;
    private ColorPickerPreference mAccentColor;
    private ColorPickerPreference mTextColor;
    private ColorPickerPreference mIconColor;
    private ColorPickerPreference mRippleColor;
    private ColorPickerPreference mDismissAllColor;

    private boolean mCustomizeColors;
    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        refreshSettings();
    }

    public void refreshSettings() {
        PreferenceScreen prefs = getPreferenceScreen();
        if (prefs != null) {
            prefs.removeAll();
        }

        addPreferencesFromResource(R.xml.colors_notification);
        mResolver = getContentResolver();

        mCustomizeColors = !ThemeHelper.notificationUseThemeColors(getActivity());

        mUseThemeColors = (SwitchPreference) findPreference(PREF_USE_THEME_COLORS);
        mUseThemeColors.setChecked(!mCustomizeColors);
        mUseThemeColors.setOnPreferenceChangeListener(this);

        PreferenceCategory catDismissAll =
                (PreferenceCategory) findPreference(PREF_CAT_DISMISS_ALL);

        if (mCustomizeColors) {
            int intColor;
            String hexColor;

            mPrimaryBackgroundColor =
                    (ColorPickerPreference) findPreference(PREF_PRIMARY_BACKGROUND_COLOR);
            intColor = NotificationColorHelper.getPrimaryBackgroundColor(getActivity());
            mPrimaryBackgroundColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mPrimaryBackgroundColor.setSummary(hexColor);
            mPrimaryBackgroundColor.setResetColor(
                    ThemeHelper.getNotificationPrimaryBgColor(getActivity()));
            mPrimaryBackgroundColor.setOnPreferenceChangeListener(this);

            removePreference(PREF_ICON_COLOR);
            removePreference(PREF_RIPPLE_COLOR);

            mSecondaryBackgroundColor =
                    (ColorPickerPreference) findPreference(PREF_SECONDARY_BACKGROUND_COLOR);
            intColor = NotificationColorHelper.getSecondaryBackgroundColor(getActivity());
            mSecondaryBackgroundColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mSecondaryBackgroundColor.setSummary(hexColor);
            mSecondaryBackgroundColor.setResetColor(
                    ThemeHelper.getNotificationSecondaryBgColor(getActivity()));
            mSecondaryBackgroundColor.setOnPreferenceChangeListener(this);

            mAccentColor =
                    (ColorPickerPreference) findPreference(PREF_ACCENT_COLOR);
            intColor = NotificationColorHelper.getAccentColor(getActivity());
            mAccentColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mAccentColor.setSummary(hexColor);
            mAccentColor.setResetColor(ThemeHelper.getNotificationAccentColor(getActivity()));
            mAccentColor.setOnPreferenceChangeListener(this);

            mTextColor =
                    (ColorPickerPreference) findPreference(PREF_TEXT_COLOR);
            intColor = NotificationColorHelper.getTextColor(getActivity());
            mTextColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mTextColor.setSummary(hexColor);
            mTextColor.setResetColor(ThemeHelper.getNotificationTextColor(getActivity()));
            mTextColor.setOnPreferenceChangeListener(this);
/*
            mIconColor =
                    (ColorPickerPreference) findPreference(PREF_ICON_COLOR);
            intColor = NotificationColorHelper.getIconColor(getActivity());
            mIconColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mIconColor.setSummary(hexColor);
            mIconColor.setResetColor(ThemeHelper.getNotificationIconColor(getActivity()));
            mIconColor.setOnPreferenceChangeListener(this);

            mRippleColor =
                    (ColorPickerPreference) findPreference(PREF_RIPPLE_COLOR);
            intColor = NotificationColorHelper.getRippleColor(getActivity());
            mRippleColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mRippleColor.setSummary(hexColor);
            mRippleColor.setResetColor(ThemeHelper.getNotificationRippleColor(getActivity()));
            mRippleColor.setOnPreferenceChangeListener(this);
 */

            mDismissAllColor =
                    (ColorPickerPreference) findPreference(PREF_DISMISS_ALL_COLOR);
            intColor = NotificationColorHelper.getDismissAllTextColor(getActivity());
            mDismissAllColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mDismissAllColor.setSummary(hexColor);
            mDismissAllColor.setResetColor(
                    ThemeHelper.getNotificationDismissAllTextColor());
            mDismissAllColor.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_PRIMARY_BACKGROUND_COLOR);
            removePreference(PREF_SECONDARY_BACKGROUND_COLOR);
            removePreference(PREF_ACCENT_COLOR);
            removePreference(PREF_TEXT_COLOR);
            removePreference(PREF_ICON_COLOR);
            removePreference(PREF_RIPPLE_COLOR);
            catDismissAll.removePreference(findPreference(PREF_DISMISS_ALL_COLOR));
        }

        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(R.drawable.ic_action_reset)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.getItem(0).setEnabled(mCustomizeColors);
        menu.getItem(0).setVisible(mCustomizeColors);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                showDialogInner(DLG_RESET);
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String hex;
        int intHex;

        if (preference == mUseThemeColors) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.NOTIFICATION_USE_THEME_COLORS, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mPrimaryBackgroundColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.NOTIFICATION_PRIMARY_BACKGROUND_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mSecondaryBackgroundColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.NOTIFICATION_SECONDARY_BACKGROUND_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mAccentColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.NOTIFICATION_ACCENT_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.NOTIFICATION_TEXT_COLOR, intHex);
            refreshSettings();
            return true;
/*
        } else if (preference == mIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.NOTIFICATION_ICON_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mRippleColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.NOTIFICATION_RIPPLE_COLOR, intHex);
            refreshSettings();
            return true;
        }
 */
        } else if (preference == mDismissAllColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.NOTIFICATION_DISMISS_ALL_COLOR, intHex);
            refreshSettings();
            return true;
        }
        return false;
    }

    private void showDialogInner(int id) {
        DialogFragment newFragment = MyAlertDialogFragment.newInstance(id);
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getFragmentManager(), "dialog " + id);
    }

    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(int id) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("id", id);
            frag.setArguments(args);
            return frag;
        }

        ColorsNotification getOwner() {
            return (ColorsNotification) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_RESET:
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.reset)
                    .setMessage(R.string.dlg_reset_theme_default_colors_message)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.dlg_ok,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NOTIFICATION_PRIMARY_BACKGROUND_COLOR,
                                    ThemeHelper.getNotificationPrimaryBgColor(getActivity()));
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NOTIFICATION_SECONDARY_BACKGROUND_COLOR,
                                    ThemeHelper.getNotificationSecondaryBgColor(getActivity()));
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NOTIFICATION_ACCENT_COLOR,
                                    ThemeHelper.getNotificationAccentColor(getActivity()));
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NOTIFICATION_TEXT_COLOR,
                                    ThemeHelper.getNotificationTextColor(getActivity()));
/*
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NOTIFICATION_ICON_COLOR,
                                    ThemeHelper.getNotificationIconColor(getActivity()));
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NOTIFICATION_RIPPLE_COLOR,
                                    ThemeHelper.getNotificationRippleColor(getActivity()));
 */
                            getOwner().refreshSettings();
                        }
                    })
                    .create();
            }
            throw new IllegalArgumentException("unknown id " + id);
        }

        @Override
        public void onCancel(DialogInterface dialog) {

        }
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.DARKKAT;
    }
}
