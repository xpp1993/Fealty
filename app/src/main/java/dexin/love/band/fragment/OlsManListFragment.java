package dexin.love.band.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Map;

import de.greenrobot.event.EventBus;
import dexin.love.band.R;
import dexin.love.band.adapter.OldmanListviewAdpter;
import dexin.love.band.base.BaseFragment;
import dexin.love.band.bean.UserInfo;
import dexin.love.band.manager.DecodeManager;
import dexin.love.band.manager.ParameterManager;
import dexin.love.band.manager.SessionHolder;
import dexin.love.band.ui.picker.CustomHeaderAndFooterPicker;
import dexin.love.band.ui.picker.OptionPicker;
import dexin.love.band.utils.AppUtils;
import dexin.love.band.utils.CommonTools;
import dexin.love.band.utils.FormatCheck;
import dexin.love.band.utils.NetWorkAccessTools;
import dexin.love.band.utils.ToastUtils;

/**
 * Created by Administrator on 2016/7/26.
 */
@ContentView(R.layout.fragement_oldmanlist)
public class OlsManListFragment extends BaseFragment implements AdapterView.OnItemClickListener, NetWorkAccessTools.RequestTaskListener, View.OnClickListener {
    @ViewInject(R.id.bar_iv_left)
    private ImageView bar_back;
    @ViewInject(R.id.bar_tv_title_left)
    private TextView bar_biaoti;
    @ViewInject(R.id.bar_view_left_line)
    private ImageView bar_view_left_line;
    @ViewInject(R.id.oldmanlistview)
    private ListView oldmanlistview;
    ArrayList<UserInfo> list_user;
    private OldmanListviewAdpter adpter;
    String identity;
    private MyHandler myHandler = new MyHandler();
    private static final int REQUEST_CODE_BIND_OTHERS = 0x11;

    @Override
    protected void init() {
        bar_back.setVisibility(View.VISIBLE);
        bar_view_left_line.setVisibility(View.VISIBLE);
        bar_biaoti.setVisibility(View.VISIBLE);
        bar_biaoti.setText("该用户已入住德信孝心手环app，请点击绑定");
    }

    @Override
    protected void initListener() {
        oldmanlistview.setOnItemClickListener(this);
        bar_back.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UserInfo userInfo = list_user.get(position);
        String oldmobile = userInfo.getMobile();
        showSetIdentityDialog(oldmobile);

    }

    private void selectedIdentity(final String oldmobile) {
        //点击弹出身份选择框
        CustomHeaderAndFooterPicker picker = new CustomHeaderAndFooterPicker(getActivity(), new String[]{
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
                //Toast.makeText(getActivity(), option, Toast.LENGTH_LONG).show();
                Map<String, String> params = CommonTools.getParameterMap(new String[]{"old_people_mobile", "mobile", "identity"}, oldmobile, SessionHolder.user.getMobile(), option);
                NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.SELECT_BIND_OLD, params, null, REQUEST_CODE_BIND_OTHERS, OlsManListFragment.this);
            }
        });
        picker.show();
    }

    private void showSetIdentityDialog(final String oldmobile) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("确认身份");
        final View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_radiogroup_identity, null);
        final RadioButton readRadioButton = (RadioButton) inflate.findViewById(R.id.dialog_radiogroup_selected);
        RadioButton inRadioButton = (RadioButton) inflate.findViewById(R.id.dialog_radiogroup_print);
        builder.setView(inflate);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                String newGender = readRadioButton.isChecked() ? "选择身份" : "输入用户身份";
                if (TextUtils.equals(newGender, "输入用户身份")) {
                    showSetIdentity(oldmobile);
                } else if (TextUtils.equals(newGender, "选择身份")) {
                    selectedIdentity(oldmobile);
                }
            }
        });
        builder.setNegativeButton("放弃", null);
        builder.setCancelable(false).create().show();
    }

    private void showSetIdentity(final String oldmobile) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("输入用户身份");
        View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_textview_nickname, null);
        final EditText editText = (EditText) inflate.findViewById(R.id.dialog_textview_tv);
        editText.setSingleLine();
        builder.setView(inflate);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                String identity = editText.getText().toString().trim();
                ArrayList list = new ArrayList();
                if (TextUtils.isEmpty(identity)) {
                    ToastUtils.showToastInUIThread("输入不能为空");
                } else if (identity.length() > 3) {
                    ToastUtils.showToastInUIThread("格式错误,重写填写!");
                } else {
                    Map<String, String> params = CommonTools.getParameterMap(new String[]{"old_people_mobile", "mobile", "identity"}, oldmobile, SessionHolder.user.getMobile(), identity);
                    NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.SELECT_BIND_OLD, params, null, REQUEST_CODE_BIND_OTHERS, OlsManListFragment.this);
                }
            }
        });
        builder.setNegativeButton("放弃", null);
        builder.setCancelable(false).create().show();
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
        ToastUtils.showToastInUIThread("网络错误");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bar_iv_left:
                //  getActivity().onBackPressed();
                getActivity().getSupportFragmentManager().popBackStack(null, 0);
                break;
            default:
                break;

        }
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
