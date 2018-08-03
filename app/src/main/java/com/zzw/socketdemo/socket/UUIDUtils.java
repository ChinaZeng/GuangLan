package com.zzw.socketdemo.socket;

import java.util.UUID;

public class UUIDUtils {
    public static String creatUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
