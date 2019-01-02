package com.zzw.guanglan.bean;

import java.io.Serializable;

/**
 * Create by zzw on 2018/12/27
 */
public class GuangLanBean implements Serializable {


    //光缆名称
    private String CABLE_NAME;
    //A端局站名称
    private String AHOSTNAME;
    //Z端经度
    private String ZGEOX;
    //区域
    private String AREA_NAME;
    //地市
    private String CITY_NAME;

    //Z端纬度
    private String ZGEOY;

    //Z端局站名称
    private String ZHOSTNAME;

    //光缆级别
    private String CABLE_TYPE;

    //A端纬度
    private String AGEOY;

    //光缆是否测试过  Y测过,N未测
    private String FLAG;

    //A端经度
    private String AGEOX;

    //光缆ID
    private String CABLE_ID;
    /**
     * STATE_NAME : 空闲
     * CAPACITY : 48
     * CABLE_ID : 47489
     * R : 2
     * ZGEOX : 118.939878
     * ZGEOY : 34.73575
     * ID : 80191
     * AGEOY : 34.762509
     * AGEOX : 118.976775
     */

    private String STATE_NAME;
    private String CAPACITY;

    private String R;
    private String ID;


    public String getCABLE_NAME() {
        return CABLE_NAME;
    }

    public GuangLanBean setCABLE_NAME(String CABLE_NAME) {
        this.CABLE_NAME = CABLE_NAME;
        return this;
    }

    public String getAHOSTNAME() {
        return AHOSTNAME;
    }

    public GuangLanBean setAHOSTNAME(String AHOSTNAME) {
        this.AHOSTNAME = AHOSTNAME;
        return this;
    }

    public String getZGEOX() {
        return ZGEOX;
    }

    public GuangLanBean setZGEOX(String ZGEOX) {
        this.ZGEOX = ZGEOX;
        return this;
    }

    public String getAREA_NAME() {
        return AREA_NAME;
    }

    public GuangLanBean setAREA_NAME(String AREA_NAME) {
        this.AREA_NAME = AREA_NAME;
        return this;
    }

    public String getCITY_NAME() {
        return CITY_NAME;
    }

    public GuangLanBean setCITY_NAME(String CITY_NAME) {
        this.CITY_NAME = CITY_NAME;
        return this;
    }

    public String getZGEOY() {
        return ZGEOY;
    }

    public GuangLanBean setZGEOY(String ZGEOY) {
        this.ZGEOY = ZGEOY;
        return this;
    }

    public String getZHOSTNAME() {
        return ZHOSTNAME;
    }

    public GuangLanBean setZHOSTNAME(String ZHOSTNAME) {
        this.ZHOSTNAME = ZHOSTNAME;
        return this;
    }

    public String getCABLE_TYPE() {
        return CABLE_TYPE;
    }

    public GuangLanBean setCABLE_TYPE(String CABLE_TYPE) {
        this.CABLE_TYPE = CABLE_TYPE;
        return this;
    }

    public String getAGEOY() {
        return AGEOY;
    }

    public GuangLanBean setAGEOY(String AGEOY) {
        this.AGEOY = AGEOY;
        return this;
    }

    public String getFLAG() {
        return FLAG;
    }

    public GuangLanBean setFLAG(String FLAG) {
        this.FLAG = FLAG;
        return this;
    }

    public String getAGEOX() {
        return AGEOX;
    }

    public GuangLanBean setAGEOX(String AGEOX) {
        this.AGEOX = AGEOX;
        return this;
    }

    public String getCABLE_ID() {
        return CABLE_ID;
    }

    public GuangLanBean setCABLE_ID(String CABLE_ID) {
        this.CABLE_ID = CABLE_ID;
        return this;
    }

    public String getSTATE_NAME() {
        return STATE_NAME;
    }

    public void setSTATE_NAME(String STATE_NAME) {
        this.STATE_NAME = STATE_NAME;
    }

    public String getCAPACITY() {
        return CAPACITY;
    }

    public void setCAPACITY(String CAPACITY) {
        this.CAPACITY = CAPACITY;
    }



    public String getR() {
        return R;
    }

    public void setR(String R) {
        this.R = R;
    }



    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }


}
