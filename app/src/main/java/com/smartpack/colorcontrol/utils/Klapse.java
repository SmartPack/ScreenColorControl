/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of Screen Color Control, an app made to offer advanced control
 * over the screen colour of smart devices having KCAL/K-lapse support.
 *
 */

package com.smartpack.colorcontrol.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 01, 2020
 */

public class Klapse {

    private static final String KLAPSE = "/sys/module/klapse/parameters";
    private static final String KLAPSE_ENABLE = KLAPSE + "/enabled_mode";
    private static final String KLAPSE_TARGET_R = KLAPSE + "/target_r";
    private static final String KLAPSE_TARGET_G = KLAPSE + "/target_g";
    private static final String KLAPSE_TARGET_B = KLAPSE + "/target_b";
    private static final String DAYTIME_R = KLAPSE + "/daytime_r";
    private static final String DAYTIME_G = KLAPSE + "/daytime_g";
    private static final String DAYTIME_B = KLAPSE + "/daytime_b";
    private static final String KLAPSE_START_MIN = KLAPSE + "/start_minute";
    private static final String KLAPSE_END_MIN = KLAPSE + "/stop_minute";
    private static final String KLAPSE_TARGET_MIN = KLAPSE + "/target_minutes";
    private static final String FADEBACK_MINUTES = KLAPSE + "/fadeback_minutes";
    private static final String DIMMER_FACTOR = KLAPSE + "/dimmer_factor";
    private static final String DIMMER_FACTOR_AUTO = KLAPSE + "/dimmer_factor_auto";
    private static final String DIMMER_START = KLAPSE + "/dimmer_auto_start_minute";
    private static final String DIMMER_END = KLAPSE + "/dimmer_auto_stop_minute";
    private static final String PULSE_FREQ = KLAPSE + "/pulse_freq";
    private static final String FLOW_FREQ = KLAPSE + "/flow_freq";
    private static final String BACKLIGHT_RANGE_UPPER = KLAPSE + "/bl_range_upper";
    private static final String BACKLIGHT_RANGE_LOWER = KLAPSE + "/bl_range_lower";
    private static final String KLAPSE_VERSION = "/sys/module/klapse/version";

    public static boolean hasEnable() {
        return Utils.existFile(KLAPSE_ENABLE);
    }

    public static List<String> enable() {
        List<String> list = new ArrayList<>();
        list.add("Turned-Off");
        list.add("Time Mode");
        list.add("Brightness Mode");
        return list;
    }

    public static int getklapseEnable() {
        return Utils.strToInt(Utils.readFile(KLAPSE_ENABLE));
    }

    public static void setklapseEnable(int value) {
        Utils.applyValue(String.valueOf(value), KLAPSE_ENABLE);
    }

    public static void setklapseStart(int value) {
        Utils.applyValue(String.valueOf(value), KLAPSE_START_MIN);
    }

    public static String getklapseStartRaw() {
        return Utils.readFile(KLAPSE_START_MIN);
    }

    public static String getklapseStart() {
        return getAdjustedTime(getklapseStartRaw());
    }

    public static boolean hasklapseStart() {
        return Utils.existFile(KLAPSE_START_MIN);
    }

    public static void setklapseStop(int value) {
        Utils.applyValue(String.valueOf(value), KLAPSE_END_MIN);
    }

    public static String getklapseStopRaw() {
        return Utils.readFile(KLAPSE_END_MIN);
    }

    public static String getklapseStop() {
        return getAdjustedTime(getklapseStopRaw());
    }

    public static boolean hasklapseStop() {
        return Utils.existFile(KLAPSE_END_MIN);
    }

    public static void setScalingRate(String value) {
        Utils.applyValue(String.valueOf(value), KLAPSE_TARGET_MIN);
    }

    public static String getScalingRate() {
        return Utils.readFile(KLAPSE_TARGET_MIN);
    }

    public static boolean hasScalingRate() {
        return Utils.existFile(KLAPSE_TARGET_MIN);
    }

    public static void setFadeBackMinutes(String value) {
        Utils.applyValue(String.valueOf(value), FADEBACK_MINUTES);
    }

    public static String getFadeBackMinutes() {
        return Utils.readFile(FADEBACK_MINUTES);
    }

    public static boolean hasFadeBackMinutes() {
        return Utils.existFile(FADEBACK_MINUTES);
    }

    public static void setklapseRed(int value) {
        Utils.applyValue(String.valueOf(value), KLAPSE_TARGET_R);
    }

    public static int getklapseRed() {
        return Utils.strToInt(Utils.readFile(KLAPSE_TARGET_R));
    }

    public static boolean hasklapseRed() {
        return Utils.existFile(KLAPSE_TARGET_R);
    }

    public static void setklapseGreen(int value) {
        Utils.applyValue(String.valueOf(value), KLAPSE_TARGET_G);
    }

    public static int getklapseGreen() {
        return Utils.strToInt(Utils.readFile(KLAPSE_TARGET_G));
    }

    public static boolean hasklapseGreen() {
        return Utils.existFile(KLAPSE_TARGET_G);
    }

    public static void setklapseBlue(int value) {
        Utils.applyValue(String.valueOf(value), KLAPSE_TARGET_B);
    }

    public static int getklapseBlue() {
        return Utils.strToInt(Utils.readFile(KLAPSE_TARGET_B));
    }

    public static boolean hasklapseBlue() {
        return Utils.existFile(KLAPSE_TARGET_B);
    }

    public static void setDayTimeRed(int value) {
        Utils.applyValue(String.valueOf(value), DAYTIME_R);
    }

    public static int getDayTimeRed() {
        return Utils.strToInt(Utils.readFile(DAYTIME_R));
    }

    public static boolean hasDayTimeRed() {
        return Utils.existFile(DAYTIME_R);
    }

    public static void setDayTimeGreen(int value) {
        Utils.applyValue(String.valueOf(value), DAYTIME_G);
    }

    public static int getDayTimeGreen() {
        return Utils.strToInt(Utils.readFile(DAYTIME_G));
    }

    public static boolean hasDayTimeGreen() {
        return Utils.existFile(DAYTIME_G);
    }

    public static void setDayTimeBlue(int value) {
        Utils.applyValue(String.valueOf(value), DAYTIME_B);
    }

    public static int getDayTimeBlue() {
        return Utils.strToInt(Utils.readFile(DAYTIME_B));
    }

    public static boolean hasDayTimeBlue() {
        return Utils.existFile(DAYTIME_B);
    }

    public static void setBrightnessFactor(int value) {
        Utils.applyValue(String.valueOf(value), DIMMER_FACTOR);
    }

    public static int getBrightnessFactor() {
        return Utils.strToInt(Utils.readFile(DIMMER_FACTOR));
    }

    public static boolean hasDimmerFactor() {
        return Utils.existFile(DIMMER_FACTOR);
    }

    public static void enableAutoBrightnessFactor(boolean enable) {
        Utils.applyValue(enable ? "1" : "0", DIMMER_FACTOR_AUTO);
    }

    public static boolean isAutoBrightnessFactorEnabled() {
        return Utils.readFile(DIMMER_FACTOR_AUTO).startsWith("1")
                || Utils.readFile(DIMMER_FACTOR_AUTO).startsWith("Y");
    }

    public static boolean hasAutoBrightnessFactor() {
        return Utils.existFile(DIMMER_FACTOR_AUTO);
    }

    public static void setBrightFactStart(int value) {
        Utils.applyValue(String.valueOf(value), DIMMER_START);
    }

    public static String getBrightFactStartRaw() {
        return Utils.readFile(DIMMER_START);
    }

    public static String getBrightFactStart() {
        return getAdjustedTime(getBrightFactStartRaw());
    }

    public static boolean hasDimmerStart() {
        return Utils.existFile(DIMMER_START);
    }

    public static void setBrightFactStop(int value) {
        Utils.applyValue(String.valueOf(value), DIMMER_END);
    }

    public static String getBrightFactStopRaw() {
        return Utils.readFile(DIMMER_END);
    }

    public static String getBrightFactStop() {
        return getAdjustedTime(getBrightFactStopRaw());
    }

    public static boolean hasDimmerStop() {
        return Utils.existFile(DIMMER_END);
    }

    public static void setBLRangeUpper(String value) {
        Utils.applyValue(String.valueOf(value), BACKLIGHT_RANGE_UPPER);
    }

    public static String getBLRangeUpper() {
        return Utils.readFile(BACKLIGHT_RANGE_UPPER);
    }

    public static boolean hasBLRangeUpper() {
        return Utils.existFile(BACKLIGHT_RANGE_UPPER);
    }

    public static void setBLRangeLower(String value) {
        Utils.applyValue(String.valueOf(value), BACKLIGHT_RANGE_LOWER);
    }

    public static String getBLRangeLower() {
        return Utils.readFile(BACKLIGHT_RANGE_LOWER);
    }

    public static boolean hasBLRangeLower() {
        return Utils.existFile(BACKLIGHT_RANGE_LOWER);
    }

    public static void setPulseFreq(String value) {
        Utils.applyValue(String.valueOf(value), PULSE_FREQ);
    }

    public static String getPulseFreq() {
        return Utils.readFile(PULSE_FREQ);
    }

    public static boolean hasPulseFreq() {
        return Utils.existFile(PULSE_FREQ);
    }

    public static void setFlowFreq(String value) {
        Utils.applyValue(String.valueOf(value), FLOW_FREQ);
    }

    public static String getFlowFreq() {
        return Utils.readFile(FLOW_FREQ);
    }

    public static boolean hasFlowFreq() {
        return Utils.existFile(FLOW_FREQ);
    }

    public static boolean hasklapseVersion() {
        return Utils.existFile(KLAPSE_VERSION);
    }

    public static String getklapseVersion() {
        return Utils.readFile(KLAPSE_VERSION);
    }

    /*
     * Convert K-lapse schedule times (in minutes) into a human readable format
     * (hr:min & AM/PM)
     */
    private static String getAdjustedTime(String string) {
        int time = Utils.strToInt(string);
        int timeHr = time / 60;
        int timeMin = time - (timeHr * 60);
        return (timeHr > 12 ? timeHr - 12 : timeHr) + ":" + (timeMin < 10 ?
                "0" + timeMin : timeMin) + (timeHr > 12 ? " PM" : " AM");
    }

    private static final String[] KLAPSE_PROFILE = {
            KLAPSE_ENABLE, KLAPSE_TARGET_R, KLAPSE_TARGET_G, KLAPSE_TARGET_B,
            DAYTIME_R, DAYTIME_G, DAYTIME_B, KLAPSE_START_MIN, KLAPSE_END_MIN, KLAPSE_TARGET_MIN,
            FADEBACK_MINUTES, DIMMER_FACTOR, DIMMER_FACTOR_AUTO, DIMMER_START, DIMMER_END, PULSE_FREQ,
            FLOW_FREQ, BACKLIGHT_RANGE_UPPER, BACKLIGHT_RANGE_LOWER
    };

    public static int size() {
        return KLAPSE_PROFILE.length;
    }

    public static void exportKlapseSettings(int position) {
        String profileFolder = Utils.getInternalDataStorage () + "/";
        String value = Utils.readFile(KLAPSE_PROFILE[position]);
        if (value.startsWith("HBM mode = 1") || value.startsWith("mode = 1") || value.contains("Y")) {
            value = "1";
        } else if (value.startsWith("HBM mode = 0") || value.startsWith("mode = 0") || value.contains("N")) {
            value = "0";
        }
        if (Utils.existFile(KLAPSE_PROFILE[position])) {
            String command = "echo " + value + " > " + KLAPSE_PROFILE[position];
            Utils.mProfile.append(command).append("\n");
        }
    }

    public static boolean supported() {
        return hasEnable();
    }

}
