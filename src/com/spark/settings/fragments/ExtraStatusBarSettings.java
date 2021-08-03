package com.spark.settings.fragments;

import com.android.internal.logging.nano.MetricsProto;

import static android.os.UserHandle.USER_SYSTEM;
import android.os.RemoteException;
import android.os.ServiceManager;
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
import android.net.Uri;
import android.database.ContentObserver;
import android.os.Handler;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.content.ContentResolver;
import android.content.res.Resources;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
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
import com.spark.settings.preferences.SystemSettingListPreference;

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

public class ExtraStatusBarSettings extends SettingsPreferenceFragment implements
         Preference.OnPreferenceChangeListener {

    private IOverlayManager mOverlayManager;
    private static final String SYSUI_ROUNDED_SIZE = "sysui_rounded_size";
    private static final String SYSUI_ROUNDED_CONTENT_PADDING = "sysui_rounded_content_padding";
    private static final String SYSUI_ROUNDED_FWVALS = "sysui_rounded_fwvals";
    private static final String VO_ICON_PICKER = "vo_icon_picker";

    private CustomSeekBarPreference mCornerRadius;
    private CustomSeekBarPreference mContentPadding;
    private SecureSettingSwitchPreference mRoundedFwvals;
    private SystemSettingListPreference mVo;
    private Handler mHandler;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.spark_settings_extrastatusbar);

        PreferenceScreen prefSet = getPreferenceScreen();
        final ContentResolver resolver = getActivity().getContentResolver();
        Context mContext = getContext();
        mOverlayManager = IOverlayManager.Stub.asInterface(
                ServiceManager.getService(Context.OVERLAY_SERVICE));
        mVo = (SystemSettingListPreference) findPreference(VO_ICON_PICKER);
        mCustomSettingsObserver.observe();
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
                    Settings.System.VO_ICON_PICKER ),
                    false, this, UserHandle.USER_ALL);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            if (uri.equals(Settings.System.getUriFor(Settings.System.VO_ICON_PICKER ))) {
                updateVo();
            }
        }
    }

    private void updateVo() {
        ContentResolver resolver = getActivity().getContentResolver();

        boolean VoDef = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.VO_ICON_PICKER , 0, UserHandle.USER_CURRENT) == 0;
        boolean VoVivo = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.VO_ICON_PICKER , 0, UserHandle.USER_CURRENT) == 1;

        if (VoDef) {
            setDefaultVo(mOverlayManager);
        } else if (VoVivo) {
            enableSettingsVo(mOverlayManager, "com.android.theme.systemui_voiconpack.vivo");
        }
    }

    public static void setDefaultVo(IOverlayManager overlayManager) {
        for (int i = 0; i < VO.length; i++) {
            String vo = VO[i];
            try {
                overlayManager.setEnabled(vo, false, USER_SYSTEM);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static void enableSettingsVo(IOverlayManager overlayManager, String overlayName) {
        try {
            for (int i = 0; i < VO.length; i++) {
                String vo = VO[i];
                try {
                    overlayManager.setEnabled(vo, false, USER_SYSTEM);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            overlayManager.setEnabled(overlayName, true, USER_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void handleOverlays(String packagename, Boolean state, IOverlayManager mOverlayManager) {
        try {
            mOverlayManager.setEnabled(packagename,
                    state, USER_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static final String[] VO = {
        "com.android.theme.systemui_voiconpack.vivo"
    };

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
        } else if (preference == mVo) {
            mCustomSettingsObserver.observe();
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
            new BaseSearchIndexProvider(R.xml.spark_settings_extrastatusbar);

}
