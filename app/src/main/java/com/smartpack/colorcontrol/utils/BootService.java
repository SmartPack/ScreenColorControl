/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of Screen Color Control, an app made to offer advanced control
 * over the screen colour of smart devices having KCAL/K-lapse support.
 *
 */

package com.smartpack.colorcontrol.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.smartpack.colorcontrol.R;
import com.smartpack.colorcontrol.utils.root.RootFile;
import com.smartpack.colorcontrol.utils.root.RootUtils;

import java.io.File;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on May 15, 2020
 */

public class BootService extends Service {

    private static String onBoot = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Profile.ProfileFile().exists() && (Klapse.supported() || ScreenColor.getInstance().supported()
                || DRMColor.supported())) {
            for (final String profileItems : Profile.profileItems()) {
                File profiles = new File(Profile.ProfileFile() + "/" + profileItems);
                if (Utils.getExtension(profiles.toString()).equals("sh") && Profile.isColorConrolProfile(profiles.toString())) {
                    if (Utils.existFile(getFilesDir().toString() + "/" + profiles.getName())) {
                        onBoot = profiles.toString();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationManager mNotificationManager = (NotificationManager)
                                    getSystemService(NOTIFICATION_SERVICE);
                            NotificationChannel notificationChannel = new NotificationChannel("onboot",
                                    getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
                            notificationChannel.setSound(null, null);
                            assert mNotificationManager != null;
                            mNotificationManager.createNotificationChannel(notificationChannel);
                            Notification.Builder builder = new Notification.Builder(
                                    this, "onboot");
                            builder.setContentTitle(getString(R.string.onboot))
                                    .setContentText(getString(R.string.onboot_summary, profiles.getName()))
                                    .setSmallIcon(R.mipmap.ic_launcher_round);
                            startForeground(1, builder.build());
                        }
                    }
                }
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (onBoot != null) {
            RootFile file = new RootFile(onBoot);
            file.execute();
            RootUtils.runCommand("sleep 5");
            stopSelf();
        }
        return START_NOT_STICKY;
    }

}