package com.lxkj.administrator.fealty.event;

import android.os.Bundle;

import com.lxkj.administrator.fealty.base.BaseFragment;

/**
 * Created by Administrator on 2016/7/26.
 */
public class NavFragmentEvent {
    public BaseFragment fragment;
    public Bundle bundle;
    public NavFragmentEvent(BaseFragment fragment) {
        this.fragment = fragment;
    }

    public NavFragmentEvent(BaseFragment fragment, Bundle bundle) {
        this.fragment = fragment;
        this.bundle = bundle;
    }
}
