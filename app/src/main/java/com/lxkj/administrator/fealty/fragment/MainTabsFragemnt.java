package com.lxkj.administrator.fealty.fragment;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.BaseFragment;
import com.lxkj.administrator.fealty.widget.QuickFragmentTabHost;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/26.
 */
@ContentView(R.layout.fragment_main_tab)
public class MainTabsFragemnt extends BaseFragment {
    @ViewInject(android.R.id.tabhost)
    private QuickFragmentTabHost mTabsHost;
    private final String[] TITLES = {"状态", "老人", "我的"};
    private final String[] TAGS = {"status", "oldman", "me"};
    private int[] ICONS = {R.drawable.tab_status, R.drawable.tab_status,R.drawable.tab_status};
    private final Class[] fragments = {StatusFragment.class, OldManListFragment.class, MeFragment.class};
    private List<ViewHolder> viewHolders = new ArrayList<ViewHolder>();

    @Override
    protected void init() {
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
