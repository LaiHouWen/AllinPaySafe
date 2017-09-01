package com.allinpaysafe.app.ui;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.allinpaysafe.app.R;
import com.allinpaysafe.app.ui.base.BaseActivity;
import com.allinpaysafe.app.ui.setting.AbountActivity;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by houwen.lai on 2017/9/1.
 * 主页
 *
 *
 */

public class HomeActivity extends BaseActivity implements PopupMenu.OnMenuItemClickListener {

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

    @Override
    public int getLayoutId() {
        return R.layout.home_activity;
    }

    @Override
    public void initView() {
//        wave_view.setBorder(mBorderWidth, mBorderColor);
//        wave_view.setShapeType(WaveView.ShapeType.CIRCLE);

// Set Progress
        cirLoaders.setProgress(80);
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
            ,R.id.btn_learn_merory_contiune})
    public void onClinck(View view){
        switch (view.getId()){
            case R.id.imgbtn_about://关于
                shoePopMenu();
            case R.id.btn_learn_merory://清理内存 暂停

                break;
            case R.id.btn_learn_merory_stop://停止

                break;
            case R.id.btn_learn_merory_contiune://继续

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
}
