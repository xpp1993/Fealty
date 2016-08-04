package com.lxkj.administrator.fealty.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.BaseFragment;
import com.lxkj.administrator.fealty.event.NavFragmentEvent;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/7/26.
 */
@ContentView(R.layout.fragement_me)
public class MeFragment extends BaseFragment implements View.OnClickListener {
    @ViewInject(R.id.me_iv_left)
    private ImageView me_iv_left;
    @ViewInject(R.id.me_headpic)
    private CircleImageView circleImageView;
    @ViewInject(R.id.me_username)
    private TextView me_username;
    @ViewInject(R.id.me_phone)
    private TextView me_phone;
    @Override
    protected void init() {

    }

    @Override
    protected void initListener() {
        me_iv_left.setOnClickListener(this);

    }
    @Override
    protected void initData() {

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.me_iv_left:
                EventBus.getDefault().post(new NavFragmentEvent(new MeSettingFragment()));
                break;
        }
    }
}
