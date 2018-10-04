package com.zzw.socketdemo.bean;

import java.util.List;

/**
 * Created by zzw on 2018/10/4.
 * 描述:
 */
public class ListDataBean<T> {
    private List<T> list;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
