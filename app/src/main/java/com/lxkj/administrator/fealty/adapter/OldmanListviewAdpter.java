package com.lxkj.administrator.fealty.adapter;


import android.view.View;
import android.view.ViewGroup;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.MyBaseAdapter;
import com.lxkj.administrator.fealty.bean.UserInfo;
import com.lxkj.administrator.fealty.ui.ContactsListItemView;
import com.lxkj.administrator.fealty.utils.AppUtils;
import com.lxkj.administrator.fealty.utils.NetWorkAccessTools;

import java.util.List;

/**
 * Created by Administrator on 2016/7/27.
 */
public class OldmanListviewAdpter extends MyBaseAdapter<UserInfo> {

    public OldmanListviewAdpter(List<UserInfo> list) {
        super(list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new ContactsListItemView(AppUtils.getBaseContext());
        }
        ContactsListItemView contactsListItemView = (ContactsListItemView) convertView;
        contactsListItemView.getTv_phone().setText(list.get(position).getMobile());
        // 加载图片
//        NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).
//                toLoadImage("http://192.168.8.133:8080" + "/" + list.get(position).getUserpic(), contactsListItemView.getHeadimage(), R.mipmap.unknow_head, R.mipmap.unknow_head);
        NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).
                toLoadImage("http://120.76.27.233:8080" + "/" + list.get(position).getUserpic(), contactsListItemView.getHeadimage(), R.mipmap.unknow_head, R.mipmap.unknow_head);
        return convertView;
    }
}
