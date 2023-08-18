package com.spark.settings.fragments;

import com.android.internal.logging.nano.MetricsProto;

import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.content.Context;
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
import com.android.internal.util.spark.SparkUtils;
import com.spark.settings.preferences.colorpicker.SystemSettingColorPickerPreference;
import java.util.Locale;
import android.text.TextUtils;
import android.view.View;
import androidx.preference.Preference.OnPreferenceChangeListener;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import android.util.Log;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import com.android.internal.util.spark.udfps.UdfpsUtils;

public class LockScreenSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String AMBIENT_ICONS_COLOR = "ambient_icons_color";
    private static final String UDFPS_CATEGORY = "udfps_category";

    private SystemSettingColorPickerPreference mAmbientIconsColor;
    private PreferenceCategory mUdfpsCategory;
    private Preference mUserSwitcher;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.spark_settings_ls);

        PreferenceScreen prefSet = getPreferenceScreen();

        mAmbientIconsColor = (SystemSettingColorPickerPreference) findPreference(AMBIENT_ICONS_COLOR);
        mAmbientIconsColor.setOnPreferenceChangeListener(this);
        mUserSwitcher = findPreference("persist.sys.flags.enableBouncerUserSwitcher");
        mUserSwitcher.setOnPreferenceChangeListener(this);
        mUdfpsCategory = findPreference(UDFPS_CATEGORY);
        if (!UdfpsUtils.hasUdfpsSupport(getContext())) {
            prefSet.removePreference(mUdfpsCategory);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
	Context mContext = getActivity().getApplicationContext();
	ContentResolver resolver = mContext.getContentResolver();
        if (preference == mAmbientIconsColor) {
            SparkUtils.showSystemUiRestartDialog(getContext());
            return true;
	} else if (preference == mUserSwitcher) {
            boolean value = (Boolean) newValue;
            Settings.Secure.putIntForUser(getContentResolver(),
                Settings.Secure.PREF_KG_USER_SWITCHER, value ? 1 : 0, UserHandle.USER_CURRENT);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SPARK_SETTINGS;
    }

}
