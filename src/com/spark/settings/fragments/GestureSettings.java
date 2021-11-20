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
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceCategory;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;
import android.provider.Settings;
import com.android.settings.R;
import com.android.settings.display.OverlayCategoryPreferenceController;
import com.android.settings.display.TapToWakePreferenceController;
import java.util.Locale;
import android.text.TextUtils;
import android.view.View;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import android.content.Context;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import android.util.Log;
import androidx.fragment.app.Fragment;
import com.android.settings.dashboard.DashboardFragment;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import android.widget.Toast;

import com.spark.support.preferences.CustomSeekBarPreference;
import com.spark.support.preferences.SystemSettingSwitchPreference;


public class GestureSettings extends DashboardFragment implements
        OnPreferenceChangeListener {

    private static final String TORCH_POWER_BUTTON_GESTURE = "torch_power_button_gesture";

    private static final String KEY_VOL_MUSIC_CONTROL = "volume_button_music_control";
    private static final String KEY_VOL_MUSIC_CONTROL_DELAY = "volume_button_music_control_delay";

    private SystemSettingSwitchPreference mVolMusicControl;
    private CustomSeekBarPreference mVolMusicControlDelay;

    private ListPreference mTorchPowerButton;
    public static final String TAG = "GestureSettings";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        final ContentResolver resolver = getActivity().getContentResolver();
        PreferenceScreen prefSet = getPreferenceScreen();
        mTorchPowerButton = (ListPreference) findPreference(TORCH_POWER_BUTTON_GESTURE);
        int mTorchPowerButtonValue = Settings.System.getInt(resolver,
                Settings.System.TORCH_POWER_BUTTON_GESTURE, 0);
        mTorchPowerButton.setValue(Integer.toString(mTorchPowerButtonValue));
        mTorchPowerButton.setSummary(mTorchPowerButton.getEntry());
        mTorchPowerButton.setOnPreferenceChangeListener(this);

        if (getResources().getInteger(com.android.internal.R.integer.config_deviceHardwareKeys) == 64) {
        final Preference hwkeyscategory = (Preference) prefSet
                .findPreference("button_settings");
            prefSet.removePreference(hwkeyscategory);
        }
        mVolMusicControlDelay = (CustomSeekBarPreference) findPreference(KEY_VOL_MUSIC_CONTROL_DELAY);
        int value = Settings.System.getIntForUser(resolver,
                KEY_VOL_MUSIC_CONTROL_DELAY, 500, UserHandle.USER_CURRENT);
        mVolMusicControlDelay.setValue(value);
        mVolMusicControlDelay.setOnPreferenceChangeListener(this);

        mVolMusicControl = (SystemSettingSwitchPreference) findPreference(KEY_VOL_MUSIC_CONTROL);
        boolean enabled = Settings.System.getIntForUser(resolver,
                KEY_VOL_MUSIC_CONTROL, 0, UserHandle.USER_CURRENT) == 1;
        mVolMusicControl.setChecked(enabled);
        mVolMusicControl.setOnPreferenceChangeListener(this);
        mVolMusicControlDelay.setVisible(enabled);

    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.spark_settings_gestures;
    }

    @Override
    protected String getLogTag() {
       return TAG;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mTorchPowerButton) {
            int mTorchPowerButtonValue = Integer.valueOf((String) newValue);
            int index = mTorchPowerButton.findIndexOfValue((String) newValue);
            mTorchPowerButton.setSummary(
                    mTorchPowerButton.getEntries()[index]);
            Settings.System.putInt(resolver, Settings.System.TORCH_POWER_BUTTON_GESTURE,
                    mTorchPowerButtonValue);
            return true;
        } else if (preference == mVolMusicControl) {
            boolean enabled = (Boolean) newValue;
            Settings.System.putIntForUser(resolver,
                    KEY_VOL_MUSIC_CONTROL, enabled ? 1 : 0, UserHandle.USER_CURRENT);
            mVolMusicControlDelay.setVisible(enabled);
            return true;
        } else if (preference == mVolMusicControlDelay) {
            int value = (Integer) newValue;
            Settings.System.putIntForUser(resolver,
                    KEY_VOL_MUSIC_CONTROL_DELAY, value, UserHandle.USER_CURRENT);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SPARK_SETTINGS;
    }

    @Override
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle(), this);
   }

    private static List<AbstractPreferenceController> buildPreferenceControllers(
            Context context, Lifecycle lifecycle, Fragment fragment) {
        final List<AbstractPreferenceController> controllers = new ArrayList<>();
        controllers.add(new TapToWakePreferenceController(context));
        return controllers;
    }
}
