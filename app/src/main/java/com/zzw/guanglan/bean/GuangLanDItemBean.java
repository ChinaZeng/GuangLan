package com.zzw.guanglan.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by zzw on 2018/10/3.
 * 描述:
 */
public class GuangLanDItemBean implements Serializable {


    /**
     * STATE_NAME : 空闲
     * SERIAL_NO : 90000012
     * LONGITUDE : 118.760247
     * NAME : 连云港
     * CABLE_NAME : 东海城南-东海综合楼48芯光缆
     * R : 1
     * AHOSTNAME : 东海综合楼
     * ROOM_ID : 763
     * ROOM_NAME : 东海综合楼
     * CAPATICY : 48
     * CABLE_LEVEL : 本地骨干光缆
     * ID : 5270804
     * CABL_OP_NAME : 东海城南-东海综合楼光缆段
     * LATITUDE : 34.52912
     * CABL_OP_CODE : 东海城南-东海综合楼光缆段
     * PA_CABLE_ID : 3280085
     */

    private String STATE_NAME;
    private String SERIAL_NO;
    private String LONGITUDE;
    private String NAME;
    private String CABLE_NAME;
    private String R;
    private String AHOSTNAME;
    private String ROOM_ID;
    private String ROOM_NAME;
    private String CAPATICY;
    private String CABLE_LEVEL;
    private String ID;
    private String CABL_OP_NAME;
    private String LATITUDE;
    private String CABL_OP_CODE;
    private String PA_CABLE_ID;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GuangLanDItemBean that = (GuangLanDItemBean) o;

        return TextUtils.equals(that.ID, ID);
    }

    public String getSTATE_NAME() {
        return STATE_NAME;
    }

    public void setSTATE_NAME(String STATE_NAME) {
        this.STATE_NAME = STATE_NAME;
    }

    public String getSERIAL_NO() {
        return SERIAL_NO;
    }

    public void setSERIAL_NO(String SERIAL_NO) {
        this.SERIAL_NO = SERIAL_NO;
    }

    public String getLONGITUDE() {
        return LONGITUDE;
    }

    public void setLONGITUDE(String LONGITUDE) {
        this.LONGITUDE = LONGITUDE;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getCABLE_NAME() {
        return CABLE_NAME;
    }

    public void setCABLE_NAME(String CABLE_NAME) {
        this.CABLE_NAME = CABLE_NAME;
    }

    public String getR() {
        return R;
    }

    public void setR(String R) {
        this.R = R;
    }

    public String getAHOSTNAME() {
        return AHOSTNAME;
    }

    public void setAHOSTNAME(String AHOSTNAME) {
        this.AHOSTNAME = AHOSTNAME;
    }

    public String getROOM_ID() {
        return ROOM_ID;
    }

    public void setROOM_ID(String ROOM_ID) {
        this.ROOM_ID = ROOM_ID;
    }

    public String getROOM_NAME() {
        return ROOM_NAME;
    }

    public void setROOM_NAME(String ROOM_NAME) {
        this.ROOM_NAME = ROOM_NAME;
    }

    public String getCAPATICY() {
        return CAPATICY;
    }

    public void setCAPATICY(String CAPATICY) {
        this.CAPATICY = CAPATICY;
    }

    public String getCABLE_LEVEL() {
        return CABLE_LEVEL;
    }

    public void setCABLE_LEVEL(String CABLE_LEVEL) {
        this.CABLE_LEVEL = CABLE_LEVEL;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCABL_OP_NAME() {
        return CABL_OP_NAME;
    }

    public void setCABL_OP_NAME(String CABL_OP_NAME) {
        this.CABL_OP_NAME = CABL_OP_NAME;
    }

    public String getLATITUDE() {
        return LATITUDE;
    }

    public void setLATITUDE(String LATITUDE) {
        this.LATITUDE = LATITUDE;
    }

    public String getCABL_OP_CODE() {
        return CABL_OP_CODE;
    }

    public void setCABL_OP_CODE(String CABL_OP_CODE) {
        this.CABL_OP_CODE = CABL_OP_CODE;
    }

    public String getPA_CABLE_ID() {
        return PA_CABLE_ID;
    }

    public void setPA_CABLE_ID(String PA_CABLE_ID) {
        this.PA_CABLE_ID = PA_CABLE_ID;
    }
}
