package com.allinpaysafe.app.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.allinpaysafe.app.R;
import com.allinpaysafe.app.adapter.CacheListAdapter;
import com.allinpaysafe.app.model.CacheListItem;
import com.allinpaysafe.app.mvp.impl.CircularLoaderView;
import com.allinpaysafe.app.mvp.impl.RubbishCleanView;
import com.allinpaysafe.app.mvp.presenter.RubbishCleanPresenter;
import com.allinpaysafe.app.ui.base.BaseActivity;
import com.allinpaysafe.app.ui.base.RubbishActivity;
import com.allinpaysafe.app.utils.AppUtils;
import com.allinpaysafe.app.utils.BtnUtils;
import com.allinpaysafe.app.utils.LogUtil;
import com.allinpaysafe.app.utils.TextFormater;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by houwen.lai on 2017/9/4.
 *
 * 内存列表
 *BaseActivity implements RubbishCleanView
 */

public class MeoryClearActivity extends RubbishActivity{

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView toolbar_title;

    @BindView(R.id.tv_ariable_meory)
    TextView tv_ariable_meory;
    @BindView(R.id.tv_ariable_meory_t)
    TextView tv_ariable_meory_t;//MB
    @BindView(R.id.tv_meory_total)
    TextView tv_meory_total;

    @BindView(R.id.tv_choise_apps)
    TextView tv_choise_apps;
    @BindView(R.id.checbox_all)
    CheckBox checbox_all;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.listview_meory)
    ListView listview_meory;

    @BindView(R.id.btn_lear_meory)
    Button btn_lear_meory;

    RubbishCleanPresenter mRubbishCleanPresenter;
    StringBuffer bf=new StringBuffer();

    CacheListAdapter recyclerAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.meory_clear;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        toolbar_title.setText(getString(R.string.text_clear_memery));
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        checbox_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setDateNotify(isChecked);
            }
        });

        mRubbishCleanPresenter = new RubbishCleanPresenter(this);
        mRubbishCleanPresenter.attachView(this);
        mRubbishCleanPresenter.onCreate(savedInstanceState);

    }

    @Override public void onDestroy() {
        super.onDestroy();
        mRubbishCleanPresenter.onDestroy();
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.btn_lear_meory})
    void onClick(View view){
        switch (view.getId()){
            case R.id.btn_lear_meory://一键清理
                if (BtnUtils.isFastDoubleClick())
                mRubbishCleanPresenter.cleanCache();
                break;
        }
    }

    @Override
    public void initViews(CacheListAdapter recyclerAdapter, Context context, ItemTouchHelper itemTouchHelper) {
        this.recyclerAdapter = recyclerAdapter;
        recyclerView.setLayoutManager(
                new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
                        false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerAdapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    @Override
    public void onScanStarted(Context context) {
        tv_ariable_meory.setText("0");
        tv_ariable_meory_t.setText("KB");
        long sum = AppUtils.getTotalMemory();
        long available = AppUtils.getAvailMemory(mContext);
        float percent = AppUtils.getPercent(mContext);
        tv_meory_total.setText(TextFormater.dataSizeFormat(sum - available) + "/" +
                TextFormater.dataSizeFormat(sum));//可用内存/总内存

    }

    @Override
    public void onScanProgressUpdated(Context context, int current, int max, long cacheSize, String packageName) {

        LogUtil.d("onScan=current="+current+"\tmax="+max+"\tcacheSize="+cacheSize+"\tpackageName="+packageName);

        tv_ariable_meory.setText(TextFormater.dataSizeFormatArray(cacheSize)[0]);
        tv_ariable_meory_t.setText(TextFormater.dataSizeFormatArray(cacheSize)[1]);

//        mTextView.setText("正在扫描:" + current + "/" + max + " 包名:" +
//                packageName);
//        float percent = (int) (1.0 * current / max * 100);
//        mProgressBar.setProgress((int) percent);
    }

//    @Override
//    public void onScanProgressUpdated(Context context, int current, int max, long cacheSize, String packageName, CacheListItem item) {
//        LogUtil.d("onScan=current="+current+"\tmax="+max+"\tcacheSize="+cacheSize+"\tpackageName="+packageName);
//
//        tv_ariable_meory.setText(TextFormater.dataSizeFormatArray(cacheSize)[0]);
//        tv_ariable_meory_t.setText(TextFormater.dataSizeFormatArray(cacheSize)[1]);
//
//        if (recyclerAdapter==null)return;
//        recyclerAdapter.add(item);
//        recyclerAdapter.notifyDataSetChanged();
//        recyclerView.smoothScrollToPosition(recyclerAdapter.getList().size()-1);
//    }

    @Override
    public void onScanCompleted() {

    }

    @Override
    public void stopRefresh() {

    }

    @Override
    public void startRefresh() {

    }

    @Override
    public boolean isRefreshing() {
        return false;
    }

    @Override
    public void enableSwipeRefreshLayout(boolean enable) {

    }

    @Override
    public void showSnackbar(String message) {

    }

    /**
     * 是否全选
     * @param flag
     */
    private void setDateNotify(boolean flag){
        if (recyclerView==null)return;
        if (recyclerAdapter==null)return;
        if(recyclerAdapter.getList()==null||recyclerAdapter.getList().size()==0)return;
        for (int i=0;i<recyclerAdapter.getList().size();i++){
            recyclerAdapter.getList().get(i).setIsChoise(flag);
        }
        recyclerAdapter.notifyDataSetChanged();
    }

}
