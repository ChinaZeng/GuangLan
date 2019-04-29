package com.zzw.guanglan.bean;


import java.util.List;

/**
 * Create by zzw on 2018/12/25
 */
public class GongDanBean {


    /**
     * STATE_NAME : 待施工
     * LAST_SCORE : null
     * ORG_ID : 11136658
     * FIRST_SCORE : -163.37
     * GUANG_LAN_DUAN : [{"STATE_NAME":null,"SERIAL_NO":90000011,"StringITUDE":118.80679063114874,"NAME":"南京","CABLE_NAME":"虎踞路81号--东井亭机房（玄武湖隧道方向）光缆01","R":1,"AHOSTNAME":"南京鼓楼区东井亭机房三层机房","ROOM_ID":7123,"ROOM_NAME":"南京鼓楼区东井亭机房三层机房","CAPATICY":"144","CABLE_LEVEL":"二干光缆","ID":"2170612","CABL_OP_NAME":"南京东井亭机楼-GT01/080588#光缆段01","LATITUDE":32.10590995572503,"CABL_OP_CODE":null,"PA_CABLE_ID":1383561}]
     * ORG_NAME : 江宁区维护部
     * CREATE_NAME : 超级管理员
     * WO_TYPE_NAME : 低得分工单
     * FIRST_TIME : 2019-01-23 13:38:50
     * WO_ID : 20190413020
     * LAST_TIME : null
     * CABLE_ID : 2170612
     * PLAN_END_TIME : 2019-04-26
     * STATE_ID : 100042
     * CREATE_DATE : 2019-04-23 19:34:47
     * R : 1
     * CABL_OP_NAME : 南京东井亭机楼-GT01/080588#光缆段01
     * CABL_OP_CODE : null
     * WO_NAME : 789
     */

    private String STATE_NAME;
    private String LAST_SCORE;
    private String ORG_ID;
    private String FIRST_SCORE;
    private String ORG_NAME;
    private String CREATE_NAME;
    private String WO_TYPE_NAME;
    private String FIRST_TIME;
    private String WO_ID;
    private String LAST_TIME;
    private String CABLE_ID;
    private String PLAN_END_TIME;
    private String STATE_ID;
    private String CREATE_DATE;
    private String R;
    private String CABL_OP_NAME;
    private String CABL_OP_CODE;
    private String WO_NAME;
    private List<GuangLanDItemBean> GUANG_LAN_DUAN;

    public List<GuangLanDItemBean> getGUANG_LAN_DUAN() {
        return GUANG_LAN_DUAN;
    }

    public void setGUANG_LAN_DUAN(List<GuangLanDItemBean> GUANG_LAN_DUAN) {
        this.GUANG_LAN_DUAN = GUANG_LAN_DUAN;
    }

    public String getSTATE_NAME() {
        return STATE_NAME;
    }

    public void setSTATE_NAME(String STATE_NAME) {
        this.STATE_NAME = STATE_NAME;
    }

    public String getLAST_SCORE() {
        return LAST_SCORE;
    }

    public void setLAST_SCORE(String LAST_SCORE) {
        this.LAST_SCORE = LAST_SCORE;
    }

    public String getORG_ID() {
        return ORG_ID;
    }

    public void setORG_ID(String ORG_ID) {
        this.ORG_ID = ORG_ID;
    }

    public String getFIRST_SCORE() {
        return FIRST_SCORE;
    }

    public void setFIRST_SCORE(String FIRST_SCORE) {
        this.FIRST_SCORE = FIRST_SCORE;
    }

    public String getORG_NAME() {
        return ORG_NAME;
    }

    public void setORG_NAME(String ORG_NAME) {
        this.ORG_NAME = ORG_NAME;
    }

    public String getCREATE_NAME() {
        return CREATE_NAME;
    }

    public void setCREATE_NAME(String CREATE_NAME) {
        this.CREATE_NAME = CREATE_NAME;
    }

    public String getWO_TYPE_NAME() {
        return WO_TYPE_NAME;
    }

    public void setWO_TYPE_NAME(String WO_TYPE_NAME) {
        this.WO_TYPE_NAME = WO_TYPE_NAME;
    }

    public String getFIRST_TIME() {
        return FIRST_TIME;
    }

    public void setFIRST_TIME(String FIRST_TIME) {
        this.FIRST_TIME = FIRST_TIME;
    }

    public String getWO_ID() {
        return WO_ID;
    }

    public void setWO_ID(String WO_ID) {
        this.WO_ID = WO_ID;
    }

    public String getLAST_TIME() {
        return LAST_TIME;
    }

    public void setLAST_TIME(String LAST_TIME) {
        this.LAST_TIME = LAST_TIME;
    }

    public String getCABLE_ID() {
        return CABLE_ID;
    }

    public void setCABLE_ID(String CABLE_ID) {
        this.CABLE_ID = CABLE_ID;
    }

    public String getPLAN_END_TIME() {
        return PLAN_END_TIME;
    }

    public void setPLAN_END_TIME(String PLAN_END_TIME) {
        this.PLAN_END_TIME = PLAN_END_TIME;
    }

    public String getSTATE_ID() {
        return STATE_ID;
    }

    public void setSTATE_ID(String STATE_ID) {
        this.STATE_ID = STATE_ID;
    }

    public String getCREATE_DATE() {
        return CREATE_DATE;
    }

    public void setCREATE_DATE(String CREATE_DATE) {
        this.CREATE_DATE = CREATE_DATE;
    }

    public String getR() {
        return R;
    }

    public void setR(String R) {
        this.R = R;
    }

    public String getCABL_OP_NAME() {
        return CABL_OP_NAME;
    }

    public void setCABL_OP_NAME(String CABL_OP_NAME) {
        this.CABL_OP_NAME = CABL_OP_NAME;
    }

    public String getCABL_OP_CODE() {
        return CABL_OP_CODE;
    }

    public void setCABL_OP_CODE(String CABL_OP_CODE) {
        this.CABL_OP_CODE = CABL_OP_CODE;
    }

    public String getWO_NAME() {
        return WO_NAME;
    }

    public void setWO_NAME(String WO_NAME) {
        this.WO_NAME = WO_NAME;
    }

}
