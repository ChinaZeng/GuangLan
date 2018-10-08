package com.zzw.guanglan.socket.utils;

import android.util.Log;

public class MyLog {
    public static void e(String msg){
        e("zzz",msg);
    }
    public static void e(String tag,String msg){
        Log.e(tag,msg);
    }
}
