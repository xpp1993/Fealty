package com.lxkj.administrator.fealty.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.lxkj.administrator.fealty.bean.UserInfo;
import com.lxkj.administrator.fealty.fragment.HealthDataFragement;
import com.lxkj.administrator.fealty.manager.SessionHolder;
import com.lxkj.administrator.fealty.utils.AppUtils;
import com.lxkj.administrator.fealty.utils.ContextUtils;
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
  //  private BackData backData;
//    public HeathMonitoringAdapter(FragmentManager fm, JazzyViewPager mJazzy, List<HealthDataFragement> fragments,BackData backData) {
//        super(fm);
//        this.fragments = fragments;
//        this.mJazzy = mJazzy;
//        this.backData=backData;
//       // notifyDataSetChanged();
//    }
    public HeathMonitoringAdapter(FragmentManager fm, JazzyViewPager mJazzy, List<HealthDataFragement> fragments) {
        super(fm);
        this.fragments = fragments;
        this.mJazzy = mJazzy;
        // notifyDataSetChanged();
    }
    @Override
    public Fragment getItem(int position) {
    return fragments.get(position);
     //   return  Fragment.instantiate(AppUtils.getBaseContext(),fragments.get(position).getClass().getName());
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
//    public void addFragment(HealthDataFragement fragment){
//        for(HealthDataFragement healthDataFragement:fragments){
//            if(healthDataFragement.getArguments().getString("parentPhone").equals(fragment.getArguments().getString("parentPhone"))){
//              if (backData!=null){
//                  backData.callYou(healthDataFragement);
//              }
//                return ;
//            }
//
//        }
//        if(TextUtils.equals(fragment.getArguments().getString("parentPhone"), SessionHolder.user.getMobile())){
//            fragments.add(0,fragment);
//        }else{
//            fragments.add(fragment);
//        }
//    }
    public HealthDataFragement addFragment(HealthDataFragement fragment){
        for(HealthDataFragement healthDataFragement:fragments){
            if(healthDataFragement.getArguments().getString("parentPhone").equals(fragment.getArguments().getString("parentPhone")))
                return healthDataFragement;
        }
        UserInfo userInfo= ContextUtils.getObjFromSp(AppUtils.getBaseContext(),"userInfo");
      //  if(TextUtils.equals(fragment.getArguments().getString("parentPhone"), SessionHolder.user.getMobile())){
        if(TextUtils.equals(fragment.getArguments().getString("parentPhone"), userInfo.getMobile())){
            fragments.add(0,fragment);
        }else{
            fragments.add(fragment);
        }
        return fragment;
    }

//    //2016-8-24 xpp add
//    public interface BackData {
//        void callYou(HealthDataFragement healthDataFragement);
//    }
}
