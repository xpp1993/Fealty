package com.example.xpp.blelib;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class BroadcastManager {
    private static final String TAG = BroadcastManager.class.getSimpleName();

    private static void sendBroadcast(Context context, String intentAction, Bundle bundle) {
        Intent intent = new Intent(intentAction);
        if (bundle != null)
            intent.putExtras(bundle);
        Log.e(TAG, "sendBroadcast " + intentAction);
        context.sendBroadcast(intent);
    }

    public static void sendBroadcast4ConnectState(Context context, String intentAction, boolean state) {
        Bundle bundle = new Bundle();
        bundle.putString(GlobalValues.NAME_CONNECT_STATE, state ? GlobalValues.VALUE_CONNECT_STATE_YES : GlobalValues.VALUE_CONNECT_STATE_NO);
        sendBroadcast(context, intentAction, bundle);
    }

    public static void sendBroadcast4Electricity(Context context, String intentAction, String electricity,String isElectricize) {
        Bundle bundle = new Bundle();
        bundle.putString(GlobalValues.NAME_ELECTRICITY, electricity);
        bundle.putString(GlobalValues.NAME_ISELECTRICITE,isElectricize);
        sendBroadcast(context, intentAction, bundle);
    }

    /**
     * 发送当前运动信息广播
     */

    public static void sendBroadcast4CURRENTMOTION(Context context, String intentAction, String devTime, String steps, int distance, int calories, int sleep) {
        Bundle bundle = new Bundle();
        bundle.putString(GlobalValues.NAME_DEVICE_TIME, devTime);
        bundle.putString(GlobalValues.NAME_STEPS, steps);
        bundle.putInt(GlobalValues.NAME_DISTANCE, distance);
        bundle.putInt(GlobalValues.NAME_CALORIES, calories);
        bundle.putInt(GlobalValues.NAME_SLEEP, sleep);
        sendBroadcast(context, intentAction, bundle);
    }

    /**
     * 发送指令送达广播
     */

    public static void sendBroadcast4CommandReceived(Context context, String intentAction) {
        sendBroadcast(context, intentAction, null);
    }
    /**
     * 发送睡眠质量数据广播
     */
    public static void sendBroadcast4SleepQuality(Context context, String intentAction, int lightSleep, int deepSleep) {
        Bundle bundle = new Bundle();
        bundle.putInt(GlobalValues.NAME_DEEPSLEEP, deepSleep);
        bundle.putInt(GlobalValues.NAME_LIGHTSLEEP, lightSleep);
        sendBroadcast(context, intentAction, bundle);
    }
    /**
     * 发送心率测试数据广播
     */
    public static void sendBroadcast4RateData(Context context, String intentAction, String testTime, int rate,int status) {
        Bundle bundle = new Bundle();
        bundle.putString(GlobalValues.NAME_RATETIME,testTime);
        bundle.putInt(GlobalValues.NAME_RATE,rate);
        bundle.putInt(GlobalValues.NAME_RATE_STATUS,status);
        sendBroadcast(context, intentAction, bundle);
    }
}
