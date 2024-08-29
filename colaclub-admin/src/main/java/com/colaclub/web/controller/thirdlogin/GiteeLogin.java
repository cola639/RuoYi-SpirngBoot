package com.colaclub.web.controller.thirdlogin;

import com.colaclub.common.constant.Constants;
import com.colaclub.common.core.domain.AjaxResult;
import com.colaclub.common.utils.uuid.IdUtils;
import com.colaclub.framework.config.ThirdLogins;
import com.colaclub.framework.web.service.SysLoginService;
import com.colaclub.thirdlogin.LoginByOtherSourceBody;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.request.AuthGiteeRequest;
import me.zhyd.oauth.request.AuthGithubRequest;
import me.zhyd.oauth.request.AuthRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class GiteeLogin {

    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ThirdLogins thirdLogins;

    @GetMapping("/PreLoginByGitee")
    public AjaxResult PreLoginByGitee() {

        AuthRequest authRequest = new AuthGiteeRequest(
                AuthConfig.builder()
                        .clientId(thirdLogins.getGiteeClientId())
                        .clientSecret(thirdLogins.getGiteeClientSecret())
                        .redirectUri(thirdLogins.getGiteeRedirectURL())
                        .build()
        );

        String uuid = IdUtils.fastUUID();
        log.info("uuid: '{}'", uuid);
        String authorizeUrl = authRequest.authorize(uuid); // 将 UUID 作为参数添加到回调 URL
        AjaxResult ajax = AjaxResult.success(); // 存储
        ajax.put("authorizeUrl", authorizeUrl);
        ajax.put("uuid", uuid);
        return ajax;
    }

    @PostMapping("/loginByGitee")
    public AjaxResult loginByGitee(@RequestBody LoginByOtherSourceBody loginByOtherSourceBody) {
        String token = loginService.loginByGitee(loginByOtherSourceBody.getCode(), loginByOtherSourceBody.getSource(), loginByOtherSourceBody.getUuid());
        AjaxResult ajax = AjaxResult.success();
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    @GetMapping("/PreLoginByGithub")
    public AjaxResult PreLoginByGitub() {
        AuthRequest authRequest = new AuthGithubRequest(
                AuthConfig.builder()
                        .clientId(thirdLogins.getGithubClientId())
                        .clientSecret(thirdLogins.getGithubClientSecret())
                        .redirectUri(thirdLogins.getGithubRedirectURL() + "?source=github")
                        .build()
        );
        String uuid = IdUtils.fastUUID();
        String authorizeUrl = authRequest.authorize(uuid); // 将 UUID 作为参数添加到回调 URL
        AjaxResult ajax = AjaxResult.success(); // 存储
        ajax.put("authorizeUrl", authorizeUrl);
        ajax.put("uuid", uuid);
        return ajax;
    }

    @PostMapping("/loginByGithub")
    public AjaxResult loginByGithub(@RequestBody LoginByOtherSourceBody loginByOtherSourceBody) {
        String token = loginService.loginByGithub(loginByOtherSourceBody.getCode(), loginByOtherSourceBody.getSource(), loginByOtherSourceBody.getUuid());
        AjaxResult ajax = AjaxResult.success();
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }
}
