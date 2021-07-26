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

import java.util.ArrayList;
import java.util.List;

public class UiSettings extends DashboardFragment implements
        OnPreferenceChangeListener {

    public static final String TAG = "UiSettings";

    private static final String PREF_RGB_ACCENT_PICKER_WHITE = "rgb_accent_picker_white";
    private static final String PREF_RGB_ACCENT_PICKER_DARK = "rgb_accent_picker_dark";

    private Context mContext;
    private IOverlayManager mOverlayManager;
    private IOverlayManager mOverlayService;
    private IntentFilter mIntentFilter;

    private ColorPickerPreference rgbAccentPickerWhite;
    private ColorPickerPreference rgbAccentPickerDark;

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

        rgbAccentPickerWhite = (ColorPickerPreference) findPreference(PREF_RGB_ACCENT_PICKER_WHITE);
        rgbAccentPickerDark = (ColorPickerPreference) findPreference(PREF_RGB_ACCENT_PICKER_DARK);
        String colorValWhite = Settings.Secure.getStringForUser(mContext.getContentResolver(),
                Settings.Secure.ACCENT_LIGHT, UserHandle.USER_CURRENT);
        String colorValDark = Settings.Secure.getStringForUser(mContext.getContentResolver(),
                Settings.Secure.ACCENT_DARK, UserHandle.USER_CURRENT);
        int colorWhite = (colorValWhite == null)
                ? Color.WHITE
                : Color.parseColor("#" + colorValWhite);
        rgbAccentPickerWhite.setNewPreviewColor(colorWhite);
        rgbAccentPickerWhite.setOnPreferenceChangeListener(this);
        int colorDark = (colorValDark == null)
                ? Color.WHITE
                : Color.parseColor("#" + colorValDark);
        rgbAccentPickerDark.setNewPreviewColor(colorDark);
        rgbAccentPickerDark.setOnPreferenceChangeListener(this);
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
        return controllers;
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == rgbAccentPickerWhite) {
            int colorWhite = (Integer) objValue;
            String hexColor = String.format("%08X", (0xFFFFFFFF & colorWhite));
            Settings.Secure.putStringForUser(mContext.getContentResolver(),
                        Settings.Secure.ACCENT_LIGHT,
                        hexColor, UserHandle.USER_CURRENT);
            try {
                 mOverlayManager.reloadAssets("com.android.settings", UserHandle.USER_CURRENT);
                 mOverlayManager.reloadAssets("com.android.systemui", UserHandle.USER_CURRENT);
             } catch (RemoteException ignored) {
             }
            return true;
        } else if (preference == rgbAccentPickerDark) {
            int colorDark = (Integer) objValue;
            String hexColor = String.format("%08X", (0xFFFFFFFF & colorDark));
            Settings.Secure.putStringForUser(mContext.getContentResolver(),
                        Settings.Secure.ACCENT_DARK,
                        hexColor, UserHandle.USER_CURRENT);
            try {
                 mOverlayManager.reloadAssets("com.android.settings", UserHandle.USER_CURRENT);
                 mOverlayManager.reloadAssets("com.android.systemui", UserHandle.USER_CURRENT);
             } catch (RemoteException ignored) {
             }
            return true;
        }
        return false;
    }


    /**
     * For Search.
     */


    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.spark_settings_ui);
}
