package dexin.love.band.base;

import android.app.Application;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

import  org.xutils.x;

import cn.jpush.android.api.JPushInterface;
import dexin.love.band.BuildConfig;
import dexin.love.band.baidugps.LocationService;

/**
 * Created by Administrator on 2016/7/26.
 */
public class BaseApplication extends Application{
    public static Context context;
    public LocationService locationService;
    public Vibrator mVibrator;
    public NotificationManager notificationManager;
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
        mVibrator =(Vibrator)context.getSystemService(Service.VIBRATOR_SERVICE);//使用Vibrator控制手机振动，获取系统的vibrator的服务
    notificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);//获取状态通知栏管理
        //  SDKInitializer.initialize(context);
        //注册极光消息推送
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }
//    /**
//     *设置通知提示方式 - 基础属性
//     */
//    private void setStyleBasic(){
//        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(PushSetActivity.this);
//        builder.statusBarDrawable = R.drawable.ic_launcher;
//        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL;  //设置为点击后自动消失
//        builder.notificationDefaults = Notification.DEFAULT_SOUND;  //设置为铃声（ Notification.DEFAULT_SOUND）或者震动（ Notification.DEFAULT_VIBRATE）
//        JPushInterface.setPushNotificationBuilder(1, builder);
//        Toast.makeText(PushSetActivity.this, "Basic Builder - 1", Toast.LENGTH_SHORT).show();
//    }
//
//
//    /**
//     *设置通知栏样式 - 定义通知栏Layout
//     */
//    private void setStyleCustom(){
//        CustomPushNotificationBuilder builder = new CustomPushNotificationBuilder(PushSetActivity.this,R.layout.customer_notitfication_layout,R.id.icon, R.id.title, R.id.text);
//        builder.layoutIconDrawable = R.drawable.ic_launcher;
//        builder.developerArg0 = "developerArg2";
//        JPushInterface.setPushNotificationBuilder(2, builder);
//        Toast.makeText(PushSetActivity.this,"Custom Builder - 2", Toast.LENGTH_SHORT).show();
//    }
}
