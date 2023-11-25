package com.colaclub.framework.web.service;

import com.colaclub.common.constant.CacheConstants;
import com.colaclub.common.constant.Constants;
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
import com.colaclub.system.service.ISysConfigService;
import com.colaclub.system.service.ISysUserService;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthGiteeRequest;
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

/**
 * 登录校验方法
 *
 * @author 大帅和
 */
@Component
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

        // 用户验证
        Authentication authentication = null;
        try {
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
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
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        System.out.println("loginUser" + loginUser);
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
     * 记录登录信息
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
    public String loginByOtherSource(String code, String source, String uuid) {
        // 输出传入参数的值，用于跟踪代码执行
        System.out.println("code  " + code + " source " + source + " uuid " + uuid);

        // 创建授权request，包括clientId, clientSecret, redirectUri，这些通常在 OAuth 认证中使用
        System.out.println("GiteeClientId GiteeClientSecret getGiteeRedirectURL " + thirdLogins.getGiteeClientId() + thirdLogins.getGiteeClientSecret() + thirdLogins.getGiteeRedirectURL());
        AuthRequest authRequest = new AuthGiteeRequest(AuthConfig.builder()
                .clientId(thirdLogins.getGiteeClientId())
                .clientSecret(thirdLogins.getGiteeClientSecret())
                .redirectUri(thirdLogins.getGiteeRedirectURL())
                .build());

        // 通过AuthRequest对象执行登录，获取AuthResponse
        AuthResponse<AuthUser> login = authRequest.login(AuthCallback.builder().state(uuid).code(code).build());
        // 输出登录结果
        System.out.println("login" + login);

        // 从AuthResponse中获取AuthUser数据
        AuthUser authUser = login.getData();
        // 创建SysUser对象，并将AuthUser的username和source设定到SysUser
        SysUser sysUser = new SysUser();
        sysUser.setUserName(authUser.getUsername());
        sysUser.setSource(authUser.getSource());
        // 输出sysUser的信息
        System.out.println("sysUser" + sysUser);

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
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(sysUser.getUserName(), Constants.REGISTER,
                        MessageUtils.message("user.register.success")));

                // 如果没有异常被抛出，那么用户注册成功，可以进行下一步
                Long[] roleIds = new Long[]{4L};
                userService.insertUserAuth(newUserId, roleIds);
                Authentication authentication = authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(authUser.getUsername(), 123456));

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

        // 记录登录成功的信息
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(sysUser.getUserName(), Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));

        // 输出permissionService的信息
        System.out.println("permissionService: " + permissionService);

        // 创建LoginUser对象，这通常包含用户的ID、部门ID、SysUser信息和用户权限信息
        LoginUser loginUser = new LoginUser(sysUser.getUserId(), sysUser.getDeptId(), sysUser, permissionService.getMenuPermission(sysUser));

        // 记录登录用户的信息
        recordLoginInfo(loginUser.getUserId());

        // 生成token，并返回token值
        return tokenService.createToken(loginUser);
    }

}
