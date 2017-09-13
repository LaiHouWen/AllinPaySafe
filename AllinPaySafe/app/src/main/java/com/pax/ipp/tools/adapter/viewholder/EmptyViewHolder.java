package com.pax.ipp.tools.adapter.viewholder;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pax.ipp.tools.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by houwen.lai on 2017/9/13.
 */

public class EmptyViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.img_empty_flow)
    ImageView img_empty_flow;
    @BindView(R.id.tv_empty_text)
    TextView tv_empty_text;

    public EmptyViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }



}
