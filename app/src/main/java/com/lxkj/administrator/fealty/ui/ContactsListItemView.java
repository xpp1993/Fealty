package com.lxkj.administrator.fealty.ui;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.BaseView;

import org.xutils.view.annotation.ViewInject;

/**
 * Created by Administrator on 2016/8/2/0002.
 */
public class ContactsListItemView extends BaseView {
    @ViewInject(R.id.old_headpic)
    private ImageView headimage;
    @ViewInject(R.id.tv_username)
    private TextView tv_phone;
    public ContactsListItemView(Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        View.inflate(getContext(), R.layout.oldmanlist_item, this);
    }

    public ImageView getHeadimage() {
        return headimage;
    }

    public void setHeadimage(ImageView headimage) {
        this.headimage = headimage;
    }

    public TextView getTv_phone() {
        return tv_phone;
    }

    public void setTv_phone(TextView tv_phone) {
        this.tv_phone = tv_phone;
    }
}
