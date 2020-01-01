/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of Screen Color Control, an app made to offer advanced control
 * over the screen colour of smart devices having KCAL/K-lapse support.
 *
 */

package com.smartpack.colorcontrol.utils;

import com.smartpack.colorcontrol.utils.root.RootUtils;

import java.io.File;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 01, 2020
 */

public class Profile {

    private static final String PROFILE = Utils.getInternalDataStorage();

    public static File ProfileFile() {
        return new File(PROFILE);
    }

    public static void importProfile(String string) {
        File file = new File(PROFILE);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        file.mkdirs();
        RootUtils.runCommand("cp " + string + " " + PROFILE);
    }

    public static void deleteProfile(String path) {
        File file = new File(path);
        file.delete();
    }

    public static String applyProfile(String file) {
        return RootUtils.runCommand("sh " + file);
    }

    public static String readProfile(String file) {
        return Utils.readFile(file);
    }

    public static boolean isColorConrolProfile(String file) {
        return readProfile(file).contains("# Created by Screen Color Control") && (readProfile(file).contains("# Screen Color")
                || readProfile(file).contains("# KCAL") || readProfile(file).contains("# K-lapse"));
    }

    public static String profileExistsCheck(String profile) {
        return ProfileFile().toString() + "/" + profile;
    }

}