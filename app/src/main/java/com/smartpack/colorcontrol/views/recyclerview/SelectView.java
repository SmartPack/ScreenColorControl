package com.smartpack.colorcontrol.views.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.smartpack.colorcontrol.R;
import com.smartpack.colorcontrol.views.dialog.Dialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapted from https://github.com/Grarak/KernelAdiutor by Willi Ye.
 */

public class SelectView extends ValueView {

    public interface OnItemSelected {
        void onItemSelected(SelectView selectView, int position, String item);
    }

    private View mView;
    private OnItemSelected mOnItemSelected;
    private Dialog mDialog;
    private List<String> mItems = new ArrayList<>();

    @Override
    public void onRecyclerViewCreate(Activity activity) {
        super.onRecyclerViewCreate(activity);

        if (mDialog != null) {
            mDialog.show();
        }
    }

    @Override
    public void onCreateView(View view) {
        mView = view;
        super.onCreateView(view);
    }

    public void setItem(String item) {
        setValue(item);
    }

    public void setItem(int position) {
        if (position >= 0 && position < mItems.size()) {
            setValue(mItems.get(position));
        } else {
            setValue(R.string.not_in_range);
        }
    }

    public void setItems(List<String> items) {
        mItems = items;
        refresh();
    }

    public void setOnItemSelected(OnItemSelected onItemSelected) {
        mOnItemSelected = onItemSelected;
    }

    private void showDialog(Context context) {
        String[] items = mItems.toArray(new String[0]);

        mDialog = new Dialog(context).setItems(items, (dialog, which) -> {
            setItem(which);
            if (mOnItemSelected != null) {
                mOnItemSelected.onItemSelected(SelectView.this, which, mItems.get(which));
            }
        }).setOnDismissListener(dialog -> mDialog = null);
        if (getTitle() != null) {
            mDialog.setTitle(getTitle());
        }
        mDialog.show();
    }

    @Override
    protected void refresh() {
        super.refresh();

        if (mView != null && getValue() != null) {
            mView.setOnClickListener(v -> showDialog(v.getContext()));
        }
    }

}