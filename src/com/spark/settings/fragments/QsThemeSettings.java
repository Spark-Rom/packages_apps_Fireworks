/*
 * Copyright (C) 2021 Spark
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.spark.settings.fragments;

import static android.os.UserHandle.USER_SYSTEM;

import android.app.AlertDialog;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;

import android.os.SystemProperties;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.os.RemoteException;
import android.os.ServiceManager;
import androidx.preference.*;
import android.provider.SearchIndexableResource;
import android.provider.Settings;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;

import com.spark.settings.display.QsColorPreferenceController;
import com.spark.settings.display.QsTileStylePreferenceController;
import com.android.settings.R;
import com.spark.settings.preferences.SystemSettingListPreference;
import com.spark.settings.preferences.SystemSettingSwitchPreference;
import com.android.internal.util.spark.SparkUtils;

import java.util.ArrayList;
import java.util.List;

public class QsThemeSettings extends DashboardFragment {
    private static final String TAG = "QsThemeSettings";

    private IOverlayManager mOverlayManager;
    private PackageManager mPackageManager;
    private static final String SLIDER_STYLE  = "slider_style";
    private static final String CLEAR_ALL_ICON_STYLE  = "clear_all_icon_style";

    private SystemSettingListPreference mClearAll;
    private SystemSettingListPreference mSlider;
    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ContentResolver resolver = getActivity().getContentResolver();
        Context mContext = getContext();
        mOverlayManager = IOverlayManager.Stub.asInterface(
                ServiceManager.getService(Context.OVERLAY_SERVICE));
        mSlider = (SystemSettingListPreference) findPreference(SLIDER_STYLE);
        mClearAll = (SystemSettingListPreference) findPreference(CLEAR_ALL_ICON_STYLE);
        mCustomSettingsObserver.observe();
    }

    private CustomSettingsObserver mCustomSettingsObserver = new CustomSettingsObserver(mHandler);
    private class CustomSettingsObserver extends ContentObserver {

        CustomSettingsObserver(Handler handler) {
            super(handler);
        }

        void observe() {
            Context mContext = getContext();
            ContentResolver resolver = mContext.getContentResolver();
            resolver.registerContentObserver(Settings.System.getUriFor(
                    Settings.System.SLIDER_STYLE  ),
                    false, this, UserHandle.USER_ALL);
            resolver.registerContentObserver(Settings.System.getUriFor(
                    Settings.System.CLEAR_ALL_ICON_STYLE  ),
                    false, this, UserHandle.USER_ALL);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            if (uri.equals(Settings.System.getUriFor(Settings.System.SLIDER_STYLE  ))) {
                updateSlider();
            } else if (uri.equals(Settings.System.getUriFor(Settings.System.CLEAR_ALL_ICON_STYLE))) {
                updateClearAll();
            }
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mSlider) {
            mCustomSettingsObserver.observe();
            return true;
        } else if (preference == mClearAll) {
            mCustomSettingsObserver.observe();
             SparkUtils.showSystemUiRestartDialog(getContext());
            return true;
        }
        return false;
    }

    private void updateClearAll() {
        ContentResolver resolver = getActivity().getContentResolver();
        boolean ClearAllDefault = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.CLEAR_ALL_ICON_STYLE , 0, UserHandle.USER_CURRENT) == 0;
        boolean ClearAllOOS = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.CLEAR_ALL_ICON_STYLE , 0, UserHandle.USER_CURRENT) == 1;

        if (ClearAllDefault) {
            setDefaultClearAll(mOverlayManager);
        } else if (ClearAllOOS) {
            enableClearAll(mOverlayManager, "com.android.theme.systemui_clearall_oos");
        }
    }

    public static void setDefaultClearAll(IOverlayManager overlayManager) {
        for (int i = 0; i < CLEAR_ALL_ICONS.length; i++) {
            String icons = CLEAR_ALL_ICONS[i];
            try {
                overlayManager.setEnabled(icons, false, USER_SYSTEM);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static void enableClearAll(IOverlayManager overlayManager, String overlayName) {
        try {
            for (int i = 0; i < CLEAR_ALL_ICONS.length; i++) {
                String icons = CLEAR_ALL_ICONS[i];
                try {
                    overlayManager.setEnabled(icons, false, USER_SYSTEM);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            overlayManager.setEnabled(overlayName, true, USER_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void updateSlider() {
        ContentResolver resolver = getActivity().getContentResolver();

        boolean sliderDefault = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.SLIDER_STYLE , 0, UserHandle.USER_CURRENT) == 0;
        boolean sliderOOS = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.SLIDER_STYLE , 0, UserHandle.USER_CURRENT) == 1;
        boolean sliderAosp = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.SLIDER_STYLE , 0, UserHandle.USER_CURRENT) == 2;
        boolean sliderRUI = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.SLIDER_STYLE , 0, UserHandle.USER_CURRENT) == 3;
        boolean sliderA12 = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.SLIDER_STYLE , 0, UserHandle.USER_CURRENT) == 4;

        if (sliderDefault) {
            setDefaultSlider(mOverlayManager);
        } else if (sliderOOS) {
            enableSlider(mOverlayManager, "com.android.theme.systemui_slider_oos");
        } else if (sliderAosp) {
            enableSlider(mOverlayManager, "com.android.theme.systemui_slider.aosp");
        } else if (sliderRUI) {
            enableSlider(mOverlayManager, "com.android.theme.systemui_slider.rui");
        } else if (sliderA12) {
            enableSlider(mOverlayManager, "com.android.theme.systemui_slider.a12");
        }
    }

    public static void setDefaultSlider(IOverlayManager overlayManager) {
        for (int i = 0; i < SLIDERS.length; i++) {
            String sliders = SLIDERS[i];
            try {
                overlayManager.setEnabled(sliders, false, USER_SYSTEM);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static void enableSlider(IOverlayManager overlayManager, String overlayName) {
        try {
            for (int i = 0; i < SLIDERS.length; i++) {
                String sliders = SLIDERS[i];
                try {
                    overlayManager.setEnabled(sliders, false, USER_SYSTEM);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            overlayManager.setEnabled(overlayName, true, USER_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void handleOverlays(String packagename, Boolean state, IOverlayManager mOverlayManager) {
        try {
            mOverlayManager.setEnabled(packagename,
                    state, USER_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static final String[] SLIDERS = {
        "com.android.theme.systemui_slider_oos",
        "com.android.theme.systemui_slider.aosp",
        "com.android.theme.systemui_slider.rui",
        "com.android.theme.systemui_slider.a12"
    };

    public static final String[] CLEAR_ALL_ICONS = {
        "com.android.theme.systemui_clearall_oos"
    };

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
        return R.xml.spark_settings_qsthemes;
    }

    @Override
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle(), this);
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(
            Context context, Lifecycle lifecycle, Fragment fragment) {
        final List<AbstractPreferenceController> controllers = new ArrayList<>();
        controllers.add(new QsColorPreferenceController(context));
        controllers.add(new QsTileStylePreferenceController(context));
        return controllers;
    }
}
