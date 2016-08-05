package com.lxkj.administrator.fealty.fragment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.BaseFragment;
import com.lxkj.administrator.fealty.event.NavFragmentEvent;
import com.lxkj.administrator.fealty.manager.DecodeManager;
import com.lxkj.administrator.fealty.manager.ParameterManager;
import com.lxkj.administrator.fealty.manager.SessionHolder;
import com.lxkj.administrator.fealty.utils.AppUtils;
import com.lxkj.administrator.fealty.utils.CommonTools;
import com.lxkj.administrator.fealty.utils.NetWorkAccessTools;
import com.lxkj.administrator.fealty.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.Map;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/7/26.
 */
@ContentView(R.layout.fragement_me)
public class MeFragment extends BaseFragment implements View.OnClickListener, NetWorkAccessTools.RequestTaskListener {
    @ViewInject(R.id.me_iv_left)
    private ImageView me_iv_left;
    @ViewInject(R.id.me_headpic)
    private CircleImageView circleImageView;
    @ViewInject(R.id.me_username)
    private TextView me_username;
    @ViewInject(R.id.me_phone)
    private TextView me_phone;
    public static final int REQUEST_USER_BYMIBILE=0x01;
    private Handler handler=new MyHandler();
    @Override
    protected void init() {
        //初始化个人资料显示
        initPersonalDataShow();
        Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile"}, SessionHolder.user.getMobile());
        NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.GET_USER_BYMOBILE, params, null, MeFragment.REQUEST_USER_BYMIBILE, this);
    }
    @Override
    protected void initListener() {
        me_iv_left.setOnClickListener(this);
    }
    @Override
    protected void initData() {

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.me_iv_left:
                EventBus.getDefault().post(new NavFragmentEvent(new MeSettingFragment()));
                break;
        }
    }
    @Override
    public void onRequestStart(int requestCode) {

    }

    @Override
    public void onRequestLoading(int requestCode, long current, long count) {

    }

    @Override
    public void onRequestSuccess(JSONObject jsonObject, int requestCode) {
        switch (requestCode){
            case REQUEST_USER_BYMIBILE:
                try {
                    DecodeManager.decodeSelfDataQuery(jsonObject,REQUEST_USER_BYMIBILE,handler);
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.showToastInUIThread("服务器异常！");
                }
                break;
        }

    }

    @Override
    public void onRequestFail(int requestCode, int errorNo) {
        ToastUtils.showToastInUIThread("网络连接错误，请检查重试！");

    }

    /**
     * 初始化个人资料显示
     */
    private void initPersonalDataShow() {
        if (TextUtils.isEmpty(SessionHolder.user.getUserpic())||"".equals(SessionHolder.user.getUserpic())) {
            circleImageView.setImageResource(R.mipmap.unknow_head);
        } else {
            NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).toLoadImage(ParameterManager.GET_USER_BYMOBILE+"/"+SessionHolder.user.getUserpic(), circleImageView, R.mipmap.unknow_head, R.mipmap.unknow_head);
        }
        me_username.setText(TextUtils.isEmpty(SessionHolder.user.getNickName()) ? "未设置" : SessionHolder.user.getNickName());
        me_phone.setText(TextUtils.isEmpty(SessionHolder.user.getMobile()) ? "未设置" : "手机号:"+SessionHolder.user.getMobile());
    }
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REQUEST_USER_BYMIBILE://个人信息查询
                    if (msg.getData().getInt("code") == 1) {//请求成功
                        String mobile = msg.getData().getString("mobile");
                        String nickname = msg.getData().getString("nickName");
                       String userpic = msg.getData().getString("headFile");
                        String sex=msg.getData().getString("sex");
                        String birthday=msg.getData().getString("birthday");
                        SessionHolder.user.setMobile(mobile);
                        SessionHolder.user.setNickName(nickname);
                        SessionHolder.user.setUserpic(userpic);
                        SessionHolder.user.setGender(sex);
                        SessionHolder.user.setBirthday(birthday);
                    } else {//请求失败
                        ToastUtils.showToastInUIThread(msg.getData().getString("desc"));
                        Log.w("service error", msg.getData().getString("desc"));
                    }
                    initPersonalDataShow();
                    break;
            }}}
}
