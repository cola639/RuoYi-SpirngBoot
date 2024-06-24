package com.colaclub.web.controller.demo;

import com.colaclub.framework.config.RedisLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
public class RedisLockController {

    @Autowired
    private RedisLockService redisLockService;

    @PostMapping("/acquireLock")
    public String acquireLock() {
        String lockValue = redisLockService.acquireLock();
        if (lockValue != null) {
            return "Lock acquired with value: " + lockValue;
        } else {
            return "Failed to acquire lock";
        }
    }

    @PostMapping("/releaseLock")
    public String releaseLock(@RequestParam String lockValue) {
        redisLockService.releaseLock(lockValue);
        return "Lock released with value: " + lockValue;
    }

    @PostMapping("/renewLock")
    public String renewLock(@RequestParam String lockValue) {
        boolean success = redisLockService.renewLock(lockValue);
        return success ? "Lock renewed with value: " + lockValue : "Failed to renew lock";
    }
}
