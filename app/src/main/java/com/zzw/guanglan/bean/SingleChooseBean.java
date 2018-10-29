package com.zzw.guanglan.bean;

import java.util.List;

public class SingleChooseBean {

    private int id;
    private String name;
    private int value;
    private float floatValue;

    private List<SingleChooseBean> nextChooses;

    public SingleChooseBean(int id, String name, int value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }


    public SingleChooseBean(int id, String name, float floatValue) {
        this.id = id;
        this.name = name;
        this.floatValue = floatValue;
    }

    public SingleChooseBean(int id, String name, int value, List<SingleChooseBean> nextChooses) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.nextChooses = nextChooses;
    }

    public List<SingleChooseBean> getNextChooses() {
        return nextChooses;
    }

    public void setNextChooses(List<SingleChooseBean> nextChooses) {
        this.nextChooses = nextChooses;
    }

    public int getId() {
        return id;
    }


    public float getFloatValue() {
        return floatValue;
    }

    public void setFloatValue(float floatValue) {
        this.floatValue = floatValue;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
