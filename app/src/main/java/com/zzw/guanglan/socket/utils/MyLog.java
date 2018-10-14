package com.zzw.guanglan.socket.utils;

import timber.log.Timber;

public class MyLog {
    public static void e(String msg) {
        e("MyLog", msg);
    }

    public static void e(String tag, String msg) {
        Timber.tag(tag).e(msg);
    }
}
