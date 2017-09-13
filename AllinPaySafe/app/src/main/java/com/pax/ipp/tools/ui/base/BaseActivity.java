package com.pax.ipp.tools.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.pax.ipp.tools.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;

/**
 *
 * activity 基类
 *
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected final String TAG = this.getClass().getSimpleName();

    public Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(this.getLayoutId());

        ButterKnife.bind(this);

//        EventBus.getDefault().register(this);

        mContext = this;
//        mPresenter = TUtil.getT(this, 0);
//        mModel = TUtil.getT(this, 1);

        this.initView(savedInstanceState);
        this.initPresenter();
    }

    public abstract int getLayoutId();

    public abstract void initView(Bundle savedInstanceState);
    /**
     * 简单页面无需mvp就不用管此方法即可,完美兼容各种实际场景的变通
     * mPresenter.setVM(this, mModel);
     */
    public abstract void initPresenter();
    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.i("---------onStart ");
    }
    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.i("---------onResume ");
//        if(isMobclick) {
//            MobclickAgent.onResume(this);
//            TextView title = (TextView) findViewById(R.id.toolbar_title);
//            if (null != title && !TextUtils.isEmpty(title.getText().toString()) && !"华夏信财".equals(title.getText().toString()))
//                StatService.onPageStart(this, title.getText().toString());
//            else
//                StatService.onResume(this);
//        }
    }
    //用于友盟，百度统计
    public boolean isMobclick = true;
    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.i("---------onPause ");
//        if(isMobclick) {
//            MobclickAgent.onPause(this);
//            TextView title = (TextView) findViewById(R.id.toolbar_title);
//            if (null != title && !TextUtils.isEmpty(title.getText().toString()) && !"华夏信财".equals(title.getText().toString()))
//                StatService.onPageEnd(this, title.getText().toString());
//            else
//                StatService.onPause(this);
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.i("---------onStop ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.i("---------onDestroy ");
//        if (mPresenter != null)
//            mPresenter.onDestroy();
    }

    /**
     * toast
     */
    public void toast(@NonNull CharSequence text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * toast
     */
    public void toast(@StringRes int stringRes) {
        Toast.makeText(this, stringRes, Toast.LENGTH_SHORT).show();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_base, menu);
//        return true;
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }


}
