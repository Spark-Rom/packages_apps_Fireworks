/*
 * Copyright (C) 2019-2020 The Spark  Project
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

package com.spark.settings.fragments;

import android.app.Activity;
import android.text.format.DateFormat;
import java.time.format.DateTimeFormatter;
import java.time.LocalTime;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.content.pm.PackageManager.NameNotFoundException;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;
import com.spark.settings.preferences.SecureSettingSwitchPreference;
import com.spark.settings.preferences.CustomSeekBarPreference;

import java.util.ArrayList;
import java.util.List;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class MiscSettings extends SettingsPreferenceFragment {

     private Preference mSleepMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.spark_settings_misc);
        final ContentResolver resolver = getActivity().getContentResolver();
        mSleepMode = findPreference("sleep_mode");
        updateSleepModeSummary();
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.SPARK_SETTINGS;
    }

    private void updateSleepModeSummary() {
        if (mSleepMode == null) return;
        boolean enabled = Settings.Secure.getIntForUser(getActivity().getContentResolver(),
                Settings.Secure.SLEEP_MODE_ENABLED, 0, UserHandle.USER_CURRENT) == 1;
        int mode = Settings.Secure.getIntForUser(getActivity().getContentResolver(),
                Settings.Secure.SLEEP_MODE_AUTO_MODE, 0, UserHandle.USER_CURRENT);
        String timeValue = Settings.Secure.getStringForUser(getActivity().getContentResolver(),
                Settings.Secure.SLEEP_MODE_AUTO_TIME, UserHandle.USER_CURRENT);
        if (timeValue == null || timeValue.equals("")) timeValue = "20:00,07:00";
        String[] time = timeValue.split(",", 0);
        String outputFormat = DateFormat.is24HourFormat(getContext()) ? "HH:mm" : "h:mm a";
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(outputFormat);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime sinceValue = LocalTime.parse(time[0], formatter);
        LocalTime tillValue = LocalTime.parse(time[1], formatter);
        String detail;
        switch (mode) {
            default:
            case 0:
                detail = getActivity().getString(enabled
                        ? R.string.night_display_summary_on_auto_mode_never
                        : R.string.night_display_summary_off_auto_mode_never);
                break;
            case 1:
                detail = getActivity().getString(enabled
                        ? R.string.night_display_summary_on_auto_mode_twilight
                        : R.string.night_display_summary_off_auto_mode_twilight);
                break;
            case 2:
                if (enabled) {
                    detail = getActivity().getString(R.string.night_display_summary_on_auto_mode_custom, tillValue.format(outputFormatter));
                } else {
                    detail = getActivity().getString(R.string.night_display_summary_off_auto_mode_custom, sinceValue.format(outputFormatter));
                }
                break;
            case 3:
                if (enabled) {
                    detail = getActivity().getString(R.string.night_display_summary_on_auto_mode_custom, tillValue.format(outputFormatter));
                } else {
                    detail = getActivity().getString(R.string.night_display_summary_off_auto_mode_twilight);
                }
                break;
            case 4:
                if (enabled) {
                    detail = getActivity().getString(R.string.night_display_summary_on_auto_mode_twilight);
                } else {
                    detail = getActivity().getString(R.string.night_display_summary_off_auto_mode_custom, sinceValue.format(outputFormatter));
                }
                break;
        }
        String summary = getActivity().getString(enabled
                ? R.string.night_display_summary_on
                : R.string.night_display_summary_off, detail);
        mSleepMode.setSummary(summary);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSleepModeSummary();
    }

    @Override
    public void onPause() {
        super.onPause();
        updateSleepModeSummary();
    }

    /**
     * For Search.
     */

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.spark_settings_misc);
}
