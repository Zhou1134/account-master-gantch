package com.edu.bupt.new_account.model;

public class Transform {
    private Integer transformid;

    private String name;

    private String url;

    private String method;

    private String requestbody;

    public Transform(Integer transformid, String name, String url, String method, String requestbody) {
        this.transformid = transformid;
        this.name = name;
        this.url = url;
        this.method = method;
        this.requestbody = requestbody;
    }

    public Transform() {
        super();
    }

    public Integer getTransformid() {
        return transformid;
    }

    public void setTransformid(Integer transformid) {
        this.transformid = transformid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method == null ? null : method.trim();
    }

    public String getRequestbody() {
        return requestbody;
    }

    public void setRequestbody(String requestbody) {
        this.requestbody = requestbody == null ? null : requestbody.trim();
    }
}