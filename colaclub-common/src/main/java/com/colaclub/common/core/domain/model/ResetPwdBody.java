package com.colaclub.common.core.domain.model;

import lombok.Data;

@Data
public class ResetPwdBody {
    /** 用户名 */
    private String username;

    /** 唯一标识 */
    private String uuid;

    /** 手机号 */
    private String mobile;

    /** 手机验证码 */
    private String smsCode;

    /** 密码 */
    private String password;
}
