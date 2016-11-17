package dexin.love.band.manager;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import java.io.File;
import java.util.Map;
import dexin.love.band.utils.CommonTools;

/**
 * Created by Administrator on 2016/11/14.
 */
public class PlayerService extends AsyncTask<Void, Void, Void> {
    Map<String, Object> requestParamsMap;
    File file;
    Context mContext;

    public PlayerService(Context context, Map<String, Object> requestParamsMap) {
        this.requestParamsMap = requestParamsMap;
        this.file = new File(Environment.getExternalStorageDirectory(), "tts.audio");
        this.mContext = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        boolean isDowned = CommonTools.postDownTTS(ParameterManager.TTS, requestParamsMap, file);
        if (isDowned) {
            MediaPlayer player = MediaPlayer.create(mContext, Uri.fromFile(file));
            player.setLooping(false);//循环么
            player.start();
        }
        return null;
    }
}
