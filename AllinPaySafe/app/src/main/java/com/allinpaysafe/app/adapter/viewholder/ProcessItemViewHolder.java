package com.allinpaysafe.app.adapter.viewholder;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import edu.wkd.towave.memorycleaner.R;

/**
 * Created by towave on 2016/5/16.
 */
public class ProcessItemViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.icon) ImageView mImageView;
    @Bind(R.id.name) TextView mTextView;
    @Bind(R.id.memory) TextView mTextView2;
    @Bind(R.id.is_clean) CheckBox mCheckBox;

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
}
