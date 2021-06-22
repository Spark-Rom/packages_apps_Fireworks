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


public class FodSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String FOD_ANIMATION_CATEGORY = "fod_animations";
    private static final String FINGERPRINT_CUSTOM_ICON = "custom_fingerprint_icon";
    private static final int GET_CUSTOM_FP_ICON = 69;
    private Preference mFilePicker;
    private SystemSettingSwitchPreference mIconAnima;

    private Handler mHandler;


    private boolean mHasFod;

    private FingerprintManager mFingerprintManager;
    private PreferenceCategory mFODIconPickerCategory;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
		    getActivity().getActionBar().hide();
        addPreferencesFromResource(R.xml.spark_settings_fod);
        PreferenceScreen prefScreen = getPreferenceScreen();
        final PackageManager mPm = getActivity().getPackageManager();
        final Resources res = getResources();
        Context mContext = getContext();

        final PreferenceCategory fodCat = (PreferenceCategory) prefScreen
                .findPreference(FOD_ANIMATION_CATEGORY);
        final boolean isFodAnimationResources = SparkUtils.isPackageInstalled(getContext(),
                      getResources().getString(com.android.internal.R.string.config_fodAnimationPackage));
        if (!isFodAnimationResources) {
            prefScreen.removePreference(fodCat);
        }

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
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mIconAnima) {
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
            new BaseSearchIndexProvider(R.xml.spark_settings_fod);

}
