package dexin.love.band.ui;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.xutils.view.annotation.ViewInject;

import dexin.love.band.R;
import dexin.love.band.base.BaseView;

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
