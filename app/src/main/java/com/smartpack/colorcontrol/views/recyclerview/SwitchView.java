package com.smartpack.colorcontrol.views.recyclerview;

import android.view.View;
import android.widget.CompoundButton;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;

import com.smartpack.colorcontrol.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapted from https://github.com/Grarak/KernelAdiutor by Willi Ye.
 */

public class SwitchView extends RecyclerViewItem {

    public interface OnSwitchListener {
        void onChanged(SwitchView switchView, boolean isChecked);
    }

    private AppCompatTextView mTitle;
    private AppCompatTextView mSummary;
    private SwitchCompat mSwitcher;

    private CharSequence mTitleText;
    private CharSequence mSummaryText;
    private boolean mChecked;

    private List<OnSwitchListener> mOnSwitchListeners = new ArrayList<>();

    @Override
    public int getLayoutRes() {
        return R.layout.rv_switch_view;
    }

    @Override
    public void onCreateView(View view) {
        mTitle = view.findViewById(R.id.title);
        mSummary = view.findViewById(R.id.summary);
        mSwitcher = view.findViewById(R.id.switcher);

        super.onCreateView(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwitcher.setChecked(!mChecked);
            }
        });
        mSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mChecked = isChecked;
                List<OnSwitchListener> applied = new ArrayList<>();
                for (OnSwitchListener onSwitchListener : mOnSwitchListeners) {
                    if (applied.indexOf(onSwitchListener) == -1) {
                        onSwitchListener.onChanged(SwitchView.this, isChecked);
                        applied.add(onSwitchListener);
                    }
                }
            }
        });
    }

    public void setTitle(CharSequence title) {
        mTitleText = title;
        refresh();
    }

    public void setSummary(CharSequence summary) {
        mSummaryText = summary;
        refresh();
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
        refresh();
    }

    public CharSequence getTitle() {
        return mTitleText;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void addOnSwitchListener(OnSwitchListener onSwitchListener) {
        mOnSwitchListeners.add(onSwitchListener);
    }

    @Override
    protected void refresh() {
        super.refresh();
        if (mTitle != null) {
            if (mTitleText != null) {
                mTitle.setText(mTitleText);
                mTitle.setVisibility(View.VISIBLE);
            } else {
                mTitle.setVisibility(View.GONE);
            }
        }
        if (mSummary != null && mSummaryText != null) {
            mSummary.setText(mSummaryText);
        }
        if (mSwitcher != null) {
            mSwitcher.setChecked(mChecked);
        }
    }
}
