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
import android.content.Context;
import com.android.settings.R;

import java.util.Locale;
import android.text.TextUtils;
import android.view.View;
import java.net.InetAddress;
import android.os.Handler;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import android.util.Log;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class MiscSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String PREF_ADBLOCK = "persist.spark.hosts_block";
    private static final String CHARGING_LIGHTS_PREF = "charging_light";
    private static final String LED_CATEGORY = "led";
    private static final String NOTIFICATION_LIGHTS_PREF = "notification_light";

    private Preference mChargingLeds;
    private Preference mNotLights;
    private PreferenceCategory mLedCategory;

    private Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.spark_settings_misc);

        final ContentResolver resolver = getActivity().getContentResolver();
        final Context mContext = getActivity().getApplicationContext();
        final PreferenceScreen prefSet = getPreferenceScreen();
        final Resources res = mContext.getResources();

        findPreference(PREF_ADBLOCK).setOnPreferenceChangeListener(this);

        boolean hasLED = res.getBoolean(
                com.android.internal.R.bool.config_hasNotificationLed);
        if (hasLED) {
            mNotLights = (Preference) findPreference(NOTIFICATION_LIGHTS_PREF);
            boolean mNotLightsSupported = res.getBoolean(
                    com.android.internal.R.bool.config_intrusiveNotificationLed);
            if (!mNotLightsSupported) {
                prefSet.removePreference(mNotLights);
            }
            mChargingLeds = (Preference) findPreference(CHARGING_LIGHTS_PREF);
            if (mChargingLeds != null
                    && !getResources().getBoolean(
                            com.android.internal.R.bool.config_intrusiveBatteryLed)) {
                prefSet.removePreference(mChargingLeds);
            }
        } else {
            mLedCategory = findPreference(LED_CATEGORY);
            mLedCategory.setVisible(false);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (PREF_ADBLOCK.equals(preference.getKey())) {
            // Flush the java VM DNS cache to re-read the hosts file.
            // Delay to ensure the value is persisted before we refresh
            mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InetAddress.clearDnsCache();
                    }
            }, 1000);
            return true;
        } else {
            return false;
        }
    }


    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SPARK_SETTINGS;
    }

}
