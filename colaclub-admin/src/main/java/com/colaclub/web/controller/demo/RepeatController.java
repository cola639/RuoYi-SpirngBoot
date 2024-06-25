package com.colaclub.web.controller.demo;

import com.colaclub.common.annotation.RepeatSubmit;
import com.colaclub.common.core.controller.BaseController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 限流 接口
 *
 * @author colaclub
 */
@RestController
@RequestMapping("/repeatSubmit")
public class RepeatController extends BaseController {

  @RepeatSubmit(interval = 5000, message = "请不要重复提交请求")
  @GetMapping("")
  public String submitForm(@RequestParam String data) {
    // 处理提交请求的逻辑
    return "提交成功：" + data;
  }
}
