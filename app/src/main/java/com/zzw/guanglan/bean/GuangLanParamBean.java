package com.zzw.guanglan.bean;

import java.io.Serializable;

/**
 * Created by zzw on 2018/10/14.
 * 描述:
 */
public class GuangLanParamBean implements Serializable{
    private String NAME;
    private String ID;




    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
