package com.lxkj.administrator.fealty.fragment;

import android.support.v4.app.Fragment;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.adapter.HeathMonitoringAdapter;
import com.lxkj.administrator.fealty.base.BaseFragment;
import com.lxkj.administrator.fealty.widget.JazzyViewPager;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/26.
 */
@ContentView(R.layout.fragement_status)
public class StatusFragment extends BaseFragment {
    @ViewInject(R.id.mJazzy)
    private JazzyViewPager mJazzy;
    private HeathMonitoringAdapter adapter;
    private List<Fragment> fragments;
    @Override
    protected void init() {
        mJazzy.setTransitionEffect(JazzyViewPager.TransitionEffect.CubeOut);
        fragments = new ArrayList<Fragment>();
        initData();
        adapter = new HeathMonitoringAdapter(getChildFragmentManager(), fragments, mJazzy);
        mJazzy.setAdapter(adapter);
    }
    @Override
    protected void initListener() {

    }
    @Override
    protected void initData() {

        Fragment[] pages = new Fragment[]{new LoginFragment(), new RegistFragment(), new ResetPasswordFragment(), new MeFragment()};
        for (int i = 0; i < pages.length; i++) {
            fragments.add(pages[i]);
        }

    }
}
