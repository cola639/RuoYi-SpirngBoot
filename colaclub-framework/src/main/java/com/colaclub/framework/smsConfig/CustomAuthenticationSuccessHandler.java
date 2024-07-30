package com.colaclub.framework.smsConfig;

import com.colaclub.common.constant.Constants;
import com.colaclub.common.core.domain.AjaxResult;
import com.colaclub.common.core.domain.model.LoginUser;
import com.colaclub.framework.web.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
  @Autowired TokenService tokenService;
  @Autowired private ObjectMapper objectMapper;
  private Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {
    logger.info("登录成功");

    AjaxResult ajax = AjaxResult.success();
    LoginUser loginUser = (LoginUser) authentication.getPrincipal();
    String token = tokenService.createToken(loginUser);
    ajax.put(Constants.TOKEN, token);

    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().write(objectMapper.writeValueAsString(ajax));
  }
}
