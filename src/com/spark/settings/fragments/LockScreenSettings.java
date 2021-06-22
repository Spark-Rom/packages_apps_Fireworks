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

public class LockScreenSettings extends SettingsPreferenceFragment {

    private static final String FOD_CATEGORY = "fod_settings_key";
    private static final String AOD_SCHEDULE_KEY = "always_on_display_schedule";

    private Handler mHandler;

    private boolean mHasFod;

    private Preference mFODIconCategory;
    private FingerprintManager mFingerprintManager;

    static final int MODE_DISABLED = 0;
    static final int MODE_NIGHT = 1;
    static final int MODE_TIME = 2;
    static final int MODE_MIXED_SUNSET = 3;
    static final int MODE_MIXED_SUNRISE = 4;

    Preference mAODPref;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.spark_settings_lockscreen);
        PreferenceScreen prefScreen = getPreferenceScreen();
        final PackageManager mPm = getActivity().getPackageManager();

        mAODPref = findPreference(AOD_SCHEDULE_KEY);
        updateAlwaysOnSummary();

        mFODIconCategory = findPreference(FOD_CATEGORY);
        if (mFODIconCategory != null && !FodUtils.hasFodSupport(getContext())) {
            prefScreen.removePreference(mFODIconCategory);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        updateAlwaysOnSummary();
    }

    private void updateAlwaysOnSummary() {
        if (mAODPref == null) return;
        int mode = Settings.Secure.getIntForUser(getActivity().getContentResolver(),
                Settings.Secure.DOZE_ALWAYS_ON_AUTO_MODE, 0, UserHandle.USER_CURRENT);
        switch (mode) {
            default:
            case MODE_DISABLED:
                mAODPref.setSummary(R.string.disabled);
                break;
            case MODE_NIGHT:
                mAODPref.setSummary(R.string.night_display_auto_mode_twilight);
                break;
            case MODE_TIME:
                mAODPref.setSummary(R.string.night_display_auto_mode_custom);
                break;
            case MODE_MIXED_SUNSET:
                mAODPref.setSummary(R.string.always_on_display_schedule_mixed_sunset);
                break;
            case MODE_MIXED_SUNRISE:
                mAODPref.setSummary(R.string.always_on_display_schedule_mixed_sunrise);
                break;
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SPARK_SETTINGS;
    }

    /**
     * For Search.
     */

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.spark_settings_lockscreen);

}
