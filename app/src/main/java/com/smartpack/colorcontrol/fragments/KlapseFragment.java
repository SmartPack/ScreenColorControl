/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of Screen Color Control, an app made to offer advanced control
 * over the screen colour of smart devices having KCAL/K-lapse support.
 *
 */

package com.smartpack.colorcontrol.fragments;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.text.InputType;

import com.smartpack.colorcontrol.R;
import com.smartpack.colorcontrol.utils.Klapse;
import com.smartpack.colorcontrol.utils.Utils;
import com.smartpack.colorcontrol.views.recyclerview.DescriptionView;
import com.smartpack.colorcontrol.views.recyclerview.GenericSelectView;
import com.smartpack.colorcontrol.views.recyclerview.RecyclerViewItem;
import com.smartpack.colorcontrol.views.recyclerview.SeekBarView;
import com.smartpack.colorcontrol.views.recyclerview.SelectView;
import com.smartpack.colorcontrol.views.recyclerview.SwitchView;
import com.smartpack.colorcontrol.views.recyclerview.TitleView;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 01, 2020
 */

public class KlapseFragment extends RecyclerViewFragment {

    private AsyncTask<Void, Void, List<RecyclerViewItem>> mLoader;

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (Klapse.supported()) {
            reload();
        } else {
            DescriptionView unsupported = new DescriptionView();
            unsupported.setDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
            unsupported.setSummary(getString(R.string.no_support, getString(R.string.klapse)));

            items.add(unsupported);
        }
    }

    private void reload() {
        if (mLoader == null) {
            getHandler().postDelayed(new Runnable() {
                @SuppressLint("StaticFieldLeak")
                @Override
                public void run() {
                    clearItems();
                    mLoader = new AsyncTask<Void, Void, List<RecyclerViewItem>>() {

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            showProgress();
                        }

                        @Override
                        protected List<RecyclerViewItem> doInBackground(Void... voids) {
                            List<RecyclerViewItem> items = new ArrayList<>();
                            klapseInit(items);
                            return items;
                        }

                        @Override
                        protected void onPostExecute(List<RecyclerViewItem> recyclerViewItems) {
                            super.onPostExecute(recyclerViewItems);
                            for (RecyclerViewItem item : recyclerViewItems) {
                                addItem(item);
                            }
                            hideProgress();
                            mLoader = null;
                        }
                    };
                    mLoader.execute();
                }
            }, 250);
        }
    }

    @Override
    protected void postInit() {
        super.postInit();

        addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.klapse),
                Utils.htmlFrom(getString(R.string.klapse_summary))));
    }

    private void klapseInit(List<RecyclerViewItem> items) {
        TitleView klapse = new TitleView();
        klapse.setText(Klapse.hasklapseVersion() ? getString(R.string.klapse) + " v" + Klapse.getklapseVersion() : getString(R.string.klapse));
        items.add(klapse);

        int nightR = Klapse.getklapseRed();
        int nightG = Klapse.getklapseGreen();
        int nightB = Klapse.getklapseBlue();

        if (Klapse.hasEnable()) {
            SelectView enable = new SelectView();
            enable.setSummary(getString(R.string.klapse_enable));
            enable.setItems(Klapse.enable());
            enable.setItem(Klapse.getklapseEnable());
            enable.setOnItemSelected((selectView, position, item) -> {
                Klapse.setklapseEnable(position);
                Klapse.setklapseRed((nightR));
                Klapse.setklapseGreen((nightG));
                Klapse.setklapseBlue((nightB));
                reload();
            });

            items.add(enable);
        }

        if (Klapse.getklapseEnable() == 1 && Klapse.hasklapseStart()) {
            int startTime = Utils.strToInt(Klapse.getklapseStartRaw());
            int startHr = startTime / 60;
            int startMin = startTime - (startHr * 60);

            DescriptionView klapseStart = new DescriptionView();
            klapseStart.setTitle(getString(R.string.night_mode_schedule));
            klapseStart.setSummary(getString(R.string.start_time) + ": " + Klapse.getklapseStart());
            klapseStart.setOnItemClickListener(item -> {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        (view, hourOfDay, minute) -> {
                            Klapse.setklapseStart((hourOfDay * 60) + minute);
                            getHandler().postDelayed(() -> {
                                        klapseStart.setSummary(getString(R.string.start_time) + ": " + Klapse.getklapseStart());
                                    },
                                    500);
                        }, startHr, startMin, false);
                timePickerDialog.show();
            });

            items.add(klapseStart);
        }

        if (Klapse.getklapseEnable() == 1 && Klapse.hasklapseStop()) {
            int EndTime = Utils.strToInt(Klapse.getklapseStopRaw());
            int EndHr = EndTime / 60;
            int EndMin = EndTime - (EndHr * 60);

            DescriptionView klapseStop = new DescriptionView();
            klapseStop.setSummary(getString(R.string.end_time) + ": " + Klapse.getklapseStop());
            klapseStop.setOnItemClickListener(item -> {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        (view, hourOfDay, minute) -> {
                            Klapse.setklapseStop((hourOfDay * 60) + minute);
                            getHandler().postDelayed(() -> {
                                        klapseStop.setSummary(getString(R.string.end_time) + ": " + Klapse.getklapseStop());
                                    },
                                    500);
                        }, EndHr, EndMin, false);
                timePickerDialog.show();
            });

            items.add(klapseStop);
        }

        if (Klapse.getklapseEnable() == 1 && Klapse.hasklapseRed()) {
            SeekBarView targetRed = new SeekBarView();
            targetRed.setTitle(getString(R.string.night_mode_rgb));
            targetRed.setSummary(getString(R.string.red));
            targetRed.setMax(256);
            targetRed.setProgress(Klapse.getklapseRed());
            targetRed.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Klapse.setklapseRed((position));
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            items.add(targetRed);
        }

        if (Klapse.getklapseEnable() == 1 && Klapse.hasklapseGreen()) {
            SeekBarView targetGreen = new SeekBarView();
            targetGreen.setSummary(getString(R.string.green));
            targetGreen.setMax(256);
            targetGreen.setProgress(Klapse.getklapseGreen());
            targetGreen.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Klapse.setklapseGreen((position));
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            items.add(targetGreen);
        }

        if (Klapse.getklapseEnable() == 1 && Klapse.hasklapseBlue()) {
            SeekBarView targetBlue = new SeekBarView();
            targetBlue.setSummary(getString(R.string.blue));
            targetBlue.setMax(256);
            targetBlue.setProgress(Klapse.getklapseBlue());
            targetBlue.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Klapse.setklapseBlue((position));
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            items.add(targetBlue);
        }

        if (Klapse.getklapseEnable() == 1 && Klapse.hasScalingRate()) {
            GenericSelectView scalingRate = new GenericSelectView();
            scalingRate.setTitle(getString(R.string.scaling_rate));
            scalingRate.setSummary(getString(R.string.scaling_rate_summary));
            scalingRate.setValue(Klapse.getScalingRate());
            scalingRate.setInputType(InputType.TYPE_CLASS_NUMBER);
            scalingRate.setOnGenericValueListener((genericSelectView, value) -> {
                Klapse.setScalingRate(value);
                genericSelectView.setValue(value);
            });

            items.add(scalingRate);
        }

        if (Klapse.getklapseEnable() == 1 && Klapse.hasFadeBackMinutes()) {
            GenericSelectView fadebackMinutes = new GenericSelectView();
            fadebackMinutes.setTitle(getString(R.string.fadeback_time));
            fadebackMinutes.setSummary(getString(R.string.fadeback_time_summary));
            fadebackMinutes.setValue(Klapse.getFadeBackMinutes());
            fadebackMinutes.setInputType(InputType.TYPE_CLASS_NUMBER);
            fadebackMinutes.setOnGenericValueListener((genericSelectView, value) -> {
                Klapse.setFadeBackMinutes(value);
                genericSelectView.setValue(value);
            });

            items.add(fadebackMinutes);
        }

        if (Klapse.getklapseEnable() == 1 && Klapse.hasDayTimeRed()) {
            SeekBarView dayTimeRed = new SeekBarView();
            dayTimeRed.setTitle(getString(R.string.daytime_rgb));
            dayTimeRed.setSummary(getString(R.string.red));
            dayTimeRed.setMax(256);
            dayTimeRed.setProgress(Klapse.getDayTimeRed());
            dayTimeRed.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Klapse.setDayTimeRed((position));
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            items.add(dayTimeRed);
        }

        if (Klapse.getklapseEnable() == 1 && Klapse.hasDayTimeGreen()) {
            SeekBarView dayTimeGreen = new SeekBarView();
            dayTimeGreen.setSummary(getString(R.string.green));
            dayTimeGreen.setMax(256);
            dayTimeGreen.setProgress(Klapse.getDayTimeGreen());
            dayTimeGreen.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Klapse.setDayTimeGreen((position));
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            items.add(dayTimeGreen);
        }

        if (Klapse.getklapseEnable() == 1 && Klapse.hasDayTimeBlue()) {
            SeekBarView dayTimeBlue = new SeekBarView();
            dayTimeBlue.setSummary(getString(R.string.blue));
            dayTimeBlue.setMax(256);
            dayTimeBlue.setProgress(Klapse.getDayTimeBlue());
            dayTimeBlue.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Klapse.setDayTimeBlue((position));
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            items.add(dayTimeBlue);
        }

        if (Klapse.hasPulseFreq()) {
            GenericSelectView pulseFreq = new GenericSelectView();
            pulseFreq.setTitle(getString(R.string.pulse_freq));
            pulseFreq.setSummary(getString(R.string.pulse_freq_summary));
            pulseFreq.setValue(Klapse.getPulseFreq());
            pulseFreq.setInputType(InputType.TYPE_CLASS_NUMBER);
            pulseFreq.setOnGenericValueListener((genericSelectView, value) -> {
                Klapse.setPulseFreq(value);
                genericSelectView.setValue(value);
            });

            items.add(pulseFreq);
        }

        if (Klapse.hasFlowFreq()) {
            GenericSelectView flowFreq = new GenericSelectView();
            flowFreq.setTitle(getString(R.string.flow_freq));
            flowFreq.setSummary(getString(R.string.flow_freq_summary));
            flowFreq.setValue(Klapse.getFlowFreq());
            flowFreq.setInputType(InputType.TYPE_CLASS_NUMBER);
            flowFreq.setOnGenericValueListener((genericSelectView, value) -> {
                Klapse.setFlowFreq(value);
                genericSelectView.setValue(value);
            });

            items.add(flowFreq);
        }

        if (Klapse.getklapseEnable() == 2 && Klapse.hasBLRangeLower()) {
            GenericSelectView backlightRange = new GenericSelectView();
            backlightRange.setTitle(getString(R.string.backlight_range));
            backlightRange.setSummary("Min");
            backlightRange.setValue(Klapse.getBLRangeLower());
            backlightRange.setInputType(InputType.TYPE_CLASS_NUMBER);
            backlightRange.setOnGenericValueListener((genericSelectView, value) -> {
                Klapse.setBLRangeLower(value);
                genericSelectView.setValue(value);
            });

            items.add(backlightRange);
        }

        if (Klapse.getklapseEnable() == 2 && Klapse.hasBLRangeUpper()) {
            GenericSelectView backlightRange = new GenericSelectView();
            backlightRange.setSummary("Max");
            backlightRange.setValue(Klapse.getBLRangeUpper());
            backlightRange.setInputType(InputType.TYPE_CLASS_NUMBER);
            backlightRange.setOnGenericValueListener((genericSelectView, value) -> {
                Klapse.setBLRangeUpper(value);
                genericSelectView.setValue(value);
            });

            items.add(backlightRange);
        }

        TitleView dimmer = new TitleView();
        dimmer.setText(getString(R.string.dimming));
        items.add(dimmer);

        if (Klapse.hasDimmerFactor()) {
            SeekBarView Dimmer = new SeekBarView();
            Dimmer.setSummary(getString(R.string.dimming_summary));
            Dimmer.setMax(100);
            Dimmer.setMin(10);
            Dimmer.setUnit(" %");
            Dimmer.setProgress(Klapse.getBrightnessFactor() - 10);
            Dimmer.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Klapse.setBrightnessFactor(position + 10);
                }
            });

            items.add(Dimmer);
        }

        DescriptionView brightFactStart = new DescriptionView();
        DescriptionView brightFactStop = new DescriptionView();

        if (Klapse.hasAutoBrightnessFactor()) {
            SwitchView autoBrightness = new SwitchView();
            autoBrightness.setTitle(getString(R.string.auto_dimming));
            autoBrightness.setSummary(getString(R.string.auto_dimming_summary));
            autoBrightness.setChecked(Klapse.isAutoBrightnessFactorEnabled());
            autoBrightness.addOnSwitchListener((switchView, isChecked) -> {
                Klapse.enableAutoBrightnessFactor(isChecked);
                reload();
            });

            items.add(autoBrightness);
        }

        if (Klapse.isAutoBrightnessFactorEnabled() && Klapse.hasDimmerStart()) {
            int startTime = Utils.strToInt(Klapse.getBrightFactStartRaw());
            int startHr = startTime / 60;
            int startMin = startTime - (startHr * 60);

            brightFactStart.setTitle(getString(R.string.auto_dimming_schedule));
            brightFactStart.setSummary(getString(R.string.start_time) + ": " + Klapse.getBrightFactStart());
            brightFactStart.setOnItemClickListener(item -> {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        (view, hourOfDay, minute) -> {
                            Klapse.setBrightFactStart((hourOfDay * 60) + minute);
                            getHandler().postDelayed(() -> {
                                        brightFactStart.setSummary(getString(R.string.start_time) + ": " + Klapse.getBrightFactStart());
                                    },
                                    500);
                        }, startHr, startMin, false);
                timePickerDialog.show();
            });
            items.add(brightFactStart);
        }

        if (Klapse.isAutoBrightnessFactorEnabled() && Klapse.hasDimmerStop()) {
            int EndTime = Utils.strToInt(Klapse.getBrightFactStopRaw());
            int EndHr = EndTime / 60;
            int EndMin = EndTime - (EndHr * 60);

            brightFactStop.setSummary(getString(R.string.end_time) + ": " + Klapse.getBrightFactStop());
            brightFactStop.setOnItemClickListener(item -> {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        (view, hourOfDay, minute) -> {
                            Klapse.setBrightFactStop((hourOfDay * 60) + minute);
                            getHandler().postDelayed(() -> {
                                        brightFactStop.setSummary(getString(R.string.end_time) + ": " + Klapse.getBrightFactStop());
                                    },
                                    500);
                        }, EndHr, EndMin, false);
                timePickerDialog.show();
            });
            items.add(brightFactStop);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoader != null) {
            mLoader.cancel(true);
        }
    }

}