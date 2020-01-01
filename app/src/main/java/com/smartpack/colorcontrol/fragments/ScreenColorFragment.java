/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of Screen Color Control, an app made to offer advanced control
 * over the screen colour of smart devices having KCAL/K-lapse support.
 *
 */

package com.smartpack.colorcontrol.fragments;

import android.view.View;
import android.widget.CheckBox;

import com.smartpack.colorcontrol.R;
import com.smartpack.colorcontrol.utils.DRMColor;
import com.smartpack.colorcontrol.utils.Klapse;
import com.smartpack.colorcontrol.utils.Prefs;
import com.smartpack.colorcontrol.utils.Profile;
import com.smartpack.colorcontrol.utils.ScreenColor;
import com.smartpack.colorcontrol.utils.Utils;
import com.smartpack.colorcontrol.views.dialog.Dialog;
import com.smartpack.colorcontrol.views.recyclerview.CardView;
import com.smartpack.colorcontrol.views.recyclerview.DescriptionView;
import com.smartpack.colorcontrol.views.recyclerview.ImageView;
import com.smartpack.colorcontrol.views.recyclerview.RecyclerViewItem;
import com.smartpack.colorcontrol.views.recyclerview.SeekBarView;
import com.smartpack.colorcontrol.views.recyclerview.SwitchView;

import java.util.List;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 01, 2020
 */

public class ScreenColorFragment extends RecyclerViewFragment {

    private boolean mWelcomeDialog = true;

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
            unsupported.setDrawable(getResources().getDrawable(R.drawable.ic_about));
            unsupported.setTitle(getString(R.string.no_support, "KCAL"));

            items.add(unsupported);
        }
    }

    private void ColorInit(List<RecyclerViewItem> items) {
        CardView kcalCard = new CardView(getActivity());
        kcalCard.setTitle(getString(R.string.screen_color));

        ImageView color = new ImageView();
        color.setDrawable(getResources().getDrawable(R.drawable.ic_calibration));
        color.setLayoutParams(750, 520);

        kcalCard.addItem(color);

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
                        if (mMinColor != null && position < mMinColor.getProgress()) {
                            mMinColor.setProgress(position);
                        }
                    }

                    @Override
                    public void onStop(SeekBarView seekBarView, int position, String value) {
                        if (mMinColor != null) {
                            int current = Utils.strToInt(mScreenColor.getLimits().get(position));
                            if (mScreenColor.getMinColor() > current) {
                                mScreenColor.setMinColor(current);
                            }
                        }

                        // TODO: Avoid hardcoding index
                        int r = colorViews[0].getProgress();
                        int g = colorViews[1].getProgress();
                        int b = colorViews[2].getProgress();
                        mScreenColor.setColors(limits.get(r) + " " + limits.get(g) + " " + limits.get(b));
                    }
                });

                kcalCard.addItem(colorViews[i]);
            }
        }

        if (DRMColor.haskcalRed()) {
            SeekBarView kcal = new SeekBarView();
            kcal.setTitle(getString(R.string.red));
            kcal.setMax(256);
            kcal.setProgress(DRMColor.getkcalRed());
            kcal.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    DRMColor.setkcalRed((position));
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            kcalCard.addItem(kcal);
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
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            kcalCard.addItem(kcal);
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
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            kcalCard.addItem(kcal);
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

            kcalCard.addItem(grayscaleMode);
        }

        if (mScreenColor.hasScreenHBM()) {
            SwitchView screenHBM = new SwitchView();
            screenHBM.setSummary(getString(R.string.high_brightness_mode));
            screenHBM.setChecked(mScreenColor.isScreenHBMEnabled());
            screenHBM.addOnSwitchListener((switchView, isChecked)
                    -> mScreenColor.enableScreenHBM(isChecked));

            kcalCard.addItem(screenHBM);
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

            kcalCard.addItem(invertScreen);
        }

        if (mScreenColor.hasSRGB()) {
            SwitchView sRGB = new SwitchView();
            sRGB.setSummary(getString(R.string.srgb));
            sRGB.setChecked(mScreenColor.isSRGBEnabled());
            sRGB.addOnSwitchListener((switchView, isChecked)
                    -> mScreenColor.enableSRGB(isChecked));

            kcalCard.addItem(sRGB);
        }

        if (kcalCard.size() > 0) {
            items.add(kcalCard);
        }
    }

    /*
     * Taken and used almost as such from https://github.com/morogoku/MTweaks-KernelAdiutorMOD/
     * Ref: https://github.com/morogoku/MTweaks-KernelAdiutorMOD/blob/dd5a4c3242d5e1697d55c4cc6412a9b76c8b8e2e/app/src/main/java/com/moro/mtweaks/fragments/kernel/BoefflaWakelockFragment.java#L133
     */
    private void WelcomeDialog() {
        View checkBoxView = View.inflate(getActivity(), R.layout.rv_checkbox, null);
        CheckBox checkBox = checkBoxView.findViewById(R.id.checkbox);
        checkBox.setChecked(true);
        checkBox.setText(getString(R.string.always_show));
        checkBox.setOnCheckedChangeListener((buttonView, isChecked)
                -> mWelcomeDialog = isChecked);

        Dialog alert = new Dialog(Objects.requireNonNull(getActivity()));
        alert.setIcon(R.mipmap.ic_launcher);
        alert.setTitle(getString(R.string.app_name));
        if (!Klapse.supported() && !ScreenColor.getInstance().supported()
                && !DRMColor.supported()) {
            alert.setMessage(getString(R.string.no_support, "KCAL/K-lapse"));
        } else {
            alert.setMessage(getString(R.string.welcome_message, Profile.ProfileFile().toString()));
        }
        alert.setCancelable(false);
        alert.setView(checkBoxView);
        alert.setNeutralButton(getString(R.string.cancel), (dialog, id) -> {
        });
        alert.setPositiveButton(getString(R.string.got_it), (dialog, id)
                -> Prefs.saveBoolean("welcomeMessage", mWelcomeDialog, getActivity()));

        alert.show();
    }

    @Override
    public void onStart(){
        super.onStart();

        boolean showDialog = Prefs.getBoolean("welcomeMessage", true, getActivity());
        if (showDialog) {
            WelcomeDialog();
        }
    }
    
}