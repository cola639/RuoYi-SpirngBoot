package com.ruoyi.framework.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取项目相关配置
 *
 * @author cola639
 */
@Component
@ConfigurationProperties(prefix = "third-login")
public class ThirdLogins {
    private String giteeClientId;
    private String giteeClientSecret;
    private String giteeRedirectURL;

    public String getGiteeClientId() {
        return giteeClientId;
    }

    public void setGiteeClientId(String giteeClientId) {
        this.giteeClientId = giteeClientId;
    }

    public String getGiteeClientSecret() {
        return giteeClientSecret;
    }

    public void setGiteeClientSecret(String giteeClientSecret) {
        this.giteeClientSecret = giteeClientSecret;
    }

    public String getGiteeRedirectURL() {
        return giteeRedirectURL;
    }

    public void setGiteeRedirectURL(String giteeRedirectURL) {
        this.giteeRedirectURL = giteeRedirectURL;
    }
}
