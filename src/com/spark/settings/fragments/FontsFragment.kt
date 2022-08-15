/*
 * Copyright (C) 2022 FlamingoOS Project
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

import android.content.Context

import com.android.settings.R
import com.android.settings.search.BaseSearchIndexProvider
import com.android.settingslib.core.AbstractPreferenceController
import com.android.settingslib.core.lifecycle.Lifecycle
import com.android.settingslib.search.SearchIndexable
import com.spark.settings.fragments.SparkDashboardFragment

@SearchIndexable
class FontsFragment : SparkDashboardFragment() {

    override protected fun getPreferenceScreenResId() = R.xml.font_settings

    override protected fun getLogTag() = TAG

    override protected fun createPreferenceControllers(
        context: Context
    ): List<AbstractPreferenceController> = buildPreferenceControllers(
        context,
        this /* host */,
        settingsLifecycle,
    )

    companion object {
        private const val TAG = "FontsFragment"

        private const val OVERLAY_CATEGORY_ICON_ANDROID = "android.theme.customization.icon_pack.android"
        private const val OVERLAY_CATEGORY_ICON_SYSUI = "android.theme.customization.icon_pack.systemui"
        private const val OVERLAY_CATEGORY_ICON_SETTINGS = "android.theme.customization.icon_pack.settings"
        private const val OVERLAY_CATEGORY_ICON_LAUNCHER = "android.theme.customization.icon_pack.launcher"
        private const val OVERLAY_CATEGORY_ICON_THEME_PICKER = "android.theme.customization.icon_pack.themepicker"
        private const val OVERLAY_CATEGORY_FONT = "android.theme.customization.font"

        private const val TARGET_ANDROID = "android"
        private const val TARGET_SYSUI = "com.android.systemui"
        private const val TARGET_SETTINGS = "com.android.settings"
        private const val TARGET_LAUNCHER = "com.android.launcher3"
        private const val TARGET_THEME_PICKER = "com.android.wallpaper"

        private const val FONT_PREFERENCE_KEY = "font_preference"
        private const val CUSTOM_FONT_PREFERENCE_KEY = "custom_font_preference"

        private fun buildPreferenceControllers(
            context: Context,
            host: FontsFragment?,
            lifecycle: Lifecycle?,
        ): List<AbstractPreferenceController> {
            return listOf(
                ThemeOverlayPreferenceController(
                    context,
                    FONT_PREFERENCE_KEY,
                    mapOf(OVERLAY_CATEGORY_FONT to TARGET_ANDROID),
                ),
                CustomFontPreferenceController(
                    context,
                    CUSTOM_FONT_PREFERENCE_KEY,
                    host,
                    lifecycle,
                )
            )
        }

        @JvmField
        val SEARCH_INDEX_DATA_PROVIDER = object : BaseSearchIndexProvider(R.xml.font_settings) {
            override fun createPreferenceControllers(
                context: Context
            ): List<AbstractPreferenceController> = buildPreferenceControllers(
                context,
                null /* host */,
                null /* lifecycle */,
            )
        }
    }
}
