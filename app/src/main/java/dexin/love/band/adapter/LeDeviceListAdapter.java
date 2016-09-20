package dexin.love.band.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import dexin.love.band.R;

/**
 * Created by Administrator on 2016/8/3.
 */
public class LeDeviceListAdapter extends BaseAdapter {
    private ArrayList<BluetoothDevice> mLeDevices;
    private Context context;

    public LeDeviceListAdapter(Context context) {
        this.context = context;
        mLeDevices = new ArrayList<BluetoothDevice>();
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
        if (view == null) {
            LayoutInflater mInflator = LayoutInflater.from(context);
            view = mInflator.inflate(R.layout.listitem_device, viewGroup, false);
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
        if (deviceName != null && deviceName.length() > 0) {
            if (deviceName.contains("MH")) {
                 viewHolder.deviceName.setText("德信孝心手环");
            } else {
                viewHolder.deviceName.setText(deviceName);
            }
        } else
            viewHolder.deviceName.setText("未知设备");
        viewHolder.deviceAddress.setText(device.getAddress());

        return view;
    }

    class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
}


