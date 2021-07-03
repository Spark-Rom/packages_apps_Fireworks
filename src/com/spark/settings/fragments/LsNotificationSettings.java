package com.spark.settings.fragments;

import com.android.internal.logging.nano.MetricsProto;

import android.app.Activity;
import android.app.WallpaperManager;
import android.os.ParcelFileDescriptor;
import android.content.Context;
import android.os.UserHandle;
import android.graphics.Color;
import android.content.ContentResolver;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import com.android.internal.util.spark.fod.FodUtils;
import com.android.internal.util.spark.SparkUtils;
import android.hardware.biometrics.BiometricSourceType;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.preference.SwitchPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.spark.settings.preferences.CustomSeekBarPreference;
import com.spark.settings.preferences.SystemSettingSwitchPreference;
import com.spark.settings.preferences.SystemSettingSwitchPreference;
import com.android.settings.search.BaseSearchIndexProvider;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.os.Handler;
import android.os.UserHandle;
import android.os.ParcelFileDescriptor;

import android.provider.Settings;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import java.io.FileDescriptor;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class LsNotificationSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String AMBIENT_ICONS_LOCKSCREEN = "ambient_icons_lockscreen";
    private static final String LOCKSCREEN_MAX_NOTIF_CONFIG = "lockscreen_max_notif_cofig";
    private Handler mHandler;
    private CustomSeekBarPreference mMaxKeyguardNotifConfig;
    private SystemSettingSwitchPreference mAmbientIconsLockscreen;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.spark_settings_lsnotifications);
        PreferenceScreen prefScreen = getPreferenceScreen();
        final PackageManager mPm = getActivity().getPackageManager();
        final Resources res = getResources();
        Context mContext = getContext();
        ContentResolver resolver = getActivity().getContentResolver();

        mMaxKeyguardNotifConfig = (CustomSeekBarPreference) findPreference(LOCKSCREEN_MAX_NOTIF_CONFIG);
        int kgconf = Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKSCREEN_MAX_NOTIF_CONFIG, 3);
        mMaxKeyguardNotifConfig.setValue(kgconf);
        mMaxKeyguardNotifConfig.setOnPreferenceChangeListener(this);

        mAmbientIconsLockscreen = (SystemSettingSwitchPreference) findPreference(AMBIENT_ICONS_LOCKSCREEN);
        mAmbientIconsLockscreen.setChecked((Settings.System.getInt(resolver,
                Settings.System.AMBIENT_ICONS_LOCKSCREEN, 0) == 1));
        mAmbientIconsLockscreen.setOnPreferenceChangeListener(this);

        }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mMaxKeyguardNotifConfig) {
            int kgconf = (Integer) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_MAX_NOTIF_CONFIG, kgconf);
            return true;
        } else if (preference == mAmbientIconsLockscreen) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.AMBIENT_ICONS_LOCKSCREEN, value ? 1 : 0);
            SparkUtils.showSystemUiRestartDialog(getContext());
            return true;
        }
        return false;
    }


    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SPARK_SETTINGS;
    }

    /**
     * For Search.
     */

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.spark_settings_lsnotifications);

}
