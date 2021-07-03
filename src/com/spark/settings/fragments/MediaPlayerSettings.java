/*
 * Copyright (C) 2021 Spark
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.spark.settings.fragments;

import static android.os.UserHandle.USER_SYSTEM;

import android.app.AlertDialog;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;

import android.os.SystemProperties;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.os.RemoteException;
import android.os.ServiceManager;
import androidx.preference.*;
import android.provider.SearchIndexableResource;
import android.provider.Settings;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;

import com.android.settings.R;
import com.spark.settings.preferences.SystemSettingListPreference;
import com.spark.settings.preferences.SystemSettingSwitchPreference;
import com.android.internal.util.spark.SparkUtils;

import java.util.ArrayList;
import java.util.List;

public class MediaPlayerSettings extends DashboardFragment {
    private static final String TAG = "MediaPlayerSettings";

    private IOverlayManager mOverlayManager;
    private PackageManager mPackageManager;
    private static final String MEDIA_PLAYER_STYLE = "media_player_style";
    private static final String QS_MEDIA_PLAYER = "qs_media_player";

    private SystemSettingListPreference mMediaPlayer;
    private SystemSettingSwitchPreference mQsMediaPlayer;
    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ContentResolver resolver = getActivity().getContentResolver();
        Context mContext = getContext();
        mOverlayManager = IOverlayManager.Stub.asInterface(
                ServiceManager.getService(Context.OVERLAY_SERVICE));
        mMediaPlayer = (SystemSettingListPreference) findPreference(MEDIA_PLAYER_STYLE);
        mQsMediaPlayer = (SystemSettingSwitchPreference) findPreference(QS_MEDIA_PLAYER);

        mCustomSettingsObserver.observe();
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
                    Settings.System.MEDIA_PLAYER_STYLE ),
                    false, this, UserHandle.USER_ALL);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            if (uri.equals(Settings.System.getUriFor(Settings.System.MEDIA_PLAYER_STYLE ))) {
                updateMediaPlayer();
            }
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mMediaPlayer) {
            mCustomSettingsObserver.observe();
            return true;
        } else if (preference == mQsMediaPlayer) {
            SparkUtils.showSystemUiRestartDialog(getContext());
            return true;
        }
        return false;
    }

    private void updateMediaPlayer() {
        ContentResolver resolver = getActivity().getContentResolver();

        boolean mediaplayerS = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.MEDIA_PLAYER_STYLE , 0, UserHandle.USER_CURRENT) == 0;
        boolean mediaPlayerCompact = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.MEDIA_PLAYER_STYLE , 0, UserHandle.USER_CURRENT) == 1;

        if (mediaplayerS) {
            setDefaultMusicPlayer(mOverlayManager);
        } else if (mediaPlayerCompact) {
            enableSettingsMusic(mOverlayManager, "com.android.theme.systemui_qs.compact");
        }
    }

    public static void setDefaultMusicPlayer(IOverlayManager overlayManager) {
        for (int i = 0; i < MUSICPLAYERS.length; i++) {
            String musicplayer = MUSICPLAYERS[i];
            try {
                overlayManager.setEnabled(musicplayer, false, USER_SYSTEM);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static void enableSettingsMusic(IOverlayManager overlayManager, String overlayName) {
        try {
            for (int i = 0; i < MUSICPLAYERS.length; i++) {
                String musicplayer = MUSICPLAYERS[i];
                try {
                    overlayManager.setEnabled(musicplayer, false, USER_SYSTEM);
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

    public static final String[] MUSICPLAYERS = {
        "com.android.theme.systemui_qs.compact"
    };

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.SPARK_SETTINGS;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.spark_settings_media_player;
    }

    @Override
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle(), this);
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(
            Context context, Lifecycle lifecycle, Fragment fragment) {
        final List<AbstractPreferenceController> controllers = new ArrayList<>();
        return controllers;
    }
}
