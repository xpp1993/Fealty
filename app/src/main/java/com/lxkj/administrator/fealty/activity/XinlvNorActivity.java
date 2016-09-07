package com.lxkj.administrator.fealty.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.bean.JpushData;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/9/7.
 */
public class XinlvNorActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jpush_xinlvnor);// 设置布局内容
        //获得json数据
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        //解析json
        JpushData jpushData = parseJson(data);
        //把数据放入控件中
    }
    public JpushData parseJson(String json) {
        JpushData jpushData = null;
        if (json == null) {
            return null;
        }
        try {
            JSONObject object = new JSONObject(json);
            String address = object.optString("address");
            String currentHeart = object.optString("currentHeart");
            String locationdescrible = object.optString("locationdescrible");
            String identity = object.optString("identity");
            jpushData = new JpushData(address, currentHeart, locationdescrible, identity);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jpushData;
    }
}