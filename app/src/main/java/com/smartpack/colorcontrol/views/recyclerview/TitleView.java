package com.smartpack.colorcontrol.views.recyclerview;

import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import com.smartpack.colorcontrol.R;

/**
 * Adapted from https://github.com/Grarak/KernelAdiutor by Willi Ye.
 */

public class TitleView extends RecyclerViewItem {

    private AppCompatTextView mTitle;

    private CharSequence mTitleText;

    @Override
    public int getLayoutRes() {
        return R.layout.rv_title_view;
    }

    @Override
    public void onCreateView(View view) {
        mTitle = view.findViewById(R.id.title);

        setFullSpan(true);
        super.onCreateView(view);
    }

    public void setText(CharSequence text) {
        mTitleText = text;
        refresh();
    }

    @Override
    protected void refresh() {
        super.refresh();
        if (mTitle != null && mTitleText != null) {
            mTitle.setText(mTitleText);
        }
    }

    @Override
    protected boolean cardCompatible() {
        return false;
    }
}
