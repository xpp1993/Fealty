package com.lxkj.administrator.test;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.Toast;

import com.avast.android.dialogs.fragment.ListDialogFragment;
import com.yc.peddemo.sdk.BLEServiceOperate;
import com.yc.peddemo.sdk.DeviceScanInterfacer;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements DeviceScanInterfacer {
    private Button button;
    private BLEServiceOperate mBLEServiceOperate;
    private final int REQUEST_ENABLE_BT = 1;
    private Handler mHandler = new Handler();
    private boolean mScanning = false;
    private static final int REQUEST_LIST_SINGLE = 11;
    private ArrayList<BluetoothDevice> mLeDevices;
    String deString;
    String[] deStrings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button1);
        mHandler = new Handler();
        mLeDevices = new ArrayList<>();
        deStrings = new String[mLeDevices.size()+1];
        mBLEServiceOperate = BLEServiceOperate.getInstance(this);//BluetoothLeService实例化准备,必须
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //2。是否支持ble 4.0，否退出app
                //   Checks if Bluetooth is supported on the device.
                if (!mBLEServiceOperate.isSupportBle4_0()) {
                    Toast.makeText(MainActivity.this, "设备不支持蓝牙4.0", Toast.LENGTH_LONG).show();

                    finish();
                    return;
                }
                //3.设置扫描监听
                mBLEServiceOperate.setDeviceScanListener(MainActivity.this);
            }
        });

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (!mBLEServiceOperate.isBleEnabled()) {
//            Intent enableBtIntent = new Intent(
//                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//        }
//        //5.是，扫描设备，未扫描扫到，相应提示
//        scanLeDevice(true);
//    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
      mLeDevices.clear();
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mBLEServiceOperate.unBindService();// unBindService
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
            }, 1000);

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
        if (requestCode == REQUEST_ENABLE_BT
                && resultCode == RESULT_CANCELED) {

            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void LeScanCallback(final BluetoothDevice bluetoothDevice, int i) {


    }
}
