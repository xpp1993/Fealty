package com.lxkj.administrator.fealty.fragment;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.BaseFragment;
import com.lxkj.administrator.fealty.event.NavFragmentEvent;
import com.lxkj.administrator.fealty.utils.AppUtils;

import org.xutils.view.annotation.ContentView;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/7/26.
 */
@ContentView(R.layout.fragment_reset_password)
public class ResetPasswordFragment extends BaseFragment {
    protected void init() {
        EventBus.getDefault().register(this);
    }
    // 用EventBus 来导航,订阅者
    public void onEventMainThread(NavFragmentEvent event) {
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public boolean finish() {
        return true;
    }
}
