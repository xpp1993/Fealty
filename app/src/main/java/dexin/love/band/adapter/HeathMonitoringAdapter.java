package dexin.love.band.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import dexin.love.band.bean.UserInfo;
import dexin.love.band.fragment.HealthDataFragement;
import dexin.love.band.utils.AppUtils;
import dexin.love.band.utils.ContextUtils;
import dexin.love.band.widget.JazzyViewPager;


/**
 * 健康监测adapter
 * Created by Administrator on 2016/8/3.
 */
public class HeathMonitoringAdapter extends FragmentPagerAdapter {
   //  private List<HealthDataFragement> fragments;
    private CopyOnWriteArrayList<HealthDataFragement> fragments;
    private JazzyViewPager mJazzy;

  //   public HeathMonitoringAdapter(FragmentManager fm, JazzyViewPager mJazzy, List<HealthDataFragement> fragments) {
   public HeathMonitoringAdapter(FragmentManager fm, JazzyViewPager mJazzy, CopyOnWriteArrayList<HealthDataFragement> fragments) {
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

    public HealthDataFragement addFragment(HealthDataFragement fragment) {
        for (HealthDataFragement healthDataFragement : fragments) {
            if (healthDataFragement.getArguments().getString("parentPhone").equals(fragment.getArguments().getString("parentPhone")))
                return healthDataFragement;
        }
        UserInfo userInfo = ContextUtils.getObjFromSp(AppUtils.getBaseContext(), "userInfo");
        //  if(TextUtils.equals(fragment.getArguments().getString("parentPhone"), SessionHolder.user.getMobile())){
        if (TextUtils.equals(fragment.getArguments().getString("parentPhone"), userInfo.getMobile())) {
            fragments.add(0, fragment);
        } else {
            fragments.add(fragment);
        }
        return fragment;
    }

    public void clearFragment() {
        UserInfo userInfo = ContextUtils.getObjFromSp(AppUtils.getBaseContext(), "userInfo");
        for (HealthDataFragement healthDataFragement : fragments) {
            if (!healthDataFragement.getArguments().getString("parentPhone").equals(userInfo.getMobile())) {
                fragments.clear();
            }
        }
    }
}
