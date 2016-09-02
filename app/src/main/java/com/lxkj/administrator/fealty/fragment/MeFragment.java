package com.lxkj.administrator.fealty.fragment;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.avast.android.dialogs.fragment.ListDialogFragment;
import com.avast.android.dialogs.iface.IListDialogListener;
import com.baidu.location.LocationClientOption;
import com.leaking.slideswitch.SlideSwitch;
import com.lxkj.administrator.fealty.MainActivity;
import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.baidugps.LocationService;
import com.lxkj.administrator.fealty.baidugps.MyLocationListener;
import com.lxkj.administrator.fealty.base.BaseApplication;
import com.lxkj.administrator.fealty.base.BaseFragment;
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
import com.lxkj.administrator.fealty.utils.ToastUtils;
import com.lxkj.administrator.fealty.utils.XinLvSqliteHelper;
import com.yc.peddemo.sdk.BLEServiceOperate;
import com.yc.peddemo.sdk.BluetoothLeService;
import com.yc.peddemo.sdk.DataProcessing;
import com.yc.peddemo.sdk.DeviceScanInterfacer;
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
import java.util.Map;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/7/26.
 */
@ContentView(R.layout.fragement_me)
public class MeFragment extends BaseFragment implements View.OnClickListener, NetWorkAccessTools.RequestTaskListener, DeviceScanInterfacer, IListDialogListener, ICallback {
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
    private boolean mScanning;
    private ArrayList<BluetoothDevice> mDevice;
    private String[] devicesInfo;
    private String deviceinfo;
    private String deviceAddress;
    private String deviceName;
    private final int DISCONNECT_MSG = 18;
    private final int CONNECTED_MSG = 19;
    private static final int REQUEST_ENABLE = 2;
    private static final int REQUEST_LIST_SINGLE = 11;
    private static final int REQUEST_LIST_SIMPLE = 9;
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
    private Handler mHandler;
    private DataProcessing mDataProcessing;
    private final int UPDATA_REAL_RATE_MSG = 20;//处理心率监测数据
    //    private final int UPDATE_STEP_UI_MSG = 0;//同步计步数据
//    private final int UPDATE_SLEEP_UI_MSG = 23;//同步睡眠数据
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
    private SharedPreferences.Editor editor1;
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

    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        bar_view_left_line.setVisibility(View.VISIBLE);
        bar_biaoti.setVisibility(View.VISIBLE);
        bar_biaoti.setText("我");
        //1.获得sharedPreference对象,SharedPrefences只能放基础数据类型，不能放自定义数据类型。
        preferences = SPManager.getSharedPreferences(AppUtils.getBaseContext());
        //2. 获得编辑器:当将数据存储到SharedPrefences对象中时，需要获得编辑器。如果取出则不需要。
        editor1 = preferences.edit();
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
        mHandler = new Handler();
        mDevice = new ArrayList<>();
        mBLEServiceOperate = BLEServiceOperate.getInstance(AppUtils.getBaseContext());// 用于BluetoothLeService实例化准备,必须
        mRegisterReceiver();
        xinlvhelper = new XinLvSqliteHelper(AppUtils.getBaseContext());
        xinlvdb = xinlvhelper.getReadableDatabase();
        mVibrator = ((BaseApplication) AppUtils.getBaseContext()).mVibrator;
        notificationManager = ((BaseApplication) AppUtils.getBaseContext()).notificationManager;
    }


    /**
     * 初始化个人资料显示
     */
    private void initPersonalDataShow() {
        if (TextUtils.isEmpty(SessionHolder.user.getUserpic()) || "".equals(SessionHolder.user.getUserpic())) {
            circleImageView.setImageResource(R.mipmap.unknow_head);
        } else {
            NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).toLoadImage("http://192.168.8.133:8080" + "/" + SessionHolder.user.getUserpic(), circleImageView, R.mipmap.unknow_head, R.mipmap.unknow_head);
            // NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).toLoadImage("120.76.27.233:8080" + "/" + SessionHolder.user.getUserpic(), circleImageView, R.mipmap.unknow_head, R.mipmap.unknow_head);
        }
        me_username.setText(TextUtils.isEmpty(SessionHolder.user.getNickName()) ? "未设置" : SessionHolder.user.getNickName());
        me_phone.setText(TextUtils.isEmpty(SessionHolder.user.getMobile()) ? "未设置" : "手机号:" + SessionHolder.user.getMobile());
    }

    private void mRegisterReceiver() {
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(GlobalVariable.READ_BATTERY_ACTION);//监测手环电量
        //  mFilter.addAction("android.intent.action.BATTERY_LOW");//监测手机电量
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
                if (!mBLEServiceOperate.isSupportBle4_0()) {
                    ToastUtils.showToastInUIThread("设备不支持蓝牙4.0");
                    return;
                }
                mBLEServiceOperate.setDeviceScanListener(MeFragment.this);//for DeviceScanInterfacer
                if (!mBLEServiceOperate.isBleEnabled()) {
                    Intent enableBtIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE);
                }
                scanLeDevice(true);
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
        String str = event.getBytes().toString();
        initPersonalDataShow();
    }


    //2016-8-21 xpp add
    @Override
    public void onStop() {
        super.onStop();
//        locService.unregisterListener(mListener); //注销掉监听
//        locService.stop(); //停止定位服务
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
        if (runnable6 != null)
            myHandler.removeCallbacks(runnable6);
        if (runable_startGps != null)
            myHandler.removeCallbacks(runable_startGps);
        if (runnable_rate != null)
            myHandler.removeCallbacks(runnable_rate);
        if (helper != null) {
            helper.close();
        }
        if (xinlvhelper != null) {
            xinlvhelper.close();
        }
//        if (xinlvdb != null) {
//            xinlvdb.close();
//        }
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

    //开始扫描设备
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBLEServiceOperate.stopLeScan();
                }
            }, 10000);
            mScanning = true;
            mBLEServiceOperate.startLeScan();
        } else {
            mScanning = false;
            mBLEServiceOperate.stopLeScan();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE
                && resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        //  ToastUtils.showToastInUIThread("网络连接错误，请检查重试！");
    }

    @Override
    public void LeScanCallback(final BluetoothDevice bluetoothDevice, final int j) {


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDevice.add(bluetoothDevice);
                if (mDevice.size() == 0) {
                    ToastUtils.showToastInUIThread("未扫描到设备！");
                    return;
                }
                devicesInfo = new String[mDevice.size()];
                for (int i = 0; i < mDevice.size(); i++) {
                    BluetoothDevice bluetoothDevice1 = mDevice.get(i);
                    deviceAddress = bluetoothDevice1.getAddress();
                    deviceName = bluetoothDevice1.getName();
                    if (deviceName != null && deviceName.length() > 0) {
                        deviceinfo = deviceName + "\n" + deviceAddress;
                    } else {
                        deviceinfo = "未知设备" + "\n" + deviceAddress;
                    }
                    devicesInfo[i] = deviceinfo;
                    Log.d("devicesInfo", deviceinfo);
                }
                ListDialogFragment.createBuilder(getActivity(), getActivity().getSupportFragmentManager()).setTitle("扫描到设备如下：")
                        .setItems(devicesInfo)
                        .setCancelButtonText("取消")
                        .setConfirmButtonText("确定")
                        .setRequestCode(REQUEST_LIST_SINGLE)
                        .setChoiceMode(AbsListView.CHOICE_MODE_SINGLE)
                        .setTargetFragment(MeFragment.this, REQUEST_LIST_SINGLE)
                        .show();
                mDevice.clear();
                //2016-8-13
                if (mScanning) {
                    mBLEServiceOperate.stopLeScan();
                    mScanning = false;
                }
            }
        });
    }

    // IListDialogListener
    @Override
    public void onListItemSelected(CharSequence value, int number, int requestCode) {
        if (requestCode == REQUEST_LIST_SINGLE || requestCode == REQUEST_LIST_SIMPLE) {
            mBluetoothLeService = mBLEServiceOperate.getBleService();
            mBluetoothLeService.setICallback(this);
            mDataProcessing = DataProcessing.getInstance(AppUtils.getBaseContext());//获得数据库处理类
            mDataProcessing.setOnSleepChangeListener(mOnSlepChangeListener);//设置睡眠监听
            mDataProcessing.setOnRateListener(mOnRateListener);//心率监听
            mDataProcessing.setOnStepChangeListener(mOnStepChangeListener);//计步监听
            mWriteCommand = new WriteCommandToBLE(AppUtils.getBaseContext());
            mBLEServiceOperate.connect(deviceAddress);//连接手环
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
                    if (message_shark == true) {//如果打开震动
                        setNotification(1, Notification.DEFAULT_VIBRATE, getResources().getString(R.string.disconnected));
                    } else if (message_sound == true) {//如果打开声音
                        setNotification(1, Notification.DEFAULT_SOUND, getResources().getString(R.string.disconnected));

                    } else if (message_dialog == true) {//如果打开弹窗提醒

                    } else if (message_yuyin == true) {//如果打开语音提醒

                    }
                    // bluee_iv_left.setSlideable(true);
                    String lastConnectAddr0 = sp.getString(
                            GlobalVariable.LAST_CONNECT_DEVICE_ADDRESS_SP, "");
                    boolean connectResute0 = mBLEServiceOperate
                            .connect(lastConnectAddr0);
                    Log.i(TAG, "connectResute0=" + connectResute0);
                    break;
                case CONNECTED_MSG:
                    mBluetoothLeService.setRssiHandler(myHandler);
                    CURRENT_STATUS = CONNECTED;
                    // bluee_iv_left.setSlideable(false);
                    ToastUtils.showToastInUIThread("已连接");
                    break;
                case UPDATA_REAL_RATE_MSG://处理接收到的心率数据.把心率数据写入数据库
                    // Log.d("tempRate", tempRate + "");
                    Bundle rate_bundle = msg.getData();
                    int rate = rate_bundle.getInt("rate");
                    String rate_time = rate_bundle.getString("time");
                    xinlvdb.insert("xinlv", null, toContentValues(rate_time, rate, SessionHolder.user.getMobile()));
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
                            rateAbnormalNotify();
                        }
                    }
                    break;
//                case UPDATE_STEP_UI_MSG://返回今天的步数、距离、卡路里的集合
//                    queryStepInfo();
                //   break;
                case REQUEST_CODE_UPLOAD_CONTACTS://处理上传手机通讯录返回的数据
                    if (msg.getData().getInt("code") == 1) {//请求成功
                        if (msg.getData().getSerializable("old_people_list") != null) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("old_people_list", msg.getData().getSerializable("old_people_list"));
                            EventBus.getDefault().post(new NavFragmentEvent(new OlsManListFragment(), bundle));
                        }
                    }
                    break;
                case NEW_DAY_MSG:
                    mySQLOperate.updateStepSQL();//新一天初始化步行数据库
                    mySQLOperate.updateSleepSQL();//新一天初始化睡眠数据库
                    mySQLOperate.updateRateSQL();//新一天初始化心率数据库
                    // mySQLOperate.isDeleteRefreshTable();//  mySQLOperate.isDeleteRateTable(CalendarUtils.getCalendar(-1));
                    // resetValues();
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
                        ToastUtils.showToastInUIThread("GPS数据已上传到服务器！");
                    }
                    break;
                default:
                    break;
            }
        }
    }

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
        Map<String, String> params = CommonTools.getParameterMap(new String[]{"heartRate", "mobile"}, jsonString, SessionHolder.user.getMobile());
        NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.UPLOAD_ZHEXIAN, params, null, REQUEST_CODE_RATE, MeFragment.this);
        myHandler.postDelayed(delatesqlite, 2000);//清空数据库
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
            if (message_shark == true) {//如果打开震动
                setNotification(0x123, Notification.DEFAULT_VIBRATE, notice);
            } else if (message_sound == true) {//如果打开声音
                setNotification(0x123, Notification.DEFAULT_SOUND, notice);

            } else if (message_dialog == true) {//如果打开弹窗提醒

            } else if (message_yuyin == true) {//如果打开语音提醒

            }
        }
    }

    private void setNotifyDian2(int electricity, String notice) {
        if (electricity <= 15) {//通知
            if (message_shark == true) {//如果打开震动
                setNotification(0x27, Notification.DEFAULT_VIBRATE, notice);
            } else if (message_sound == true) {//如果打开声音
                setNotification(0x27, Notification.DEFAULT_SOUND, notice);

            } else if (message_dialog == true) {//如果打开弹窗提醒

            } else if (message_yuyin == true) {//如果打开语音提醒

            }
        }
    }

    //通过setContentIntent(PendingIntent intent)方法中的意图设置对应的flags
    public PendingIntent getDefalutIntent(int flags) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        // PendingIntent pendingIntent = PendingIntent.getActivity(AppUtils.getBaseContext(), 1, new Intent(), flags);
        PendingIntent pendingIntent = PendingIntent.getActivity(AppUtils.getBaseContext(), 1, intent, flags);
        return pendingIntent;
    }

    private static final int NEW_DAY_MSG = 3;
    private static final int OFFLINE_STEP_SYNC_OK = 5;
    private static final int OFFLINE_SLEEP_SYNC_OK = 7;

//    //判断是否是新的一天
//    private void JudgeNewDayWhenResume() {
//        boolean isFirstOpenAPK = sp.getBoolean(GlobalVariable.FIRST_OPEN_APK, true);
//        editor.putBoolean(GlobalVariable.FIRST_OPEN_APK, false);
//        editor.commit();
//        int lastDay = sp.getInt(GlobalVariable.LAST_DAY_NUMBER_SP, 0);
//        String lastDayString = sp.getString(GlobalVariable.LAST_DAY_CALLENDAR_SP,
//                CalendarUtils.getCalendar(-1));
//        Calendar c = Calendar.getInstance();
//        int currentDay = c.get(Calendar.DAY_OF_YEAR);
//        String currentDayString = CalendarUtils.getCalendar(0);
//        if (isFirstOpenAPK) {
//            lastDay = currentDay;
//            lastDayString = currentDayString;
//            editor = sp.edit();
//            editor.putInt(GlobalVariable.LAST_DAY_NUMBER_SP, lastDay);
//            editor.putString(GlobalVariable.LAST_DAY_CALLENDAR_SP,
//                    lastDayString);
//            editor.commit();
//        } else {
//
//            if (currentDay != lastDay) {
//                if ((lastDay + 1) == currentDay || currentDay == 1) { // 连续的日期
//                    myHandler.sendEmptyMessage(NEW_DAY_MSG);
//                } else {
//                    mySQLOperate.insertLastDayStepSQL(lastDayString);
//                    mySQLOperate.updateSleepSQL();
//                    resetValues();
//                }
//                lastDay = currentDay;
//                lastDayString = currentDayString;
//                editor.putInt(GlobalVariable.LAST_DAY_NUMBER_SP, lastDay);
//                editor.putString(GlobalVariable.LAST_DAY_CALLENDAR_SP,
//                        lastDayString);
//                editor.commit();
//            } else {
//                Log.d("b1offline", "currentDay == lastDay");
//            }
//        }
//    }

//    private void resetValues() {
//        editor.putInt(GlobalVariable.YC_PED_UNFINISH_HOUR_STEP_SP, 0);
//        editor.putInt(GlobalVariable.YC_PED_UNFINISH_HOUR_VALUE_SP, 0);
//        editor.putInt(GlobalVariable.YC_PED_LAST_HOUR_STEP_SP, 0);
//        editor.commit();
//    }

    /**
     * 第一个参数，心率值
     * 第二个参数当前心率值得状态 1.测试中 2.测试完成
     * 使用时，需要提前调用 mDataProcessing.setOnRateListener(mOnRateListener);
     */
    //  DataHandler dataHandler = new DataHandler();
    private RateChangeListener mOnRateListener = new RateChangeListener() {
        @Override
        public void onRateChange(int rate, int status) {
            Intent intent = new Intent();
            intent.putExtra("tempRate", rate);
            intent.setAction(HealthDataFragement.RATE_CHANGED);
            getActivity().sendBroadcast(intent);
            RATE_STATUS = rate;
            Log.e("wyj", "onRateChange =" + RATE_STATUS);
            readSP();
            if (rate < norMin || rate > norMax) {//心率不正常，app响铃报警
                rateAbnormalNotify();
            }
            //把心率测试数据写入数据库
            //获取系统当前时间
            java.sql.Timestamp time = new java.sql.Timestamp(new java.util.Date().getTime());
            int hour = time.getHours();//时
            int minute = time.getMinutes();//分
            int second = time.getSeconds();//秒
            String rate_time = hour + ":" + minute + ":" + second;//测到此刻心率的时间
           // String rate_time =minute + ":" + second;//测到此刻心率的时间
            Bundle rate_bundle = new Bundle();
            rate_bundle.putString("time", rate_time);
            rate_bundle.putInt("rate", rate);
            Message message = Message.obtain();
            message.what = UPDATA_REAL_RATE_MSG;
            message.setData(rate_bundle);
//          myHandler.sendEmptyMessage(UPDATA_REAL_RATE_MSG);
            myHandler.sendMessage(message);
        }
    };
    /**
     * 睡眠监听
     */
    private SleepChangeListener mOnSlepChangeListener = new SleepChangeListener() {

        @Override
        public void onSleepChange() {
            //表示睡眠状态，在老人睡觉时每隔两小时上传一次手机坐标
            myHandler.postDelayed(runnable_updataGPS2, 1000 * 60 * 60 * sleep_gpsin);
            //心率不正常，实时实时上传GPS信息。然后每隔三分钟上传一次。
            //  myHandler.sendEmptyMessage(UPDATE_SLEEP_UI_MSG);
            judgeSleepRate();
        }
    };

    /**
     * 判断睡眠时的心率是否异常，通知提醒
     */
    private void judgeSleepRate() {
        readSP();
        if (RATE_STATUS < norMin || RATE_STATUS > norMax) {//心率不正常，app响铃报警，他人接收到报警
            //  rateAbnormalNotify();
        } else {
            if (RATE_STATUS < sleepMinRate) {//睡眠时心率偏低，消息通知
                if (message_shark == true) {//如果打开震动
                    setNotification(0, Notification.DEFAULT_VIBRATE, getResources().getString(R.string.sleeplow));
                } else if (message_sound == true) {//如果打开声音
                    setNotification(0, Notification.DEFAULT_SOUND, getResources().getString(R.string.sleeplow));

                } else if (message_dialog == true) {//如果打开弹窗提醒

                } else if (message_yuyin == true) {//如果打开语音提醒

                }
            } else if (RATE_STATUS > sleepMaxRate) {//睡眠时心率偏高，消息通知
                if (message_shark == true) {//如果打开震动
                    setNotification(0, Notification.DEFAULT_VIBRATE, getResources().getString(R.string.sleephight));
                } else if (message_sound == true) {//如果打开声音
                    setNotification(0, Notification.DEFAULT_SOUND, getResources().getString(R.string.sleephight));
                } else if (message_dialog == true) {//如果打开弹窗提醒

                } else if (message_yuyin == true) {//如果打开语音提醒

                }
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
            //运动时每隔15分钟上传一次GPS
            myHandler.postDelayed(runnable_updataGPS, 1000 * 60 * sport_gpsint);
            //判断心率是否偏低偏高
            judgeStepRate();
        }
    };

    private void judgeStepRate() {
        readSP();
        if (RATE_STATUS < norMin || RATE_STATUS > norMax) {//心率不正常，app响铃报警，上传服务器，他人接收到报警
            //  rateAbnormalNotify();
        } else {
            if (RATE_STATUS < sportMinRate) {//运动时心率偏低，消息通知
                if (message_shark == true) {//如果打开震动
                    setNotification(0, Notification.DEFAULT_VIBRATE, getResources().getString(R.string.sportlow));
                } else if (message_sound == true) {//如果打开声音
                    setNotification(0, Notification.DEFAULT_SOUND, getResources().getString(R.string.sportlow));

                } else if (message_dialog == true) {//如果打开弹窗提醒

                } else if (message_yuyin == true) {//如果打开语音提醒

                }
            } else if (RATE_STATUS > sportMaxRate) {//运动时心率偏高，消息通知
                if (message_shark == true) {//如果打开震动
                    setNotification(0, Notification.DEFAULT_VIBRATE, getResources().getString(R.string.sporthight));
                } else if (message_sound == true) {//如果打开声音
                    setNotification(0, Notification.DEFAULT_SOUND, getResources().getString(R.string.sporthight));
                } else if (message_dialog == true) {//如果打开弹窗提醒

                } else if (message_yuyin == true) {//如果打开语音提醒

                }
            }
        }
    }

    /**
     * 心率异常设置通知
     */
    private void rateAbnormalNotify() {
        if (message_shark == true) {//如果打开震动
            setNotification(0, Notification.DEFAULT_VIBRATE, getResources().getString(R.string.xinlvabnormal));
        } else if (message_sound == true) {//如果打开声音
            setNotification(0, Notification.DEFAULT_SOUND, getResources().getString(R.string.xinlvabnormal));
        } else if (message_dialog == true) {//如果打开弹窗提醒

        } else if (message_yuyin == true) {//如果打开语音提醒

        }
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
                .setSmallIcon(R.mipmap.baojing)
                .setDefaults(defaults)
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

//    /**
//     * mBluetoothLeService.setICallback(this)检测返回的数据
//     *
//     * @param result
//     * @param status
//     */
//    @Override
//    public void OnResult(boolean result, int status) {
//        Log.i(TAG, "result=" + result + ",status=" + status);
//        if (status == ICallbackStatus.CONNECTED_STATUS) {
//            myHandler.sendEmptyMessage(CONNECTED_MSG);
////            if (result == true) {//表示计步状态,在老人清醒时，每隔15分钟上传一次手机坐标
////            } else if (result == false) {//表示睡眠状态，在老人睡觉时每隔两小时上传一次手机坐标
////            }
//        } else if (status == ICallbackStatus.DISCONNECT_STATUS) {
//            myHandler.sendEmptyMessage(DISCONNECT_MSG);
//        } else if (status == ICallbackStatus.SYNC_TIME_OK) {// 设置时间操作完成，发送同步睡眠数据数据指令
//            myHandler.postDelayed(runnable2, 1000);
//        } else if (status == ICallbackStatus.OFFLINE_SLEEP_SYNC_OK) {//离线睡眠同步完成,发送同步运动数据指令
//            myHandler.postDelayed(runnable1, 1000);
//            //把睡眠数据发给健康监测页，且上传到服务器
//            myHandler.sendEmptyMessage(OFFLINE_SLEEP_SYNC_OK);
//        } else if (status == ICallbackStatus.OFFLINE_STEP_SYNC_OK) {//离线运动同步完成，发送请求电量指令,开始测试心率指令
//            myHandler.sendEmptyMessage(OFFLINE_STEP_SYNC_OK);//把离线同步的运动数据发送给首页
//            myHandler.postDelayed(runnable4, 1000 * 60 * rate_int);//请求手环电量
//            myHandler.postDelayed(runnable3, 1000 * 60 * rate_int);//发送心率测试开始
//            // myHandler.postDelayed(runnable5, 1000);//把数据上传到服务器
//        } else if (status == ICallbackStatus.GET_BLE_BATTERY_OK) {
//        }
//    }

    /**
     * mBluetoothLeService.setICallback(this)检测返回的数据
     *
     * @param result
     * @param status
     */
    @Override
    public void OnResult(boolean result, int status) {
        Log.i(TAG, "result=" + result + ",status=" + status);
        if (status == ICallbackStatus.CONNECTED_STATUS) {
            myHandler.sendEmptyMessage(CONNECTED_MSG);
            //发送同步时间指令
            // myHandler.postDelayed(runnable, 1000);
            myHandler.postDelayed(runnable, 500);
//            if (result == true) {//表示计步状态,在老人清醒时，每隔15分钟上传一次手机坐标
//            } else if (result == false) {//表示睡眠状态，在老人睡觉时每隔两小时上传一次手机坐标
//            }
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
    int rate_int = 3;
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
            int ce_int = Integer.parseInt(ce_gps);
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
     *
     * @param contacts
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
     *
     * @param context
     * @return
     */
    /**
     * 获取手机联系人的方法
     *
     * @param context
     * @return
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
