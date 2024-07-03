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

### POJO

在 Spring Boot 中，当你使用 `@GetMapping` 处理 GET 请求时，如果方法参数是一个 POJO（Plain Old Java Object），Spring
会自动将请求参数绑定到该 POJO 的属性上。这种机制称为 **数据绑定**（Data Binding）。

### 数据绑定的工作原理

Spring MVC 的数据绑定机制会自动将 HTTP 请求中的参数映射到 POJO 的属性上。具体来说：

- 请求参数的名称和 POJO 属性的名称相匹配。
- Spring 自动调用 POJO 的 setter 方法，将请求参数的值赋给对应的属性。

### 示例

假设有一个 POJO 类 `SysLogininfor`，它包含了一些属性，如 `username` 和 `loginTime`：

```java
public class SysLogininfor {
    private String username;
    private Date loginTime;

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }
}
```

当你发送一个 GET 请求，如：

```
GET /list?username=john&loginTime=2024-07-03
```

Spring 会自动将 `username` 和 `loginTime` 的值绑定到 `SysLogininfor` 对象的相应属性上。

### 具体代码解析

在你的代码中：

```java

@PreAuthorize("@ss.hasPermi('monitor:logininfor:list')")
@GetMapping("/list")
public TableDataInfo list(SysLogininfor logininfor) {
    startPage();
    List<SysLogininfor> list = logininforService.selectLogininforList(logininfor);
    return getDataTable(list);
}
```

- **方法参数 `SysLogininfor logininfor`**：Spring 会自动创建一个 `SysLogininfor` 对象，并根据请求参数填充其属性。
- **`@GetMapping("/list")`**：处理 `/list` 路径的 GET 请求。
- **`startPage()`**：通常用于分页的开始方法（可能是你项目中的一个自定义方法）。
- **`logininforService.selectLogininforList(logininfor)`**：使用填充了请求参数的 `logininfor` 对象调用服务层方法。

### 为什么不需要 `@RequestParam`

1. **POJO 自动绑定**：如果使用 POJO 作为参数，Spring 会自动将请求参数绑定到 POJO
   的属性上。你不需要为每个参数单独使用 `@RequestParam`。
2. **更简洁的代码**：通过使用 POJO 自动绑定，可以避免多个 `@RequestParam` 注解，使代码更简洁和易于维护。
3. **适应变化**：当请求参数较多或可能变化时，使用 POJO 可以更方便地适应变化，而不需要修改方法签名。

### 如何验证自动绑定

你可以通过调试或打印日志的方式验证 Spring 是否正确地将请求参数绑定到 POJO 的属性上。例如：

```java

@PreAuthorize("@ss.hasPermi('monitor:logininfor:list')")
@GetMapping("/list")
public TableDataInfo list(SysLogininfor logininfor) {
    // 打印 logininfor 对象以验证绑定
    System.out.println(logininfor);

    startPage();
    List<SysLogininfor> list = logininforService.selectLogininforList(logininfor);
    return getDataTable(list);
}
```

### 总结

在 Spring Boot 中，通过使用 POJO 作为控制器方法的参数，Spring 会自动将 HTTP 请求参数绑定到 POJO
的属性上。这种数据绑定机制使代码更简洁和易于维护，不需要为每个请求参数单独使用 `@RequestParam`。

## @DataSource切换数据源

需求提出
在项目中使用多个数据源是常见需求，尤其是在读写分离、分库分表等场景下。本文将介绍如何在若依项目中配置和使用多数据源，以实现对主从数据库的管理。

解决思路
修改配置文件，定义多个数据源。

使用Spring的注解机制，在Service层指定使用哪个数据源。

所需技术
Spring Boot

MyBatis

Druid 数据库连接池

项目结构
ruoyi-admin
├── src\main\java
│ ├── resources
│ │ └── application-druid.yml
ruoyi-test
├── src\main\java
│ │ └── com\ruoyi\test
│ │ └── service\impl
│ │ └── UserServiceImpl.java
注意事项
确保Druid连接池的依赖已添加到项目中。

数据源配置中的用户名、密码和URL根据实际情况填写。

在Service层使用注解时，确保指定的数据源名称正确。

完整代码（分步骤）
第一步：修改配置文件，定义多个数据源
在 resources 目录下修改 application-druid.yml 文件，添加从库数据源配置。

spring:
datasource:
druid:
master:
url: jdbc:mysql://localhost:3306/ry_master?useUnicode=true&characterEncoding=utf-8&useSSL=false
username: root
password: 123456
driver-class-name: com.mysql.cj.jdbc.Driver
slave:
enabled: true
url: jdbc:mysql://localhost:3306/ry_slave?useUnicode=true&characterEncoding=utf-8&useSSL=false
username: root
password: 123456
driver-class-name: com.mysql.cj.jdbc.Driver
第二步：在Service实现中添加DataSource注解
在自定义模块或ruoyi-system的Service实现中，使用 @DataSource 注解指定使用从库数据源。

import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.project.system.mapper.UserMapper;
import com.ruoyi.project.system.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl {

    @Autowired
    private UserMapper userMapper;

    @DataSource(value = DataSourceType.SLAVE)
    public List<User> selectUserList() {
        return userMapper.selectUserList();
    }

}
通过上述步骤，成功配置了若依项目的多数据源。在调用 selectUserList 方法时，将使用从库数据源进行查询操作。