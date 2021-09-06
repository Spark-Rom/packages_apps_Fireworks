/*
 * Copyright (C) 2019-2020 The Spark Project
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

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.util.TypedValue;

import com.spark.settings.preferences.SettingEditTextPreference;
import com.spark.settings.preferences.CustomSystemSeekBarPreference;
import com.spark.settings.preferences.SystemSettingSwitchPreference;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class NetworkTrafficSettings extends SettingsPreferenceFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getContext();
        addPreferencesFromResource(R.xml.spark_settings_network_traffic);
        PreferenceScreen prefSet = getPreferenceScreen();
        final ContentResolver resolver = getActivity().getContentResolver();

        SettingEditTextPreference unitTextSizePreference = (SettingEditTextPreference) findPreference(
            "statusbar_network_traffic_unit_text_size_preference");
        SettingEditTextPreference rateTextScaleFactorPreference = (SettingEditTextPreference) findPreference(
            "statusbar_network_traffic_rate_text_scale_factor_preference");
        try {
            Resources res = context.getPackageManager().getResourcesForApplication("com.android.systemui");
            int resId = res.getIdentifier("network_traffic_unit_text_default_size", "dimen", "com.android.systemui");
            if (resId != 0) {
                unitTextSizePreference.setSettingDefault((int) res.getDimension(resId));
            }
            resId = res.getIdentifier("network_traffic_rate_text_default_scale_factor", "dimen", "com.android.systemui");
            if (resId != 0) {
                TypedValue value = new TypedValue();
                res.getValue(resId, value, true);
                rateTextScaleFactorPreference.setSettingDefault((int) (value.getFloat() * 10));
            }
        } catch(NameNotFoundException e) {
            // Do nothing
        }

    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.SPARK_SETTINGS;
    }

    /**
     * For Search.
     */

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {

                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();
                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.spark_settings_network_traffic;
                    result.add(sir);
                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    List<String> keys = super.getNonIndexableKeys(context);
                    return keys;
                }
    };
}
