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


public class LockScreenSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String AMBIENT_ICONS_LOCKSCREEN = "ambient_icons_lockscreen";
    private static final String POCKET_JUDGE = "pocket_judge";
    private static final String AOD_SCHEDULE_KEY = "always_on_display_schedule";
    private static final String FINGERPRINT_VIB = "fingerprint_success_vib";
    private static final String FOD_ICON_PICKER_CATEGORY = "fod_icon_picker";
    private static final String FOD_ANIMATION_CATEGORY = "fod_animations";
    private static final String LOCKSCREEN_MAX_NOTIF_CONFIG = "lockscreen_max_notif_cofig";
    private static final String LOCK_FP_ICON = "lock_fp_icon";
    private static final String LOCKSCREEN_BLUR = "lockscreen_blur";
    private static final String FINGERPRINT_CUSTOM_ICON = "custom_fingerprint_icon";
    private static final int GET_CUSTOM_FP_ICON = 69;
    private Preference mFilePicker;
    private SystemSettingSwitchPreference mIconAnima;

    private Handler mHandler;

    private SystemSettingSwitchPreference mLockFPIcon;

    private boolean mHasFod;

    private FingerprintManager mFingerprintManager;
    private PreferenceCategory mFODIconPickerCategory;
    private SwitchPreference mFingerprintVib;
    private CustomSeekBarPreference mMaxKeyguardNotifConfig;
    private Preference mPocketJudge;
    private SystemSettingSwitchPreference mAmbientIconsLockscreen;
    private Preference mLockscreenBlur;

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
        final Resources res = getResources();
        Context mContext = getContext();
        WallpaperManager manager = WallpaperManager.getInstance(mContext);
        ContentResolver resolver = getActivity().getContentResolver();

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

        mMaxKeyguardNotifConfig = (CustomSeekBarPreference) findPreference(LOCKSCREEN_MAX_NOTIF_CONFIG);
        int kgconf = Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKSCREEN_MAX_NOTIF_CONFIG, 3);
        mMaxKeyguardNotifConfig.setValue(kgconf);
        mMaxKeyguardNotifConfig.setOnPreferenceChangeListener(this);

        mFODIconPickerCategory = findPreference(FOD_ICON_PICKER_CATEGORY);
        if (mFODIconPickerCategory != null && !FodUtils.hasFodSupport(getContext())) {
            prefScreen.removePreference(mFODIconPickerCategory);
        }
        final PreferenceCategory fodCat = (PreferenceCategory) prefScreen
                .findPreference(FOD_ANIMATION_CATEGORY);
        final boolean isFodAnimationResources = SparkUtils.isPackageInstalled(getContext(),
                      getResources().getString(com.android.internal.R.string.config_fodAnimationPackage));
        if (!isFodAnimationResources) {
            prefScreen.removePreference(fodCat);
        }

        mPocketJudge = (Preference) prefScreen.findPreference(POCKET_JUDGE);
        boolean mPocketJudgeSupported = res.getBoolean(
                com.android.internal.R.bool.config_pocketModeSupported);
        if (!mPocketJudgeSupported)
            prefScreen.removePreference(mPocketJudge);

        Resources resources = getResources();

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

        ParcelFileDescriptor pfd = manager.getWallpaperFile(WallpaperManager.FLAG_LOCK);
        mLockscreenBlur = (Preference) findPreference(LOCKSCREEN_BLUR);
        if (!SparkUtils.supportsBlur() || pfd != null) {
            mLockscreenBlur.setEnabled(false);
            mLockscreenBlur.setSummary(getString(R.string.lockscreen_blur_disabled));
        }

        mAODPref = findPreference(AOD_SCHEDULE_KEY);
        updateAlwaysOnSummary();

        mAmbientIconsLockscreen = (SystemSettingSwitchPreference) findPreference(AMBIENT_ICONS_LOCKSCREEN);
        mAmbientIconsLockscreen.setChecked((Settings.System.getInt(resolver,
                Settings.System.AMBIENT_ICONS_LOCKSCREEN, 0) == 1));
        mAmbientIconsLockscreen.setOnPreferenceChangeListener(this);

        mFilePicker = (Preference) findPreference(FINGERPRINT_CUSTOM_ICON);
            final String customIconURI = Settings.System.getString(getContext().getContentResolver(),
                Settings.System.OMNI_CUSTOM_FP_ICON);

            if (!TextUtils.isEmpty(customIconURI)) {
                setPickerIcon(customIconURI);
                mFilePicker.setSummary(customIconURI);
            }

            mFilePicker.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/png");

                    startActivityForResult(intent, GET_CUSTOM_FP_ICON);

                    return true;
                }
            });
        }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
        Intent resultData) {
        if (requestCode == GET_CUSTOM_FP_ICON && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                mFilePicker.setSummary(uri.toString());
                setPickerIcon(uri.toString());
                Settings.System.putString(getContentResolver(), Settings.System.OMNI_CUSTOM_FP_ICON,
                    uri.toString());
            }
        } else if (requestCode == GET_CUSTOM_FP_ICON && resultCode == Activity.RESULT_CANCELED) {
            mFilePicker.setSummary("");
            mFilePicker.setIcon(new ColorDrawable(Color.TRANSPARENT));
            Settings.System.putString(getContentResolver(), Settings.System.OMNI_CUSTOM_FP_ICON, "");
        }
    }


    private void setPickerIcon(String uri) {
        try {
                ParcelFileDescriptor parcelFileDescriptor =
                    getContext().getContentResolver().openFileDescriptor(Uri.parse(uri), "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                parcelFileDescriptor.close();
                Drawable d = new BitmapDrawable(getResources(), image);
                mFilePicker.setIcon(d);
            }
            catch (Exception e) {}
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
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mFingerprintVib) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.FINGERPRINT_SUCCESS_VIB, value ? 1 : 0);
            return true;
        } else if (preference == mMaxKeyguardNotifConfig) {
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
        } else if (preference == mIconAnima) {
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
            new BaseSearchIndexProvider(R.xml.spark_settings_lockscreen);

}
