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
    private final String[] TITLES = {"健康监测", "我的设置"};
    private final String[] TAGS = {"status", "me"};
    private int[] ICONS = {R.drawable.tab_status, R.drawable.tab_status};
    private final Class[] fragments = {StatusFragment.class, MeFragment.class};
    private List<ViewHolder> viewHolders = new ArrayList<ViewHolder>();

    @Override
    protected void init() {
        //在那个布局上填充的id
        mTabsHost.setup(getContext(), getChildFragmentManager(), R.id.realtabcontent);
        initTabs();
        //当前手机是否打开蓝牙
//        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (bluetoothAdapter==null){
//            ToastUtils.showToastInUIThread("本机没有找到蓝牙硬件或驱动！");
//            finish();
//        }
//        //如果蓝牙没有开启，则开启
//        if (!bluetoothAdapter.isEnabled()){
//            // 我们通过startActivityForResult()方法发起的Intent将会在onActivityResult()回调方法中获取用户的选择，比如用户单击了Yes开启，
//            // 那么将会收到RESULT_OK的结果，
//            // 如果RESULT_CANCELED则代表用户不愿意开启蓝牙
//            Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(mIntent, 1);
//        }
    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // TODO Auto-generated method stub
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1) {
//            if (resultCode ==getActivity(). RESULT_OK) {
//                ToastUtils.showToastInUIThread("蓝牙已经开启");
//            } else if (resultCode == getActivity().RESULT_CANCELED) {
//                ToastUtils.showToastInUIThread("不允许蓝牙开启启");
//                finish();
//            }
//        }
//
//    }
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
//            if (i==1) {
//                //请求个人资料
//
//            }
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
