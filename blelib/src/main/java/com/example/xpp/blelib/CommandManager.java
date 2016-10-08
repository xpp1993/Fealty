package com.example.xpp.blelib;

import android.content.Context;
import android.util.Log;


public class CommandManager {
    private static final String TAG = CommandManager.class.getSimpleName();

    public static void decode(Context context, byte[] data) {
        String dataString = Utils.bytesToHexString(data);
        if (dataString.startsWith(GlobalValues.BLE_COMMAND_TYPE_CODE_ELECTRICITY)) {
            String electricity = String.valueOf(Integer.parseInt(dataString.substring(2, 4), 16));
            BroadcastManager.sendBroadcast4Electricity(context, GlobalValues.BROADCAST_INTENT_ELECTRICITY, electricity);
        } else if (dataString.startsWith(GlobalValues.BLE_COMMAND_TYPE_CODE_CURRENTMOTION)) {
            String datetime = dataString.substring(8, 10) + dataString.substring(6, 8) + dataString.substring(4, 6) + dataString.substring(2, 4);
            String steps = dataString.substring(16, 18) + dataString.substring(14, 16) + dataString.substring(12, 14) + dataString.substring(10, 12);
            String distance = dataString.substring(24, 26) + dataString.substring(22, 24) + dataString.substring(20, 22) + dataString.substring(18, 20);
            String calo = dataString.substring(32, 34) + dataString.substring(30, 32) + dataString.substring(28, 30) + dataString.substring(26, 28);
            String sleep = dataString.substring(38, 40) + dataString.substring(36, 38) + dataString.substring(34, 36);
            BroadcastManager.sendBroadcast4CURRENTMOTION(
                    context, GlobalValues.BROADCAST_INTENT_CURRENTMOTION,
                    String.valueOf(Integer.parseInt(datetime, 16)),
                    String.valueOf(Integer.parseInt(steps, 16)),
                    Integer.parseInt(distance, 16),
                    Integer.parseInt(calo, 16),
                    Integer.parseInt(sleep, 16));
        }
    }

    private static synchronized void send(final BleEngine bleEngine, final byte[] data) {
        Log.e(TAG, "sendCommand -> " + Utils.bytesToHexString(data));
        bleEngine.writeCharacteristic(data);
    }

    /**
     * 获得十六进制时间字符串,发个手环做同步时间用
     *
     * @param bleEngine
     */
    public static void sendSynTime(BleEngine bleEngine) {
        long t = System.currentTimeMillis();
        //获取系统当前时间(包含时区)
        long sysTime = t / 1000 + 8 * 60 * 60;
        //   System.err.println("系统当前时间为（带时区）：" + sysTime);
        String hexString = Long.toHexString(sysTime);
        String str1 = hexString.substring(hexString.length() - 2, hexString.length());
        String str2 = hexString.substring(hexString.length() - 4, hexString.length() - 2);
        String str3 = hexString.substring(hexString.length() - 6, hexString.length() - 4);
        String str4 = hexString.substring(hexString.length() - 8, hexString.length() - 6);
        String str5 = GlobalValues.BLE_COMMAND_TYPE_CODE_TIME + str1 + str2 + str3 + str4;
        send(bleEngine, Utils.hexStringToBytes(str5));
    }

    /**
     * 发送指令给手环，获取手环电量
     *
     * @param bleEngine
     */
    public static void sendGetElectricity(BleEngine bleEngine) {
        send(bleEngine, Utils.hexStringToBytes(GlobalValues.BLE_COMMAND_TYPE_CODE_ELECTRICITY));
    }

    /**
     * 发送指令给手环，手环开始振动
     *
     * @param bleEngine
     * @param count     振动次数
     */
    public static void sendVibration(BleEngine bleEngine, int count) {
        send(bleEngine, Utils.hexStringToBytes(GlobalValues.BLE_COMMAND_TYPE_CODE_VIBRATION +
                Utils.bytesToHexString(new byte[]{Byte.parseByte(Integer.toHexString(count), 16)})));
    }
}
