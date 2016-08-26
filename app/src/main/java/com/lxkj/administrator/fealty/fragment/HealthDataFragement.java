package com.lxkj.administrator.fealty.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.LocationClientOption;
import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.baidugps.LocationService;
import com.lxkj.administrator.fealty.baidugps.MyLocationListener;
import com.lxkj.administrator.fealty.base.BaseApplication;
import com.lxkj.administrator.fealty.base.BaseFragment;
import com.lxkj.administrator.fealty.bean.SleepData;
import com.lxkj.administrator.fealty.bean.SportData;
import com.lxkj.administrator.fealty.event.NavFragmentEvent;
import com.lxkj.administrator.fealty.manager.DecodeManager;
import com.lxkj.administrator.fealty.manager.ParameterManager;
import com.lxkj.administrator.fealty.manager.SessionHolder;
import com.lxkj.administrator.fealty.utils.AppUtils;
import com.lxkj.administrator.fealty.utils.CommonTools;
import com.lxkj.administrator.fealty.utils.NetWorkAccessTools;
import com.lxkj.administrator.fealty.view.LineChart03View;
import com.lxkj.administrator.fealty.view.LineChart03View_left;
import com.lxkj.administrator.fealty.view.PPView;
import com.yc.peddemo.sdk.UTESQLOperate;
import com.yc.peddemo.utils.CalendarUtils;
import com.yc.pedometer.info.RateOneDayInfo;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/8/4.
 */
@ContentView(R.layout.fragment_healthdata)
public class HealthDataFragement extends BaseFragment implements View.OnClickListener {
    @ViewInject(R.id.shuimiantotal)
    private TextView shuimiantotal;
    @ViewInject(R.id.shuimiandetail)
    private TextView shuimiandetail;
    @ViewInject(R.id.shuimiandetail2)
    private TextView shuimiandetail2;
    @ViewInject(R.id.yundongtotal)
    private TextView yundongtotal;
    @ViewInject(R.id.yundongdetail)
    private TextView yundongdetail;
    @ViewInject(R.id.yundongdetail2)
    private TextView yundongdetail2;
    @ViewInject(R.id.horizontalScrollView1)
    private HorizontalScrollView horiView;
    @ViewInject(R.id.pp)
    private PPView mPpView;
    @ViewInject(R.id.lineChartView_right)
    private LineChart03View mLineChart03View;
    @ViewInject(R.id.circle_view)
    private LineChart03View_left mLineChart03View_left;
    @ViewInject(R.id.tv_dingwei)
    private TextView tv_gps;
    @ViewInject(R.id.dingwei)
    private ImageView im_dingwei;
    private MyHandler handler;
    TreeMap<Integer, Integer> map;
    private final int REQURST_HANDLER_GPSDATA = 0x21;
    private final int REQURST_HANDLER_SlEEPDATA = 0x22;
    private final int REQURST_HANDLER_SPORTDATA = 0x23;
    private final int REQURST_HANDLER_IDENTITY = 0x24;
    private final int REQURST_HANDLER_CURRENT_RATE = 0x25;
    public static final String RATE_CHANGED = "com.lxkj.administrator.fealty.fragment.HealthDataFragment";
    private HealthRateReceiver mReceiver = null;
    @Override
    protected void init() {
        handler = new MyHandler();
        map = new TreeMap<>();
        //设置horizontalScrollvView拉到头和尾的时候没有阴影颜色
        horiView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }
    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver!=null)
        getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public void onGetBunndle(Bundle arguments) {
        super.onGetBunndle(arguments);
        if (TextUtils.equals(arguments.getString("parentPhone"), SessionHolder.user.getMobile())) {
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
                EventBus.getDefault().post(new NavFragmentEvent(new LocaltionFragment(), bundle));
                break;
            default:
                break;
        }
    }

    double lat = 0.0;
    double lon = 0.0;
    String describle = null;

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REQURST_HANDLER_GPSDATA://处理GPS数据
                    Bundle GPSData = msg.getData();
                    String address = GPSData.getString("address");
                    tv_gps.setText(address);
                    //把lon ,lat ,地址描述传到GPs定位页面显示在百度地图上
                    String latstr = GPSData.getString("lat");
                    lat = Double.parseDouble(latstr);
                    String lonStr = GPSData.getString("lon");
                    lon = Double.parseDouble(lonStr);
                    describle = GPSData.getString("locationdescrible");
                    Log.e("setGPS", describle);
                    break;
                case REQURST_HANDLER_SlEEPDATA://处理睡眠数据
                    Bundle sleepData = msg.getData();
                    String light_hour = sleepData.getString("light_hour");
                    String light_minute = sleepData.getString("light_minute");
                    String deep_hour = sleepData.getString("deep_hour");
                    String deep_minute = sleepData.getString("deep_minute");
                    String total_hour_str = sleepData.getString("total_hour_str");
                    shuimiantotal.setText("睡眠  " + total_hour_str + "小时");
                    shuimiandetail2.setText("浅度睡眠" + light_hour + "小时" + light_minute + "分钟");
                    shuimiandetail.setText("深度度睡眠" + deep_hour + "小时" + deep_minute + "分钟");
                    break;
                case REQURST_HANDLER_SPORTDATA://处理运动数据
                    Bundle stepData = msg.getData();
                    String calories = stepData.getString("calories");
                    String steps = stepData.getString("step");
                    String distance = stepData.getString("distance");
                    yundongtotal.setText("运动  " + distance + "公里");
                    yundongdetail.setText("消耗" + calories + "卡路里");
                    yundongdetail2.setText("步行" + steps + "步");
                    break;
                case REQURST_HANDLER_IDENTITY://处理身份标识
                    String indentity = (String) msg.obj;
                    mPpView.setFirstText(indentity);
                    mPpView.postInvalidate();
                    break;
                case REQURST_HANDLER_CURRENT_RATE://处理当前心率
                    int currentRate = msg.arg1;
                    mPpView.setFountText(currentRate + "");
                    mPpView.invalidate();
                    //把实时心率传到定位页面
                    Intent intent=new Intent();
                    intent.putExtra("tempRate", currentRate);
                    intent.setAction(LocaltionFragment.RATE_CHANGED);
                    getActivity().sendBroadcast(intent);
                    break;
                default:
                    break;
            }
        }
    }
    private void initChart(TreeMap<Integer, Integer> map) {
        mLineChart03View.reset(map);
        mLineChart03View_left.reset(map);
        mLineChart03View.postInvalidate();
        mLineChart03View_left.postInvalidate();
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
        }
        Message message = Message.obtain();
        message.what = REQURST_HANDLER_SlEEPDATA;
        message.setData(sleepData);
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
        Message message = Message.obtain();
        message.obj = currentHeart;
        if (TextUtils.isEmpty(currentHeart)) {
            return;
        }
        message.what = REQURST_HANDLER_CURRENT_RATE;
        handler.sendMessage(message);
    }
}
