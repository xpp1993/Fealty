package com.lxkj.administrator.fealty.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.adapter.HeathMonitoringAdapter;
import com.lxkj.administrator.fealty.base.BaseFragment;
import com.lxkj.administrator.fealty.bean.SleepData;
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
    HealthDataFragement healthDataFragement;
    String identity;
    int tempRate;
    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        mJazzy.setTransitionEffect(JazzyViewPager.TransitionEffect.ZoomIn);
        mJazzy.setPageMargin(30);
    }
    @Override
    protected void initListener() {

    }
    // 用EventBus 来导航,订阅者
    public void onEventMainThread(Bundle event) {
        String identity = event.getString("IF_CONNECTED");
      tempRate = event.getInt("tempRate");
        Bundle bundle = new Bundle();
        if (!"".equals(identity) && identity != null) {
            if (healthDataFragement == null) {
            healthDataFragement = new HealthDataFragement();
              //  healthDataFragement = (HealthDataFragement) HealthDataFragement.instantiate(AppUtils.getBaseContext(), HealthDataFragement.class.getName());
                SportData sportData = (SportData) event.getSerializable("sportdata");
                SleepData sleepData = (SleepData) event.getSerializable("sleepData");
                bundle.putString("identity", identity);
                bundle.putSerializable("sportData", sportData);
                bundle.putSerializable("sleepData", sleepData);
                healthDataFragement.setArguments(bundle);
                fragments.add(healthDataFragement);
                adapter = new HeathMonitoringAdapter(getChildFragmentManager(), mJazzy, fragments);
                adapter.notifyDataSetChanged();
                mJazzy.setAdapter(adapter);
            }
            Log.e("816", tempRate + "");
          //  EventBus.getDefault().post(tempRate);
            Intent intent = new Intent();
            intent.putExtra("tempRate",tempRate);
            intent.setAction(HealthDataFragement.DATA_CHANGED);
            getActivity().sendBroadcast(intent);
        }
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
