package dexin.love.band.firmwareupgrade;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import org.xutils.view.annotation.ContentView;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.greenrobot.event.EventBus;
import dexin.love.band.R;
import dexin.love.band.base.BaseFragment;
import dexin.love.band.manager.ParameterManager;
import dexin.love.band.manager.SPManager;
import dexin.love.band.utils.AppUtils;
import dexin.love.band.utils.ThreadPoolUtils;

/**
 * Created by Administrator on 2016/10/23.
 */
@ContentView(R.layout.device_container)
public class ScannerFragment extends BaseFragment implements View.OnClickListener {
    //public class ScannerFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private final static String TAG = ScannerFragment.class.getSimpleName();
    private boolean isScanning = false;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler handler;
    DeviceConnectTask connectTask;
    BroadcastReceiver bluetoothGattReceiver, progressUpdateReceiver, connectionStateReceiver;
    LayoutInflater inflater;

    ProgressDialog dialog;
    ViewFlipper deviceContainer;
    View deviceMain, deviceParameterSettings, progressLayout;
    public ProgressBar progressBar;
    public TextView progressText;
    RadioButton memoryTypeSPI;
    LinearLayout imageBankContainer, blockSizeContainer;
    View parameterSpiView;
    TextView blockSize, imageBankSpinner;
    int memoryType;
    public BluetoothManager bluetoothManager = new SuotaManager(getActivity(), this);
    //1.获得sharedPreference对象,SharedPrefences只能放基础数据类型，不能放自定义数据类型。
    SharedPreferences preferences;
    Map<String, String> previousSettings;
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi,
                             final byte[] scanRecord) {

            ThreadPoolUtils.runTaskOnUIThread(new Runnable() {
                @Override
                public void run() {
                    List<UUID> uuids = Uuid.parseFromAdvertisementData(scanRecord);
                    for (UUID uuid : uuids) {
                        if (uuid.equals(Statics.SPOTA_SERVICE_UUID)) {
                            Log.e(TAG, device.getName() + "," + preferences.getString(ParameterManager.DEVICES_ADDRESS, ""));
                            if (device.getName().equals(preferences.getString(ParameterManager.DEVICES_ADDRESS, ""))) {
                                bluetoothManager.setDevice(device);
                                initdata();
                            } else {
                                Toast.makeText(AppUtils.getBaseContext(), "未扫描到设备！", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            });
        }
    };

    private void initdata() {
        connectTask = new DeviceConnectTask(AppUtils.getBaseContext(), ScannerFragment.this, bluetoothManager.getDevice()) {
            @Override
            protected void onProgressUpdate(BluetoothGatt... gatt) {
                BluetoothGattSingleton.setGatt(gatt[0]);
            }
        };
        connectTask.execute();

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("正在升级固件，请稍后....");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        inflater = LayoutInflater.from(getActivity());
        deviceContainer = (ViewFlipper) getActivity().findViewById(R.id.deviceLayoutContainer);
        deviceMain = inflater.inflate(R.layout.device_main, deviceContainer, true);
        deviceParameterSettings = inflater.inflate(R.layout.device_parameter_settings, deviceContainer, true);
        progressLayout = inflater.inflate(R.layout.progress, deviceContainer, true);
        switchView(0);
        progressText = (TextView) progressLayout.findViewById(R.id.progress_text);
        progressBar = (ProgressBar) progressLayout.findViewById(R.id.progress_bar);
        progressBar.setProgress(0);
        progressBar.setMax(100);
    }

    @Override
    protected void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        preferences = SPManager.getSharedPreferences(AppUtils.getBaseContext());
        previousSettings = (Map<String, String>) Statics.getAllPreviousInput(AppUtils.getBaseContext());
        this.initialize();
        if (!Statics.fileDirectoriesCreated(AppUtils.getBaseContext()) || true) {
            File.createFileDirectories(AppUtils.getBaseContext());
            Statics.setFileDirectoriesCreated(AppUtils.getBaseContext());
        }

        ScannerFragment.this.bluetoothGattReceiver = new BluetoothGattReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                super.onReceive(context, intent);
                bluetoothManager.processStep(intent);
            }
        };
        ScannerFragment.this.progressUpdateReceiver = new BluetoothGattReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                super.onReceive(context, intent);
                int progress = intent.getIntExtra("progess", 0);
                progressBar.setProgress(progress);
            }
        };

        ScannerFragment.this.connectionStateReceiver = new BluetoothGattReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                super.onReceive(context, intent);
                int connectionState = intent.getIntExtra("state", 0);
                connectionStateChanged(connectionState);
            }
        };

        getActivity().registerReceiver(
                ScannerFragment.this.bluetoothGattReceiver,
                new IntentFilter(Statics.BLUETOOTH_GATT_UPDATE));

        getActivity().registerReceiver(
                ScannerFragment.this.progressUpdateReceiver,
                new IntentFilter(Statics.PROGRESS_UPDATE));

        getActivity().registerReceiver(
                ScannerFragment.this.connectionStateReceiver,
                new IntentFilter(Statics.CONNECTION_STATE_UPDATE));

    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mBluetoothAdapter.cancelDiscovery();
        try {
            getActivity().unregisterReceiver(this.bluetoothGattReceiver);
            getActivity().unregisterReceiver(this.progressUpdateReceiver);
            getActivity().unregisterReceiver(this.connectionStateReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    // 用EventBus 来导航,订阅者
    public void onEventMainThread(Bundle event) {
    }

    private void initialize() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Log.e(TAG, "Bluetooth not supported.");
        }
        handler = new Handler();
        this.startDeviceScan();
    }

    private void startDeviceScan() {
        isScanning = true;
        Log.d(TAG, "Start scanning");
        mBluetoothAdapter.startLeScan(mLeScanCallback);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopDeviceScan();
            }
        }, 7000);

    }

    private void stopDeviceScan() {
        if (isScanning) {
            isScanning = false;
            Log.d(TAG, "Stop scanning");
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

    }

    public void initMainScreen() {
        Log.d(TAG, "initMainScreen");
        BluetoothDevice device = bluetoothManager.getDevice();
        bluetoothManager = new SuotaManager(getActivity(), ScannerFragment.this);
        bluetoothManager.setDevice(device);
        initFileList();
        switchView(1);
    }

    @Override
    public boolean finish() {
        return true;
    }

    private void initFileList() {
        String filename = ParameterManager.FIRMWARE_NAME;
        bluetoothManager.setFileName(filename);
        Log.d(TAG, "Clicked: " + filename);
        try {
            bluetoothManager.setFile(File.getByFileName(filename));
            initParameterSettings();
            switchView(2);
            startUpdate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initParameterSettings() {
        memoryTypeSPI = (RadioButton) deviceParameterSettings.findViewById(R.id.memoryTypeSPI);
        memoryTypeSPI.setOnClickListener(this);
        imageBankContainer = (LinearLayout) deviceParameterSettings.findViewById(R.id.imageBankContainer);
        blockSizeContainer = (LinearLayout) deviceParameterSettings.findViewById(R.id.blockSizeContainer);
        blockSize = (TextView) deviceParameterSettings.findViewById(R.id.blockSize);
        String previousText = null;
        if (bluetoothManager.type == SuotaManager.TYPE) {
            imageBankContainer.setVisibility(View.VISIBLE);
            blockSizeContainer.setVisibility(View.VISIBLE);
            previousText = previousSettings.get(String.valueOf(R.id.blockSize));
            if (previousText == null || previousText.equals("")) {
                previousText = Statics.DEFAULT_BLOCK_SIZE_VALUE;
            }
            blockSize.setText(previousText);
        }

        // Different views for memory types
        parameterSpiView = deviceParameterSettings.findViewById(R.id.pSpiContainer);

        // SUOTA image bank
        imageBankSpinner = (TextView) deviceParameterSettings.findViewById(R.id.imageBank);
        previousText = previousSettings.get(String.valueOf(R.id.imageBank));
        if (previousText == null || previousText.equals("")) {
            previousText = "0";
        }
        imageBankSpinner.setText(previousText);
        // Spinners for SPI
        String[] gpioValues = getResources().getStringArray(R.array.gpio_values);

        String stringValue = gpioValues[Statics.DEFAULT_MISO_VALUE];
        int value = Statics.gpioStringToInt(stringValue);
        Statics.setPreviousInput(AppUtils.getBaseContext(), Statics.DEFAULT_MISO_VALUE, stringValue);
        bluetoothManager.setMISO_GPIO(value);
        Log.d("gpioValues", "MISO: " + stringValue);

        stringValue = gpioValues[Statics.DEFAULT_MOSI_VALUE];
        value=Statics.gpioStringToInt(stringValue);
        Statics.setPreviousInput(AppUtils.getBaseContext(), Statics.DEFAULT_MOSI_VALUE, stringValue);
        bluetoothManager.setMOSI_GPIO(value);
        Log.d("gpioValues", "MOSI: " + stringValue);

        stringValue = gpioValues[Statics.DEFAULT_CS_VALUE];
        value=Statics.gpioStringToInt(stringValue);
        Statics.setPreviousInput(AppUtils.getBaseContext(), Statics.DEFAULT_CS_VALUE, stringValue);
        bluetoothManager.setCS_GPIO(value);
        Log.d("gpioValues", "CS: " + stringValue);

        stringValue = gpioValues[Statics.DEFAULT_SCK_VALUE];
        value=Statics.gpioStringToInt(stringValue);
        Statics.setPreviousInput(AppUtils.getBaseContext(), Statics.DEFAULT_SCK_VALUE, stringValue);
        bluetoothManager.setSCK_GPIO(value);
        Log.d("gpioValues", "SCK: " + stringValue);

        int previousMemoryType;
        previousMemoryType = Integer.parseInt(Statics.getPreviousInput(AppUtils.getBaseContext(), Statics.MEMORY_TYPE_SUOTA_INDEX));
        if (previousMemoryType > 0) {
            setMemoryType(previousMemoryType);
        } else {
            // Set default memory type to SPI
            setMemoryType(Statics.MEMORY_TYPE_SPI);
        }
    }

    private void startUpdate() {
        Intent intent = new Intent();

        if (bluetoothManager.type == SuotaManager.TYPE) {
            Statics.setPreviousInput(AppUtils.getBaseContext(), R.id.blockSize, blockSize.getText().toString());
        }
        // Set default block size to 1 for SPOTA, this will not be used in this case
        int fileBlockSize = 1;
        if (bluetoothManager.type == SuotaManager.TYPE) {
            fileBlockSize = Integer.parseInt(blockSize.getText().toString());
        }
        bluetoothManager.getFile().setFileBlockSize(fileBlockSize);

        intent.setAction(Statics.BLUETOOTH_GATT_UPDATE);
        intent.putExtra("step", 1);
        getActivity().sendBroadcast(intent);
        switchView(3);
        handler.post(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        });
    }

    private void setMemoryType(int memoryType) {
        this.clearMemoryTypeChecked();
        this.memoryType = memoryType;
        bluetoothManager.setMemoryType(memoryType);
        parameterSpiView.setVisibility(View.GONE);

        Statics.setPreviousInput(AppUtils.getBaseContext(), Statics.MEMORY_TYPE_SUOTA_INDEX, String.valueOf(memoryType));
        if (memoryType == Statics.MEMORY_TYPE_SPI) {
            parameterSpiView.setVisibility(View.VISIBLE);
            memoryTypeSPI.setChecked(true);
        }
    }

    public void switchView(int viewIndex) {
        this.deviceContainer.setDisplayedChild(viewIndex);
    }

    private void clearMemoryTypeChecked() {
        memoryTypeSPI.setChecked(false);
    }

    private void connectionStateChanged(int connectionState) {
        if (connectionState == BluetoothProfile.STATE_DISCONNECTED) {
            Toast.makeText(AppUtils.getBaseContext(), this.bluetoothManager.getDevice().getName() + " disconnected.", Toast.LENGTH_LONG).show();
            if (BluetoothGattSingleton.getGatt() != null)
                BluetoothGattSingleton.getGatt().close();
            if (!bluetoothManager.isFinished()) {
                if (!bluetoothManager.getError())
                    finish();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.memoryTypeSPI:
                setMemoryType(Statics.MEMORY_TYPE_SPI);
                break;
        }
    }
}
