package com.spark.settings.fragments;

import com.android.internal.logging.nano.MetricsProto;

import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.content.ContentResolver;
import android.content.res.Resources;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceCategory;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;
import android.provider.Settings;
import com.android.settings.R;
import com.android.settings.display.OverlayCategoryPreferenceController;
import com.android.settings.display.TapToWakePreferenceController;
import java.util.Locale;
import android.text.TextUtils;
import android.view.View;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import android.content.Context;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import android.util.Log;
import androidx.fragment.app.Fragment;
import com.android.settings.dashboard.DashboardFragment;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class GestureSettings extends DashboardFragment implements
        OnPreferenceChangeListener {

    public static final String TAG = "GestureSettings";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);


        PreferenceScreen prefSet = getPreferenceScreen();

    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.spark_settings_gestures;
    }

    @Override
    protected String getLogTag() {
       return TAG;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {

        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SPARK_SETTINGS;
    }

    @Override
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle(), this);
   }

    private static List<AbstractPreferenceController> buildPreferenceControllers(
            Context context, Lifecycle lifecycle, Fragment fragment) {
        final List<AbstractPreferenceController> controllers = new ArrayList<>();
        controllers.add(new TapToWakePreferenceController(context));
        return controllers;
    }
}
