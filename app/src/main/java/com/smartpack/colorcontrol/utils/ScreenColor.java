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

public class ScreenColor {

    private static ScreenColor sInstance;

    public static ScreenColor getInstance() {
        if (sInstance == null) {
            sInstance = new ScreenColor();
        }
        return sInstance;
    }

    private static final String KCAL = "/sys/devices/platform/kcal_ctrl.0";
    private static final String KCAL_CTRL = KCAL + "/kcal";
    private static final String KCAL_CTRL_CTRL = KCAL + "/kcal_ctrl";
    private static final String KCAL_CTRL_ENABLE = KCAL + "/kcal_enable";
    private static final String KCAL_CTRL_MIN = KCAL + "/kcal_min";
    private static final String KCAL_CTRL_INVERT = KCAL + "/kcal_invert";
    private static final String KCAL_CTRL_SAT = KCAL + "/kcal_sat";
    private static final String KCAL_CTRL_HUE = KCAL + "/kcal_hue";
    private static final String KCAL_CTRL_VAL = KCAL + "/kcal_val";
    private static final String KCAL_CTRL_CONT = KCAL + "/kcal_cont";

    private static final String DIAG0 = "/sys/devices/platform/DIAG0.0";
    private static final String DIAG0_POWER = DIAG0 + "/power_rail";
    private static final String DIAG0_POWER_CTRL = DIAG0 + "/power_rail_ctrl";

    private static final String COLOR_CONTROL = "/sys/class/misc/colorcontrol";
    private static final String COLOR_CONTROL_MUILTIPLIER = COLOR_CONTROL + "/multiplier";
    private static final String COLOR_CONTROL_CTRL = COLOR_CONTROL + "/safety_enabled";

    private static final String SAMOLED_COLOR = "/sys/class/misc/samoled_color";
    private static final String SAMOLED_COLOR_RED = SAMOLED_COLOR + "/red_multiplier";
    private static final String SAMOLED_COLOR_GREEN = SAMOLED_COLOR + "/green_multiplier";
    private static final String SAMOLED_COLOR_BLUE = SAMOLED_COLOR + "/blue_multiplier";

    private static final String FB0_RGB = "/sys/class/graphics/fb0/rgb";
    private static final String FB_KCAL = "/sys/class/graphics/fb0/kcal";

    private static final String HBM = "/sys/class/graphics/fb0/hbm";

    private static final String SRGB = "/sys/class/graphics/fb0/SRGB";
    private static final String SRGB2 = "/sys/class/graphics/fb0/srgb";

    private final List<String> mColors = new ArrayList<>();
    private final List<String> mColorEnables = new ArrayList<>();
    private final List<String> mNewKCAL = new ArrayList<>();

    {
        mColors.add(KCAL_CTRL);
        mColors.add(DIAG0_POWER);
        mColors.add(COLOR_CONTROL_MUILTIPLIER);
        mColors.add(SAMOLED_COLOR);
        mColors.add(FB0_RGB);
        mColors.add(FB_KCAL);

        mColorEnables.add(KCAL_CTRL_CTRL);
        mColorEnables.add(KCAL_CTRL_ENABLE);
        mColorEnables.add(DIAG0_POWER_CTRL);
        mColorEnables.add(COLOR_CONTROL_CTRL);

        mNewKCAL.add(KCAL_CTRL_ENABLE);
        mNewKCAL.add(KCAL_CTRL_INVERT);
        mNewKCAL.add(KCAL_CTRL_SAT);
        mNewKCAL.add(KCAL_CTRL_HUE);
        mNewKCAL.add(KCAL_CTRL_VAL);
        mNewKCAL.add(KCAL_CTRL_CONT);
    }

    private String COLOR;
    private String COLOR_ENABLE;

    private boolean HBM_NEW;

    private ScreenColor() {
        for (String file : mColors) {
            if (Utils.existFile(file)) {
                COLOR = file;
                break;
            }
        }

        if (COLOR == null) return;
        for (String file : mColorEnables) {
            if (Utils.existFile(file)) {
                COLOR_ENABLE = file;
                break;
            }
        }
    }

    public void enableSRGB(boolean enable) {
        if (Utils.existFile(SRGB)) {
            Utils.applyValue(enable ? "1" : "0", SRGB);
        } else {
            Utils.applyValue(enable ? "1" : "0", SRGB2);
        }
    }

    public boolean isSRGBEnabled() {
        String value = Utils.readFile(SRGB);
        return value.equals("1") || value.contains("mode = 1");
    }

    public boolean hasSRGB() {
        return Utils.existFile(SRGB) || Utils.existFile(SRGB2);
    }

    public void enableScreenHBM(boolean enable) {
        Utils.applyValue(enable ? HBM_NEW ? "2" : "1" : "0", HBM);
    }

    public boolean isScreenHBMEnabled() {
        if (HBM_NEW) {
            return Utils.readFile(HBM).contains("= 2");
        }
        return Utils.readFile(HBM).equals("1");
    }

    public boolean hasScreenHBM() {
        boolean supported = Utils.existFile(HBM);
        if (supported) {
            HBM_NEW = Utils.readFile(HBM).contains("2-->HBM Enabled");
            return true;
        }
        return false;
    }

    public void setScreenContrast(int value) {
        Utils.applyValue(String.valueOf(value), KCAL_CTRL_CONT);
    }

    public int getScreenContrast() {
        return Utils.strToInt(Utils.readFile(KCAL_CTRL_CONT));
    }

    public boolean hasScreenContrast() {
        return Utils.existFile(KCAL_CTRL_CONT);
    }

    public void setScreenValue(int value) {
        Utils.applyValue(String.valueOf(value), KCAL_CTRL_VAL);
    }

    public int getScreenValue() {
        return Utils.strToInt(Utils.readFile(KCAL_CTRL_VAL));
    }

    public boolean hasScreenValue() {
        return Utils.existFile(KCAL_CTRL_VAL);
    }

    public void setScreenHue(int value) {
        Utils.applyValue(String.valueOf(value), KCAL_CTRL_HUE);
    }

    public int getScreenHue() {
        return Utils.strToInt(Utils.readFile(KCAL_CTRL_HUE));
    }

    public boolean hasScreenHue() {
        return Utils.existFile(KCAL_CTRL_HUE);
    }

    public void enableGrayscaleMode(boolean enable) {
        Utils.applyValue(enable ? "128" : "255", KCAL_CTRL_SAT);
    }

    public boolean isGrayscaleModeEnabled() {
        return getSaturationIntensity() == 128;
    }

    public void setSaturationIntensity(int value) {
        Utils.applyValue(String.valueOf(value), KCAL_CTRL_SAT);
    }

    public int getSaturationIntensity() {
        return Utils.strToInt(Utils.readFile(KCAL_CTRL_SAT));
    }

    public boolean hasSaturationIntensity() {
        return Utils.existFile(KCAL_CTRL_SAT);
    }

    public void enableInvertScreen(boolean enable) {
        Utils.applyValue(enable ? "1" : "0", KCAL_CTRL_INVERT);
    }

    public boolean isInvertScreenEnabled() {
        return Utils.readFile(KCAL_CTRL_INVERT).equals("1");
    }

    public boolean hasInvertScreen() {
        return Utils.existFile(KCAL_CTRL_INVERT);
    }

    public void setMinColor(int value) {
        Utils.applyValue(String.valueOf(value), KCAL_CTRL_MIN);
    }

    public int getMinColor() {
        return Utils.strToInt(Utils.readFile(KCAL_CTRL_MIN));
    }

    public void setColors(String values) {
        if (hasColorEnable() && COLOR_CONTROL_CTRL.equals(COLOR_ENABLE)) {
            Utils.applyValue("0", COLOR_CONTROL_CTRL);
        }

        switch (COLOR) {
            case COLOR_CONTROL_MUILTIPLIER: {
                String[] colors = values.split(" ");
                String red = String.valueOf(Utils.strToLong(colors[0]) * 10000000L);
                String green = String.valueOf(Utils.strToLong(colors[1]) * 10000000L);
                String blue = String.valueOf(Utils.strToLong(colors[2]) * 10000000L);
                Utils.applyValue(red + " " + green + " " + blue, COLOR_CONTROL_MUILTIPLIER);
                break;
            }
            case SAMOLED_COLOR: {
                String[] colors = values.split(" ");
                Utils.applyValue(String.valueOf(Utils.strToLong(colors[0]) * 10000000), SAMOLED_COLOR_RED);
                Utils.applyValue(String.valueOf(Utils.strToLong(colors[1]) * 10000000), SAMOLED_COLOR_GREEN);
                Utils.applyValue(String.valueOf(Utils.strToLong(colors[2]) * 10000000), SAMOLED_COLOR_RED);
                break;
            }
            default:
                Utils.applyValue(values, COLOR);
                break;
        }

        if (hasColorEnable() && !COLOR_CONTROL_CTRL.equals(COLOR_ENABLE)) {
            Utils.applyValue("1", COLOR_ENABLE);
        }
    }

    public List<String> getLimits() {
        List<String> list = new ArrayList<>();
        switch (COLOR) {
            case COLOR_CONTROL_MUILTIPLIER:
            case SAMOLED_COLOR:
                for (int i = 60; i <= 400; i++) {
                    list.add(String.valueOf(i));
                }
                break;
            case FB0_RGB:
                for (int i = 255; i <= 32768; i++) {
                    list.add(String.valueOf(i));
                }
                break;
            case FB_KCAL:
                for (int i = 0; i < 256; i++) {
                    list.add(String.valueOf(i));
                }
                break;
            default:
                int max = hasNewKCAL() ? 256 : 255;
                for (int i = 0; i <= max; i++) {
                    list.add(String.valueOf(i));
                }
                break;
        }
        return list;
    }

    private boolean hasNewKCAL() {
        for (String file : mNewKCAL) {
            if (Utils.existFile(file)) {
                return true;
            }
        }
        return false;
    }

    public List<String> getColors() {
        List<String> list = new ArrayList<>();
        switch (COLOR) {
            case COLOR_CONTROL_MUILTIPLIER:
                String[] colors = Utils.readFile(COLOR_CONTROL_MUILTIPLIER).split(" ");
                for (String color : colors) {
                    list.add(String.valueOf(Utils.strToLong(color) / 10000000L));
                }
                break;
            case SAMOLED_COLOR:
                if (Utils.existFile(SAMOLED_COLOR_RED)) {
                    long color = Utils.strToLong(Utils.readFile(SAMOLED_COLOR_RED)) / 10000000L;
                    list.add(String.valueOf(color));
                }
                if (Utils.existFile(SAMOLED_COLOR_GREEN)) {
                    long color = Utils.strToLong(Utils.readFile(SAMOLED_COLOR_GREEN)) / 10000000L;
                    list.add(String.valueOf(color));
                }
                if (Utils.existFile(SAMOLED_COLOR_BLUE)) {
                    long color = Utils.strToLong(Utils.readFile(SAMOLED_COLOR_BLUE)) / 10000000L;
                    list.add(String.valueOf(color));
                }
                break;
            default:
                String value = Utils.readFile(COLOR);
                if (value != null) {
                    for (String color : value.split(" ")) {
                        list.add(String.valueOf(Utils.strToLong(color)));
                    }
                }
                break;
        }
        return list;
    }

    private boolean hasColorEnable() {
        return COLOR_ENABLE != null;
    }

    public boolean hasColors() {
        return COLOR != null;
    }

    private static final String[] COLOR_PROFILE = {
            KCAL_CTRL, KCAL_CTRL_CTRL, KCAL_CTRL_ENABLE, KCAL_CTRL_MIN,
            KCAL_CTRL_INVERT, KCAL_CTRL_SAT, KCAL_CTRL_HUE, KCAL_CTRL_VAL,
            KCAL_CTRL_CONT, DIAG0_POWER, DIAG0_POWER_CTRL, COLOR_CONTROL_MUILTIPLIER,
            COLOR_CONTROL_CTRL, SAMOLED_COLOR_RED, SAMOLED_COLOR_GREEN,
            SAMOLED_COLOR_BLUE, FB0_RGB, FB_KCAL, HBM, SRGB, SRGB2
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
            Utils.append(command, profileFolder + "/" + name);
        }
    }

    public boolean supported() {
        return hasColors() || hasInvertScreen() || hasSaturationIntensity() || hasScreenHue()
                || hasScreenValue() | hasScreenContrast() || hasScreenHBM();
    }

}
