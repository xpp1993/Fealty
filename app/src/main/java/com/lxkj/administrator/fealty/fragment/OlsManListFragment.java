package com.lxkj.administrator.fealty.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.adapter.OldmanListviewAdpter;
import com.lxkj.administrator.fealty.base.BaseFragment;
import com.lxkj.administrator.fealty.bean.UserInfo;
import com.lxkj.administrator.fealty.manager.DecodeManager;
import com.lxkj.administrator.fealty.manager.ParameterManager;
import com.lxkj.administrator.fealty.manager.SessionHolder;
import com.lxkj.administrator.fealty.ui.picker.OptionPicker;
import com.lxkj.administrator.fealty.utils.AppUtils;
import com.lxkj.administrator.fealty.utils.CommonTools;
import com.lxkj.administrator.fealty.utils.NetWorkAccessTools;
import com.lxkj.administrator.fealty.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/26.
 */
@ContentView(R.layout.fragement_oldmanlist)
public class OlsManListFragment extends BaseFragment implements AdapterView.OnItemClickListener, NetWorkAccessTools.RequestTaskListener {
    @ViewInject(R.id.oldmanlistview)
    private ListView oldmanlistview;
    ArrayList<UserInfo> list_user;
    private OldmanListviewAdpter adpter;
    String identity;
    private MyHandler myHandler = new MyHandler();
    private static final int REQUEST_CODE_BIND_OTHERS = 0x11;

    @Override
    protected void init() {


    }

    @Override
    protected void initListener() {
        oldmanlistview.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UserInfo userInfo = list_user.get(position);
        final String oldmobile = userInfo.getMobile();

        //点击弹出身份选择框
        OptionPicker picker = new OptionPicker(getActivity(), new String[]{
                "爸爸", "妈妈", "爷爷", "奶奶", "姥爷", "姥姥", "叔叔", "阿姨", "姑姑", "大伯", "婶婶", "姐姐", "哥哥"
        });
        picker.setSelectedIndex(1);
        picker.setTopBackgroundColor(0xFFEEEEEE);
        picker.setTopLineColor(0xFF33B5E5);
        picker.setCancelTextColor(0xFF33B5E5);
        picker.setSubmitTextColor(0xFF33B5E5);
        picker.setTextColor(0xFF33B5E5, 0xFF999999);
        picker.setLineColor(0xFF33B5E5);
        picker.setAnimationStyle(R.style.Animation_Popup);
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int position, String option) {
                Toast.makeText(getActivity(), option, Toast.LENGTH_LONG).show();
                identity = option;
                Map<String, String> params = CommonTools.getParameterMap(new String[]{"old_people_mobile", "mobile", "identity"}, oldmobile, SessionHolder.mobile, identity);
                NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.SELECT_BIND_OLD, params, null, REQUEST_CODE_BIND_OTHERS, OlsManListFragment.this);
            }
        });
        picker.show();
    }
    @Override
    public void onGetBunndle(Bundle arguments) {
        super.onGetBunndle(arguments);
        list_user = (ArrayList<UserInfo>) arguments.getSerializable("old_people_list");
        adpter = new OldmanListviewAdpter(list_user);
        oldmanlistview.setAdapter(adpter);
    }

    @Override
    public void onRequestStart(int requestCode) {

    }

    @Override
    public void onRequestLoading(int requestCode, long current, long count) {

    }

    @Override
    public void onRequestSuccess(JSONObject jsonObject, int requestCode) {
        switch (requestCode) {
            case REQUEST_CODE_BIND_OTHERS:
                try {
                    DecodeManager.decodeCommon(jsonObject, requestCode, myHandler);
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.showToastInUIThread("服务器返回结果错误");
                }
                break;
        }
    }

    @Override
    public void onRequestFail(int requestCode, int errorNo) {

    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            Bundle bundle = msg.getData();
            switch (what) {
                case REQUEST_CODE_BIND_OTHERS:
                    String desc = bundle.getString("desc");
                    ToastUtils.showToastInUIThread(desc);
                    break;

            }
        }

    }
}
