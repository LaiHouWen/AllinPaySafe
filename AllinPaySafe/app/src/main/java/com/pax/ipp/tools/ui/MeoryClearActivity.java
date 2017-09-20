package com.pax.ipp.tools.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;


import com.pax.ipp.tools.AppManager;
import com.pax.ipp.tools.R;
import com.pax.ipp.tools.adapter.CacheListAdapter;
import com.pax.ipp.tools.event.ClearMeoryEvent;
import com.pax.ipp.tools.event.PresenterEvent;
import com.pax.ipp.tools.ui.base.RubbishActivity;
import com.pax.ipp.tools.ui.view.LoadingView;
import com.pax.ipp.tools.ui.view.Loading_view;
import com.pax.ipp.tools.utils.AppUtils;
import com.pax.ipp.tools.utils.BtnUtils;
import com.pax.ipp.tools.utils.LogUtil;
import com.pax.ipp.tools.utils.TextFormater;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by houwen.lai on 2017/9/4.
 *
 * 内存列表
 *BaseActivity implements RubbishCleanView
 */

public class MeoryClearActivity extends RubbishActivity {

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

    @BindView(R.id.recyclerView_c)
    RecyclerView recyclerView;

    @BindView(R.id.listview_meory)
    ListView listview_meory;

    @BindView(R.id.btn_lear_meory)
    Button btn_lear_meory;


    StringBuffer bf=new StringBuffer();

    CacheListAdapter recyclerAdapter;


    long cacheSizs=0;

    Loading_view loading_view;

    private Dialog load_dialog;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPresenterEvent(PresenterEvent event){
        LogUtil.e("event","PresenterEvent");
        if (event==null)return;
        LogUtil.e("event","PresenterEvent getPresenter");
        cacheSizs = event.getCacheSize();
        mRubbishCleanPresenter=event.getPresenter();
        mRubbishCleanPresenter.initViews();
        setRecyclerViewAdapter();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public int getLayoutId() {
        return R.layout.meory_clear;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        AppManager.getAppManager().addActivity(this);
        toolbar_title.setText(getString(R.string.text_clear_memery));
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        loading_view = new Loading_view(mContext,R.style.CustomDialog);
        loading_view.setTv_load_dialog("清理中...");

        checbox_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setDateNotify(isChecked);
            }
        });
//        mRubbishCleanPresenter = new RubbishCleanPresenter(this);
//        mRubbishCleanPresenter.attachView(this);
//        mRubbishCleanPresenter.onCreate(savedInstanceState);

        loading_view.show();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        AppManager.getAppManager().removeActivity(this);
    }

    @Override
    public void initPresenter() {
        if (mRubbishCleanPresenter!=null){
            mRubbishCleanPresenter.initViews();
        }

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
                if (BtnUtils.isFastDoubleClick()){
                    setClearMeory("");
//                    EventBus.getDefault().post(new String());
//                    setResult(RESULT_OK);
//                    finish();
                }
                break;
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setClearMeory(String text){
        LogUtil.e("clear-","message ");

        loading_view.show();

        btn_lear_meory.setText("清理中...");
        btn_lear_meory.setEnabled(false);
        mRubbishCleanPresenter.cleanMemory("");
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDismDialog(String t){
        LogUtil.e("clear-","onDismDialog ");
        handler.postAtTime(new Runnable() {
            @Override
            public void run() {
                loading_view.show();
            }
        }, 200);
    }

    class onLister implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            LogUtil.e("clear-","message ");
            loading_view.show();
//            showMyDialog();
            btn_lear_meory.setText("清理中...");
            btn_lear_meory.setEnabled(false);
            mRubbishCleanPresenter.cleanMemory("");
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
//        itemTouchHelper.attachToRecyclerView(recyclerView);

        long sum = AppUtils.getTotalMemory();
        long available = AppUtils.getAvailMemory(mContext);
        float percent = AppUtils.getPercent(mContext);
        tv_meory_total.setText(TextFormater.dataSizeFormat(sum - available) + "/" +
                TextFormater.dataSizeFormat(sum));//可用内存/总内存

//        loading_view.show();
    }

    public void setRecyclerViewAdapter(){

        LogUtil.d("mer","setRecyclerViewAdapter");
        recyclerAdapter = mRubbishCleanPresenter.recyclerAdapter;
        recyclerView.setLayoutManager(
                new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,
                        false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerAdapter);
//        itemTouchHelper.attachToRecyclerView(recyclerView);


        tv_ariable_meory.setText(TextFormater.dataSizeFormatArray(cacheSizs)[0]);
        tv_ariable_meory_t.setText(TextFormater.dataSizeFormatArray(cacheSizs)[1]);

        long sum = AppUtils.getTotalMemory();
        long available = AppUtils.getAvailMemory(mContext);
        float percent = AppUtils.getPercent(mContext);
        tv_meory_total.setText(TextFormater.dataSizeFormat(sum - available) + "/" +
                TextFormater.dataSizeFormat(sum));//可用内存/总内存
        LogUtil.d("mer","setRecyclerViewAdapter end");

        loading_view.dismiss();
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
        cacheSizs = cacheSize;
        tv_ariable_meory.setText(TextFormater.dataSizeFormatArray(cacheSize)[0]);
        tv_ariable_meory_t.setText(TextFormater.dataSizeFormatArray(cacheSize)[1]);


//        mTextView.setText("正在扫描:" + current + "/" + max + " 包名:" +
//                packageName);
//        float percent = (int) (1.0 * current / max * 100);
//        mProgressBar.setProgress((int) percent);
    }


    @Override
    public void onScanCompleted() {
        LogUtil.e("clear-","message  onScanCompleted");
        loading_view.dismiss();
//        closeDialog();
        btn_lear_meory.setEnabled(true);
        long sum = AppUtils.getTotalMemory();
        long available = AppUtils.getAvailMemory(mContext);
        float percent = AppUtils.getPercent(mContext);
        tv_meory_total.setText(TextFormater.dataSizeFormat(sum - available) + "/" +
                TextFormater.dataSizeFormat(sum));//可用内存/总内存
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
        LogUtil.e("clear-","message  showSnackbar");
        loading_view.dismiss();
//        closeDialog();
        checbox_all.setChecked(false);
        btn_lear_meory.setText("一键清理");
        btn_lear_meory.setEnabled(true);

        if ( recyclerAdapter!=null&&recyclerAdapter.getList().size()==0){
            tv_ariable_meory.setText("0");
            tv_ariable_meory_t.setText("KB");
        }

        tv_choise_apps.setText(getResources().getString(R.string.text_all_choise)+
                0+ getResources().getString(R.string.text_all_choise_r));
        toast(message);

    }

    /**
     * 是否全选
     * @param flag
     */
    private void setDateNotify(boolean flag){
        if (recyclerView==null)return;
        if (recyclerAdapter==null)return;
        if(recyclerAdapter.getList()==null||recyclerAdapter.getList().size()==0)return;
        int sum =flag?recyclerAdapter.getList().size():0;
        tv_choise_apps.setText(getResources().getString(R.string.text_all_choise)+
               sum+ getResources().getString(R.string.text_all_choise_r));
        for (int i=0;i<recyclerAdapter.getList().size();i++){
            recyclerAdapter.getList().get(i).setIsChoise(flag);
        }
        recyclerAdapter.setFlagAllTrue(flag);
        recyclerAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode =ThreadMode.MAIN,sticky = true)
    public void onMeoryMethod(ClearMeoryEvent event) {

        LogUtil.e("onEvent=","Meory clear activity");
        btn_lear_meory.setEnabled(true);
        loading_view.dismiss();
//        closeDialog();
        long temp = 0;
        if (cacheSizs-event.getMeorySize()>=0){
            temp=cacheSizs-event.getMeorySize();
        }
        tv_ariable_meory.setText(TextFormater.dataSizeFormatArray(temp)[0]);
        tv_ariable_meory_t.setText(TextFormater.dataSizeFormatArray(temp)[1]);
        long sum = AppUtils.getTotalMemory();
        long available = AppUtils.getAvailMemory(mContext);
        float percent = AppUtils.getPercent(mContext);
        tv_meory_total.setText(TextFormater.dataSizeFormat(sum - available) + "/" +
                TextFormater.dataSizeFormat(sum));//可用内存/总内存

        loading_view.dismiss();
//        closeDialog();
        checbox_all.setChecked(false);
        btn_lear_meory.setText("一键清理");
        btn_lear_meory.setEnabled(true);

        if ( recyclerAdapter!=null&&recyclerAdapter.getList().size()==0){
            tv_ariable_meory.setText("0");
            tv_ariable_meory_t.setText("KB");
        }

        tv_choise_apps.setText(getResources().getString(R.string.text_all_choise)+
                0+ getResources().getString(R.string.text_all_choise_r));
        toast(event.getMessage());
    }
    private Handler handler=new Handler();
    private void showMyDialog() {
        load_dialog = new Dialog(this, R.style.loadingDialogTheme);
        load_dialog.setContentView(new LoadingView(this));
        load_dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });
        handler.postAtTime(new Runnable() {
            @Override
            public void run() {
                load_dialog.show();
            }
        },100);

    }

    private void closeDialog() {
        if (null != load_dialog) {
            if (load_dialog.isShowing()) {
                handler.postAtTime(new Runnable() {
                    @Override
                    public void run() {
                        load_dialog.dismiss();
                    }
                }, 300);
            }
        }
    }}
