package com.colaclub.web.controller.system;

import cn.hutool.core.util.RandomUtil;
import com.colaclub.common.annotation.RateLimiter;
import com.colaclub.common.constant.Constants;
import com.colaclub.common.core.domain.AjaxResult;
import com.colaclub.common.core.domain.model.LoginBody;
import com.colaclub.common.core.redis.RedisCache;
import com.colaclub.common.enums.LimitType;
import com.colaclub.common.service.AliyunSmsService;
import com.colaclub.common.utils.uuid.IdUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Api("生成验证码接口")
@Slf4j
@Controller
public class GenerateSmsController {
    @Autowired
    private RedisCache redisCache;

    @Autowired
    private AliyunSmsService smsService;

    @PostMapping("/sms/code")
    @RateLimiter(time = 60, count = 1, limitType = LimitType.IP)
    @ResponseBody
    public AjaxResult sms(@RequestBody LoginBody loginBody) throws Exception {
        String mobile = loginBody.getMobile();

        // 保存验证码信息
        String uuid = IdUtils.simpleUUID();
        String verifyKey = Constants.SMS_CAPTCHA_CODE_KEY + uuid;
        String code = RandomUtil.randomNumbers(4);

        // 缓存验证码
        Map<String, Object> map = new HashMap<>(16);
        map.put("mobile", mobile);
        map.put("code", code);
        redisCache.setCacheObject(verifyKey, map, Constants.SMS_CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
        log.info(" 为 {} 设置短信验证码：{}", mobile, code);

        // 阿里云发送验证码通知
        smsService.sendCode(mobile, code);

        // 返回验证码对应uuid
        AjaxResult ajax = AjaxResult.success();
        ajax.put("uuid", uuid);
        return ajax;
    }
}
