package dexin.love.band.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import dexin.love.band.R;
import dexin.love.band.bean.JpushData;

/**
 * Created by Administrator on 2016/9/7.
 */
public class XinlvNorActivity extends Activity {
    private TextView xinlv;
    private TextView dizhi;
    private TextView identity;

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jpush_xinlvnor);// 设置布局内容
        xinlv = (TextView) findViewById(R.id.xinlv);
        dizhi = (TextView) findViewById(R.id.dizhi);
        identity = (TextView) findViewById(R.id.identity);
        button = (Button) findViewById(R.id.dialogbtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //获得json数据
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        //解析json
        JpushData jpushData = parseJson(data);
        //把数据放入控件中
        setData(jpushData);
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

    private void setData(JpushData jpushData) {
        xinlv.setText(jpushData.getCurrentHeart());
        dizhi.setText(jpushData.getAddress());
        identity.setText(jpushData.getIdentity() + "心率异常");
    }
}