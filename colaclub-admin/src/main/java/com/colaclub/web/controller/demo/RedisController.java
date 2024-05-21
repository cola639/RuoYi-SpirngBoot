package com.colaclub.web.controller.demo;

import com.colaclub.common.core.redis.RedisCache;
import com.colaclub.demo.model.RedisRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存操作接口
 *
 * @author colaclub
 */
@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private RedisCache redisCache;

    @GetMapping("/get/{key}")
    public Object getRedis(@PathVariable String key) {
        return redisCache.getCacheObject(key);
    }

    @PostMapping("/add")
    public String addRedis(@RequestBody RedisRequest request) {
        String key = request.getKey();
        String value = request.getValue();
        // 创建带有层级关系的缓存键
        String redisKey = "tree_config:" + key;
        redisCache.setCacheObject(redisKey, value, 60, TimeUnit.SECONDS);
        return "Key: " + redisKey + ", Value: " + value + " has been added to Redis.";
    }

    @PutMapping("/update")
    public String updateRedis(@RequestBody RedisRequest request) {
        String key = request.getKey();
        String value = request.getValue();
        // 创建带有层级关系的缓存键
        String redisKey = "tree_config:" + key;

        // 更新数据库 (假设有对应的数据库操作)
        // exampleService.updateEntity(key, value);

        // 更新缓存
        redisCache.setCacheObject(redisKey, value);

        return "Key: " + redisKey + " has been updated to new Value: " + value;
    }

    @DeleteMapping("/delete/{key}")
    public String deleteRedis(@PathVariable String key) {
        boolean result = redisCache.deleteObject(key);
        return result ? "Key: " + key + " has been deleted from Redis." : "Failed to delete key: " + key;
    }
}

