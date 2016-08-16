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
import android.widget.TextView;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.BaseFragment;
import com.lxkj.administrator.fealty.bean.SleepData;
import com.lxkj.administrator.fealty.bean.SportData;
import com.lxkj.administrator.fealty.view.LineChart03View;
import com.lxkj.administrator.fealty.view.LineChart03View_left;
import com.lxkj.administrator.fealty.view.PPView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.TreeMap;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/8/4.
 */
@ContentView(R.layout.fragment_healthdata)
public class HealthDataFragement extends BaseFragment {
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
    private HealthDataReceiver mReceiver = null;
    public static final String DATA_CHANGED = "com.lxkj.administrator.fealty.fragment";
private MyHandler handler=new MyHandler();
    @Override
    public void onGetBunndle(Bundle arguments) {
        super.onGetBunndle(arguments);
        identiy = arguments.getString("identity");
        SportData sportData = (SportData) arguments.getSerializable("sportData");
        SleepData sleepData = (SleepData) arguments.getSerializable("sleepData");
        if (identiy != null && !"".equals(identiy)) {
            TreeMap<Integer, Integer> map = new TreeMap<>();
            map.put(9, 75);
            map.put(10, 84);
            map.put(11, 75);
            map.put(12, 75);
            map.put(13, 79);
            map.put(14, 87);
            map.put(15, 80);
            map.put(16, 75);
            map.put(17, 75);
            initChart(map);
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
//    // 用EventBus 来导航,订阅者
//    public void onEventMainThread(int event) {
//        int tempRate=event;
//        Log.e("2016rate",tempRate+"");
//            if (tempRate!=0){
//                mPpView.setFountText(tempRate+"");
//                mPpView.invalidate();}
//    }

    @Override
    protected void init() {
        //设置horizontalScrollvView拉到头和尾的时候没有阴影颜色
//        EventBus.getDefault().register(this);
        registeHealthDataChangedReceiver();
        horiView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
      //  EventBus.getDefault().unregister(this);
        getActivity().unregisterReceiver(mReceiver);

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {


    }

    private void initChart(TreeMap<Integer, Integer> map) {
        mLineChart03View.reset(map);
        mLineChart03View_left.reset(map);
        mLineChart03View.postInvalidate();
        mLineChart03View_left.postInvalidate();
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
    //广播接受者
    class HealthDataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(HealthDataFragement.DATA_CHANGED)) {
                int tempRate= intent.getIntExtra("tempRate",-1);
             Message msg = new Message();
               msg.what =0x21;
               msg.obj =tempRate ;
              handler.sendMessage(msg);

            }
        }
    }
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x21:
                   int rate= (int) msg.obj;
                    mPpView.setFountText(rate+"");
                    mPpView.invalidate();
                    break;
            }
        }
    }
}
