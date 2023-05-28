package com.spark.settings.fragments;

import com.android.internal.logging.nano.MetricsProto;

import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
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
import com.android.internal.util.spark.ThemeUtils;

import com.spark.settings.preferences.SystemSettingListPreference;
import com.spark.settings.preferences.SystemSettingSwitchPreference;
import com.spark.settings.preferences.SystemSettingEditTextPreference;
import java.util.Locale;
import android.text.TextUtils;
import android.view.View;

import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;
import com.android.settings.Utils;
import android.util.Log;
import com.android.internal.util.spark.SparkUtils;

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
    private static final String KEY_PREF_BATTERY_ESTIMATE = "qs_show_battery_estimate";
    private static final String SETTINGS_DASHBOARD_STYLE = "settings_dashboard_style";
    private static final String SETTINGS_HEADER_IMAGE = "settings_header_image";
    private static final String SETTINGS_HEADER_IMAGE_RANDOM = "settings_header_image_random";
    private static final String SETTINGS_HEADER_TEXT = "settings_header_text";
    private static final String SETTINGS_HEADER_TEXT_ENABLED = "settings_header_text_enabled";
    private static final String SETTINGS_CONTEXTUAL_MESSAGES = "settings_contextual_messages";
    private static final String USE_STOCK_LAYOUT = "use_stock_layout";
    private static final String ABOUT_PHONE_STYLE = "about_card_style";
    private static final String HIDE_USER_CARD = "hide_user_card";
    private static final String KEY_SYS_INFO = "qs_system_info";
    private static final String KEY_SYS_INFO_ICON = "qs_system_info_icon";
    private static final String KEY_QS_PANEL_STYLE  = "qs_panel_style";
    private static final String overlayThemeTarget  = "com.android.systemui";
    private static final String QS_PAGE_TRANSITIONS = "custom_transitions_page_tile";
    private static final String KEY_PREF_TILE_ANIM_STYLE = "qs_tile_animation_style";
    private static final String KEY_PREF_TILE_ANIM_DURATION = "qs_tile_animation_duration";
    private static final String KEY_PREF_TILE_ANIM_INTERPOLATOR = "qs_tile_animation_interpolator";
    private static final String KEY_QS_UI_STYLE  = "qs_ui_style";

    private ListPreference mTileAnimationStyle;
    private CustomSeekBarPreference mTileAnimationDuration;
    private ListPreference mTileAnimationInterpolator;
    private SystemSettingListPreference mPageTransitions;
    private ThemeUtils mThemeUtils;
    private Handler mHandler;
    private SystemSettingListPreference mSettingsDashBoardStyle;
    private SystemSettingListPreference mAboutPhoneStyle;
    private SystemSettingSwitchPreference mUseStockLayout;
    private SystemSettingSwitchPreference mHideUserCard;
    private Preference mSettingsHeaderImage;
    private Preference mSettingsHeaderImageRandom;
    private Preference mSettingsMessage;
    private SystemSettingEditTextPreference mSettingsHeaderText;
    private SystemSettingSwitchPreference mSettingsHeaderTextEnabled;
    private Preference mCombinedQsHeaders;
    private SwitchPreference mVolumePanelLeft;
    private CustomSeekBarPreference mHeadsUpTimeOut;
    private ListPreference mShowBrightnessSlider;
    private ListPreference mBrightnessSliderPosition;
    private SwitchPreference mShowAutoBrightness;
    private SwitchPreference mBatteryEstimate;
    private ListPreference mSystemInfo;
    private SwitchPreference mSystemInfoIcon;
    private SystemSettingListPreference mQsStyle;
    private SystemSettingListPreference mQsUI;

    private int[] currentValue = new int[2];

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.spark_settings_themes);
        final Context mContext = getActivity().getApplicationContext();
        final ContentResolver resolver = mContext.getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();
        mQsStyle = (SystemSettingListPreference) findPreference(KEY_QS_PANEL_STYLE);
        mQsUI = (SystemSettingListPreference) findPreference(KEY_QS_UI_STYLE);
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

        mThemeUtils = new ThemeUtils(getActivity());
        mCustomSettingsObserver.observe();

        mSettingsDashBoardStyle = (SystemSettingListPreference) findPreference(SETTINGS_DASHBOARD_STYLE);
        mSettingsDashBoardStyle.setOnPreferenceChangeListener(this);
        mSettingsHeaderImageRandom = findPreference(SETTINGS_HEADER_IMAGE_RANDOM);
        mSettingsHeaderImageRandom.setOnPreferenceChangeListener(this);
        mSettingsMessage = findPreference(SETTINGS_CONTEXTUAL_MESSAGES);
        mSettingsMessage.setOnPreferenceChangeListener(this);
        mSettingsHeaderImage = findPreference(SETTINGS_HEADER_IMAGE);
        mSettingsHeaderImage.setOnPreferenceChangeListener(this);
        mUseStockLayout = (SystemSettingSwitchPreference) findPreference(USE_STOCK_LAYOUT);
        mUseStockLayout.setOnPreferenceChangeListener(this);
        mAboutPhoneStyle = (SystemSettingListPreference) findPreference(ABOUT_PHONE_STYLE);
        mAboutPhoneStyle.setOnPreferenceChangeListener(this);
        mHideUserCard = (SystemSettingSwitchPreference) findPreference(HIDE_USER_CARD);
        mHideUserCard.setOnPreferenceChangeListener(this);
        mSettingsHeaderText = (SystemSettingEditTextPreference) findPreference(SETTINGS_HEADER_TEXT);
        mSettingsHeaderText.setOnPreferenceChangeListener(this);
        mSettingsHeaderTextEnabled = (SystemSettingSwitchPreference) findPreference(SETTINGS_HEADER_TEXT_ENABLED);
        mSettingsHeaderTextEnabled.setOnPreferenceChangeListener(this);

        mCombinedQsHeaders = findPreference("persist.sys.flags.combined_qs_headers");
        mCombinedQsHeaders.setOnPreferenceChangeListener(this);

        boolean turboInstalled = SparkUtils.isPackageInstalled(getContext(),
                "com.google.android.apps.turbo");
        mBatteryEstimate = findPreference(KEY_PREF_BATTERY_ESTIMATE);
        if (!turboInstalled)
            prefScreen.removePreference(mBatteryEstimate);

        mSystemInfo = (ListPreference) findPreference(KEY_SYS_INFO);
	mSystemInfoIcon = (SwitchPreference) findPreference(KEY_SYS_INFO_ICON);
        boolean mSystemInfoSupported = mContext.getResources().getBoolean(
                com.android.internal.R.bool.config_supportSystemInfo);
        if (!mSystemInfoSupported) {
            prefScreen.removePreference(mSystemInfo);
            prefScreen.removePreference(mSystemInfoIcon);
        } else {
            mSystemInfo.setOnPreferenceChangeListener(this);
            mSystemInfoIcon.setOnPreferenceChangeListener(this);
        }

        mPageTransitions = (SystemSettingListPreference) findPreference(QS_PAGE_TRANSITIONS);
        mPageTransitions.setOnPreferenceChangeListener(this);
        int customTransitions = Settings.System.getIntForUser(resolver,
                Settings.System.CUSTOM_TRANSITIONS_KEY,
                0, UserHandle.USER_CURRENT);
        mPageTransitions.setValue(String.valueOf(customTransitions));
        mPageTransitions.setSummary(mPageTransitions.getEntry());

        mTileAnimationStyle = (ListPreference) findPreference(KEY_PREF_TILE_ANIM_STYLE);
        mTileAnimationDuration = (CustomSeekBarPreference) findPreference(KEY_PREF_TILE_ANIM_DURATION);
        mTileAnimationInterpolator = (ListPreference) findPreference(KEY_PREF_TILE_ANIM_INTERPOLATOR);

        mTileAnimationStyle.setOnPreferenceChangeListener(this);

        int tileAnimationStyle = Settings.System.getIntForUser(resolver,
                Settings.System.QS_TILE_ANIMATION_STYLE, 0, UserHandle.USER_CURRENT);
        updateAnimTileStyle(tileAnimationStyle);
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
        } else if (preference == mSettingsDashBoardStyle) {
            mCustomSettingsObserver.observe();
            return true;
        } else if (preference == mUseStockLayout) {
            SparkUtils.showSettingsRestartDialog(getContext());
            return true;
        } else if (preference == mHideUserCard) {
            SparkUtils.showSettingsRestartDialog(getContext());
            return true;
        } else if (preference == mAboutPhoneStyle) {
            SparkUtils.showSettingsRestartDialog(getContext());
            return true;
        } else if (preference == mSettingsHeaderImage) {
            SparkUtils.showSettingsRestartDialog(getContext());
            return true;
        } else if (preference == mSettingsHeaderImageRandom) {
            SparkUtils.showSettingsRestartDialog(getContext());
            return true;
        } else if (preference == mSettingsMessage) {
            SparkUtils.showSettingsRestartDialog(getContext());
            return true;
        } else if (preference == mSettingsHeaderTextEnabled) {
            boolean enable = (Boolean) newValue;
            SystemProperties.set("persist.sys.settings.header_text_enabled", enable ? "true" : "false");
            SparkUtils.showSettingsRestartDialog(getContext());
            return true;
        } else if (preference == mSettingsHeaderText) {
            String value = (String) newValue;
            SystemProperties.set("persist.sys.settings.header_text", value);
            SparkUtils.showSettingsRestartDialog(getContext());
            return true;
        } else if (preference == mCombinedQsHeaders) {
            boolean value = (Boolean) newValue;
            Settings.Secure.putIntForUser(getContentResolver(),
                Settings.Secure.ENABLE_COMBINED_QS_HEADERS, value ? 1 : 0, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mSystemInfo) {
            SparkUtils.showSystemUiRestartDialog(getContext());
            return true;
        } else if (preference == mSystemInfoIcon) {
            SparkUtils.showSystemUiRestartDialog(getContext());
            return true;
        } else if (preference == mQsStyle || preference == mQsUI) {
            mCustomSettingsObserver.observe();
            return true;
	} else if (preference == mPageTransitions) {
            int customTransitions = Integer.parseInt(((String) newValue).toString());
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.CUSTOM_TRANSITIONS_KEY, customTransitions, UserHandle.USER_CURRENT);
            int index = mPageTransitions.findIndexOfValue((String) newValue);
            mPageTransitions.setSummary(
                    mPageTransitions.getEntries()[index]);
            return true;
        } else if (preference == mTileAnimationStyle) {
            int value = Integer.parseInt((String) newValue);
            updateAnimTileStyle(value);
            return true;
        }
         return false;
    }


    private void updateAnimTileStyle(int tileAnimationStyle) {
        mTileAnimationDuration.setEnabled(tileAnimationStyle != 0);
        mTileAnimationInterpolator.setEnabled(tileAnimationStyle != 0);
    }

    private CustomSettingsObserver mCustomSettingsObserver = new CustomSettingsObserver(mHandler);
    private class CustomSettingsObserver extends ContentObserver {

        CustomSettingsObserver(Handler handler) {
            super(handler);
        }

        void observe() {
            Context mContext = getContext();
            ContentResolver resolver = mContext.getContentResolver();
            resolver.registerContentObserver(Settings.System.getUriFor(
                    Settings.System.SETTINGS_DASHBOARD_STYLE),
                    false, this, UserHandle.USER_ALL);
            resolver.registerContentObserver(Settings.System.getUriFor(
                    Settings.System.QS_PANEL_STYLE),
                    false, this, UserHandle.USER_ALL);
            resolver.registerContentObserver(Settings.System.getUriFor(
                    Settings.System.QS_UI_STYLE),
                    false, this, UserHandle.USER_ALL);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            if (uri.equals(Settings.System.getUriFor(Settings.System.SETTINGS_DASHBOARD_STYLE))) {
                updateSettingsStyle();
            } else if (uri.equals(Settings.System.getUriFor(Settings.System.QS_PANEL_STYLE)) || uri.equals(Settings.System.getUriFor(Settings.System.QS_UI_STYLE))) {
                updateQsStyle();
            }
        }
    }

    private void updateQsStyle() {
        ContentResolver resolver = getActivity().getContentResolver();

        int qsPanelStyle = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.QS_PANEL_STYLE , 0, UserHandle.USER_CURRENT);

        boolean isA11Style = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.QS_UI_STYLE , 0, UserHandle.USER_CURRENT) == 1;

	String qsPanelStyleCategory = "android.theme.customization.qs_panel";
	String qsUIStyleCategory = "android.theme.customization.qs_ui";

	/// reset all overlays before applying
	resetQsOverlays(qsPanelStyleCategory);
	resetQsOverlays(qsUIStyleCategory);

	if (isA11Style) {
	    setQsStyle("com.android.system.qs.ui.A11", qsUIStyleCategory);
	}

	if (qsPanelStyle == 0) return;

        switch (qsPanelStyle) {
            case 1:
              setQsStyle("com.android.system.qs.outline", qsPanelStyleCategory);
              break;
            case 2:
            case 3:
              setQsStyle("com.android.system.qs.twotoneaccent", qsPanelStyleCategory);
              break;
            case 4:
              setQsStyle("com.android.system.qs.shaded", qsPanelStyleCategory);
              break;
            case 5:
              setQsStyle("com.android.system.qs.cyberpunk", qsPanelStyleCategory);
              break;
            case 6:
              setQsStyle("com.android.system.qs.neumorph", qsPanelStyleCategory);
              break;
            case 7:
              setQsStyle("com.android.system.qs.reflected", qsPanelStyleCategory);
              break;
            case 8:
              setQsStyle("com.android.system.qs.surround", qsPanelStyleCategory);
              break;
            case 9:
              setQsStyle("com.android.system.qs.thin", qsPanelStyleCategory);
              break;
            case 10:
              setQsStyle("com.android.system.qs.twotoneaccenttrans", qsPanelStyleCategory);
              break;
            default:
              break;
        }
    }

    public void resetQsOverlays(String category) {
        mThemeUtils.setOverlayEnabled(category, overlayThemeTarget, overlayThemeTarget);
    }

    public void setQsStyle(String overlayName, String category) {
        mThemeUtils.setOverlayEnabled(category, overlayName, overlayThemeTarget);
    }

    private void updateSettingsStyle() {
        ContentResolver resolver = getActivity().getContentResolver();

        int settingsPanelStyle = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.SETTINGS_DASHBOARD_STYLE, 0, UserHandle.USER_CURRENT);

	// reset all overlays before applying
        mThemeUtils.setOverlayEnabled("android.theme.customization.icon_pack.settings", "com.android.settings", "com.android.settings");

	if (settingsPanelStyle == 0) return;

        switch (settingsPanelStyle) {
            case 1:
              setSettingsStyle("com.android.system.settings.rui");
              break;
            case 2:
              setSettingsStyle("com.android.system.settings.arc");
              break;
            case 3:
              setSettingsStyle("com.android.system.settings.aosp");
              break;
            case 4:
              setSettingsStyle("com.android.system.settings.mt");
              break;
            case 5:
              setSettingsStyle("com.android.system.settings.card");
              break;
            default:
              break;
        }
    }

    public void setSettingsStyle(String overlayName) {
       mThemeUtils.setOverlayEnabled("android.theme.customization.icon_pack.settings", overlayName, "com.android.settings");
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
            new BaseSearchIndexProvider(R.xml.spark_settings_themes) {

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    List<String> keys = super.getNonIndexableKeys(context);
                    final Resources res = context.getResources();
                    boolean turboInstalled = SparkUtils.isPackageInstalled(context,
                            "com.google.android.apps.turbo");
                    boolean mSystemInfoSupported = res.getBoolean(
                            com.android.internal.R.bool.config_supportSystemInfo);


                    if (!turboInstalled)
                        keys.add(KEY_PREF_BATTERY_ESTIMATE);
                    if (!mSystemInfoSupported) {
                        keys.add(KEY_SYS_INFO);
                        keys.add(KEY_SYS_INFO_ICON);
                    }
                    return keys;
                }
            };
}
