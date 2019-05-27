package com.zzw.guanglan.bean;

public class UserBean extends ResultBean{

    private String userId;
    private String areaId;
    private String staffNbr;


    public String getStaffNbr() {
        return staffNbr;
    }

    public void setStaffNbr(String staffNbr) {
        this.staffNbr = staffNbr;
    }

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
