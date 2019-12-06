package com.edu.bupt.new_account.model;

public class Rule2FilterKey {
    private Integer filterid;

    private Integer ruleid;

    public Rule2FilterKey(Integer filterid, Integer ruleid) {
        this.filterid = filterid;
        this.ruleid = ruleid;
    }

    public Rule2FilterKey() {
        super();
    }

    public Integer getFilterid() {
        return filterid;
    }

    public void setFilterid(Integer filterid) {
        this.filterid = filterid;
    }

    public Integer getRuleid() {
        return ruleid;
    }

    public void setRuleid(Integer ruleid) {
        this.ruleid = ruleid;
    }
}