package com.colaclub.web.controller.thirdlogin;

import com.colaclub.common.constant.Constants;
import com.colaclub.common.core.domain.AjaxResult;
import com.colaclub.common.utils.uuid.IdUtils;
import com.colaclub.framework.config.ThirdLogins;
import com.colaclub.framework.web.service.SysLoginService;
import com.colaclub.thirdlogin.LoginByOtherSourceBody;
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

  @Autowired private SysLoginService loginService;

  @Autowired private ThirdLogins thirdLogins;

  @GetMapping("/PreLoginByGitee")
  public AjaxResult PreLoginByGitee() {
    AjaxResult ajax = AjaxResult.success();
    AuthRequest authRequest =
        new AuthGiteeRequest(
            AuthConfig.builder()
                .clientId(thirdLogins.getGiteeClientId())
                .clientSecret(thirdLogins.getGiteeClientSecret())
                .redirectUri(thirdLogins.getGiteeRedirectURL())
                .build());

    String uuid = IdUtils.fastUUID();
    System.out.println("uuid" + uuid);

    String authorizeUrl = authRequest.authorize(uuid); // 将 UUID 作为参数添加到回调 URL
    // 存储
    ajax.put("authorizeUrl", authorizeUrl);
    ajax.put("uuid", uuid);
    return ajax;
  }

  @PostMapping("/loginByGitee")
  public AjaxResult loginByGitee(@RequestBody LoginByOtherSourceBody loginByOtherSourceBody) {
    AjaxResult ajax = AjaxResult.success();
    String token =
        loginService.loginByOtherSource(
            loginByOtherSourceBody.getCode(),
            loginByOtherSourceBody.getSource(),
            loginByOtherSourceBody.getUuid());
    ajax.put(Constants.TOKEN, token);
    return ajax;
  }
}
