package com.allinpaysafe.app.adapter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.allinpaysafe.app.R;
import com.allinpaysafe.app.adapter.base.BaseRecyclerViewAdapter;
import com.allinpaysafe.app.adapter.viewholder.ProcessItemViewHolder;
import com.allinpaysafe.app.model.CacheListItem;
import com.allinpaysafe.app.utils.TextFormater;

import java.util.List;

/**
 * Created by  on 2016/5/16.
 *
 */
public class CacheListAdapter extends BaseRecyclerViewAdapter<CacheListItem> {
    private Context mContext;


    public CacheListAdapter(List<CacheListItem> list) {
        super(list);
    }


    public CacheListAdapter(List<CacheListItem> list, Context context) {
        super(list, context);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        final View view = LayoutInflater.from(mContext)
                                        .inflate(R.layout.item_list_view,
                                                parent, false);
        return new ProcessItemViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        ProcessItemViewHolder holder = (ProcessItemViewHolder) viewHolder;
        CacheListItem cacheListItem = list.get(position);
        if (cacheListItem == null) return;
        holder.setIcon(cacheListItem.getApplicationIcon());
        holder.setName(cacheListItem.getApplicationName());
        holder.setMemory(
                TextFormater.dataSizeFormat(cacheListItem.getCacheSize()));
        holder.setCheckBoxVisible(true);
        animate(viewHolder, position);
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
