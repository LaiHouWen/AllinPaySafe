package com.pax.ipp.tools.adapter.viewholder;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pax.ipp.tools.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 */
public class MenuItemViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.icon) ImageView mImageView;
    @BindView(R.id.content) TextView mTextView;
    //ImageView mImageView;
    //TextView mTextView;


    public MenuItemViewHolder(View parent) {
        super(parent);
        ButterKnife.bind(this, parent);
        mImageView = (ImageView) parent.findViewById(R.id.icon);
        mTextView = (TextView) parent.findViewById(R.id.content);
    }


    public void setIcon(Drawable icon) {
        mImageView.setImageDrawable(icon);
    }


    public void setContent(String content) {
        mTextView.setText(content);
    }
}
