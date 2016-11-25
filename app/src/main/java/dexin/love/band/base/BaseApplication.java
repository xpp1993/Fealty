package dexin.love.band.base;

import android.app.Application;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

import com.tencent.bugly.crashreport.CrashReport;

import  org.xutils.x;

import cn.jpush.android.api.JPushInterface;
import dexin.love.band.BuildConfig;
import dexin.love.band.baidugps.LocationService;
import dexin.love.band.utils.CommonTools;

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
        /**
         * 如果App使用了多进程且各个进程都会初始化Bugly（例如在Application类onCreate()中初始化Bugly），
         * 那么每个进程下的Bugly都会进行数据上报，造成不必要的资源浪费。
         因此，为了节省流量、内存等资源，建议初始化的时候对上报进程进行控制，只在主进程下上报数据：
         判断是否是主进程（通过进程名是否为包名来判断），并在初始化Bugly时增加一个上报进程的策略配置。
         */
        //获取当前报名
        String packageName=context.getPackageName();
        //获取当前进程名
        String processName= CommonTools.getProcessName(android.os.Process.myPid());
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        /**
         * 第三个参数为sdk调试的模式开关调试模式的行为特性如下：
         输出详细的Bugly SDK的Log；
         每一条Crash都会被立即上报；
         自定义日志将会在Logcat中输出。
         建议在测试阶段建议设置成true，发布时设置为false。
         */
        CrashReport.initCrashReport(getApplicationContext(), "900059611", true, strategy);
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
