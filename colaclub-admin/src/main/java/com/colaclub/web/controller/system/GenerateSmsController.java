package com.colaclub.web.controller.system;

import com.colaclub.common.constant.Constants;
import com.colaclub.common.core.domain.AjaxResult;
import com.colaclub.common.core.domain.model.LoginBody;
import com.colaclub.common.core.redis.RedisCache;
import com.colaclub.common.utils.uuid.IdUtils;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Api("生成验证码接口")
@Controller
public class GenerateSmsController {
    @Autowired
    private RedisCache redisCache;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @PostMapping("/sms/code")
    @ResponseBody
    public AjaxResult sms(@RequestBody LoginBody loginBody) {

        String mobile = loginBody.getMobile();
        // 保存验证码信息
        String uuid = IdUtils.simpleUUID();
        String verifyKey = Constants.SMS_CAPTCHA_CODE_KEY + uuid;

        int code = (int) Math.ceil(Math.random() * 9000 + 1000);
        // TODO：阿里云验证码通知
        Map<String, Object> map = new HashMap<>(16);
        map.put("mobile", mobile);
        map.put("code", code);

        redisCache.setCacheObject(verifyKey, map, Constants.SMS_EXPIRATION, TimeUnit.MINUTES);

        logger.info(" 为 {} 设置短信验证码：{}", mobile, code);

        AjaxResult ajax = AjaxResult.success();
        ajax.put("uuid", uuid);
        return ajax;
    }
}
