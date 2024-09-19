package com.colaclub.web.controller.demo;

import cn.hutool.core.util.RandomUtil;
import com.colaclub.common.annotation.Anonymous;
import com.colaclub.common.core.controller.BaseController;
import com.colaclub.common.core.domain.AjaxResult;
import com.colaclub.common.service.AliyunSmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
public class SmsController extends BaseController {

    @Autowired
    private AliyunSmsService smsService;

    @Anonymous
    @GetMapping("/send")
    public AjaxResult send(String phone) throws Exception {
        return toAjax(smsService.sendCode(phone, RandomUtil.randomNumbers(4), "smsTemplateCode"));
    }
}

