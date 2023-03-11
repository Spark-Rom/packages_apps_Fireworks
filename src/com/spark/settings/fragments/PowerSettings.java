package com.spark.settings.fragments;

import static com.android.internal.util.spark.globalactions.PowerMenuConstants.*;

import android.Manifest;
import android.content.Context;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.service.controls.ControlsProviderService;
import com.android.internal.logging.nano.MetricsProto;
import androidx.preference.SwitchPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.Utils;
import com.android.settings.R;

import java.util.ArrayList;
import java.util.List;

import com.android.internal.util.spark.globalactions.SparkGlobalActions;
import com.android.internal.util.spark.globalactions.PowerMenuConstants;
import com.android.internal.util.EmergencyAffordanceManager;
import com.android.internal.widget.LockPatternUtils;
import com.android.settingslib.applications.ServiceListing;

import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.regex.Pattern;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class PowerSettings extends SettingsPreferenceFragment {

    final static String TAG = "PowerSettings";

    private static final String CATEGORY_POWER_MENU_ITEMS = "power_menu_items";

    private PreferenceCategory mPowerMenuItemsCategory;

    private SwitchPreference mScreenshotPref;
    private SwitchPreference mOnTheGoPref;
    private SwitchPreference mAirplanePref;
    private SwitchPreference mUsersPref;
    private SwitchPreference mLockDownPref;
    private SwitchPreference mEmergencyPref;
    private SwitchPreference mDeviceControlsPref;
    private SwitchPreference mRestartSystemUIPref;

    private SparkGlobalActions mSparkGlobalActions;

    Context mContext;
    private LockPatternUtils mLockPatternUtils;
    private UserManager mUserManager;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.spark_settings_power);
        mContext = getActivity().getApplicationContext();
        mLockPatternUtils = new LockPatternUtils(mContext);
        mUserManager = UserManager.get(mContext);
        mSparkGlobalActions = SparkGlobalActions.getInstance(mContext);

        mPowerMenuItemsCategory = findPreference(CATEGORY_POWER_MENU_ITEMS);

        for (String action : PowerMenuConstants.getAllActions()) {
            if (action.equals(GLOBAL_ACTION_KEY_SCREENSHOT)) {
                mScreenshotPref = (SwitchPreference) findPreference(GLOBAL_ACTION_KEY_SCREENSHOT);
            } else if (action.equals(GLOBAL_ACTION_KEY_ONTHEGO)) {
                mOnTheGoPref = (SwitchPreference) findPreference(GLOBAL_ACTION_KEY_ONTHEGO);    
            } else if (action.equals(GLOBAL_ACTION_KEY_AIRPLANE)) {
                mAirplanePref = (SwitchPreference) findPreference(GLOBAL_ACTION_KEY_AIRPLANE);
            } else if (action.equals(GLOBAL_ACTION_KEY_USERS)) {
                mUsersPref = (SwitchPreference) findPreference(GLOBAL_ACTION_KEY_USERS);
            } else if (action.equals(GLOBAL_ACTION_KEY_LOCKDOWN)) {
                mLockDownPref = (SwitchPreference) findPreference(GLOBAL_ACTION_KEY_LOCKDOWN);
            } else if (action.equals(GLOBAL_ACTION_KEY_EMERGENCY)) {
                mEmergencyPref = (SwitchPreference) findPreference(GLOBAL_ACTION_KEY_EMERGENCY);
            } else if (action.equals(GLOBAL_ACTION_KEY_DEVICECONTROLS)) {
                mDeviceControlsPref = findPreference(GLOBAL_ACTION_KEY_DEVICECONTROLS);
            } else if (action.equals(GLOBAL_ACTION_KEY_RESTART_SYSTEMUI)) {
                mRestartSystemUIPref = (SwitchPreference) findPreference(GLOBAL_ACTION_KEY_RESTART_SYSTEMUI);
            }
        }

        if (!Utils.isVoiceCapable(getActivity())) {
            mPowerMenuItemsCategory.removePreference(mEmergencyPref);
            mEmergencyPref = null;
        }

    }


    @Override
    public void onStart() {
        super.onStart();

        if (mScreenshotPref != null) {
            mScreenshotPref.setChecked(mSparkGlobalActions.userConfigContains(
                    GLOBAL_ACTION_KEY_SCREENSHOT));
        }

        if (mOnTheGoPref != null) {
            mOnTheGoPref.setChecked(mSparkGlobalActions.userConfigContains(
                    GLOBAL_ACTION_KEY_ONTHEGO));
        }

        if (mAirplanePref != null) {
            mAirplanePref.setChecked(mSparkGlobalActions.userConfigContains(
                    GLOBAL_ACTION_KEY_AIRPLANE));
        }

        if (mEmergencyPref != null) {
            mEmergencyPref.setChecked(mSparkGlobalActions.userConfigContains(
                    GLOBAL_ACTION_KEY_EMERGENCY));
        }

        if (mDeviceControlsPref != null) {
            mDeviceControlsPref.setChecked(mSparkGlobalActions.userConfigContains(
                    GLOBAL_ACTION_KEY_DEVICECONTROLS));

            // Enable preference if any device control app is installed
            ServiceListing serviceListing = new ServiceListing.Builder(mContext)
                    .setIntentAction(ControlsProviderService.SERVICE_CONTROLS)
                    .setPermission(Manifest.permission.BIND_CONTROLS)
                    .setNoun("Controls Provider")
                    .setSetting("controls_providers")
                    .setTag("controls_providers")
                    .build();
            serviceListing.addCallback(
                    services -> mDeviceControlsPref.setEnabled(!services.isEmpty()));
            serviceListing.reload();
        }

        if (mRestartSystemUIPref != null) {
            mRestartSystemUIPref.setChecked(mSparkGlobalActions.userConfigContains(
                    GLOBAL_ACTION_KEY_RESTART_SYSTEMUI));
        }

        updatePreferences();
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePreferences();
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        boolean value;

        if (preference == mScreenshotPref) {
            value = mScreenshotPref.isChecked();
            mSparkGlobalActions.updateUserConfig(value, GLOBAL_ACTION_KEY_SCREENSHOT);

        } else if (preference == mOnTheGoPref) {
            value = mOnTheGoPref.isChecked();
            mSparkGlobalActions.updateUserConfig(value, GLOBAL_ACTION_KEY_ONTHEGO);

        } else if (preference == mAirplanePref) {
            value = mAirplanePref.isChecked();
            mSparkGlobalActions.updateUserConfig(value, GLOBAL_ACTION_KEY_AIRPLANE);

        } else if (preference == mUsersPref) {
            value = mUsersPref.isChecked();
            mSparkGlobalActions.updateUserConfig(value, GLOBAL_ACTION_KEY_USERS);

        } else if (preference == mLockDownPref) {
            value = mLockDownPref.isChecked();
            mSparkGlobalActions.updateUserConfig(value, GLOBAL_ACTION_KEY_LOCKDOWN);

        } else if (preference == mEmergencyPref) {
            value = mEmergencyPref.isChecked();
            mSparkGlobalActions.updateUserConfig(value, GLOBAL_ACTION_KEY_EMERGENCY);

        } else if (preference == mDeviceControlsPref) {
            value = mDeviceControlsPref.isChecked();
            mSparkGlobalActions.updateUserConfig(value, GLOBAL_ACTION_KEY_DEVICECONTROLS);

        } else if (preference == mRestartSystemUIPref) {
            value = mRestartSystemUIPref.isChecked();
            mSparkGlobalActions.updateUserConfig(value, GLOBAL_ACTION_KEY_RESTART_SYSTEMUI);

        } else {
            return super.onPreferenceTreeClick(preference);
        }
        return true;
    }

    private void updatePreferences() {
        boolean isKeyguardSecure = mLockPatternUtils.isSecure(UserHandle.myUserId());
        if (mLockDownPref != null) {
            mLockDownPref.setEnabled(isKeyguardSecure);
            mLockDownPref.setChecked(mSparkGlobalActions.userConfigContains(
                    GLOBAL_ACTION_KEY_LOCKDOWN));
            if (isKeyguardSecure) {
                mLockDownPref.setSummary(null);
            } else {
                mLockDownPref.setSummary(R.string.power_menu_lockdown_unavailable);
            }
        }
        if (mUsersPref != null) {
            if (!UserHandle.MU_ENABLED || !UserManager.supportsMultipleUsers()) {
                mPowerMenuItemsCategory.removePreference(mUsersPref);
                mUsersPref = null;
            } else {
                List<UserInfo> users = mUserManager.getUsers();
                boolean enabled = (users.size() > 1);
                mUsersPref.setChecked(mSparkGlobalActions.userConfigContains(
                        GLOBAL_ACTION_KEY_USERS) && enabled);
                mUsersPref.setEnabled(enabled);
            }
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
            new BaseSearchIndexProvider(R.xml.spark_settings_power);
}
