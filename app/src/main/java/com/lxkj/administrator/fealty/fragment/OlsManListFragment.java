package com.lxkj.administrator.fealty.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.adapter.OldmanListviewAdpter;
import com.lxkj.administrator.fealty.base.BaseFragment;
import com.lxkj.administrator.fealty.bean.UserInfo;
import com.lxkj.administrator.fealty.event.NavFragmentEvent;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.Serializable;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/7/26.
 */
@ContentView(R.layout.fragement_oldmanlist)
public class OlsManListFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    @ViewInject(R.id.oldmanlistview)
    private ListView oldmanlistview;
//    @ViewInject(R.id.tv_tomain)
//    private TextView textView;
    ArrayList<UserInfo> list_user;
   private OldmanListviewAdpter adpter;
    @Override
    protected void init() {
        adpter=new OldmanListviewAdpter(list_user);
        oldmanlistview.setAdapter(adpter);
    }
    @Override
    protected void initListener() {
        oldmanlistview.setOnItemClickListener(this);

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onGetBunndle(Bundle arguments) {
        super.onGetBunndle(arguments);
        if (arguments==null){
            return;
        }
            Serializable data = arguments.getSerializable("list_user");
            list_user = (ArrayList<UserInfo>) data;

        }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


    }
    @Event(R.id.tv_tomain)
    private void goToMain(View view){
        EventBus.getDefault().post(new NavFragmentEvent(new MainTabsFragemnt()));
    }
}
