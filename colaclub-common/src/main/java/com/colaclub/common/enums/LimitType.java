package com.colaclub.common.enums;

/**
 * 限流类型
 *
 * @author colaclub
 */

public enum LimitType {
    /**
     * 默认策略全局限流
     */
    DEFAULT,

    /**
     * 根据请求者IP进行限流
     */
    IP,

    /**
     * 用户帐户限流
     */
    USER,

    /**
     * 设备号限流
     */
    DEVICE,

    /**
     * 手机号
     */
    PHONE,
}
