package com.lxkj.administrator.fealty.adapter;


import android.view.View;
import android.view.ViewGroup;

import com.lxkj.administrator.fealty.base.MyBaseAdapter;
import com.lxkj.administrator.fealty.bean.UserInfo;
import com.lxkj.administrator.fealty.ui.ContactsListItemView;
import com.lxkj.administrator.fealty.utils.AppUtils;

import org.xutils.x;

import java.util.List;

/**
 * Created by Administrator on 2016/7/27.
 */
public class OldmanListviewAdpter extends MyBaseAdapter<UserInfo>{

    public OldmanListviewAdpter(List<UserInfo> list) {
        super(list);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            convertView=new ContactsListItemView(AppUtils.getBaseContext());
        }
        ContactsListItemView contactsListItemView= (ContactsListItemView) convertView;
        contactsListItemView.getTv_phone().setText(list.get(position).getMobile());
        if (list.get(position).getUserpic()!=null){
            x.image().bind(contactsListItemView.getHeadimage(),list.get(position).getUserpic());
        }
        return convertView;
    }
}
