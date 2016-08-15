package com.lxkj.administrator.fealty.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.MyBaseAdapter;
import com.lxkj.administrator.fealty.ui.DevicelistitemView;
import com.lxkj.administrator.fealty.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/3.
 */
public class LeDeviceListAdapter extends BaseAdapter {
//    public LeDeviceListAdapter(List list) {
//        super(list);
//    }
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        if (convertView==null){
//            convertView=new DevicelistitemView(AppUtils.getBaseContext());
//        }
//        DevicelistitemView devicelistitemView= (DevicelistitemView) convertView;
//        //获得蓝牙设备
//        BluetoothDevice bluetoothDevice=list.get(position);
//        //获得蓝牙设备地址
//        devicelistitemView.device_address.setText(bluetoothDevice.getAddress());
//        //获得蓝牙设备名称
//        devicelistitemView.device_name.setText(bluetoothDevice.getName());
//        return convertView;
//    }
private ArrayList<BluetoothDevice> mLeDevices;
    private LayoutInflater mInflator;

    public LeDeviceListAdapter() {
        super();
        mLeDevices = new ArrayList<BluetoothDevice>();
      //  mInflator = DeviceScanActivity.this.getLayoutInflater();
        mInflator=LayoutInflater.from(AppUtils.getBaseContext());
    }

    public void addDevice(BluetoothDevice device) {
        if (!mLeDevices.contains(device)) {
            mLeDevices.add(device);
        }
    }

    public BluetoothDevice getDevice(int position) {
        return mLeDevices.get(position);
    }

    public void clear() {
        mLeDevices.clear();
    }

    @Override
    public int getCount() {
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return mLeDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {
            view = mInflator.inflate(R.layout.listitem_device, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceName = (TextView) view
                    .findViewById(R.id.device_name);
            viewHolder.deviceAddress = (TextView) view
                    .findViewById(R.id.device_address);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        BluetoothDevice device = mLeDevices.get(i);
        final String deviceName = device.getName();
        if (deviceName != null && deviceName.length() > 0)
            viewHolder.deviceName.setText(deviceName);
        else
            viewHolder.deviceName.setText("未知设备");
        viewHolder.deviceAddress.setText(device.getAddress());

        return view;
    }
}
 class ViewHolder {
    TextView deviceName;
    TextView deviceAddress;
}

