package com.zzw.guanglan.manager;

import com.zzw.guanglan.bean.UserBean;

public class UserManager {

    private UserBean userBean;

    private UserManager() {
    }

    public static UserManager getInstance() {
        return SingletonHolder.mInstance;
    }

    public static class SingletonHolder {
        private static volatile UserManager mInstance = new UserManager();
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public String getUserId() {
        if(userBean == null) return null;
        return userBean.getUserId();
    }

    public String getAreaId() {
        if(userBean == null) return null;
        return userBean.getAreaId();
    }

}
