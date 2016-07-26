package com.lxkj.administrator.fealty.base;

import android.app.Application;
import android.content.Context;

import com.lxkj.administrator.fealty.BuildConfig;

import  org.xutils.x;

/**
 * Created by Administrator on 2016/7/26.
 */
public class BaseApplication extends Application{
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        //注册xutils3
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);
    }
}
