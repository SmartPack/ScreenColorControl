package com.smartpack.colorcontrol.views.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.smartpack.colorcontrol.utils.ViewUtils;

/**
 * Adapted from https://github.com/Grarak/KernelAdiutor by Willi Ye.
 */

public class GenericSelectView extends ValueView {

    public interface OnGenericValueListener {
        void onGenericValueSelected(GenericSelectView genericSelectView, String value);
    }

    private String mValueRaw;
    private OnGenericValueListener mOnGenericValueListener;
    private int mInputType = -1;
    private boolean mShowDialog;

    @Override
    public void onRecyclerViewCreate(Activity activity) {
        super.onRecyclerViewCreate(activity);

        if (mShowDialog) {
            showDialog(activity);
        }
    }

    @Override
    public void onCreateView(View view) {
        view.setOnClickListener(v -> showDialog(v.getContext()));
        super.onCreateView(view);
    }

    private void setValueRaw(String value) {
        mValueRaw = value;
    }

    public void setOnGenericValueListener(OnGenericValueListener onGenericValueListener) {
        mOnGenericValueListener = onGenericValueListener;
    }

    public void setInputType(int inputType) {
        mInputType = inputType;
    }

    private void showDialog(Context context) {
        if (mValueRaw == null) {
            mValueRaw = getValue();
        }
        if (mValueRaw == null) return;

        mShowDialog = true;
        ViewUtils.dialogEditText(mValueRaw, (dialog, which) -> {
        }, text -> {
            setValueRaw(text);
            if (mOnGenericValueListener != null) {
                mOnGenericValueListener.onGenericValueSelected(GenericSelectView.this, text);
            }
        }, mInputType, context).setTitle(getTitle()).setOnDismissListener(
                dialog -> mShowDialog = false).show();
    }

}