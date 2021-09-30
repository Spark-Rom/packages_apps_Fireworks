/*
 * Copyright (C) 2020-2021 The Spark Project
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

import static android.os.UserHandle.USER_CURRENT;
import static android.os.UserHandle.USER_SYSTEM;

import android.os.UserHandle;
import android.graphics.Color;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.om.IOverlayManager;
import android.os.Bundle;
import android.provider.SearchIndexableResource;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.*;
import androidx.preference.Preference.OnPreferenceChangeListener;
import com.spark.settings.fragments.AccentPickerFragment;
import com.spark.settings.preferences.AccentPickerPreference;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.internal.util.spark.SparkUtils;
import com.android.internal.util.spark.ThemesUtils;

import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.development.OverlayCategoryPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.search.SearchIndexable;

import com.spark.settings.display.QsColorPreferenceController;
import com.spark.settings.display.QsTileStylePreferenceController;
import com.spark.settings.display.SwitchStylePreferenceController;
import net.margaritov.preference.colorpicker.ColorPickerPreference;
import com.spark.settings.preferences.AccentOverlayCategoryPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class UiSettings extends DashboardFragment {

    public static final String TAG = "UiSettings";

    private Context mContext;
    private IOverlayManager mOverlayManager;
    private IOverlayManager mOverlayService;
    private IntentFilter mIntentFilter;

    private ListPreference mLockClockStyles;

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.SPARK_SETTINGS;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.spark_settings_ui;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        mOverlayService = IOverlayManager.Stub
                .asInterface(ServiceManager.getService(Context.OVERLAY_SERVICE));

        mContext = getActivity();

    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (preference instanceof AccentPickerPreference) {
            final AccentPickerFragment fragment = new AccentPickerFragment();
            fragment.setTargetFragment(this, 0);
            fragment.show(getParentFragmentManager(), TAG);
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }
    @Override
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle(), this);
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(
            Context context, Lifecycle lifecycle, Fragment fragment) {
        final List<AbstractPreferenceController> controllers = new ArrayList<>();
        controllers.add(new OverlayCategoryPreferenceController(context,
                "android.theme.customization.font"));
        controllers.add(new AccentOverlayCategoryPreferenceController(context,
                "android.theme.customization.accent_color"));
        return controllers;
    }


    /**
     * For Search.
     */


    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.spark_settings_ui);
}
