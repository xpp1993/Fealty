package com.lxkj.administrator.fealty.base;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.lxkj.administrator.fealty.BuildConfig;
import com.lxkj.administrator.fealty.baidugps.LocationService;
import com.lxkj.administrator.fealty.manager.SessionHolder;
import com.lxkj.administrator.fealty.utils.AppUtils;
import com.lxkj.administrator.fealty.utils.ExampleUtil;

import  org.xutils.x;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

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
   //  mVibrator =(Vibrator)context.getSystemService(Service.VIBRATOR_SERVICE);
      //  SDKInitializer.initialize(context);
        //注册极光消息推送
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }

}
