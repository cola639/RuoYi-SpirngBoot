在Spring Boot中，`@RequestMapping` 和其他映射注解（如 `@GetMapping`, `@PostMapping` 等）在处理路径时会自动规范化路径。
具体来说：

- 当路径结尾有 `/` 时，会自动去掉。
- 当路径之间有多个 `/` 时，会自动合并成一个 `/`。

因此，在你提供的示例中，无论是访问 `/repeatSubmit` 还是 `/repeatSubmit/`，都能够访问到 `submitForm` 方法。

### 具体示例

#### 控制器代码

```java

@RestController
@RequestMapping("/repeatSubmit")
public class RepeatController extends BaseController {

    @RepeatSubmit(interval = 5000, message = "请不要重复提交请求")
    @GetMapping("/")
    public String submitForm(@RequestParam String data) {
        // 处理提交请求的逻辑
        return "提交成功：" + data;
    }
}
```

#### 测试访问

- 访问 `http://localhost:8080/repeatSubmit`：
    - 结果：能够正确访问到 `submitForm` 方法，并返回 "提交成功：data"。

- 访问 `http://localhost:8080/repeatSubmit/`：
    - 结果：能够正确访问到 `submitForm` 方法，并返回 "提交成功：data"。

### 说明

Spring Boot 会自动规范化请求路径，所以无论是否包含结尾的 `/`，结果都是一样的。这种行为有助于简化路径匹配，并避免由于路径格式问题导致的错误。

### 示例日志

如果你希望看到实际的日志来验证这一点，可以添加一个简单的日志记录功能：

```java

@RestController
@RequestMapping("/repeatSubmit")
public class RepeatController extends BaseController {

    @RepeatSubmit(interval = 5000, message = "请不要重复提交请求")
    @GetMapping("/")
    public String submitForm(@RequestParam String data) {
        // 处理提交请求的逻辑
        System.out.println("Received data: " + data);
        return "提交成功：" + data;
    }
}
```

访问 `http://localhost:8080/repeatSubmit?data=test` 和 `http://localhost:8080/repeatSubmit/?data=test` 都会在控制台输出：

```
Received data: test
```