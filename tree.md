RuoYi-SpringBoot
├── ruoyi-admin
│ ├── pom.xml
│ ├── src
│ │ └── main
│ │ ├── java
│ │ │ └── com
│ │ │ └── ruoyi
│ │ │ ├── RuoYiApplication.java
│ │ │ ├── RuoYiServletInitializer.java
│ │ │ └── web
│ │ │ ├── controller
│ │ │ │ ├── chat
│ │ │ │ │ └── UserChatController.java
│ │ │ │ ├── common
│ │ │ │ │ ├── CaptchaController.java
│ │ │ │ │ └── CommonController.java
│ │ │ │ ├── login
│ │ │ │ │ ├── GiteeLogin.java
│ │ │ │ │ └── LoginByOtherSourceBody.java
│ │ │ │ ├── monitor
│ │ │ │ │ ├── CacheController.java
│ │ │ │ │ ├── ServerController.java
│ │ │ │ │ ├── SysLogininforController.java
│ │ │ │ │ ├── SysOperlogController.java
│ │ │ │ │ └── SysUserOnlineController.java
│ │ │ │ ├── system
│ │ │ │ │ ├── SysConfigController.java
│ │ │ │ │ ├── SysDeptController.java
│ │ │ │ │ ├── SysDictDataController.java
│ │ │ │ │ ├── SysDictTypeController.java
│ │ │ │ │ ├── SysIndexController.java
│ │ │ │ │ ├── SysLoginController.java
│ │ │ │ │ ├── SysMenuController.java
│ │ │ │ │ ├── SysNoticeController.java
│ │ │ │ │ ├── SysPostController.java
│ │ │ │ │ ├── SysProfileController.java
│ │ │ │ │ ├── SysRegisterController.java
│ │ │ │ │ ├── SysRoleController.java
│ │ │ │ │ └── SysUserController.java
│ │ │ │ └── tool
│ │ │ │ ├── SwaggerController.java
│ │ │ │ └── TestController.java
│ │ │ └── core
│ │ │ └── config
│ │ │ └── SwaggerConfig.java
├── ruoyi-common
│ ├── pom.xml
│ ├── src
│ │ └── main
│ │ └── java
│ │ └── com
│ │ └── ruoyi
│ │ └── common
│ │ ├── annotation
│ │ │ ├── Anonymous.java
│ │ │ ├── DataScope.java
│ │ │ ├── DataSource.java
│ │ │ ├── Excel.java
│ │ │ ├── Excels.java
│ │ │ ├── Log.java
│ │ │ ├── RateLimiter.java
│ │ │ └── RepeatSubmit.java
│ │ ├── config
│ │ │ └── RuoYiConfig.java
│ │ ├── constant
│ │ │ ├── CacheConstants.java
│ │ │ ├── Constants.java
│ │ │ ├── GenConstants.java
│ │ │ ├── HttpStatus.java
│ │ │ ├── ScheduleConstants.java
│ │ │ └── UserConstants.java
│ │ ├── core
│ │ │ ├── controller
│ │ │ │ └── BaseController.java
│ │ │ ├── domain
│ │ │ │ ├── AjaxResult.java
│ │ │ │ ├── BaseEntity.java
│ │ │ │ ├── R.java
│ │ │ │ ├── TreeEntity.java
│ │ │ │ ├── TreeSelect.java
│ │ │ │ ├── entity
│ │ │ │ └── model
│ │ │ ├── page
│ │ │ │ ├── PageDomain.java
│ │ │ │ ├── TableDataInfo.java
│ │ │ │ └── TableSupport.java
│ │ │ ├── redis
│ │ │ │ └── RedisCache.java
│ │ │ └── text
│ │ │ ├── CharsetKit.java
│ │ │ ├── Convert.java
│ │ │ └── StrFormatter.java
│ │ ├── enums
│ │ │ ├── BusinessStatus.java
│ │ │ ├── BusinessType.java
│ │ │ ├── DataSourceType.java
│ │ │ ├── HttpMethod.java
│ │ │ ├── LimitType.java
│ │ │ ├── OperatorType.java
│ │ │ └── UserStatus.java
│ │ ├── exception
│ │ │ ├── DemoModeException.java
│ │ │ ├── GlobalException.java
│ │ │ ├── ServiceException.java
│ │ │ ├── UtilException.java
│ │ │ ├── base
│ │ │ │ └── BaseException.java
│ │ │ ├── file
│ │ │ │ ├── FileException.java
│ │ │ │ ├── FileNameLengthLimitExceededException.java
│ │ │ │ ├── FileSizeLimitExceededException.java
│ │ │ │ └── InvalidExtensionException.java
│ │ │ ├── job
│ │ │ │ └── TaskException.java
│ │ │ └── user
│ │ │ ├── CaptchaException.java
│ │ │ ├── CaptchaExpireException.java
│ │ │ ├── UserException.java
│ │ │ └── UserPasswordNotMatchException.java
│ │ ├── filter
│ │ │ ├── RepeatableFilter.java
│ │ │ ├── RepeatedlyRequestWrapper.java
│ │ │ ├── XssFilter.java
│ │ │ └── XssHttpServletRequestWrapper.java
│ │ ├── utils
│ │ │ ├── Arith.java
│ │ │ ├── DateUtils.java
│ │ │ ├── DictUtils.java
│ │ │ ├── ExceptionUtil.java
│ │ │ ├── LogUtils.java
│ │ │ ├── MessageUtils.java
│ │ │ ├── PageUtils.java
│ │ │ ├── SecurityUtils.java
│ │ │ ├── ServletUtils.java
│ │ │ ├── StringUtils.java
│ │ │ ├── Threads.java
│ │ │ ├── bean
│ │ │ │ ├── BeanUtils.java
│ │ │ │ └── BeanValidators.java
│ │ │ ├── file
│ │ │ │ ├── FileTypeUtils.java
│ │ │ │ ├── FileUploadUtils.java
│ │ │ │ ├── FileUtils.java
│ │ │ │ ├── ImageUtils.java
│ │ │ │ └── MimeTypeUtils.java
│ │ │ ├── html
│ │ │ │ ├── EscapeUtil.java
│ │ │ │ └── HTMLFilter.java
│ │ │ ├── http
│ │ │ │ ├── HttpHelper.java
│ │ │ │ └── HttpUtils.java
│ │ │ ├── ip
│ │ │ │ ├── AddressUtils.java
│ │ │ │ └── IpUtils.java
│ │ │ ├── poi
│ │ │ │ ├── ExcelHandlerAdapter.java
│ │ │ │ └── ExcelUtil.java
│ │ │ ├── reflect
│ │ │ │ └── ReflectUtils.java
│ │ │ ├── sign
│ │ │ │ ├── Base64.java
│ │ │ │ └── Md5Utils.java
│ │ │ ├── spring
│ │ │ │ └── SpringUtils.java
│ │ │ └── uuid
│ │ │ ├── IdUtils.java
│ │ │ ├── Seq.java
│ │ │ └── UUID.java
│ │ └── xss
│ │ ├── Xss.java
│ │ └── XssValidator.java
├── ruoyi-framework
│ ├── pom.xml
│ ├── src
│ │ └── main
│ │ └── java
│ │ └── com
│ │ └── ruoyi
│ │ └── framework
│ │ ├── aspectj
│ │ │ ├── DataScopeAspect.java
│ │ │ ├── DataSourceAspect.java
│ │ │ ├── LogAspect.java
│ │ │ └── RateLimiterAspect.java
│ │ ├── config
│ │ │ ├── ApplicationConfig.java
│ │ │ ├── CaptchaConfig.java
│ │ │ ├── DruidConfig.java
│ │ │ ├── FastJson2JsonRedisSerializer.java
│ │ │ ├── FilterConfig.java
│ │ │ ├── KaptchaTextCreator.java
│ │ │ ├── MyBatisConfig.java
│ │ │ ├── RedisConfig.java
│ │ │ ├── ResourcesConfig.java
│ │ │ ├── SecurityConfig.java
│ │ │ ├── ServerConfig.java
│ │ │ ├── ThreadPoolConfig.java
│ │ │ └── properties
│ │ │ ├── DruidProperties.java
│ │ │ └── PermitAllUrlProperties.java
│ │ ├── datasource
│ │ │ ├── DynamicDataSource.java
│ │ │ └── DynamicDataSourceContextHolder.java
│ │ ├── interceptor
│ │ │ ├── RepeatSubmitInterceptor.java
│ │ │ └── impl
│ │ │ └── SameUrlDataInterceptor.java
│ │ ├── manager
│ │ │ ├── AsyncManager.java
│ │ │ ├── ShutdownManager.java
│ │ │ └── factory
│ │ │ └── AsyncFactory.java
│ │ ├── security
│ │ │ ├── filter
│ │ │ │ └── JwtAuthenticationTokenFilter.java
│ │ │ └── handle
│ │ │ ├── AuthenticationEntryPointImpl.java
│ │ │ └── LogoutSuccessHandlerImpl.java
│ │ └── web
│ │ ├── domain
│ │ │ ├── Server.java
│ │ │ └── server
│ │ ├── exception
│ │ │ └── GlobalExceptionHandler.java
│ │ └── service
│ │ ├── PermissionService.java
│ │ ├── SysLoginService.java
│ │ ├── SysPermissionService.java
│ │ ├── SysRegisterService.java
│ │ ├── TokenService.java
│ │ └── UserDetailsServiceImpl.java
├── ruoyi-generator
│ ├── pom.xml
│ ├── src
│ │ └── main
│ │ ├── java
│ │ │ └── com
│ │ │ └── ruoyi
│ │ │ └── generator
│ │ │ ├── config
│ │ │ │ └── GenConfig.java
│ │ │ ├── controller
│ │ │ │ └── GenController.java
│ │ │ ├── domain
│ │ │ │ ├── GenTable.java
│ │ │ │ └── GenTableColumn.java
│ │ │ ├── mapper
│ │ │ │ ├── GenTableColumnMapper.java
│ │ │ │ └── GenTableMapper.java
│ │ │ ├── service
│ │ │ │ ├── GenTableColumnServiceImpl.java
│ │ │ │ ├── GenTableServiceImpl.java
│ │ │ │ ├── IGenTableColumnService.java
│ │ │ │ └── IGenTableService.java
│ │ │ └── util
│ │ │ ├── GenUtils.java
│ │ │ ├── VelocityInitializer.java
│ │ │ └── VelocityUtils.java
├── ruoyi-quartz
│ ├── pom.xml
│ ├── src
│ │ └── main
│ │ ├── java
│ │ │ └── com
│ │ │ └── ruoyi
│ │ │ └── quartz
│ │ │ ├── config
│ │ │ │ └── ScheduleConfig.java
│ │ │ ├── controller
│ │ │ │ ├── SysJobController.java
│ │ │ │ └── SysJobLogController.java
│ │ │ ├── domain
│ │ │ │ ├── SysJob.java
│ │ │ │ └── SysJobLog.java
│ │ │ ├── mapper
│ │ │ │ ├── SysJobLogMapper.java
│ │ │ │ └── SysJobMapper.java
│ │ │ ├── service
│ │ │ │ ├── ISysJobLogService.java
│ │ │ │ ├── ISysJobService.java
│ │ │ │ └── impl
│ │ │ │ ├── SysJobLogServiceImpl.java
│ │ │ │ └── SysJobServiceImpl.java
│ │ │ ├── task
│ │ │ │ └── RyTask.java
│ │ │ └── util
│ │ │ ├── AbstractQuartzJob.java
│ │ │ ├── CronUtils.java
│ │ │ ├── JobInvokeUtil.java
│ │ │ ├── QuartzDisallowConcurrentExecution.java
│ │ │ ├── QuartzJobExecution.java
│ │ │ └── ScheduleUtils.java
├── ruoyi-system
│ ├── pom.xml
│ ├── src
│ │ └── main
│ │ ├── java
│ │ │ └── com
│ │ │ └── ruoyi
│ │ │ ├── chat
│ │ │ │ ├── mapper
│ │ │ │ │ └── UserChatMapper.java
│ │ │ │ ├── model
│ │ │ │ │ └── UserChat.java
│ │ │ │ └── service
│ │ │ │ ├── IUserChatService.java
│ │ │ │ └── impl
│ │ │ │ └── UserChatServiceImpl.java
│ │ │ └── system
│ │ │ └── service
│ │ │ ├── ISysConfigService.java
│ │ │ ├── ISysDeptService.java
│ │ │ ├── ISysDictDataService.java
│ │ │ ├── ISysDictTypeService.java
│ │ │ ├── ISysLogininforService.java
│ │ │ ├── ISysMenuService.java
│ │ │ ├── ISysNoticeService.java
│ │ │ ├── ISysOperLogService.java
│ │ │ ├── ISysPostService.java
│ │ │ ├── ISysRoleService.java
│ │ │ ├── ISysUserOnlineService.java
│ │ │ ├── ISysUserService.java
│ │ │ └── impl
│ │ │ ├── SysConfigServiceImpl.java
│ │ │ ├── SysDeptServiceImpl.java
│ │ │ ├── SysDictDataServiceImpl.java
│ │ │ ├── SysDictTypeServiceImpl.java
│ │ │ ├── SysLogininforServiceImpl.java
│ │ │ ├── SysMenuServiceImpl.java
│ │ │ ├── SysNoticeServiceImpl.java
│ │ │ ├── SysOperLogServiceImpl.java
│ │ │ ├── SysPostServiceImpl.java
│ │ │ ├── SysRoleServiceImpl.java
│ │ │ ├── SysUserOnlineServiceImpl.java
│ │ │ ├── SysUserServiceImpl.java
│ │ │ ├── domain
│ │ │ └── mapper
