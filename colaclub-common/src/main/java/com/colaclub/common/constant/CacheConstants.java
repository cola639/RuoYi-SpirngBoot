package com.colaclub.common.constant;

/**
 * 缓存的key 常量
 *
 * @author colaclub
 */
public class CacheConstants {
    /** 短信验证码 */
    public static final String SMS_CAPTCHA_CODE_KEY = "sms_captcha:codes:";

    /** 验证码有效期（分钟） */
    public static final Integer CAPTCHA_EXPIRATION = 2;

    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     * 参数管理 cache key
     */
    public static final String SYS_CONFIG_KEY = "sys_config:";

    /**
     * 字典管理 cache key
     */
    public static final String SYS_DICT_KEY = "sys_dict:";

    /**
     * 防重提交 redis key
     */
    public static final String REPEAT_SUBMIT_KEY = "repeat_submit:";

    /**
     * 限流 redis key
     */
    public static final String RATE_LIMIT_KEY = "rate_limit:";
}
