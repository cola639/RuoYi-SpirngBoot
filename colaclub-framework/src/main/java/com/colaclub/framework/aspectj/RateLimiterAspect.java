package com.colaclub.framework.aspectj;

import com.colaclub.common.annotation.RateLimiter;
import com.colaclub.common.enums.LimitType;
import com.colaclub.common.exception.ServiceException;
import com.colaclub.common.utils.SecurityUtils;
import com.colaclub.common.utils.ServletUtils;
import com.colaclub.common.utils.StringUtils;
import com.colaclub.common.utils.ip.IpUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * 限流处理
 *
 * @author colaclub
 */
@Aspect
@Component
public class RateLimiterAspect {
    private static final Logger log = LoggerFactory.getLogger(RateLimiterAspect.class);

    private RedisTemplate<Object, Object> redisTemplate;

    private RedisScript<Long> limitScript;

    @Autowired
    public void setRedisTemplate1(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Autowired
    public void setLimitScript(RedisScript<Long> limitScript) {
        this.limitScript = limitScript;
    }

    @Before("@annotation(rateLimiter)")
    public void doBefore(JoinPoint point, RateLimiter rateLimiter) throws Throwable {
        String key = rateLimiter.key();
        int time = rateLimiter.time();
        int count = rateLimiter.count();
        String message = rateLimiter.message();

        String combineKey = getCombineKey(rateLimiter, point);
        List<Object> keys = Collections.singletonList(combineKey);
        try {
            Long number = redisTemplate.execute(limitScript, keys, count, time);
            if (StringUtils.isNull(number) || number.intValue() > count) {
                throw new ServiceException(message);
            }
            log.info("限制请求'{}',当前请求'{}',缓存key'{}'", count, number.intValue(), key);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("服务器限流异常，请稍候再试");
        }
    }

    public String getCombineKey(RateLimiter rateLimiter, JoinPoint point) throws ServiceException {
        HttpServletRequest request = ServletUtils.getRequest();
        StringBuffer stringBuffer = new StringBuffer(rateLimiter.key());

        // 根据限流类型添加不同的后缀到 key
        if (rateLimiter.limitType() == LimitType.IP) {
            stringBuffer.append(IpUtils.getIpAddr(request)).append("-");
        } else if (rateLimiter.limitType() == LimitType.USER) {
            Long userId = SecurityUtils.getUserId();
            log.info("限制userId请求'{}'", userId);
            // userId缺失
            if (StringUtils.isNull(userId)) {
                throw new ServiceException("用户ID缺失，无法进行用户限流");
            }
            stringBuffer.append(userId).append("-");
        } else if (rateLimiter.limitType() == LimitType.DEVICE) {
            String deviceId = request.getHeader("Device-Id");  // 从请求头中获取设备号
            if (StringUtils.isNotEmpty(deviceId)) {
                stringBuffer.append(deviceId).append("-");
            } else {
                throw new ServiceException("设备号缺失，无法进行限流");
            }
        }

        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Class<?> targetClass = method.getDeclaringClass();
        stringBuffer.append(targetClass.getName()).append("-").append(method.getName());
        return stringBuffer.toString();
    }

}
