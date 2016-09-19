package dexin.love.band.view;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import dexin.love.band.R;
import dexin.love.band.base.BaseView;

/**
 * Created by XPP on 2016/9/4/004.
 */
public class DialogViewSleephigh extends BaseView {
    private Button button;

    public DialogViewSleephigh(Context context) {
        super(context);
    }

    public Button getButton() {
        return button;
    }

    @Override
    protected void initView() {
        View view = View.inflate(getContext(), R.layout.dialog_sleephigh, this);
        button = (Button) view.findViewById(R.id.dialogbtn);
    }
}
