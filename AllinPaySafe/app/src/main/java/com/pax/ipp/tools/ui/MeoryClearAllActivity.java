package com.pax.ipp.tools.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.pax.ipp.tools.Constant;
import com.pax.ipp.tools.R;
import com.pax.ipp.tools.adapter.CacheListAdapter;
import com.pax.ipp.tools.mvp.presenter.RubbishCleanPresenter;
import com.pax.ipp.tools.ui.base.RubbishActivity;
import com.pax.ipp.tools.utils.TextFormater;

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


    RubbishCleanPresenter mRubbishCleanPresenter;

    final String gif_0 ="gi_f_1.gif";
    final String gif_1 ="gi_f_2.gif";
    final String gif_2 ="gi_f_3.gif";
    final String gif_3 ="gi_f_4.gif";
    boolean flag_0 = false;
    boolean flag_1 = false;
    boolean flag_2 = false;
    boolean flag_3 = false;

    final String text_temp = "确认";

    @Override
    public int getLayoutId() {
        return R.layout.meory_clear_all;
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

        flag_0 = false;
        flag_1 = false;
        flag_2 = false;
        flag_3 = false;

        tv_ariable_meory_c.setVisibility(View.GONE);
        tv_ariable_c.setVisibility(View.GONE);
        tv_clear_over.setVisibility(View.GONE);

        btn_look_detail.setVisibility(View.GONE);
        btn_one_cler.setVisibility(View.GONE);

        mRubbishCleanPresenter = new RubbishCleanPresenter(this);
        mRubbishCleanPresenter.attachView(this);
        mRubbishCleanPresenter.onCreate(savedInstanceState);

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
            gifDrawable.addAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationCompleted(int loopNumber) {
                    gifDrawable.reset();
                    gifImageview.setImageDrawable(gifDrawable);
                    flag_2=true;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            btn_one_cler.setEnabled(true);
        }
        mRubbishCleanPresenter.cleanCache();
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
        if (mRubbishCleanPresenter!=null)
        mRubbishCleanPresenter.scanCache();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        mRubbishCleanPresenter.onDestroy();
    }
    @Override
    public void initViews(CacheListAdapter recyclerAdapter, Context context, ItemTouchHelper itemTouchHelper) {

    }

    @Override
    public void onScanStarted(Context context) {
//        setGifImageview(gif_0);
        try {
            GifDrawable gifDrawable = new GifDrawable(mContext.getAssets(),gif_0);
            gifImageview.setImageDrawable(gifDrawable);
            gifDrawable.addAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationCompleted(int loopNumber) {
                    tv_ariable_meory_c.setVisibility(View.VISIBLE);
                    tv_ariable_c.setVisibility(View.VISIBLE);
                    tv_clear_over.setVisibility(View.GONE);

                    btn_look_detail.setVisibility(View.VISIBLE);
                    btn_one_cler.setVisibility(View.VISIBLE);
                    flag_0=true;
                }
            });
        } catch (IOException e) {
            btn_one_cler.setEnabled(true);
            e.printStackTrace();
        }
    }

    @Override
    public void onScanProgressUpdated(Context context, int current, int max, long cacheSize, String packageName) {
        btn_one_cler.setEnabled(false);
        tv_ariable_meory_c.setText(TextFormater.dataSizeFormatArray(cacheSize)[0]);
        tv_ariable_c.setText(TextFormater.dataSizeFormatArray(cacheSize)[1]);
    }

    @Override
    public void onScanCompleted() {
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
}
