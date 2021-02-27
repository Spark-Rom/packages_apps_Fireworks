package com.spark.settings.fragments;

import com.android.internal.logging.nano.MetricsProto;

import android.app.Activity;
import android.content.Context;
import android.os.UserHandle;
import android.content.ContentResolver;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import com.android.internal.util.spark.fod.FodUtils;
import com.android.internal.util.spark.SparkUtils;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.preference.SwitchPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;

import android.provider.Settings;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class LockScreenSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String POCKET_JUDGE = "pocket_judge";
    private static final String AOD_SCHEDULE_KEY = "always_on_display_schedule";
    private static final String FINGERPRINT_VIB = "fingerprint_success_vib";
    private static final String FOD_ICON_PICKER_CATEGORY = "fod_icon_picker";
    private static final String FOD_ANIMATION_CATEGORY = "fod_animations";

    private FingerprintManager mFingerprintManager;
    private PreferenceCategory mFODIconPickerCategory;
    private SwitchPreference mFingerprintVib;
    private Preference mPocketJudge;

    Preference mAODPref;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.spark_settings_lockscreen);
        PreferenceScreen prefScreen = getPreferenceScreen();
        final PackageManager mPm = getActivity().getPackageManager();
        final Resources res = getResources();

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

        ContentResolver resolver = getActivity().getContentResolver();
        Resources resources = getResources();

        mAODPref = findPreference(AOD_SCHEDULE_KEY);
        updateAlwaysOnSummary();
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
            case 0:
                mAODPref.setSummary(R.string.disabled);
                break;
            case 1:
                mAODPref.setSummary(R.string.night_display_auto_mode_twilight);
                break;
            case 2:
                mAODPref.setSummary(R.string.night_display_auto_mode_custom);
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
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SPARK_SETTINGS;
    }

}
