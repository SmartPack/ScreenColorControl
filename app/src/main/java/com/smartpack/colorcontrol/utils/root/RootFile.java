/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of Screen Color Control, an app made to offer advanced control
 * over the screen colour of smart devices having KCAL/K-lapse support.
 *
 */

package com.smartpack.colorcontrol.utils.root;
import androidx.annotation.NonNull;

import com.smartpack.colorcontrol.utils.Utils;
import com.topjohnwu.superuser.Shell;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 01, 2020
 * Based on the original implementation on Kernel Adiutor by
 * Willi Ye <williye97@gmail.com>
 */

public class RootFile {

    private final String mFile;

    public RootFile(String file) {
        mFile = file;
    }

    public String getName() {
        return new File(mFile).getName();
    }

    public void mkdir() {
        Shell.su("mkdir -p '" + mFile + "'").exec();
    }

    public void write(String text, boolean append) {
        String[] array = text.split("\\r?\\n");
        if (!append) delete();
        for (String line : array) {
            Shell.su("echo '" + line + "' >> " + mFile).exec();
        }
        RootUtils.chmod(mFile, "755");
    }

    private void delete() {
        Shell.su("rm -r '" + mFile + "'").exec();
    }

    public List<String> list() {
        List<String> list = new ArrayList<>();
        String files = RootUtils.runCommand("ls '" + mFile + "/'");
        if (!files.isEmpty()) {
            // Make sure the files exists
            for (String file : files.split("\\r?\\n")) {
                if (file != null && !file.isEmpty() && Utils.existFile(mFile + "/" + file)) {
                    list.add(file);
                }
            }
        }
        return list;
    }

    public boolean exists() {
        String output = RootUtils.runCommand("[ -e " + mFile + " ] && echo true");
        return !output.isEmpty() && output.equals("true");
    }

    public String readFile() {
        return RootUtils.runCommand("cat '" + mFile + "'");
    }

    @NonNull
    @Override
    public String toString() {
        return mFile;
    }

}