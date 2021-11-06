package com.spark.settings;

import androidx.appcompat.app.AppCompatActivity;

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
import androidx.cardview.widget.CardView;

import com.android.settings.R;

import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.content.res.Resources;
import android.view.Window;

import com.spark.settings.fragments.ThemeSettings;
import com.spark.settings.fragments.StatusBarSettings;
import com.spark.settings.fragments.LockScreenSettings;
import com.spark.settings.fragments.PowerSettings;
import com.spark.settings.fragments.GestureSettings;
import com.spark.settings.fragments.MiscSettings;

import com.android.settings.SettingsPreferenceFragment;

public class SparkSettings extends SettingsPreferenceFragment implements View.OnClickListener {

  CardView mStatusbarSettingsCard;
  CardView mLockScreenSettingsCard;
  CardView mPowerSettingsCard;
  CardView mGestureSettingsCard;
  CardView mThemeSettingsCard;
  CardView mMiscSettingsCard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.spark_settings, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Resources res = getResources();
        Window win = getActivity().getWindow();
    getActivity().setTitle(getResources().getString(R.string.app_name));

        mStatusbarSettingsCard = (CardView) view.findViewById(R.id.statusbarsettings_card);
        mStatusbarSettingsCard.setOnClickListener(this);

        mLockScreenSettingsCard = (CardView) view.findViewById(R.id.lockscreensettings_card);
        mLockScreenSettingsCard.setOnClickListener(this);

        mPowerSettingsCard = (CardView) view.findViewById(R.id.powersettings_card);
        mPowerSettingsCard.setOnClickListener(this);

        mGestureSettingsCard = (CardView) view.findViewById(R.id.gesturesettings_card);
        mGestureSettingsCard.setOnClickListener(this);

        mThemeSettingsCard = (CardView) view.findViewById(R.id.themesettings_card);
        mThemeSettingsCard.setOnClickListener(this);

        mMiscSettingsCard = (CardView) view.findViewById(R.id.miscsettings_card);
        mMiscSettingsCard.setOnClickListener(this);
        }

    @Override
    public void onClick(View view) {
        int id = view.getId();
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
            if (id == R.id.themesettings_card)
              {
                ThemeSettings themesettingsfragment = new ThemeSettings();
                FragmentTransaction transaction5 = getFragmentManager().beginTransaction();
                transaction5.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction5.replace(this.getId(), themesettingsfragment);
                transaction5.addToBackStack(null);
                transaction5.commit();
               }
            if (id == R.id.miscsettings_card)
              {
                MiscSettings miscsettingsfragment = new MiscSettings();
                FragmentTransaction transaction6 = getFragmentManager().beginTransaction();
                transaction6.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction6.replace(this.getId(), miscsettingsfragment);
                transaction6.addToBackStack(null);
                transaction6.commit();
               }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SPARK_SETTINGS;
    }

}
