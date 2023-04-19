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
import lineageos.providers.LineageSettings;
import com.spark.settings.preferences.CustomSeekBarPreference;
import java.util.Locale;
import android.text.TextUtils;
import android.view.View;

import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;
import com.android.settings.Utils;
import android.util.Log;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

@SearchIndexable
public class ThemeSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String KEY_SHOW_BRIGHTNESS_SLIDER = "qs_show_brightness_slider";
    private static final String KEY_BRIGHTNESS_SLIDER_POSITION = "qs_brightness_slider_position";
    private static final String KEY_SHOW_AUTO_BRIGHTNESS = "qs_show_auto_brightness";
    private static final String HEADS_UP_TIMEOUT_PREF = "heads_up_timeout";
    private static final String KEY_VOLUME_PANEL_LEFT = "volume_panel_on_left";

    private SwitchPreference mVolumePanelLeft;
    private CustomSeekBarPreference mHeadsUpTimeOut;
    private ListPreference mShowBrightnessSlider;
    private ListPreference mBrightnessSliderPosition;
    private SwitchPreference mShowAutoBrightness;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.spark_settings_themes);
        final Context mContext = getActivity().getApplicationContext();
        final ContentResolver resolver = mContext.getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();

        mShowBrightnessSlider = findPreference(KEY_SHOW_BRIGHTNESS_SLIDER);
        mShowBrightnessSlider.setOnPreferenceChangeListener(this);
        boolean showSlider = LineageSettings.Secure.getIntForUser(resolver,
                LineageSettings.Secure.QS_SHOW_BRIGHTNESS_SLIDER, 1, UserHandle.USER_CURRENT) > 0;

        mBrightnessSliderPosition = findPreference(KEY_BRIGHTNESS_SLIDER_POSITION); 
        mBrightnessSliderPosition.setEnabled(showSlider);

        mShowAutoBrightness = findPreference(KEY_SHOW_AUTO_BRIGHTNESS); 
        boolean automaticAvailable = mContext.getResources().getBoolean(
                com.android.internal.R.bool.config_automatic_brightness_available);
        if (automaticAvailable) {
            mShowAutoBrightness.setEnabled(showSlider);
        } else {
            prefScreen.removePreference(mShowAutoBrightness);
        }

        mHeadsUpTimeOut = (CustomSeekBarPreference)
                            prefScreen.findPreference(HEADS_UP_TIMEOUT_PREF);
        mHeadsUpTimeOut.setDefaultValue(getDefaultDecay(mContext));


        boolean isAudioPanelOnLeft = LineageSettings.Secure.getIntForUser(resolver,
                LineageSettings.Secure.VOLUME_PANEL_ON_LEFT, isAudioPanelOnLeftSide(getActivity()) ? 1 : 0,
                UserHandle.USER_CURRENT) != 0;

        mVolumePanelLeft = (SwitchPreference) prefScreen.findPreference(KEY_VOLUME_PANEL_LEFT);
        mVolumePanelLeft.setChecked(isAudioPanelOnLeft);

    }

    private static boolean isAudioPanelOnLeftSide(Context context) {
        try {
            Context con = context.createPackageContext("org.lineageos.lineagesettings", 0);
            int id = con.getResources().getIdentifier("def_volume_panel_on_left",
                    "bool", "org.lineageos.lineagesettings");
            return con.getResources().getBoolean(id);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mShowBrightnessSlider) {
            int value = Integer.parseInt((String) newValue);
            mBrightnessSliderPosition.setEnabled(value > 0);
            if (mShowAutoBrightness != null)
                mShowAutoBrightness.setEnabled(value > 0);
            return true;
         }
         return false;
    }

    private static int getDefaultDecay(Context context) {
        int defaultHeadsUpTimeOut = 5;
        Resources systemUiResources;
        try {
            systemUiResources = context.getPackageManager().getResourcesForApplication("com.android.systemui");
            defaultHeadsUpTimeOut = systemUiResources.getInteger(systemUiResources.getIdentifier(
                    "com.android.systemui:integer/heads_up_notification_decay", null, null)) / 1000;
        } catch (Exception e) {
        }
        return defaultHeadsUpTimeOut;
    }


    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SPARK_SETTINGS;
    }

    /**
     * For search
     */
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.spark_settings_themes);
}
