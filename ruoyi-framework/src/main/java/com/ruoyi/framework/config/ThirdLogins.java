package com.ruoyi.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取第三方授权相关配置
 *
 * @author cola639
 */
@Data
@Component
@ConfigurationProperties(prefix = "third-login")
public class ThirdLogins {

    private String giteeClientId;
    private String giteeClientSecret;
    private String giteeRedirectURL;

    private String wechatAppId;
    private String wechatAppSecret;
    private String wechatUrl;

}
