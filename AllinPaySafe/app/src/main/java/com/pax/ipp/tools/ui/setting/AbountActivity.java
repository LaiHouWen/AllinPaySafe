package com.pax.ipp.tools.ui.setting;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pax.ipp.tools.R;
import com.pax.ipp.tools.ui.base.BaseActivity;
import com.pax.ipp.tools.utils.ApplicationUtils;
import com.pax.ipp.tools.utils.FlowUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by houwen.lai on 2017/9/1.
 * 关于页面
 */

public class AbountActivity extends BaseActivity{

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView toolbar_title;

    @BindView(R.id.tv_version_name)
    TextView tv_version_name;

    @BindView(R.id.img_icon_about)
    ImageView img_icon_about;

    @BindView(R.id.btn_updata)
    Button btn_updata;

    @Override
    public int getLayoutId() {
        return R.layout.about_activity;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        toolbar_title.setText(getString(R.string.action_settings));
        toolbar.setBackgroundColor(getResources().getColor(R.color.blue_0980));
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        btn_updata.setVisibility(View.GONE);
        tv_version_name.setText(ApplicationUtils.getVersionName(mContext));
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
    long time=0;
    @OnClick(R.id.img_icon_about)
    public void onClick(View v){
        if (v.getId()==R.id.img_icon_about&&System.currentTimeMillis()-time<1100){
            //清除流量
                FlowUtil.getInstance().cleanCachaFlow(mContext);
                FlowUtil.getInstance().cleanCachaFlowByUid(mContext);
        }else  time = System.currentTimeMillis();

    }


}
