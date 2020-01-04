/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of Screen Color Control, an app made to offer advanced control
 * over the screen colour of smart devices having KCAL/K-lapse support.
 *
 */

package com.smartpack.colorcontrol.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import com.smartpack.colorcontrol.BuildConfig;
import com.smartpack.colorcontrol.R;
import com.smartpack.colorcontrol.views.dialog.Dialog;

import java.io.File;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 03, 2020
 */

public class UpdateCheck {

    private static final String PLAY_STORE = "com.android.vending";
    private static final String LATEST_VERSION = Utils.getInternalDataStorage() + "/.version";
    private static final String LATEST_VERSION_URL = "";
    private static final String DOWNLOAD_PAGE_URL = "";


    public static boolean isPlayStoreInstalled(Context context) {
        try {
            context.getPackageManager().getApplicationInfo(PLAY_STORE, 0);
            return true;
        } catch (PackageManager.NameNotFoundException ignored) {
            return false;
        }
    }

    private static void getVersionInfo() {
        File file = new File(Utils.getInternalDataStorage());
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        file.mkdirs();
        Utils.downloadFile(LATEST_VERSION, LATEST_VERSION_URL);
    }

    private static int getLatestVersionNumber() {
        return Utils.strToInt(Utils.readFile(LATEST_VERSION));
    }

    public static boolean hasVersionInfo() {
        return Utils.existFile(LATEST_VERSION);
    }

    private static void updateAvailableDialog(Context context) {
        new Dialog(context)
                .setMessage(context.getString(R.string.update_available))
                .setCancelable(false)
                .setNegativeButton(context.getString(R.string.cancel), (dialog, id) -> {
                })
                .setPositiveButton(context.getString(R.string.get_it), (dialog, id) -> {
                    Utils.launchUrl(DOWNLOAD_PAGE_URL, context);
                })
                .show();
    }

    public static void autoUpdateCheck(Context context) {
        getVersionInfo();
        if (hasVersionInfo()) {
            if (BuildConfig.VERSION_CODE < getLatestVersionNumber()) {
                updateAvailableDialog(context);
            }
        } else {
            Utils.toast(context.getString(R.string.update_check_failed) + " " + context.getString(R.string.no_internet), context);
        }
    }

    public static void updateCheck(Context context) {
        getVersionInfo();
        if (hasVersionInfo()) {
            if (BuildConfig.VERSION_CODE < getLatestVersionNumber()) {
                updateAvailableDialog(context);
            } else {
                new Dialog(context)
                        .setMessage(context.getString(R.string.uptodate_message))
                        .setCancelable(false)
                        .setPositiveButton(context.getString(R.string.cancel), (dialog, id) -> {
                        })
                        .show();
            }
        } else {
            Utils.toast(context.getString(R.string.update_check_failed) + " " + context.getString(R.string.no_internet), context);
        }
    }

}