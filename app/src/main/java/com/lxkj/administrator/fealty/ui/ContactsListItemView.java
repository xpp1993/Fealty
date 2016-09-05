package com.lxkj.administrator.fealty.ui;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.BaseView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/8/2/0002.
 */
public class ContactsListItemView extends BaseView {
    @ViewInject(R.id.fragment_friend_list_item_iv)
    private CircleImageView headimage;
    @ViewInject(R.id.fragment_friend_list_item_tv)
    private TextView tv_phone;
    public ContactsListItemView(Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        View.inflate(getContext(), R.layout.fragment_friend_list_item, this);
    }

    @Override
    public Button getButton() {
        return null;
    }

    public CircleImageView getHeadimage() {
        return headimage;
    }

//    public void setHeadimage(ImageView headimage) {
//        this.headimage = headimage;
//    }

    public TextView getTv_phone() {
        return tv_phone;
    }

//    public void setTv_phone(TextView tv_phone) {
//        this.tv_phone = tv_phone;
//    }
}
