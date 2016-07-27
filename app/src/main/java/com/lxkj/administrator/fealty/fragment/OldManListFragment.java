package com.lxkj.administrator.fealty.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.BaseFragment;
import com.lxkj.administrator.fealty.bean.UserInfo;
import com.lxkj.administrator.fealty.event.NavFragmentEvent;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/7/26.
 */
@ContentView(R.layout.fragement_oldmanlist)
public class OldManListFragment extends BaseFragment {
    @ViewInject(R.id.oldmanlistview)
    private ListView oldmanlistview;
    private MyAdapter adapter;

    @Override
    protected void init() {
        adapter = new MyAdapter(getContext());
        oldmanlistview.setAdapter(adapter);

        List<UserInfo> list = new ArrayList<UserInfo>();
        for (int i = 0; i < 10; i++) {
            UserInfo userInfo = new UserInfo();
            userInfo.userName = "老人" + i;
            userInfo.bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.explore_friends);
            list.add(userInfo);
        }
        adapter.addData(list);
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {

    }


    class MyAdapter extends BaseAdapter {
        private List<UserInfo> list = new ArrayList<UserInfo>();
        private Context context;

        public MyAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public UserInfo getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                //加载上下文
                LayoutInflater inflater = LayoutInflater.from(getContext());
                //获得view
                convertView = inflater.inflate(R.layout.oldmanlist_item, parent, false);
                viewHolder = new ViewHolder();
                //把view的item设置给viewholder
                viewHolder.old_headpic = (ImageView) convertView.findViewById(R.id.old_headpic);
                viewHolder.tv_username = (TextView) convertView.findViewById(R.id.tv_username);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            //textview.setText(list.get(position).userName);
            UserInfo userInfo = list.get(position);
            viewHolder.tv_username.setText(userInfo.userName);
            //点击头像进入老人详情页
            viewHolder.old_headpic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToOldDetail();
                }
            });
            return convertView;
        }

        // 提供一个方法。添加数据到集合中。
        public void addData(List<UserInfo> data) {
            list.addAll(data);
            // 通知View更新界面
            notifyDataSetChanged();
        }

        class ViewHolder {
            TextView tv_username;
            ImageView old_headpic;
        }
    }

    private void goToOldDetail() {
        OldlistitemDetailsFragment oldlistitemDetailsFragment = new OldlistitemDetailsFragment();
        EventBus.getDefault().post(new NavFragmentEvent(oldlistitemDetailsFragment));
    }

}
