package com.colaclub.common.service;

import cn.hutool.json.JSONUtil;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Objects;

/**
 * 阿里云短信配置
 *
 * @author: cola639
 * @date: 2023/4/16 16:38
 */
@Slf4j
@Data
@Service
@ConfigurationProperties("aliyun.sms")
public class AliyunSmsService {

    private String smsAccessKeyId;
    private String smsAccessKeySecret;
    private String smsEndpoint;
    private String smsSignName;
    private String smsTemplateCode;
    private Client client;

    /**
     * 使用 AK&ASK 初始化账号 Client
     *
     * @param accessKeyId
     * @param accessKeySecret
     * @return Client
     * @throws Exception 短信推送异常
     */
    private Client createClient(String accessKeyId, String accessKeySecret, String endpoint) throws Exception {
        Config config = new Config().setAccessKeyId(accessKeyId).setAccessKeySecret(accessKeySecret);
        config.endpoint = endpoint;
        return new com.aliyun.dysmsapi20170525.Client(config);
    }

    @PostConstruct
    public void init() throws Exception {
        /** this指向 类AliyunSmsService 本身变量client */
        this.client = createClient(smsAccessKeyId, smsAccessKeySecret, smsEndpoint);
    }

    /**
     * 发送短信验证码
     *
     * @param phone 电话号码
     * @throws Exception 短信推送异常
     */
    public boolean sendCode(String phone, String code) throws Exception {
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setSignName(smsSignName)
                .setTemplateCode(smsTemplateCode)
                .setPhoneNumbers(phone)
                .setTemplateParam("{\"code\":\"" + code + "\"}");
        try {
            log.info("发送短信入参: " + JSONUtil.toJsonStr(sendSmsRequest));
            SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);
            log.info("发送短信结果: " + JSONUtil.toJsonStr(sendSmsResponse));

            if (Objects.nonNull(sendSmsResponse) && "OK".equals(sendSmsResponse.getBody().code)) {
                return true;
            }
        } catch (TeaException error) {
            // 如有需要，请打印 error
            log.error("短信推送异常结果: " + error.message);
            return false;
        } catch (Exception e) {
            TeaException error = new TeaException(e.getMessage(), e);
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
            log.error("短信推送异常结果: " + error.message);
            return true;
        }

        return Boolean.FALSE;
    }
}
