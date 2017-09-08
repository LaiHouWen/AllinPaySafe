package com.pax.ipp.tools.adapter.viewholder;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.pax.ipp.tools.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by houwen.lai on 2017/9/7.
 */

public class FlowViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.icon_app)
    AppCompatImageView mImageView;
    @BindView(R.id.name_app)
    AppCompatTextView name_app;
    @BindView(R.id.memory)
    AppCompatTextView name_detail;

    public FlowViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setImgIcon(Drawable icon) {
        mImageView.setImageDrawable(icon);
    }

    public void setNameApp(String name) {
        name_app.setText(name);
    }


    public void setNameDetail(String name_c) {
        name_detail.setText(name_c);
    }

    public AppCompatImageView getmImageView() {
        return mImageView;
    }
    public AppCompatTextView getName_app() {
        return name_app;
    }

    public AppCompatTextView getName_detail() {
        return name_detail;
    }

}
