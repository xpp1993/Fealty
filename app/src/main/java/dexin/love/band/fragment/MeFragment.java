package dexin.love.band.fragment;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.avast.android.dialogs.fragment.ListDialogFragment;
import com.avast.android.dialogs.iface.IListDialogListener;
import com.baidu.location.LocationClientOption;
import com.example.xpp.blelib.BleEngine;
import com.example.xpp.blelib.CommandManager;
import com.example.xpp.blelib.GlobalValues;
import com.leaking.slideswitch.SlideSwitch;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;
import dexin.love.band.MainActivity;
import dexin.love.band.R;
import dexin.love.band.baidugps.LocationService;
import dexin.love.band.baidugps.MyLocationListener;
import dexin.love.band.base.BaseApplication;
import dexin.love.band.base.BaseFragment;
import dexin.love.band.base.BaseView;
import dexin.love.band.bean.Contacts;
import dexin.love.band.bean.RateListData;
import dexin.love.band.bean.SleepData;
import dexin.love.band.bean.SportData;
import dexin.love.band.bean.UserInfo;
import dexin.love.band.event.NavFragmentEvent;
import dexin.love.band.manager.DecodeManager;
import dexin.love.band.manager.ParameterManager;
import dexin.love.band.manager.SPManager;
import dexin.love.band.manager.SessionHolder;
import dexin.love.band.utils.AppUtils;
import dexin.love.band.utils.CommonTools;
import dexin.love.band.utils.ContextUtils;
import dexin.love.band.utils.FormatCheck;
import dexin.love.band.utils.MySqliteHelper;
import dexin.love.band.utils.NetWorkAccessTools;
import dexin.love.band.utils.ThreadPoolUtils;
import dexin.love.band.utils.ToastUtils;
import dexin.love.band.utils.WorkQueue;
import dexin.love.band.utils.XinLvSqliteHelper;
import dexin.love.band.view.DialogView;
import dexin.love.band.view.DialogViewC;
import dexin.love.band.view.DialogViewLo;
import dexin.love.band.view.DialogViewRate;
import dexin.love.band.view.DialogViewSleepLow;
import dexin.love.band.view.DialogViewSleephigh;
import dexin.love.band.view.DialogViewSportLow;
import dexin.love.band.view.DialogViewSporthigh;

/**
 * Created by Administrator on 2016/7/26.
 */
@ContentView(R.layout.fragement_me)
public class MeFragment extends BaseFragment implements View.OnClickListener, NetWorkAccessTools.RequestTaskListener, IListDialogListener, TextToSpeech.OnInitListener {
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
    private ProgressDialog progressDialog;
    private WorkQueue mWorkQueue = WorkQueue.getInstance();
    private int RATE_STATUS = 95;
    private BleEngine mBleEngine;
    private final int UPDATA_REAL_RATE_MSG = 20;//处理心率监测数据
    public static final int REQUEST_CODE_UPLOAD_CONTACTS = 0X87;//上传通讯录
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
    private UserInfo userInfo;
    private LinearLayout layout_device_list;
    private AlertDialog dialog;
    private SleepData sleepData;

    @Override
    protected void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        bar_view_left_line.setVisibility(View.VISIBLE);
        bar_biaoti.setVisibility(View.VISIBLE);
        bar_biaoti.setText("我");
        /**
         * xpp add 10-6
         */
        progressDialog = new ProgressDialog(this.getActivity());
        View devicesView = LayoutInflater.from(AppUtils.getBaseContext()).inflate(R.layout.deviceslayout, null);
        layout_device_list = (LinearLayout) devicesView.findViewById(R.id.layout_device_list);
        //1.获得sharedPreference对象,SharedPrefences只能放基础数据类型，不能放自定义数据类型。
        preferences = SPManager.getSharedPreferences(AppUtils.getBaseContext());
        userInfo = ContextUtils.getObjFromSp(AppUtils.getBaseContext(), "userInfo");
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
                    //   bundle.putString("parentPhone", SessionHolder.user.getMobile());
                    bundle.putString("parentPhone", userInfo.getMobile());
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
        Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile"}, userInfo.getMobile());
        NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.GET_USER_BYMOBILE, params, null, MeFragment.REQUEST_USER_BYMIBILE, this);
        mBleEngine = new BleEngine(getActivity());
        mBleEngine.initialize();
        mRegisterReceiver();
        xinlvhelper = new XinLvSqliteHelper(AppUtils.getBaseContext());
        xinlvdb = xinlvhelper.getReadableDatabase();
        mVibrator = ((BaseApplication) AppUtils.getBaseContext()).mVibrator;
        notificationManager = ((BaseApplication) AppUtils.getBaseContext()).notificationManager;
        tts = new TextToSpeech(AppUtils.getBaseContext(), this);
        sleepData = new SleepData();
    }

    /**
     * 初始化个人资料显示
     */
    private void initPersonalDataShow() {
        //  if (TextUtils.isEmpty(SessionHolder.user.getUserpic()) || "".equals(SessionHolder.user.getUserpic())) {
        if (TextUtils.isEmpty(userInfo.getUserpic()) || "".equals(userInfo.getUserpic())) {
            circleImageView.setImageResource(R.mipmap.unknow_head);
        } else {
            // NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).toLoadImage("http://192.168.8.133:8080" + "/" + userInfo.getUserpic(), circleImageView, R.mipmap.unknow_head, R.mipmap.unknow_head);
            if (SessionHolder.user == null)
                //  NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).toLoadImage("http://120.76.27.233:8080" + "/" + SessionHolder.user.getUserpic(), circleImageView, R.mipmap.unknow_head, R.mipmap.unknow_head);
                NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).toLoadImage("http://120.76.27.233:8080" + "/" + userInfo.getUserpic(), circleImageView, R.mipmap.unknow_head, R.mipmap.unknow_head);
            else
                NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).toLoadImage("http://120.76.27.233:8080" + "/" + SessionHolder.user.getUserpic(), circleImageView, R.mipmap.unknow_head, R.mipmap.unknow_head);
        }
        if (SessionHolder.user != null) {
            me_username.setText(TextUtils.isEmpty(SessionHolder.user.getNickName()) ? "未设置" : SessionHolder.user.getNickName());
            me_phone.setText(TextUtils.isEmpty(SessionHolder.user.getMobile()) ? "未设置" : "手机号:" + SessionHolder.user.getMobile());
        } else {
            me_username.setText(TextUtils.isEmpty(userInfo.getNickName()) ? "未设置" : userInfo.getNickName());
            me_phone.setText(TextUtils.isEmpty(userInfo.getMobile()) ? "未设置" : "手机号:" + userInfo.getMobile());
        }
    }

    private void mRegisterReceiver() {
        IntentFilter mFilter = new IntentFilter();

        mFilter.addAction(GlobalValues.BROADCAST_INTENT_CONNECT_STATE_CHANGED);//连接状态
        mFilter.addAction(GlobalValues.BROADCAST_INTENT_ELECTRICITY);
        mFilter.addAction(GlobalValues.BROADCAST_INTENT_CURRENTMOTION);
        mFilter.addAction(GlobalValues.BROADCAST_INTENT_COMMAND_RECEIVED);
        mFilter.addAction(Intent.ACTION_BATTERY_CHANGED);//获取手机电量
        mFilter.addAction(GlobalValues.BROADCAST_INTENT_SLEEPQ);//获取睡眠质量数据
        mFilter.addAction(GlobalValues.BROADCAST_INTENT_RATE);//获取心率测试值
        //  mFilter.addAction(GlobalVariable.READ_BLE_VERSION_ACTION);
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
                if (mBleEngine.enableBle()) {
                    progressDialog.show();
                    mBleEngine.scanBleDevice(true, 5000, new BleEngine.ListScanCallback() {
                        @Override
                        public void onDeviceFound(List<BluetoothDevice> devices) {
                            progressDialog.dismiss();
                            showDeviceList(devices);
                        }
                    });
                }
            }

            @Override
            public void close() {
                if (mBleEngine != null)
                    // mBleEngine.disconnect();
                    mBleEngine.close();
            }
        });
    }

    /**
     * 显示蓝牙设备列表
     */
    private void showDeviceList(List<BluetoothDevice> devices) {
        layout_device_list.removeAllViews();
        View itemView = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        for (int i = 0; devices != null && i < devices.size(); i++) {
            itemView = LayoutInflater.from(AppUtils.getBaseContext()).inflate(R.layout.layout_list_item_device, null);

            RelativeLayout layout_item_device = (RelativeLayout) itemView.findViewById(R.id.layout_list_item_device);
            TextView txt_item_name = (TextView) itemView.findViewById(R.id.txt_item_name);

            final BluetoothDevice device = devices.get(i);
            if (device.getName() != null && device.getName().length() > 0)
                txt_item_name.setText(device.getName());
//            else
//                txt_item_name.setText("未知设备");
            layout_item_device.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mBleEngine.connect(device.getAddress());
                }
            });
            layout_device_list.addView(itemView);
        }
        ViewGroup p = (ViewGroup) layout_device_list.getParent();
        if (p != null) {
            p.removeAllViewsInLayout();
        }
        builder.setTitle("点击连接设备");
        builder.setView(layout_device_list);
        builder.setNegativeButton("放弃", null);
        dialog = builder.setCancelable(false).create();
        dialog.show();
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
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        getActivity().unregisterReceiver(mReceiver);
        locService.unregisterListener(mListener); //注销掉监听
        locService.stop(); //停止定位服务
        if (runnable3 != null)
            myHandler.removeCallbacks(runnable3);
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
        if (helper != null) {
            helper.close();
        }
        if (xinlvhelper != null) {
            xinlvhelper.close();
        }
        if (xinlvdb.isOpen()) {
            xinlvdb.close();
        }
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        if (delatesqlite != null) {
            myHandler.removeCallbacks(delatesqlite);
        }
    }

    private void showSetPhoneNumberDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("请输入电话号码");
        View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_textview_nickname, null);
        final EditText editText = (EditText) inflate.findViewById(R.id.dialog_textview_tv);
        editText.setSingleLine();
        builder.setView(inflate);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                String phoneNumber = editText.getText().toString().trim();
                ArrayList list = new ArrayList();
                if (TextUtils.isEmpty(phoneNumber)) {
                    ToastUtils.showToastInUIThread("号码输入不能为空");
                } else if (!FormatCheck.isMobile(phoneNumber)) {
                    ToastUtils.showToastInUIThread("手机号码格式错误,重写填写!");
                } else {
                    list.add(phoneNumber);
                    //上传号码
                    Map<String, String> params = CommonTools.getParameterMap(new String[]{"contact_list", "mobile"}, uploadPhoneNumber(list), userInfo.getMobile());
                    NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.UPLOAD_CONTACTS_LIST, params, null, REQUEST_CODE_UPLOAD_CONTACTS, MeFragment.this);
                }
            }
        });
        builder.setNegativeButton("放弃", null);
        builder.setCancelable(false).create().show();
    }

    private static final int REQUEST_LIST_SINGLE = 11;
    private static final int REQUEST_LIST_SIMPLE = 9;

    private void showSetBindDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("请选择");
        final View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_radiogroup_bind, null);
        final RadioButton readRadioButton = (RadioButton) inflate.findViewById(R.id.dialog_radiogroup_read);
        RadioButton inRadioButton = (RadioButton) inflate.findViewById(R.id.dialog_radiogroup_in);
        builder.setView(inflate);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                String newGender = readRadioButton.isChecked() ? "导入通讯录" : "输入手机号码";
                if (TextUtils.equals(newGender, "输入手机号码")) {
                    showSetPhoneNumberDialog();
                } else if (TextUtils.equals(newGender, "导入通讯录")) {
                    ArrayList<Contacts> contacts = (ArrayList<Contacts>) getContacts(AppUtils.getBaseContext());
                    String[] contact = new String[contacts.size()];
                    if (contacts.size() == 0) {
                        ToastUtils.showToastInUIThread("通讯录导入失败");
                        return;
                    }
                    for (int i = 0; i < contacts.size(); i++) {

                        contact[i] = contacts.get(i).toString();
                    }
                    ListDialogFragment.createBuilder(getActivity(), getActivity().getSupportFragmentManager()).setTitle("通讯录列表如下").setItems(contact).setCancelButtonText("取消")
                            .setConfirmButtonText("确定")
                            .setRequestCode(REQUEST_LIST_SINGLE)
                            .setChoiceMode(AbsListView.CHOICE_MODE_SINGLE)
                            .setTargetFragment(MeFragment.this, REQUEST_LIST_SINGLE)
                            .show();
                }
            }
        });
        builder.setNegativeButton("放弃", null);
        builder.setCancelable(false).create().show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_me_details:
                EventBus.getDefault().post(new NavFragmentEvent(new MeSettingFragment()));//跳转到个人信息详情页
                break;
            case R.id.relative_binded://绑定用户
                //弹出对话框1.选择从通讯录里导入号码 2.弹出输入号码对话框
                showSetBindDialog();
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
        //  ToastUtils.showToastInUIThread("网络错误");
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

    /**
     * 绑定用户弹出的对话框
     *
     * @param value
     * @param number
     * @param requestCode
     */
    @Override
    public void onListItemSelected(CharSequence value, int number, int requestCode) {
        if (requestCode == REQUEST_LIST_SINGLE || requestCode == REQUEST_LIST_SIMPLE) {
            Log.e("listfragment", value + "");
            String info = (String) value;
            String[] namePhone = info.split(":");
            Log.e("namePhone", namePhone[1]);
            ArrayList list = new ArrayList();
            if (namePhone[1] != null) {
                list.add(namePhone[1]);
                //上传号码
                Map<String, String> params = CommonTools.getParameterMap(new String[]{"contact_list", "mobile"}, uploadPhoneNumber(list), userInfo.getMobile());
                NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.UPLOAD_CONTACTS_LIST, params, null, REQUEST_CODE_UPLOAD_CONTACTS, MeFragment.this);
            }
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
                        SessionHolder.initHolder(mobile, userInfo);
                        Log.e("message", mobile + "," + nickname + "," + userpic + "," + sex + "," + birthday);
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
                case UPDATA_REAL_RATE_MSG://处理接收到的心率数据.把心率数据写入数据库
                    Bundle rate_bundle = msg.getData();
                    int rate = rate_bundle.getInt("rate");
                    int status = rate_bundle.getInt("status");
                    String rate_time = rate_bundle.getString("time");
                    xinlvdb.insert("xinlv", null, toContentValues(rate_time, rate, userInfo.getMobile()));
                    readSP();
                    if (rate < norMin || rate > norMax) {//心率不正常，app响铃报警
                        rateAbnormalNotify();
                        myHandler.postDelayed(runnable_updataGPS3, 1000 * 60 * ce_int);
                    } else {
                        if (status == ParameterManager.CURRENT_STATUES) {//如果为运动状态
                            judgeStepRate();
                        } else {//如果为睡眠状态
                            judgeSleepRate();
                        }
                    }
                    //上传到服务器
                    // Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile", "uploadTime", "currentHeart"}, SessionHolder.user.getMobile(), rate_time, rate + "");
                    Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile", "uploadTime", "currentHeart"}, userInfo.getMobile(), rate_time, rate + "");
                    NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.INSERT_CURRENTRATE, params, null, REQUEST_CODE_CURRENTRATE, MeFragment.this);
                    break;
                case REQUEST_CODE_UPLOAD_CONTACTS://处理上传手机通讯录返回的数据
                    if (msg.getData().getInt("code") == 1) {//请求成功
                        if (msg.getData().getSerializable("old_people_list") != null) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("old_people_list", msg.getData().getSerializable("old_people_list"));
                            EventBus.getDefault().post(new NavFragmentEvent(new OlsManListFragment(), bundle));
                        }
                    } else if (msg.getData().getInt("code") == 0) {
                        ToastUtils.showToastInUIThread("该用户不是德信手环app用户，不能绑定!");
                    }
                    break;
//                case OFFLINE_STEP_SYNC_OK://同步了离线运动数据，显示,且上传服务器
//                    queryStepInfo();
//                    break;
//                case OFFLINE_SLEEP_SYNC_OK://同步了离线睡眠数据，显示,且上传服务器
//                    querySleepInfo();
//                    break;
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

    /**
     * 发送命令，心率停止测试
     */
    private void functionRateFinished() {
        mWorkQueue.execute(new Runnable() {
            @Override
            public void run() {
                //  mWriteCommand.sendRateTestCommand(GlobalVariable.RATE_TEST_STOP);//发送心率测试关闭
                CommandManager.sendStopRate(mBleEngine);
            }
        });
        //讲写入数据库的心率读取出来，执行上传收集到的心率测试数据，上传成功后清空
        Log.e("wyj", "functionRateFinished");
        com.alibaba.fastjson.JSONObject object = new com.alibaba.fastjson.JSONObject();
        JSONArray jsonArray = new JSONArray();
        List<RateListData> rateList = new ArrayList<>();
        Map<String, List<RateListData>> map = new HashMap<>();
        RateListData rateListData = null;
        com.alibaba.fastjson.JSONObject jsonObject = null;
        if (!xinlvdb.isOpen())
            return;
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
        // map.put(SessionHolder.user.getMobile(), rateList);
        map.put(userInfo.getMobile(), rateList);
        object.put("heartRate", jsonArray);
        //object.put("mobile", SessionHolder.user.getMobile());
        object.put("mobile", userInfo.getMobile());
        String jsonString = object.toJSONString();
        if (rateList.size() == 0) {
            return;
        }
        //把心率发给首页
        EventBus.getDefault().post(map);
        //  myHandler.postDelayed(d)
        //把心率json数据上传到服务器,上传到服务器之后，清空数据库
        Log.e("json", jsonString);
        // Map<String, String> params = CommonTools.getParameterMap(new String[]{"heartRate", "mobile"}, jsonString, SessionHolder.user.getMobile());
        Map<String, String> params = CommonTools.getParameterMap(new String[]{"heartRate", "mobile"}, jsonString, userInfo.getMobile());
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
            if (action.equals(GlobalValues.BROADCAST_INTENT_COMMAND_RECEIVED)) {
                Log.d("wyj", "BROADCAST_INTENT_COMMAND_RECEIVED");
                mWorkQueue.notifyState(true);
            } else if (action.equals(GlobalValues.BROADCAST_INTENT_CURRENTMOTION)) {
                Bundle bundle = intent.getExtras();
                Log.d("wyj", "devTime is " + bundle.getString(GlobalValues.NAME_DEVICE_TIME));
                String steps = bundle.getString(GlobalValues.NAME_STEPS);
                String distance = new DecimalFormat("0.00").format(bundle.getInt(GlobalValues.NAME_DISTANCE) / 1000.000); // 保留2位小数，带前导零
                String calories = new DecimalFormat("0.0").format(bundle.getInt(GlobalValues.NAME_CALORIES) / 1000.0);
                SportData sportData = new SportData(steps, calories, userInfo.getMobile(), distance);
                Log.d("wyj", "sleepTime is " + bundle.getInt(GlobalValues.NAME_SLEEP));
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
                String sleepTotal = simpleDateFormat.format(new Date(bundle.getInt(GlobalValues.NAME_SLEEP) * 1000L));
                sleepData.setTotal_hour_str(sleepTotal);//睡眠总时间
                //把计步监听的结果上传服务器
                updataSportData(steps, calories, distance);
                EventBus.getDefault().post(sportData);
                mWorkQueue.execute(new Runnable() {
                    @Override
                    public void run() {
                        CommandManager.sendSynSleep(mBleEngine, 1);
                    }
                });//发送获取当天睡眠质量数据，0表示当天数据，1表示昨天数据，2表示前天数据
                //发送心率测试开始
                myHandler.postDelayed(runnable3, 1000 * 60 * rate_int);
            } else if (action.equals(GlobalValues.BROADCAST_INTENT_ELECTRICITY)) {
                String value = intent.getExtras().getString(GlobalValues.NAME_ELECTRICITY);
                String ISELECTRICITE=intent.getExtras().getString(GlobalValues.NAME_ISELECTRICITE);//手环是否处于充电状态
                ToastUtils.showToastInUIThread("当前手环电量为：" + value);
                Map<String, String> params = new HashMap<>();
                params.put("cuffElectricity", value);
                alterSelfData(params);//发送到服务器
                //电量异常，发送通知
                if (isAdded())
                    setNotifyDian2(Integer.parseInt(value), ISELECTRICITE,getResources().getString(R.string.betty));
            } else if (action.equals(GlobalValues.BROADCAST_INTENT_CONNECT_STATE_CHANGED)) {
                String state = intent.getExtras().getString(GlobalValues.NAME_CONNECT_STATE);
                if (TextUtils.equals(state, GlobalValues.VALUE_CONNECT_STATE_YES)) {//成功连接手环
                    dialog.dismiss();
                    myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mWorkQueue.notifyState(true);
                        }
                    }, 500);

                    mWorkQueue.execute(new Runnable() {
                        @Override
                        public void run() {
                            CommandManager.sendSynTime(mBleEngine);
                        }
                    });//发送同步手环时间指令
                    mWorkQueue.execute(new Runnable() {
                        @Override
                        public void run() {
                            CommandManager.sendVibration(mBleEngine, 6);
                        }
                    });//发送手环振动指令
                } else {
                    progressDialog.dismiss();
                    dialog.dismiss();
                    ToastUtils.showToastInUIThread("与手环失去连接！");
                    bluee_iv_left.setState(false);
                    readSP();
                    DialogViewC dialogView = null;
                    if (message_dialog == true) {
                        dialogView = new DialogViewC(AppUtils.getBaseContext());
                    }
                    if (isAdded())
                        diffNotifyShow(1, getResources().getString(R.string.disconnected), dialogView);
                }
            } else if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                //获取手机电量
                getMobileLevel(intent);
            } else if (action.equals(GlobalValues.BROADCAST_INTENT_SLEEPQ)) {//获取睡眠质量数据
                Bundle bundle = intent.getExtras();
                int deepSleep = bundle.getInt(GlobalValues.NAME_DEEPSLEEP);
                int deep_hour = deepSleep / 60;
                int deep_minute = deepSleep % 60;
                int lightSleep = bundle.getInt(GlobalValues.NAME_LIGHTSLEEP);
                int light_hour = lightSleep / 60;
                int light_minute = lightSleep % 60;
                sleepData.setDeep_hour(deep_hour);
                sleepData.setDeep_minute(deep_minute);
                sleepData.setLight_hour(light_hour);
                sleepData.setLight_minute(light_minute);
                sleepData.setParentPhone(userInfo.getMobile());
                Log.e("xpp", deepSleep + "," + lightSleep);
                //把这些数据上传到服务器
                updataSleepData(deep_hour, deep_minute, light_hour, light_minute, sleepData.getTotal_hour_str());
                EventBus.getDefault().post(sleepData);//把数据传到首页面
            } else if (action.equals(GlobalValues.BROADCAST_INTENT_RATE)) {//心率数据
                Bundle bundle = intent.getExtras();
                String rateTime = bundle.getString(GlobalValues.NAME_RATETIME);//该次心率测试的时间
                int rate = bundle.getInt(GlobalValues.NAME_RATE);
                int status = bundle.getInt(GlobalValues.NAME_RATE_STATUS);
                if (rate == 0)
                    return;
                //将心率数据发给首页
                Intent intent_health = new Intent();
                intent_health.putExtra("tempRate", rate);
                intent_health.setAction(HealthDataFragement.RATE_CHANGED);
                if (isAdded())
                    getActivity().sendBroadcast(intent_health);
                //把心率测试数据写入数据库
                Bundle rate_bundle = new Bundle();
                rate_bundle.putString("time", rateTime);
                rate_bundle.putInt("rate", rate);
                rate_bundle.putInt("status", status);
                Message message = Message.obtain();
                message.what = UPDATA_REAL_RATE_MSG;
                message.setData(rate_bundle);
                myHandler.sendMessage(message);
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
            // mWriteCommand.sendRateTestCommand(GlobalVariable.RATE_TEST_START);
            mWorkQueue.execute(new Runnable() {
                @Override
                public void run() {
                    CommandManager.sendStartRate(mBleEngine, "FFFFFFFF");
                    Log.e("wyj", "start to rate");
                }
            });
            String rate_ji = preferences.getString(ParameterManager.SHEZHI_JIANCEXINLV, "3");
            rate_int = Integer.parseInt(rate_ji);
            myHandler.postDelayed(this, 1000 * 60 * rate_int);
            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    functionRateFinished();
                }
            }, 1000 * 30);

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
            if (isAdded())
                setNotifyDian(level, getResources().getString(R.string.phonebetty));//电量异常发送通知
        }
    }

    private void setNotifyDian(int electricity, String notice) {
        if (electricity < 10) {//通知
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

    /**
     *
     * @param electricity 手环电量
     * @param ISELECTRICITE 手环是否处于充电状态
     * @param notice
     */
    private void setNotifyDian2(int electricity, String ISELECTRICITE,String notice) {
        //手环电量少于15，并且处于未充电状态，发送消息通知
        if (electricity <= 15&&ISELECTRICITE.equals(GlobalValues.VALUE_ISELECTRICITE_NO)) {//通知
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

    /**
     * 判断睡眠时的心率是否异常，通知提醒
     */
    private void judgeSleepRate() {
        //表示睡眠状态，在老人睡觉时每隔两小时上传一次手机坐标
        myHandler.postDelayed(runnable_updataGPS2, 1000 * 60 * 60 * sleep_gpsin);
        if (RATE_STATUS < sleepMinRate) {//睡眠时心率偏低，消息通知
            DialogViewSleepLow dialogView = null;
            if (message_dialog == true) {
                dialogView = new DialogViewSleepLow(AppUtils.getBaseContext());
            }
            if (isAdded())
                diffNotifyShow(0, getResources().getString(R.string.sleeplow), dialogView);
        } else if (RATE_STATUS > sleepMaxRate) {//睡眠时心率偏高，消息通知
            DialogViewSleephigh dialogView = null;
            if (message_dialog == true) {
                dialogView = new DialogViewSleephigh(AppUtils.getBaseContext());
            }
            if (isAdded())
                diffNotifyShow(0, getResources().getString(R.string.sporthight), dialogView);
        }
    }

    private void judgeStepRate() {
        //运动时每隔15分钟上传一次GPS
        myHandler.postDelayed(runnable_updataGPS, 1000 * 60 * sport_gpsint);
        if (RATE_STATUS < sportMinRate) {//运动时心率偏低，消息通知
            DialogViewSportLow dialogView = null;
            if (message_dialog == true) {
                dialogView = new DialogViewSportLow(AppUtils.getBaseContext());
            }
            if (isAdded())
                diffNotifyShow(0, getResources().getString(R.string.sportlow), dialogView);
        } else if (RATE_STATUS > sportMaxRate) {//运动时心率偏高，消息通知
            DialogViewSporthigh dialogView = null;
            if (message_dialog == true) {
                dialogView = new DialogViewSporthigh(AppUtils.getBaseContext());
            }
            if (isAdded())
                diffNotifyShow(0, getResources().getString(R.string.sporthight), dialogView);
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
        if (isAdded())
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

    //把运动数据上传到服务器
    private void updataSportData(String step, String calories, String distance) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("step", step);
        parameters.put("calories", calories);
        parameters.put("distance", distance);
        alterSelfData(parameters);
    }

    //把睡眠数据上传到服务器
    private void updataSleepData(int deep_hour, int deep_minute, int light_hour, int light_minute, String total_hour_str) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("total_hour_str", total_hour_str);
        parameters.put("light_hour", light_hour + "");
        parameters.put("light_minute", light_minute + "");
        parameters.put("deep_hour", deep_hour + "");
        parameters.put("deep_minute", deep_minute + "");
        alterSelfData(parameters);
    }

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

    int sport_gpsint = 15;
    //运动时每15分钟上传一次GPS
    private Runnable runnable_updataGPS = new Runnable() {
        @Override
        public void run() {
            // Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile", "locationdescrible", "address", "lat", "lon"}, SessionHolder.user.getMobile(), locationdescrible, address, String.valueOf(lat), String.valueOf(lon));
            Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile", "locationdescrible", "address", "lat", "lon"}, userInfo.getMobile(), locationdescrible, address, String.valueOf(lat), String.valueOf(lon));
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
            Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile", "locationdescrible", "address", "lat", "lon"}, userInfo.getMobile(), locationdescrible, address, String.valueOf(lat), String.valueOf(lon));

            NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.GPS_UPLOAD_URL, params, null, GPS_UPLOAD_CODE, MeFragment.this);
            String sleep_gps = preferences.getString(ParameterManager.SHEZHI_SLEEP_GPS, "2");
            sleep_gpsin = Integer.parseInt(sleep_gps);
            myHandler.postDelayed(this, 1000 * 60 * 60 * sleep_gpsin);
        }
    };
    int ce_int = 3;
    /**
     * 心率异常时，没三分钟上传一次GPS
     */
    private Runnable runnable_updataGPS3 = new Runnable() {
        @Override
        public void run() {
            Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile", "locationdescrible", "address", "lat", "lon"}, userInfo.getMobile(), locationdescrible, address, String.valueOf(lat), String.valueOf(lon));
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
//        Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile", "total_hour_str", "light_hour", "light_minute", "deep_hour", "deep_minute", "calories", "distance", "step", "cuffElectricity", "mobileElectricity"},
//                SessionHolder.user.getMobile(), "", "", "", "", "", "", "", "", "", "");
        Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile", "total_hour_str", "light_hour", "light_minute", "deep_hour", "deep_minute", "calories", "distance", "step", "cuffElectricity", "mobileElectricity"},
                userInfo.getMobile(), "", "", "", "", "", "", "", "", "", "");
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
     * 上传要绑定的号码
     *
     * @return
     */
    private String uploadPhoneNumber(ArrayList list) {
        com.alibaba.fastjson.JSONObject object = new com.alibaba.fastjson.JSONObject();
        JSONArray jsonArray = new JSONArray();
        com.alibaba.fastjson.JSONObject jsonObject = null;
        for (int i = 0; i < list.size(); i++) {
            jsonObject = new com.alibaba.fastjson.JSONObject();
            jsonObject.put("mobile", list.get(i));
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
