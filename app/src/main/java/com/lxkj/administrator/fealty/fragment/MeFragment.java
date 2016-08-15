package com.lxkj.administrator.fealty.fragment;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.avast.android.dialogs.fragment.ListDialogFragment;
import com.avast.android.dialogs.iface.IListDialogListener;
import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.adapter.LeDeviceListAdapter;
import com.lxkj.administrator.fealty.base.BaseFragment;
import com.lxkj.administrator.fealty.bean.Contacts;
import com.lxkj.administrator.fealty.bean.SleepData;
import com.lxkj.administrator.fealty.bean.SportData;
import com.lxkj.administrator.fealty.event.NavFragmentEvent;
import com.lxkj.administrator.fealty.manager.DecodeManager;
import com.lxkj.administrator.fealty.manager.ParameterManager;
import com.lxkj.administrator.fealty.manager.SessionHolder;
import com.lxkj.administrator.fealty.utils.AppUtils;
import com.lxkj.administrator.fealty.utils.CommonTools;
import com.lxkj.administrator.fealty.utils.NetWorkAccessTools;
import com.lxkj.administrator.fealty.utils.ToastUtils;
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
import com.yc.pedometer.info.SleepTimeInfo;
import com.yc.pedometer.info.StepInfo;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/7/26.
 */
@ContentView(R.layout.fragement_me)
public class MeFragment extends BaseFragment implements View.OnClickListener, NetWorkAccessTools.RequestTaskListener, DeviceScanInterfacer, IListDialogListener, ICallback {
    @ViewInject(R.id.me_iv_left)
    private ImageView me_iv_left;
    @ViewInject(R.id.me_headpic)
    private CircleImageView circleImageView;
    @ViewInject(R.id.me_username)
    private TextView me_username;
    @ViewInject(R.id.me_phone)
    private TextView me_phone;
    @ViewInject(R.id.bluee_iv_left)
    private TextView bluee_iv_left;
    @ViewInject(R.id.binded_action)
    private TextView binded_action;
    public static final int REQUEST_USER_BYMIBILE = 0x01;
    private Handler myHandler = new MyHandler();
    private BLEServiceOperate mBLEServiceOperate;
    private final int REQUEST_ENABLE_BT = 1;
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
    private final int CONNECTING = 2;
    private final int DISCONNECTED = 3;
    private int CURRENT_STATUS = DISCONNECTED;
    private WriteCommandToBLE mWriteCommand;
    private String TAG = "xpp";
    private Handler mHandler;
    private int tempRate = 75;
    private int tempStatus;
    private DataProcessing mDataProcessing;
    private final int UPDATA_REAL_RATE_MSG = 20;//处理心率监测数据
    private final int UPDATE_STEP_UI_MSG = 0;//同步计步数据
    private final int UPDATE_SLEEP_UI_MSG = 23;//同步睡眠数据
    public static final int REQUEST_CODE_UPLOAD_CONTACTS = 0X20;//上传通讯录
    public static final int REQUEST_CODE_SPORTDATA_SLEEPDATA = 0x10;//把运动数据睡眠数据上传到服务器
    public static final int REQUEST_CODE_RATE = 0x22;//把测试的心率数据上传到服务器
    private Bundle bundle;
    private String phone;
    int step, calories;
    float distance;
    String total_hour_str;
    int deep_hour, deep_minute, light_minute, light_hour;
    private static final String[] CONTACTOR_NEED = new String[]{    //联系人字段
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.Contacts.DISPLAY_NAME
    };

    //    private LeDeviceListAdapter mLeDeviceListAdapter;
//    private ListView listView=new ListView(AppUtils.getBaseContext());
    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        //初始化个人资料显示
        initPersonalDataShow();
        Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile"}, SessionHolder.user.getMobile());
        NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.GET_USER_BYMOBILE, params, null, MeFragment.REQUEST_USER_BYMIBILE, this);
        bluee_iv_left.setText("连接设备");
        sp = getActivity().getSharedPreferences(GlobalVariable.SettingSP, 0);
        editor = sp.edit();
        mySQLOperate = new UTESQLOperate(AppUtils.getBaseContext());
        mHandler = new Handler();
        mDevice = new ArrayList<>();
        bundle = new Bundle();
        mBLEServiceOperate = BLEServiceOperate.getInstance(AppUtils.getBaseContext());// 用于BluetoothLeService实例化准备,必须
        mRegisterReceiver();
    }

    /**
     * 初始化个人资料显示
     */
    private void initPersonalDataShow() {
        if (TextUtils.isEmpty(SessionHolder.user.getUserpic()) || "".equals(SessionHolder.user.getUserpic())) {
            circleImageView.setImageResource(R.mipmap.unknow_head);
        } else {
            NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).toLoadImage("http://192.168.8.133:8080" + "/" + SessionHolder.user.getUserpic(), circleImageView, R.mipmap.unknow_head, R.mipmap.unknow_head);
        }
        me_username.setText(TextUtils.isEmpty(SessionHolder.user.getNickName()) ? "未设置" : SessionHolder.user.getNickName());
        me_phone.setText(TextUtils.isEmpty(SessionHolder.user.getMobile()) ? "未设置" : "手机号:" + SessionHolder.user.getMobile());
    }

    private void mRegisterReceiver() {
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(GlobalVariable.READ_BATTERY_ACTION);
        mFilter.addAction(GlobalVariable.READ_BLE_VERSION_ACTION);
        getActivity().registerReceiver(mReceiver, mFilter);
    }

    @Override
    protected void initListener() {
        me_iv_left.setOnClickListener(this);
        bluee_iv_left.setOnClickListener(this);
        binded_action.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    // 用EventBus 来导航,订阅者
    public void onEventMainThread(String event) {
        String str = event.getBytes().toString();
        Log.d("Tag", str);
        initPersonalDataShow();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mBLEServiceOperate.unBindService();// unBindService
        getActivity().unregisterReceiver(mReceiver);
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
        if (runnable5!=null)
            myHandler.removeCallbacks(runnable5);
        mBLEServiceOperate.disConnect();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.me_iv_left:
                EventBus.getDefault().post(new NavFragmentEvent(new MeSettingFragment()));
                break;
            case R.id.bluee_iv_left:
                if (!mBLEServiceOperate.isSupportBle4_0()) {
                    ToastUtils.showToastInUIThread("设备不支持蓝牙4.0");
                    return;
                }
                mBLEServiceOperate.setDeviceScanListener(this);//for DeviceScanInterfacer
                if (!mBLEServiceOperate.isBleEnabled()) {
                    Intent enableBtIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE);
                }
                //2016-813
//                mLeDeviceListAdapter = new LeDeviceListAdapter();
//                listView.setAdapter(mLeDeviceListAdapter);//end
                scanLeDevice(true);
                break;
            case R.id.binded_action://绑定用户
                //1.获取手机联系人
                ArrayList<Contacts> contacts = (ArrayList<Contacts>) getContacts(AppUtils.getBaseContext());
                //2.上传通讯录,返回通讯录中,注册过  的 用户列表
                Log.e("telephoe", SessionHolder.mobile);
                Map<String, String> params = CommonTools.getParameterMap(new String[]{"contact_list", "mobile"}, uploadContacts(contacts), SessionHolder.mobile);
                NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.UPLOAD_CONTACTS_LIST, params, null, REQUEST_CODE_UPLOAD_CONTACTS, MeFragment.this);
                ToastUtils.showToastInUIThread("正在获取联系人列表....");
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
//
//    @Override
//    public void onPause() {
//        super.onResume();
//        scanLeDevice(false);
//        mDevice.clear();
//
//    }

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
                }
                break;
            case REQUEST_CODE_SPORTDATA_SLEEPDATA:
            case REQUEST_CODE_RATE:
                try {
                    DecodeManager.decodeCommon(jsonObject, requestCode, myHandler);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestFail(int requestCode, int errorNo) {
        ToastUtils.showToastInUIThread("网络连接错误，请检查重试！");
    }

    @Override
    public void LeScanCallback(final BluetoothDevice bluetoothDevice, final int j) {


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("Rssi", "" + j);
                mDevice.add(bluetoothDevice);
//                //代表扫描过程,2016-8-13
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }//end
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
                }//end
//                mLeDeviceListAdapter.addDevice(bluetoothDevice);
//                mLeDeviceListAdapter.notifyDataSetChanged();
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
            mWriteCommand = new WriteCommandToBLE(AppUtils.getBaseContext());//****
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
                case DISCONNECT_MSG:
                    bluee_iv_left.setText("未连接");
                    CURRENT_STATUS = DISCONNECTED;
                    ToastUtils.showToastInUIThread("未连接或者是连接失败！");
                    String lastConnectAddr0 = sp.getString(
                            GlobalVariable.LAST_CONNECT_DEVICE_ADDRESS_SP, "");
                    boolean connectResute0 = mBLEServiceOperate
                            .connect(lastConnectAddr0);
                    Log.i(TAG, "connectResute0=" + connectResute0);
                    break;
                case CONNECTED_MSG:
                    bluee_iv_left.setText("已连接");
                    mBluetoothLeService.setRssiHandler(myHandler);
                    CURRENT_STATUS = CONNECTED;
                    bundle.putString("IF_CONNECTED", "我的");
                    ToastUtils.showToastInUIThread("已连接");
                    break;
                case UPDATA_REAL_RATE_MSG://处理接收到的心率数据
                    Log.d("tempRate", tempRate + "");
                    //如果测试完成
                    if (tempStatus == GlobalVariable.RATE_TEST_FINISH) {
//                        UpdateUpdataRateMainUI(CalendarUtils.getCalendar(0));
//                        Toast.makeText(mContext, "Rate test finish", Toast.LENGTH_SHORT).show();
                        bundle.putInt("tempRate", tempRate);
                        EventBus.getDefault().post(bundle);//将心率发送到状态页
                    }
                    break;
                case UPDATE_SLEEP_UI_MSG://睡眠数据
                    querySleepInfo();
                    break;
                case UPDATE_STEP_UI_MSG://返回今天的步数、距离、卡路里的集合
                    queryStepInfo();
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
                case REQUEST_CODE_SPORTDATA_SLEEPDATA:
                case REQUEST_CODE_RATE:
                    ToastUtils.showToastInUIThread("数据已更新到服务器！");
                    break;
                default:
                    break;
            }
        }
    }
    private void functionRateFinished() {
        mWriteCommand.sendRateTestCommand(GlobalVariable.RATE_TEST_STOP);//发送心率测试关闭
        //执行上传收集到的心率测试数据，上传成功后清空
        //做数据保存操作
        bundle.putInt("tempRate", tempRate);
        EventBus.getDefault().post(bundle);//将心率发送到状态页
//        Bundle rate=new Bundle();
//        rate.putInt("tempRate",tempRate);
//        new StatusFragment().setArguments(rate);
        Log.e("wyj", "functionRateFinished");
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
                //把号码，设备名，设备地址，连接手环状态，设备电量发送给服务器，服务器判断是否低电量，像APP发送消息（）推送,app接收短信并震动
            }
        }
    };
    /**
     * 第一个参数，心率值
     * 第二个参数当前心率值得状态 1.测试中 2.测试完成
     * 使用时，需要提前调用 mDataProcessing.setOnRateListener(mOnRateListener);
     */
    private RateChangeListener mOnRateListener = new RateChangeListener() {
        @Override
        public void onRateChange(int rate, int status) {
            tempRate = rate;
            tempStatus = status;
            Log.e("wyj", "onRateChange =" + tempRate);
            myHandler.sendEmptyMessage(UPDATA_REAL_RATE_MSG);
        }
    };
    /**
     * 睡眠监听
     */
    private SleepChangeListener mOnSlepChangeListener = new SleepChangeListener() {

        @Override
        public void onSleepChange() {
            myHandler.sendEmptyMessage(UPDATE_SLEEP_UI_MSG);
        }
    };
    /**
     * 计步监听
     */
    private StepChangeListener mOnStepChangeListener = new StepChangeListener() {
        @Override
        public void onStepChange(int steps, float distance, int calories) {
            Log.d("onStepHandler", "steps =" + steps + ",distance =" + distance
                    + ",calories =" + calories);
            myHandler.sendEmptyMessage(UPDATE_STEP_UI_MSG);
        }
    };

    /**
     * 返回今天的步数、距离、卡路里的集合
     */
    private void queryStepInfo() {
        StepInfo stepInfo = mySQLOperate.queryStepInfo(CalendarUtils.getCalendar(0));
//        int step, calories;
//        float distance;
        if (stepInfo != null) {
            step = stepInfo.getStep();//运动的步数
            calories = stepInfo.getCalories();//卡路里
            distance = stepInfo.getDistance();//距离
            SportData sportData = new SportData(step, calories, distance);
            bundle.putSerializable("sportdata", sportData);
            //上传到服务器

        } else {
            Log.e("stepData", stepInfo + "");
        }
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

            int[] colorArray = sleepTimeInfo.getSleepStatueArray();// 绘图中不同睡眠状态可用不同颜色表示，颜色自定义
            int[] timeArray = sleepTimeInfo.getDurationTimeArray();
            int[] timePointArray = sleepTimeInfo.getTimePointArray();

            Log.d("getSleepInfo", "Calendar=" + CalendarUtils.getCalendar(0)
                    + ",timeArray =" + timeArray + ",timeArray.length ="
                    + timeArray.length + ",colorArray =" + colorArray
                    + ",colorArray.length =" + colorArray.length
                    + ",timePointArray =" + timePointArray
                    + ",timePointArray.length =" + timePointArray.length);
            double total_hour = ((float) sleepTotalTime / 60f);
            DecimalFormat df1 = new DecimalFormat("0.0"); // 保留1位小数，带前导零
            deep_hour = deepTime / 60;
            Log.e("deep", deep_hour + "");
            // int deep_minute = (deepTime - deep_hour * 60);
            deep_minute = deepTime % 60;
            light_hour = lightTime / 60;
            // int light_minute = (lightTime - light_hour * 60);
            light_minute = lightTime % 60;
            Log.e("light", light_hour + "");
            int active_count = awakeCount;
            total_hour_str = df1.format(total_hour);
            SleepData sleepData = new SleepData(deep_hour, deep_minute, light_hour, light_minute, active_count, total_hour_str);
            //把这些数据上传到服务器
            bundle.putSerializable("sleepData", sleepData);
            if (total_hour_str.equals("0.0")) {
                total_hour_str = "0";
            }
        } else {
            Log.d("getSleepInfo", "sleepTimeInfo =" + sleepTimeInfo);

        }
    }

    /**
     * mBluetoothLeService.setICallback(this)检测返回的数据
     *
     * @param result
     * @param status
     */
    @Override
    public void OnResult(boolean result, int status) {
        Log.i(TAG, "result=" + result + ",status=" + status);
        if (status == ICallbackStatus.SYNC_TIME_OK) {// 设置时间操作完成，发送同步计步数据指令
            myHandler.postDelayed(runnable1, 1000);
        } else if (status == ICallbackStatus.OFFLINE_STEP_SYNC_OK) {//离线步数同步完成,发送同步睡眠数据指令
            myHandler.postDelayed(runnable2, 1000);
        } else if (status == ICallbackStatus.OFFLINE_SLEEP_SYNC_OK) {//离线睡眠同步完成，发送请求电量指令,开始测试心率指令
            myHandler.postDelayed(runnable3, 1000 * 30);//发送心率测试开始
            myHandler.postDelayed(runnable4, 1000 * 15);//请求手环电量
            EventBus.getDefault().post(bundle);
            myHandler.postDelayed(runnable5, 1000);//把数据上传到服务器
        } else if (status == ICallbackStatus.GET_BLE_BATTERY_OK) {
        } else if (status == ICallbackStatus.DISCONNECT_STATUS) {
            myHandler.sendEmptyMessage(DISCONNECT_MSG);
        } else if (status == ICallbackStatus.CONNECTED_STATUS) {
            myHandler.sendEmptyMessage(CONNECTED_MSG);
        } else if (result == true) {//表示计步状态,在老人清醒时，每隔15分钟上床一次手机坐标
            Log.e("stepTime", "现在是计步状态");
        } else if (result == false) {//表示睡眠状态，在老人睡觉时每隔两小时上传一次手机坐标
            Log.e("stepTime", "现在是睡眠状态");
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
     * 发送心率测试开启
     */
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
            }, 1000 * 10);
            myHandler.postDelayed(this, 1000 * 30);
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
            myHandler.postDelayed(this, 1000 * 15);
        }
    };
  private Runnable runnable5=  new Runnable() {
        @Override
        public void run() {
            Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile", "total_hour_str", "light_hour", "light_minute", "deep_hour", "deep_minute", "calories", "distance", "step"}, SessionHolder.user.getMobile(), total_hour_str, light_hour + "", light_minute + "", deep_hour + "", deep_minute + "", calories + "", distance + "", step + "");
            NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.UPDATE_SLEEP_SPORT, params, null, REQUEST_CODE_SPORTDATA_SLEEPDATA, MeFragment.this);
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
        for (int i = 0; i < contacts.size(); i++) {
            Contacts contact = contacts.get(i);
            phone = contact.getPhone();
            jsonObject = new com.alibaba.fastjson.JSONObject();
            jsonObject.put("mobile", phone);
            jsonArray.add(jsonObject);
        }
        object.put("contact_list", jsonArray);
        String jsonString = object.toJSONString();
        //  System.out.println(jsonString);
        return jsonString;
    }

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
