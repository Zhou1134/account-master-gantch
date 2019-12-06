package com.edu.bupt.new_account.model;

public class Rule {
    private Integer ruleid;

    private Integer tenantid;

    private String additionalInfo;

    private String name;

    private String state;

    private String ruleType;

    private String gatewayid;

    public Rule(Integer ruleid, Integer tenantid, String additionalInfo, String name, String state, String ruleType, String gatewayid) {
        this.ruleid = ruleid;
        this.tenantid = tenantid;
        this.additionalInfo = additionalInfo;
        this.name = name;
        this.state = state;
        this.ruleType = ruleType;
        this.gatewayid = gatewayid;
    }

    public Rule() {
        super();
    }

    public Integer getRuleid() {
        return ruleid;
    }

    public void setRuleid(Integer ruleid) {
        this.ruleid = ruleid;
    }

    public Integer getTenantid() {
        return tenantid;
    }

    public void setTenantid(Integer tenantid) {
        this.tenantid = tenantid;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo == null ? null : additionalInfo.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state == null ? null : state.trim();
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType == null ? null : ruleType.trim();
    }

    public String getGatewayid() {
        return gatewayid;
    }

    public void setGatewayid(String gatewayid) {
        this.gatewayid = gatewayid == null ? null : gatewayid.trim();
    }
}