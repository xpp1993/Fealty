package dexin.love.band.adapter;


import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import dexin.love.band.R;
import dexin.love.band.base.MyBaseAdapter;
import dexin.love.band.bean.UserInfo;
import dexin.love.band.ui.ContactsListItemView;
import dexin.love.band.utils.AppUtils;
import dexin.love.band.utils.NetWorkAccessTools;

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
       NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).
              toLoadImage("http://192.168.8.133:8080" + "/" + list.get(position).getUserpic(), contactsListItemView.getHeadimage(), R.mipmap.unknow_head, R.mipmap.unknow_head);
      // NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).
       //        toLoadImage("http://120.76.27.233:8080" + "/" + list.get(position).getUserpic(), contactsListItemView.getHeadimage(), R.mipmap.unknow_head, R.mipmap.unknow_head);
        return convertView;
    }
}
