package com.ruoyi.web.controller.thirdlogin;

import com.ruoyi.chat.domain.bo.LoginByOtherSourceBody;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.framework.config.ThirdLogins;
import com.ruoyi.framework.web.service.SysLoginService;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.request.AuthGiteeRequest;
import me.zhyd.oauth.request.AuthRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GiteeLogin {

    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ThirdLogins thirdLogins;

    @GetMapping("/PreLoginByGitee")
    public AjaxResult PreLoginByGitee() {
        AjaxResult ajax = AjaxResult.success();
        AuthRequest authRequest = new AuthGiteeRequest(AuthConfig.builder()
                .clientId(thirdLogins.getGiteeClientId())
                .clientSecret(thirdLogins.getGiteeClientSecret())
                .redirectUri(thirdLogins.getGiteeRedirectURL())
                .build());

        String uuid = IdUtils.fastUUID();
        System.out.println("uuid" + uuid);

        String authorizeUrl = authRequest.authorize(uuid); // 将 UUID 作为参数添加到回调 URL
        //存储
        ajax.put("authorizeUrl", authorizeUrl);
        ajax.put("uuid", uuid);
        return ajax;

    }

    @PostMapping("/loginByGitee")
    public AjaxResult loginByGitee(@RequestBody LoginByOtherSourceBody loginByOtherSourceBody) {
        AjaxResult ajax = AjaxResult.success();
        String token = loginService
                .loginByOtherSource(loginByOtherSourceBody.getCode(), loginByOtherSourceBody.getSource(), loginByOtherSourceBody.getUuid());
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }
}

