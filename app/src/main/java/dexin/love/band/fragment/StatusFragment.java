package dexin.love.band.fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import de.greenrobot.event.EventBus;
import dexin.love.band.R;
import dexin.love.band.adapter.HeathMonitoringAdapter;
import dexin.love.band.base.BaseFragment;
import dexin.love.band.bean.RateListData;
import dexin.love.band.bean.SleepData;
import dexin.love.band.bean.SportData;
import dexin.love.band.bean.UserInfo;
import dexin.love.band.manager.DecodeManager;
import dexin.love.band.manager.ParameterManager;
import dexin.love.band.utils.AppUtils;
import dexin.love.band.utils.CommonTools;
import dexin.love.band.utils.ContextUtils;
import dexin.love.band.utils.NetWorkAccessTools;
import dexin.love.band.utils.ToastUtils;
import dexin.love.band.widget.JazzyViewPager;

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
    private final int REQUEST_CODE_UPDATA_ZHEXIAN_HEART = 0x25;
    private MyHandler myHandler;
    // private HealthDataFragement healthDataFragement;
    private UserInfo userInfo;
    @Override
    protected void init() {
        myHandler = new MyHandler();
        userInfo = ContextUtils.getObjFromSp(AppUtils.getBaseContext(), "userInfo");
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
//        EventBus.getDefault().register(this);
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
//        Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile"}, SessionHolder.user.getMobile());
        Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile"}, userInfo.getMobile());
        //1.
        NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.SELECT_USER_CURRENT_HEART, params, null, REQUEST_CODE_UPDATA_USERIFO_INTERNET, this);
        //2.
        NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.GET_GPS_FROM_URL, params, null, REQUEST_CODE_UPDATA_GPS_INTERNET, this);
        //3.折线心率
        NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.SELECT_USER_HEART, params, null, REQUEST_CODE_UPDATA_ZHEXIAN_HEART, this);
        //写个循环每一段时间刷新一次页面
        myHandler.postDelayed(LoadData, 1000 * 60 * 3);
    }

    private void initFragments() {
        HealthDataFragement healthDataFragement = new HealthDataFragement();
        Bundle bundle = new Bundle();
        // bundle.putString("parentPhone", SessionHolder.user.getMobile());
        bundle.putString("parentPhone", userInfo.getMobile());
        healthDataFragement.setArguments(bundle);
        adapter.addFragment(healthDataFragement);
        adapter.notifyDataSetChanged();
        //  healthDataFragement.setIdentity("我的");
    }

    private Runnable LoadData = new Runnable() {
        @Override
        public void run() {

            // Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile"}, SessionHolder.user.getMobile());
            Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile"}, userInfo.getMobile());
            //1.
            NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.SELECT_USER_CURRENT_HEART, params, null, REQUEST_CODE_UPDATA_USERIFO_INTERNET, StatusFragment.this);
            //2.
            NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.GET_GPS_FROM_URL, params, null, REQUEST_CODE_UPDATA_GPS_INTERNET, StatusFragment.this);
            //3
            NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.SELECT_USER_HEART, params, null, REQUEST_CODE_UPDATA_ZHEXIAN_HEART, StatusFragment.this);
            myHandler.postDelayed(this, 1000 * 60 * 3);
        }
    };

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
        for (HealthDataFragement healthDataFragement : fragments) {
            if (healthDataFragement.getArguments().getString("parentPhone").equals(phoneNumber)) {
                healthDataFragement.setGPSData(String.valueOf(lat), String.valueOf(lon), describle, address);
            }
        }
    }

    // 用EventBus 来导航,订阅者
    public void onEventMainThread(Map<String, List<RateListData>> map) {
        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            String phoneNumber = (String) it.next();
            Log.e("phoneNumber", phoneNumber);
            for (HealthDataFragement healthDataFragement : fragments) {
                if (healthDataFragement.getArguments().getString("parentPhone").equals(phoneNumber)) {
                    healthDataFragement.setRateListData(map.get(phoneNumber));
                }
            }
        }
    }

    /**
     * 写一个event方法接收运动数据
     */
    public void onEventMainThread(SportData sportData) {
        String phoneNumber = sportData.getParentphone();
        // String distance = new DecimalFormat("0.00").format(sportData.getDistance()); // 保留2位小数，带前导零
        String distance = sportData.getDistance();
//        String steps = String.valueOf(sportData.getSteps());
//        String calories = String.valueOf(sportData.getCalories());
        String steps = sportData.getSteps();
        String calories = sportData.getCalories();
        Log.e("eventsport", phoneNumber + "\n" + distance + "\n" + calories + "\n" + steps);
        for (HealthDataFragement healthDataFragement : fragments) {
            if (healthDataFragement.getArguments().getString("parentPhone").equals(phoneNumber)) {
                healthDataFragement.setSportData(calories, steps, distance);
            }
        }
    }

    /**
     * 写一个event方法接收睡眠数据
     */
    public void onEventMainThread(SleepData sleepData) {
        String total_time = sleepData.getTotal_hour_str();
        String deep_hour = String.valueOf(sleepData.getDeep_hour());
        String deep_minute = String.valueOf(sleepData.getDeep_minute());
        String light_hour = String.valueOf(sleepData.getLight_hour());
        String light_minute = String.valueOf(sleepData.getLight_minute());
        String phoneNumber = sleepData.getParentPhone();//电话号码给睡眠数据做标识
        for (HealthDataFragement healthDataFragement : fragments) {
            if (healthDataFragement.getArguments().getString("parentPhone").equals(phoneNumber)) {
                healthDataFragement.setSleepData(light_hour, light_minute, deep_hour, deep_minute, total_time);
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
        if (LoadData != null) {
            myHandler.removeCallbacks(LoadData);
        }
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
                case REQUEST_CODE_UPDATA_ZHEXIAN_HEART:
                    DecodeManager.decodeHeartMessage(jsonObject, requestCode, myHandler);
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
                            //  if (TextUtils.isEmpty(locationdescrible))
                            //    return;
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
                            Log.e("currrent", currentHeart);
                            Bundle bundle = new Bundle();
                            bundle.putString("parentPhone", parentPhone);
                            //  HealthDataFragement healthDataFragement = new HealthDataFragement();
                            HealthDataFragement fragement = new HealthDataFragement();
                            fragement.setArguments(bundle);
                            fragement = adapter.addFragment(fragement);
                            adapter.notifyDataSetChanged();
                            if ("".equals(light_hour) || light_hour == null)
                                light_hour = 0 + "";
                            if ("".equals(light_minute) || light_minute == null)
                                light_minute = 0 + "";
                            if ("".equals(deep_hour) || deep_hour == null)
                                deep_hour = 0 + "";
                            if ("".equals(deep_minute) || deep_minute == null)
                                deep_minute = 0 + "";
                            fragement.setSleepData(light_hour, light_minute, deep_hour, deep_minute, total_hour_str);
                            fragement.setSportData(calories, step, distance);
                            fragement.setIdentity(identity);
                            fragement.setCurentRate(currentHeart);
                        }

                    } else {
                        ToastUtils.showToastInUIThread("服务器数据有异常！");
                    }
                    break;
                case REQUEST_CODE_UPDATA_ZHEXIAN_HEART:
                    if (data.getInt("code") == 1) {
                        HashMap<String, List<RateListData>> result = (HashMap<String, List<RateListData>>) data.getSerializable("result");
                        if (result == null)
                            return;
                        Iterator keyIterator = result.entrySet().iterator();
                        while (keyIterator.hasNext()) {
                            Map.Entry entry = (Map.Entry) keyIterator.next();
                            String parentPhone = (String) entry.getKey();
                            // List<RateListData> listDatas = (List<RateListData>) entry.getValue();
                            Bundle bundle = new Bundle();
                            bundle.putString("parentPhone", parentPhone);
                            //  HealthDataFragement healthDataFragement = new HealthDataFragement();
                            HealthDataFragement healthDataFragement = new HealthDataFragement();
                            healthDataFragement.setArguments(bundle);
                            HealthDataFragement fragement = adapter.addFragment(healthDataFragement);
                            adapter.notifyDataSetChanged();
                            fragement.setRateListData(result.get(entry.getKey()));
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