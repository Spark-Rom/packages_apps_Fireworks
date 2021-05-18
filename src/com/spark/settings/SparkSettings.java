/*
 * Copyright (C) 2020 The Pure Nexus Project
 * used for Project Spark
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.spark.settings;

import com.android.internal.logging.nano.MetricsProto;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Surface;
import android.preference.Preference;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.Toast;
import android.view.View;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.ComponentName;
import com.google.android.material.card.MaterialCardView;

import com.android.settings.R;

import com.spark.settings.preferences.Utils;
import com.spark.settings.fragments.ThemeSettings;
import com.spark.settings.fragments.StatusBarSettings;
import com.spark.settings.fragments.LockScreenSettings;
import com.spark.settings.fragments.PowerSettings;
import com.spark.settings.fragments.GestureSettings;
import com.spark.settings.fragments.NotificationSettings;
import com.spark.settings.fragments.ButtonSettings;
import com.spark.settings.fragments.MiscSettings;
import com.spark.settings.fragments.NavigationSettings;
import com.spark.settings.fragments.QuickSettings;

import com.android.settings.SettingsPreferenceFragment;

public class SparkSettings extends SettingsPreferenceFragment implements View.OnClickListener {

  MaterialCardView mQuickSettingsCard;
  MaterialCardView mStatusbarSettingsCard;
  MaterialCardView mLockScreenSettingsCard;
  MaterialCardView mPowerSettingsCard;
  MaterialCardView mGestureSettingsCard;
  MaterialCardView mNotificationSettingsCard;
  MaterialCardView mButtonSettingsCard;
  MaterialCardView mThemeSettingsCard;
  MaterialCardView mNavigationSettingsCard;
  MaterialCardView mMiscSettingsCard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.spark_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mQuickSettingsCard = (MaterialCardView) view.findViewById(R.id.quicksettings_card);
        mQuickSettingsCard.setOnClickListener(this);

        mStatusbarSettingsCard = (MaterialCardView) view.findViewById(R.id.statusbarsettings_card);
        mStatusbarSettingsCard.setOnClickListener(this);

        mLockScreenSettingsCard = (MaterialCardView) view.findViewById(R.id.lockscreensettings_card);
        mLockScreenSettingsCard.setOnClickListener(this);

        mPowerSettingsCard = (MaterialCardView) view.findViewById(R.id.powersettings_card);
        mPowerSettingsCard.setOnClickListener(this);

        mGestureSettingsCard = (MaterialCardView) view.findViewById(R.id.gesturesettings_card);
        mGestureSettingsCard.setOnClickListener(this);

        mNotificationSettingsCard = (MaterialCardView) view.findViewById(R.id.notificationsettings_card);
        mNotificationSettingsCard.setOnClickListener(this);

        mButtonSettingsCard = (MaterialCardView) view.findViewById(R.id.buttonsettings_card);
        mButtonSettingsCard.setOnClickListener(this);

        mThemeSettingsCard = (MaterialCardView) view.findViewById(R.id.themesettings_card);
        mThemeSettingsCard.setOnClickListener(this);

        mNavigationSettingsCard = (MaterialCardView) view.findViewById(R.id.navigationsettings_card);
        mNavigationSettingsCard.setOnClickListener(this);

        mMiscSettingsCard = (MaterialCardView) view.findViewById(R.id.miscsettings_card);
        mMiscSettingsCard.setOnClickListener(this);
        }

    @Override
    public void onClick(View view) {
        int id = view.getId();
            if (id == R.id.quicksettings_card)
              {
                QuickSettings quicksettingsfragment = new QuickSettings();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.replace(this.getId(), quicksettingsfragment);
                transaction.addToBackStack(null);
                transaction.commit();
               }
            if (id == R.id.statusbarsettings_card)
              {
                StatusBarSettings statusbarsettingsfragment = new StatusBarSettings();
                FragmentTransaction transaction1 = getFragmentManager().beginTransaction();
                transaction1.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction1.replace(this.getId(), statusbarsettingsfragment);
                transaction1.addToBackStack(null);
                transaction1.commit();
              }
            if (id == R.id.lockscreensettings_card)
              {
                LockScreenSettings lockscreensettingsfragment = new LockScreenSettings();
                FragmentTransaction transaction2 = getFragmentManager().beginTransaction();
                transaction2.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction2.replace(this.getId(), lockscreensettingsfragment);
                transaction2.addToBackStack(null);
                transaction2.commit();
               }
            if (id == R.id.powersettings_card)
              {
                PowerSettings powersettingsfragment = new PowerSettings();
                FragmentTransaction transaction3 = getFragmentManager().beginTransaction();
                transaction3.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction3.replace(this.getId(), powersettingsfragment);
                transaction3.addToBackStack(null);
                transaction3.commit();
               }
            if (id == R.id.gesturesettings_card)
              {
                GestureSettings gesturesettingsfragment = new GestureSettings();
                FragmentTransaction transaction4 = getFragmentManager().beginTransaction();
                transaction4.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction4.replace(this.getId(), gesturesettingsfragment);
                transaction4.addToBackStack(null);
                transaction4.commit();
              }
            if (id == R.id.notificationsettings_card)
              {
                NotificationSettings notificationsettingsfragment = new NotificationSettings();
                FragmentTransaction transaction5 = getFragmentManager().beginTransaction();
                transaction5.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction5.replace(this.getId(), notificationsettingsfragment);
                transaction5.addToBackStack(null);
                transaction5.commit();
               }
            if (id == R.id.buttonsettings_card)
              {
                ButtonSettings buttonsettingsfragment = new ButtonSettings();
                FragmentTransaction transaction6 = getFragmentManager().beginTransaction();
                transaction6.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction6.replace(this.getId(), buttonsettingsfragment);
                transaction6.addToBackStack(null);
                transaction6.commit();
               }
            if (id == R.id.themesettings_card)
              {
                ThemeSettings themesettingsfragment = new ThemeSettings();
                FragmentTransaction transaction7 = getFragmentManager().beginTransaction();
                transaction7.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction7.replace(this.getId(), themesettingsfragment);
                transaction7.addToBackStack(null);
                transaction7.commit();
               }
            if (id == R.id.miscsettings_card)
              {
                MiscSettings miscsettingsfragment = new MiscSettings();
                FragmentTransaction transaction8 = getFragmentManager().beginTransaction();
                transaction8.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction8.replace(this.getId(), miscsettingsfragment);
                transaction8.addToBackStack(null);
                transaction8.commit();
               }
           if (id == R.id.navigationsettings_card)
             {
               NavigationSettings navigationsettingsfragment = new NavigationSettings();
               FragmentTransaction transaction9 = getFragmentManager().beginTransaction();
               transaction9.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
               transaction9.replace(this.getId(), navigationsettingsfragment);
               transaction9.addToBackStack(null);
               transaction9.commit();
              }
        }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SPARK_SETTINGS;
    }

    public static void lockCurrentOrientation(Activity activity) {
        int currentRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int orientation = activity.getResources().getConfiguration().orientation;
        int frozenRotation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        switch (currentRotation) {
            case Surface.ROTATION_0:
                frozenRotation = orientation == Configuration.ORIENTATION_LANDSCAPE
                        ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                break;
            case Surface.ROTATION_90:
                frozenRotation = orientation == Configuration.ORIENTATION_PORTRAIT
                        ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                        : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                break;
            case Surface.ROTATION_180:
                frozenRotation = orientation == Configuration.ORIENTATION_LANDSCAPE
                        ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                        : ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                break;
            case Surface.ROTATION_270:
                frozenRotation = orientation == Configuration.ORIENTATION_PORTRAIT
                        ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        : ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                break;
        }
        activity.setRequestedOrientation(frozenRotation);
    }
}
