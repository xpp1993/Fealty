package com.lxkj.administrator.fealty.base;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

import com.baidu.mapapi.SDKInitializer;
import com.lxkj.administrator.fealty.BuildConfig;
import com.lxkj.administrator.fealty.baidugps.LocationService;

import  org.xutils.x;

/**
 * Created by Administrator on 2016/7/26.
 */
public class BaseApplication extends Application{
    public static Context context;
    public LocationService locationService;
    public Vibrator mVibrator;

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        //注册xutils3
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);
        /***
         * 初始化定位sdk，建议在Application中创建
         */
        locationService = new LocationService(context);
        mVibrator =(Vibrator)context.getSystemService(Service.VIBRATOR_SERVICE);
        SDKInitializer.initialize(context);
    }
}
