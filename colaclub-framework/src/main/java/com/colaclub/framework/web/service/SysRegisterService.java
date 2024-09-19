package com.colaclub.framework.web.service;

import com.colaclub.common.constant.Constants;
import com.colaclub.common.core.domain.entity.SysUser;
import com.colaclub.common.core.domain.model.LoginUser;
import com.colaclub.common.core.domain.model.RegisterBody;
import com.colaclub.common.core.redis.RedisCache;
import com.colaclub.common.exception.ServiceException;
import com.colaclub.common.utils.MessageUtils;
import com.colaclub.common.utils.SecurityUtils;
import com.colaclub.common.utils.StringUtils;
import com.colaclub.framework.manager.AsyncManager;
import com.colaclub.framework.manager.factory.AsyncFactory;
import com.colaclub.framework.smsConfig.SmsCodeAuthenticationToken;
import com.colaclub.system.service.ISysConfigService;
import com.colaclub.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.Random;

/**
 * 注册校验方法
 *
 * @author colaclub
 */
@Slf4j
@Component
public class SysRegisterService {
    // 性格数组
    private static final String[] CHARACTERS = {
            "温柔", "勇敢", "聪明", "调皮", "机智", "可爱", "善良", "坚强", "乐观", "幽默"
    };
    // 故事人物数组
    private static final String[] STORY_CHARACTERS = {
            "小王子", "灰姑娘", "白雪公主", "小红帽", "哈利波特", "爱丽丝", "匹诺曹", "彼得潘", "绿野仙踪", "长发公主"
    };

    @Autowired
    private ISysUserService userService;

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private TokenService tokenService;

    private static String getRandomNickname() {
        Random random = new Random();

        // 随机选择一个性格
        String character = CHARACTERS[random.nextInt(CHARACTERS.length)];

        // 随机选择一个故事人物
        String storyCharacter = STORY_CHARACTERS[random.nextInt(STORY_CHARACTERS.length)];

        // 组合成昵称
        return character + storyCharacter;
    }

    public String register(RegisterBody registerBody) {
        String phone = registerBody.getPhone();
        String code = registerBody.getSmsCode();
        String uuid = registerBody.getUuid();
        String token = null;

        try {
            validateSmsCode(phone, code, uuid);
            token = createUser(phone); // 创建用户并生成token
        } catch (BadCredentialsException e) {
            // 处理异常，比如设置错误消息
            throw new ServiceException(e.getMessage());
        }

        return token; // 返回生成的token
    }

    /**
     * 创建用户
     *
     * @param
     */
    private String createUser(String phone) throws ServiceException {
        // 打印获取到的用户信息
        log.info("获取到的用户手机号: {}", phone);
        SysUser user = userService.selectUserByPhone(phone);

        if (user != null) {
            throw new ServiceException("手机号注册异常，账号已存在");
        } else {
            SysUser sysUser = new SysUser();
            String nickName = getRandomNickname(); // 随机生成昵称
            String password = RandomStringUtils.randomAlphanumeric(8); // 随机生成密码
            sysUser.setUserName(phone);
            sysUser.setSource("phone");
            sysUser.setPhonenumber(phone);
            sysUser.setNickName(nickName);
            sysUser.setPassword(SecurityUtils.encryptPassword(password));  // 在生产环境中应更加安全处理
            sysUser.setCreateBy("phone");       // 第三方登录
            sysUser.setRoleIds(new Long[]{4L});  // 默认为普通用户 用enums代替
            sysUser.setDeptId(105L);             // deptId 默认为100
            sysUser.setCreateTime(new Date());

            Long newUserId = userService.registerUserAndGetUserId(sysUser);
            userService.insertUserAuth(newUserId, new Long[]{4L});

        }

        // 用户验证
        Authentication authentication = null;
        authentication = authenticationManager.authenticate(new SmsCodeAuthenticationToken(phone));

        // 异步日志记录
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(phone, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));

        // 获取用户信息
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        user = userService.selectUserByPhone(phone);
        loginUser.setUserId(user.getUserId());

        // 生成token
        String token = tokenService.createToken(loginUser);
        return token;
    }

    /**
     * 检查验证码是否失效
     *
     * @param
     */
    private void validateSmsCode(String phone, String inputCode, String uuid) {
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

        String verifyKey = Constants.SMS_REGISTER_CODE_KEY + uuid;
        Map<String, Object> info = redisCache.getCacheObject(verifyKey);

        if (info == null) {
            throw new BadCredentialsException("验证码失效");
        }

        String applyPhone = (String) info.get("phone");
        String codeStr = (String) info.get("code");

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
