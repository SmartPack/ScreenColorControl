/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of Screen Color Control, an app made to offer advanced control
 * over the screen colour of smart devices having KCAL/K-lapse support.
 *
 */

package com.smartpack.colorcontrol.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.smartpack.colorcontrol.BuildConfig;
import com.smartpack.colorcontrol.R;
import com.smartpack.colorcontrol.utils.DRMColor;
import com.smartpack.colorcontrol.utils.EditorActivity;
import com.smartpack.colorcontrol.utils.Klapse;
import com.smartpack.colorcontrol.utils.Profile;
import com.smartpack.colorcontrol.utils.ScreenColor;
import com.smartpack.colorcontrol.utils.Utils;
import com.smartpack.colorcontrol.utils.ViewUtils;
import com.smartpack.colorcontrol.views.dialog.Dialog;
import com.smartpack.colorcontrol.views.recyclerview.DescriptionView;
import com.smartpack.colorcontrol.views.recyclerview.RecyclerViewItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 01, 2020
 */

public class ProfileFragment extends RecyclerViewFragment {

    private AsyncTask<Void, Void, List<RecyclerViewItem>> mLoader;

    private Dialog mOptionsDialog;

    private String mEditProfile;
    private String mPath;

    @Override
    protected Drawable getTopFabDrawable() {
        return getResources().getDrawable(R.drawable.ic_add);
    }

    @Override
    protected boolean showTopFab() {
        return true;
    }

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.profiles),
                getString(R.string.profile_summary)));

        if (mOptionsDialog != null) {
            mOptionsDialog.show();
        }
    }

    @Override
    public int getSpanCount() {
        int span = Utils.isTablet(requireActivity()) ? Utils.getOrientation(getActivity()) ==
                Configuration.ORIENTATION_LANDSCAPE ? 4 : 3 : Utils.getOrientation(getActivity()) ==
                Configuration.ORIENTATION_LANDSCAPE ? 3 : 2;
        if (itemsSize() != 0 && span > itemsSize()) {
            span = itemsSize();
        }
        return span;
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (Utils.checkWriteStoragePermission(requireActivity())) {
            reload();
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    private void reload() {
        if (mLoader == null) {
            getHandler().postDelayed(new Runnable() {
                @SuppressLint("StaticFieldLeak")
                @Override
                public void run() {
                    clearItems();
                    mLoader = new AsyncTask<Void, Void, List<RecyclerViewItem>>() {

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            showProgress();
                        }

                        @Override
                        protected List<RecyclerViewItem> doInBackground(Void... voids) {
                            List<RecyclerViewItem> items = new ArrayList<>();
                            load(items);
                            return items;
                        }

                        @Override
                        protected void onPostExecute(List<RecyclerViewItem> recyclerViewItems) {
                            super.onPostExecute(recyclerViewItems);
                            for (RecyclerViewItem item : recyclerViewItems) {
                                addItem(item);
                            }
                            hideProgress();
                            mLoader = null;
                        }
                    };
                    mLoader.execute();
                }
            }, 250);
        }
    }

    private void load(List<RecyclerViewItem> items) {
        if (!Profile.ProfileFile().exists() || !Klapse.supported() && !ScreenColor.getInstance().supported()
                && !DRMColor.supported()) {
            return;
        }
        for (final String profileItems : Profile.profileItems()) {
            File profiles = new File(Profile.ProfileFile() + "/" + profileItems);
            if (Profile.ProfileFile().length() > 0 && Utils.getExtension(profiles.toString()).equals("sh")
                    && Profile.isColorConrolProfile(profiles.toString())) {
                DescriptionView descriptionView = new DescriptionView();
                descriptionView.setDrawable(ViewUtils.getColoredIcon(R.drawable.ic_color, requireActivity()));
                descriptionView.setMenuIcon(getResources().getDrawable(R.drawable.ic_dots));
                descriptionView.setSummary(profiles.getName().replace(".sh", ""));
                descriptionView.setOnItemClickListener(item -> applyProfile(profiles));
                descriptionView.setOnMenuListener((descriptionView1, popupMenu) -> {
                    Menu menu = popupMenu.getMenu();
                    menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.apply));
                    menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.details));
                    menu.add(Menu.NONE, 2, Menu.NONE, getString(R.string.edit));
                    menu.add(Menu.NONE, 3, Menu.NONE, getString(R.string.share));
                    menu.add(Menu.NONE, 4, Menu.NONE, getString(R.string.delete));
                    popupMenu.setOnMenuItemClickListener(item -> {
                        switch (item.getItemId()) {
                            case 0:
                                applyProfile(profiles);
                                break;
                            case 1:
                                new Dialog(requireActivity())
                                        .setTitle(profiles.getName().replace(".sh", ""))
                                        .setMessage(Profile.readProfile(profiles.toString()))
                                        .setPositiveButton(getString(R.string.cancel), (dialogInterface, i) -> {
                                        })
                                        .show();
                                break;
                            case 2:
                                mEditProfile = profiles.toString();
                                Intent intent = new Intent(getActivity(), EditorActivity.class);
                                intent.putExtra(EditorActivity.TITLE_INTENT, profiles);
                                intent.putExtra(EditorActivity.TEXT_INTENT, Profile.readProfile(profiles.toString()));
                                startActivityForResult(intent, 0);
                                break;
                            case 3:
                                Uri uriFile = FileProvider.getUriForFile(requireActivity(),
                                        BuildConfig.APPLICATION_ID + ".provider", new File(profiles.toString()));
                                Intent shareScript = new Intent(Intent.ACTION_SEND);
                                shareScript.setType("application/sh");
                                shareScript.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_by, profiles.getName()));
                                shareScript.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message) +
                                        (getString(R.string.share_app_message, BuildConfig.VERSION_NAME)));
                                shareScript.putExtra(Intent.EXTRA_STREAM, uriFile);
                                shareScript.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                startActivity(Intent.createChooser(shareScript, getString(R.string.share_with)));
                                Utils.getInstance().showInterstitialAd(requireActivity());
                                break;
                            case 4:
                                Utils.getInstance().showInterstitialAd(requireActivity());
                                new Dialog(requireActivity())
                                        .setMessage(getString(R.string.sure_question, profiles.getName().replace(".sh", "")))
                                        .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                                        })
                                        .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                                            Profile.deleteProfile(profiles.toString());
                                            reload();
                                        })
                                        .show();
                                break;
                        }
                        return false;
                    });
                });

                items.add(descriptionView);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void applyProfile(File file) {
        new Dialog(requireActivity())
                .setMessage(getString(R.string.apply_question, file.getName().replace(".sh", "")))
                .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                })
                .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                    if (!Profile.isColorConrolProfile(file.toString())) {
                        Utils.toast(getString(R.string.wrong_profile, file.getName().replace(".sh", "")), getActivity());
                        return;
                    }
                    new AsyncTask<Void, Void, String>() {
                        private ProgressDialog mProgressDialog;
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();

                            mProgressDialog = new ProgressDialog(getActivity());
                            mProgressDialog.setMessage(getString(R.string.applying_profile, file.getName().replace(".sh", "") + "..."));
                            mProgressDialog.setCancelable(false);
                            mProgressDialog.show();
                        }

                        @Override
                        protected String doInBackground(Void... voids) {
                            return Profile.applyProfile(file.toString());
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            super.onPostExecute(s);
                            try {
                                mProgressDialog.dismiss();
                            } catch (IllegalArgumentException ignored) {
                            }
                            Utils.getInstance().showInterstitialAd(requireActivity());
                            if (s != null && !s.isEmpty()) {
                                new Dialog(requireActivity())
                                        .setMessage(s)
                                        .setCancelable(false)
                                        .setPositiveButton(getString(R.string.cancel), (dialog, id) -> {
                                        })
                                        .show();
                            }
                        }
                    }.execute();
                })
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) return;
        if (requestCode == 0) {
            Profile.createProfile(mEditProfile, Objects.requireNonNull(data.getCharSequenceExtra(EditorActivity.TEXT_INTENT)).toString());
            reload();
        } else if (requestCode == 1) {
            Uri uri = data.getData();
            assert uri != null;
            File file = new File(Objects.requireNonNull(uri.getPath()));
            String fileName = file.getName();
            if (fileName.contains("primary")) {
                fileName = fileName.replace("primary:", "");
            }
            if (fileName.contains("file%3A%2F%2F%2F")) {
                fileName = fileName.replace("file%3A%2F%2F%2F", "").replace("%2F", "/");
            }
            if (fileName.contains("%2F")) {
                fileName = fileName.replace("%2F", "/");
            }
            if (Utils.isDocumentsUI(uri)) {
                @SuppressLint("Recycle") Cursor cursor = requireActivity().getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    mPath = Environment.getExternalStorageDirectory().toString() + "/Download/" +
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } else {
                mPath = Utils.getPath(file);
            }
            if (!Utils.getExtension(mPath).equals("sh")) {
                Utils.toast(getString(R.string.wrong_extension, ".sh"), getActivity());
                return;
            }
            if (!Profile.isColorConrolProfile(mPath)) {
                Utils.toast(getString(R.string.wrong_profile, file.getName().replace(".sh", "")), getActivity());
                return;
            }
            if (Utils.existFile(Profile.profileExistsCheck(fileName))) {
                Utils.toast(getString(R.string.profile_exists, file.getName()), getActivity());
                return;
            }
            Utils.getInstance().showInterstitialAd(requireActivity());
            Dialog selectQuestion = new Dialog(requireActivity());
            selectQuestion.setMessage(getString(R.string.select_question, fileName));
            selectQuestion.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
            });
            selectQuestion.setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                Profile.importProfile(mPath);
                reload();
            });
            selectQuestion.show();
        }
    }

    @Override
    protected void onTopFabClick() {
        super.onTopFabClick();

        if (!Utils.checkWriteStoragePermission(requireActivity())) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            Utils.toast(R.string.permission_denied_write_storage, getActivity());
            return;
        }

        if (!Klapse.supported() && !ScreenColor.getInstance().supported()
                && !DRMColor.supported()) {
            Utils.toast(getString(R.string.no_support, "KCAL/K-lapse"), getActivity());
            return;
        }

        mOptionsDialog = new Dialog(requireActivity()).setItems(getResources().getStringArray(
                R.array.profile_options), (dialogInterface, i) -> {
                    switch (i) {
                        case 0:
                            showCreateDialog();
                            break;
                        case 1:
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("*/*");
                            startActivityForResult(intent, 1);
                            break;
                    }
                }).setOnDismissListener(dialogInterface -> mOptionsDialog = null);
        mOptionsDialog.show();
    }

    private void showCreateDialog() {
        ViewUtils.dialogEditText("",
                (dialogInterface, i) -> {
                }, new ViewUtils.OnDialogEditTextListener() {
                    @SuppressLint("StaticFieldLeak")
                    @Override
                    public void onClick(String text) {
                        if (text.isEmpty()) {
                            Utils.toast(R.string.name_empty, getActivity());
                            return;
                        }
                        if (!text.endsWith(".sh")) {
                            text += ".sh";
                        }
                        if (text.contains(" ")) {
                            text = text.replace(" ", "_");
                        }
                        if (Utils.existFile(Profile.ProfileFile().toString() + text)) {
                            Utils.toast(getString(R.string.profile_exists, text), getActivity());
                            return;
                        }
                        final String path = text;
                        new AsyncTask<Void, Void, Void>() {
                            private ProgressDialog mProgressDialog;
                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                mProgressDialog = new ProgressDialog(getActivity());
                                mProgressDialog.setMessage("Exporting Screen Color settings into " + Profile.ProfileFile().toString());
                                mProgressDialog.setCancelable(false);
                                mProgressDialog.show();
                            }
                            @Override
                            protected Void doInBackground(Void... voids) {
                                if (!Utils.existFile(Profile.ProfileFile().toString())) {
                                    if (Profile.ProfileFile().exists () && Profile.ProfileFile().isFile ())
                                    {
                                        Profile.ProfileFile().delete ();
                                    }
                                    Profile.ProfileFile().mkdirs ();
                                }
                                Utils.create("#!/system/bin/sh\n\n# Created by Screen Color Control", Profile.ProfileFile().toString() + "/" + path);
                                if (ScreenColor.getInstance().supported()) {
                                    Utils.append("\n# Screen Color", Profile.ProfileFile().toString() + "/" + path);
                                    for (int i = 0; i < ScreenColor.size(); i++) {
                                        ScreenColor.exportColorSettings(path, i);
                                    }
                                }
                                if (DRMColor.supported()) {
                                    Utils.append("\n# KCAL", Profile.ProfileFile().toString() + "/" + path);
                                    for (int i = 0; i < DRMColor.size(); i++) {
                                        DRMColor.exportColorSettings(path, i);
                                    }
                                }
                                if (Klapse.supported()) {
                                    Utils.append("\n# K-lapse", Profile.ProfileFile().toString() + "/" + path);
                                    for (int i = 0; i < Klapse.size(); i++) {
                                        Klapse.exportKlapseSettings(path, i);
                                    }
                                }
                                Utils.append("\n# The END\necho \"Profile applied successfully...\" | tee /dev/kmsg", Profile.ProfileFile().toString() + "/" + path);
                                reload();
                                return null;
                            }
                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                try {
                                    mProgressDialog.dismiss();
                                } catch (IllegalArgumentException ignored) {
                                }
                            }
                        }.execute();
                    }
                }, getActivity()).setOnDismissListener(dialogInterface -> {
                }).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoader != null) {
            mLoader.cancel(true);
        }
    }

}