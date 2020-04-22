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
import com.smartpack.colorcontrol.utils.Klapse;
import com.smartpack.colorcontrol.utils.ScreenColor;
import com.smartpack.colorcontrol.utils.Utils;
import com.smartpack.colorcontrol.views.recyclerview.DescriptionView;
import com.smartpack.colorcontrol.views.recyclerview.ImageView;
import com.smartpack.colorcontrol.views.recyclerview.RecyclerViewItem;
import com.smartpack.colorcontrol.views.recyclerview.SeekBarView;
import com.smartpack.colorcontrol.views.recyclerview.SwitchView;
import com.smartpack.colorcontrol.views.recyclerview.TitleView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 01, 2020
 */

public class ScreenColorFragment extends RecyclerViewFragment {

    private ScreenColor mScreenColor;

    @Override
    protected void init() {
        super.init();

        mScreenColor = ScreenColor.getInstance();

        addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.screen_color),
                getString(R.string.screen_color_summary)));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (ScreenColor.getInstance().supported() || ScreenColor.getInstance().hasSRGB() || DRMColor.supported()) {
            ColorInit(items);
        } else {
            DescriptionView unsupported = new DescriptionView();
            unsupported.setDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
            unsupported.setSummary(getString(R.string.no_support, "KCAL"));

            items.add(unsupported);
        }
    }

    private void ColorInit(List<RecyclerViewItem> items) {
        TitleView kcalTitle = new TitleView();
        kcalTitle.setText(getString(R.string.screen_color));
        items.add(kcalTitle);

        ImageView color = new ImageView();
        color.setDrawable(getResources().getDrawable(R.drawable.ic_calibration));
        color.setLayoutParams(750, 520);

        items.add(color);

        int localTimeHr = Calendar.getInstance(Locale.getDefault()).get(Calendar.HOUR_OF_DAY);
        int localTimeMin = Calendar.getInstance(Locale.getDefault()).get(Calendar.MINUTE);
        int localTime = (localTimeHr * 60 ) + localTimeMin;
        int klapseStartTime = Utils.strToInt(Klapse.getklapseStartRaw());
        if (mScreenColor.hasColors()) {
            List<String> colors = mScreenColor.getColors();
            final List<String> limits = mScreenColor.getLimits();
            final SeekBarView mMinColor = new SeekBarView();
            final SeekBarView[] colorViews = new SeekBarView[colors.size()];
            for (int i = 0; i < colors.size(); i++) {
                colorViews[i] = new SeekBarView();
                colorViews[i].setSummary(getResources().getStringArray(R.array.colors)[i]);
                colorViews[i].setItems(limits);
                colorViews[i].setProgress(limits.indexOf(colors.get(i)));
                colorViews[i].setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                    @Override
                    public void onMove(SeekBarView seekBarView, int position, String value) {
                        if (position < mMinColor.getProgress()) {
                            mMinColor.setProgress(position);
                        }
                    }

                    @Override
                    public void onStop(SeekBarView seekBarView, int position, String value) {
                        int current = Utils.strToInt(mScreenColor.getLimits().get(position));
                        if (mScreenColor.getMinColor() > current) {
                            mScreenColor.setMinColor(current);
                        }

                        // TODO: Avoid hardcoding index
                        int r = colorViews[0].getProgress();
                        int g = colorViews[1].getProgress();
                        int b = colorViews[2].getProgress();
                        mScreenColor.setColors(limits.get(r) + " " + limits.get(g) + " " + limits.get(b));
                        if (Klapse.supported() && Klapse.getklapseEnable() == 1 && localTime >= klapseStartTime) {
                            Klapse.setklapseRed(Utils.strToInt(limits.get(r)));
                            Klapse.setklapseGreen(Utils.strToInt(limits.get(g)));
                            Klapse.setklapseBlue(Utils.strToInt(limits.get(b)));
                            if (localTime < (klapseStartTime + Utils.strToInt(Klapse.getScalingRate()))) {
                                Utils.toast(getString(R.string.time_mode_warning), getActivity());
                            }
                        } else {
                            Klapse.setDayTimeRed(Utils.strToInt(limits.get(r)));
                            Klapse.setDayTimeGreen(Utils.strToInt(limits.get(g)));
                            Klapse.setDayTimeBlue(Utils.strToInt(limits.get(b)));
                        }
                    }
                });

                items.add(colorViews[i]);
            }
        } else if (DRMColor.haskcalRed() || DRMColor.haskcalGreen() || DRMColor.haskcalBlue()) {
            if (DRMColor.haskcalRed()) {
                SeekBarView kcal = new SeekBarView();
                kcal.setTitle(getString(R.string.red));
                kcal.setMax(256);
                kcal.setProgress(DRMColor.getkcalRed());
                kcal.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                    @Override
                    public void onStop(SeekBarView seekBarView, int position, String value) {
                        DRMColor.setkcalRed((position));
                        if (Klapse.supported() && Klapse.getklapseEnable() == 1 && localTime >= klapseStartTime) {
                            Klapse.setklapseRed(position);
                            if (localTime < (klapseStartTime + Utils.strToInt(Klapse.getScalingRate()))) {
                                Utils.toast(getString(R.string.time_mode_warning), getActivity());
                            }
                        } else {
                            Klapse.setDayTimeRed(position);
                        }
                    }

                    @Override
                    public void onMove(SeekBarView seekBarView, int position, String value) {
                    }
                });

                items.add(kcal);
            }

            if (DRMColor.haskcalGreen()) {
                SeekBarView kcal = new SeekBarView();
                kcal.setTitle(getString(R.string.green));
                kcal.setMax(256);
                kcal.setProgress(DRMColor.getkcalGreen());
                kcal.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                    @Override
                    public void onStop(SeekBarView seekBarView, int position, String value) {
                        DRMColor.setkcalGreen((position));
                        if (Klapse.supported() && Klapse.getklapseEnable() == 1 && localTime >= klapseStartTime) {
                            Klapse.setklapseGreen(position);
                            if (localTime < (klapseStartTime + Utils.strToInt(Klapse.getScalingRate()))) {
                                Utils.toast(getString(R.string.time_mode_warning), getActivity());
                            }
                        } else {
                            Klapse.setDayTimeGreen(position);
                        }
                    }

                    @Override
                    public void onMove(SeekBarView seekBarView, int position, String value) {
                    }
                });

                items.add(kcal);
            }

            if (DRMColor.haskcalBlue()) {
                SeekBarView kcal = new SeekBarView();
                kcal.setTitle(getString(R.string.blue));
                kcal.setMax(256);
                kcal.setProgress(DRMColor.getkcalBlue());
                kcal.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                    @Override
                    public void onStop(SeekBarView seekBarView, int position, String value) {
                        DRMColor.setkcalBlue((position));
                        if (Klapse.supported() && Klapse.getklapseEnable() == 1 && localTime >= klapseStartTime) {
                            Klapse.setklapseBlue(position);
                            if (localTime < (klapseStartTime + Utils.strToInt(Klapse.getScalingRate()))) {
                                Utils.toast(getString(R.string.time_mode_warning), getActivity());
                            }
                        } else {
                            Klapse.setDayTimeBlue(position);
                        }
                    }

                    @Override
                    public void onMove(SeekBarView seekBarView, int position, String value) {
                    }
                });

                items.add(kcal);
            }
        }

        if (mScreenColor.hasSaturationIntensity() || DRMColor.hasScreenSat()) {
            SwitchView grayscaleMode = new SwitchView();
            grayscaleMode.setSummary(getString(R.string.grayscale_mode));
            if (DRMColor.hasScreenSat()) {
            grayscaleMode.setChecked(DRMColor.isGrayscaleModeEnabled());
                grayscaleMode.addOnSwitchListener((switchView, isChecked)
                        -> DRMColor.enableGrayscaleMode(isChecked));
            } else {
                grayscaleMode.setChecked(mScreenColor.isGrayscaleModeEnabled());
                grayscaleMode.addOnSwitchListener((switchView, isChecked)
                        -> mScreenColor.enableGrayscaleMode(isChecked));
            }

            items.add(grayscaleMode);
        }

        if (mScreenColor.hasScreenHBM()) {
            SwitchView screenHBM = new SwitchView();
            screenHBM.setSummary(getString(R.string.high_brightness_mode));
            screenHBM.setChecked(mScreenColor.isScreenHBMEnabled());
            screenHBM.addOnSwitchListener((switchView, isChecked)
                    -> mScreenColor.enableScreenHBM(isChecked));

            items.add(screenHBM);
        }

        if (mScreenColor.hasInvertScreen() || DRMColor.hasInvertScreen()) {
            SwitchView invertScreen = new SwitchView();
            invertScreen.setSummary(getString(R.string.invert_screen));
            if (DRMColor.hasInvertScreen()) {
                invertScreen.setChecked(DRMColor.isInvertScreenEnabled());
                invertScreen.addOnSwitchListener((switchView, isChecked)
                        -> DRMColor.enableInvertScreen(isChecked));
            } else {
                invertScreen.setChecked(mScreenColor.isInvertScreenEnabled());
                invertScreen.addOnSwitchListener((switchView, isChecked)
                        -> mScreenColor.enableInvertScreen(isChecked));
            }

            items.add(invertScreen);
        }

        if (mScreenColor.hasSRGB()) {
            SwitchView sRGB = new SwitchView();
            sRGB.setSummary(getString(R.string.srgb));
            sRGB.setChecked(mScreenColor.isSRGBEnabled());
            sRGB.addOnSwitchListener((switchView, isChecked)
                    -> mScreenColor.enableSRGB(isChecked));

            items.add(sRGB);
        }
    }

}