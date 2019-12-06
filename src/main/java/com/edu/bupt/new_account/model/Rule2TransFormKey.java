package com.edu.bupt.new_account.model;

public class Rule2TransFormKey {
    private Integer transformid;

    private Integer ruleid;

    public Rule2TransFormKey(Integer transformid, Integer ruleid) {
        this.transformid = transformid;
        this.ruleid = ruleid;
    }

    public Rule2TransFormKey() {
        super();
    }

    public Integer getTransformid() {
        return transformid;
    }

    public void setTransformid(Integer transformid) {
        this.transformid = transformid;
    }

    public Integer getRuleid() {
        return ruleid;
    }

    public void setRuleid(Integer ruleid) {
        this.ruleid = ruleid;
    }
}