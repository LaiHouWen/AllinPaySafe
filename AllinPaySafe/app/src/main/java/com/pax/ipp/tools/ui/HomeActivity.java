package com.pax.ipp.tools.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;
import com.pax.ipp.tools.R;
import com.pax.ipp.tools.mvp.impl.CircularLoaderView;
import com.pax.ipp.tools.mvp.presenter.CircularLoaderPresenter;
import com.pax.ipp.tools.ui.base.BaseActivity;
import com.pax.ipp.tools.utils.BtnUtils;
import com.pax.ipp.tools.utils.DateUtil;
import com.pax.ipp.tools.utils.LogUtil;
import com.pax.ipp.tools.utils.TextFormater;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by houwen.lai on 2017/9/1.
 * 主页
 * 内存
 * 流量
 */

public class HomeActivity extends BaseActivity implements PopupMenu.OnMenuItemClickListener,
        CircularLoaderView {

    @BindView(R.id.imgbtn_about)
    ImageButton imgbtn_about;

//    @BindView(R.id.wave_view)
//    WaveView wave_view;
    @BindView(R.id.cirLoaders)
    CircularFillableLoaders cirLoaders;


    @BindView(R.id.tv_ware_tip)
    TextView tv_ware_tip;
    @BindView(R.id.tv_ware_round)
    TextView tv_ware_round;
    @BindView(R.id.tv_ware_merory)
    TextView tv_ware_merory;

//清理内存
    @BindView(R.id.btn_learn_merory)
    Button btn_learn_merory;
    @BindView(R.id.btn_learn_merory_stop)
    Button btn_learn_merory_stop;
    @BindView(R.id.btn_learn_merory_contiune)
    Button btn_learn_merory_contiune;

//流量消耗
    @BindView(R.id.tv_today_useing_d)
    TextView tv_today_useing_d;
    @BindView(R.id.tv_month_useing_d)
    TextView tv_month_useing_d;

    private int mBorderColor = Color.parseColor("#FFFFFF");
    private int mBorderWidth = 10;

    CircularLoaderPresenter cirPresenter;

    @Override
    public int getLayoutId() {
        return R.layout.home_activity;
    }

    @Override
    public void initView(Bundle savedInstanceState) {

        cirLoaders.setProgress(0);



        cirPresenter =new CircularLoaderPresenter(this);
        cirPresenter.attachView(this);
        cirPresenter.setTimeTask();
//        wave_view.setBorder(mBorderWidth, mBorderColor);
//        wave_view.setShapeType(WaveView.ShapeType.CIRCLE);

// Set Progress

// Set Wave and Border Color
//        cirLoaders.setColor(Color.RED);
// Set Border Width
//        cirLoaders.setBorderWidth(10 * getResources().getDisplayMetrics().density);
// Set Wave Amplitude (between 0.00f and 0.10f)
//        cirLoaders.setAmplitudeRatio(0.03f);


    }

    @Override
    public void initPresenter() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cirPresenter.onDestroy();
    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_base, menu);//关于
//        return true;
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {//进入关于页面
//            startActivity(new Intent(this, AbountActivity.class));
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @OnClick({R.id.imgbtn_about,R.id.btn_learn_merory,R.id.btn_learn_merory_stop
            ,R.id.btn_learn_merory_contiune,R.id.tv_today_useing,R.id.tv_today_useing_d,
            R.id.tv_month_useing,R.id.tv_month_useing_d})
    public void onClinck(View view){
        if (!BtnUtils.isFastDoubleClick())return;
        switch (view.getId()){
            case R.id.imgbtn_about://关于
                shoePopMenu();
                break;
            case R.id.btn_learn_merory://清理内存 暂停
//                startActivity(new Intent(this,MeoryClearActivity.class));
                startActivity(new Intent(this,MeoryClearAllActivity.class));

                break;
            case R.id.btn_learn_merory_stop://停止

                break;
            case R.id.btn_learn_merory_contiune://继续

                break;
            case R.id.tv_today_useing://今日流量
            case R.id.tv_today_useing_d:
                startActivity(new Intent(mContext,FlowActivity.class).putExtra("time","today"));
                break;
            case R.id.tv_month_useing://本月流量
            case R.id.tv_month_useing_d:
                startActivity(new Intent(mContext,FlowActivity.class).putExtra("time","month"));
                break;
                default:
                    break;
        }
    }

    PopupMenu popup;
//
    private void shoePopMenu(){
        if (imgbtn_about==null)return;
        if (popup!=null){
            popup.show();
            return;
        }
        //创建弹出式菜单对象（最低版本11）
        popup = new PopupMenu(this, imgbtn_about);//第二个参数是绑定的那个view
        //获取菜单填充器
        MenuInflater inflater = popup.getMenuInflater();
        //填充菜单
        inflater.inflate(R.menu.menu_base, popup.getMenu());
        //绑定菜单项的点击事件
        popup.setOnMenuItemClickListener(this);
        //显示(这一行代码不要忘记了)
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings://关于
                if (popup!=null)
                popup.dismiss();
//                startActivity(new Intent(this, AbountActivity.class));
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void initViews() {

    }

    @Override
    public void updateViews(long sum, long available, float percent) {
        LogUtil.d("sum="+sum+"\tavilable="+available+"\tprcent="+percent);
        // Set Progress
        cirLoaders.setProgress((int) (100 - percent));//水波文

        SpannableStringBuilder spannableString = new SpannableStringBuilder(DateUtil.getInstance().getDouble(percent)+"");
        spannableString.append("%");
        AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(20);
        //AbsoluteSizeSpan px    true dp
        spannableString.setSpan(new AbsoluteSizeSpan(36,true), 0, spannableString.length()-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(20,true), spannableString.length()-1,spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_ware_round.setText(spannableString);

        tv_ware_merory.setText("" + TextFormater.dataSizeFormat(sum - available) + "/" +
                TextFormater.dataSizeFormat(sum));//使用情况

        //流量使用情况
        tv_today_useing_d.setText("0MB");//今日消耗的流量
        tv_month_useing_d.setText("0MB");//本月的总流量

    }

    @Override
    public void onCleanStarted(Context context) {

    }

    @Override
    public void onCleanCompleted(Context context, long memory) {

    }


}
