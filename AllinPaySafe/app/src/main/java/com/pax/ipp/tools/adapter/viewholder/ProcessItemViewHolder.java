package com.pax.ipp.tools.adapter.viewholder;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.pax.ipp.tools.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by towave on 2016/5/16.
 *
 */
public class ProcessItemViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.icon_app)
    AppCompatImageView mImageView;
    @BindView(R.id.name_app)
    AppCompatTextView mTextView;
    @BindView(R.id.memory) AppCompatTextView mTextView2;
    @BindView(R.id.is_clean)
    AppCompatCheckBox mCheckBox;

    public ProcessItemViewHolder(View parent) {
        super(parent);
        ButterKnife.bind(this, parent);
    }


    public void setIcon(Drawable icon) {
        mImageView.setImageDrawable(icon);
    }


    public void setName(String name) {
        mTextView.setText(name);
    }


    public void setMemory(String memory) {
        mTextView2.setText(memory);
    }


    public void setChecked(boolean checked) {
        mCheckBox.setChecked(checked);
    }


    public void setCheckBoxVisible(boolean visible) {
        mCheckBox.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    public void setMemoryVisible(boolean visible) {
        mTextView2.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }
    public void setCheckBoxChangeListern(CompoundButton.OnCheckedChangeListener listener) {
        mCheckBox.setOnCheckedChangeListener(listener);
    }
}
