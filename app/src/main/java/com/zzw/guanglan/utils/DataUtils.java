package com.zzw.guanglan.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUtils {

    private final static String PATTERN = "yyyy-MM-dd  HH:mm:ss";

    public static String getNowTime() {
        try {
            SimpleDateFormat format = new SimpleDateFormat(PATTERN);
            return format.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
            return "无";
        }
    }
}
