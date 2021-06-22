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


public class LsExtraSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String FINGERPRINT_VIB = "fingerprint_success_vib";
    private static final String LOCK_FP_ICON = "lock_fp_icon";
    private static final String POCKET_JUDGE = "pocket_judge";
    private static final String LOCKSCREEN_BLUR = "lockscreen_blur";

    private Handler mHandler;
    private Preference mPocketJudge;
    private Preference mLockscreenBlur;
    private SystemSettingSwitchPreference mLockFPIcon;
    private SwitchPreference mFingerprintVib;
    private FingerprintManager mFingerprintManager;

    private boolean mHasFod;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
		    getActivity().getActionBar().hide();
        addPreferencesFromResource(R.xml.spark_settings_lsextra);
        PreferenceScreen prefScreen = getPreferenceScreen();
        final PackageManager mPm = getActivity().getPackageManager();
        final Resources res = getResources();
        Context mContext = getContext();
        WallpaperManager manager = WallpaperManager.getInstance(mContext);
        ContentResolver resolver = getActivity().getContentResolver();

        mPocketJudge = (Preference) prefScreen.findPreference(POCKET_JUDGE);
        boolean mPocketJudgeSupported = res.getBoolean(
                com.android.internal.R.bool.config_pocketModeSupported);
        if (!mPocketJudgeSupported)
            prefScreen.removePreference(mPocketJudge);

        Resources resources = getResources();

        ParcelFileDescriptor pfd = manager.getWallpaperFile(WallpaperManager.FLAG_LOCK);
        mLockscreenBlur = (Preference) findPreference(LOCKSCREEN_BLUR);
        if (!SparkUtils.supportsBlur() || pfd != null) {
            mLockscreenBlur.setEnabled(false);
            mLockscreenBlur.setSummary(getString(R.string.lockscreen_blur_disabled));
         }

        mFingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
        mFingerprintVib = (SwitchPreference) findPreference(FINGERPRINT_VIB);
        if (mPm.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT) &&
                 mFingerprintManager != null) {
            if (!mFingerprintManager.isHardwareDetected()){
                prefScreen.removePreference(mFingerprintVib);
            } else {
                mFingerprintVib.setChecked((Settings.System.getInt(getContentResolver(),
                        Settings.System.FINGERPRINT_SUCCESS_VIB, 1) == 1));
                mFingerprintVib.setOnPreferenceChangeListener(this);
            }
        } else {
            prefScreen.removePreference(mFingerprintVib);
        }

        mLockFPIcon = findPreference(LOCK_FP_ICON);
 	FingerprintManager fingerprintManager = (FingerprintManager) mContext.getSystemService(Context.FINGERPRINT_SERVICE);
        mHasFod = FodUtils.hasFodSupport(mContext);

        if (fingerprintManager == null) {
            mLockFPIcon.setSummary(getString(R.string.unsupported_feature_summary));
            mLockFPIcon.setEnabled(false);
        } else if (!fingerprintManager.isHardwareDetected()) {
            mLockFPIcon.setSummary(getString(R.string.lock_fp_icon_no_fp_summary));
            mLockFPIcon.setEnabled(false);
        } else if (mHasFod) {
            mLockFPIcon.setSummary(getString(R.string.lock_fp_icon_fod_summary));
            mLockFPIcon.setEnabled(false);
        } else if (!fingerprintManager.hasEnrolledFingerprints()) {
            mLockFPIcon.setSummary(getString(R.string.lock_fp_icon_rart_user_summary));
            mLockFPIcon.setEnabled(false);
        } else {
            mLockFPIcon.setSummary(getString(R.string.lock_fp_icon_summary));
            mLockFPIcon.setEnabled(true);
        }
     }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mFingerprintVib) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.FINGERPRINT_SUCCESS_VIB, value ? 1 : 0);
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
            new BaseSearchIndexProvider(R.xml.spark_settings_lsextra);

}
