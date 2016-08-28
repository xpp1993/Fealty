package com.lxkj.administrator.fealty.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.BaseFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/8/27/0027.
 */
@ContentView(R.layout.shezhi_gps)
public class fragment_ShezhiGPS extends BaseFragment implements View.OnClickListener {
    //jiancegps,relative_sleep_gps,sport_gps
    @ViewInject(R.id.bar_iv_left)
    private ImageView bar_back;
    @ViewInject(R.id.bar_tv_title_left)
    private TextView bar_biaoti;
    @ViewInject(R.id.bar_view_left_line)
    private ImageView bar_view_left_line;
    @ViewInject(R.id.jiancegps)
    private RelativeLayout jianceGps;//间测定位
    @ViewInject(R.id.relative_sleep_gps)
    private RelativeLayout sleep_gps;//睡眠时定位
    @ViewInject(R.id.sport_gps)
    private RelativeLayout sport_gps;//运动定位

    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        bar_back.setVisibility(View.VISIBLE);
        bar_view_left_line.setVisibility(View.VISIBLE);
        bar_biaoti.setVisibility(View.VISIBLE);
        bar_biaoti.setText("GPS");

    }

    @Override
    protected void initListener() {
        //设置点击监听
        bar_back.setOnClickListener(this);
        jianceGps.setOnClickListener(this);
        sport_gps.setOnClickListener(this);
        sleep_gps.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relative_sleep_gps://点击设置睡眠时多少小时上传一次GPS定位信息，弹出时间数字选择框
                break;
            case R.id.sport_gps://点击设置运动时多少分钟上传一次GPS定位信息，弹出时间数字选择框
                break;
            case R.id.jiancegps:
                break;
            case R.id.bar_iv_left:
                getActivity().onBackPressed();
                break;
            default:
                break;
        }
    }
}
