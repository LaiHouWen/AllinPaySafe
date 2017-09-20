package com.pax.ipp.tools.adapter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.pax.ipp.tools.R;
import com.pax.ipp.tools.adapter.base.BaseRecyclerViewAdapter;
import com.pax.ipp.tools.adapter.viewholder.ProcessItemViewHolder;
import com.pax.ipp.tools.model.CacheListItem;
import com.pax.ipp.tools.utils.LogUtil;
import com.pax.ipp.tools.utils.TextFormater;

import java.util.List;

/**
 * Created by  on 2016/5/16.
 * 内存适配器
 */
public class CacheListAdapter extends BaseRecyclerViewAdapter<CacheListItem> {
    private Context mContext;

    private Boolean[] flagArray = new Boolean[]{};

    public void setFlagAllTrue(boolean flag){
        if (flagArray==null)return;
        for (int i=0;i<flagArray.length;i++){
            flagArray[i]=flag;
        }
    }

    public CacheListAdapter(List<CacheListItem> list) {
        super(list);
        if (list!=null) {
            flagArray = new Boolean[list.size()];
            setFlagAllTrue(false);
        }
    }


    public CacheListAdapter(List<CacheListItem> list, Context context) {
        super(list, context);
        if (list!=null) {
            flagArray = new Boolean[list.size()];
            setFlagAllTrue(false);
        }
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
        holder.setCheckBoxChangeListern(null);
        holder.setChecked(flagArray[position]);//cacheListItem.getIsChoise()
        LogUtil.i("cacheList position="+position+" flag="+flagArray[position]);
        holder.setCheckBoxChangeListern(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LogUtil.i("cacheListAdapter  position="+position+"  is="+isChecked);
                if (list.size()>position){
                    flagArray[position]=isChecked;
                    list.get(position).setIsChoise(isChecked);

//                    notifyDataSetChanged();
                }
            }
        });

//        animate(viewHolder, position);
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
