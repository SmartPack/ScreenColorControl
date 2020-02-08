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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 03, 2020
 */

public class UpdateCheck {

    private static final String PLAY_STORE = "com.android.vending";
    private static final String LATEST_VERSION = Utils.getInternalDataStorage() + "/version";
    private static final String LATEST_VERSION_URL = "https://raw.githubusercontent.com/SmartPack/ScreenColorControl/master/release/version.json?raw=true";
    private static final String DOWNLOAD_PAGE_URL = "https://github.com/SmartPack/ScreenColorControl/tree/master/release";


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
        try {
            JSONObject obj = new JSONObject(Utils.readFile(LATEST_VERSION));
            return (obj.getInt("versionCode"));
        } catch (JSONException e) {
            return BuildConfig.VERSION_CODE;
        }
    }

    private static String getLatestVersionName() {
        try {
            JSONObject obj = new JSONObject(Utils.readFile(LATEST_VERSION));
            return (obj.getString("versionName"));
        } catch (JSONException e) {
            return "Unavailable";
        }
    }

    private static String getChangelogs() {
        try {
            JSONObject obj = new JSONObject(Utils.readFile(LATEST_VERSION));
            return (obj.getString("changelog"));
        } catch (JSONException e) {
            return "Unavailable";
        }
    }

    private static long lastModified() {
        return new File(LATEST_VERSION).lastModified();
    }

    public static boolean hasVersionInfo() {
        return Utils.existFile(LATEST_VERSION);
    }

    private static void updateAvailableDialog(Context context) {
        new Dialog(context)
                .setTitle(context.getString(R.string.update_available) + getLatestVersionName())
                .setMessage(context.getString(R.string.update_available_summary, getChangelogs()))
                .setCancelable(false)
                .setNegativeButton(context.getString(R.string.cancel), (dialog, id) -> {
                })
                .setPositiveButton(context.getString(R.string.get_it), (dialog, id) -> {
                    Utils.launchUrl(DOWNLOAD_PAGE_URL, context);
                })
                .show();
    }

    public static void autoUpdateCheck(Context context) {
        if (!hasVersionInfo() && lastModified() + 89280000L < System.currentTimeMillis()) {
            getVersionInfo();
        }
        if (hasVersionInfo()) {
            if (BuildConfig.VERSION_CODE < getLatestVersionNumber()) {
                updateAvailableDialog(context);
            }
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