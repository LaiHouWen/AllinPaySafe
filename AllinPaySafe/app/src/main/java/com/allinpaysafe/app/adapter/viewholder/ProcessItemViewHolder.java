package com.allinpaysafe.app.adapter.viewholder;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.allinpaysafe.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by towave on 2016/5/16.
 *
 */
public class ProcessItemViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.icon) ImageView mImageView;
//    @BindView(R.id.name) TextView mTextView;
//    @BindView(R.id.memory) TextView mTextView2;
//    @BindView(R.id.is_clean) CheckBox mCheckBox;

    public ProcessItemViewHolder(View parent) {
        super(parent);
        ButterKnife.bind(this, parent);
    }


    public void setIcon(Drawable icon) {
        mImageView.setImageDrawable(icon);
    }


//    public void setName(String name) {
//        mTextView.setText(name);
//    }
//
//
//    public void setMemory(String memory) {
//        mTextView2.setText(memory);
//    }
//
//
//    public void setChecked(boolean checked) {
//        mCheckBox.setChecked(checked);
//    }
//
//
//    public void setCheckBoxVisible(boolean visible) {
//        mCheckBox.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
//    }
//
//
//    public void setMemoryVisible(boolean visible) {
//        mTextView2.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
//    }
}
