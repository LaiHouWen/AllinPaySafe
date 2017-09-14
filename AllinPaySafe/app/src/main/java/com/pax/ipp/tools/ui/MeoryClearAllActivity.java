package com.pax.ipp.tools.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pax.ipp.tools.AppManager;
import com.pax.ipp.tools.Constant;
import com.pax.ipp.tools.R;
import com.pax.ipp.tools.adapter.CacheListAdapter;
import com.pax.ipp.tools.event.ClearChoiseAllEvent;
import com.pax.ipp.tools.event.MeoryClearEvent;
import com.pax.ipp.tools.event.PresenterEvent;
import com.pax.ipp.tools.mvp.presenter.RubbishCleanPresenter;
import com.pax.ipp.tools.ui.base.RubbishActivity;
import com.pax.ipp.tools.utils.LogUtil;
import com.pax.ipp.tools.utils.TextFormater;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by houwen.lai on 2017/9/5.
 * 内存清理动画
 *  BaseActivity implements RubbishCleanView
 *
 */

public class MeoryClearAllActivity extends RubbishActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView toolbar_title;

    @BindView(R.id.appImg_clear_meory)
    ImageView appImg_clear_meory;

//    @BindView(R.id.gif_view)
//    GifView gif_view;

    @BindView(R.id.gifImageview)
    GifImageView gifImageview;


    @BindView(R.id.tv_ariable_meory_c)
    TextView tv_ariable_meory_c;
    @BindView(R.id.tv_ariable_c)
    TextView tv_ariable_c;
    @BindView(R.id.tv_clear_over)
    TextView tv_clear_over;

    @BindView(R.id.btn_look_detail)
    Button btn_look_detail;

    @BindView(R.id.btn_one_cler)
    Button btn_one_cler;

    final String gif_0 ="gi_f_1.gif";
    final String gif_1 ="gi_f_2.gif";
    final String gif_1_1 ="gi_f_2_1.gif";
    final String gif_2 ="gi_f_3.gif";
    final String gif_3 ="gi_f_4.gif";
    boolean flag_0 = false;
    boolean flag_1 = false;
    boolean flag_2 = false;
    boolean flag_3 = false;

    final String text_temp = "确认";

    long cacheSizes= 0;

    @Override
    protected void onStart() {
        super.onStart();
//
    }

    @Override
    public int getLayoutId() {
        return R.layout.meory_clear_all;
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

        flag_0 = false;
        flag_1 = false;
        flag_2 = false;
        flag_3 = false;

        tv_ariable_meory_c.setVisibility(View.GONE);
        tv_ariable_c.setVisibility(View.GONE);
        tv_clear_over.setVisibility(View.GONE);

        btn_look_detail.setVisibility(View.GONE);
        btn_one_cler.setVisibility(View.GONE);


        btn_look_detail.setVisibility(View.GONE);
        btn_one_cler.setVisibility(View.GONE);

        mRubbishCleanPresenter = new RubbishCleanPresenter(this);
        mRubbishCleanPresenter.attachView(this);
        mRubbishCleanPresenter.onCreate(savedInstanceState);
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

    @OnClick({R.id.btn_look_detail,R.id.btn_one_cler})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_look_detail://查看详情
                startActivityForResult(new Intent(mContext,MeoryClearActivity.class), Constant.RequestCode_meory_all);
            break;
            case R.id.btn_one_cler://一键清理
                pressKey();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== Constant.RequestCode_meory_all&&resultCode==RESULT_OK){
//            pressKey();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        btn_one_cler.setEnabled(true);
//        if (mRubbishCleanPresenter!=null)
//        mRubbishCleanPresenter.scanCache();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.e("event","onStop");
        if (AppManager.getAppManager().isActivity(MeoryClearActivity.class)){
            LogUtil.e("event","isActivity");
            EventBus.getDefault().post(new PresenterEvent(mRubbishCleanPresenter,cacheSizes));
        }
    }

    @Override public void onDestroy() {
        super.onDestroy();
        mRubbishCleanPresenter.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    /**
     * final GifDrawable gifDrawable_3 = new GifDrawable(mContext.getAssets(),"gi_f_4.gif");
     * @param path
     */
    private void setGifImageview(String path){//"gi_f_4.gif"
        try {
            GifDrawable gifDrawable = new GifDrawable(mContext.getAssets(),path);
            gifImageview.setImageDrawable(gifDrawable);
            gifDrawable.addAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationCompleted(int loopNumber) {
                    return ;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 一键清理
     */
    private void pressKey(){
        if (btn_one_cler.getText().toString().trim().contains(text_temp)){
            finish();
            return;
        }
        btn_one_cler.setEnabled(false);
        tv_ariable_meory_c.setVisibility(View.GONE);
        tv_ariable_c.setVisibility(View.GONE);
        tv_clear_over.setVisibility(View.GONE);

        btn_look_detail.setVisibility(View.GONE);
        btn_one_cler.setVisibility(View.GONE);
        try {
            GifDrawable gifDrawable = new GifDrawable(mContext.getAssets(),gif_1);
            gifImageview.setImageDrawable(gifDrawable);
            gifDrawable.setSpeed(1.3f);
            gifDrawable.addAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationCompleted(int loopNumber) {
                    gifDrawable.reset();
//                    gifImageview.setImageDrawable(gifDrawable);
                    flag_2=true;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            btn_one_cler.setEnabled(true);
        }
        mRubbishCleanPresenter.cleanMemory(Constant.TYPE_CLEARN_MEORY_ALL);
    }

    @Override
    public void initViews(CacheListAdapter recyclerAdapter, Context context, ItemTouchHelper itemTouchHelper) {

    }

    @Override
    public void onScanStarted(Context context) {
        btn_one_cler.setVisibility(View.INVISIBLE);
        btn_one_cler.setEnabled(false);
//        setGifImageview(gif_0);
        try {
            GifDrawable gifDrawable = new GifDrawable(mContext.getAssets(),gif_0);
            GifDrawable gifDrawable_1 = new GifDrawable(mContext.getAssets(),gif_1_1);
            gifImageview.setImageDrawable(gifDrawable);
            gifDrawable.addAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationCompleted(int loopNumber) {
                    tv_ariable_meory_c.setVisibility(View.VISIBLE);
                    tv_ariable_c.setVisibility(View.VISIBLE);
                    tv_clear_over.setVisibility(View.GONE);

                    btn_look_detail.setVisibility(View.GONE);
                    btn_one_cler.setVisibility(View.GONE);
                    flag_0=true;

                    gifImageview.setImageDrawable(gifDrawable_1);


                }
            });
            gifDrawable_1.addAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationCompleted(int loopNumber) {
                    if (!btn_one_cler.isEnabled()){
                        gifDrawable_1.reset();
                    }else {
                        btn_look_detail.setVisibility(View.VISIBLE);
                        btn_one_cler.setVisibility(View.VISIBLE);
                    }
                }
            });
        } catch (IOException e) {
            btn_one_cler.setEnabled(true);
            e.printStackTrace();
        }
    }

    @Override
    public void onScanProgressUpdated(Context context, int current, int max, long cacheSize, String packageName) {

        cacheSizes = cacheSize;
        btn_one_cler.setVisibility(View.GONE);
        btn_one_cler.setEnabled(false);
        tv_ariable_meory_c.setText(TextFormater.dataSizeFormatArray(cacheSize)[0]);
        tv_ariable_c.setText(TextFormater.dataSizeFormatArray(cacheSize)[1]);
    }

    @Override
    public void onScanCompleted() {
        btn_look_detail.setVisibility(View.VISIBLE);
        btn_one_cler.setVisibility(View.VISIBLE);
        btn_one_cler.setEnabled(true);
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
        btn_one_cler.setEnabled(true);

//        tv_ariable_meory_c.setText(TextFormater.dataSizeFormatArray(cacheSizes)[0]);
//        tv_ariable_c.setText(TextFormater.dataSizeFormatArray(cacheSizes)[1]);
        cancalAnimation();

    }

    public void cancalAnimation(){
        try {
            GifDrawable gifDrawable = new GifDrawable(mContext.getAssets(),gif_2);
            GifDrawable gifDrawable_3 = new GifDrawable(mContext.getAssets(),gif_3);
            gifImageview.setImageDrawable(gifDrawable);
            gifDrawable.addAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationCompleted(int loopNumber) {
                    gifImageview.setImageDrawable(gifDrawable_3);
                }
            });
            gifDrawable_3.addAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationCompleted(int loopNumber) {
                    tv_ariable_meory_c.setVisibility(View.VISIBLE);
                    tv_ariable_c.setVisibility(View.VISIBLE);
                    tv_clear_over.setVisibility(View.VISIBLE);

                    btn_look_detail.setVisibility(View.GONE);
                    btn_one_cler.setVisibility(View.VISIBLE);
                    btn_one_cler.setText(text_temp);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onClearAllMeory(Long L){
        cancalAnimation();
        tv_ariable_meory_c.setText(TextFormater.dataSizeFormatArray(L)[0]);
        tv_ariable_c.setText(TextFormater.dataSizeFormatArray(L)[1]);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMeoryClearEvent(MeoryClearEvent event) {
        LogUtil.e("onEvent=","onMeoryClearEvent="+event.getCacheSize());
        GifDrawable gifDrawable_3 = null;
        try {
            gifDrawable_3 = new GifDrawable(mContext.getAssets(),gif_3);
            gifImageview.setImageDrawable(gifDrawable_3);
        } catch (IOException e) {
            e.printStackTrace();
        }

        tv_ariable_meory_c.setText(TextFormater.dataSizeFormatArray(event.getCacheSize())[0]);
        tv_ariable_c.setText(TextFormater.dataSizeFormatArray(event.getCacheSize())[1]);
        tv_ariable_meory_c.setVisibility(View.VISIBLE);
        tv_ariable_c.setVisibility(View.VISIBLE);
        tv_clear_over.setVisibility(View.VISIBLE);

        btn_look_detail.setVisibility(View.GONE);
        btn_one_cler.setVisibility(View.VISIBLE);
        btn_one_cler.setText(text_temp);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMeoryEvent(ClearChoiseAllEvent event){
        LogUtil.e("eventbus","ClearChoiseAllEvent 空");
        if (event==null)return;
        LogUtil.e("eventbus",event.toString());
        if (event.getType().equals(Constant.TYPE_CLEARN_MEORY_ALL)){
            showSnackbar(event.getCount() > 0 ? "共清理" + event.getCount() + "个进程,共占内存" +
                    TextFormater.dataSizeFormat(event.getMemorySize()) +
                    "内存" : "未选中要清理的进程");
            EventBus.getDefault().post(new Long(event.getMemorySize()));
        }

    }




}
