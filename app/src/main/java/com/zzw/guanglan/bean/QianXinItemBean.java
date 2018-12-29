package com.zzw.guanglan.bean;

/**
 * Created by zzw on 2018/10/3.
 * 描述:
 */
public class QianXinItemBean {


    


    private String testLocalFilePath;
    private boolean upload=false;


    /**
     * NO : 1
     * FIBER_ID : 10000004816750
     * CREATE_DATE : 2018-12-29 09:22:38
     * R : 1
     * STATENAME : 空闲
     * CABL_OP_NAME : 测试33
     * CABLE_ID : 75896283801325602
     */

    private String NO;
    private String FIBER_ID;
    private String CREATE_DATE;
    private String R;
    private String STATENAME;
    private String CABL_OP_NAME;
    private String CABLE_ID;

    private String modifyTimeString;

    public String getModifyTimeString() {
        return modifyTimeString;
    }

    public void setModifyTimeString(String modifyTimeString) {
        this.modifyTimeString = modifyTimeString;
    }



    public String getTestLocalFilePath() {
        return testLocalFilePath;
    }

    public void setTestLocalFilePath(String testLocalFilePath) {
        this.testLocalFilePath = testLocalFilePath;
    }

    public boolean isUpload() {
        return upload;
    }

    public void setUpload(boolean upload) {
        this.upload = upload;
    }

    public String getNO() {
        return NO;
    }

    public void setNO(String NO) {
        this.NO = NO;
    }

    public String getFIBER_ID() {
        return FIBER_ID;
    }

    public void setFIBER_ID(String FIBER_ID) {
        this.FIBER_ID = FIBER_ID;
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

    public String getSTATENAME() {
        return STATENAME;
    }

    public void setSTATENAME(String STATENAME) {
        this.STATENAME = STATENAME;
    }

    public String getCABL_OP_NAME() {
        return CABL_OP_NAME;
    }

    public void setCABL_OP_NAME(String CABL_OP_NAME) {
        this.CABL_OP_NAME = CABL_OP_NAME;
    }

    public String getCABLE_ID() {
        return CABLE_ID;
    }

    public void setCABLE_ID(String CABLE_ID) {
        this.CABLE_ID = CABLE_ID;
    }
}
