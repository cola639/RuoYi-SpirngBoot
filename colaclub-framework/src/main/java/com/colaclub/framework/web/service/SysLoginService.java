package com.colaclub.framework.web.service;

import com.colaclub.common.constant.CacheConstants;
import com.colaclub.common.constant.Constants;
import com.colaclub.common.core.domain.AjaxResult;
import com.colaclub.common.core.domain.entity.SysUser;
import com.colaclub.common.core.domain.model.LoginUser;
import com.colaclub.common.core.redis.RedisCache;
import com.colaclub.common.exception.ServiceException;
import com.colaclub.common.exception.user.CaptchaException;
import com.colaclub.common.exception.user.CaptchaExpireException;
import com.colaclub.common.exception.user.UserPasswordNotMatchException;
import com.colaclub.common.utils.*;
import com.colaclub.common.utils.ip.IpUtils;
import com.colaclub.framework.config.ThirdLogins;
import com.colaclub.framework.manager.AsyncManager;
import com.colaclub.framework.manager.factory.AsyncFactory;
import com.colaclub.framework.smsConfig.SmsCodeAuthenticationToken;
import com.colaclub.system.service.ISysConfigService;
import com.colaclub.system.service.ISysUserService;
import com.xkcoding.http.config.HttpConfig;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthGiteeRequest;
import me.zhyd.oauth.request.AuthGithubRequest;
import me.zhyd.oauth.request.AuthRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 登录校验方法
 *
 * @author colaclub
 */
@Component
@Slf4j
public class SysLoginService {
    @Autowired
    private TokenService tokenService;

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private ThirdLogins thirdLogins;

    /**
     * 登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @param code     验证码
     * @param uuid     唯一标识
     * @return 结果
     */
    public String login(String username, String password, String code, String uuid) {
        boolean captchaOnOff = configService.selectCaptchaOnOff();
        // 验证码开关
        if (captchaOnOff) {
            validateCaptcha(username, code, uuid);
        }

        /** 异步工厂和线程池 1 主线程验证用户信息 异步记录登录成功或失败信息 2 主线程继续执行后续操作 如获取用户信息 记录登录信息 生成token等 */
        // 用户验证
        Authentication authentication = null;
        try {
            // authenticationManager.authenticate(...)，该过程成功后会将 Authentication 对象保存到 SecurityContextHolder 中
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            if (e instanceof BadCredentialsException) {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
                throw new UserPasswordNotMatchException();
            } else {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, e.getMessage()));
                throw new ServiceException(e.getMessage());
            }
        }
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
        // 此处与第三方区别
        // 获取已认证用户信息
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        log.info("loginUser:{}", loginUser);
        // 记录或更新sys_user表用户登录信息
        recordLoginInfo(loginUser.getUserId());
        // 生成token
        return tokenService.createToken(loginUser);
    }

    /**
     * 校验验证码
     *
     * @param username 用户名
     * @param code     验证码
     * @param uuid     唯一标识
     * @return 结果
     */
    public void validateCaptcha(String username, String code, String uuid) {
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);
        if (captcha == null) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire")));
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha)) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error")));
            throw new CaptchaException();
        }
    }

    /**
     * sys_user表 记录登录信息或更新用户登录信息
     *
     * @param userId 用户ID
     */
    public void recordLoginInfo(Long userId) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setLoginIp(IpUtils.getIpAddr(ServletUtils.getRequest()));
        sysUser.setLoginDate(DateUtils.getNowDate());
        userService.updateUserProfile(sysUser);
    }

    /**
     * 扩展第三方登录
     *
     * @param code   用户授权
     * @param source 用户来源平台
     * @param uuid   会话uuid
     */
    public String loginByGitee(String code, String source, String uuid) {
        // 输出传入参数的值，用于跟踪代码执行
        log.info("code: '{}' source: '{}' uuid: '{}' ", code, source, uuid);

        // 创建授权request，包括clientId, clientSecret, redirectUri，这些通常在 OAuth 认证中使用
        System.out.println("GiteeClientId GiteeClientSecret getGiteeRedirectURL " + thirdLogins.getGiteeClientId() + thirdLogins.getGiteeClientSecret() + thirdLogins.getGiteeRedirectURL());
        AuthRequest authRequest = new AuthGiteeRequest(AuthConfig.builder().clientId(thirdLogins.getGiteeClientId()).clientSecret(thirdLogins.getGiteeClientSecret()).redirectUri(thirdLogins.getGiteeRedirectURL()).build());

        // 通过AuthRequest对象执行登录，获取AuthResponse
        AuthResponse<AuthUser> login = authRequest.login(AuthCallback.builder().state(uuid).code(code).build());

        // 从AuthResponse中获取AuthUser数据
        AuthUser authUser = login.getData();
        // 创建SysUser对象，并将AuthUser的username和source设定到SysUser
        SysUser sysUser = new SysUser();
        sysUser.setUserName(authUser.getUsername());
        sysUser.setSource(authUser.getSource());
        // 输出sysUser的信息
        log.info("sysUser: '{}' ", sysUser);

        // 查询数据库中是否有匹配的SysUser
        List<SysUser> sysUsers = userService.selectUserListNoDataScope(sysUser);
        // 判断匹配的SysUser数量，如果有多个则抛出异常，如果没有则注册新用户，如果只有一个则直接获取
        if (sysUsers.size() > 1) {
            throw new ServiceException("第三方登录异常，账号重叠");
        } else if (sysUsers.size() == 0) {
            // 如果没有找到匹配的SysUser，则注册新用户
            sysUser.setNickName(authUser.getNickname());
            sysUser.setAvatar(authUser.getAvatar());
            sysUser.setEmail(authUser.getEmail());
            sysUser.setRemark("");
            sysUser.setPassword(SecurityUtils.encryptPassword("123456"));
            sysUser.setRemark("Gitee授权登录用户");
            sysUser.setCreateBy("third_gite_token");
            sysUser.setDeptId(105L);
            sysUser.setCreateTime(new Date());
            sysUser.setRoleIds(new Long[]{4L});
            try {
                // 注册新用户并获取新的userId

                Long newUserId = userService.registerUserAndGetUserId(sysUser);
                System.out.println("newUserId" + newUserId);
                // 记录注册信息
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(sysUser.getUserName(), Constants.REGISTER, MessageUtils.message("user.register.success")));

                // 如果没有异常被抛出，那么用户注册成功，可以进行下一步
                Long[] roleIds = new Long[]{4L};
                userService.insertUserAuth(newUserId, roleIds);
                Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authUser.getUsername(), 123456));

                // 记录登录成功的信息
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(sysUser.getUserName(), Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));

                LoginUser loginUser = (LoginUser) authentication.getPrincipal();

                recordLoginInfo(loginUser.getUserId());
                // 生成token
                return tokenService.createToken(loginUser);

            } catch (Exception e) {
                // 如果有异常被抛出，那么用户注册失败，处理异常...
                e.printStackTrace();
            }

        } else {
            // 如果找到了一个匹配的SysUser，则获取该SysUser
            sysUser = sysUsers.get(0);
        }

        // 日志 记录登录成功的信息
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(sysUser.getUserName(), Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));

        // 创建LoginUser对象，这通常包含用户的ID、部门ID、SysUser信息和用户权限信息
        LoginUser loginUser = new LoginUser(sysUser.getUserId(), sysUser.getDeptId(), sysUser, permissionService.getMenuPermission(sysUser));

        // 记录登录用户的信息
        recordLoginInfo(loginUser.getUserId());

        // 生成token，并返回token值
        return tokenService.createToken(loginUser);
    }

    /**
     * 扩展第三方登录
     *
     * @param code   用户授权
     * @param source 用户来源平台
     * @param uuid   会话uuid
     */
    public String loginByGithub(String code, String source, String uuid) {
        log.info("code: '{}' source: '{}' uuid: '{}' ", code, source, uuid);

        try {
            // 验证 uuid 的有效性（假设您已经在会话中保存了这个 uuid）
            //            if (!isValidUuid(uuid)) {
            //                throw new ServiceException("无效的请求");
            //            }

            AuthRequest authRequest = new AuthGithubRequest(AuthConfig.builder()
                    .clientId(thirdLogins.getGithubClientId())
                    .clientSecret(thirdLogins.getGithubClientSecret())
                    .redirectUri(thirdLogins.getGithubRedirectURL())
                    // 针对国外平台配置代理
                    .httpConfig(HttpConfig.builder()
                            .timeout(30 * 1000)  // 增加超时时间到30秒
                            .build())
                    .build());

            AuthResponse<AuthUser> authResponse = authRequest.login(AuthCallback.builder().state(uuid).code(code).build());
            log.info("authResponse code: {}, msg: {}, data: {}", authResponse.getCode(), authResponse.getMsg(), authResponse.getData());
            if (authResponse.getCode() != 2000) {
                throw new ServiceException("GitHub 登录失败：" + authResponse.getMsg());
            }

            AuthUser authUser = authResponse.getData();
            SysUser sysUser = findOrCreateUser(authUser);

            // 创建和返回 JWT Token
            LoginUser loginUser = new LoginUser(sysUser.getUserId(), sysUser.getDeptId(), sysUser, permissionService.getMenuPermission(sysUser));
            recordLoginInfo(loginUser.getUserId());

            return tokenService.createToken(loginUser);

        } catch (Exception e) {
            log.error("GitHub 登录过程中出现错误", e);
            throw new ServiceException("登录失败，请稍后再试。");
        }
    }

    private SysUser findOrCreateUser(AuthUser authUser) throws ServiceException {
        // 打印获取到的用户信息
        log.info("获取到的用户信息: {}", authUser);
        SysUser sysUser = new SysUser();
        sysUser.setUserName(authUser.getUsername());
        sysUser.setSource(authUser.getSource());

        List<SysUser> sysUsers = userService.selectUserListNoDataScope(sysUser);

        if (sysUsers.size() > 1) {
            throw new ServiceException("第三方登录异常，账号重叠");
        } else if (sysUsers.isEmpty()) {
            sysUser.setNickName(authUser.getUsername());
            sysUser.setAvatar(authUser.getAvatar());
            sysUser.setEmail(authUser.getEmail());
            sysUser.setPassword(SecurityUtils.encryptPassword("123456"));  // 在生产环境中应更加安全处理
            sysUser.setCreateBy("third_github_token");
            sysUser.setDeptId(105L);
            sysUser.setCreateTime(new Date());
            sysUser.setRoleIds(new Long[]{4L});

            Long newUserId = userService.registerUserAndGetUserId(sysUser);
            userService.insertUserAuth(newUserId, new Long[]{4L});
            return sysUser;
        } else {
            return sysUsers.get(0);
        }
    }

    /**
     * 手机号登录验证
     *
     * @param mobile 手机号
     * @param code   验证码
     * @param uuid   唯一标识
     * @return 结果
     */
    public AjaxResult smsLogin(String phone, String code, String uuid) {

        // 用户验证
        Authentication authentication = null;
        try {
            checkSmsCode(phone, code, uuid);

            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager.authenticate(new SmsCodeAuthenticationToken(phone));
        } catch (Exception e) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(phone, Constants.LOGIN_FAIL, e.getMessage()));
            throw new ServiceException(e.getMessage());
        }
        // 异步日志记录
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(phone, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
        // 获取用户信息
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        SysUser user = userService.selectUserByPhone(phone);
        loginUser.setUserId(user.getUserId());
        // 生成token
        String token = tokenService.createToken(loginUser);

        // 记录用户登录信息
        // recordLoginInfo(loginUser.getUserId());

        AjaxResult ajax = AjaxResult.success();
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 检查手机号登录验证码
     *
     * @param
     */
    private void checkSmsCode(String phone, String inputCode, String uuid) {
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

        String verifyKey = Constants.SMS_CAPTCHA_CODE_KEY + uuid;
        Map<String, Object> smsCode = redisCache.getCacheObject(verifyKey);

        if (smsCode == null) {
            throw new BadCredentialsException("验证码失效");
        }

        String applyPhone = (String) smsCode.get("phone");
        String codeStr = (String) smsCode.get("code");

        // codeStr转为数字 并检查 codeStr 是否为空或非数字
        int code;
        try {
            code = Integer.parseInt(codeStr);
        } catch (NumberFormatException e) {
            throw new BadCredentialsException("验证码数据格式不正确");
        }

        if (!applyPhone.equals(phone)) {
            throw new BadCredentialsException("手机号码不一致");
        }
        if (code != inputCodeInt) {
            throw new BadCredentialsException("验证码不一致");
        }
    }
}
