package com.zzw.socketdemo.bean;

import java.util.List;

/**
 * Created by zzw on 2018/10/4.
 * 描述:
 */
public class ListDataBean<T> {
    private int total;
    private List<T> list;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
