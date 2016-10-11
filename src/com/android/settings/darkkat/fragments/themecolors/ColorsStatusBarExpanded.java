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
import android.support.v7.preference.PreferenceScreen;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.internal.util.darkkat.ColorConstants;
import com.android.internal.util.darkkat.StatusBarExpandedColorHelper;
import com.android.internal.util.darkkat.ThemeHelper;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import net.darkkatrom.colorpicker.preference.ColorPickerPreference;

public class ColorsStatusBarExpanded extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_USE_THEME_COLORS =
            "colors_status_bar_expanded_use_theme_colors";
    private static final String PREF_PRIMARY_BACKGROUND_COLOR =
            "colors_status_bar_expanded_primary_background_color";
    private static final String PREF_SECONDARY_BACKGROUND_COLOR =
            "colors_status_bar_expanded_secondary_background_color";
    private static final String PREF_ACCENT_COLOR =
            "colors_status_bar_expanded_accent_color";
    private static final String PREF_TEXT_COLOR =
            "colors_status_bar_expanded_text_color";
    private static final String PREF_ICON_COLOR =
            "colors_status_bar_expanded_icon_color";
    private static final String PREF_RIPPLE_COLOR =
            "colors_status_bar_expanded_ripple_color";

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET  = 0;

    private SwitchPreference mUseThemeColors;
    private ColorPickerPreference mPrimaryBackgroundColor;
    private ColorPickerPreference mSecondaryBackgroundColor;
    private ColorPickerPreference mAccentColor;
    private ColorPickerPreference mTextColor;
    private ColorPickerPreference mIconColor;
    private ColorPickerPreference mRippleColor;

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

        addPreferencesFromResource(R.xml.colors_status_bar_expanded);
        mResolver = getContentResolver();

        mCustomizeColors = !ThemeHelper.statusBarExpandedUseThemeColors(getActivity());

        mUseThemeColors = (SwitchPreference) findPreference(PREF_USE_THEME_COLORS);
        mUseThemeColors.setChecked(!mCustomizeColors);
        mUseThemeColors.setOnPreferenceChangeListener(this);

        if (mCustomizeColors) {
            int intColor;
            String hexColor;

            mPrimaryBackgroundColor =
                    (ColorPickerPreference) findPreference(PREF_PRIMARY_BACKGROUND_COLOR);
            intColor = StatusBarExpandedColorHelper.getPrimaryBackgroundColor(getActivity());
            mPrimaryBackgroundColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mPrimaryBackgroundColor.setSummary(hexColor);
            mPrimaryBackgroundColor.setResetColor(ThemeHelper.getSystemUIPrimaryColor(getActivity()));
            mPrimaryBackgroundColor.setOnPreferenceChangeListener(this);

            mSecondaryBackgroundColor =
                    (ColorPickerPreference) findPreference(PREF_SECONDARY_BACKGROUND_COLOR);
            intColor = StatusBarExpandedColorHelper.getSecondaryBackgroundColor(getActivity());
            mSecondaryBackgroundColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mSecondaryBackgroundColor.setSummary(hexColor);
            mSecondaryBackgroundColor.setResetColor(
                    ThemeHelper.getSystemUISecondaryColor(getActivity()));
            mSecondaryBackgroundColor.setOnPreferenceChangeListener(this);

            mAccentColor =
                    (ColorPickerPreference) findPreference(PREF_ACCENT_COLOR);
            intColor = StatusBarExpandedColorHelper.getAccentColor(getActivity());
            mAccentColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mAccentColor.setSummary(hexColor);
            mAccentColor.setResetColor(ThemeHelper.getSystemUIAccentColor(getActivity()));
            mAccentColor.setOnPreferenceChangeListener(this);

            mTextColor =
                    (ColorPickerPreference) findPreference(PREF_TEXT_COLOR);
            intColor = StatusBarExpandedColorHelper.getTextColor(getActivity());
            mTextColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mTextColor.setSummary(hexColor);
            mTextColor.setResetColor(ColorConstants.WHITE);
            mTextColor.setOnPreferenceChangeListener(this);

            mIconColor =
                    (ColorPickerPreference) findPreference(PREF_ICON_COLOR);
            intColor = StatusBarExpandedColorHelper.getIconColor(getActivity());
            mIconColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mIconColor.setSummary(hexColor);
            mIconColor.setResetColor(ColorConstants.WHITE);
            mIconColor.setOnPreferenceChangeListener(this);

            mRippleColor =
                    (ColorPickerPreference) findPreference(PREF_RIPPLE_COLOR);
            intColor = StatusBarExpandedColorHelper.getRippleColor(getActivity());
            mRippleColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mRippleColor.setSummary(hexColor);
            mRippleColor.setResetColor(ThemeHelper.getSystemUIRippleColor(getActivity()));
            mRippleColor.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_PRIMARY_BACKGROUND_COLOR);
            removePreference(PREF_SECONDARY_BACKGROUND_COLOR);
            removePreference(PREF_ACCENT_COLOR);
            removePreference(PREF_TEXT_COLOR);
            removePreference(PREF_ICON_COLOR);
            removePreference(PREF_RIPPLE_COLOR);
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
                    Settings.System.STATUS_BAR_EXPANDED_USE_THEME_COLORS, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mPrimaryBackgroundColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_PRIMARY_BACKGROUND_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mSecondaryBackgroundColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_SECONDARY_BACKGROUND_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mAccentColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_ACCENT_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_TEXT_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_ICON_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mRippleColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_RIPPLE_COLOR, intHex);
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

        ColorsStatusBarExpanded getOwner() {
            return (ColorsStatusBarExpanded) getTargetFragment();
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
                                    Settings.System.STATUS_BAR_EXPANDED_PRIMARY_BACKGROUND_COLOR,
                                    ThemeHelper.getSystemUIPrimaryColor(getActivity()));
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_SECONDARY_BACKGROUND_COLOR,
                                    ThemeHelper.getSystemUISecondaryColor(getActivity()));
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_ACCENT_COLOR,
                                    ThemeHelper.getSystemUIAccentColor(getActivity()));
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_TEXT_COLOR,
                                    ColorConstants.WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_ICON_COLOR,
                                    ColorConstants.WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_RIPPLE_COLOR,
                                    ThemeHelper.getSystemUIRippleColor(getActivity()));
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
