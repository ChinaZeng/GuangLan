package com.zzw.guanglan.bean;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by zzw on 2018/10/3.
 * 描述:
 */
public class GuangLanDItemBean implements Serializable {


    /**
     * id : 55592803299758080
     * cabelOpName : 光缆段名称123
     * cabelOpCode : 光缆段编码
     * statId : 621
     * areaId : 377
     * roomId : 496
     * capaticy : 24
     * state : 100001
     * opString : 12
     * orgId : null
     * orgUserName : null
     * opStartTime : null
     * lastTime : 1536508800000
     * paCableId : null
     * paEquipId : null
     * createOp : null
     * areaName : 东城区
     * remark : null
     * paCableName : 测试11133
     * paCableLevel : 配线
     * stateName : 空闲
     */

    private String id;
    private String cabelOpName;
    private String cabelOpCode;
    private String statId;
    private String areaId;
    private String roomId;
    private String capaticy;
    private String state;
    private String opString;
    private String orgId;
    private String orgUserName;
    private String opStartTime;
    private String lastTime;
    private String paCableId;
    private String paEquipId;
    private String createOp;
    private String areaName;
    private String remark;
    private String paCableName;
    private String paCableLevel;
    private String stateName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCabelOpName() {
        return cabelOpName;
    }

    public void setCabelOpName(String cabelOpName) {
        this.cabelOpName = cabelOpName;
    }

    public String getCabelOpCode() {
        return cabelOpCode;
    }

    public void setCabelOpCode(String cabelOpCode) {
        this.cabelOpCode = cabelOpCode;
    }

    public String getStatId() {
        return statId;
    }

    public void setStatId(String statId) {
        this.statId = statId;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getCapaticy() {
        return capaticy;
    }

    public void setCapaticy(String capaticy) {
        this.capaticy = capaticy;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getOpString() {
        return opString;
    }

    public void setOpString(String opString) {
        this.opString = opString;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgUserName() {
        return orgUserName;
    }

    public void setOrgUserName(String orgUserName) {
        this.orgUserName = orgUserName;
    }

    public String getOpStartTime() {
        return opStartTime;
    }

    public void setOpStartTime(String opStartTime) {
        this.opStartTime = opStartTime;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getPaCableId() {
        return paCableId;
    }

    public void setPaCableId(String paCableId) {
        this.paCableId = paCableId;
    }

    public String getPaEquipId() {
        return paEquipId;
    }

    public void setPaEquipId(String paEquipId) {
        this.paEquipId = paEquipId;
    }

    public String getCreateOp() {
        return createOp;
    }

    public void setCreateOp(String createOp) {
        this.createOp = createOp;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPaCableName() {
        return paCableName;
    }

    public void setPaCableName(String paCableName) {
        this.paCableName = paCableName;
    }

    public String getPaCableLevel() {
        return paCableLevel;
    }

    public void setPaCableLevel(String paCableLevel) {
        this.paCableLevel = paCableLevel;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GuangLanDItemBean that = (GuangLanDItemBean) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (cabelOpName != null ? !cabelOpName.equals(that.cabelOpName) : that.cabelOpName != null)
            return false;
        if (cabelOpCode != null ? !cabelOpCode.equals(that.cabelOpCode) : that.cabelOpCode != null)
            return false;
        if (statId != null ? !statId.equals(that.statId) : that.statId != null) return false;
        if (areaId != null ? !areaId.equals(that.areaId) : that.areaId != null) return false;
        if (roomId != null ? !roomId.equals(that.roomId) : that.roomId != null) return false;
        if (capaticy != null ? !capaticy.equals(that.capaticy) : that.capaticy != null)
            return false;
        if (state != null ? !state.equals(that.state) : that.state != null) return false;
        if (opString != null ? !opString.equals(that.opString) : that.opString != null)
            return false;
        if (orgId != null ? !orgId.equals(that.orgId) : that.orgId != null) return false;
        if (orgUserName != null ? !orgUserName.equals(that.orgUserName) : that.orgUserName != null)
            return false;
        if (opStartTime != null ? !opStartTime.equals(that.opStartTime) : that.opStartTime != null)
            return false;
        if (lastTime != null ? !lastTime.equals(that.lastTime) : that.lastTime != null)
            return false;
        if (paCableId != null ? !paCableId.equals(that.paCableId) : that.paCableId != null)
            return false;
        if (paEquipId != null ? !paEquipId.equals(that.paEquipId) : that.paEquipId != null)
            return false;
        if (createOp != null ? !createOp.equals(that.createOp) : that.createOp != null)
            return false;
        if (areaName != null ? !areaName.equals(that.areaName) : that.areaName != null)
            return false;
        if (remark != null ? !remark.equals(that.remark) : that.remark != null) return false;
        if (paCableName != null ? !paCableName.equals(that.paCableName) : that.paCableName != null)
            return false;
        if (paCableLevel != null ? !paCableLevel.equals(that.paCableLevel) : that.paCableLevel != null)
            return false;
        return stateName != null ? stateName.equals(that.stateName) : that.stateName == null;
    }

}
