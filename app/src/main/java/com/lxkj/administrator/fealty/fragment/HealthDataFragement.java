package com.lxkj.administrator.fealty.fragment;

import android.os.Bundle;
import android.widget.TextView;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.BaseFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by Administrator on 2016/8/4.
 */
@ContentView(R.layout.fragment_healthdata)
public class HealthDataFragement extends BaseFragment {
    @ViewInject(R.id.tv_gps)
    private TextView tv_gps;
    @ViewInject(R.id.tv_health)
    private TextView tv_health;
    @Override
    public void onGetBunndle(Bundle arguments) {
        super.onGetBunndle(arguments);
        tv_gps.setText("老人" + arguments.getInt("i") + "号的GPS");
        tv_health.setText("老人" + arguments.getInt("i") + "号的健康监测数据");
    }
    @Override
    protected void init() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {

    }

}
