package com.smartpack.colorcontrol.views.recyclerview;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.smartpack.colorcontrol.R;

/**
 * Adapted from https://github.com/Grarak/KernelAdiutor by Willi Ye.
 */

public abstract class ValueView extends RecyclerViewItem {

    private TextView mTitleView;
    private TextView mSummaryView;
    private View mValueParent;
    private TextView mValueView;
    private View mProgress;

    private CharSequence mTitle;
    private CharSequence mSummary;
    private String mValue;
    private int mValuesRes;

    @Override
    public int getLayoutRes() {
        return R.layout.rv_value_view;
    }

    @Override
    public void onCreateView(View view) {
        mTitleView = view.findViewById(R.id.title);
        mSummaryView = view.findViewById(R.id.summary);
        mValueParent = view.findViewById(R.id.value_parent);
        mValueView = view.findViewById(R.id.value);
        mProgress = view.findViewById(R.id.progress);

        super.onCreateView(view);
    }

    public void setTitle(CharSequence title) {
        mTitle = title;
        refresh();
    }

    public void setSummary(CharSequence summary) {
        mSummary = summary;
        refresh();
    }

    public void setValue(String value) {
        mValue = value;
        refresh();
    }

    public void setValue(@StringRes int value) {
        mValuesRes = value;
        refresh();
    }

    public CharSequence getTitle() {
        return mTitle;
    }

    public String getValue() {
        return mValue;
    }

    @Override
    protected void refresh() {
        super.refresh();

        if (mTitleView != null) {
            if (mTitle != null) {
                mTitleView.setText(mTitle);
                mTitleView.setVisibility(View.VISIBLE);
            } else {
                mTitleView.setVisibility(View.GONE);
            }
        }

        if (mSummaryView != null && mSummary != null) {
            mSummaryView.setText(mSummary);
        }

        if (mValueView != null && (mValue != null || mValuesRes != 0)) {
            if (mValue == null) {
                mValue = mValueView.getContext().getString(mValuesRes);
            }
            mValueView.setText(mValue);
            mValueView.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.GONE);
            mValueParent.setVisibility(mValue.isEmpty() ? View.GONE : View.VISIBLE);
        }
    }

}
