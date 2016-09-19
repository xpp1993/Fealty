package dexin.love.band.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.leaking.slideswitch.SlideSwitch;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import de.greenrobot.event.EventBus;
import dexin.love.band.R;
import dexin.love.band.base.BaseFragment;
import dexin.love.band.manager.ParameterManager;
import dexin.love.band.manager.SPManager;
import dexin.love.band.utils.AppUtils;

/**
 * Created by Administrator on 2016/8/27/0027.
 */
@ContentView(R.layout.shezhi_baojin)
public class fragment_BaojinMode extends BaseFragment implements View.OnClickListener {
    @ViewInject(R.id.bar_iv_left)
    private ImageView bar_back;
    @ViewInject(R.id.bar_tv_title_left)
    private TextView bar_biaoti;
    @ViewInject(R.id.bar_view_left_line)
    private ImageView bar_view_left_line;
    @ViewInject(R.id.voidbaojing)
    private SlideSwitch voice;//设置声音提醒
    @ViewInject(R.id.zdbaojing)
    private SlideSwitch shark;//设置振动提醒
    @ViewInject(R.id.yuyinbaojing)
    private SlideSwitch yuyin;//设置语音提醒
    @ViewInject(R.id.tancbaojing)
    private SlideSwitch dialog;//设置弹窗提醒
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        bar_back.setVisibility(View.VISIBLE);
        bar_view_left_line.setVisibility(View.VISIBLE);
        bar_biaoti.setVisibility(View.VISIBLE);
        bar_biaoti.setText("异常报警");
        //1.获得sharedPreference对象,SharedPrefences只能放基础数据类型，不能放自定义数据类型。
        preferences = SPManager.getSharedPreferences(AppUtils.getBaseContext());
        //2. 获得编辑器:当将数据存储到SharedPrefences对象中时，需要获得编辑器。如果取出则不需要。
        editor = preferences.edit();
    }

    // 用EventBus 来导航,订阅者
    public void onEventMainThread(Bundle event) {
    }

    @Override
    protected void initListener() {
        bar_back.setOnClickListener(this);
        voice.setSlideListener(new SlideSwitch.SlideListener() {
            @Override
            public void open() {//如果声音提醒是打开的。把状态写入sp 文件(写一个静态类表示各个开关状态)
                editor.putBoolean(ParameterManager.BAOJIN_SOUND, true);
                editor.commit();
           //     Toast.makeText(getActivity(), "设置成功", Toast.LENGTH_LONG).show();
                shark.setState(false);
                dialog.setState(false);
                yuyin.setState(false);
            }

            @Override
            public void close() {
                editor.putBoolean(ParameterManager.BAOJIN_SOUND, false);
                editor.commit();
//                Toast.makeText(getActivity(), "取消设置", Toast.LENGTH_SHORT).show();
            }
        });
        shark.setSlideListener(new SlideSwitch.SlideListener() {
            @Override
            public void open() {
                editor.putBoolean(ParameterManager.BAOJIN_zhend, true);
                editor.commit();
             //   Toast.makeText(getActivity(), "设置成功", Toast.LENGTH_LONG).show();
                voice.setState(false);
                dialog.setState(false);
                yuyin.setState(false);
            }

            @Override
            public void close() {
                editor.putBoolean(ParameterManager.BAOJIN_zhend, false);
                editor.commit();
//                Toast.makeText(getActivity(), "取消设置", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.setSlideListener(new SlideSwitch.SlideListener() {
            @Override
            public void open() {
                editor.putBoolean(ParameterManager.BAOJIN_dialog, true);
          //      Toast.makeText(getActivity(), "设置成功", Toast.LENGTH_LONG).show();
                editor.commit();
                voice.setState(false);
                yuyin.setState(false);
                shark.setState(false);
            }

            @Override
            public void close() {
                editor.putBoolean(ParameterManager.BAOJIN_dialog, false);
                editor.commit();
//                Toast.makeText(getActivity(), "取消设置", Toast.LENGTH_SHORT).show();
            }
        });
        yuyin.setSlideListener(new SlideSwitch.SlideListener() {
            @Override
            public void open() {
                editor.putBoolean(ParameterManager.BAOJIN_yuyin, true);
                editor.commit();
                voice.setState(false);
                dialog.setState(false);
                shark.setState(false);
            }

            @Override
            public void close() {
                editor.putBoolean(ParameterManager.BAOJIN_yuyin, false);
                editor.commit();
//                Toast.makeText(getActivity(), "取消设置", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 初始化数据
     */
    @Override
    protected void initData() {
        boolean message_shark = preferences.getBoolean(ParameterManager.BAOJIN_zhend, true);//默认震动提醒
        boolean message_sound = preferences.getBoolean(ParameterManager.BAOJIN_SOUND, false);
        boolean message_dialog = preferences.getBoolean(ParameterManager.BAOJIN_dialog, false);
        boolean message_yuyin = preferences.getBoolean(ParameterManager.BAOJIN_yuyin, false);
        voice.setState(message_sound);
        shark.setState(message_shark);
        yuyin.setState(message_yuyin);
        dialog.setState(message_dialog);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bar_iv_left) {
            getActivity().onBackPressed();
        }
    }
}
