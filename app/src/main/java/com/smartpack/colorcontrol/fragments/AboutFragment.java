/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of Screen Color Control, an app made to offer advanced control
 * over the screen colour of smart devices having KCAL/K-lapse support.
 *
 */

package com.smartpack.colorcontrol.fragments;

import android.content.Intent;

import com.smartpack.colorcontrol.BuildConfig;
import com.smartpack.colorcontrol.MainActivity;
import com.smartpack.colorcontrol.R;
import com.smartpack.colorcontrol.utils.Prefs;
import com.smartpack.colorcontrol.utils.UpdateCheck;
import com.smartpack.colorcontrol.utils.Utils;
import com.smartpack.colorcontrol.views.dialog.Dialog;
import com.smartpack.colorcontrol.views.recyclerview.DescriptionView;
import com.smartpack.colorcontrol.views.recyclerview.RecyclerViewItem;
import com.smartpack.colorcontrol.views.recyclerview.SwitchView;
import com.smartpack.colorcontrol.views.recyclerview.TitleView;

import java.util.List;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 01, 2020
 */

public class AboutFragment extends RecyclerViewFragment {

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.app_name),
                getString(R.string.about_me)));
    }

    @Override
    public int getSpanCount() {
        return super.getSpanCount() + 1;
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        aboutInit(items);
        creditsInit(items);
    }

    private void aboutInit(List<RecyclerViewItem> items) {

        TitleView about = new TitleView();
        about.setText(getString(R.string.about));

        items.add(about);

        DescriptionView versioninfo = new DescriptionView();
        versioninfo.setDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
        versioninfo.setTitle(getString(R.string.version));
        versioninfo.setSummary(BuildConfig.VERSION_NAME);

        items.add(versioninfo);

        DescriptionView changelogs = new DescriptionView();
        changelogs.setDrawable(getResources().getDrawable(R.drawable.ic_changelog));
        changelogs.setTitle(getString(R.string.change_logs));
        changelogs.setSummary(getString(R.string.change_logs_summary));
        changelogs.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                Utils.launchUrl("https://github.com/SmartPack/ScreenColorControl/raw/master/change-logs.md", requireActivity());
            }
        });

        items.add(changelogs);

        SwitchView allow_ads = new SwitchView();
        allow_ads.setDrawable(getResources().getDrawable(R.drawable.ic_ads));
        allow_ads.setSummary(getString(R.string.allow_ads));
        allow_ads.setChecked(Prefs.getBoolean("google_ads", false, getActivity()));
        allow_ads.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchview, boolean isChecked) {
                Prefs.saveBoolean("google_ads", isChecked, getActivity());
                if (!isChecked) {
                    new Dialog(getActivity())
                            .setMessage(R.string.disable_ads_message)
                            .setPositiveButton(R.string.ok, (dialog, id) -> {
                            })
                            .show();
                } else {
                    new Dialog(getActivity())
                            .setMessage(R.string.allow_ads_message)
                            .setPositiveButton(R.string.ok, (dialog, id) -> {
                            })
                            .show();
                }
            }
        });

        items.add(allow_ads);

        SwitchView dark_theme = new SwitchView();
        dark_theme.setDrawable(getResources().getDrawable(R.drawable.ic_color));
        dark_theme.setSummary(getString(R.string.dark_theme));
        dark_theme.setChecked(Prefs.getBoolean("dark_theme", true, getActivity()));
        dark_theme.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchview, boolean isChecked) {
                Prefs.saveBoolean("dark_theme", isChecked, getActivity());
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        items.add(dark_theme);

        if (UpdateCheck.isPlayStoreInstalled(getActivity())) {
            DescriptionView playstore = new DescriptionView();
            playstore.setDrawable(getResources().getDrawable(R.drawable.ic_playstore));
            playstore.setTitle(getString(R.string.playstore));
            playstore.setSummary(getString(R.string.playstore_summary));
            playstore.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    Utils.launchUrl("https://play.google.com/store/apps/details?id=com.smartpack.colorcontrol", requireActivity());
                }
            });

            items.add(playstore);
        } else {
            DescriptionView updateCheck = new DescriptionView();
            updateCheck.setDrawable(getResources().getDrawable(R.drawable.ic_update));
            updateCheck.setTitle(getString(R.string.update_check));
            updateCheck.setSummary(getString(R.string.update_check_summary));
            updateCheck.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    if (!Utils.isNetworkAvailable(getContext())) {
                        Utils.toast(R.string.no_internet, getActivity());
                        return;
                    }
                    UpdateCheck.updateCheck(getActivity());
                }
            });

            items.add(updateCheck);
        }

        DescriptionView share = new DescriptionView();
        share.setDrawable(getResources().getDrawable(R.drawable.ic_share));
        share.setTitle(getString(R.string.share_app));
        share.setSummary(getString(R.string.share_app_summary));
        share.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                Intent shareapp = new Intent()
                        .setAction(Intent.ACTION_SEND)
                        .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                        .putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_message, BuildConfig.VERSION_NAME))
                        .setType("text/plain");
                Intent shareIntent = Intent.createChooser(shareapp, null);
                startActivity(shareIntent);
            }
        });

        items.add(share);

        DescriptionView donatetome = new DescriptionView();
        donatetome.setDrawable(getResources().getDrawable(R.drawable.ic_donate));
        donatetome.setTitle(getString(R.string.donate_me));
        donatetome.setSummary(getString(R.string.donate_me_summary));
        donatetome.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                Dialog donate_to_me = new Dialog(getActivity());
                donate_to_me.setIcon(R.mipmap.ic_launcher);
                donate_to_me.setTitle(getString(R.string.donate_me));
                if (Utils.isDonated(requireActivity())) {
                    donate_to_me.setMessage(getString(R.string.donate_me_message));
                    donate_to_me.setNegativeButton(getString(R.string.donate_nope), (dialogInterface, i) -> {
                    });
                } else {
                    donate_to_me.setMessage(getString(R.string.donate_me_message) + getString(R.string.donate_me_playstore));
                    donate_to_me.setNegativeButton(getString(R.string.purchase_app), (dialogInterface, i) -> {
                        Utils.launchUrl("https://play.google.com/store/apps/details?id=com.smartpack.donate", getActivity());
                    });
                }
                donate_to_me.setPositiveButton(getString(R.string.paypal_donation), (dialog1, id1) -> {
                    Utils.launchUrl("https://www.paypal.me/sunilpaulmathew", getActivity());
                });
                donate_to_me.show();
            }
        });

        items.add(donatetome);
    }

    private void creditsInit(List<RecyclerViewItem> items) {

        TitleView credits = new TitleView();
        credits.setText(getString(R.string.credits));

        items.add(credits);

        DescriptionView grarak = new DescriptionView();
        grarak.setDrawable(getResources().getDrawable(R.drawable.ic_grarak));
        grarak.setTitle("Willi Ye");
        grarak.setSummary("Kernel Adiutor");
        grarak.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                Utils.launchUrl("https://github.com/Grarak", getActivity());
            }
        });

        items.add(grarak);

        DescriptionView toxinpiper = new DescriptionView();
        toxinpiper.setDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
        toxinpiper.setTitle("Toxinpiper");
        toxinpiper.setSummary("App Icon");
        toxinpiper.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                Utils.launchUrl("https://t.me/toxinpiper", getActivity());
            }
        });

        items.add(toxinpiper);

        DescriptionView burst = new DescriptionView();
        burst.setDrawable(getResources().getDrawable(R.drawable.ic_burst));
        burst.setTitle("Burst");
        burst.setSummary("Screen Color Image");
        burst.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                Utils.launchUrl("https://burst.shopify.com/photos/child-picking-dandelions-in-field?q=child+picking", getActivity());
            }
        });

        items.add(burst);
    }

}