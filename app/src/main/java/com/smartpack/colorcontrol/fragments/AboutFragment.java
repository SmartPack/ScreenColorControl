/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of Screen Color Control, an app made to offer advanced control
 * over the screen colour of smart devices having KCAL/K-lapse support.
 *
 */

package com.smartpack.colorcontrol.fragments;

import android.content.Intent;
import android.view.Menu;

import com.smartpack.colorcontrol.BuildConfig;
import com.smartpack.colorcontrol.MainActivity;
import com.smartpack.colorcontrol.R;
import com.smartpack.colorcontrol.utils.Prefs;
import com.smartpack.colorcontrol.utils.UpdateCheck;
import com.smartpack.colorcontrol.utils.Utils;
import com.smartpack.colorcontrol.utils.ViewUtils;
import com.smartpack.colorcontrol.views.dialog.Dialog;
import com.smartpack.colorcontrol.views.recyclerview.DescriptionView;
import com.smartpack.colorcontrol.views.recyclerview.RecyclerViewItem;
import com.smartpack.colorcontrol.views.recyclerview.SwitchView;
import com.smartpack.colorcontrol.views.recyclerview.TitleView;

import java.util.LinkedHashMap;
import java.util.List;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 01, 2020
 */

public class AboutFragment extends RecyclerViewFragment {

    private static final LinkedHashMap<String, String> sCredits = new LinkedHashMap<>();

    static {
        sCredits.put("Kernel Adiutor,Willi Ye", "https://github.com/Grarak");
        sCredits.put("Korean Translations,SmgKhOaRn", "https://github.com/SmgKhOaRn");
        sCredits.put("Amharic Translations,Mikesew1320", "https://github.com/Mikesew1320");
        sCredits.put("Greek Translations,tsiflimagas", "https://github.com/tsiflimagas");
        sCredits.put("Vietnamese Translations,shinyheroX", "https://github.com/shinyheroX");
        sCredits.put("Screen Color Image,Burst", "https://burst.shopify.com/photos/child-picking-dandelions-in-field?q=child+picking");
        sCredits.put("App Icon,Toxinpiper", "https://t.me/toxinpiper");
    }

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
        changelogs.setOnItemClickListener(item -> {
            Utils.getInstance().showInterstitialAd(requireActivity());
            Utils.launchUrl("https://github.com/SmartPack/ScreenColorControl/raw/master/change-logs.md", requireActivity());
        });

        items.add(changelogs);

        SwitchView allow_ads = new SwitchView();
        allow_ads.setDrawable(getResources().getDrawable(R.drawable.ic_ads));
        allow_ads.setSummary(getString(R.string.allow_ads));
        allow_ads.setChecked(Prefs.getBoolean("google_ads", true, getActivity()));
        allow_ads.addOnSwitchListener((switchview, isChecked) -> {
            Prefs.saveBoolean("google_ads", isChecked, getActivity());
            if (!isChecked) {
                new Dialog(requireActivity())
                        .setMessage(R.string.disable_ads_message)
                        .setPositiveButton(R.string.ok, (dialog, id) -> {
                            restartApp();
                        })
                        .show();
            } else {
                new Dialog(requireActivity())
                        .setMessage(R.string.allow_ads_message)
                        .setPositiveButton(R.string.ok, (dialog, id) -> {
                            restartApp();
                        })
                        .show();
            }
        });

        items.add(allow_ads);

        SwitchView dark_theme = new SwitchView();
        dark_theme.setDrawable(ViewUtils.getColoredIcon(R.drawable.ic_color, requireActivity()));
        dark_theme.setSummary(getString(R.string.dark_theme));
        dark_theme.setChecked(Prefs.getBoolean("dark_theme", true, getActivity()));
        dark_theme.addOnSwitchListener((switchview, isChecked) -> {
            Prefs.saveBoolean("dark_theme", isChecked, getActivity());
            restartApp();
        });

        items.add(dark_theme);

        if (UpdateCheck.isPlayStoreInstalled(requireActivity())) {
            DescriptionView playstore = new DescriptionView();
            playstore.setDrawable(getResources().getDrawable(R.drawable.ic_playstore));
            playstore.setTitle(getString(R.string.playstore));
            playstore.setSummary(getString(R.string.playstore_summary));
            playstore.setOnItemClickListener(item -> Utils.launchUrl("https://play.google.com/store/apps/details?id=com.smartpack.colorcontrol", requireActivity()));

            items.add(playstore);
        } else {
            DescriptionView updateCheck = new DescriptionView();
            updateCheck.setDrawable(getResources().getDrawable(R.drawable.ic_update));
            updateCheck.setTitle(getString(R.string.update_check));
            updateCheck.setSummary(getString(R.string.update_check_summary));
            updateCheck.setOnItemClickListener(item -> {
                if (!Utils.isNetworkAvailable(requireActivity())) {
                    Utils.toast(R.string.no_internet, getActivity());
                    return;
                }
                Utils.getInstance().showInterstitialAd(requireActivity());
                UpdateCheck.updateCheck(getActivity());
            });

            items.add(updateCheck);
        }

        DescriptionView sourcecode = new DescriptionView();
        sourcecode.setDrawable(getResources().getDrawable(R.drawable.ic_source));
        sourcecode.setTitle(getString(R.string.source_code));
        sourcecode.setSummary(getString(R.string.source_code_summary));
        sourcecode.setOnItemClickListener(item -> Utils.launchUrl("https://github.com/SmartPack/ScreenColorControl", requireActivity()));

        items.add(sourcecode);

        DescriptionView donatetome = new DescriptionView();
        donatetome.setDrawable(getResources().getDrawable(R.drawable.ic_donate));
        donatetome.setTitle(getString(R.string.donate_me));
        donatetome.setSummary(getString(R.string.donate_me_summary));
        donatetome.setOnItemClickListener(item -> {
            Dialog donate_to_me = new Dialog(requireActivity());
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
                Utils.launchUrl("https://www.paypal.me/menacherry", getActivity());
            });
            donate_to_me.show();
        });

        items.add(donatetome);
        DescriptionView share = new DescriptionView();
        share.setDrawable(getResources().getDrawable(R.drawable.ic_share));
        share.setTitle(getString(R.string.share_app));
        share.setSummary(getString(R.string.share_app_summary));
        share.setOnItemClickListener(item -> {
            Intent shareapp = new Intent()
                    .setAction(Intent.ACTION_SEND)
                    .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                    .putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_message, BuildConfig.VERSION_NAME))
                    .setType("text/plain");
            Intent shareIntent = Intent.createChooser(shareapp, null);
            startActivity(shareIntent);
        });

        items.add(share);

        DescriptionView language = new DescriptionView();
        language.setDrawable(ViewUtils.getColoredIcon(R.drawable.ic_language, requireActivity()));
        language.setTitle(getString(R.string.language, Utils.getLanguage(getActivity())));
        language.setMenuIcon(getResources().getDrawable(R.drawable.ic_dots));
        language.setOnMenuListener((script, popupMenu) -> {
            Menu menu = popupMenu.getMenu();
            menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.language_default)).setCheckable(true)
                    .setChecked(Utils.languageDefault(getActivity()));
            menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.language_en)).setCheckable(true)
                    .setChecked(Prefs.getBoolean("use_en", false, getActivity()));
            menu.add(Menu.NONE, 2, Menu.NONE, getString(R.string.language_ko)).setCheckable(true)
                    .setChecked(Prefs.getBoolean("use_ko", false, getActivity()));
            menu.add(Menu.NONE, 3, Menu.NONE, getString(R.string.language_am)).setCheckable(true)
                    .setChecked(Prefs.getBoolean("use_am", false, getActivity()));
            menu.add(Menu.NONE, 4, Menu.NONE, getString(R.string.language_el)).setCheckable(true)
                    .setChecked(Prefs.getBoolean("use_el", false, getActivity()));
            menu.add(Menu.NONE, 5, Menu.NONE, getString(R.string.language_vi)).setCheckable(true)
                    .setChecked(Prefs.getBoolean("use_vi", false, getActivity()));
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 0:
                        if (!Utils.languageDefault(getActivity())) {
                            Prefs.saveBoolean("use_en", false, getActivity());
                            Prefs.saveBoolean("use_ko", false, getActivity());
                            Prefs.saveBoolean("use_am", false, getActivity());
                            Prefs.saveBoolean("use_el", false, getActivity());
                            Prefs.saveBoolean("use_vi", false, getActivity());
                            restartApp();
                        }
                        break;
                    case 1:
                        if (!Prefs.getBoolean("use_en", false, getActivity())) {
                            Prefs.saveBoolean("use_en", true, getActivity());
                            Prefs.saveBoolean("use_ko", false, getActivity());
                            Prefs.saveBoolean("use_am", false, getActivity());
                            Prefs.saveBoolean("use_el", false, getActivity());
                            Prefs.saveBoolean("use_vi", false, getActivity());
                            restartApp();
                        }
                        break;
                    case 2:
                        if (!Prefs.getBoolean("use_ko", false, getActivity())) {
                            Prefs.saveBoolean("use_en", false, getActivity());
                            Prefs.saveBoolean("use_ko", true, getActivity());
                            Prefs.saveBoolean("use_am", false, getActivity());
                            Prefs.saveBoolean("use_el", false, getActivity());
                            Prefs.saveBoolean("use_vi", false, getActivity());
                            restartApp();
                        }
                        break;
                    case 3:
                        if (!Prefs.getBoolean("use_am", false, getActivity())) {
                            Prefs.saveBoolean("use_en", false, getActivity());
                            Prefs.saveBoolean("use_ko", false, getActivity());
                            Prefs.saveBoolean("use_am", true, getActivity());
                            Prefs.saveBoolean("use_el", false, getActivity());
                            Prefs.saveBoolean("use_vi", false, getActivity());
                            restartApp();
                        }
                        break;
                    case 4:
                        if (!Prefs.getBoolean("use_el", false, getActivity())) {
                            Prefs.saveBoolean("use_en", false, getActivity());
                            Prefs.saveBoolean("use_ko", false, getActivity());
                            Prefs.saveBoolean("use_am", false, getActivity());
                            Prefs.saveBoolean("use_el", true, getActivity());
                            Prefs.saveBoolean("use_vi", false, getActivity());
                            restartApp();
                        }
                        break;
                    case 5:
                        if (!Prefs.getBoolean("use_vi", false, getActivity())) {
                            Prefs.saveBoolean("use_en", false, getActivity());
                            Prefs.saveBoolean("use_ko", false, getActivity());
                            Prefs.saveBoolean("use_am", false, getActivity());
                            Prefs.saveBoolean("use_el", false, getActivity());
                            Prefs.saveBoolean("use_vi", true, getActivity());
                            restartApp();
                        }
                        break;
                }
                return false;
            });
        });

        items.add(language);
    }

    private void creditsInit(List<RecyclerViewItem> items) {

        TitleView credits = new TitleView();
        credits.setText(getString(R.string.credits));

        items.add(credits);

        for (final String lib : sCredits.keySet()) {
            String title = lib.split(",")[1];
            String summary = lib.split(",")[0];
            DescriptionView descriptionView = new DescriptionView();
            switch (title) {
                case "Willi Ye":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_grarak));
                    break;
                case "SmgKhOaRn":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_smgkhoarn));
                    break;
                case "Mikesew1320":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_mikesew));
                    break;
                case "tsiflimagas":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_tsiflimagas));
                    break;
                case "shinyheroX":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_shinyherox));
                    break;
                case "Burst":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_burst));
                    break;
                case "Toxinpiper":
                    descriptionView.setDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
                    break;
            }
            descriptionView.setTitle(title);
            descriptionView.setSummary(summary);
            descriptionView.setOnItemClickListener(item ->
                    Utils.launchUrl(sCredits.get(lib), getActivity())
            );

            items.add(descriptionView);
        }
    }

    private void restartApp() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}