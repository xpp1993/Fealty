package com.lxkj.administrator.fealty.fragment;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.adapter.HeathMonitoringAdapter;
import com.lxkj.administrator.fealty.base.BaseFragment;
import com.lxkj.administrator.fealty.bean.SportData;
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
    String identity;
    @Override
    protected void init() {
//       mJazzy.setPageMargin(-50);
//       mJazzy.setHorizontalFadingEdgeEnabled(true);
//       mJazzy.setFadingEdgeLength(30);
        EventBus.getDefault().register(this);
        for (int i = 1; i < 6; i++) {
            Bundle bundle = new Bundle();
            bundle.putInt("i", i);
            HealthDataFragement healthDataFragement = new HealthDataFragement();
            healthDataFragement.setArguments(bundle);
            fragments.add(healthDataFragement);
        }
        mJazzy.setTransitionEffect(JazzyViewPager.TransitionEffect.ZoomIn);
        mJazzy.setPageMargin(30);
        adapter = new HeathMonitoringAdapter(getChildFragmentManager(), mJazzy, fragments);
        mJazzy.setAdapter(adapter);
    }

    @Override
    protected void initListener() {

    }
    // 用EventBus 来导航,订阅者
    public void onEventMainThread(Bundle event) {
       SportData sportData = (SportData) event.getSerializable("sportdata");
        Log.e("Tag", sportData.toString());
        Toast.makeText(getActivity(),sportData.toString(),Toast.LENGTH_LONG).show();
    }
    @Override
    protected void initData() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
