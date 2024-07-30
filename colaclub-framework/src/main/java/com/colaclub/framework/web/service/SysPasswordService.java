package com.colaclub.framework.web.service;

import com.colaclub.common.constant.Constants;
import com.colaclub.common.core.domain.entity.SysUser;
import com.colaclub.common.core.redis.RedisCache;
import com.colaclub.common.exception.ServiceException;
import com.colaclub.common.utils.StringUtils;
import com.colaclub.framework.manager.AsyncManager;
import com.colaclub.framework.manager.factory.AsyncFactory;
import com.colaclub.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class SysPasswordService {
    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ISysUserService userService;

    /**
     * 验证码修改密码
     *
     * @param mobile   手机号
     * @param code     验证码
     * @param uuid     唯一标识
     * @param password 新密码
     * @return 结果
     */
    public boolean smsResetPwd(String mobile, String code, String uuid, String password) {
        try {
            checkSmsRestCode(mobile, code, uuid);
            updatePassword(mobile, password);
        } catch (Exception e) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(mobile, Constants.LOGIN_FAIL, e.getMessage()));
            throw new ServiceException(e.getMessage());
        }

        return true;
    }

    private void checkSmsRestCode(String mobile, String inputCode, String uuid) {
        String verifyKey = Constants.SMS_RESET_CODE_KEY + uuid;
        Map<String, Object> smsCode = redisCache.getCacheObject(verifyKey);

        // inputCode 转换为 int类型，首先检查 inputCode 是否为空或非数字
        if (StringUtils.isEmpty(inputCode)) {
            throw new BadCredentialsException("验证码不能为空");
        }

        int inputCodeInt;
        try {
            inputCodeInt = Integer.parseInt(inputCode);
        } catch (NumberFormatException e) {
            throw new BadCredentialsException("验证码格式不正确");
        }

        if (smsCode == null) {
            throw new BadCredentialsException("验证码失效");
        }

        String applyMobile = (String) smsCode.get("mobile");
        String codeStr = (String) smsCode.get("code");

        // codeStr转为数字 并检查 codeStr 是否为空或非数字
        int code;
        try {
            code = Integer.parseInt(codeStr);
        } catch (NumberFormatException e) {
            throw new BadCredentialsException("验证码数据格式不正确");
        }

        if (!applyMobile.equals(mobile)) {
            throw new BadCredentialsException("手机号码不一致");
        }
        if (code != inputCodeInt) {
            throw new BadCredentialsException("验证码不一致");
        }
    }

    private void updatePassword(String mobile, String password) {
        SysUser user = userService.selectUserByPhone(mobile);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        // 更新用户密码
        user.setPassword(password);
        userService.updateUser(user);
    }
}
