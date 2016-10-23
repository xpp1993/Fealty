package dexin.love.band.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import dexin.love.band.utils.ContextUtils;
import dexin.love.band.utils.NetWorkAccessTools;
import dexin.love.band.utils.ToastUtils;

/**
 * Created by Administrator on 2016/9/5.
 * 查看绑定的用户详情
 */
@ContentView(R.layout.fragement_userdetail)
public class UserDetailInfo extends BaseFragment implements View.OnClickListener, NetWorkAccessTools.RequestTaskListener, AdapterView.OnItemClickListener {
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
    public static final int REQUEST_CODE_REJECT_BINDED = 2;
    private MyHandler myHandler;
    private ArrayList<UserInfo> list;
    public static final String TAG = UserDetailInfo.class.getSimpleName();
    UserInfo userInfo;
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
       userInfo = ContextUtils.getObjFromSp(AppUtils.getBaseContext(), "userInfo");
        //   Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile"}, SessionHolder.user.getMobile());
        Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile"}, userInfo.getMobile());
        NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.SELECT_USER_BINDED, params, null, REQUEST_CODE_USERINFO_BINDED, this);
    }

    @Override
    protected void initListener() {
        bar_back.setOnClickListener(this);
        listView.setOnItemClickListener(this);
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
                //getActivity().onBackPressed();
                getActivity().getSupportFragmentManager().popBackStack(null, 0);
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
                case REQUEST_CODE_REJECT_BINDED://网络请求解除绑定用户
                    DecodeManager.decodeCommon(jsonObject, requestCode, myHandler);
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

    /**
     * 点击取消绑定用户
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final UserInfo userInfop = (UserInfo) adapter.getItem(position);
        Log.d(TAG,userInfop.getMobile());
        //弹框，取消绑定该用户，你将不能监控到该用户的健康数据，是否取消绑定该用户？
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("解除绑定");
        builder.setMessage("您将不能监控该用户的健康数据，是否解除绑定？");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Map<String, String> params = CommonTools.getParameterMap(new String[]{"childrenPhone", "parentPhone"}, userInfo.getMobile(), userInfop.getMobile());
                NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.HOST+ParameterManager.REJECT_BINDED, params, null, REQUEST_CODE_REJECT_BINDED, UserDetailInfo.this);
            }
        });
        builder.setNegativeButton("否", null).create().show();
//        builder.setCancelable(false).create().show();
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
                case REQUEST_CODE_REJECT_BINDED:
                    if (data.getInt("code") == 1) {//解除绑定成功
                       ToastUtils.showToastInUIThread("解除绑定成功！");
                        //发送广播
                        AppUtils.getBaseContext().sendBroadcast(new Intent(TAG));
                        adapter.clear();
                        //再请求一次数据
                        Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile"}, userInfo.getMobile());
                        NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.SELECT_USER_BINDED, params, null, REQUEST_CODE_USERINFO_BINDED, UserDetailInfo.this);
                    }else{
                        ToastUtils.showToastInUIThread("解除绑定失败！");
                    }
                    break;
                default:
                    break;
            }
        }
    }
}