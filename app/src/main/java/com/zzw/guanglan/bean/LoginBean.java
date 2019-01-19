package com.zzw.guanglan.bean;

import java.io.Serializable;

/**
 * Created by zzw on 2019/1/19.
 * 描述:
 */
public class LoginBean implements Serializable {


    private String staffNbr;
    private String password;

    public String getStaffNbr() {
        return staffNbr;
    }

    public void setStaffNbr(String staffNbr) {
        this.staffNbr = staffNbr;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
