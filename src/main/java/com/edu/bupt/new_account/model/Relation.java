package com.edu.bupt.new_account.model;

public class Relation {
    private Integer id;

    private Integer binder;

    private Integer binded;

    private String gateid;

    private String remark;

    public Relation(Integer id, Integer binder, Integer binded, String gateid, String remark) {
        this.id = id;
        this.binder = binder;
        this.binded = binded;
        this.gateid = gateid;
        this.remark = remark;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Relation() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBinder() {
        return binder;
    }

    public void setBinder(Integer binder) {
        this.binder = binder;
    }

    public Integer getBinded() {
        return binded;
    }

    public void setBinded(Integer binded) {
        this.binded = binded;
    }

    public String getGateid() {
        return gateid;
    }

    public void setGateid(String gateid) {
        this.gateid = gateid == null ? null : gateid.trim();
    }
}