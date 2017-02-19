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
import com.android.internal.util.darkkat.SlimRecentsColorHelper;
import com.android.internal.util.darkkat.ThemeHelper;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import net.darkkatrom.colorpicker.preference.ColorPickerPreference;

public class ColorsRecents extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_CAT_Panel =
            "colors_slim_recents_cat_panel";
    private static final String PREF_CAT_CARD =
            "colors_slim_recents_cat_card";
    private static final String PREF_CAT_CARD_HEADER =
            "colors_slim_recents_cat_card_header";
    private static final String PREF_USE_THEME_COLORS =
            "colors_slim_recents_use_theme_colors";
    private static final String PREF_PANEL_BACKGROUND_COLOR =
            "colors_slim_recents_panel_background_color";
    private static final String PREF_PANEL_EMPTY_ICON_COLOR =
            "colors_slim_recents_panel_empty_icon_color";
    private static final String PREF_CARD_BACKGROUND_COLOR =
            "colors_slim_recents_card_background_color";
    private static final String PREF_CARD_HEADER_BACKGROUND_COLOR =
            "colors_slim_recents_card_header_background_color";
    private static final String PREF_CARD_HEADER_ACTIVATED_BACKGROUND_COLOR =
            "colors_slim_recents_card_header_activated_background_color";
    private static final String PREF_CARD_HEADER_TEXT_COLOR =
            "colors_slim_recents_card_header_text_color";
    private static final String PREF_CARD_HEADER_ICON_COLOR =
            "colors_slim_recents_card_header_icon_color";

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET  = 0;

    private SwitchPreference mUseThemeColors;
    private ColorPickerPreference mPanelBackgroundColor;
    private ColorPickerPreference mPanelEmptyIconColor;
    private ColorPickerPreference mCardBackgroundColor;
    private ColorPickerPreference mCardHeaderBackgroundColor;
    private ColorPickerPreference mCardHeaderActivatedBackgroundColor;
    private ColorPickerPreference mCardHeaderTextColor;
    private ColorPickerPreference mCardHeaderIconColor;

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

        addPreferencesFromResource(R.xml.colors_recents);
        mResolver = getContentResolver();

        PreferenceCategory catPanel =
                (PreferenceCategory) findPreference(PREF_CAT_Panel);
        PreferenceCategory catCard =
                (PreferenceCategory) findPreference(PREF_CAT_CARD);
        PreferenceCategory catCardHeader =
                (PreferenceCategory) findPreference(PREF_CAT_CARD_HEADER);

        mCustomizeColors = !ThemeHelper.slimRecentsUseThemeColors(getActivity());

        mUseThemeColors = (SwitchPreference) findPreference(PREF_USE_THEME_COLORS);
        mUseThemeColors.setChecked(!mCustomizeColors);
        mUseThemeColors.setOnPreferenceChangeListener(this);

        if (mCustomizeColors) {
            int intColor;
            String hexColor;

            mPanelBackgroundColor =
                    (ColorPickerPreference) findPreference(PREF_PANEL_BACKGROUND_COLOR);
            intColor = SlimRecentsColorHelper.getPanelBackgroundColor(getActivity());
            mPanelBackgroundColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mPanelBackgroundColor.setSummary(hexColor);
            mPanelBackgroundColor.setResetColor(ThemeHelper.getSlimRecentsPanelBgColor(getActivity()));
            mPanelBackgroundColor.setOnPreferenceChangeListener(this);

            mPanelEmptyIconColor =
                    (ColorPickerPreference) findPreference(PREF_PANEL_EMPTY_ICON_COLOR);
            intColor = SlimRecentsColorHelper.getPanelEmptyIconColor(getActivity());
            mPanelEmptyIconColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mPanelEmptyIconColor.setSummary(hexColor);
            mPanelEmptyIconColor.setResetColor(
                    ThemeHelper.getSlimRecentsPanelEmptyIconColor());
            mPanelEmptyIconColor.setOnPreferenceChangeListener(this);

            mCardBackgroundColor =
                    (ColorPickerPreference) findPreference(PREF_CARD_BACKGROUND_COLOR);
            intColor = SlimRecentsColorHelper.getCardBackgroundColor(getActivity());
            mCardBackgroundColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mCardBackgroundColor.setSummary(hexColor);
            mCardBackgroundColor.setResetColor(ThemeHelper.getColorBackgroundFloating(getActivity()));
            mCardBackgroundColor.setOnPreferenceChangeListener(this);

            mCardHeaderBackgroundColor =
                    (ColorPickerPreference) findPreference(PREF_CARD_HEADER_BACKGROUND_COLOR);
            intColor = SlimRecentsColorHelper.getCardHeaderBackgroundColor(getActivity());
            mCardHeaderBackgroundColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mCardHeaderBackgroundColor.setSummary(hexColor);
            mCardHeaderBackgroundColor.setResetColor(
                    ThemeHelper.getColorBackgroundFloating(getActivity()));
            mCardHeaderBackgroundColor.setOnPreferenceChangeListener(this);

            mCardHeaderActivatedBackgroundColor =
                    (ColorPickerPreference) findPreference(PREF_CARD_HEADER_ACTIVATED_BACKGROUND_COLOR);
            intColor = SlimRecentsColorHelper.getCardHeaderActivatedBackgroundColor(getActivity());
            mCardHeaderActivatedBackgroundColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mCardHeaderActivatedBackgroundColor.setSummary(hexColor);
            mCardHeaderActivatedBackgroundColor.setResetColor(
                    ThemeHelper.getSlimRecentsCardHeaderActivatedBgColor(getActivity()));
            mCardHeaderActivatedBackgroundColor.setOnPreferenceChangeListener(this);

            mCardHeaderTextColor =
                    (ColorPickerPreference) findPreference(PREF_CARD_HEADER_TEXT_COLOR);
            intColor = SlimRecentsColorHelper.getCardHeaderTextColor(getActivity());
            mCardHeaderTextColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mCardHeaderTextColor.setSummary(hexColor);
            mCardHeaderTextColor.setResetColor(ThemeHelper.getPrimaryTextColor(getActivity()));
            mCardHeaderTextColor.setOnPreferenceChangeListener(this);

            mCardHeaderIconColor =
                    (ColorPickerPreference) findPreference(PREF_CARD_HEADER_ICON_COLOR);
            intColor = SlimRecentsColorHelper.getCardHeaderIconColor(getActivity());
            mCardHeaderIconColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mCardHeaderIconColor.setSummary(hexColor);
            mCardHeaderIconColor.setResetColor(ThemeHelper.getIconColor(getActivity()));
            mCardHeaderIconColor.setOnPreferenceChangeListener(this);
        } else {
            catPanel.removePreference(findPreference(PREF_PANEL_BACKGROUND_COLOR));
            catPanel.removePreference(findPreference(PREF_PANEL_EMPTY_ICON_COLOR));
            catCardHeader.removePreference(findPreference(PREF_CARD_BACKGROUND_COLOR));
            catCardHeader.removePreference(findPreference(PREF_CARD_HEADER_BACKGROUND_COLOR));
            catCard.removePreference(findPreference(PREF_CARD_HEADER_ACTIVATED_BACKGROUND_COLOR));
            catCardHeader.removePreference(findPreference(PREF_CARD_HEADER_TEXT_COLOR));
            catCardHeader.removePreference(findPreference(PREF_CARD_HEADER_ICON_COLOR));
            removePreference(PREF_CAT_Panel);
            removePreference(PREF_CAT_CARD);
            removePreference(PREF_CAT_CARD_HEADER);
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
                    Settings.System.SLIM_RECENTS_USE_THEME_COLORS, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mPanelBackgroundColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.SLIM_RECENTS_PANEL_BG_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mPanelEmptyIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.SLIM_RECENTS_PANEL_EMPTY_ICON_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mCardBackgroundColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.SLIM_RECENTS_CARD_BG_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mCardHeaderBackgroundColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.SLIM_RECENTS_CARD_HEADER_BG_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mCardHeaderActivatedBackgroundColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.SLIM_RECENTS_CARD_HEADER_ACTIVATED_BG_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mCardHeaderTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.SLIM_RECENTS_CARD_HEADER_TEXT_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mCardHeaderIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.SLIM_RECENTS_CARD_HEADER_ICON_COLOR, intHex);
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

        ColorsRecents getOwner() {
            return (ColorsRecents) getTargetFragment();
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
                                    Settings.System.SLIM_RECENTS_PANEL_BG_COLOR,
                                    ThemeHelper.getSlimRecentsPanelBgColor(getActivity()));
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SLIM_RECENTS_PANEL_EMPTY_ICON_COLOR,
                                    ThemeHelper.getSlimRecentsPanelEmptyIconColor());
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SLIM_RECENTS_CARD_BG_COLOR,
                                    ThemeHelper.getColorBackgroundFloating(getActivity()));
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SLIM_RECENTS_CARD_HEADER_BG_COLOR,
                                    ThemeHelper.getColorBackgroundFloating(getActivity()));
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SLIM_RECENTS_CARD_HEADER_ACTIVATED_BG_COLOR,
                                    ThemeHelper.getSlimRecentsCardHeaderActivatedBgColor(getActivity()));
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SLIM_RECENTS_CARD_HEADER_TEXT_COLOR,
                                    ThemeHelper.getPrimaryTextColor(getActivity()));
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SLIM_RECENTS_CARD_HEADER_ICON_COLOR,
                                    ThemeHelper.getIconColor(getActivity()));
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
