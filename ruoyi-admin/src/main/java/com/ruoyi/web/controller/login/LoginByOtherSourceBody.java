package com.ruoyi.web.controller.login;

public class LoginByOtherSourceBody {

    private String code;
    private String source;
    private String uuid;

    // getter and setter methods

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
