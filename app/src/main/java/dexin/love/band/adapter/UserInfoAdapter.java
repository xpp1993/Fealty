package dexin.love.band.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dexin.love.band.R;
import dexin.love.band.bean.UserInfo;

/**
 * Created by Administrator on 2016/9/5.
 */
public class UserInfoAdapter extends BaseAdapter {
    private List<UserInfo> list;
    private Context context;

    @Override
    public int getCount() {
        return list.size();
    }

    public UserInfoAdapter(Context context) {
        this.context = context;
        list = new ArrayList<UserInfo>();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.bindeduser_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.text_subject = (TextView) convertView
                    .findViewById(R.id.text_subject);
            viewHolder.text_address = (TextView) convertView
                    .findViewById(R.id.tv_address);
            viewHolder.text_xin = (TextView) convertView
                    .findViewById(R.id.text_xin);
            viewHolder.text_bettry = (TextView) convertView
                    .findViewById(R.id.text_bettry);
            viewHolder.text_bettrym = (TextView) convertView
                    .findViewById(R.id.text_bettrym);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        UserInfo userInfo = list.get(position);
        viewHolder.text_subject.setText(userInfo.getIdentity());
        viewHolder.text_address.setText(userInfo.getAddress());
        viewHolder.text_xin.setText("实时心率："+userInfo.getCurrentHeart() );
        viewHolder.text_bettry.setText("手环电量："+userInfo.getCuffElectricity());
        viewHolder.text_bettrym.setText("手机电量:"+userInfo.getMobileElectricity());
        return convertView;
    }
    class ViewHolder {
        TextView text_subject, text_address, text_xin, text_bettry, text_bettrym;
    }

    // 将数据添加到集合
    public void addData(List<UserInfo> data) {
            list.addAll(data);
            notifyDataSetChanged();
    }
    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }
}
