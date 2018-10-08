package com.zzw.socketdemo.bean;

import com.zzw.socketdemo.dialogs.multilevel.INamedEntity;

import java.util.List;

public class AreaBean implements INamedEntity {

    private String id;
    private String parentId;
    private String text;
    private String state;
    private List<AreaBean> children;

    @Override
    public String _getDisplayName_() {
        return text;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<AreaBean> getChildren() {
        return children;
    }

    public void setChildren(List<AreaBean> children) {
        this.children = children;
    }
}
