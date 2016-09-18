package com.lxkj.administrator.fealty.fragment;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.baidu.location.LocationClientOption;
import com.leaking.slideswitch.SlideSwitch;
import com.lxkj.administrator.fealty.MainActivity;
import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.activity.ScanDeviceActivity;
import com.lxkj.administrator.fealty.baidugps.LocationService;
import com.lxkj.administrator.fealty.baidugps.MyLocationListener;
import com.lxkj.administrator.fealty.base.BaseApplication;
import com.lxkj.administrator.fealty.base.BaseFragment;
import com.lxkj.administrator.fealty.base.BaseView;
import com.lxkj.administrator.fealty.bean.Contacts;
import com.lxkj.administrator.fealty.bean.RateListData;
import com.lxkj.administrator.fealty.bean.SleepData;
import com.lxkj.administrator.fealty.bean.SportData;
import com.lxkj.administrator.fealty.event.NavFragmentEvent;
import com.lxkj.administrator.fealty.manager.DecodeManager;
import com.lxkj.administrator.fealty.manager.ParameterManager;
import com.lxkj.administrator.fealty.manager.SPManager;
import com.lxkj.administrator.fealty.manager.SessionHolder;
import com.lxkj.administrator.fealty.utils.AppUtils;
import com.lxkj.administrator.fealty.utils.CommonTools;
import com.lxkj.administrator.fealty.utils.MySqliteHelper;
import com.lxkj.administrator.fealty.utils.NetWorkAccessTools;
import com.lxkj.administrator.fealty.utils.ThreadPoolUtils;
import com.lxkj.administrator.fealty.utils.ToastUtils;
import com.lxkj.administrator.fealty.utils.XinLvSqliteHelper;
import com.lxkj.administrator.fealty.view.DialogView;
import com.lxkj.administrator.fealty.view.DialogViewC;
import com.lxkj.administrator.fealty.view.DialogViewLo;
import com.lxkj.administrator.fealty.view.DialogViewRate;
import com.lxkj.administrator.fealty.view.DialogViewSleepLow;
import com.lxkj.administrator.fealty.view.DialogViewSleephigh;
import com.lxkj.administrator.fealty.view.DialogViewSportLow;
import com.lxkj.administrator.fealty.view.DialogViewSporthigh;
import com.yc.peddemo.sdk.BLEServiceOperate;
import com.yc.peddemo.sdk.BluetoothLeService;
import com.yc.peddemo.sdk.DataProcessing;
import com.yc.peddemo.sdk.ICallback;
import com.yc.peddemo.sdk.ICallbackStatus;
import com.yc.peddemo.sdk.RateChangeListener;
import com.yc.peddemo.sdk.SleepChangeListener;
import com.yc.peddemo.sdk.StepChangeListener;
import com.yc.peddemo.sdk.UTESQLOperate;
import com.yc.peddemo.sdk.WriteCommandToBLE;
import com.yc.peddemo.utils.CalendarUtils;
import com.yc.peddemo.utils.GlobalVariable;
import com.yc.pedometer.info.RateOneDayInfo;
import com.yc.pedometer.info.SleepTimeInfo;
import com.yc.pedometer.info.StepInfo;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/7/26.
 */
@ContentView(R.layout.fragement_me)
public class MeFragment extends BaseFragment implements View.OnClickListener, NetWorkAccessTools.RequestTaskListener, ICallback, TextToSpeech.OnInitListener {
    @ViewInject(R.id.fragment_me_details)
    private RelativeLayout fragment_me_details;//个人资料
    @ViewInject(R.id.relative_shezhi)
    private RelativeLayout me_shezhi;//设置
    @ViewInject(R.id.relative_about)
    private RelativeLayout relative_about;//关于我们
    @ViewInject(R.id.relative_binded)
    private RelativeLayout binded_action;//绑定用户
    @ViewInject(R.id.relative_location)
    private RelativeLayout relative_location;//我的路线查看
    @ViewInject(R.id.me_headpic)
    private CircleImageView circleImageView;
    @ViewInject(R.id.me_username)
    private TextView me_username;
    @ViewInject(R.id.me_phone)
    private TextView me_phone;
    @ViewInject(R.id.bluee_iv_left)
    private SlideSwitch bluee_iv_left;
    public static final int REQUEST_USER_BYMIBILE = 0x01;
    private Handler myHandler = new MyHandler();
    private BLEServiceOperate mBLEServiceOperate;
    private final int DISCONNECT_MSG = 18;
    private final int CONNECTED_MSG = 19;
    private BluetoothLeService mBluetoothLeService;
    private UTESQLOperate mySQLOperate;//数据库操作类
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private final int CONNECTED = 1;
    private final int DISCONNECTED = 3;
    private int CURRENT_STATUS = DISCONNECTED;
    private int RATE_STATUS = 95;
    private WriteCommandToBLE mWriteCommand;
    private String TAG = "xpp";
    private DataProcessing mDataProcessing;
    private final int UPDATA_REAL_RATE_MSG = 20;//处理心率监测数据
    private final int OFFLINE_RATE_SYNC_OK = 0x123;
    public static final int REQUEST_CODE_UPLOAD_CONTACTS = 0X20;//上传通讯录
    public static final int REQUEST_CODE_SPORTDATA_SLEEPDATA = 0x10;//把运动数据睡眠数据上传到服务器
    public static final int REQUEST_CODE_CURRENTRATE = 0x22;//把测试的心率数据上传到服务器
    public static final int REQUEST_CODE_RATE = 0x26;//把测试的心率数据上传到服务器
    private String phone;
    public static final int GPS_UPLOAD_CODE = 0x11;
    @ViewInject(R.id.relative_location)
    private RelativeLayout see_myGPSINFO;
    @ViewInject(R.id.bar_tv_title_center)
    private TextView bar_biaoti;
    @ViewInject(R.id.bar_view_left_line)
    private ImageView bar_view_left_line;
    private static final String[] CONTACTOR_NEED = new String[]{    //联系人字段
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.Contacts.DISPLAY_NAME
    };
    private double lat;
    private double lon;
    private String locationdescrible;
    private String address;
    private MyLocationListener.CallBack mCallback;
    private MySqliteHelper helper;
    private long currentTime;
    @ViewInject(R.id.jiancebaojing)
    private SlideSwitch jiancebaojing;//监测报警信息
    XinLvSqliteHelper xinlvhelper;
    SQLiteDatabase xinlvdb;
    private SharedPreferences preferences;
    int sportMinRate;
    int sportMaxRate;
    int norMin;
    int norMax;
    int sleepMaxRate;
    int sleepMinRate;
    private boolean message_shark;//是否震动
    private boolean message_sound;//是否声音提醒
    private boolean message_dialog;//是否弹窗提醒
    private boolean message_yuyin;//是否语音提醒
    private Vibrator mVibrator;
    private NotificationManager notificationManager;//1.获取状态栏通知管理器
    private TextToSpeech tts;

    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        bar_view_left_line.setVisibility(View.VISIBLE);
        bar_biaoti.setVisibility(View.VISIBLE);
        bar_biaoti.setText("我");
        //1.获得sharedPreference对象,SharedPrefences只能放基础数据类型，不能放自定义数据类型。
        preferences = SPManager.getSharedPreferences(AppUtils.getBaseContext());
        mCallback = new MyLocationListener.CallBack() {
            @Override
            public void callYou(double lat, double lon, String loctiondescrible, String address, MySqliteHelper helper, long currentTime) {
                MeFragment.this.lat = lat;
                MeFragment.this.lon = lon;
                MeFragment.this.locationdescrible = loctiondescrible;
                MeFragment.this.address = address;
                MeFragment.this.helper = helper;
                MeFragment.this.currentTime = currentTime;
                Log.e("xpp", loctiondescrible);
                if (address != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("parentPhone", SessionHolder.user.getMobile());
                    bundle.putDouble("lon", lon);
                    bundle.putDouble("lat", lat);
                    bundle.putString("describle", locationdescrible);
                    bundle.putString("address", address);
                    EventBus.getDefault().post(bundle);//把定位数据数据返回到首页
                    myHandler.postDelayed(runnable6, 2000);//停止定位
                }
            }
        };
        //初始化个人资料显示
        initPersonalDataShow();
        Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile"}, SessionHolder.user.getMobile());
        NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.GET_USER_BYMOBILE, params, null, MeFragment.REQUEST_USER_BYMIBILE, this);
        sp = getActivity().getSharedPreferences(GlobalVariable.SettingSP, 0);
        editor = sp.edit();
        mySQLOperate = new UTESQLOperate(AppUtils.getBaseContext());
        mBLEServiceOperate = BLEServiceOperate.getInstance(AppUtils.getBaseContext());// 用于BluetoothLeService实例化准备,必须
        mRegisterReceiver();
        xinlvhelper = new XinLvSqliteHelper(AppUtils.getBaseContext());
        xinlvdb = xinlvhelper.getReadableDatabase();
        mVibrator = ((BaseApplication) AppUtils.getBaseContext()).mVibrator;
        notificationManager = ((BaseApplication) AppUtils.getBaseContext()).notificationManager;
        tts = new TextToSpeech(AppUtils.getBaseContext(), this);
    }

    /**
     * 初始化个人资料显示
     */
    private void initPersonalDataShow() {
        if (TextUtils.isEmpty(SessionHolder.user.getUserpic()) || "".equals(SessionHolder.user.getUserpic())) {
            circleImageView.setImageResource(R.mipmap.unknow_head);
        } else {
           // NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).toLoadImage("http://192.168.8.133:8080" + "/" + SessionHolder.user.getUserpic(), circleImageView, R.mipmap.unknow_head, R.mipmap.unknow_head);
           NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).toLoadImage("http://120.76.27.233:8080" + "/" + SessionHolder.user.getUserpic(), circleImageView, R.mipmap.unknow_head, R.mipmap.unknow_head);
        }
        me_username.setText(TextUtils.isEmpty(SessionHolder.user.getNickName()) ? "未设置" : SessionHolder.user.getNickName());
        me_phone.setText(TextUtils.isEmpty(SessionHolder.user.getMobile()) ? "未设置" : "手机号:" + SessionHolder.user.getMobile());
    }

    private void mRegisterReceiver() {
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(GlobalVariable.READ_BATTERY_ACTION);//监测手环电量
        mFilter.addAction(Intent.ACTION_BATTERY_CHANGED);//获取手机电量
        mFilter.addAction(GlobalVariable.READ_BLE_VERSION_ACTION);
        getActivity().registerReceiver(mReceiver, mFilter);
    }

    @Override
    protected void initListener() {
        fragment_me_details.setOnClickListener(this);//跳转到个人信息页
        binded_action.setOnClickListener(this);
        see_myGPSINFO.setOnClickListener(this);
        me_shezhi.setOnClickListener(this);
        relative_about.setOnClickListener(this);
        relative_location.setOnClickListener(this);
        bluee_iv_left.setSlideListener(new SlideSwitch.SlideListener() {
            @Override
            public void open() {
                Intent intent = new Intent(getActivity(), ScanDeviceActivity.class);
                startActivity(intent);
            }

            @Override
            public void close() {
            }
        });
        jiancebaojing.setSlideListener(new SlideSwitch.SlideListener() {
            @Override
            public void open() {
                //跳转到报警模式设置页面
                EventBus.getDefault().post(new NavFragmentEvent(new fragment_BaojinMode()));
            }

            @Override
            public void close() {

            }
        });
    }

    @Override
    protected void initData() {
        //判断是否是新的一天
        // JudgeNewDayWhenResume();
        readSP();//读取文件中的值
        startGps();//开始定位
    }

    // 用EventBus 来导航,订阅者
    public void onEventMainThread(String event) {
        if (event.equals(getResources().getString(R.string.reflush))) {
            initPersonalDataShow();
        } else if (event.contains(":")) {
            Log.e(" deviceAddress", event);
            mBluetoothLeService = mBLEServiceOperate.getBleService();
            mBluetoothLeService.setICallback(this);
            mDataProcessing = DataProcessing.getInstance(AppUtils.getBaseContext());//获得数据库处理类
            mDataProcessing.setOnSleepChangeListener(mOnSlepChangeListener);//设置睡眠监听
            mDataProcessing.setOnRateListener(mOnRateListener);//心率监听
            mDataProcessing.setOnStepChangeListener(mOnStepChangeListener);//计步监听
            mWriteCommand = new WriteCommandToBLE(AppUtils.getBaseContext());
            mBLEServiceOperate.connect(event);//连接手环
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mBLEServiceOperate.unBindService();// unBindService
        getActivity().unregisterReceiver(mReceiver);
        locService.unregisterListener(mListener); //注销掉监听
        locService.stop(); //停止定位服务
        if (runnable != null)
            myHandler.removeCallbacks(runnable);
        if (runnable1 != null)
            myHandler.removeCallbacks(runnable1);
        if (runnable2 != null)
            myHandler.removeCallbacks(runnable2);
        if (runnable3 != null)
            myHandler.removeCallbacks(runnable3);
        if (runnable4 != null)
            myHandler.removeCallbacks(runnable4);
        if (runnable_updataGPS != null)
            myHandler.removeCallbacks(runnable_updataGPS);
        if (runnable_updataGPS2 != null)
            myHandler.removeCallbacks(runnable_updataGPS2);
        if (runnable_updataGPS3 != null)
            myHandler.removeCallbacks(runnable_updataGPS3);
        if (runnable6 != null)
            myHandler.removeCallbacks(runnable6);
        if (runable_startGps != null)
            myHandler.removeCallbacks(runable_startGps);
        if (runnable_rate != null)
            myHandler.removeCallbacks(runnable_rate);
        if (helper != null) {
            helper.close();
        }
        if (warn_runnable != null) {
            myHandler.removeCallbacks(warn_runnable);
        }
        if (xinlvhelper != null) {
            xinlvhelper.close();
        }
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        if (delatesqlite != null) {
            myHandler.removeCallbacks(delatesqlite);
        }
        mBLEServiceOperate.disConnect();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_me_details:
                EventBus.getDefault().post(new NavFragmentEvent(new MeSettingFragment()));//跳转到个人信息详情页
                break;
            case R.id.relative_binded://绑定用户
                //1.获取手机联系人
                ArrayList<Contacts> contacts = (ArrayList<Contacts>) getContacts(AppUtils.getBaseContext());
                //2.上传通讯录,返回通讯录中,注册过  的 用户列表
                Log.e("telephoe", SessionHolder.user.getMobile());
                Map<String, String> params = CommonTools.getParameterMap(new String[]{"contact_list", "mobile"}, uploadContacts(contacts), SessionHolder.user.getMobile());
                NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.UPLOAD_CONTACTS_LIST, params, null, REQUEST_CODE_UPLOAD_CONTACTS, MeFragment.this);
                break;
            case R.id.relative_location:
                //跳转到百度地图
                Melucheng_fragment melucheng_fragment = new Melucheng_fragment();
                melucheng_fragment.setHelper(helper);
                Bundle bundle = new Bundle();
                bundle.putDouble("lat", MeFragment.this.lat);
                bundle.putDouble("lon", MeFragment.this.lon);
                bundle.putLong("currentTime", currentTime);
                melucheng_fragment.setArguments(bundle);
                EventBus.getDefault().post(new NavFragmentEvent(melucheng_fragment, bundle));
                break;
            case R.id.relative_shezhi:
                //跳入设置界面
                EventBus.getDefault().post(new NavFragmentEvent(new Fragment_Shezhi()));
                break;
            default:
                break;
        }
    }

    LocationService locService;
    LocationClientOption mOption;
    MyLocationListener mListener;

    /**
     * 开始定位
     */
    private void startGps() {
        //开始定位
        mListener = new MyLocationListener(mCallback);
        Log.e("mefragment", lon + "," + lat);
        locService = ((BaseApplication) AppUtils.getBaseContext()).locationService;
        //注册监听
        locService.registerListener(mListener);
        mOption = new LocationClientOption();
        mOption = locService.getDefaultLocationClientOption();
        mOption.setOpenAutoNotifyMode(1000, 50, LocationClientOption.LOC_SENSITIVITY_HIGHT);
        locService.setLocationOption(mOption);
        locService.start();// 定位SDK
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
            case REQUEST_USER_BYMIBILE:
                try {
                    DecodeManager.decodeSelfDataQuery(jsonObject, REQUEST_USER_BYMIBILE, myHandler);
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.showToastInUIThread("服务器异常！");
                }
            case REQUEST_CODE_UPLOAD_CONTACTS:
                try {//上传手机通讯录得到的返回数据
                    DecodeManager.decodeFriendMessage(jsonObject, REQUEST_CODE_UPLOAD_CONTACTS, myHandler);

                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.showToastInUIThread("服务器异常！");
                }
                break;
            case REQUEST_CODE_SPORTDATA_SLEEPDATA:
                try {
                    DecodeManager.decodeCommon(jsonObject, requestCode, myHandler);
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.showToastInUIThread("服务器异常！");
                }
                break;
            case REQUEST_CODE_CURRENTRATE://上传实时心率
                try {
                    DecodeManager.decodeCommon(jsonObject, requestCode, myHandler);
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.showToastInUIThread("服务器异常！");
                }
                break;
            case GPS_UPLOAD_CODE:
                try {
                    DecodeManager.decodeCommon(jsonObject, requestCode, myHandler);
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.showToastInUIThread("服务器异常！");
                }
                break;
            case REQUEST_CODE_RATE:
                try {
                    DecodeManager.decodeCommon(jsonObject, requestCode, myHandler);
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.showToastInUIThread("服务器异常！");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestFail(int requestCode, int errorNo) {
        // ToastUtils.showToastInUIThread("网络连接错误，请检查重试！");
    }

    /**
     * 文字转化为语音，初始化
     *
     * @param status
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.CHINA);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REQUEST_USER_BYMIBILE://个人信息查询
                    if (msg.getData().getInt("code") == 1) {//请求成功
                        String mobile = msg.getData().getString("mobile");
                        String nickname = msg.getData().getString("nickName");
                        String userpic = msg.getData().getString("headFile");
                        String sex = msg.getData().getString("sex");
                        String birthday = msg.getData().getString("birthday");
                        SessionHolder.user.setMobile(mobile);
                        SessionHolder.user.setNickName(nickname);
                        SessionHolder.user.setUserpic(userpic);
                        SessionHolder.user.setGender(sex);
                        SessionHolder.user.setBirthday(birthday);
                    } else {//请求失败
                        ToastUtils.showToastInUIThread(msg.getData().getString("desc"));
                        Log.w("service error", msg.getData().getString("desc"));
                    }
                    initPersonalDataShow();
                    break;
                case GlobalVariable.SERVER_IS_BUSY_MSG:
                    ToastUtils.showToastInUIThread("服务器繁忙");
                    break;
                case DISCONNECT_MSG://报警并尝试连接
                    CURRENT_STATUS = DISCONNECTED;
                    readSP();
                    DialogViewC dialogView = null;
                    if (message_dialog == true) {
                        dialogView = new DialogViewC(AppUtils.getBaseContext());
                    }
                    diffNotifyShow(1, getResources().getString(R.string.disconnected), dialogView);
                    String lastConnectAddr0 = sp.getString(
                            GlobalVariable.LAST_CONNECT_DEVICE_ADDRESS_SP, "");
                    boolean connectResute0 = mBLEServiceOperate
                            .connect(lastConnectAddr0);
                    Log.i(TAG, "connectResute0=" + connectResute0);
                    break;
                case CONNECTED_MSG:
                    mBluetoothLeService.setRssiHandler(myHandler);
                    CURRENT_STATUS = CONNECTED;
                    ToastUtils.showToastInUIThread("已连接");
                    break;
                case UPDATA_REAL_RATE_MSG://处理接收到的心率数据.把心率数据写入数据库
                    Bundle rate_bundle = msg.getData();
                    int rate = rate_bundle.getInt("rate");
                    String rate_time = rate_bundle.getString("time");
                    xinlvdb.insert("xinlv", null, toContentValues(rate_time, rate, SessionHolder.user.getMobile()));
                    readSP();
                    if (rate < norMin || rate > norMax) {//心率不正常，app响铃报警
                        rateAbnormalNotify();
                        myHandler.postDelayed(runnable_updataGPS3, 1000 * 60 * ce_int);
                    }
                    //上传到服务器
                    Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile", "uploadTime", "currentHeart"}, SessionHolder.user.getMobile(), rate_time, rate + "");
                    NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.INSERT_CURRENTRATE, params, null, REQUEST_CODE_CURRENTRATE, MeFragment.this);
                    break;
                case OFFLINE_RATE_SYNC_OK://同步心率数据
                    RateOneDayInfo rateOneDayInfo = mySQLOperate.queryRateOneDayMainInfo(CalendarUtils.getCalendar(0));
                    if (rateOneDayInfo != null) {
                        int rateToday = rateOneDayInfo.getCurrentRate();//获得今天最后一次测得的心率值
                        //发给界面显示
                        Intent intent = new Intent();
                        intent.putExtra("tempRate", rateToday);
                        intent.setAction(HealthDataFragement.RATE_CHANGED);
                        getActivity().sendBroadcast(intent);
                        //判断心率是否异常
                        readSP();
                        if (RATE_STATUS < norMin || RATE_STATUS > norMax) {//心率不正常，app响铃报警，他人接收到报警
                            myHandler.postDelayed(warn_runnable, 1000);
                            //心率不正常，实时实时上传GPS信息。然后每隔三分钟上传一次。
                            myHandler.postDelayed(runnable_updataGPS3, 1000 * 60 * ce_int);
                        }
                    }
                    break;
                case REQUEST_CODE_UPLOAD_CONTACTS://处理上传手机通讯录返回的数据
                    if (msg.getData().getInt("code") == 1) {//请求成功
                        if (msg.getData().getSerializable("old_people_list") != null) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("old_people_list", msg.getData().getSerializable("old_people_list"));
                            EventBus.getDefault().post(new NavFragmentEvent(new OlsManListFragment(), bundle));
                        }
                    }
                    break;
                case OFFLINE_STEP_SYNC_OK://同步了离线运动数据，显示,且上传服务器
                    queryStepInfo();
                    break;
                case OFFLINE_SLEEP_SYNC_OK://同步了离线睡眠数据，显示,且上传服务器
                    querySleepInfo();
                    break;
                case REQUEST_CODE_SPORTDATA_SLEEPDATA:
                    if (msg.getData().getInt("code") == 1) {
                        Log.e("upLoad:", msg.getData().getString("desc"));
                    }
                    break;
                case REQUEST_CODE_RATE:
                    if (msg.getData().getInt("code") == 1) {
                        Log.e("upLoad:", msg.getData().getString("desc"));
                        ToastUtils.showToastInUIThread("心率数据已上传到服务器！");
                    }
                    break;
                case REQUEST_CODE_CURRENTRATE://上传实时心率
                    if (msg.getData().getInt("code") == 1) {
                        Log.e("upLoad:", msg.getData().getString("desc"));
                        Log.e("currentRate", " 实时心率数据已上传到服务器!");
                    }
                    break;
                case GPS_UPLOAD_CODE:
                    if (msg.getData().getInt("code") == 1) {
                        Log.e("upLoad:", msg.getData().getString("desc"));
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private Runnable warn_runnable = new Runnable() {
        @Override
        public void run() {
            rateAbnormalNotify();
        }
    };

    // 将数据封装为ContentValues,心率数据局放入数据库
    public ContentValues toContentValues(String time, int rate, String phone) {
        // 底部类似于Map
        ContentValues values = new ContentValues();
        // 将值跟列一起放到ConentValues里。
        values.put("time", time);
        values.put("rate", rate);
        values.put("phone", phone);
        return values;
    }

    private void functionRateFinished() {
        mWriteCommand.sendRateTestCommand(GlobalVariable.RATE_TEST_STOP);//发送心率测试关闭
        //讲写入数据库的心率读取出来，执行上传收集到的心率测试数据，上传成功后清空
        Log.e("wyj", "functionRateFinished");
        com.alibaba.fastjson.JSONObject object = new com.alibaba.fastjson.JSONObject();
        JSONArray jsonArray = new JSONArray();
        List<RateListData> rateList = new ArrayList<>();
        Map<String, List<RateListData>> map = new HashMap<>();
        RateListData rateListData = null;
        com.alibaba.fastjson.JSONObject jsonObject = null;
        Cursor cursor = xinlvdb.query("xinlv", null, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            jsonObject = new com.alibaba.fastjson.JSONObject();
            rateListData = new RateListData();
            //获取，第二列和第三列的值 ，time,rate
            String rate_time = cursor.getString(1);
            int rate = cursor.getInt(2);
            rateListData.setRate(rate);
            rateListData.setTime(rate_time);
            jsonObject.put("rate_time", rate_time);
            jsonObject.put("rate", rate);
            jsonArray.add(jsonObject);
            rateList.add(rateListData);
            if (rateList.size() == 7)
                break;
        }
        cursor.close();
        map.put(SessionHolder.user.getMobile(), rateList);
        object.put("heartRate", jsonArray);
        object.put("mobile", SessionHolder.user.getMobile());
        String jsonString = object.toJSONString();
        if (rateList.size() == 0) {
            return;
        }
        //把心率发给首页
        EventBus.getDefault().post(map);
        //  myHandler.postDelayed(d)
        //把心率json数据上传到服务器,上传到服务器之后，清空数据库
        Log.e("json",jsonString);
        Map<String, String> params = CommonTools.getParameterMap(new String[]{"heartRate", "mobile"}, jsonString, SessionHolder.user.getMobile());
        NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.UPLOAD_ZHEXIAN, params, null, REQUEST_CODE_RATE, MeFragment.this);
        myHandler.postDelayed(delatesqlite, 1000);//清空数据库
    }

    /**
     * 电量和蓝牙版本接收
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(GlobalVariable.READ_BLE_VERSION_ACTION)) {
                String version = intent
                        .getStringExtra(GlobalVariable.INTENT_BLE_VERSION_EXTRA);
            } else if (action.equals(GlobalVariable.READ_BATTERY_ACTION)) {
                int battery = intent.getIntExtra(
                        GlobalVariable.INTENT_BLE_BATTERY_EXTRA, -1);
                Log.d("battery=", battery + "");
                Map<String, String> params = new HashMap<>();
                params.put("cuffElectricity", battery + "");
                alterSelfData(params);//发送到服务器
                //电量异常，发送通知
                setNotifyDian2(battery, getResources().getString(R.string.betty));
                //把号码，设备名，设备地址，连接手环状态，设备电量发送给服务器，服务器判断是否低电量，像APP发送消息（）推送,app接收短信并震动
            } else if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                //获取手机电量
                getMobileLevel(intent);
            }
        }
    };

    /**
     * 获取手机电量
     *
     * @param intent
     */
    private void getMobileLevel(Intent intent) {
    /*获得当前电量  */
        int rawlevel = intent.getIntExtra("level", -1);
                /*获得总电量  */
        int scale = intent.getIntExtra("scale", -1);
        int level = -1;
        if (rawlevel >= 0 && scale > 0) {
            level = (rawlevel * 100) / scale;
            Log.e("Battery Level Remaining", level + "%");
            Map<String, String> params = new HashMap<>();
            params.put("mobileElectricity", level + "");
            alterSelfData(params);//上传到服务器
            setNotifyDian(level, getResources().getString(R.string.phonebetty));//电量异常发送通知
        }
    }

    private void setNotifyDian(int electricity, String notice) {
        if (electricity <= 15) {//通知
            DialogView dialogView = null;
            if (message_dialog == true) {
                dialogView = new DialogView(AppUtils.getBaseContext());
            }
            diffNotifyShow(0x123, notice, dialogView);
        }
    }

    private void showDialogWarn(BaseView dialogView, Button button) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //   Looper.prepare();
        final AlertDialog dialog = builder.setView(dialogView).create();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //    Looper.loop();
        dialog.show();
    }

    private void setNotifyDian2(int electricity, String notice) {
        if (electricity <= 15) {//通知
            DialogViewLo dialogView = null;
            if (message_dialog == true) {
                dialogView = new DialogViewLo(AppUtils.getBaseContext());
            }
            diffNotifyShow(0x27, notice, dialogView);
        }
    }

    //通过setContentIntent(PendingIntent intent)方法中的意图设置对应的flags
    public PendingIntent getDefalutIntent(int flags) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(AppUtils.getBaseContext(), 1, intent, flags);
        return pendingIntent;
    }

    private static final int OFFLINE_STEP_SYNC_OK = 5;
    private static final int OFFLINE_SLEEP_SYNC_OK = 7;
    /**
     * 第一个参数，心率值
     * 第二个参数当前心率值得状态 1.测试中 2.测试完成
     * 使用时，需要提前调用 mDataProcessing.setOnRateListener(mOnRateListener);
     */
    private RateChangeListener mOnRateListener = new RateChangeListener() {
        @Override
        public void onRateChange(int rate, int status) {
            Intent intent = new Intent();
            intent.putExtra("tempRate", rate);
            intent.setAction(HealthDataFragement.RATE_CHANGED);
            getActivity().sendBroadcast(intent);
            RATE_STATUS = rate;
            Log.e("wyj", "onRateChange =" + RATE_STATUS);
            //把心率测试数据写入数据库
            //获取系统当前时间
            java.sql.Timestamp time = new java.sql.Timestamp(new java.util.Date().getTime());
            int hour = time.getHours();//时
            int minute = time.getMinutes();//分
            int second = time.getSeconds();//秒
            String rate_time = hour + ":" + minute + ":" + second;//测到此刻心率的时间
            Bundle rate_bundle = new Bundle();
            rate_bundle.putString("time", rate_time);
            rate_bundle.putInt("rate", rate);
            Message message = Message.obtain();
            message.what = UPDATA_REAL_RATE_MSG;
            message.setData(rate_bundle);
            myHandler.sendMessage(message);
        }
    };
    /**
     * 睡眠监听
     */
    private SleepChangeListener mOnSlepChangeListener = new SleepChangeListener() {
        @Override
        public void onSleepChange() {
            judgeSleepRate();
        }
    };

    /**
     * 判断睡眠时的心率是否异常，通知提醒
     */
    private void judgeSleepRate() {
        readSP();
        if (RATE_STATUS < norMin || RATE_STATUS > norMax) {//心率不正常，app响铃报警，他人接收到报警
        } else {
            //表示睡眠状态，在老人睡觉时每隔两小时上传一次手机坐标
            myHandler.postDelayed(runnable_updataGPS2, 1000 * 60 * 60 * sleep_gpsin);
            if (RATE_STATUS < sleepMinRate) {//睡眠时心率偏低，消息通知
                DialogViewSleepLow dialogView = null;
                if (message_dialog == true) {
                    dialogView = new DialogViewSleepLow(AppUtils.getBaseContext());
                }
                diffNotifyShow(0, getResources().getString(R.string.sleeplow), dialogView);
            } else if (RATE_STATUS > sleepMaxRate) {//睡眠时心率偏高，消息通知
                DialogViewSleephigh dialogView = null;
                if (message_dialog == true) {
                    dialogView = new DialogViewSleephigh(AppUtils.getBaseContext());
                }
                diffNotifyShow(0, getResources().getString(R.string.sporthight), dialogView);
            }
        }
    }

    /**
     * 计步监听
     */
    private StepChangeListener mOnStepChangeListener = new StepChangeListener() {
        @Override
        public void onStepChange(int steps, float distance, int calories) {
            Log.d("onStepHandler", "steps =" + steps + ",distance =" + distance
                    + ",calories =" + calories);
            SportData sportData = new SportData();
            sportData.setCalories(calories);
            sportData.setSteps(steps);
            sportData.setDistance(distance);
            sportData.setParentphone(SessionHolder.user.getMobile());
            //把计步监听的结果上传服务器
            updataSportData(steps, calories, distance);
            EventBus.getDefault().post(sportData);
            //  myHandler.sendEmptyMessage(UPDATE_STEP_UI_MSG);
            //判断心率是否偏低偏高
            judgeStepRate();
        }
    };

    private void judgeStepRate() {
        readSP();
        if (RATE_STATUS < norMin || RATE_STATUS > norMax) {//心率不正常，app响铃报警，上传服务器，他人接收到报警
            //  rateAbnormalNotify();
        } else {
            //运动时每隔15分钟上传一次GPS
            myHandler.postDelayed(runnable_updataGPS, 1000 * 60 * sport_gpsint);
            if (RATE_STATUS < sportMinRate) {//运动时心率偏低，消息通知
                DialogViewSportLow dialogView = null;
                if (message_dialog == true) {
                    dialogView = new DialogViewSportLow(AppUtils.getBaseContext());
                }
                diffNotifyShow(0, getResources().getString(R.string.sportlow), dialogView);
            } else if (RATE_STATUS > sportMaxRate) {//运动时心率偏高，消息通知
                DialogViewSporthigh dialogView = null;
                if (message_dialog == true) {
                    dialogView = new DialogViewSporthigh(AppUtils.getBaseContext());
                }
                diffNotifyShow(0, getResources().getString(R.string.sporthight), dialogView);
            }
        }
    }

    /**
     * 心率异常设置通知
     */
    private void rateAbnormalNotify() {
        DialogViewRate dialogView = null;
        if (message_dialog == true) {
            dialogView = new DialogViewRate(AppUtils.getBaseContext());
        }
        diffNotifyShow(0, getResources().getString(R.string.xinlvabnormal), dialogView);
    }

    /**
     * 不同的报警方式，震动，声音，弹框，语音
     *
     * @param noId
     * @param context
     * @param dialogView
     */
    private void diffNotifyShow(int noId, String context, final BaseView dialogView) {
        if (message_shark == true) {//如果打开震动
            setNotification(noId, Notification.DEFAULT_VIBRATE, context);
        } else if (message_sound == true) {//如果打开声音
            setNotification(noId, Notification.DEFAULT_SOUND, context);
        } else if (message_dialog == true) {//如果打开弹窗提醒
            final Button button = dialogView.getButton();
            ThreadPoolUtils.runTaskOnUIThread(new Runnable() {
                @Override
                public void run() {
                    showDialogWarn(dialogView, button);
                }
            });

        } else if (message_yuyin == true) {//如果打开语音提醒
            speakOut(context);
            try {
                Thread.sleep(1800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 文本转语音
     */
    private void speakOut(String str) {
        tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);
    }

    /**
     * 设置通知
     *
     * @param noId
     * @param defaults
     * @param content
     */
    private void setNotification(int noId, int defaults, String content) {
        //2,实例化通知栏构造器
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MeFragment.this.getActivity());
        //3.对Build进行配置
        mBuilder.setContentTitle("有新消息")
                .setContentText(content)
                .setTicker("通知来了")
                .setWhen(System.currentTimeMillis())
                .setDefaults(defaults)
                .setSmallIcon(R.mipmap.baojing)
                .setContentIntent(getDefalutIntent(Notification.FLAG_INSISTENT)) //让声音、振动无限循环，直到用户响应 （取消或者打开）
                .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))//设置通知栏点击意图, 用户单击通知后自动消失;
                .setPriority(Notification.PRIORITY_HIGH);//设置该通知优先级;
        notificationManager.notify(noId, mBuilder.build());
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
        message_shark = preferences.getBoolean(ParameterManager.MESSAGE_zhend, true);//默认震动提醒
        message_sound = preferences.getBoolean(ParameterManager.MESSAGE_SOUND, false);
        message_dialog = preferences.getBoolean(ParameterManager.MESSAGE_dialog, false);
        message_yuyin = preferences.getBoolean(ParameterManager.MESSAGE_yuyin, false);
    }

    /**
     * 返回今天的步数、距离、卡路里的集合
     */
    private void queryStepInfo() {
        StepInfo stepInfo = mySQLOperate.queryStepInfo(CalendarUtils.getCalendar(0));
        if (stepInfo != null) {
            int step = stepInfo.getStep();//运动的步数
            int calories = stepInfo.getCalories();//卡路里
            float distance = stepInfo.getDistance();//距离
            SportData sportData = new SportData(step, calories, distance, SessionHolder.user.getMobile());
            //把这些数据上传到服务器
            updataSportData(step, calories, distance);
            //发给首页显示
            EventBus.getDefault().post(sportData);
        } else {
            Log.e("stepData", stepInfo + "");
        }
    }

    //把运动数据上传到服务器
    private void updataSportData(int step, int calories, float distance) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("step", step + "");
        parameters.put("calories", calories + "");
        parameters.put("distance", distance + "");
        alterSelfData(parameters);
    }

    /**
     * 获取今天睡眠详细，并更新睡眠UI CalendarUtils.getCalendar(0)代表今天，也可写成"2016811"
     * CalendarUtils.getCalendar(-1)代表昨天，也可写成"2016810"
     * CalendarUtils.getCalendar(-2)代表前天，也可写成"201689" 以此类推
     */
    private void querySleepInfo() {
        SleepTimeInfo sleepTimeInfo = mySQLOperate.querySleepInfo(
                CalendarUtils.getCalendar(-1), CalendarUtils.getCalendar(0));
        int deepTime, lightTime, awakeCount, sleepTotalTime;
        if (sleepTimeInfo != null) {
            deepTime = sleepTimeInfo.getDeepTime();
            lightTime = sleepTimeInfo.getLightTime();
            awakeCount = sleepTimeInfo.getAwakeCount();
            sleepTotalTime = sleepTimeInfo.getSleepTotalTime();
            double total_hour = ((float) sleepTotalTime / 60f);
            DecimalFormat df1 = new DecimalFormat("0.0"); // 保留1位小数，带前导零
            int deep_hour = deepTime / 60;
            Log.e("deep", deep_hour + "");
            // int deep_minute = (deepTime - deep_hour * 60);
            int deep_minute = deepTime % 60;
            int light_hour = lightTime / 60;
            // int light_minute = (lightTime - light_hour * 60);
            int light_minute = lightTime % 60;
            Log.e("light", light_hour + "");
            int active_count = awakeCount;
            String total_hour_str = df1.format(total_hour);
            SleepData sleepData = new SleepData(deep_hour, deep_minute, light_hour, light_minute, active_count, total_hour_str, SessionHolder.user.getMobile());
            //把这些数据上传到服务器
            updataSleepData(deep_hour, light_hour, light_minute, total_hour_str);
            EventBus.getDefault().post(sleepData);//把数据传到首页面
            if (total_hour_str.equals("0.0")) {
                total_hour_str = "0";
            }
        } else {
            Log.d("getSleepInfo", "sleepTimeInfo =" + sleepTimeInfo);
        }
    }

    //把睡眠数据上传到服务器
    private void updataSleepData(int deep_hour, int light_hour, int light_minute, String total_hour_str) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("total_hour_str", total_hour_str);
        parameters.put("light_hour", light_hour + "");
        parameters.put("light_minute", light_minute + "");
        parameters.put("deep_hour", deep_hour + "");
        parameters.put("light_minute", light_minute + "");
        alterSelfData(parameters);
    }

    /**
     * mBluetoothLeService.setICallback(this)检测返回的数据
     */
    @Override
    public void OnResult(boolean result, int status) {
        Log.i(TAG, "result=" + result + ",status=" + status);
        if (status == ICallbackStatus.CONNECTED_STATUS) {
            myHandler.sendEmptyMessage(CONNECTED_MSG);
            //发送同步时间指令
            // myHandler.postDelayed(runnable, 1000);
            myHandler.postDelayed(runnable, 500);
        } else if (status == ICallbackStatus.DISCONNECT_STATUS) {
            myHandler.sendEmptyMessage(DISCONNECT_MSG);
        } else if (status == ICallbackStatus.SYNC_TIME_OK) {// 设置时间操作完成，发送同步睡眠数据数据指令
            //  myHandler.postDelayed(runnable2, 1000);
            myHandler.postDelayed(runnable2, 500);
        } else if (status == ICallbackStatus.OFFLINE_SLEEP_SYNC_OK) {//离线睡眠同步完成,发送同步心率数据指令
            //把睡眠数据发给健康监测页，且上传到服务器
            myHandler.sendEmptyMessage(OFFLINE_SLEEP_SYNC_OK);
            // myHandler.postDelayed(runnable_rate, 1000);//同步心率数据
            myHandler.postDelayed(runnable_rate, 500);//同步心率数据
        } else if (status == ICallbackStatus.OFFLINE_RATE_SYNC_OK) {//如果同步心率完成，发送同步运动数据指令
            // myHandler.postDelayed(runnable1, 1000);
            myHandler.postDelayed(runnable1, 500);
            myHandler.sendEmptyMessage(OFFLINE_RATE_SYNC_OK);
        } else if (status == ICallbackStatus.OFFLINE_STEP_SYNC_OK) {//离线运动同步完成，发送请求电量指令,开始测试心率指令
            myHandler.sendEmptyMessage(OFFLINE_STEP_SYNC_OK);//把离线同步的运动数据发送给首页
            myHandler.postDelayed(runnable4, 1000 * 60);//请求手环电量
            myHandler.postDelayed(runnable3, 1000 * 60 * rate_int);//发送心率测试开始
            // myHandler.postDelayed(runnable5, 1000);//把数据上传到服务器
        } else if (status == ICallbackStatus.GET_BLE_BATTERY_OK) {
        }
    }

    /**
     * 同步蓝牙端时间指令
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mBluetoothLeService != null) {
                mWriteCommand.syncBLETime();
            }
        }
    };
    /**
     * 同步计步数据指令
     */
    private Runnable runnable1 = new Runnable() {
        @Override
        public void run() {
            if (mBluetoothLeService != null) {
                mWriteCommand.syncAllStepData();
            }
        }
    };
    /**
     * 同步睡眠数据指令
     */
    private Runnable runnable2 = new Runnable() {
        @Override
        public void run() {
            if (mBluetoothLeService != null) {
                mWriteCommand.syncAllSleepData();
            }
        }
    };
    /**
     * 同步心率数据指令
     */
    private Runnable runnable_rate = new Runnable() {
        @Override
        public void run() {
            if (mBluetoothLeService != null) {
                mWriteCommand.syncAllRateData();
            }
        }
    };
    /**
     * 发送心率测试开启
     */
    int rate_int;
    private Runnable runnable3 = new Runnable() {
        @Override
        public void run() {
            mWriteCommand.sendRateTestCommand(GlobalVariable.RATE_TEST_START);
            Log.e("wyj", "start to rate");
            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    functionRateFinished();
                }
            }, 1000 * 30);
            String rate_ji = preferences.getString(ParameterManager.SHEZHI_JIANCEXINLV, "3");
            rate_int = Integer.parseInt(rate_ji);
            myHandler.postDelayed(this, 1000 * 60 * rate_int);

        }
    };
    /**
     * 删除数据库内容
     */
    private Runnable delatesqlite = new Runnable() {
        @Override
        public void run() {
            long xinlv_id = xinlvdb.delete("xinlv", null, null);
            Log.e("xinlv_id", xinlv_id + "");
        }
    };
    /**
     * 请求获取电量指令
     */
    private Runnable runnable4 = new Runnable() {
        @Override
        public void run() {
            if (mBluetoothLeService != null) {
                mBluetoothLeService.readRssi();
                mWriteCommand.sendToReadBLEBattery();
            }
            myHandler.postDelayed(this, 1000 * 60);
        }
    };
    int sport_gpsint = 15;
    //运动时每15分钟上传一次GPS
    private Runnable runnable_updataGPS = new Runnable() {
        @Override
        public void run() {
            Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile", "locationdescrible", "address", "lat", "lon"}, SessionHolder.user.getMobile(), locationdescrible, address, String.valueOf(lat), String.valueOf(lon));
            NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.GPS_UPLOAD_URL, params, null, GPS_UPLOAD_CODE, MeFragment.this);
            String sport_gps = preferences.getString(ParameterManager.SHEZHI_SPORT_GPS, "15");
            sport_gpsint = Integer.parseInt(sport_gps);
            myHandler.postDelayed(this, 1000 * 60 * sport_gpsint);
        }
    };
    int sleep_gpsin = 2;
    //睡眠时每两个小时上传一次GPS
    private Runnable runnable_updataGPS2 = new Runnable() {
        @Override
        public void run() {
            Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile", "locationdescrible", "address", "lat", "lon"}, SessionHolder.user.getMobile(), locationdescrible, address, String.valueOf(lat), String.valueOf(lon));
            NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.GPS_UPLOAD_URL, params, null, GPS_UPLOAD_CODE, MeFragment.this);
            String sleep_gps = preferences.getString(ParameterManager.SHEZHI_SLEEP_GPS, "2");
            sleep_gpsin = Integer.parseInt(sleep_gps);
            myHandler.postDelayed(this, 1000 * 60 * 60 * sleep_gpsin);
        }
    };
    int ce_int = 3;
    //心率异常时，没三分钟上传一次GPS
    private Runnable runnable_updataGPS3 = new Runnable() {
        @Override
        public void run() {
            Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile", "locationdescrible", "address", "lat", "lon"}, SessionHolder.user.getMobile(), locationdescrible, address, String.valueOf(lat), String.valueOf(lon));
            NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.GPS_UPLOAD_URL, params, null, GPS_UPLOAD_CODE, MeFragment.this);
            String ce_gps = preferences.getString(ParameterManager.SHEZHI_JIANCEGPS, "3");
            ce_int = Integer.parseInt(ce_gps);
            myHandler.postDelayed(this, 1000 * 60 * ce_int);
        }
    };

    /**
     * 上传睡眠和运动数据到服务器
     */
    private void alterSelfData(Map<String, String> parameters) {
        Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile", "total_hour_str", "light_hour", "light_minute", "deep_hour", "deep_minute", "calories", "distance", "step", "cuffElectricity", "mobileElectricity"},
                SessionHolder.user.getMobile(), "", "", "", "", "", "", "", "", "", "");
        params.putAll(parameters);
        try {
            NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.UPDATE_SLEEP_SPORT, params, null, REQUEST_CODE_SPORTDATA_SLEEPDATA, MeFragment.this);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showToastInUIThread("程序错误");
        }
    }

    /**
     * GPS定位相关
     */
    private Runnable runnable6 = new Runnable() {
        @Override
        public void run() {
            //停止定位
            locService.stop();
            //三分钟后开始定位
            String ce_gps = preferences.getString(ParameterManager.SHEZHI_JIANCEGPS, "3");
            ce_int = Integer.parseInt(ce_gps);
            myHandler.postDelayed(runable_startGps, 1000 * 60 * ce_int);
        }
    };
    private Runnable runable_startGps = new Runnable() {
        @Override
        public void run() {
            locService.start();
        }
    };

    /**
     * 上传通讯录
     */
    private String uploadContacts(ArrayList<Contacts> contacts) {
        com.alibaba.fastjson.JSONObject object = new com.alibaba.fastjson.JSONObject();
        JSONArray jsonArray = new JSONArray();
        com.alibaba.fastjson.JSONObject jsonObject = null;
        Log.e("Tag", contacts.size() + "");
        for (int i = 0; i < contacts.size(); i++) {
            Contacts contact = contacts.get(i);
            phone = contact.getPhone();
            jsonObject = new com.alibaba.fastjson.JSONObject();
            jsonObject.put("mobile", phone);
            jsonArray.add(jsonObject);
        }
        object.put("contact_list", jsonArray);
        String jsonString = object.toJSONString();
        System.out.println(jsonString);
        return jsonString;
    }

    /**
     * 获取手机联系人的方法
     */
    public static List<Contacts> getContacts(Context context) {
        ArrayList<Contacts> contacts = new ArrayList<Contacts>();
        Cursor phones = null;
        ContentResolver cr = context.getContentResolver();
        try {
            phones = cr
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                            , CONTACTOR_NEED, null, null, null);
            if (phones != null) {
                final int contactIdIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
                final int displayNameIndex = phones.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int phoneIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String phoneString, displayNameString, contactIdString;
                while (phones.moveToNext()) {
                    try {
                        contactIdString = phones.getString(contactIdIndex);    //id
                        phoneString = phones.getString(phoneIndex);    //号码
                        if (TextUtils.isEmpty(phoneString)) {
                            continue;
                        }
                        phoneString = phoneString.replace(" ", "");
                        if (phoneString.length() > 11) {
                            phoneString = phoneString.substring(phoneString.length() - 11, phoneString.length());
                        }
                        displayNameString = phones.getString(displayNameIndex);    //名称
                        if (TextUtils.isEmpty(displayNameString)) {
                            continue;
                        }
                        contacts.add(new Contacts(contactIdString, displayNameString, phoneString));
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (phones != null)
                phones.close();
        }
        return contacts;
    }
}
