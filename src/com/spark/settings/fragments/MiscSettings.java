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
import com.spark.settings.preferences.CustomSeekBarPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceCategory;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;
import android.provider.Settings;
import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;
import android.content.Context;
import java.util.Locale;
import android.text.TextUtils;
import android.view.View;
import java.net.InetAddress;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.internal.util.spark.SparkUtils;
import android.util.Log;
import android.os.Handler;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

@SearchIndexable
public class MiscSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String BATTERY_LIGHTS_PREF = "battery_lights";
    private static final String NOTIFICATION_LIGHTS_PREF = "notification_lights";
    private static final String PREF_ADBLOCK = "persist.spark.hosts_block";
    private static final String FLASHLIGHT_CATEGORY = "flashlight_category";
    private static final String FLASHLIGHT_CALL_PREF = "flashlight_on_call";
    private static final String FLASHLIGHT_DND_PREF = "flashlight_on_call_ignore_dnd";
    private static final String FLASHLIGHT_RATE_PREF = "flashlight_on_call_rate";

    private ListPreference mFlashOnCall;
    private SwitchPreference mFlashOnCallIgnoreDND;
    private CustomSeekBarPreference mFlashOnCallRate;
    private Preference mBatLights;
    private Preference mNotLights;
    private PreferenceCategory lightsCategory;
    private Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.spark_settings_misc);
        final PreferenceScreen prefScreen = getPreferenceScreen();
        final Context mContext = getActivity().getApplicationContext();
        final Resources res = mContext.getResources();
        final ContentResolver resolver = mContext.getContentResolver();

        mBatLights = (Preference) prefScreen.findPreference(BATTERY_LIGHTS_PREF);
        boolean mBatLightsSupported = res.getInteger(
                org.lineageos.platform.internal.R.integer.config_deviceLightCapabilities) >= 64;
        if (!mBatLightsSupported)
            prefScreen.removePreference(mBatLights);

        mNotLights = (Preference) prefScreen.findPreference(NOTIFICATION_LIGHTS_PREF);
        boolean mNotLightsSupported = res.getBoolean(
                com.android.internal.R.bool.config_intrusiveNotificationLed);
        if (!mNotLightsSupported)
            prefScreen.removePreference(mNotLights);

        if (!mBatLightsSupported && !mNotLightsSupported) {
            lightsCategory = (PreferenceCategory) prefScreen.findPreference("light_brightness");
            prefScreen.removePreference(lightsCategory);
        }
        if (!SparkUtils.deviceHasFlashlight(mContext)) {
            final PreferenceCategory flashlightCategory =
                    (PreferenceCategory) prefScreen.findPreference(FLASHLIGHT_CATEGORY);
            prefScreen.removePreference(flashlightCategory);
        } else {
            mFlashOnCall = (ListPreference)
                    prefScreen.findPreference(FLASHLIGHT_CALL_PREF);
            mFlashOnCall.setOnPreferenceChangeListener(this);

            mFlashOnCallIgnoreDND = (SwitchPreference)
                    prefScreen.findPreference(FLASHLIGHT_DND_PREF);
            int value = Settings.System.getInt(resolver,
                    Settings.System.FLASHLIGHT_ON_CALL, 0);

            mFlashOnCallRate = (CustomSeekBarPreference)
                    prefScreen.findPreference(FLASHLIGHT_RATE_PREF);

            mFlashOnCallIgnoreDND.setEnabled(value > 1);
            mFlashOnCallRate.setEnabled(value > 0);
        }

        findPreference(PREF_ADBLOCK).setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
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
        } else if (preference == mFlashOnCall) {
            int value = Integer.parseInt((String) newValue);
            mFlashOnCallIgnoreDND.setEnabled(value > 1);
            mFlashOnCallRate.setEnabled(value > 0);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SPARK_SETTINGS;
    }

    /**
     * For search
     */
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.spark_settings_misc) {

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    List<String> keys = super.getNonIndexableKeys(context);
                    final Resources res = context.getResources();

                    boolean mBatLightsSupported = res.getInteger(
                            org.lineageos.platform.internal.R.integer.config_deviceLightCapabilities) >= 64;
                    if (!mBatLightsSupported)
                        keys.add(BATTERY_LIGHTS_PREF);

                    boolean mNotLightsSupported = res.getBoolean(
                            com.android.internal.R.bool.config_intrusiveNotificationLed);
                    if (!mNotLightsSupported)
                        keys.add(NOTIFICATION_LIGHTS_PREF);

                    if (!SparkUtils.deviceHasFlashlight(context)) {
                        keys.add(FLASHLIGHT_CALL_PREF);
                        keys.add(FLASHLIGHT_DND_PREF);
                        keys.add(FLASHLIGHT_RATE_PREF);
                    }

                    return keys;
                }
            };
}
