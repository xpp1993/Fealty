package com.lxkj.administrator.fealty.ui;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.BaseView;

import org.xutils.view.annotation.ViewInject;

/**
 * Created by Administrator on 2016/8/3.
 */
public class DevicelistitemView extends BaseView {
    @ViewInject(R.id.device_name)
    public TextView device_name;
    @ViewInject(R.id.device_address)
    public TextView device_address;
    public DevicelistitemView(Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        View.inflate(getContext(), R.layout.listitem_device, this);
    }

    @Override
    public Button getButton() {
        return null;
    }
}
