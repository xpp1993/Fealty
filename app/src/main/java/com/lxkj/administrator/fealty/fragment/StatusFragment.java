package com.lxkj.administrator.fealty.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.adapter.HeathMonitoringAdapter;
import com.lxkj.administrator.fealty.base.BaseFragment;
import com.lxkj.administrator.fealty.utils.AppUtils;
import com.lxkj.administrator.fealty.widget.JazzyViewPager;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/26.
 */
@ContentView(R.layout.fragement_status)
public class StatusFragment extends BaseFragment {
    @ViewInject(R.id.mJazzy)
    private JazzyViewPager mJazzy;
    private HeathMonitoringAdapter adapter;
    private List<HealthDataFragement> fragments = new ArrayList<HealthDataFragement>();

    @Override
    protected void init() {
        mJazzy.setTransitionEffect(JazzyViewPager.TransitionEffect.Stack);
        for (int i = 1; i < 6; i++) {
            HealthDataFragement healthDataFragement = new HealthDataFragement();
            Bundle bundle=new Bundle();
            bundle.putInt("i",i);
            healthDataFragement.setArguments(bundle);
            fragments.add(healthDataFragement);
        }
        adapter = new HeathMonitoringAdapter(getFragmentManager(), mJazzy, fragments);
        mJazzy.setAdapter(adapter);
    }
    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {

    }
}
