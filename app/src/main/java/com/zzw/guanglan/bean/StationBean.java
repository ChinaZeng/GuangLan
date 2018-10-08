package com.zzw.guanglan.bean;

public class StationBean {

    /**
     * inChargeTel : 
     * mntOrgId : 0
     * pubResourceSpec : {"resSpecId":0,"parName":"","no":"","parentResSpecId":0,"modifyDate":null,"modifyOp":0,"version":0,"isInstance":0,"name":"","createOp":0,"createDate":null,"notes":"","buildCost":0}
     * gradeId : 0
     * spcArea : {"idPrefix":0,"no":"","parentAreaId":0,"code":"","modifyDate":null,"modifyOp":0,"areaId":377,"areaLevel":0,"zipCode":0,"isLeaf":0,"name":"","createOp":0,"parentAreaName":"","notes":"","createDate":null,"zoneNumber":0}
     * no : DCQ-001
     * stationId : 621
     * useDate : null
     * modifyDate : null
     * modifyOp : 0
     * addressId : 0
     * propertyType : 0
     * address : 上海东城区001号
     * name : 东城区局站
     * pubStatus : {"specStateId":0,"no":"","stateId":0,"statusTypeId":0,"name":"","createOp":0,"modifyDate":null,"createDate":null,"modifyOp":0,"notes":"","version":0}
     * createOp : 10000763
     * createOpName : 
     * stationTypeId : 0
     * mntOp : 0
     * inChargeOp : 
     * createDate : {"time":1520840795000,"minutes":46,"seconds":35,"hours":15,"month":2,"timezoneOffset":-480,"year":118,"day":1,"date":12}
     * notes : 测试数据
     */

    private String inChargeTel;
    private String mntOrgId;
    private PubResourceSpecBean pubResourceSpec;
    private String gradeId;
    private SpcAreaBean spcArea;
    private String no;
    private String stationId;
    private String useDate;
    private String modifyDate;
    private String modifyOp;
    private String addressId;
    private String propertyType;
    private String address;
    private String name;
    private PubStatusBean pubStatus;
    private String createOp;
    private String createOpName;
    private String stationTypeId;
    private String mntOp;
    private String inChargeOp;
    private CreateDateBean createDate;
    private String notes;

    public String getInChargeTel() {
        return inChargeTel;
    }

    public void setInChargeTel(String inChargeTel) {
        this.inChargeTel = inChargeTel;
    }

    public String getMntOrgId() {
        return mntOrgId;
    }

    public void setMntOrgId(String mntOrgId) {
        this.mntOrgId = mntOrgId;
    }

    public PubResourceSpecBean getPubResourceSpec() {
        return pubResourceSpec;
    }

    public void setPubResourceSpec(PubResourceSpecBean pubResourceSpec) {
        this.pubResourceSpec = pubResourceSpec;
    }

    public String getGradeId() {
        return gradeId;
    }

    public void setGradeId(String gradeId) {
        this.gradeId = gradeId;
    }

    public SpcAreaBean getSpcArea() {
        return spcArea;
    }

    public void setSpcArea(SpcAreaBean spcArea) {
        this.spcArea = spcArea;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getUseDate() {
        return useDate;
    }

    public void setUseDate(String useDate) {
        this.useDate = useDate;
    }

    public String getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getModifyOp() {
        return modifyOp;
    }

    public void setModifyOp(String modifyOp) {
        this.modifyOp = modifyOp;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PubStatusBean getPubStatus() {
        return pubStatus;
    }

    public void setPubStatus(PubStatusBean pubStatus) {
        this.pubStatus = pubStatus;
    }

    public String getCreateOp() {
        return createOp;
    }

    public void setCreateOp(String createOp) {
        this.createOp = createOp;
    }

    public String getCreateOpName() {
        return createOpName;
    }

    public void setCreateOpName(String createOpName) {
        this.createOpName = createOpName;
    }

    public String getStationTypeId() {
        return stationTypeId;
    }

    public void setStationTypeId(String stationTypeId) {
        this.stationTypeId = stationTypeId;
    }

    public String getMntOp() {
        return mntOp;
    }

    public void setMntOp(String mntOp) {
        this.mntOp = mntOp;
    }

    public String getInChargeOp() {
        return inChargeOp;
    }

    public void setInChargeOp(String inChargeOp) {
        this.inChargeOp = inChargeOp;
    }

    public CreateDateBean getCreateDate() {
        return createDate;
    }

    public void setCreateDate(CreateDateBean createDate) {
        this.createDate = createDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public static class PubResourceSpecBean {
        /**
         * resSpecId : 0
         * parName : 
         * no : 
         * parentResSpecId : 0
         * modifyDate : null
         * modifyOp : 0
         * version : 0
         * isInstance : 0
         * name : 
         * createOp : 0
         * createDate : null
         * notes : 
         * buildCost : 0
         */

        private String resSpecId;
        private String parName;
        private String no;
        private String parentResSpecId;
        private String modifyDate;
        private String modifyOp;
        private String version;
        private String isInstance;
        private String name;
        private String createOp;
        private String createDate;
        private String notes;
        private String buildCost;

        public String getResSpecId() {
            return resSpecId;
        }

        public void setResSpecId(String resSpecId) {
            this.resSpecId = resSpecId;
        }

        public String getParName() {
            return parName;
        }

        public void setParName(String parName) {
            this.parName = parName;
        }

        public String getNo() {
            return no;
        }

        public void setNo(String no) {
            this.no = no;
        }

        public String getParentResSpecId() {
            return parentResSpecId;
        }

        public void setParentResSpecId(String parentResSpecId) {
            this.parentResSpecId = parentResSpecId;
        }

        public String getModifyDate() {
            return modifyDate;
        }

        public void setModifyDate(String modifyDate) {
            this.modifyDate = modifyDate;
        }

        public String getModifyOp() {
            return modifyOp;
        }

        public void setModifyOp(String modifyOp) {
            this.modifyOp = modifyOp;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getIsInstance() {
            return isInstance;
        }

        public void setIsInstance(String isInstance) {
            this.isInstance = isInstance;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCreateOp() {
            return createOp;
        }

        public void setCreateOp(String createOp) {
            this.createOp = createOp;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public String getBuildCost() {
            return buildCost;
        }

        public void setBuildCost(String buildCost) {
            this.buildCost = buildCost;
        }
    }

    public static class SpcAreaBean {
        /**
         * idPrefix : 0
         * no : 
         * parentAreaId : 0
         * code : 
         * modifyDate : null
         * modifyOp : 0
         * areaId : 377
         * areaLevel : 0
         * zipCode : 0
         * isLeaf : 0
         * name : 
         * createOp : 0
         * parentAreaName : 
         * notes : 
         * createDate : null
         * zoneNumber : 0
         */

        private String idPrefix;
        private String no;
        private String parentAreaId;
        private String code;
        private String modifyDate;
        private String modifyOp;
        private String areaId;
        private String areaLevel;
        private String zipCode;
        private String isLeaf;
        private String name;
        private String createOp;
        private String parentAreaName;
        private String notes;
        private String createDate;
        private String zoneNumber;

        public String getIdPrefix() {
            return idPrefix;
        }

        public void setIdPrefix(String idPrefix) {
            this.idPrefix = idPrefix;
        }

        public String getNo() {
            return no;
        }

        public void setNo(String no) {
            this.no = no;
        }

        public String getParentAreaId() {
            return parentAreaId;
        }

        public void setParentAreaId(String parentAreaId) {
            this.parentAreaId = parentAreaId;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getModifyDate() {
            return modifyDate;
        }

        public void setModifyDate(String modifyDate) {
            this.modifyDate = modifyDate;
        }

        public String getModifyOp() {
            return modifyOp;
        }

        public void setModifyOp(String modifyOp) {
            this.modifyOp = modifyOp;
        }

        public String getAreaId() {
            return areaId;
        }

        public void setAreaId(String areaId) {
            this.areaId = areaId;
        }

        public String getAreaLevel() {
            return areaLevel;
        }

        public void setAreaLevel(String areaLevel) {
            this.areaLevel = areaLevel;
        }

        public String getZipCode() {
            return zipCode;
        }

        public void setZipCode(String zipCode) {
            this.zipCode = zipCode;
        }

        public String getIsLeaf() {
            return isLeaf;
        }

        public void setIsLeaf(String isLeaf) {
            this.isLeaf = isLeaf;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCreateOp() {
            return createOp;
        }

        public void setCreateOp(String createOp) {
            this.createOp = createOp;
        }

        public String getParentAreaName() {
            return parentAreaName;
        }

        public void setParentAreaName(String parentAreaName) {
            this.parentAreaName = parentAreaName;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getZoneNumber() {
            return zoneNumber;
        }

        public void setZoneNumber(String zoneNumber) {
            this.zoneNumber = zoneNumber;
        }
    }

    public static class PubStatusBean {
        /**
         * specStateId : 0
         * no : 
         * stateId : 0
         * statusTypeId : 0
         * name : 
         * createOp : 0
         * modifyDate : null
         * createDate : null
         * modifyOp : 0
         * notes : 
         * version : 0
         */

        private String specStateId;
        private String no;
        private String stateId;
        private String statusTypeId;
        private String name;
        private String createOp;
        private String modifyDate;
        private String createDate;
        private String modifyOp;
        private String notes;
        private String version;

        public String getSpecStateId() {
            return specStateId;
        }

        public void setSpecStateId(String specStateId) {
            this.specStateId = specStateId;
        }

        public String getNo() {
            return no;
        }

        public void setNo(String no) {
            this.no = no;
        }

        public String getStateId() {
            return stateId;
        }

        public void setStateId(String stateId) {
            this.stateId = stateId;
        }

        public String getStatusTypeId() {
            return statusTypeId;
        }

        public void setStatusTypeId(String statusTypeId) {
            this.statusTypeId = statusTypeId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCreateOp() {
            return createOp;
        }

        public void setCreateOp(String createOp) {
            this.createOp = createOp;
        }

        public String getModifyDate() {
            return modifyDate;
        }

        public void setModifyDate(String modifyDate) {
            this.modifyDate = modifyDate;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getModifyOp() {
            return modifyOp;
        }

        public void setModifyOp(String modifyOp) {
            this.modifyOp = modifyOp;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }

    public static class CreateDateBean {
        /**
         * time : 1520840795000
         * minutes : 46
         * seconds : 35
         * hours : 15
         * month : 2
         * timezoneOffset : -480
         * year : 118
         * day : 1
         * date : 12
         */

        private long time;
        private String minutes;
        private String seconds;
        private String hours;
        private String month;
        private String timezoneOffset;
        private String year;
        private String day;
        private String date;

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public String getMinutes() {
            return minutes;
        }

        public void setMinutes(String minutes) {
            this.minutes = minutes;
        }

        public String getSeconds() {
            return seconds;
        }

        public void setSeconds(String seconds) {
            this.seconds = seconds;
        }

        public String getHours() {
            return hours;
        }

        public void setHours(String hours) {
            this.hours = hours;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public String getTimezoneOffset() {
            return timezoneOffset;
        }

        public void setTimezoneOffset(String timezoneOffset) {
            this.timezoneOffset = timezoneOffset;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
}
