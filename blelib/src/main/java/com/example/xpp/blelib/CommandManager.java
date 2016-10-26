package com.example.xpp.blelib;

import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommandManager {
    private static final String TAG = CommandManager.class.getSimpleName();
    static int lightSleepTime = 0;//浅睡时间
    static int deepSleepTime = 0;//深度睡眠时间

    public static void decode(Context context, byte[] data) {
        String dataString = Utils.bytesToHexString(data);
        if (dataString.startsWith(GlobalValues.BLE_COMMAND_TYPE_CODE_ELECTRICITY)) {//获取手环电量
            String electricity = String.valueOf(Integer.parseInt(dataString.substring(2, 4), 16));
            String isElectricize = dataString.substring(4, 6);//是否充电，00为没有充电，01为充电
            BroadcastManager.sendBroadcast4Electricity(context, GlobalValues.BROADCAST_INTENT_ELECTRICITY, electricity, isElectricize);
        } else if (dataString.startsWith(GlobalValues.BLE_COMMAND_TYPE_CODE_CURRENTMOTION)) {//获取手环运动数据
            String datetime = dataString.substring(8, 10) + dataString.substring(6, 8) + dataString.substring(4, 6) + dataString.substring(2, 4);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
            Long time = (Long.parseLong(datetime, 16) - 8 * 3600) * 1000;
            String timeStr = sdf.format(new Date(time));//当前手环时间
            Log.e("BleEngine获取手环当前运动数据", timeStr);
            String steps = dataString.substring(16, 18) + dataString.substring(14, 16) + dataString.substring(12, 14) + dataString.substring(10, 12);
            String distance = dataString.substring(24, 26) + dataString.substring(22, 24) + dataString.substring(20, 22) + dataString.substring(18, 20);
            String calo = dataString.substring(32, 34) + dataString.substring(30, 32) + dataString.substring(28, 30) + dataString.substring(26, 28);
            String sleep = "00" + dataString.substring(38, 40) + dataString.substring(36, 38) + dataString.substring(34, 36);
            BroadcastManager.sendBroadcast4CURRENTMOTION(
                    context, GlobalValues.BROADCAST_INTENT_CURRENTMOTION,
                    String.valueOf(Integer.parseInt(datetime, 16)),
                    String.valueOf(Integer.parseInt(steps, 16)),
                    Integer.parseInt(distance, 16),
                    Integer.parseInt(calo, 16),
                    Integer.parseInt(sleep, 16));
        } else if (dataString.startsWith(GlobalValues.BLE_COMMAND_TYPE_CODE_SYNSLEEP_BACK)) {//返回睡眠质量数据
           // Log.e("xpp", "同步睡眠数据完成:" + dataString.toString());
            String datetime = dataString.substring(8, 10) + dataString.substring(6, 8) + dataString.substring(4, 6) + dataString.substring(2, 4);
//            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
            Long time = (Long.parseLong(datetime, 16) - 8 * 3600) * 1000;
            String timeStr = sdf.format(new Date(time));
            Log.e("BleEngine同步睡眠数据", timeStr);
            for (int i = 10; i < 40; i += 2) {
                //将十六进制字符转化为二进制字符
                String BinaryString = Utils.toFullBinaryString(Integer.parseInt(dataString.substring(i, i + 2), 16));
                int sleepData = Integer.parseInt(BinaryString.substring(24, 26), 2);//睡眠质量数据
               // Log.e(TAG, sleepData + "");
                // 睡眠质量数据如果为 0 表明没有记录或没有睡眠，有记录时的取值范围为（ 1-3）。 3 为最好的睡眠质量， 1 为最差的睡眠质
                if (sleepData == 0)
                    return;
                if (sleepData == 1) {
                    //浅睡状态
                    lightSleepTime += 1;
                } else if (sleepData == 2 || sleepData == 3) {
                    //深度睡眠状态
                    deepSleepTime += 1;
                }
            }
            // Log.e(TAG, "sleepLight" + lightSleepTime + ",sleepdeep" + deepSleepTime);
            // }
            // if (timeStr.equals("11:45:00")) {//发送广播通知前段显示数据
            BroadcastManager.sendBroadcast4SleepQuality(context, GlobalValues.BROADCAST_INTENT_SLEEPQ, lightSleepTime, deepSleepTime);
            // }
        } else if (dataString.startsWith(GlobalValues.BLE_COMMAND_TYPE_CODE_SYNSLEEPNO)) {
            Log.e(TAG, "没有睡眠数据！");
        } else if (dataString.startsWith(GlobalValues.BLE_COMMAND_TYPE_CODE_RATESTART)) {//心率测试回复数据
            String datetime = dataString.substring(8, 10) + dataString.substring(6, 8) + dataString.substring(4, 6) + dataString.substring(2, 4);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Long time = (Long.parseLong(datetime, 16) - 8 * 3600) * 1000;
            String timeStr = sdf.format(new Date(time));//当前手环时间
            //  Log.e(TAG, timeStr);
            int rate = Integer.valueOf(dataString.substring(10, 12), 16);//当前心率值
            int status = Integer.valueOf(dataString.substring(12, 14), 16);//当前状态，0x00:不是睡眠状态，0x01,当前睡眠状态最差，0x03:当前睡眠状态最好
            //发送广播给前端
            BroadcastManager.sendBroadcast4RateData(context, GlobalValues.BROADCAST_INTENT_RATE, timeStr, rate, status);
        } else if (dataString.startsWith(GlobalValues.BLE_COMMAND_TYPE_CODE_A_KEY_ALARM)) {//一键报警信息
            //发送一键报警广播给前端
            BroadcastManager.sendBroadcast4AKEYALARM(context, GlobalValues.BROADCAST_INTENT_A_KEY_ALARM);
        } else if (dataString.startsWith(GlobalValues.BLE_COMMAND_TYPE_CODE_VERSIONMAC)) {//获取手环信息系和MAC地址
            Log.e("MAC", dataString);
            String version = "V" + Integer.parseInt(dataString.substring(4, 6) + dataString.substring(2, 4), 16);
            String MAC = dataString.substring(6, 8) + ":" + dataString.substring(8, 10) + ":" + dataString.substring(10, 12) + ":" + dataString.substring(12, 14);
            String status = dataString.substring(14, 16);
            BroadcastManager.sendBroadcast4BandInfo(context, GlobalValues.BROADCAST_INTENT_BAND_INFO, version, MAC, status);
        } else if (dataString.startsWith(GlobalValues.BLE_COMMAND_TYPE_CODE_TIME)) {
            String datetime = dataString.substring(8, 10) + dataString.substring(6, 8) + dataString.substring(4, 6) + dataString.substring(2, 4);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");
            Long time = (Long.parseLong(datetime, 16) - 8 * 3600) * 1000;
            String timeStr = sdf.format(new Date(time));//当前手环时间
            Log.e(TAG, timeStr);
        } else if (dataString.startsWith(GlobalValues.BLE_COMMAND_TYPE_CODE_RATESTOP)) {//心率停止回复
            BroadcastManager.sendBroadcast4StopRate(context, GlobalValues.BROADCAST_INTENT_STOPRATE);
        }
    }


    private static synchronized void send(final BleEngine bleEngine, final byte[] data) {
        Log.e(TAG, "sendCommand -> " + Utils.bytesToHexString(data));
        bleEngine.writeCharacteristic(data);
    }

    /***
     * 根据时间类型比较时间大小
     *
     * @param source
     * @param traget
     * @param type   "YYYY-MM-DD" "yyyyMMdd HH:mm:ss"  类型可自定义
     * @return 0 ：source和traget时间相同
     * 1 ：source比traget时间大
     * -1：source比traget时间小
     * @throws Exception
     */
    public static int DateCompare(String source, String traget, String type) {
        int ret = 2;
        SimpleDateFormat format = new SimpleDateFormat(type);
        Date sourcedate = null;
        try {
            sourcedate = format.parse(source);
            Date tragetdate = format.parse(traget);
            ret = sourcedate.compareTo(tragetdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ret;
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
     * 发送指令给手环，读取手环信息
     *
     * @param bleEngine
     */
    public static void sendGetVersionandMac(BleEngine bleEngine) {
        send(bleEngine, Utils.hexStringToBytes(GlobalValues.BLE_COMMAND_TYPE_CODE_VERSIONMAC));
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

    /**
     * 发送指令给手环，获取某天睡眠质量数据
     *
     * @param bleEngine
     * @param count
     */
    public static void sendSynSleep(BleEngine bleEngine, int count) {
        send(bleEngine, Utils.hexStringToBytes(GlobalValues.BLE_COMMAND_TYPE_CODE_SYNSLEEP +
                Utils.bytesToHexString(new byte[]{Byte.parseByte(Integer.toHexString(count), 16)})));
    }

    /**
     * 发送指令给手环，心率测试开始
     *
     * @param bleEngine
     * @param testTime
     */
    public static void sendStartRate(BleEngine bleEngine, String testTime) {
        send(bleEngine, Utils.hexStringToBytes(GlobalValues.BLE_COMMAND_TYPE_CODE_RATESTART + testTime));
    }

    /**
     * 发送指令给手环，进入固件升级模式
     *
     * @param bleEngine
     */
    public static void sendStartFirmWareUpgrade(BleEngine bleEngine) {
        send(bleEngine, Utils.hexStringToBytes(GlobalValues.BLE_COMMAND_TYPE_CODE_FIRMWOREUPGRADE));
    }

    /**
     * 发送指令给手环，心率测试停止
     *
     * @param bleEngine
     */
    public static void sendStopRate(BleEngine bleEngine) {
        send(bleEngine, Utils.hexStringToBytes(GlobalValues.BLE_COMMAND_TYPE_CODE_RATESTOP));
    }
}
