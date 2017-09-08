package com.pax.ipp.tools.adapter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pax.ipp.tools.R;
import com.pax.ipp.tools.adapter.base.BaseRecyclerViewAdapter;
import com.pax.ipp.tools.adapter.viewholder.FlowViewHolder;
import com.pax.ipp.tools.adapter.viewholder.ProcessItemViewHolder;
import com.pax.ipp.tools.model.CacheListItem;
import com.pax.ipp.tools.model.FlowModel;
import com.pax.ipp.tools.utils.TextFormater;

import java.util.List;

/**
 * Created by houwen.lai on 2017/9/7.
 */

public class FlowListAdapter extends BaseRecyclerViewAdapter<FlowModel> {


    public FlowListAdapter(List<FlowModel> list) {
        super(list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        final View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_list_flow,
                        parent, false);
        return new FlowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        FlowViewHolder holder = (FlowViewHolder) viewHolder;
        FlowModel flowModel = list.get(position);
        if (flowModel == null) return;
        holder.setImgIcon(flowModel.getmIcon());
        holder.setNameApp(flowModel.getmApplicationName());
        holder.setNameDetail(
                TextFormater.dataSizeFormat(flowModel.getFlowSize()));

        animate(viewHolder, position);//动画
    }

    @Override
    protected Animator[] getAnimators(View view) {
        if (view.getMeasuredHeight() <= 0) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX",
                    1.05f, 1.0f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY",
                    1.05f, 1.0f);
            return new ObjectAnimator[] { scaleX, scaleY };
        }
        return new Animator[] {
                ObjectAnimator.ofFloat(view, "scaleX", 1.05f, 1.0f),
                ObjectAnimator.ofFloat(view, "scaleY", 1.05f, 1.0f), };
    }
}
