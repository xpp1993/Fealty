package com.lxkj.administrator.fealty.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.adapter.LeDeviceListAdapter;
import com.lxkj.administrator.fealty.utils.AppUtils;
import com.lxkj.administrator.fealty.utils.ToastUtils;
import com.yc.peddemo.sdk.BLEServiceOperate;
import com.yc.peddemo.sdk.DeviceScanInterfacer;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/9/6.
 */
public class ScanDeviceActivity extends Activity implements View.OnClickListener, DeviceScanInterfacer, AdapterView.OnItemClickListener {
    private ImageView bar_back;
    private TextView bar_biaoti;
    private ImageView bar_view_left_line;
    private ListView devicelistview;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private boolean mScanning;
    private final int REQUEST_ENABLE_BT = 1;
    private final long SCAN_PERIOD = 10000;
    private Handler mHandler;
    private BLEServiceOperate mBLEServiceOperate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragement_devicelist);
        initializes();
        // Checks if Bluetooth is supported on the device.
        if (!mBLEServiceOperate.isSupportBle4_0()) {
            ToastUtils.showToastInUIThread("设备不支持蓝牙4.0");
            finish();
            return;
        }
        bar_back.setOnClickListener(this);
        mBLEServiceOperate.setDeviceScanListener(this);//for DeviceScanInterfacer
        devicelistview.setOnItemClickListener(this);
    }

    /**
     * 初始化
     */
    private void initializes() {
        bar_back = (ImageView) findViewById(R.id.bar_iv_left);
        bar_biaoti = (TextView) findViewById(R.id.bar_tv_title_left);
        bar_view_left_line = (ImageView) findViewById(R.id.bar_view_left_line);
        devicelistview = (ListView) findViewById(R.id.devicelistview);
        mHandler = new Handler();
        bar_back.setVisibility(View.VISIBLE);
        bar_view_left_line.setVisibility(View.VISIBLE);
        bar_biaoti.setVisibility(View.VISIBLE);
        bar_biaoti.setText("扫描到的设备如下");
        mBLEServiceOperate = BLEServiceOperate.getInstance(AppUtils.getBaseContext());// 用于BluetoothLeService实例化准备,必须
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mBLEServiceOperate.isBleEnabled()) {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter(AppUtils.getBaseContext());
        devicelistview.setAdapter(mLeDeviceListAdapter);
        scanLeDevice(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT
                && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBLEServiceOperate.stopLeScan();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBLEServiceOperate.startLeScan();
        } else {
            mScanning = false;
            mBLEServiceOperate.stopLeScan();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBLEServiceOperate.unBindService();// unBindService
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bar_iv_left:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void LeScanCallback(BluetoothDevice bluetoothDevice, int i) {
        mLeDeviceListAdapter.addDevice(bluetoothDevice);
        mLeDeviceListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
        if (device == null)
            return;
        if (device.getAddress()==null||device.getAddress().equals(""))
            return;
        //把数据传出去
        EventBus.getDefault().post(device.getAddress());
        if (mScanning) {
            mBLEServiceOperate.stopLeScan();
            mScanning = false;
        }
        ToastUtils.showToastInUIThread(device.getName() + "\n" + device.getAddress());
        finish();
    }
}
