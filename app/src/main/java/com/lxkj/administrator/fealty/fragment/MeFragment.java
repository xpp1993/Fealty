package com.lxkj.administrator.fealty.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.avast.android.dialogs.fragment.ListDialogFragment;
import com.avast.android.dialogs.iface.IListDialogListener;
import com.avast.android.dialogs.iface.ISimpleDialogCancelListener;
import com.dd.CircularProgressButton;
import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.BaseFragment;
import com.lxkj.administrator.fealty.event.NavFragmentEvent;
import com.lxkj.administrator.fealty.manager.DecodeManager;
import com.lxkj.administrator.fealty.manager.ParameterManager;
import com.lxkj.administrator.fealty.manager.SessionHolder;
import com.lxkj.administrator.fealty.utils.AppUtils;
import com.lxkj.administrator.fealty.utils.CommonTools;
import com.lxkj.administrator.fealty.utils.NetWorkAccessTools;
import com.lxkj.administrator.fealty.utils.ThreadPoolUtils;
import com.lxkj.administrator.fealty.utils.ToastUtils;
import com.yc.peddemo.sdk.BLEServiceOperate;
import com.yc.peddemo.sdk.DeviceScanInterfacer;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import android.animation.ValueAnimator;

import java.util.ArrayList;
import java.util.Map;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by Administrator on 2016/7/26.
 */
@ContentView(R.layout.fragement_me)
public class MeFragment extends BaseFragment implements View.OnClickListener, NetWorkAccessTools.RequestTaskListener, DeviceScanInterfacer, IListDialogListener, ISimpleDialogCancelListener {
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
    public static final int REQUEST_USER_BYMIBILE = 0x01;
    private Handler handler = new MyHandler();
    private BLEServiceOperate mBLEServiceOperate;
    private final int REQUEST_ENABLE_BT = 1;
    private Handler mHandler = new Handler();
    private boolean mScanning = false;
    private static final int REQUEST_LIST_SINGLE = 11;
    private ArrayList<BluetoothDevice> mLeDevices;
    String deString;
    String[] deStrings;
    String address;
    ListDialogFragment.SimpleListDialogBuilder builder;

    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        //初始化个人资料显示
        initPersonalDataShow();
        Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile"}, SessionHolder.user.getMobile());
        NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.GET_USER_BYMOBILE, params, null, MeFragment.REQUEST_USER_BYMIBILE, this);
        bluee_iv_left.setText("连接设备");
        mHandler = new Handler();
        mLeDevices = new ArrayList<>();
        mBLEServiceOperate = BLEServiceOperate.getInstance(AppUtils.getBaseContext());//BluetoothLeService实例化准备,必须
        builder = ListDialogFragment
                .createBuilder(getActivity(), getActivity().getSupportFragmentManager())
                .setTitle("扫描到的设备:")
                .setCancelButtonText("取消")
                .setRequestCode(REQUEST_LIST_SINGLE)
                .setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

    }

    @Override
    protected void initListener() {
        me_iv_left.setOnClickListener(this);
        bluee_iv_left.setOnClickListener(this);
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.me_iv_left:
                EventBus.getDefault().post(new NavFragmentEvent(new MeSettingFragment()));
                break;
            case R.id.bluee_iv_left:
                //1实例化，绑定服务（DeviceScanInterfacer.getInstance(Context)）
                //2。是否支持ble 4.0，否退出app
                // Checks if Bluetooth is supported on the device.
                bluee_iv_left.setText("正在连接...");
                if (!mBLEServiceOperate.isSupportBle4_0()) {
                    ToastUtils.showToastInUIThread("设备不支持蓝牙4.0");
                    finish();
                    return;
                }
                //3.设置扫描监听
                mBLEServiceOperate.setDeviceScanListener(MeFragment.this);
                //4.蓝牙是否打开
                if (!mBLEServiceOperate.isBleEnabled()) {
                    Intent enableBtIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
                //5.是，扫描设备，未扫描扫到，相应提示
                scanLeDevice(true);
                //6.链接相应设备
                //7.如果连接成功，启动，否则提示连接失败
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
            }, 500);
            mScanning = true;
            mBLEServiceOperate.startLeScan();
        } else {
            mScanning = false;
            mBLEServiceOperate.stopLeScan();
        }
    }

    //    @Override
//    public void onPause() {
//        super.onPause();
//        scanLeDevice(false);
//        mLeDevices.clear();
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
                    DecodeManager.decodeSelfDataQuery(jsonObject, REQUEST_USER_BYMIBILE, handler);
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.showToastInUIThread("服务器异常！");
                }
                break;
        }
    }

    @Override
    public void onRequestFail(int requestCode, int errorNo) {
        ToastUtils.showToastInUIThread("网络连接错误，请检查重试！");
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

    @Override
    public void LeScanCallback(final BluetoothDevice bluetoothDevice, int j) {


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLeDevices.add(bluetoothDevice);
                System.out.print("???????????" + mLeDevices.size());
                deStrings = new String[mLeDevices.size()];
                if (bluetoothDevice == null || mLeDevices.size() <= 0) {
                    ToastUtils.showToastInUIThread("未扫描到设备!");
                    return;
                }
                ToastUtils.showToastInUIThread("正在扫描设备!");
                for (int i = 0; i < mLeDevices.size(); i++) {
                    BluetoothDevice device = mLeDevices.get(i);
                    address = device.getAddress();
                    if (device.getName() != null && device.getName().length() > 0) {
                        deString = device.getName() + "\n" + address;
                    } else {
                        deString = "未知设备" + "\n" + address;
                    }
                    if (deString != null && !"".equals(deString))
                        deStrings[i] = deString;
                }
                if (deString != null && !"".equals(deString) && deStrings != null && !"".equals(deStrings)) {
                    //  if (deStrings!=null){
//                    ListDialogFragment
//                            .createBuilder(getActivity(), getActivity().getSupportFragmentManager())
//                            .setTitle("扫描到的设备:")
//                            .setItems(deStrings)
//                            .setCancelButtonText("取消")
//                            .setRequestCode(REQUEST_LIST_SINGLE)
//                            .setChoiceMode(AbsListView.CHOICE_MODE_SINGLE)
                    builder.setItems(deStrings).show();
                    scanLeDevice(false);
                    mLeDevices.clear();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT
                && resultCode == getActivity().RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // IListDialogListener
    @Override
    public void onListItemSelected(CharSequence value, int number, int requestCode) {
        if (requestCode == REQUEST_LIST_SINGLE) {
            System.out.print("我选择的是Selected: " + value);
            mBLEServiceOperate.connect(address.trim());
            bluee_iv_left.setText("已连接");
        }
    }

    @Override
    public void onCancelled(int requestCode) {
        switch (requestCode) {
            case REQUEST_LIST_SINGLE:
//                Toast.makeText(getActivity(), "Nothing selected", Toast.LENGTH_SHORT).show();
                break;
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
            }
        }
    }
}
