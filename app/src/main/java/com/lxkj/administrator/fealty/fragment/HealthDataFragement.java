package com.lxkj.administrator.fealty.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import com.baidu.location.LocationClientOption;
import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.activity.LocationActivity;
import com.lxkj.administrator.fealty.baidugps.LocationService;
import com.lxkj.administrator.fealty.baidugps.MyLocationListener;
import com.lxkj.administrator.fealty.base.BaseApplication;
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
import com.lxkj.administrator.fealty.view.LineChart03View;
import com.lxkj.administrator.fealty.view.LineChart03View_left;
import com.lxkj.administrator.fealty.view.PPView;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;
import de.greenrobot.event.EventBus;
/**
 * Created by Administrator on 2016/8/4.
 */
@ContentView(R.layout.fragment_healthdata)
public class HealthDataFragement extends BaseFragment implements NetWorkAccessTools.RequestTaskListener, View.OnClickListener {
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
    private String identiy;
    private int steps;
    private int calroies;
    private float distance;
    private int deep_hour, deep_minute, light_hour, light_minute;
    private String total_hour_str;
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
    private HealthDataReceiver mReceiver = null;
    public static final String DATA_CHANGED = "com.lxkj.administrator.fealty.fragment";
    int rate_status;
    private MyHandler handler = new MyHandler();
    public static final int REQUEST_CODE_RATE = 0x22;//把测试的心率数据上传到服务器
    TreeMap<Integer, Integer> map = new TreeMap<>();
    private double lat;
    private double lon;

    private MyLocationListener.CallBack mCallBack ;

    @Override
    public void onGetBunndle(Bundle arguments) {
        super.onGetBunndle(arguments);
        identiy = arguments.getString("identity");
        SportData sportData = (SportData) arguments.getSerializable("sportData");
        SleepData sleepData = (SleepData) arguments.getSerializable("sleepData");
        //加载数据
        loadData(sportData, sleepData);
    }

    @Override
    protected void init() {
        //设置horizontalScrollvView拉到头和尾的时候没有阴影颜色
        registeHealthDataChangedReceiver();
        mPpView.setFountText(rate_status + "");
        mPpView.invalidate();
        horiView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mRegisterReceiver();
        mCallBack = new MyLocationListener.CallBack() {
            @Override
            public void callYou(double lat, double lon) {
                HealthDataFragement.this.lat =lat;
                HealthDataFragement.this.lon = lon;
            }
        };
    }

    private void mRegisterReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);

    }
    @Override
    protected void initListener() {
       im_dingwei.setOnClickListener(this);
    }
    LocationService locService;
    LocationClientOption mOption;
    MyLocationListener mListener;
    @Override
    protected void initData() {

    }
    private void initChart(TreeMap<Integer, Integer> map) {
        mLineChart03View.reset(map);
        mLineChart03View_left.reset(map);
        mLineChart03View.postInvalidate();
        mLineChart03View_left.postInvalidate();
    }

    @Override
    public void onStop() {
        locService.unregisterListener(mListener); //注销掉监听
        locService.stop(); //停止定位服务
        super.onStop();
    }

    @Override
    public void onStart() {

        //数据初始化，如果identity是我,定位
        if (identiy.equals("我的")){
            mListener=new MyLocationListener(tv_gps,mCallBack);
            locService=  ((BaseApplication) AppUtils.getBaseContext()).locationService;
            //注册监听
            locService.registerListener(mListener);
            mOption = new LocationClientOption();
            mOption=locService.getDefaultLocationClientOption();
            mOption.setOpenAutoNotifyMode(60*1000*15,100,LocationClientOption.LOC_SENSITIVITY_HIGHT);
            locService.setLocationOption(mOption);
            locService.start();// 定位SDK
        }
        super.onStart();
    }
    private void loadData(SportData sportData, SleepData sleepData) {
        if (identiy != null && !"".equals(identiy)) {
            mPpView.setFirstText(identiy);
            mPpView.postInvalidate();
        }
        if (sportData != null) {
            steps = sportData.getSteps();
            calroies = sportData.getCalories();
            distance = sportData.getDistance();
            yundongtotal.setText("运动  " + new java.text.DecimalFormat("0.00").format(distance) + "公里");
            yundongdetail.setText("消耗" + calroies + "卡路里");
            yundongdetail2.setText("步行" + steps + "步");
        }
        if (sleepData != null) {
            deep_hour = sleepData.getDeep_hour();
            deep_minute = sleepData.getDeep_minute();
            light_hour = sleepData.getLight_hour();
            light_minute = sleepData.getLight_minute();
            total_hour_str = sleepData.getTotal_hour_str();
            shuimiantotal.setText("睡眠  " + total_hour_str + "小时");
            shuimiandetail2.setText("浅度睡眠" + light_hour + "小时" + light_minute + "分钟");
            shuimiandetail.setText("深度度睡眠" + deep_hour + "小时" + deep_minute + "分钟");
        }
    }
    private void registeHealthDataChangedReceiver() {
        IntentFilter filter = new IntentFilter();
        try {
            if (mReceiver != null) {
                getActivity().unregisterReceiver(mReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mReceiver = new HealthDataReceiver();
        filter.addAction(HealthDataFragement.DATA_CHANGED);
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public void onRequestStart(int requestCode) {

    }
    @Override
    public void onRequestLoading(int requestCode, long current, long count) {

    }

    @Override
    public void onRequestSuccess(JSONObject jsonObject, int requestCode) {
        switch (requestCode) {
            case REQUEST_CODE_RATE:
                try {
                    DecodeManager.decodeCommon(jsonObject, requestCode, handler);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
    @Override
    public void onRequestFail(int requestCode, int errorNo) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dingwei:
                //跳转到百度地图
                Intent intent=new Intent(getActivity(), LocationActivity.class);
               intent.putExtra("lon",lon);
               intent.putExtra("lat",lat);
                startActivity(intent);
                break;
            default:
                break;
        }

    }
    //广播接受者
    class HealthDataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(HealthDataFragement.DATA_CHANGED)) {
                int tempRate = intent.getIntExtra("tempRate", -1);
                rate_status = tempRate;
                Message msg = new Message();
                msg.what = 0x21;
                msg.obj = tempRate;
                handler.sendMessage(msg);
            }
        }
    }
    int hour;
    int minute;
    //监听系统时间的广播
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_TICK)) {
                Calendar calendar = Calendar.getInstance();
                minute = calendar.get(Calendar.MINUTE);
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                //把心率和时间发给服务器
                handler.postDelayed(runnable, 1000);
            }
        }
    };
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (minute == 0&&rate_status!=0) {
                //画心率折线图
                map.put(hour, rate_status);
                initChart(map);
                Map<String, String> map = CommonTools.getParameterMap(new String[]{"mobile", "heartRate", "uploadTime"}, SessionHolder.user.getMobile(), rate_status + "", hour + "");
                NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.INSERT_RATE, map, null, REQUEST_CODE_RATE, HealthDataFragement.this);

            }
        }
    };
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x21:
                    int rate = (int) msg.obj;
                    //如果心率不正常，报警信息上传
                    mPpView.setFountText(rate + "");
                    mPpView.invalidate();
                    break;
                case REQUEST_CODE_RATE:
                    break;
            }
        }
    }
}
