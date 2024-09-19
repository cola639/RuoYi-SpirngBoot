package com.colaclub.web.controller.system;

import com.colaclub.common.constant.Constants;
import com.colaclub.common.core.controller.BaseController;
import com.colaclub.common.core.domain.AjaxResult;
import com.colaclub.common.core.domain.model.RegisterBody;
import com.colaclub.common.core.redis.RedisCache;
import com.colaclub.framework.web.service.SysRegisterService;
import com.colaclub.system.service.ISysConfigService;
import com.colaclub.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 注册验证
 *
 * @author colaclub
 */
@RestController
public class SysRegisterController extends BaseController {
    @Autowired
    private SysRegisterService registerService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ISysUserService userService;

    @Resource
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public AjaxResult register(@RequestBody RegisterBody user) {
//        if (!("true".equals(configService.selectConfigByKey("sys.account.registerUser")))) {
//            return error("当前系统没有开启注册功能！");
//        }

//        try {
//            checkSmsCode(user.getMobile(), user.getSmsCode(), user.getSmsCode());
//        } catch (Exception e) {
//            AsyncManager.me().execute(AsyncFactory.recordLogininfor(user.getMobile(), Constants.REGISTER_FAIL, e.getMessage()));
//            throw new ServiceException(e.getMessage());
//        }

        String msg;
        String token;
        try {
            token = registerService.register(user);
        } catch (BadCredentialsException e) {
            // 设置错误消息
            msg = e.getMessage();
            return error(msg);
        }

        AjaxResult ajax = AjaxResult.success();
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

}
