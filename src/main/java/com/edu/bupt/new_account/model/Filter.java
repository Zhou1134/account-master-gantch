package com.edu.bupt.new_account.model;

public class Filter {
    private Integer filterid;

    private String type;

    private String name;

    private String jscode;

    public Filter(Integer filterid, String type, String name, String jscode) {
        this.filterid = filterid;
        this.type = type;
        this.name = name;
        this.jscode = jscode;
    }

    public Filter() {
        super();
    }

    public Integer getFilterid() {
        return filterid;
    }

    public void setFilterid(Integer filterid) {
        this.filterid = filterid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getJscode() {
        return jscode;
    }

    public void setJscode(String jscode) {
        this.jscode = jscode == null ? null : jscode.trim();
    }
}