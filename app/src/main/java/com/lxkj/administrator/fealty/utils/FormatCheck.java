package com.lxkj.administrator.fealty.utils;

/**
 * Created by Administrator on 2016/8/1.
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 格式校验工具类
 * @author Carl
 *
 */
public class FormatCheck {

    /**
     * 手机号码格式校验
     * @param phone
     * @return true:合法 false:非法
     */
    public static boolean isMobile(String phone){
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,8,7][0-9]{9}$"); // 验证手机号
        m = p.matcher(phone);
        b = m.matches();
        return b;
    }
    /**
     * 匹配非负浮点数（正浮点数 + 0）
     * @param decimal
     * @return true:合法 false:非法
     */
    public static boolean isDecimal(String decimal){
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^\\d+(\\.\\d+)?$"); //
        m = p.matcher(decimal);
        b = m.matches();
        return b;
    }
    /**
     * 匹配正浮点数
     * @param decimal
     * @return true:合法 false:非法
     */
    public static boolean isDecimal2(String decimal){
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^((\\d+\\.\\d*[1-9]\\d*)|(\\d*[1-9]\\d*\\.\\d+)|(\\d*[1-9]\\d*))$"); //
        m = p.matcher(decimal);
        b = m.matches();
        return b;
    }
    /**
     * 匹配非负整数（正整数 + 0）
     * @param string
     * @return
     */
    public static boolean isPositiveInteger(String string){
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^\\d+$"); //
        m = p.matcher(string);
        b = m.matches();
        return b;
    }
}

