package com.colaclub.common.core.domain.model;

/**
 * 用户注册对象
 *
 * @author colaclub
 */
public class RegisterBody {

    /** 唯一标识 */
    private String uuid;

    /** 手机号 */
    private String phone;

    /** 手机验证码 */
    private String smsCode;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
