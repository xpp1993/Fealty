package dexin.love.band.firmwareupgrade;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import de.greenrobot.event.EventBus;
import dexin.love.band.R;
import dexin.love.band.base.BaseFragment;
import dexin.love.band.utils.AppUtils;
import dexin.love.band.utils.ThreadPoolUtils;

/**
 * Created by Administrator on 2016/10/23.
 */
@ContentView(R.layout.activity_scan)
public class ScannerFragment extends BaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    @ViewInject(R.id.device_list)
    private ListView deviceListView;
    @ViewInject(R.id.bar_iv_left)
    private ImageView bar_back;
    @ViewInject(R.id.bar_tv_title_left)
    private TextView bar_biaoti;
    @ViewInject(R.id.bar_view_left_line)
    private ImageView bar_view_left_line;
    private final static String TAG = ScannerFragment.class.getSimpleName();
    private boolean isScanning = false;
    private BluetoothAdapter mBluetoothAdapter;
    private HashMap<String, BluetoothDevice> scannedDevices;
    private ArrayList<BluetoothDevice> bluetoothDeviceList;
    //  private ArrayAdapter<String> mArrayAdapter;
    private MyBaseAdapter mArrayAdapter;
    private Handler handler;
    private ProgressDialog progressDialog;//更新软件进度条
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi,
                             final byte[] scanRecord) {

            ThreadPoolUtils.runTaskOnUIThread(new Runnable() {
                @Override
                public void run() {
                    List<UUID> uuids = Uuid.parseFromAdvertisementData(scanRecord);
                    for (UUID uuid : uuids) {
                        if (uuid.equals(Statics.SPOTA_SERVICE_UUID) && !scannedDevices.containsKey(device.getAddress())) {
                            scannedDevices.put(device.getAddress(), device);
                            bluetoothDeviceList.add(device);
                            mArrayAdapter.notifyDataSetChanged();
                            progressDialog.dismiss();
                        }
                    }
                }
            });
        }
    };

    @Override
    protected void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        this.initialize();
        if (!Statics.fileDirectoriesCreated(AppUtils.getBaseContext()) || true) {
            File.createFileDirectories(AppUtils.getBaseContext());
            Statics.setFileDirectoriesCreated(AppUtils.getBaseContext());
        }
    }

    @Override
    protected void initListener() {
        bar_back.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        // Disable discovery on close
        mBluetoothAdapter.cancelDiscovery();
        super.onDestroy();
    }

    // 用EventBus 来导航,订阅者
    public void onEventMainThread(Bundle event) {
    }

    private void initialize() {
        bar_back.setVisibility(View.VISIBLE);
        bar_view_left_line.setVisibility(View.VISIBLE);
        bar_biaoti.setVisibility(View.VISIBLE);
        bar_biaoti.setText("点击连接升级模式设备");
        scannedDevices = new HashMap<String, BluetoothDevice>();
        bluetoothDeviceList = new ArrayList<BluetoothDevice>();
        mArrayAdapter = new MyBaseAdapter();
        // Initialize Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Log.e(TAG, "Bluetooth not supported.");
        }
        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.show();
        handler = new Handler();
        this.startDeviceScan();
        deviceListView.setAdapter(mArrayAdapter);
        deviceListView.setOnItemClickListener(this);

    }

    private void startDeviceScan() {
        isScanning = true;
        bluetoothDeviceList.clear();
        scannedDevices.clear();
        mArrayAdapter.notifyDataSetChanged();
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

    /**
     * On click listener for scanned devices
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        stopDeviceScan();
        BluetoothDevice device = bluetoothDeviceList.get(position);
        Intent i = new Intent(getActivity(), DeviceActivity.class);
        i.putExtra("device", device);
        startActivity(i);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.bar_iv_left:
                getActivity().onBackPressed();
                break;
        }
    }

    class MyBaseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return bluetoothDeviceList.size();
        }

        @Override
        public Object getItem(int position) {
            return bluetoothDeviceList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(AppUtils.getBaseContext());
                convertView = inflater.inflate(R.layout.listitem_device, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.text_deviceName = (TextView) convertView
                        .findViewById(R.id.device_name);
                viewHolder.text_deviceMac = (TextView) convertView
                        .findViewById(R.id.device_address);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.text_deviceName.setText(bluetoothDeviceList.get(position).getName());
            viewHolder.text_deviceMac.setText(bluetoothDeviceList.get(position).getAddress());
            return convertView;
        }
    }

    class ViewHolder {
        TextView text_deviceName, text_deviceMac;
    }
}
