package dexin.love.band.fragment;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import dexin.love.band.R;
import dexin.love.band.base.BaseFragment;
import dexin.love.band.manager.ParameterManager;
import dexin.love.band.manager.SPManager;
import dexin.love.band.utils.AppUtils;
import dexin.love.band.widget.QuickFragmentTabHost;

/**
 * Created by Administrator on 2016/7/26.
 */
@ContentView(R.layout.fragment_main_tab)
public class MainTabsFragemnt extends BaseFragment {
    @ViewInject(android.R.id.tabhost)
    private QuickFragmentTabHost mTabsHost;
    private final String[] TITLES = {"健康监测", "设置"};
    private final String[] TAGS = {"status", "me"};
    // private int[] ICONS = {R.drawable.tab_status, R.drawable.tab_status};
    private int[] ICONS = {R.mipmap.shuju, R.mipmap.wo};
    private final Class[] fragments = {StatusFragment.class, MeFragment.class};
    private List<ViewHolder> viewHolders = new ArrayList<ViewHolder>();
    private long beforeTime;//程序进入的时刻
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void init() {
        beforeTime = System.currentTimeMillis();
        //1.获得sharedPreference对象,SharedPrefences只能放基础数据类型，不能放自定义数据类型。
        preferences = SPManager.getSharedPreferences(AppUtils.getBaseContext());
        //2. 获得编辑器:当将数据存储到SharedPrefences对象中时，需要获得编辑器。如果取出则不需要。
        editor = preferences.edit();
        editor.putLong(ParameterManager.LOGIN_TIME, beforeTime);
        editor.commit();
        //在那个布局上填充的id
        mTabsHost.setup(getContext(), getChildFragmentManager(), R.id.realtabcontent);
        initTabs();
    }

    //初始化tabs
    private void initTabs() {
        initViewHolder();
    }

    private void initViewHolder() {
        for (int i = 0; i < TITLES.length; i++) {
            View view = View.inflate(getContext(), R.layout.view_tab_icon, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.iconTabTv = (TextView) view.findViewById(R.id.icon_tab_tv);
            //给TextView设置文本
            viewHolder.iconTabTv.setText(TITLES[i]);
            viewHolder.iconTabTv.setTextSize(15);

            //设置tag
            viewHolder.tag = TAGS[i];
            //设置标题
            viewHolder.title = TITLES[i];

            //设置图片
            Drawable drawable = getResources().getDrawable(ICONS[i]);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            viewHolder.iconTabTv.setCompoundDrawables(null, drawable, null, null);
            viewHolders.add(viewHolder);
            mTabsHost.addTab(mTabsHost.newTabSpec(viewHolder.tag).setIndicator(view), fragments[i], null);
        }
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {

    }


    class ViewHolder {
        TextView iconTabTv;
        String title;
        String tag;
    }
}
