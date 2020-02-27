/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of Screen Color Control, an app made to offer advanced control
 * over the screen colour of smart devices having KCAL/K-lapse support.
 *
 */

package com.smartpack.colorcontrol;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.smartpack.colorcontrol.fragments.AboutFragment;
import com.smartpack.colorcontrol.fragments.KlapseFragment;
import com.smartpack.colorcontrol.fragments.OtherSettingsFragment;
import com.smartpack.colorcontrol.fragments.ProfileFragment;
import com.smartpack.colorcontrol.fragments.ScreenColorFragment;
import com.smartpack.colorcontrol.utils.PagerAdapter;
import com.smartpack.colorcontrol.utils.Utils;
import com.smartpack.colorcontrol.utils.root.RootUtils;

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
        Utils.getInstance().initializeGoogleAds(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatImageView unsupported = findViewById(R.id.no_root_Image);
        TextView textView = findViewById(R.id.no_root_Text);
        TabLayout tabLayout = findViewById(R.id.tabLayoutID);
        ViewPager viewPager = findViewById(R.id.viewPagerID);

        if (!RootUtils.rootAccess()) {
            textView.setText(getString(R.string.no_root));
            unsupported.setImageDrawable(getResources().getDrawable(R.drawable.ic_help));
            return;
        }

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new ScreenColorFragment(), getString(R.string.screen_color));
        adapter.AddFragment(new KlapseFragment(), getString(R.string.klapse));
        adapter.AddFragment(new OtherSettingsFragment(), getString(R.string.others));
        adapter.AddFragment(new ProfileFragment(), getString(R.string.profiles));
        adapter.AddFragment(new AboutFragment(), getString(R.string.about));

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void androidRooting(View view) {
        Utils.launchUrl("https://www.google.com/search?site=&source=hp&q=android+rooting+magisk", this);
    }

    @Override
    public void onBackPressed() {
        if (!RootUtils.rootAccess()) {
            super.onBackPressed();
        }
        if (mExit) {
            mExit = false;
            super.onBackPressed();
        } else {
            Utils.toast(R.string.press_back, this);
            mExit = true;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mExit = false;
                }
            }, 2000);
        }
    }

}