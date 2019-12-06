package com.edu.bupt.new_account.model;

public class Tenant {
    private Integer id;

    private String tenantName;

    private String email;

    private String phone;

    private String password;

    public Tenant(Integer id, String tenantName, String email, String phone, String password) {
        this.id = id;
        this.tenantName = tenantName;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public Tenant() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName == null ? null : tenantName.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }
}