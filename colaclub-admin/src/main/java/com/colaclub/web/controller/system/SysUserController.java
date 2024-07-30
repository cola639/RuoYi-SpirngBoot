package com.colaclub.web.controller.system;

import cn.hutool.core.util.RandomUtil;
import com.colaclub.common.annotation.Log;
import com.colaclub.common.annotation.RateLimiter;
import com.colaclub.common.constant.Constants;
import com.colaclub.common.constant.UserConstants;
import com.colaclub.common.core.controller.BaseController;
import com.colaclub.common.core.domain.AjaxResult;
import com.colaclub.common.core.domain.entity.SysRole;
import com.colaclub.common.core.domain.entity.SysUser;
import com.colaclub.common.core.domain.model.LoginBody;
import com.colaclub.common.core.page.TableDataInfo;
import com.colaclub.common.core.redis.RedisCache;
import com.colaclub.common.enums.BusinessType;
import com.colaclub.common.enums.LimitType;
import com.colaclub.common.exception.ServiceException;
import com.colaclub.common.service.AliyunSmsService;
import com.colaclub.common.utils.SecurityUtils;
import com.colaclub.common.utils.StringUtils;
import com.colaclub.common.utils.poi.ExcelUtil;
import com.colaclub.common.utils.uuid.IdUtils;
import com.colaclub.framework.manager.AsyncManager;
import com.colaclub.framework.manager.factory.AsyncFactory;
import com.colaclub.framework.web.service.SysPasswordService;
import com.colaclub.system.service.ISysPostService;
import com.colaclub.system.service.ISysRoleService;
import com.colaclub.system.service.ISysUserService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户信息
 *
 * @author colaclub
 */
@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController {
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysPostService postService;

    @Autowired
    private AliyunSmsService smsService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private SysPasswordService pwdService;

    /**
     * 获取用户列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysUser user) {
        startPage();
        List<SysUser> list = userService.selectUserList(user);
        return getDataTable(list);
    }

    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:user:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysUser user) {
        List<SysUser> list = userService.selectUserList(user);
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.exportExcel(response, list, "用户数据");
    }

    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('system:user:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        List<SysUser> userList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = userService.importUser(userList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.importTemplateExcel(response, "用户数据");
    }

    /**
     * 根据用户编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping(value = {"/", "/{userId}"})
    public AjaxResult getInfo(@PathVariable(value = "userId", required = false) Long userId) {
        userService.checkUserDataScope(userId);
        AjaxResult ajax = AjaxResult.success();
        List<SysRole> roles = roleService.selectRoleAll();
        ajax.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        ajax.put("posts", postService.selectPostAll());
        if (StringUtils.isNotNull(userId)) {
            SysUser sysUser = userService.selectUserById(userId);
            ajax.put(AjaxResult.DATA_TAG, sysUser);
            ajax.put("postIds", postService.selectPostListByUserId(userId));
            ajax.put("roleIds", sysUser.getRoles().stream().map(SysRole::getRoleId).collect(Collectors.toList()));
        }
        return ajax;
    }

    /**
     * 新增用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysUser user) {
        if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(user.getUserName()))) {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getPhonenumber()) && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail()) && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setCreateBy(getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return toAjax(userService.insertUser(user));
    }

    /**
     * 修改用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysUser user) {

        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        if (StringUtils.isNotEmpty(user.getPhonenumber()) && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return AjaxResult.error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail()) && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return AjaxResult.error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUpdateBy(getUsername());

        System.out.printf("edit user >> " + user);
        return toAjax(userService.updateUser(user));
    }

    /**
     * 删除用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds) {
        if (ArrayUtils.contains(userIds, getUserId())) {
            return error("当前用户不能删除");
        }
        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     */
    @PreAuthorize("@ss.hasPermi('system:user:resetPwd')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public AjaxResult resetPwd(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(getUsername());
        return toAjax(userService.resetPwd(user));
    }

    @ResponseBody
    @RateLimiter(key = "resetPwd", time = 1000, count = 1, limitType = LimitType.IP)
    @PostMapping("/resetPwd/sms")
    public AjaxResult sms(@RequestBody LoginBody loginBody) throws Exception {
        String mobile = loginBody.getMobile();

        // 保存验证码信息
        String uuid = IdUtils.simpleUUID();
        String verifyKey = Constants.SMS_RESET_CODE_KEY + uuid;
        String code = RandomUtil.randomNumbers(4);

        // 缓存验证码
        Map<String, Object> map = new HashMap<>(16);
        map.put("mobile", mobile);
        map.put("code", code);
        redisCache.setCacheObject(verifyKey, map, Constants.SMS_RESET_EXPIRATION, TimeUnit.MINUTES);
        logger.info(" 为 {} 设置短信验证码：{}", mobile, code);

        // 阿里云发送验证码通知
        smsService.sendCode(mobile, code);

        // 返回验证码对应uuid
        AjaxResult ajax = AjaxResult.success();
        ajax.put("uuid", uuid);
        return ajax;
    }

    /**
     * 验证码重置密码
     */
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwdByCode")
    public AjaxResult resetPwd(@RequestBody LoginBody loginBody) {
        String mobile = loginBody.getMobile();
        String code = loginBody.getCode();
        String uuid = loginBody.getUuid();
        String password = loginBody.getPassword();

        try {
            pwdService.smsResetPwd(mobile, code, uuid, password);
            return AjaxResult.success();
        } catch (ServiceException e) {
            // 记录错误日志
            logger.error("重置密码失败，手机号：{}，错误信息：{}", mobile, e.getMessage());
            // 记录登录失败信息
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(mobile, Constants.RESET_FAIL, e.getMessage()));
            return AjaxResult.error(e.getMessage());
        } catch (Exception e) {
            // 记录未知错误日志
            logger.error("重置密码过程中发生未知错误，手机号：{}，错误信息：{}", mobile, e.getMessage());
            return AjaxResult.error("重置密码失败，请稍后再试");
        }
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        user.setUpdateBy(getUsername());
        return toAjax(userService.updateUserStatus(user));
    }

    /**
     * 根据用户编号获取授权角色
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping("/authRole/{userId}")
    public AjaxResult authRole(@PathVariable("userId") Long userId) {
        AjaxResult ajax = AjaxResult.success();
        SysUser user = userService.selectUserById(userId);
        List<SysRole> roles = roleService.selectRolesByUserId(userId);
        ajax.put("user", user);
        ajax.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        return ajax;
    }

    /**
     * 用户授权角色
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.GRANT)
    @PutMapping("/authRole")
    public AjaxResult insertAuthRole(Long userId, Long[] roleIds) {
        userService.checkUserDataScope(userId);
        userService.insertUserAuth(userId, roleIds);
        return success();
    }
}
