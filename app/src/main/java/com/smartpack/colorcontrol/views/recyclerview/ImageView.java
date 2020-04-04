package com.smartpack.colorcontrol.views.recyclerview;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;

import com.smartpack.colorcontrol.R;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 01, 2020
 * Based on the implementation of ImageView on https://github.com/morogoku/MTweaks-KernelAdiutorMOD by Morogoku.
 * Ref: https://github.com/morogoku/MTweaks-KernelAdiutorMOD/blob/master/app/src/main/java/com/moro/mtweaks/views/recyclerview/ImageView.java
 */
public class ImageView extends RecyclerViewItem {

    private AppCompatImageView mImageView;

    private LinearLayout.LayoutParams mLp;
    private Drawable mImage;

    @Override
    public int getLayoutRes() {
        return R.layout.rv_image_view;
    }

    @Override
    public void onCreateView(View view) {
        super.onCreateView(view);

        mImageView = view.findViewById(R.id.image);

        super.onCreateView(view);

    }

    public void setDrawable(Drawable drawable) {
        mImage = drawable;
        refresh();
    }

    public void setLayoutParams(int width, int height){
        mLp = new LinearLayout.LayoutParams(width, height);
        refresh();
    }

    @Override
    protected void refresh() {
        super.refresh();

        if (mImageView != null && mImage != null) {
            mImageView.setImageDrawable(mImage);
            if (mLp != null) {
                mImageView.setLayoutParams(mLp);
            }
        }
    }

}