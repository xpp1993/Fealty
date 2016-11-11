package dexin.love.band.view;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import dexin.love.band.R;
import dexin.love.band.base.BaseView;

/**
 * Created by XPP on 2016/9/4/004.
 */
public class DialogViewKeyAlarm extends BaseView {
    private Button button;
    private TextView berryWarn;

    public DialogViewKeyAlarm(Context context) {
        super(context);
    }

    public Button getButton() {
        return button;
    }

    public void setText(String text) {
        berryWarn.setText(text);
    }

    @Override
    protected void initView() {
        View view = View.inflate(getContext(), R.layout.dialog_akeyalarm, this);
        button = (Button) view.findViewById(R.id.dialogbtn);
        berryWarn = (TextView) view.findViewById(R.id.bettrywarnso);
    }
}
