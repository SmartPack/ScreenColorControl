/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of Screen Color Control, an app made to offer advanced control
 * over the screen colour of smart devices having KCAL/K-lapse support.
 *
 */

package com.smartpack.colorcontrol.utils.root;

import androidx.annotation.NonNull;

import java.io.File;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 01, 2020
 * Based on the original implementation on Kernel Adiutor by
 * Willi Ye <williye97@gmail.com>
 */

public class RootFile {

    private final String mFile;
    private RootUtils.SU mSU;

    public RootFile(String file, RootUtils.SU su) {
        mFile = file;
        mSU = su;
    }

    public String getName() {
        return new File(mFile).getName();
    }

    public void mkdir() {
        mSU.runCommand("mkdir -p '" + mFile + "'");
    }

    public boolean exists() {
        String output = mSU.runCommand("[ -e " + mFile + " ] && echo true");
        return output != null && output.equals("true");
    }

    public String readFile() {
        return mSU.runCommand("cat '" + mFile + "'");
    }

    @Override
    @NonNull
    public String toString() {
        return mFile;
    }
}
