package com.smartpack.colorcontrol.fragments;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.smartpack.colorcontrol.R;
import com.smartpack.colorcontrol.utils.Utils;

/**
 * Adapted from https://github.com/Grarak/KernelAdiutor by Willi Ye.
 */

public class DescriptionFragment extends BaseFragment {

    static DescriptionFragment newInstance(CharSequence title, CharSequence summary) {
        Bundle args = new Bundle();
        DescriptionFragment fragment = new DescriptionFragment();
        args.putCharSequence("title", title);
        args.putCharSequence("summary", summary);
        fragment.setArguments(args);
        return fragment;
    }

    private TextView mTitleView;
    private TextView mSummaryView;

    private CharSequence mTitle;
    private CharSequence mSummary;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_description, container, false);

        mTitleView = rootView.findViewById(R.id.title);
        mSummaryView = rootView.findViewById(R.id.summary);

        if (Utils.isTv(requireActivity())) {
            mSummaryView.setFocusable(true);
        } else {
            mTitleView.setTextIsSelectable(true);
            mSummaryView.setTextIsSelectable(true);
        }

        mSummaryView.setSelected(true);
        mSummaryView.setMovementMethod(LinkMovementMethod.getInstance());

        assert getArguments() != null;
        mTitle = getArguments().getCharSequence("title");
        mSummary = getArguments().getCharSequence("summary");

        refresh();
        return rootView;
    }

    public void setTitle(CharSequence title) {
        mTitle = title;
        refresh();
    }

    private void refresh() {
        if (mTitleView != null) {
            if (mTitle != null) {
                mTitleView.setFocusable(false);
                mTitleView.setText(mTitle);
                mTitleView.setVisibility(View.VISIBLE);
            } else {
                mTitleView.setVisibility(View.GONE);
            }
        }

        if (mSummaryView != null) {
            if (mSummary != null) {
                mSummaryView.setText(mSummary);
                mSummaryView.setVisibility(View.VISIBLE);
            } else {
                mSummaryView.setVisibility(View.GONE);
            }
        }
    }

}