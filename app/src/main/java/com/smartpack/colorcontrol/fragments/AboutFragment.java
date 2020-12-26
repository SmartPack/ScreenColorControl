/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of Screen Color Control, an app made to offer advanced control
 * over the screen colour of smart devices having KCAL/K-lapse support.
 *
 */

package com.smartpack.colorcontrol.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.PopupMenu;

import com.smartpack.colorcontrol.BuildConfig;
import com.smartpack.colorcontrol.MainActivity;
import com.smartpack.colorcontrol.R;
import com.smartpack.colorcontrol.utils.Prefs;
import com.smartpack.colorcontrol.utils.Utils;
import com.smartpack.colorcontrol.views.dialog.Dialog;
import com.smartpack.colorcontrol.views.recyclerview.DescriptionView;
import com.smartpack.colorcontrol.views.recyclerview.RecyclerViewItem;
import com.smartpack.colorcontrol.views.recyclerview.TitleView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 01, 2020
 */

public class AboutFragment extends RecyclerViewFragment {

    private static final LinkedHashMap<String, String> sCredits = new LinkedHashMap<>();

    static {
        sCredits.put("Kernel Adiutor,Willi Ye", "https://github.com/Grarak");
        sCredits.put("libsu,topjohnwu", "https://github.com/topjohnwu");
        sCredits.put("Code contributions,Lennoard", "https://github.com/Lennoard");
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

        addViewPagerFragment(new InfoFragment());
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

    @SuppressLint("UseCompatLoadingForDrawables")
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
            String change_log = null;
            try {
                change_log = new JSONObject(Objects.requireNonNull(Utils.readAssetFile(
                        requireActivity(), "release.json"))).getString("fullChanges");
            } catch (JSONException ignored) {
            }
            Utils.mForegroundCard = requireActivity().findViewById(R.id.about_card);
            Utils.mBackButton = requireActivity().findViewById(R.id.back);
            Utils.mTitle = requireActivity().findViewById(R.id.card_title);
            Utils.mTitle.setText(getString(R.string.change_logs));
            Utils.mTitle.setVisibility(View.VISIBLE);
            Utils.mAppIcon = requireActivity().findViewById(R.id.app_image);
            Utils.mAppName = requireActivity().findViewById(R.id.app_title);
            Utils.mText = requireActivity().findViewById(R.id.scroll_text);
            Utils.mText.setText(change_log);
            Utils.mForegroundActive = true;
            Utils.mAppIcon.setVisibility(View.VISIBLE);
            Utils.mAppName.setVisibility(View.VISIBLE);
            Utils.mBackButton.setVisibility(View.VISIBLE);
            Utils.mBottomNav.setVisibility(View.GONE);
            Utils.mForegroundCard.setVisibility(View.VISIBLE);
        });

        items.add(changelogs);

        DescriptionView playstore = new DescriptionView();
        playstore.setDrawable(getResources().getDrawable(R.drawable.ic_playstore));
        playstore.setTitle(getString(R.string.playstore));
        playstore.setSummary(getString(R.string.playstore_summary));
        playstore.setOnItemClickListener(item -> Utils.launchUrl("https://play.google.com/store/apps/details?id=com.smartpack.colorcontrol", requireActivity()));

        items.add(playstore);

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
            donate_to_me.setMessage(getString(R.string.donate_me_message));
            donate_to_me.setNegativeButton(getString(R.string.donate_nope), (dialogInterface, i) -> {
            });
            donate_to_me.setPositiveButton(getString(R.string.purchase_app), (dialogInterface, i) -> {
                Utils.launchUrl("https://play.google.com/store/apps/details?id=com.smartpack.donate", getActivity());
            });
            donate_to_me.show();
        });

        if (!Utils.isDonated(requireActivity())) {
            items.add(donatetome);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
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
                case "topjohnwu":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_topjohnwu));
                    break;
                case "Lennoard":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_lennoard));
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

    public static class InfoFragment extends BaseFragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_about, container, false);
            AppCompatImageButton settings = rootView.findViewById(R.id.settings_menu);
            settings.setOnClickListener(v -> {
                if (Utils.mForegroundActive) return;
                PopupMenu popupMenu = new PopupMenu(requireActivity(), settings);
                Menu menu = popupMenu.getMenu();
                menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.dark_theme)).setCheckable(true)
                        .setChecked(Prefs.getBoolean("dark_theme", true, getActivity()));
                SubMenu language = menu.addSubMenu(Menu.NONE, 0, Menu.NONE, getString(R.string.language, Utils.getLanguage(getActivity())));
                language.add(Menu.NONE, 2, Menu.NONE, getString(R.string.language_default)).setCheckable(true)
                        .setChecked(Utils.languageDefault(getActivity()));
                language.add(Menu.NONE, 3, Menu.NONE, getString(R.string.language_en)).setCheckable(true)
                        .setChecked(Prefs.getBoolean("use_en", false, getActivity()));
                language.add(Menu.NONE, 4, Menu.NONE, getString(R.string.language_ko)).setCheckable(true)
                        .setChecked(Prefs.getBoolean("use_ko", false, getActivity()));
                language.add(Menu.NONE, 5, Menu.NONE, getString(R.string.language_am)).setCheckable(true)
                        .setChecked(Prefs.getBoolean("use_am", false, getActivity()));
                language.add(Menu.NONE, 6, Menu.NONE, getString(R.string.language_el)).setCheckable(true)
                        .setChecked(Prefs.getBoolean("use_el", false, getActivity()));
                language.add(Menu.NONE, 7, Menu.NONE, getString(R.string.language_vi)).setCheckable(true)
                        .setChecked(Prefs.getBoolean("use_vi", false, getActivity()));
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case 0:
                            break;
                        case 1:
                            if (Prefs.getBoolean("dark_theme", true, getActivity())) {
                                Prefs.saveBoolean("dark_theme", false, getActivity());
                            } else {
                                Prefs.saveBoolean("dark_theme", true, getActivity());
                            }
                            restartApp();
                            break;
                        case 2:
                            if (!Utils.languageDefault(getActivity())) {
                                Prefs.saveBoolean("use_en", false, getActivity());
                                Prefs.saveBoolean("use_ko", false, getActivity());
                                Prefs.saveBoolean("use_am", false, getActivity());
                                Prefs.saveBoolean("use_el", false, getActivity());
                                Prefs.saveBoolean("use_vi", false, getActivity());
                                restartApp();
                            }
                            break;
                        case 3:
                            if (!Prefs.getBoolean("use_en", false, getActivity())) {
                                Prefs.saveBoolean("use_en", true, getActivity());
                                Prefs.saveBoolean("use_ko", false, getActivity());
                                Prefs.saveBoolean("use_am", false, getActivity());
                                Prefs.saveBoolean("use_el", false, getActivity());
                                Prefs.saveBoolean("use_vi", false, getActivity());
                                restartApp();
                            }
                            break;
                        case 4:
                            if (!Prefs.getBoolean("use_ko", false, getActivity())) {
                                Prefs.saveBoolean("use_en", false, getActivity());
                                Prefs.saveBoolean("use_ko", true, getActivity());
                                Prefs.saveBoolean("use_am", false, getActivity());
                                Prefs.saveBoolean("use_el", false, getActivity());
                                Prefs.saveBoolean("use_vi", false, getActivity());
                                restartApp();
                            }
                            break;
                        case 5:
                            if (!Prefs.getBoolean("use_am", false, getActivity())) {
                                Prefs.saveBoolean("use_en", false, getActivity());
                                Prefs.saveBoolean("use_ko", false, getActivity());
                                Prefs.saveBoolean("use_am", true, getActivity());
                                Prefs.saveBoolean("use_el", false, getActivity());
                                Prefs.saveBoolean("use_vi", false, getActivity());
                                restartApp();
                            }
                            break;
                        case 6:
                            if (!Prefs.getBoolean("use_el", false, getActivity())) {
                                Prefs.saveBoolean("use_en", false, getActivity());
                                Prefs.saveBoolean("use_ko", false, getActivity());
                                Prefs.saveBoolean("use_am", false, getActivity());
                                Prefs.saveBoolean("use_el", true, getActivity());
                                Prefs.saveBoolean("use_vi", false, getActivity());
                                restartApp();
                            }
                            break;
                        case 7:
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
                popupMenu.show();
            });

            return rootView;
        }

        private void restartApp() {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

}