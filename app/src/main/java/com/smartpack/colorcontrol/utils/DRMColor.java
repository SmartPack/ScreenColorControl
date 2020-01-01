/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of Screen Color Control, an app made to offer advanced control
 * over the screen colour of smart devices having KCAL/K-lapse support.
 *
 */

package com.smartpack.colorcontrol.utils;

import com.smartpack.colorcontrol.utils.root.RootUtils;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 01, 2020
 */

public class DRMColor {

    private static final String DRM_PARANT = "/sys/module/msm_drm/parameters/";
    private static final String DRM_KCAL_RED = DRM_PARANT + "/kcal_red";
    private static final String DRM_KCAL_GREEN = DRM_PARANT + "/kcal_green";
    private static final String DRM_KCAL_BLUE = DRM_PARANT + "/kcal_blue";

    private static final String DRM_KCAL_INVERT = DRM_PARANT + "/kcal_invert";
    private static final String DRM_KCAL_SAT = DRM_PARANT + "/kcal_sat";
    private static final String DRM_KCAL_HUE = DRM_PARANT + "/kcal_hue";
    private static final String DRM_KCAL_VAL = DRM_PARANT + "/kcal_val";
    private static final String DRM_KCAL_CONT = DRM_PARANT + "/kcal_cont";

    public static void setkcalRed(int value) {
        Utils.applyValue(String.valueOf(value), DRM_KCAL_RED);
    }

    public static int getkcalRed() {
        return Utils.strToInt(Utils.readFile(DRM_KCAL_RED));
    }

    public static boolean haskcalRed() {
        return Utils.existFile(DRM_KCAL_RED);
    }

    public static void setkcalGreen(int value) {
        Utils.applyValue(String.valueOf(value), DRM_KCAL_GREEN);
    }

    public static int getkcalGreen() {
        return Utils.strToInt(Utils.readFile(DRM_KCAL_GREEN));
    }

    public static boolean haskcalGreen() {
        return Utils.existFile(DRM_KCAL_GREEN);
    }

    public static void setkcalBlue(int value) {
        Utils.applyValue(String.valueOf(value), DRM_KCAL_BLUE);
    }

    public static int getkcalBlue() {
        return Utils.strToInt(Utils.readFile(DRM_KCAL_BLUE));
    }

    public static boolean haskcalBlue() {
        return Utils.existFile(DRM_KCAL_BLUE);
    }

    public static boolean hasScreenHue() {
        return Utils.existFile(DRM_KCAL_HUE);
    }

    public static void setScreenHue(int value) {
        Utils.applyValue(String.valueOf(value), DRM_KCAL_HUE);
    }

    public static int getScreenHue() {
        return Utils.strToInt(Utils.readFile(DRM_KCAL_HUE));
    }

    public static boolean hasScreenVal() {
        return Utils.existFile(DRM_KCAL_VAL);
    }

    public static void setScreenVal(int value) {
        Utils.applyValue(String.valueOf(value), DRM_KCAL_VAL);
    }

    public static int getScreenVal() {
        return Utils.strToInt(Utils.readFile(DRM_KCAL_VAL));
    }

    public static boolean hasScreenCont() {
        return Utils.existFile(DRM_KCAL_CONT);
    }

    public static void setScreenCont(int value) {
        Utils.applyValue(String.valueOf(value), DRM_KCAL_CONT);
    }

    public static int getScreenCont() {
        return Utils.strToInt(Utils.readFile(DRM_KCAL_CONT));
    }

    public static boolean hasScreenSat() {
        return Utils.existFile(DRM_KCAL_SAT);
    }

    public static void enableGrayscaleMode(boolean enable) {
        Utils.applyValue(enable ? "128" : "255", DRM_KCAL_SAT);
    }

    public static boolean isGrayscaleModeEnabled() {
        return getScreenSat() == 128;
    }

    public static void setScreenSat(int value) {
        Utils.applyValue(String.valueOf(value), DRM_KCAL_SAT);
    }

    public static int getScreenSat() {
        return Utils.strToInt(Utils.readFile(DRM_KCAL_SAT));
    }

    public static void enableInvertScreen(boolean enable) {
        Utils.applyValue(enable ? "1" : "0", DRM_KCAL_INVERT);
    }

    public static boolean isInvertScreenEnabled() {
        return Utils.readFile(DRM_KCAL_INVERT).equals("1");
    }

    public static boolean hasInvertScreen() {
        return Utils.existFile(DRM_KCAL_INVERT);
    }

    private static final String[] COLOR_PROFILE = {
            DRM_KCAL_RED, DRM_KCAL_GREEN, DRM_KCAL_BLUE, DRM_KCAL_INVERT,
            DRM_KCAL_SAT, DRM_KCAL_HUE, DRM_KCAL_VAL, DRM_KCAL_CONT
    };

    public static int size() {
        return COLOR_PROFILE.length;
    }

    public static void exportColorSettings(String name, int position) {
        String profileFolder = Utils.getInternalDataStorage () + "/";
        String value = Utils.readFile(COLOR_PROFILE[position]);
        if (value.startsWith("HBM mode = 1") || value.startsWith("mode = 1") || value.contains("Y")) {
            value = "1";
        } else if (value.startsWith("HBM mode = 0") || value.startsWith("mode = 0") || value.contains("N")) {
            value = "0";
        }
        if (Utils.existFile(COLOR_PROFILE[position])) {
            String command = "echo " + value + " > " + COLOR_PROFILE[position];
            RootUtils.runCommand ("echo '" + command + "' >> " + profileFolder + "/" + name);
        }
    }

    public static boolean supported() {
        return haskcalBlue() || haskcalGreen() || haskcalRed() || hasInvertScreen()
                || hasScreenSat() || hasScreenCont() || hasScreenVal()
                || hasScreenHue();
    }

}
