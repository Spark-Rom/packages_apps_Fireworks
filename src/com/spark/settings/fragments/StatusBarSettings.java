package com.spark.settings.fragments;

import com.android.internal.logging.nano.MetricsProto;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
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
import com.android.settings.search.BaseSearchIndexProvider;
import android.content.pm.PackageManager.NameNotFoundException;
import com.spark.settings.preferences.SecureSettingSwitchPreference;
import com.spark.settings.preferences.CustomSeekBarPreference;

import java.util.Locale;
import android.text.TextUtils;
import android.view.View;

import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import android.util.Log;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class StatusBarSettings extends SettingsPreferenceFragment implements
         Preference.OnPreferenceChangeListener {

    private static final String PREF_KEY_CUTOUT = "cutout_settings";
    private static final String SYSUI_ROUNDED_SIZE = "sysui_rounded_size";
    private static final String SYSUI_ROUNDED_CONTENT_PADDING = "sysui_rounded_content_padding";
    private static final String SYSUI_ROUNDED_FWVALS = "sysui_rounded_fwvals";
    private static final String CUSTOM_STATUSBAR_PADDING_START = "custom_statusbar_padding_start";
    private static final String CUSTOM_STATUSBAR_PADDING_END = "custom_statusbar_padding_end";

    private CustomSeekBarPreference mCustomStatusbarPaddingStart;
    private CustomSeekBarPreference mCustomStatusbarPaddingEnd;

    private CustomSeekBarPreference mCornerRadius;
    private CustomSeekBarPreference mContentPadding;
    private SecureSettingSwitchPreference mRoundedFwvals;


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.spark_settings_statusbar);

        PreferenceScreen prefSet = getPreferenceScreen();

        Preference mCutoutPref = (Preference) findPreference(PREF_KEY_CUTOUT);

        String hasDisplayCutout = getResources().getString(com.android.internal.R.string.config_mainBuiltInDisplayCutout);

        if (TextUtils.isEmpty(hasDisplayCutout)) {
            getPreferenceScreen().removePreference(mCutoutPref);
        }

        Resources res = null;
        Context ctx = getContext();
        float density = Resources.getSystem().getDisplayMetrics().density;

        try {
            res = ctx.getPackageManager().getResourcesForApplication("com.android.systemui");
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        // Rounded Corner Radius
        mCornerRadius = (CustomSeekBarPreference) findPreference(SYSUI_ROUNDED_SIZE);
        int resourceIdRadius = (int) ctx.getResources().getDimension(com.android.internal.R.dimen.rounded_corner_radius);
        int cornerRadius = Settings.Secure.getIntForUser(ctx.getContentResolver(), Settings.Secure.SYSUI_ROUNDED_SIZE,
                ((int) (resourceIdRadius / density)), UserHandle.USER_CURRENT);
        mCornerRadius.setValue(cornerRadius);
        mCornerRadius.setOnPreferenceChangeListener(this);

        // Rounded Content Padding
        //mContentPadding = (CustomSeekBarPreference) findPreference(SYSUI_ROUNDED_CONTENT_PADDING);
        //int resourceIdPadding = res.getIdentifier("com.android.systemui:dimen/rounded_corner_content_padding", null,
        //        null);
        //int contentPadding = Settings.Secure.getIntForUser(ctx.getContentResolver(),
        //        Settings.Secure.SYSUI_ROUNDED_CONTENT_PADDING,
        //        (int) (res.getDimension(resourceIdPadding) / density), UserHandle.USER_CURRENT);
        //mContentPadding.setValue(contentPadding);
        //mContentPadding.setOnPreferenceChangeListener(this);

        // Rounded use Framework Values
        mRoundedFwvals = (SecureSettingSwitchPreference) findPreference(SYSUI_ROUNDED_FWVALS);
        mRoundedFwvals.setOnPreferenceChangeListener(this);

        mCustomStatusbarPaddingStart = (CustomSeekBarPreference) findPreference(CUSTOM_STATUSBAR_PADDING_START);
        int customStatusbarPaddingStart = Settings.System.getIntForUser(ctx.getContentResolver(),
                Settings.System.CUSTOM_STATUSBAR_PADDING_START, res.getIdentifier("com.android.systemui:dimen/status_bar_padding_start", null, null), UserHandle.USER_CURRENT);
        mCustomStatusbarPaddingStart.setValue(customStatusbarPaddingStart);
        mCustomStatusbarPaddingStart.setOnPreferenceChangeListener(this);

        mCustomStatusbarPaddingEnd = (CustomSeekBarPreference) findPreference(CUSTOM_STATUSBAR_PADDING_END);
        int customStatusbarPaddingEnd = Settings.System.getIntForUser(getActivity().getContentResolver(),
                Settings.System.CUSTOM_STATUSBAR_PADDING_END, res.getIdentifier("com.android.systemui:dimen/status_bar_padding_end", null, null), UserHandle.USER_CURRENT);
        mCustomStatusbarPaddingEnd.setValue(customStatusbarPaddingEnd);
        mCustomStatusbarPaddingEnd.setOnPreferenceChangeListener(this);

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
       if (preference == mCornerRadius) {
            Settings.Secure.putIntForUser(getContext().getContentResolver(), Settings.Secure.SYSUI_ROUNDED_SIZE,
                    (int) newValue, UserHandle.USER_CURRENT);
            return true;
        //} else if (preference == mContentPadding) {
        //    Settings.Secure.putIntForUser(getContext().getContentResolver(), Settings.Secure.SYSUI_ROUNDED_CONTENT_PADDING,
        //            (int) objValue, UserHandle.USER_CURRENT);
        //    return true;
        } else if (preference == mRoundedFwvals) {
            restoreCorners();
            return true;
        } else if (preference == mCustomStatusbarPaddingStart) {
            int value = (Integer) newValue;
            Settings.System.putIntForUser(getContext().getContentResolver(),
                    Settings.System.CUSTOM_STATUSBAR_PADDING_START, value, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mCustomStatusbarPaddingEnd) {
            int value = (Integer) newValue;
            Settings.System.putIntForUser(getContext().getContentResolver(),
                    Settings.System.CUSTOM_STATUSBAR_PADDING_END, value, UserHandle.USER_CURRENT);
            return true;
        }
        return false;
    }

    private void restoreCorners() {
        Resources res = null;
        float density = Resources.getSystem().getDisplayMetrics().density;
        Context ctx = getContext();

        try {
            res = ctx.getPackageManager().getResourcesForApplication("com.android.systemui");
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        int resourceIdRadius = (int) ctx.getResources().getDimension(com.android.internal.R.dimen.rounded_corner_radius);
        //int resourceIdPadding = res.getIdentifier("com.android.systemui:dimen/rounded_corner_content_padding", null, null);
        mCornerRadius.setValue((int) (resourceIdRadius / density));
        //mContentPadding.setValue((int) (res.getDimension(resourceIdPadding) / density));
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SPARK_SETTINGS;
    }

    /**
     * For Search.
     */

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.spark_settings_statusbar);

}
