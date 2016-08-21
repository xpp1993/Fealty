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
import com.lxkj.administrator.fealty.manager.ParameterManager;
import com.lxkj.administrator.fealty.manager.SessionHolder;
import com.lxkj.administrator.fealty.utils.AppUtils;
import com.lxkj.administrator.fealty.utils.CommonTools;
import com.lxkj.administrator.fealty.utils.NetWorkAccessTools;
import com.lxkj.administrator.fealty.widget.JazzyViewPager;

import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/7/26.
 */
@ContentView(R.layout.fragement_status)
public class StatusFragment extends BaseFragment implements NetWorkAccessTools.RequestTaskListener {
    @ViewInject(R.id.mJazzy)
    private JazzyViewPager mJazzy;
    private HeathMonitoringAdapter adapter;
    private List<HealthDataFragement> fragments = new ArrayList<HealthDataFragement>();
    int steps;
    HealthDataFragement healthDataFragement;
    String identity;
    int tempRate;
    private final int REQUEST_CODE_UPDATA_USERIFO_INTERNET=0x23;
    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        mJazzy.setTransitionEffect(JazzyViewPager.TransitionEffect.ZoomIn);
        mJazzy.setPageMargin(30);
        //网络获取数据
        Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile"}, SessionHolder.user.getMobile());
        NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.SELECT_USER_CURRENT_HEART, params, null,REQUEST_CODE_UPDATA_USERIFO_INTERNET,this );
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

    @Override
    public void onRequestStart(int requestCode) {

    }

    @Override
    public void onRequestLoading(int requestCode, long current, long count) {

    }

    @Override
    public void onRequestSuccess(JSONObject jsonObject, int requestCode) {

    }

    @Override
    public void onRequestFail(int requestCode, int errorNo) {

    }
}
