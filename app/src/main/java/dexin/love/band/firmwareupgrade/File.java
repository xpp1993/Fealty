package dexin.love.band.firmwareupgrade;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

//import com.dialog.suota.bluetooth.SpotaManager;
//import com.dialog.suota.bluetooth.SuotaManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static dexin.love.band.manager.ParameterManager.filesDir;

/**
 * Created by wouter on 9-10-14.
 */
public class File {
    /**
     * 创建文件
     */

    public static void createFileDirectories() {
        String directoryName = filesDir;
        java.io.File directory;
        directory = new java.io.File(directoryName);
        if (!directory.exists() && directory.isDirectory())
            directory.mkdirs();
        else
            System.out.println(filesDir+"文件存在");
    }
}
