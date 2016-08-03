package com.lxkj.administrator.fealty.adapter;

import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.view.ViewGroup;

import com.lxkj.administrator.fealty.base.MyBaseAdapter;
import com.lxkj.administrator.fealty.ui.DevicelistitemView;
import com.lxkj.administrator.fealty.utils.AppUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/8/3.
 */
public class LeDeviceListAdapter extends MyBaseAdapter<BluetoothDevice> {
    public LeDeviceListAdapter(List list) {
        super(list);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            convertView=new DevicelistitemView(AppUtils.getBaseContext());
        }
        DevicelistitemView devicelistitemView= (DevicelistitemView) convertView;
        //获得蓝牙设备
        BluetoothDevice bluetoothDevice=list.get(position);
        //获得蓝牙设备地址
        devicelistitemView.device_address.setText(bluetoothDevice.getAddress());
        //获得蓝牙设备名称
        devicelistitemView.device_name.setText(bluetoothDevice.getName());
        return convertView;
    }
}
