package com.colaclub.common.core.domain.model;

/**
 * 用户登录对象
 *
 * @author colaclub
 */
public class LoginBody {
    /** 用户名 */
    private String username;

    /** 用户密码 */
    private String password;

    /** 验证码 */
    private String code;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
