package com.colaclub.web.controller.thirdlogin;

import com.colaclub.chat.domain.bo.LoginByOtherSourceBody;
import com.colaclub.chat.domain.bo.WechatBody;
import com.colaclub.common.annotation.Anonymous;
import com.colaclub.common.constant.Constants;
import com.colaclub.common.core.domain.AjaxResult;
import com.colaclub.framework.config.ThirdLogins;
import com.colaclub.framework.web.service.SysLoginService;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class WechatLogin {

    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ThirdLogins thirdLogins;

    @Anonymous
    @PostMapping("/preLoginByWechat")
    public AjaxResult PreLoginByWechat(@RequestBody WechatBody wechatBody) {
        AjaxResult ajax = AjaxResult.success();

        String url = String.format("%s?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                thirdLogins.getWechatUrl(), thirdLogins.getWechatAppId(), thirdLogins.getWechatAppSecret(), wechatBody.getCode());

        System.out.println("url " + url);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            try {
                // 获取并打印状态码
                System.out.println("Response Status: " + response.getStatusLine());

                // 获取并打印响应体
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String responseBody = EntityUtils.toString(entity);
                    System.out.println("Response Body: " + responseBody);
                }
            } finally {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ajax.put(Constants.TOKEN, "123456");
        return ajax;

    }

    @PostMapping("/loginByWechat")
    public AjaxResult loginByGitee(@RequestBody LoginByOtherSourceBody loginByOtherSourceBody) {
        AjaxResult ajax = AjaxResult.success();
        String token = loginService
                .loginByOtherSource(loginByOtherSourceBody.getCode(), loginByOtherSourceBody.getSource(), loginByOtherSourceBody.getUuid());
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

}

