package com.colaclub.web.controller.limit;

import com.colaclub.common.annotation.RateLimiter;
import com.colaclub.common.core.controller.BaseController;
import com.colaclub.common.enums.LimitType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 限流 接口
 *
 * @author colaclub
 */
@RestController
@RequestMapping("/location")
public class LimitController extends BaseController {

    @RateLimiter(key = "getLocation", time = 60, count = 10, limitType = LimitType.DEVICE)
    @GetMapping("")
    public String getLocation() {
        // 假设的位置数据
        String locationData = "{ \"latitude\": \"40.7128\", \"longitude\": \"-74.0060\" }"; // 纽约的经纬度
        // 在实际应用中，这里可能会是从数据库或外部API获取的数据

        // 返回位置数据
        return locationData;
    }
}
