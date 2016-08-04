package com.lxkj.administrator.fealty.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.lxkj.administrator.fealty.fragment.HealthDataFragement;
import com.lxkj.administrator.fealty.widget.JazzyViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 健康监测adapter
 * Created by Administrator on 2016/8/3.
 */
public class HeathMonitoringAdapter extends FragmentPagerAdapter {
    private List<HealthDataFragement> fragments;
    private JazzyViewPager mJazzy;

    public HeathMonitoringAdapter(FragmentManager fm, JazzyViewPager mJazzy, List<HealthDataFragement> fragments) {
        super(fm);
        this.fragments = fragments;
        this.mJazzy = mJazzy;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments == null ? 0 : fragments.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object obj = super.instantiateItem(container, position);
        mJazzy.setObjectForPosition(obj, position);
        return obj;

    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        if (object != null) {
            return ((Fragment) object).getView() == view;
        } else {
            return false;
        }

    }

}
