package dexin.love.band.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import dexin.love.band.R;
import dexin.love.band.adapter.UserInfoAdapter;
import dexin.love.band.base.BaseFragment;
import dexin.love.band.bean.UserInfo;
import dexin.love.band.manager.DecodeManager;
import dexin.love.band.manager.ParameterManager;
import dexin.love.band.manager.SessionHolder;
import dexin.love.band.utils.AppUtils;
import dexin.love.band.utils.CommonTools;
import dexin.love.band.utils.NetWorkAccessTools;
import dexin.love.band.utils.ToastUtils;

/**
 * Created by Administrator on 2016/9/5.
 * 查看绑定的用户详情
 */
@ContentView(R.layout.fragement_userdetail)
public class UserDetailInfo extends BaseFragment implements View.OnClickListener, NetWorkAccessTools.RequestTaskListener {
    @ViewInject(R.id.bar_iv_left)
    private ImageView bar_back;
    @ViewInject(R.id.bar_tv_title_left)
    private TextView bar_biaoti;
    @ViewInject(R.id.bar_view_left_line)
    private ImageView bar_view_left_line;
    @ViewInject(R.id.userlistview)
    private ListView listView;
    private UserInfoAdapter adapter;
    public static final int REQUEST_CODE_USERINFO_BINDED = 1;
    private MyHandler myHandler;
    private ArrayList<UserInfo> list;

    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        bar_back.setVisibility(View.VISIBLE);
        bar_view_left_line.setVisibility(View.VISIBLE);
        bar_biaoti.setVisibility(View.VISIBLE);
        bar_biaoti.setText("用户详情");
        myHandler = new MyHandler();
        // list=new ArrayList<UserInfo>();
        adapter = new UserInfoAdapter(AppUtils.getBaseContext());
        listView.setAdapter(adapter);
        //网络请求数据
        Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile"}, SessionHolder.user.getMobile());
        NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.SELECT_USER_BINDED, params, null, REQUEST_CODE_USERINFO_BINDED, this);
    }

    @Override
    protected void initListener() {
        bar_back.setOnClickListener(this);
    }
    // 用EventBus 来导航,订阅者
    public void onEventMainThread(Bundle event) {
    }
    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bar_iv_left:
                getActivity().onBackPressed();
                break;
            default:
                break;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onRequestStart(int requestCode) {

    }

    @Override
    public void onRequestLoading(int requestCode, long current, long count) {

    }

    @Override
    public void onRequestSuccess(JSONObject jsonObject, int requestCode) {
        try {
            switch (requestCode) {
                case REQUEST_CODE_USERINFO_BINDED://网络请求绑定的用户的数据
                    DecodeManager.decodeUserInfoQuery(jsonObject, requestCode, myHandler);
                    break;
                default:
                    break;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            ToastUtils.showToastInUIThread("服务器返回错误");
        }
    }

    @Override
    public void onRequestFail(int requestCode, int errorNo) {
        ToastUtils.showToastInUIThread("服务器返回错误");
    }
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            switch (msg.what) {
                case REQUEST_CODE_USERINFO_BINDED:
                    if (data.getInt("code") == 1) {//获得list，放入adapter
                        list = (ArrayList<UserInfo>) data.getSerializable("userMsg_list");
                        adapter.addData(list);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}