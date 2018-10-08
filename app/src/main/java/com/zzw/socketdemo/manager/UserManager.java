package com.zzw.socketdemo.manager;

import com.zzw.socketdemo.utils.SPUtil;

public class UserManager {

    private UserManager() {
    }

    public static UserManager getInstance() {
        return SingletonHolder.mInstance;
    }

    public static class SingletonHolder {
        private static volatile UserManager mInstance = new UserManager();
    }


    public void setUserId(String userId) {
        SPUtil.getInstance().put("userId", userId);
    }

    public String getUserId() {
        return SPUtil.getInstance().getString("userId");
    }

}
