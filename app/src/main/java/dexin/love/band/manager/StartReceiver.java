package dexin.love.band.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import dexin.love.band.MainActivity;

/**
 * Created by Administrator on 2016/11/18.
 */
public class StartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase("install_and_start")) {
            Intent intent2 = new Intent(context, MainActivity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//注意,不能少
            context.startActivity(intent2);
        }
    }
}
