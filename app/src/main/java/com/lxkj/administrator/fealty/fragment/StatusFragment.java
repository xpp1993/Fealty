package com.lxkj.administrator.fealty.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
    private final int REQUEST_CODE_UPDATA_USERIFO_INTERNET = 0x23;
    private final int REQUEST_CODE_UPDATA_GPS_INTERNET = 0x26;
    private MyHandler myHandler;
    // private HealthDataFragement healthDataFragement;

    @Override
    protected void init() {
        myHandler = new MyHandler();

        EventBus.getDefault().register(this);
        mJazzy.setTransitionEffect(JazzyViewPager.TransitionEffect.ZoomIn);
        mJazzy.setPageMargin(30);

//        adapter = new HeathMonitoringAdapter(getChildFragmentManager(), mJazzy, fragments, new HeathMonitoringAdapter.BackData() {
//            @Override
//            public void callYou(HealthDataFragement healthDataFragement) {
//                StatusFragment.this.healthDataFragement = healthDataFragement;
//            }
//        });
        adapter = new HeathMonitoringAdapter(getChildFragmentManager(), mJazzy, fragments);
        mJazzy.setAdapter(adapter);
        //1.登录进来获得我的页面,初始化
        initFragments();
        //2.网络获取数据 1.运动睡眠数据 2.GPS
        Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile"}, SessionHolder.user.getMobile());
        //1.
        NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.SELECT_USER_CURRENT_HEART, params, null, REQUEST_CODE_UPDATA_USERIFO_INTERNET, this);
        //2.
        NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.GET_GPS_FROM_URL, params, null, REQUEST_CODE_UPDATA_GPS_INTERNET, this);
    }

    private void initFragments() {
        HealthDataFragement healthDataFragement = new HealthDataFragement();
        Bundle bundle = new Bundle();
        bundle.putString("parentPhone", SessionHolder.user.getMobile());
        healthDataFragement.setArguments(bundle);
        adapter.addFragment(healthDataFragement);
        adapter.notifyDataSetChanged();
      //  healthDataFragement.setIdentity("我的");
    }

    @Override
    protected void initListener() {

    }

    // 用EventBus 来导航,订阅者
    public void onEventMainThread(Bundle event) {
        double lon = event.getDouble("lon");
        String phoneNumber = event.getString("parentPhone");
        double lat = event.getDouble("lat");
        String describle = event.getString("describle");
        String address = event.getString("address");
        for(HealthDataFragement healthDataFragement:fragments){
            if (healthDataFragement.getArguments().getString("parentPhone").equals(phoneNumber)){
                healthDataFragement.setGPSData(String.valueOf(lat),String.valueOf(lon),describle,address);
            }
        }
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
                    DecodeManager.decodeSportSleep(jsonObject, requestCode, myHandler);
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
                            HealthDataFragement healthDataFragement = new HealthDataFragement();
                            //healthDataFragement = new HealthDataFragement();
                            healthDataFragement.setArguments(bundle);
                            HealthDataFragement fragement = adapter.addFragment(healthDataFragement);
                            adapter.notifyDataSetChanged();
                            fragement.setGPSData(lat, lon, locationdescrible, address);//通过Fragment提供的方法设置数据
                        }

                    } else {
                        ToastUtils.showToastInUIThread("服务器数据有异常！");
                    }
                    break;
                case REQUEST_CODE_UPDATA_USERIFO_INTERNET:
                    if (data.getInt("code") == 1) {
                        HashMap<String, String[]> result = (HashMap<String, String[]>) data.getSerializable("result");
                        if (result == null)
                            return;
                        Iterator<String> keyIterator = result.keySet().iterator();
                        while (keyIterator.hasNext()) {
                            String parentPhone = keyIterator.next();
                            String light_hour = result.get(parentPhone)[0];//light_hour, light_minute, deep_hour, deep_minute, total_hour_str, calories, step, distance, identity, currentHeart
                            String light_minute = result.get(parentPhone)[1];
                            String deep_hour = result.get(parentPhone)[2];
                            String deep_minute = result.get(parentPhone)[3];
                            String total_hour_str = result.get(parentPhone)[4];
                            String calories = result.get(parentPhone)[5];
                            String step = result.get(parentPhone)[6];
                            String distance = result.get(parentPhone)[7];
                            String identity = result.get(parentPhone)[8];
                            String currentHeart = result.get(parentPhone)[9];
                            Bundle bundle = new Bundle();
                            bundle.putString("parentPhone", parentPhone);
                            //  HealthDataFragement healthDataFragement = new HealthDataFragement();
                            HealthDataFragement healthDataFragement = new HealthDataFragement();
                            healthDataFragement.setArguments(bundle);
                            HealthDataFragement fragement = adapter.addFragment(healthDataFragement);
                            adapter.notifyDataSetChanged();
                            fragement.setSleepData(light_hour, light_minute, deep_hour, deep_minute, total_hour_str);
                            fragement.setSportData(calories, step, distance);
                            fragement.setIdentity(identity);
                            fragement.setCurentRate(currentHeart);
                        }

                    } else {
                        ToastUtils.showToastInUIThread("服务器数据有异常！");
                    }
                    break;
                default:
                    break;
            }

        }
    }

}
