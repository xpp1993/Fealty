package com.lxkj.administrator.fealty.fragment;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.BaseFragment;
import com.lxkj.administrator.fealty.widget.QuickFragmentTabHost;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by Administrator on 2016/7/26.
 */
@ContentView(R.layout.fragment_main_tab)
public class MainTabsFragemnt extends BaseFragment {
    @ViewInject(android.R.id.tabhost)
    private QuickFragmentTabHost mTabsHost;
    private final String[] TITLES = {"状态", "老人", "我的"};
    private final String[] TAGS = {"status", "oldman", "me"};
    private int[] ICONS = {R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher};
    @Override
    protected void init() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {

    }
}
