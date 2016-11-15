package com.example.xpp.blelib;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BleEngine {
    private final static String TAG = BleEngine.class.getSimpleName();
    private List<BluetoothDevice> mBluetoothDevices = new ArrayList<>();
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattService mBluetoothGattService;
    private BluetoothGattCharacteristic mBluetoothGattCharacteristicWriteable;
    private BluetoothGattCharacteristic mBluetoothGattCharacteristicListenable;
    private Handler mHandler = new Handler();
    //private BluetoothDevice device;
    private String address;

    public interface ListScanCallback {
        void onDeviceFound(final List<BluetoothDevice> devices);
    }

    private Context mContext;

    public BleEngine(Context c) {
        mContext = c;
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mBluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                BroadcastManager.sendBroadcast4ConnectState(mContext, GlobalValues.BROADCAST_INTENT_CONNECT_STATE_CHANGED, false);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            try {
                mBluetoothGattService = mBluetoothGatt.getService(UUID.fromString("000056ff-0000-1000-8000-00805f9b34fb"));
                mBluetoothGattCharacteristicWriteable = mBluetoothGattService.getCharacteristic(UUID.fromString("000033f3-0000-1000-8000-00805f9b34fb"));
                mBluetoothGattCharacteristicListenable = mBluetoothGattService.getCharacteristic(UUID.fromString("000033f4-0000-1000-8000-00805f9b34fb"));
                BluetoothGattDescriptor bluetoothGattDescriptor = mBluetoothGattCharacteristicListenable.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                if (mBluetoothGatt.setCharacteristicNotification(mBluetoothGattCharacteristicListenable, true) &&
                        mBluetoothGatt.writeDescriptor(bluetoothGattDescriptor)) {
                    BroadcastManager.sendBroadcast4ConnectState(mContext, GlobalValues.BROADCAST_INTENT_CONNECT_STATE_CHANGED, true);
                } else throw new RuntimeException("启动notify失败");
            } catch (Exception e) {
                e.printStackTrace();
                BroadcastManager.sendBroadcast4ConnectState(mContext, GlobalValues.BROADCAST_INTENT_CONNECT_STATE_CHANGED, false);
            }

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            Log.e(TAG, "onCharacteristicChanged " + gatt.getDevice().getName()
                    + " " + characteristic.getUuid().toString()
                    + " -> " + Utils.bytesToHexString(characteristic.getValue())
            );
            CommandManager.decode(mContext, characteristic.getValue());
//            try {
//                Thread.sleep(50);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }// 在接收数据之后线程Sleep()一段时间，可以提高数据接收的可靠性。
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.d("wyj", "onCharacteristicWrite");
            BroadcastManager.sendBroadcast4CommandReceived(mContext, GlobalValues.BROADCAST_INTENT_COMMAND_RECEIVED);
        }
    };

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    public boolean enableBle() {
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to enableBle.");
            return false;
        } else {
            return mBluetoothAdapter.enable();
        }
    }

    public void scanBleDevice(final boolean enable, int scanTimeMillisecond, final ListScanCallback callback) {
        final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                if (!mBluetoothDevices.contains(device)) {
                    mBluetoothDevices.add(device);
                }
            }
        };
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    callback.onDeviceFound(mBluetoothDevices);
                }
            }, scanTimeMillisecond);
            mBluetoothDevices.clear();
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            callback.onDeviceFound(mBluetoothDevices);
        }
    }

    public void connect(final String address) {
        this.address = address;
        if (mBluetoothAdapter == null)
            BroadcastManager.sendBroadcast4ConnectState(mContext, GlobalValues.BROADCAST_INTENT_CONNECT_STATE_CHANGED, true);
        mBluetoothAdapter.stopLeScan(null);
        if (mBluetoothGatt == null) {
            mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);
            if (mBluetoothDevice == null) return;
            mBluetoothGatt = mBluetoothDevice.connectGatt(mContext, false, mGattCallback);
            return;
        } else {
            mBluetoothGatt.connect();
        }
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public boolean readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return false;
        }
        return mBluetoothGatt.readCharacteristic(characteristic);
    }

    public boolean writeCharacteristic(byte[] data) {
        if (mBluetoothGattCharacteristicWriteable == null || mBluetoothGatt == null) {
           close();
            if (address != null && !"".equals(address))
                connect(address);
            Log.e(TAG, address);
            return false;
        }
        Log.e(TAG, "writeCharacteristic");
        mBluetoothGattCharacteristicWriteable.setValue(data);
        return mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristicWriteable);
    }
}


