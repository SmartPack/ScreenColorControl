/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of Screen Color Control, an app made to offer advanced control
 * over the screen colour of smart devices having KCAL/K-lapse support.
 *
 */

package com.smartpack.colorcontrol;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.smartpack.colorcontrol.fragments.AboutFragment;
import com.smartpack.colorcontrol.fragments.KlapseFragment;
import com.smartpack.colorcontrol.fragments.OtherSettingsFragment;
import com.smartpack.colorcontrol.fragments.ProfileFragment;
import com.smartpack.colorcontrol.fragments.ScreenColorFragment;
import com.smartpack.colorcontrol.utils.DRMColor;
import com.smartpack.colorcontrol.utils.Klapse;
import com.smartpack.colorcontrol.utils.NoRootActivity;
import com.smartpack.colorcontrol.utils.ScreenColor;
import com.smartpack.colorcontrol.utils.UpdateCheck;
import com.smartpack.colorcontrol.utils.Utils;
import com.smartpack.colorcontrol.utils.root.RootUtils;
import com.smartpack.colorcontrol.views.dialog.Dialog;

import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 01, 2020
 */

public class MainActivity extends AppCompatActivity {

    private boolean mExit;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Initialize App Theme & Google Ads
        Utils.initializeAppTheme(this);
        Utils.initializeGoogleAds(this);
        super.onCreate(savedInstanceState);
        // Set App Language
        Utils.setLanguage(this);
        setContentView(R.layout.activity_main);

        if (!RootUtils.rootAccess()) {
            Intent noRoot = new Intent(this, NoRootActivity.class);
            startActivity(noRoot);
            finish();
            return;
        }

        Utils.mBottomNav = findViewById(R.id.bottom_navigation);
        Utils.mBottomNav.setOnNavigationItemSelectedListener(navListener);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ScreenColorFragment()).commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener
            = menuItem -> {
        Fragment selectedFragment = null;

        switch (menuItem.getItemId()) {
            case R.id.nav_screen:
                selectedFragment = new ScreenColorFragment();
                break;
            case R.id.nav_klapse:
                selectedFragment = new KlapseFragment();
                break;
            case R.id.nav_others:
                selectedFragment = new OtherSettingsFragment();
                break;
            case R.id.nav_profiles:
                selectedFragment = new ProfileFragment();
                break;
            case R.id.nav_about:
                selectedFragment = new AboutFragment();
                break;
        }

        assert selectedFragment != null;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                selectedFragment).commit();

        return true;
    };

    public void closeForeGround(View view) {
        Utils.mForegroundCard = findViewById(R.id.about_card);
        Utils.mBackButton = findViewById(R.id.back);
        Utils.mAppIcon = findViewById(R.id.app_image);
        Utils.mAppName = findViewById(R.id.app_title);
        Utils.mTitle = findViewById(R.id.card_title);
        Utils.mAppIcon.setVisibility(View.GONE);
        Utils.mAppName.setVisibility(View.GONE);
        Utils.mBackButton.setVisibility(View.GONE);
        Utils.mTitle .setVisibility(View.GONE);
        Utils.mForegroundCard.setVisibility(View.GONE);
        Utils.mBottomNav.setVisibility(View.VISIBLE);
        Utils.mForegroundActive = false;
    }

    private void noSupport() {
        Dialog alert = new Dialog(Objects.requireNonNull(this));
        alert.setIcon(R.mipmap.ic_launcher);
        alert.setTitle(getString(R.string.app_name));
        alert.setMessage(getString(R.string.no_support, "KCAL/K-lapse"));
        alert.setCancelable(false);
        alert.setPositiveButton(getString(R.string.got_it), (dialog, id) -> {
        });

        alert.show();
    }

    @Override
    public void onStart(){
        super.onStart();

        if (!RootUtils.rootAccess()) {
            return;
        }
        if (!Klapse.supported() && !ScreenColor.getInstance().supported()
                && !DRMColor.supported()) {
            noSupport();
            return;
        }

        if (!Utils.isDownloadBinaries()) {
            return;
        }

        // Initialize manual Update Check, if play store not found
        if (!UpdateCheck.isPlayStoreInstalled(this)) {
            if (!Utils.checkWriteStoragePermission(this)) {
                return;
            }
            if (!Utils.isNetworkAvailable(this)) {
                return;
            }
            UpdateCheck.autoUpdateCheck(this);
        }
    }

    @Override
    public void onBackPressed() {
        if (RootUtils.rootAccess()) {
            if (Utils.mForegroundActive) {
                closeForeGround(Utils.mBottomNav);
            } else if (mExit) {
                mExit = false;
                super.onBackPressed();
            } else {
                Utils.snackbar(Utils.mBottomNav, getString(R.string.press_back));
                mExit = true;
                mHandler.postDelayed(() -> mExit = false, 2000);
            }
        } else {
            super.onBackPressed();
        }
    }

}