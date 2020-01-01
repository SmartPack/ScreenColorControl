/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of Screen Color Control, an app made to offer advanced control
 * over the screen colour of smart devices having KCAL/K-lapse support.
 *
 */

package com.smartpack.colorcontrol.fragments;

import com.smartpack.colorcontrol.R;
import com.smartpack.colorcontrol.utils.DRMColor;
import com.smartpack.colorcontrol.utils.ScreenColor;
import com.smartpack.colorcontrol.views.recyclerview.CardView;
import com.smartpack.colorcontrol.views.recyclerview.DescriptionView;
import com.smartpack.colorcontrol.views.recyclerview.RecyclerViewItem;
import com.smartpack.colorcontrol.views.recyclerview.SeekBarView;
import com.smartpack.colorcontrol.views.recyclerview.SwitchView;

import java.util.List;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 01, 2020
 */

public class OtherSettingsFragment extends RecyclerViewFragment {

    private ScreenColor mScreenColor;

    @Override
    protected void init() {
        super.init();

        mScreenColor = ScreenColor.getInstance();
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (ScreenColor.getInstance().supported() || DRMColor.supported()) {
            otherItemsInit(items);
        } else {
            DescriptionView unsupported = new DescriptionView();
            unsupported.setDrawable(getResources().getDrawable(R.drawable.ic_about));
            unsupported.setSummary(getString(R.string.no_support, "KCAL"));

            items.add(unsupported);
        }
    }

    @Override
    protected void postInit() {
        super.postInit();

        addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.other_settings),
                getString(R.string.other_settings_summary)));
    }

    private void otherItemsInit(List<RecyclerViewItem> items) {
        CardView otherSettingsCard = new CardView(getActivity());
        otherSettingsCard.setTitle(getString(R.string.other_settings));

        if (mScreenColor.hasScreenHue() || DRMColor.hasScreenHue()) {
            SeekBarView screenHue = new SeekBarView();
            screenHue.setSummary(getString(R.string.screen_hue));
            screenHue.setMax(1536);
            screenHue.setProgress(DRMColor.hasScreenHue() ? DRMColor.getScreenHue() :
                    mScreenColor.getScreenHue());
            screenHue.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    if (DRMColor.hasScreenHue()) {
                        DRMColor.setScreenHue(position);
                    } else {
                        mScreenColor.setScreenHue(position);
                    }
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            items.add(screenHue);
        }

        if (mScreenColor.hasScreenValue() || DRMColor.hasScreenVal()) {
            SeekBarView screenValue = new SeekBarView();
            screenValue.setSummary(getString(R.string.screen_value));
            screenValue.setMax(255);
            screenValue.setProgress(DRMColor.hasScreenVal() ? (DRMColor.getScreenVal() - 128) :
                    (mScreenColor.getScreenValue() - 128));
            screenValue.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    if (DRMColor.hasScreenVal()) {
                        DRMColor.setScreenVal(position + 128);
                    } else {
                        mScreenColor.setScreenValue(position + 128);
                    }
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            items.add(screenValue);
        }

        if (mScreenColor.hasScreenContrast() || DRMColor.hasScreenCont()) {
            SeekBarView screenContrast = new SeekBarView();
            screenContrast.setSummary(getString(R.string.screen_contrast));
            screenContrast.setMax(255);
            screenContrast.setProgress(DRMColor.hasScreenCont() ? (DRMColor.getScreenCont() - 128) :
                    (mScreenColor.getScreenContrast() - 128));
            screenContrast.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    if (DRMColor.hasScreenCont()) {
                        DRMColor.setScreenCont(position + 128);
                    } else {
                        mScreenColor.setScreenContrast(position + 128);
                    }
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            items.add(screenContrast);
        }

        if (mScreenColor.hasSaturationIntensity() || DRMColor.hasScreenSat()) {
            SeekBarView saturationIntensity = new SeekBarView();
            saturationIntensity.setSummary(getString(R.string.saturation_intensity));
            saturationIntensity.setMax(158);
            saturationIntensity.setProgress(DRMColor.hasScreenSat() ? (DRMColor.getScreenSat() == 128 ? 30 :
                    DRMColor.getScreenSat() - 225) : (mScreenColor.getSaturationIntensity() == 128 ? 30 :
                    mScreenColor.getSaturationIntensity() - 225));
            saturationIntensity.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    if (DRMColor.hasScreenSat()) {
                        DRMColor.setScreenSat(position + 225);
                    } else {
                        mScreenColor.setSaturationIntensity(position + 225);
                    }
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            items.add(saturationIntensity);
        }

        if (otherSettingsCard.size() > 0) {
            items.add(otherSettingsCard);
        }
    }

}