package com.lxkj.administrator.fealty.fragment;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.adapter.HeathMonitoringAdapter;
import com.lxkj.administrator.fealty.base.BaseFragment;
import com.lxkj.administrator.fealty.bean.SleepData;
import com.lxkj.administrator.fealty.bean.SportData;
import com.lxkj.administrator.fealty.utils.AppUtils;
import com.lxkj.administrator.fealty.widget.JazzyViewPager;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/7/26.
 */
@ContentView(R.layout.fragement_status)
public class StatusFragment extends BaseFragment {
    @ViewInject(R.id.mJazzy)
    private JazzyViewPager mJazzy;
    private HeathMonitoringAdapter adapter;
    private List<HealthDataFragement> fragments = new ArrayList<HealthDataFragement>();
    int steps;
    HealthDataFragement healthDataFragement;
    String identity;

    @Override
    protected void init() {
//       mJazzy.setPageMargin(-50);
//       mJazzy.setHorizontalFadingEdgeEnabled(true);
//       mJazzy.setFadingEdgeLength(30);
        EventBus.getDefault().register(this);
//        for (int i = 1; i < 6; i++) {
//            Bundle bundle = new Bundle();
//            bundle.putInt("i", i);
//            HealthDataFragement healthDataFragement = new HealthDataFragement();
//            healthDataFragement.setArguments(bundle);
//            fragments.add(healthDataFragement);
//        }
        mJazzy.setTransitionEffect(JazzyViewPager.TransitionEffect.ZoomIn);
        mJazzy.setPageMargin(30);
//        adapter = new HeathMonitoringAdapter(getChildFragmentManager(), mJazzy, fragments);
//        mJazzy.setAdapter(adapter);
    }

    @Override
    protected void initListener() {

    }

    // 用EventBus 来导航,订阅者
    public void onEventMainThread(Bundle event) {
        String identity = event.getString("IF_CONNECTED");
        int tempRate = event.getInt("tempRate");
        Bundle bundle = new Bundle();
        if (!"".equals(identity) && identity != null) {
            if (healthDataFragement == null) {
              //   healthDataFragement = new HealthDataFragement();
                healthDataFragement = (HealthDataFragement) HealthDataFragement.instantiate(AppUtils.getBaseContext(), HealthDataFragement.class.getName());
            }
            SportData sportData = (SportData) event.getSerializable("sportdata");
            SleepData sleepData = (SleepData) event.getSerializable("sleepData");
            bundle.putString("identity", identity);
            bundle.putSerializable("sportData", sportData);
            bundle.putSerializable("sleepData", sleepData);
            bundle.putInt("tempRate", tempRate);
            healthDataFragement.setArguments(bundle);
            fragments.add(healthDataFragement);
            adapter = new HeathMonitoringAdapter(getChildFragmentManager(), mJazzy, fragments);
            adapter.notifyDataSetChanged();
            mJazzy.setAdapter(adapter);
        }
        ;
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onGetBunndle(Bundle arguments) {
        super.onGetBunndle(arguments);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
