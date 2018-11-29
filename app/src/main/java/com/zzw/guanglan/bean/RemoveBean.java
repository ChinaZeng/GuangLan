package com.zzw.guanglan.bean;

import java.util.List;

/**
 * Create by zzw on 2018/11/29
 */
public class RemoveBean {
    private List<String> remove;
    private int code;
    private String msg;

    public RemoveBean() {

    }

    public List<String> getRemove() {
        return remove;
    }

    public RemoveBean setRemove(List<String> remove) {
        this.remove = remove;
        return this;
    }
}
