package com.spark.settings.fragments;

import com.android.settings.R;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextClock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.content.SharedPreferences;
import android.os.Handler;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

public class LockScreenClockPicker extends Fragment {

    private ViewPager2 viewPager;
    private String[] customFonts;
    private int currentPosition = 0;
    private SharedPreferences sharedPreferences;
    private ContentResolver resolver;
    private Handler handler;
    private boolean isViewPagerSwiped = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lockscreen_clock_font_preview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Context mContext = getActivity().getApplicationContext();
        resolver = mContext.getContentResolver();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        handler = new Handler();

        viewPager = view.findViewById(R.id.viewPager);
        customFonts = getResources().getStringArray(R.array.custom_font_entries);

        LockScreenClockPickerPagerAdapter pagerAdapter = new LockScreenClockPickerPagerAdapter(customFonts, resolver, mContext);
        viewPager.setAdapter(pagerAdapter);

        // Add a PageTransformer for the lock screen preview effect
        CompositePageTransformer pageTransformer = new CompositePageTransformer();
        pageTransformer.addTransformer(new LockScreenPageTransformer());
        viewPager.setPageTransformer(pageTransformer);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            private boolean isScrolling = false;

            @Override
            public void onPageScrollStateChanged(int state) {
                isScrolling = state != ViewPager2.SCROLL_STATE_IDLE;
                if (!isScrolling && currentPosition != -1) {
                    viewPager.setCurrentItem(currentPosition, false);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (isScrolling) {
                    currentPosition = position;
                }
                isViewPagerSwiped = (positionOffsetPixels != 0);
            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                viewPager.setCurrentItem(currentPosition, false);
                String fontName = customFonts[currentPosition];
                sharedPreferences.edit().putString(Settings.Secure.KG_FONT_TYPE, fontName).apply();

                setPreviewFont(fontName);
            }
        });

        LockscreenClockSettingsPreferenceFragment preferenceFragment = new LockscreenClockSettingsPreferenceFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.preferenceContainer, preferenceFragment)
                .commit();

        String selectedFont = sharedPreferences.getString(Settings.Secure.KG_FONT_TYPE, "sans-serif");

        for (int i = 0; i < customFonts.length; i++) {
            if (customFonts[i].equals(selectedFont)) {
                currentPosition = i;
                viewPager.setCurrentItem(currentPosition, false);
                break;
            }
        }
        
        Button applyButton = view.findViewById(R.id.applyButton);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonText;
                if (isViewPagerSwiped) {
                    buttonText = getString(R.string.qs_apply_change_button_title);
                } else {
                    buttonText = getString(R.string.swipe_the_clock);
                }
                applyButton.setText(buttonText);
                String selectedFont = customFonts[currentPosition];
                setPreviewFont(selectedFont);
                handler.removeCallbacks(updateSettingsProviderRunnable);
                handler.postDelayed(updateSettingsProviderRunnable, 500);
            }
        });
        
        setPreviewFont(customFonts[currentPosition]);
    }

    private void updateSettingsProvider(String fontName) {
        Settings.Secure.putStringForUser(resolver, Settings.Secure.KG_FONT_TYPE, fontName, UserHandle.USER_CURRENT);
    }

    private Runnable updateSettingsProviderRunnable = new Runnable() {
        @Override
        public void run() {
            String fontName = customFonts[currentPosition];
            updateSettingsProvider(fontName);
        }
    };

    private void setPreviewFont(String fontName) {
        RecyclerView recyclerView = (RecyclerView) viewPager.getChildAt(0);
        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(currentPosition);

        if (viewHolder instanceof LockScreenClockPickerPagerAdapter.FontViewHolder) {
            LockScreenClockPickerPagerAdapter.FontViewHolder fontViewHolder =
                    (LockScreenClockPickerPagerAdapter.FontViewHolder) viewHolder;
            fontViewHolder.setClockPreviewFont(fontName);
        }

        String buttonText;
        if (isViewPagerSwiped) {
            buttonText = getString(R.string.qs_apply_change_button_title);
        } else {
            buttonText = getString(R.string.swipe_the_clock);
        }
        Button applyButton = requireView().findViewById(R.id.applyButton);
        applyButton.setText(buttonText);
    }

    private static class LockScreenPageTransformer implements ViewPager2.PageTransformer {

    @Override
    public void transformPage(@NonNull View page, float position) {
        if (Math.abs(position) < 0.0001) {
            resetTransformations(page);
            return;
        }

        onPreTransform(page, position);
        onTransform(page, position);
    }

    protected void onPreTransform(View page, float position) {
        float width = (float) page.getWidth();
        page.setTranslationX(isPagingEnabled() ? 0.0f : (-width) * position);
        if (hideOffscreenPages()) {
            page.setAlpha(position > -1.0f && position < 1.0f ? 1.0f : 0.0f);
        } else {
            page.setAlpha(1.0f);
        }
    }

    public void onTransform(View view, float position) {
        if (position == 0) {
            view.setPivotX(0);
            view.setPivotY(view.getHeight() * 0.5f);
            view.setRotationY(0);
            view.setScaleX(1.0f);
            view.setScaleY(1.0f);
            return;
        }

        float f = 0.0f;
        if (position < 0.0f) {
            f = (float) view.getWidth();
        }
        view.setPivotX(f);
        view.setPivotY(((float) view.getHeight()) * 0.5f);
        view.setRotationY(20.0f * position);
        float normalizedPosition = Math.abs(Math.abs(position) - 1.0f);
        view.setScaleX((float) (((double) (normalizedPosition / 2.0f)) + 0.5d));
        view.setScaleY((float) (((double) (normalizedPosition / 2.0f)) + 0.5d));
    }

    protected static final float min(float val, float min) {
        return val < min ? min : val;
    }

    public boolean isPagingEnabled() {
        return true;
    }

    protected boolean hideOffscreenPages() {
        return true;
    }

    private void resetTransformations(View page) {
        page.setRotationX(0.0f);
        page.setRotationY(0.0f);
        page.setRotation(0.0f);
        page.setScaleX(1.0f);
        page.setScaleY(1.0f);
        page.setPivotX(0.0f);
        page.setPivotY(0.0f);
        page.setTranslationY(0.0f);
        page.setTranslationX(0.0f);
        page.setAlpha(1.0f);
    }
}
    public static class LockscreenClockSettingsPreferenceFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.spark_settings_lockscreen_clock, rootKey);
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            String title = getString(R.string.theme_customization_clock_font_title);
            getActivity().setTitle(title);
        }
    }
}
