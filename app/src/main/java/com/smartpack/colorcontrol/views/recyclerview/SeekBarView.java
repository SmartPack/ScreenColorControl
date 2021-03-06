package com.smartpack.colorcontrol.views.recyclerview;

import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import com.smartpack.colorcontrol.R;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapted from https://github.com/Grarak/KernelAdiutor by Willi Ye.
 */

public class SeekBarView extends RecyclerViewItem {

    public interface OnSeekBarListener {
        void onStop(SeekBarView seekBarView, int position, String value);

        void onMove(SeekBarView seekBarView, int position, String value);
    }

    private AppCompatTextView mTitle;
    private AppCompatTextView mSummary;
    private AppCompatTextView mValue;
    private DiscreteSeekBar mSeekBar;

    private CharSequence mTitleText;
    private CharSequence mSummaryText;

    private int mMin;
    private int mMax = 100;
    private int mProgress;
    private String mUnit;
    private List<String> mItems;
    private int mOffset = 1;
    private boolean mEnabled = true;

    private OnSeekBarListener mOnSeekBarListener;

    @Override
    public int getLayoutRes() {
        return R.layout.rv_seekbar_view;
    }

    @Override
    public void onCreateView(final View view) {
        mTitle = view.findViewById(R.id.title);
        mSummary = view.findViewById(R.id.summary);
        mValue = view.findViewById(R.id.value);
        mSeekBar = view.findViewById(R.id.seekbar);

        view.findViewById(R.id.button_minus).setOnClickListener(v -> {
            mSeekBar.setProgress(mSeekBar.getProgress() - 1);
            if (mOnSeekBarListener != null && mProgress < mItems.size() && mProgress >= 0) {
                mOnSeekBarListener.onStop(SeekBarView.this, mProgress, mItems.get(mProgress));
            }
        });
        view.findViewById(R.id.button_plus).setOnClickListener(v -> {
            mSeekBar.setProgress(mSeekBar.getProgress() + 1);
            if (mOnSeekBarListener != null && mProgress < mItems.size() && mProgress >= 0) {
                mOnSeekBarListener.onStop(SeekBarView.this, mProgress, mItems.get(mProgress));
            }
        });

        mSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                if (value < mItems.size() && value >= 0) {
                    mProgress = value;
                    String text = mItems.get(value);
                    if (mUnit != null) text += mUnit;
                    mValue.setText(text);
                    if (mOnSeekBarListener != null) {
                        mOnSeekBarListener.onMove(SeekBarView.this, mProgress, mItems.get(mProgress));
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                try {
                    if (mOnSeekBarListener != null) {
                        mOnSeekBarListener.onStop(SeekBarView.this, mProgress, mItems.get(mProgress));
                    }
                } catch (Exception ignored) {
                }
            }
        });
        mSeekBar.setFocusable(false);

        super.onCreateView(view);
    }

    public void setTitle(CharSequence title) {
        mTitleText = title;
        refresh();
    }

    public void setSummary(CharSequence summary) {
        mSummaryText = summary;
        refresh();
    }

    public void setProgress(int progress) {
        mProgress = progress;
        refresh();
    }

    public void setMin(int min) {
        mMin = min;
        mItems = null;
        refresh();
    }

    public void setUnit(String unit) {
        mUnit = unit;
        mItems = null;
        refresh();
    }

    public void setMax(int max) {
        mMax = max;
        mItems = null;
        refresh();
    }

    public void setItems(List<String> items) {
        mItems = items;
        refresh();
    }

    public void setOffset(int offset) {
        mOffset = offset;
        mItems = null;
        refresh();
    }

    public void setEnabled(boolean enable) {
        mEnabled = enable;
        refresh();
    }

    public int getProgress() {
        return mProgress;
    }

    public void setOnSeekBarListener(OnSeekBarListener onSeekBarListener) {
        mOnSeekBarListener = onSeekBarListener;
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
        if (mItems == null) {
            mItems = new ArrayList<>();
            for (int i = mMin; i <= mMax; i += mOffset) {
                mItems.add(String.valueOf(i));
            }
        }
        if (mSeekBar != null) {
            mSeekBar.setMax(mItems.size() - 1);
            mSeekBar.setMin(0);
            mSeekBar.setEnabled(mEnabled);
            if (mValue != null) {
                try {
                    String text = mItems.get(mProgress);
                    mSeekBar.setProgress(mProgress);
                    if (mUnit != null) text += mUnit;
                    mValue.setText(text);
                } catch (Exception ignored) {
                    mValue.setText(mValue.getResources().getString(R.string.not_in_range));
                }
            }
        }
    }

}