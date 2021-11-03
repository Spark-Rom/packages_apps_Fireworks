/*
 * Copyright (C) 2021 AOSP-Krypton Project
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
package com.spark.settings.fragments

import android.graphics.Color
import android.os.Bundle
import android.os.UserHandle
import android.provider.Settings
import android.provider.Settings.Secure.MONET_ENGINE_CHROMA_FACTOR
import android.provider.Settings.Secure.MONET_ENGINE_COLOR_OVERRIDE
import android.provider.Settings.Secure.MONET_ENGINE_WHITE_LUMINANCE
import android.widget.Toast
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import com.spark.settings.fragments.ThemeSettings;
import com.spark.support.preferences.CustomSeekBarPreference
import com.spark.settings.preferences.SettingColorPickerPreference
import com.android.settings.R
import com.android.internal.logging.nano.MetricsProto
import com.android.settings.SettingsPreferenceFragment

class MonetEngineSettingsFragment: SettingsPreferenceFragment(),
        Preference.OnPreferenceChangeListener {

    override fun getMetricsCategory(): Int = MetricsProto.MetricsEvent.SPARK_SETTINGS

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        addPreferencesFromResource(R.xml.monet_engine_settings)

        val chromaFactor = Settings.Secure.getFloat(
            context!!.contentResolver, MONET_ENGINE_CHROMA_FACTOR,
                CHROMA_DEFAULT) * 100
        findPreference<CustomSeekBarPreference>(CHROMA_SLIDER_PREF_KEY)?.also {
            it.setValue(chromaFactor.toInt())
        }?.setOnPreferenceChangeListener(this)

    }

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean =
        if (preference.key == CHROMA_SLIDER_PREF_KEY) {
            Settings.Secure.putFloat(context!!.contentResolver,
                    MONET_ENGINE_CHROMA_FACTOR, (newValue as Int) / 100f)
        } else {
            false
        }
    
    override fun onDisplayPreferenceDialog(preference: Preference) {
        if (preference is SettingColorPickerPreference) {
            val preferenceDataStore = preference.getSettingsDataStore(context!!)
            var defaultColor: Int = preferenceDataStore.getString(preference.key, null)
                ?.takeIf { it.isNotEmpty() }
                ?.let { Color.parseColor(it) } ?: Color.GREEN
            MonetColorOverrideFragment(
                preference.key,
                preferenceDataStore,
                defaultColor,
            ).let {
                it.setTargetFragment(this, 0)
                it.show(getParentFragmentManager(), TAG)
            }
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }

    companion object {
        private const val TAG = "MonetEngineSettingsFragment"

        private const val CHROMA_SLIDER_PREF_KEY = "chroma_factor"
        private const val CHROMA_DEFAULT = 1f
    }
}
