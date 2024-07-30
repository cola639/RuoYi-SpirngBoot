package com.colaclub.framework.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RedisLockService {
    // 锁的键名
    private static final String LOCK_KEY = "lock_key";
    // 锁的过期时间，单位：秒
    private static final int EXPIRE_TIME = 10; // seconds

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 尝试获取锁
     *
     * @return 返回获取锁的值（UUID），如果获取失败返回null
     */
    public String acquireLock() {
        // 生成一个唯一标识符作为锁的值
        String lockValue = UUID.randomUUID().toString();
        // 尝试设置键值，如果键不存在则设置成功，并且设置过期时间为10秒
        Boolean success = redisTemplate.opsForValue().setIfAbsent(LOCK_KEY, lockValue, EXPIRE_TIME, TimeUnit.SECONDS);
        // 如果设置成功返回锁的值，否则返回null
        return Boolean.TRUE.equals(success) ? lockValue : null;
    }

    /**
     * 释放锁
     *
     * @param lockValue 锁的值（UUID），只有持有相同锁值的线程才能释放锁
     */
    public void releaseLock(String lockValue) {
        // 获取当前锁的值
        String currentValue = redisTemplate.opsForValue().get(LOCK_KEY);
        // 只有当前锁的值与传入的锁值相同时，才能释放锁
        if (lockValue.equals(currentValue)) {
            // 删除锁
            redisTemplate.delete(LOCK_KEY);
        }
    }

    /**
     * 续约锁
     *
     * @param lockValue 锁的值（UUID），只有持有相同锁值的线程才能续约锁
     * @return 如果续约成功返回true，否则返回false
     */
    public boolean renewLock(String lockValue) {
        // 获取当前锁的值
        String currentValue = redisTemplate.opsForValue().get(LOCK_KEY);
        // 只有当前锁的值与传入的锁值相同时，才能续约锁
        if (lockValue.equals(currentValue)) {
            // 续约锁的过期时间为10秒
            redisTemplate.expire(LOCK_KEY, EXPIRE_TIME, TimeUnit.SECONDS);
            return true;
        }
        return false;
    }
}
