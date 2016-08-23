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
import com.lxkj.administrator.fealty.manager.DecodeManager;
import com.lxkj.administrator.fealty.manager.ParameterManager;
import com.lxkj.administrator.fealty.manager.SessionHolder;
import com.lxkj.administrator.fealty.utils.AppUtils;
import com.lxkj.administrator.fealty.utils.CommonTools;
import com.lxkj.administrator.fealty.utils.NetWorkAccessTools;
import com.lxkj.administrator.fealty.utils.ToastUtils;
import com.lxkj.administrator.fealty.widget.JazzyViewPager;

import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
    private final int REQUEST_CODE_UPDATA_USERIFO_INTERNET = 0x23;
    private final int REQUEST_CODE_UPDATA_GPS_INTERNET = 0x26;
    private double lat;
    private double lon;
    private String locationdescrible;
    private String address;

    private MyHandler myHandler;

    @Override
    protected void init() {
        myHandler = new MyHandler();

        EventBus.getDefault().register(this);
        mJazzy.setTransitionEffect(JazzyViewPager.TransitionEffect.ZoomIn);
        mJazzy.setPageMargin(30);

        adapter = new HeathMonitoringAdapter(getChildFragmentManager(), mJazzy, fragments);
        mJazzy.setAdapter(adapter);

        //网络获取数据 1.运动睡眠数据 2.GPS
        Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile"}, SessionHolder.user.getMobile());
        //1.
        NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.SELECT_USER_CURRENT_HEART, params, null, REQUEST_CODE_UPDATA_USERIFO_INTERNET, this);
        //2.
        NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.GET_GPS_FROM_URL, params, null, REQUEST_CODE_UPDATA_GPS_INTERNET, this);
    }

    @Override
    protected void initListener() {

    }

    // 用EventBus 来导航,订阅者
    public void onEventMainThread(Bundle event) {
        String identity = event.getString("IF_CONNECTED");
        tempRate = event.getInt("tempRate");
        address = event.getString("address");
        lat = event.getDouble("lat");
        lon = event.getDouble("lon");
        locationdescrible = event.getString("describle");
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
//                fragments.add(healthDataFragement);
                adapter.addFragment(healthDataFragement);
                adapter.notifyDataSetChanged();

            }
            Log.e("816", tempRate + "");
            Intent intent = new Intent();
            intent.putExtra("tempRate", tempRate);
            intent.putExtra("address", address);
            intent.putExtra("lat", lat);
            intent.putExtra("lon", lon);
            intent.putExtra("describle", locationdescrible);
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
        try {
            switch (requestCode) {
                case REQUEST_CODE_UPDATA_GPS_INTERNET://更新GPS信息
                    DecodeManager.decodeGPSMessage(jsonObject, requestCode, myHandler);

                    break;
                case REQUEST_CODE_UPDATA_USERIFO_INTERNET://更新首页用户信息

                    break;
                default:
                    break;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            ToastUtils.showToastInUIThread("服务器返回错误");
        }
    }

    @Override
    public void onRequestFail(int requestCode, int errorNo) {

    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            switch (msg.what) {
                case REQUEST_CODE_UPDATA_GPS_INTERNET:
                    if (data.getInt("code") == 1) {
                        HashMap<String, String[]> result = (HashMap<String, String[]>) data.getSerializable("result");
                        if (result == null)
                            return;
                        Iterator<String> keyIterator = result.keySet().iterator();
                        while (keyIterator.hasNext()) {
                            String parentPhone = keyIterator.next();
                            String lat = result.get(parentPhone)[0];//lat,lon,locationdescrible,address
                            String lon = result.get(parentPhone)[1];
                            String locationdescrible = result.get(parentPhone)[2];
                            String address = result.get(parentPhone)[3];
                            Bundle bundle = new Bundle();
                            bundle.putString("parentPhone", parentPhone);
                            bundle.putString("lat", lat);
                            bundle.putString("lon", lon);
                            bundle.putSerializable("locationdescrible", locationdescrible);
                            bundle.putString("address", address);
                            HealthDataFragement healthDataFragement = new HealthDataFragement();
                            healthDataFragement.setArguments(bundle);
                            adapter.addFragment(healthDataFragement);
                        }
                        adapter.notifyDataSetChanged();


                    } else {
                        ToastUtils.showToastInUIThread("xxxxx");
                    }

                    break;
            }

        }
    }
}
