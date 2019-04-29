package com.zzw.guanglan.bean;

public class UserBean extends ResultBean{

    private String userId;
    private String areaId;


    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
