package com.lxkj.administrator.fealty.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import  org.xutils.x;
/**
 * Created by Administrator on 2016/7/26.
 */
public abstract class BaseFragment extends Fragment {
    protected View view;
    private int fId;
    public BaseFragment(){
        fId++;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       // view=initView(inflater,container);//父类必须实现，但不知怎么实现，抽象抽象方法
        //xutils 的注入，写在BaseFragment,以便其子类复用
        view= x.view().inject(this,inflater,container);
        init();
        initListener();
        onGetBunndle(getArguments());
        return view;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();//父类必须实现，但不知怎么实现，抽象抽象方法
    }

     //  protected abstract View initView(LayoutInflater inflater, ViewGroup container);
    // Fragment 初始
    protected abstract void init();
    protected abstract void initListener();
    protected abstract void initData();
    // Fragment 直接bundle 的传递
    public void onGetBunndle(Bundle arguments) {

    }
    // 生成tag的规则：fm.findFragmentByTag(tag);
    public String getMTag(){
        return BaseFragment.class.getName()+fId;
    }

    // 返回Fragment的View
    public View getRootView(){
        return view;
    }

    // 控制Fragment生命
    public boolean finish(){
        return false;
    }

    // 返回键权利交给了Fragment管理
    public boolean onBack(){
        return false;
    }
}
