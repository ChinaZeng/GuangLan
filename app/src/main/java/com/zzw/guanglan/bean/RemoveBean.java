package com.zzw.guanglan.bean;

import java.util.List;

/**
 * Create by zzw on 2018/11/29
 */
public class RemoveBean {

    /**
     * msg : 查询成功
     * code : 0
     * dbkm : 无
     * remove : [{"fiberId":"10000004821050","text":"编号5芯检测情况：与1，2，3，4测试结果完全相同"},{"fiberId":"10000004821050","text":"编号5芯检测情况：与1，2，3，4测试结果完全相同"},{"fiberId":"10000004821050","text":"编号5芯检测情况：与1，2，3，4测试结果完全相同"}]
     */

    private String msg;
    private int code;
    private String dbkm;
    private List<RemoveObjBean> remove;
    private List<AbnormalBean> abnormal;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDbkm() {
        return dbkm;
    }

    public void setDbkm(String dbkm) {
        this.dbkm = dbkm;
    }

    public List<RemoveObjBean> getRemove() {
        return remove;
    }

    public void setRemove(List<RemoveObjBean> remove) {
        this.remove = remove;
    }

    public List<AbnormalBean> getAbnormal() {
        return abnormal;
    }

    public void setAbnormal(List<AbnormalBean> abnormal) {
        this.abnormal = abnormal;
    }

    public static class RemoveObjBean {
        /**
         * fiberId : 10000004821050
         * text : 编号5芯检测情况：与1，2，3，4测试结果完全相同
         */

        private String fiberId;
        private String text;

        public String getFiberId() {
            return fiberId;
        }

        public void setFiberId(String fiberId) {
            this.fiberId = fiberId;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public static class AbnormalBean {
        /**
         * fiberId : 10000004821050
         * text : 编号5芯检测情况：与1，2，3，4测试结果完全相同
         */

        private String fiberId;
        private String text;

        public String getFiberId() {
            return fiberId;
        }

        public void setFiberId(String fiberId) {
            this.fiberId = fiberId;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
