/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of Screen Color Control, an app made to offer advanced control
 * over the screen colour of smart devices having KCAL/K-lapse support.
 *
 */

package com.smartpack.colorcontrol.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.smartpack.colorcontrol.R;
import com.smartpack.colorcontrol.utils.DRMColor;
import com.smartpack.colorcontrol.utils.EditorActivity;
import com.smartpack.colorcontrol.utils.Klapse;
import com.smartpack.colorcontrol.utils.Profile;
import com.smartpack.colorcontrol.utils.ScreenColor;
import com.smartpack.colorcontrol.utils.Utils;
import com.smartpack.colorcontrol.utils.ViewUtils;
import com.smartpack.colorcontrol.utils.root.RootUtils;
import com.smartpack.colorcontrol.views.dialog.Dialog;
import com.smartpack.colorcontrol.views.recyclerview.CardView;
import com.smartpack.colorcontrol.views.recyclerview.DescriptionView;
import com.smartpack.colorcontrol.views.recyclerview.RecyclerViewItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
        Drawable drawable = DrawableCompat.wrap(ContextCompat.getDrawable(getActivity(), R.drawable.ic_add));
        DrawableCompat.setTint(drawable, getResources().getColor(R.color.colorOnPrimary));
        return drawable;
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
        int span = Utils.isTablet(getActivity()) ? Utils.getOrientation(getActivity()) ==
                Configuration.ORIENTATION_LANDSCAPE ? 4 : 3 : Utils.getOrientation(getActivity()) ==
                Configuration.ORIENTATION_LANDSCAPE ? 3 : 2;
        if (itemsSize() != 0 && span > itemsSize()) {
            span = itemsSize();
        }
        return span;
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (Utils.checkWriteStoragePermission(getActivity())) {
            reload();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    private void reload() {
        if (mLoader == null) {
            getHandler().postDelayed(new Runnable() {
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
        for (final File profiles : Profile.ProfileFile().listFiles()) {
            if (Profile.ProfileFile().length() > 0 && Utils.getExtension(profiles.toString()).equals("sh")
                    && Profile.isColorConrolProfile(profiles.toString())) {
                CardView cardView = new CardView(getActivity());
                cardView.setOnMenuListener(new CardView.OnMenuListener() {
                    @Override
                    public void onMenuReady(CardView cardView, PopupMenu popupMenu) {
                        Menu menu = popupMenu.getMenu();
                        menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.apply));
                        menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.edit));
                        menu.add(Menu.NONE, 2, Menu.NONE, getString(R.string.delete));

                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case 0:
                                        new Dialog(getActivity())
                                                .setMessage(getString(R.string.apply_question, profiles.getName().replace(".sh", "")))
                                                .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                                                })
                                                .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                                                    if (!Profile.isColorConrolProfile(profiles.toString())) {
                                                        Utils.toast(getString(R.string.wrong_profile, profiles.getName().replace(".sh", "")), getActivity());
                                                        return;
                                                    }
                                                    new AsyncTask<Void, Void, String>() {
                                                        private ProgressDialog mProgressDialog;
                                                        @Override
                                                        protected void onPreExecute() {
                                                            super.onPreExecute();

                                                            mProgressDialog = new ProgressDialog(getActivity());
                                                            mProgressDialog.setMessage(getString(R.string.applying_profile, profiles.getName().replace(".sh", "") + "..."));
                                                            mProgressDialog.setCancelable(false);
                                                            mProgressDialog.show();
                                                        }

                                                        @Override
                                                        protected String doInBackground(Void... voids) {
                                                            return Profile.applyProfile(profiles.toString());
                                                        }

                                                        @Override
                                                        protected void onPostExecute(String s) {
                                                            super.onPostExecute(s);
                                                            try {
                                                                mProgressDialog.dismiss();
                                                            } catch (IllegalArgumentException ignored) {
                                                            }
                                                            if (s != null && !s.isEmpty()) {
                                                                new Dialog(getActivity())
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
                                        break;
                                    case 1:
                                        mEditProfile = profiles.toString();
                                        Intent intent = new Intent(getActivity(), EditorActivity.class);
                                        intent.putExtra(EditorActivity.TITLE_INTENT, profiles);
                                        intent.putExtra(EditorActivity.TEXT_INTENT, Profile.readProfile(profiles.toString()));
                                        startActivityForResult(intent, 0);
                                        break;
                                    case 2:
                                        new Dialog(getActivity())
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
                            }
                        });
                    }
                });
                DescriptionView descriptionView = new DescriptionView();
                descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_color));
                descriptionView.setSummary(profiles.getName().replace(".sh", ""));
                descriptionView.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                    @Override
                    public void onClick(RecyclerViewItem item) {
                        new Dialog(getActivity())
                                .setTitle(profiles.getName().replace(".sh", ""))
                                .setMessage(Profile.readProfile(profiles.toString()))
                                .setPositiveButton(getString(R.string.cancel), (dialogInterface, i) -> {
                                })
                                .show();
                    }
                });

                cardView.addItem(descriptionView);
                items.add(cardView);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) return;
        if (requestCode == 0) {
            Profile.createProfile(mEditProfile, data.getCharSequenceExtra(EditorActivity.TEXT_INTENT).toString());
            reload();
        } else if (requestCode == 1) {
            Uri uri = data.getData();
            File file = new File(uri.getPath());
            mPath = Utils.getPath(file);
            if (Utils.isDocumentsUI(uri)) {
                Dialog dialogueDocumentsUI = new Dialog(getActivity());
                dialogueDocumentsUI.setMessage(getString(R.string.documentsui_message));
                dialogueDocumentsUI.setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {
                });
                dialogueDocumentsUI.show();
                return;
            }
            if (!Utils.getExtension(file.getName()).equals("sh")) {
                Utils.toast(getString(R.string.wrong_extension, ".sh"), getActivity());
                return;
            }
            if (!Profile.isColorConrolProfile(mPath)) {
                Utils.toast(getString(R.string.wrong_profile, file.getName().replace(".sh", "")), getActivity());
                return;
            }
            if (Utils.existFile(Profile.profileExistsCheck(file.getName()))) {
                Utils.toast(getString(R.string.profile_exists, file.getName()), getActivity());
                return;
            }
            Dialog selectQuestion = new Dialog(getActivity());
            selectQuestion.setMessage(getString(R.string.select_question, file.getName()));
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

        if (!Utils.checkWriteStoragePermission(getActivity())) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            Utils.toast(R.string.permission_denied_write_storage, getActivity());
            return;
        }

        if (!Klapse.supported() && !ScreenColor.getInstance().supported()
                && !DRMColor.supported()) {
            Utils.toast(getString(R.string.no_support, "KCAL/K-lapse"), getActivity());
            return;
        }

        mOptionsDialog = new Dialog(getActivity()).setItems(getResources().getStringArray(
                R.array.profile_options), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
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
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mOptionsDialog = null;
            }
        });
        mOptionsDialog.show();
    }

    private void showCreateDialog() {
        ViewUtils.dialogEditText("",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }, new ViewUtils.OnDialogEditTextListener() {
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
                                RootUtils.runCommand ("echo '#!/system/bin/sh\n\n# Created by Screen Color Control' > "
                                        + Profile.ProfileFile().toString() + "/" + path);
                                if (ScreenColor.getInstance().supported()) {
                                    RootUtils.runCommand("echo '\n# Screen Color" + "' >> " + Profile.ProfileFile().toString() + "/" + path);
                                    for (int i = 0; i < ScreenColor.getInstance().size(); i++) {
                                        ScreenColor.getInstance().exportColorSettings(path, i);
                                    }
                                }
                                if (DRMColor.supported()) {
                                    RootUtils.runCommand("echo '\n# KCAL" + "' >> " + Profile.ProfileFile().toString() + "/" + path);
                                    for (int i = 0; i < DRMColor.size(); i++) {
                                        DRMColor.exportColorSettings(path, i);
                                    }
                                }
                                if (Klapse.supported()) {
                                    RootUtils.runCommand("echo '\n# K-lapse" + "' >> " + Profile.ProfileFile().toString() + "/" + path);
                                    for (int i = 0; i < Klapse.size(); i++) {
                                        Klapse.exportKlapseSettings(path, i);
                                    }
                                }
                                RootUtils.runCommand ("echo '" +
                                        "\n# The END\necho \"Profile applied successfully...\" | tee /dev/kmsg" + "' >> " + Profile.ProfileFile().toString() + "/" + path);
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
                }, getActivity()).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
            }
        }).show();
    }

}