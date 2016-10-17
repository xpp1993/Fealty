package dexin.love.band.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.greenrobot.event.EventBus;
import dexin.love.band.R;
import dexin.love.band.base.BaseFragment;
import dexin.love.band.bean.RateListData;
import dexin.love.band.bean.UserInfo;
import dexin.love.band.event.NavFragmentEvent;
import dexin.love.band.manager.ParameterManager;
import dexin.love.band.manager.SPManager;
import dexin.love.band.utils.AppUtils;
import dexin.love.band.utils.ContextUtils;
import dexin.love.band.utils.ToastUtils;
import dexin.love.band.view.LineChart03View;
import dexin.love.band.view.LineChart03View_left;
import dexin.love.band.view.PPView;

/**
 * Created by Administrator on 2016/8/4.
 */
@ContentView(R.layout.fragment_healthdata)
public class HealthDataFragement extends BaseFragment implements View.OnClickListener {
    @ViewInject(R.id.shuimiantotal)
    TextView shuimiantotal;
    @ViewInject(R.id.shuimiandetail)
    TextView shuimiandetail;
    @ViewInject(R.id.shuimiandetail2)
    TextView shuimiandetail2;
    @ViewInject(R.id.yundongtotal)
    TextView yundongtotal;
    @ViewInject(R.id.yundongdetail)
    TextView yundongdetail;
    @ViewInject(R.id.yundongdetail2)
    TextView yundongdetail2;
    @ViewInject(R.id.horizontalScrollView1)
    HorizontalScrollView horiView;
    @ViewInject(R.id.pp)
    PPView mPpView;
    @ViewInject(R.id.lineChartView_right)
    LineChart03View mLineChart03View;
    @ViewInject(R.id.circle_view)
    LineChart03View_left mLineChart03View_left;
    @ViewInject(R.id.tv_dingwei)
    TextView tv_gps;
    @ViewInject(R.id.dingwei)
    ImageView im_dingwei;
    private MyHandler handler = new MyHandler();
    private final int REQURST_HANDLER_GPSDATA = 0x21;
    private final int REQURST_HANDLER_SlEEPDATA = 0x22;
    private final int REQURST_HANDLER_SPORTDATA = 0x23;
    private final int REQURST_HANDLER_IDENTITY = 0x24;
    private final int REQURST_HANDLER_CURRENT_RATE = 0x25;
    private final int REQURST_HANDLER_LIST_RATE = 0x26;
    public static final String RATE_CHANGED = "com.lxkj.administrator.fealty.fragment.HealthDataFragment";
    private HealthRateReceiver mReceiver = null;
    private int RATE_STATUS = 80;//保存心率
    private SharedPreferences preferences;
    private int sportMinRate, sportMaxRate, norMax, norMin, sleepMinRate, sleepMaxRate;
    private String latstr = "0";
    private String address = "在这里显示定位";
    private String lonStr = "0";
    private String total_hour_str = "00:00";
    private String light_hour = "0";
    private String light_minute = "0";
    private String deep_hour = "0";
    private String deep_minute = "0";
    private String calories = "0";
    private String steps = "0";
    private String distance = "0";
    private String indentity = "我";
    private List<RateListData> list = new ArrayList<>();

    @Override
    protected void init() {
        Log.d("wyj", "init");
        handler = new MyHandler();
        // map = new TreeMap<>();
        //设置horizontalScrollvView拉到头和尾的时候没有阴影颜色
        horiView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        //1.获得sharedPreference对象,SharedPrefences只能放基础数据类型，不能放自定义数据类型。
        preferences = SPManager.getSharedPreferences(AppUtils.getBaseContext());
        initShow();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null)
            getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public void onGetBunndle(Bundle arguments) {
        super.onGetBunndle(arguments);
        UserInfo userInfo= ContextUtils.getObjFromSp(AppUtils.getBaseContext(), "userInfo");
//        if (TextUtils.equals(arguments.getString("parentPhone"), SessionHolder.user.getMobile())) {
        if (TextUtils.equals(arguments.getString("parentPhone"), userInfo.getMobile())) {
            //注册广播接收实时心率
            registeHealthRateChangedReceiver();
        }
    }

    @Override
    protected void initListener() {
        im_dingwei.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dingwei:
                //跳转到百度地图
                Bundle bundle = new Bundle();
                bundle.putString("locationdescrible", describle);
                bundle.putDouble("lat", lat);
                bundle.putDouble("lon", lon);
                bundle.putInt("rate", RATE_STATUS);
                EventBus.getDefault().post(new NavFragmentEvent(new LocaltionFragment(), bundle));
                break;
            default:
                break;
        }
    }

    double lat = 0.0;
    double lon = 0.0;
    String describle = "";

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REQURST_HANDLER_GPSDATA://处理GPS数据
                    Bundle GPSData = msg.getData();
                    address = GPSData.getString("address");
                    latstr = GPSData.getString("lat");
                    lonStr = GPSData.getString("lon");
                    describle = GPSData.getString("locationdescrible");
                    Log.e("setGPS", describle);
                    break;
                case REQURST_HANDLER_SlEEPDATA://处理睡眠数据
                    Bundle sleepData = msg.getData();
                    light_hour = sleepData.getString("light_hour");
                    light_minute = sleepData.getString("light_minute");
                    deep_hour = sleepData.getString("deep_hour");
                    deep_minute = sleepData.getString("deep_minute");
                    total_hour_str = sleepData.getString("total_hour_str");
                    break;
                case REQURST_HANDLER_SPORTDATA://处理运动数据
                    Bundle stepData = msg.getData();
                    calories = stepData.getString("calories");
                    steps = stepData.getString("step");
                    distance = stepData.getString("distance");
                    break;
                case REQURST_HANDLER_IDENTITY://处理身份标识
                    indentity = (String) msg.obj;
                    break;
                case REQURST_HANDLER_CURRENT_RATE://处理当前心率
                    RATE_STATUS = msg.arg1;
                    break;
                case REQURST_HANDLER_LIST_RATE://画心率折线图
                    Log.d("wyj","REQURST_HANDLER_LIST_RATE");
                    list = (List<RateListData>) msg.obj;
                    break;
                default:
                    break;
            }
            if (HealthDataFragement.this.isVisible()) {
                initShow();
            }
        }
    }

    private void initShow() {
        tv_gps.setText(address);
        //把lon ,lat ,地址描述传到GPs定位页面显示在百度地图上
        lat = Double.parseDouble(latstr);
        lon = Double.parseDouble(lonStr);

        shuimiantotal.setText("睡眠时间  " + total_hour_str);
        shuimiandetail2.setText("浅度睡眠" + light_hour + "小时" + light_minute + "分钟");
        shuimiandetail.setText("深度度睡眠" + deep_hour + "小时" + deep_minute + "分钟");

        yundongtotal.setText("运动  " + distance + "公里");
        yundongdetail.setText("消耗" + calories + "卡路里");
        yundongdetail2.setText("步行" + steps + "步");

        mPpView.setFirstText(indentity);
        mPpView.postInvalidate();

        readSP();
        if (RATE_STATUS < norMin || RATE_STATUS > norMax) {//心率不正常
            functionTest(RATE_STATUS, R.color.warning);
        } else {
            functionTest(RATE_STATUS, R.color.normal);
        }
        //把实时心率传到定位页面
        Intent intent = new Intent();
        intent.putExtra("tempRate", RATE_STATUS);
        intent.setAction(LocaltionFragment.RATE_CHANGED);
        getActivity().sendBroadcast(intent);

        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            RateListData listData = list.get(i);
            try {
                map.put(listData.getTime(), listData.getRate());
            } catch (NumberFormatException e) {
            }
        }
        initChart(map);
    }

    private void initChart(Map<String, Integer> map) {
        if(null == map ||map.size() == 0)
            return;
        mLineChart03View.reset(map);
        mLineChart03View_left.reset(map);
        mLineChart03View.postInvalidate();
        mLineChart03View_left.postInvalidate();
    }


    /**
     * 读取sp文件中的内容
     */
    private void readSP() {
        sportMinRate = preferences.getInt(ParameterManager.SPORT_RATE_MIN_MINUTE, 90);//运动时最低的心率
        sportMaxRate = preferences.getInt(ParameterManager.SPORT_RATE_MAX_MINUTE, 120);//运动时最高的心率
        norMin = preferences.getInt(ParameterManager.NORMAL_RATE_MIN_MINUTE, 60);//正常最低心率
        norMax = preferences.getInt(ParameterManager.NORMAL_RATE_MAX_MINUTE, 120);//正常最高心率
        sleepMinRate = preferences.getInt(ParameterManager.SLEEP_RATE_MIN_MINUTE, 60);//睡眠时最低心率
        sleepMaxRate = preferences.getInt(ParameterManager.SLEEP_RATE_MAX_MINUTE, 100);//睡眠时最高心率
    }

    //广播接受者
    class HealthRateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(HealthDataFragement.RATE_CHANGED)) {
                int tempRate = intent.getIntExtra("tempRate", -1);
                Message msg = Message.obtain();
                msg.what = REQURST_HANDLER_CURRENT_RATE;//监听到心率改变
                msg.arg1 = tempRate;
                handler.sendMessage(msg);
            }
        }
    }

    //注册广播的方法
    private void registeHealthRateChangedReceiver() {
        IntentFilter filter = new IntentFilter();
        try {
            if (mReceiver != null) {
                getActivity().unregisterReceiver(mReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mReceiver = new HealthRateReceiver();
        filter.addAction(HealthDataFragement.RATE_CHANGED);
        getActivity().registerReceiver(mReceiver, filter);
    }

    //提供给外界设置GPS数据的方法,lat,lon,locationdescrible,address
    public void setGPSData(String lat, String lon, String locationdescrible, String address) {
        Bundle GPSData = new Bundle();
        GPSData.putString("lat", lat);
        GPSData.putString("lon", lon);
        GPSData.putString("locationdescrible", locationdescrible);
        GPSData.putString("address", address);
        if (TextUtils.isEmpty(address))
            return;
        Message message = Message.obtain();
        message.what = REQURST_HANDLER_GPSDATA;
        message.setData(GPSData);
        handler.sendMessage(message);
    }

    //提供给外界设置睡眠数据的方法,light_hour, light_minute, deep_hour, deep_minute, total_hour_str
    public void setSleepData(String light_hour, String light_minute, String deep_hour, String deep_minute, String total_hour_str) {
        Bundle sleepData = new Bundle();
        sleepData.putString("light_hour", light_hour);
        sleepData.putString("light_minute", light_minute);
        sleepData.putString("deep_hour", deep_hour);
        sleepData.putString("deep_minute", deep_minute);
        sleepData.putString("total_hour_str", total_hour_str);
        if (TextUtils.isEmpty(total_hour_str)) {
            return;
        }else if (total_hour_str.equals("0")){
            total_hour_str="00:00";
        }
        Log.e("sleep",total_hour_str);
        Message message = Message.obtain();
        message.what = REQURST_HANDLER_SlEEPDATA;
        message.setData(sleepData);
        if (handler == null) {
            handler = new MyHandler();
        }
        handler.sendMessage(message);
    }

    //提供给外界设置运动数据的方法,calories, step, distance
    public void setSportData(String calories, String step, String distance) {
        Bundle sportData = new Bundle();
        sportData.putString("calories", calories);
        sportData.putString("step", step);
        sportData.putString("distance", distance);
        if (TextUtils.isEmpty(distance) && TextUtils.isEmpty(step) && TextUtils.isEmpty(calories)) {
            return;
        }
        Message message = Message.obtain();
        message.what = REQURST_HANDLER_SPORTDATA;
        message.setData(sportData);
        handler.sendMessage(message);
    }

    //提供给外界设置身份标识的方法,identity, currentHeart
    public void setIdentity(String identity) {
        if (TextUtils.isEmpty(identity)) {
            return;
        }
        Message message = Message.obtain();
        message.obj = identity;
        message.what = REQURST_HANDLER_IDENTITY;
        handler.sendMessage(message);
    }

    //提供给外界设置当前心率的方法,currentHeart
    public void setCurentRate(String currentHeart) {
        int tempRate = Integer.parseInt(currentHeart);
        Message message = Message.obtain();
        message.arg1 = tempRate;
        if (TextUtils.isEmpty(currentHeart)) {
            return;
        }
        message.what = REQURST_HANDLER_CURRENT_RATE;
        handler.sendMessage(message);
    }

    //提供给外界设置折线图中心率的方法,currentHeart
    public void setRateListData(List<RateListData> listData) {
        Message message = Message.obtain();
        if (listData.size() == 0)
            return;
        message.what = REQURST_HANDLER_LIST_RATE;
        message.obj = listData;
        handler.sendMessage(message);
    }

    /**
     * 设置第二行文本的颜色
     */
    private void functionTest(int currentRate, int color) {
        mPpView.setSecondTextColor(ContextCompat.getColor(getContext(), color));
        mPpView.setSecondText(currentRate + "");
        mPpView.postInvalidate();
    }

}
