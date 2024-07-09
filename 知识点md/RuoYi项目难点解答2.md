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

### 详细解释 `getAuthentication().getPrincipal()`

`getAuthentication().getPrincipal()` 获取当前用户的 `Principal` 对象时，这个对象通常是 `LoginUser`
类型的实例，它包含了当前登录用户的详细信息。这个 `Principal` 对象确实是存在于内存中的，存储在 Spring Security 的上下文中。

#### 1. 用户认证过程

当用户登录时，Spring Security 会通过认证过程验证用户凭据，并将用户信息存储在内存中的 `SecurityContext` 中。这个过程通常如下：

1. **用户提交登录请求**：用户通过登录表单提交用户名和密码。
2. **Spring Security 认证过滤器**：Spring Security 的认证过滤器（如 `UsernamePasswordAuthenticationFilter`
   ）拦截登录请求，并调用认证管理器（`AuthenticationManager`）进行认证。
3. **`UserDetailsService` 加载用户信息**：认证管理器调用 `UserDetailsService` 的 `loadUserByUsername`
   方法，从数据库或其他数据源加载用户信息。
4. **创建 `Authentication` 对象**：如果用户认证成功，Spring Security 会创建一个 `Authentication`
   对象，并将其存储在 `SecurityContextHolder` 中。

#### 2. 存储用户信息

用户信息存储在 `SecurityContextHolder` 中，直到用户会话结束或显式注销。这个信息包括用户的 `Principal`
对象，通常是 `LoginUser` 类型的实例。`LoginUser` 对象包含用户的详细信息，如用户名、角色、权限等。

```java
public class LoginUser implements UserDetails {
    private SysUser user;
    private Set<String> roles;
    private Set<String> permissions;

    // Constructors, getters, setters, and other methods

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Return user roles and permissions
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // Other methods from UserDetails interface
}
```

#### 3. 获取当前用户信息

你可以通过以下代码获取当前用户的 `Principal` 对象：

```java
public static LoginUser getLoginUser() {
    try {
        return (LoginUser) getAuthentication().getPrincipal();
    } catch (Exception e) {
        throw new ServiceException("获取用户信息异常", HttpStatus.UNAUTHORIZED);
    }
}

public static Authentication getAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
}
```

这段代码从 `SecurityContextHolder` 中获取当前的 `Authentication` 对象，然后从 `Authentication`
对象中获取 `Principal`。`Principal` 通常是 `LoginUser` 对象，包含当前登录用户的详细信息。

### 总结

- **存储位置**：用户信息在认证成功后存储在内存中的 `SecurityContextHolder` 中，直到用户会话结束或显式注销。
- **获取方式**：通过 `SecurityContextHolder.getContext().getAuthentication().getPrincipal()`
  可以获取当前用户的 `Principal` 对象。
- **`Principal` 对象类型**：`Principal` 对象通常是 `LoginUser` 类型的实例，包含用户的详细信息，如用户名、角色、权限等。

这种设计使得你可以方便地在应用程序的任何地方获取当前登录用户的信息，而无需每次都查询数据库。

是的，`username`（用户名）和 `nickname`（昵称）有明确的区别，虽然它们在某些情况下可能会混用或交叉使用。以下是它们的区别和各自的用途：

### Username（用户名）

1. **唯一性**：
    - 用户名在系统中是唯一的标识符，用于区分不同的用户。每个用户都有一个唯一的用户名。

2. **用途**：
    - 用户名通常用于登录和认证。用户在登录时使用用户名和密码进行身份验证。
    - 用户名可能用于系统内部的标识和追踪，例如日志记录、权限控制等。

3. **格式**：
    - 用户名通常遵循特定的格式和规则，可能只包含字母、数字和一些特殊字符，且长度有限制。

4. **安全性**：
    - 用户名是敏感信息，虽然不像密码那样敏感，但仍需要保护，以防止未经授权的用户获取。

### Nickname（昵称）

1. **非唯一性**：
    - 昵称不需要是唯一的。多个用户可以有相同的昵称。

2. **用途**：
    - 昵称主要用于展示和社交。它通常用于显示在用户界面、评论、帖子、聊天等场合。
    - 昵称是用户在与其他用户互动时的显示名，通常用于增强用户体验和个性化。

3. **格式**：
    - 昵称的格式通常较为宽松，用户可以选择任何喜欢的昵称，包括特殊字符、表情符号等，限制较少。

4. **安全性**：
    - 昵称一般不作为敏感信息处理，因为它主要用于展示和非安全性用途。

### 示例

#### 用户表结构

在数据库中，用户表通常会包含用户名和昵称两个字段：

```sql
create table sys_user
(
    user_id     BIGINT primary key auto_increment,
    user_name   VARCHAR(50)  not null unique, -- 用户名，唯一
    nick_name   VARCHAR(50),                  -- 昵称，不需要唯一
    password    VARCHAR(100) not null,
    email       VARCHAR(100),
    phonenumber VARCHAR(20),
    status      CHAR(1),
    create_time TIMESTAMP,
    update_time TIMESTAMP
);
```

#### Java 类示例

在 Java 类中，用户类可能如下定义：

```java
public class SysUser {
    private Long userId;
    private String userName;  // 用户名
    private String nickName;  // 昵称
    private String password;
    private String email;
    private String phonenumber;
    private String status;
    private Date createTime;
    private Date updateTime;

    // Getters and setters
}
```

### 用例示例

- **登录**：
  ```java
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      SysUser user = userService.selectUserByUserName(username);
      if (user == null) {
          throw new UsernameNotFoundException("用户名不存在");
      }

@ConfigurationProperties 注解用于将属性绑定到一个 POJO 类上，通常用于处理一组相关的配置属性。它使用前缀来识别需要绑定的属性。
@Value 注解用于将单个属性值注入到字段中。它通常用于简单的属性注入

### 密码问题

在Spring Security中，`BCryptPasswordEncoder` 使用 bcrypt 算法来对密码进行加密。bcrypt 是一种哈希算法，
它为每个输入生成不同的哈希值，即使输入的密码相同。bcrypt 内部会自动生成一个随机的 salt 来确保相同的输入生成不同的哈希值。

### 代码示例解释

#### 1. 加密密码

```java
public static String encryptPassword(String password) {
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    return passwordEncoder.encode(password);
}
```

`encryptPassword` 方法使用 `BCryptPasswordEncoder` 来对输入的明文密码进行加密。
`BCryptPasswordEncoder` 会自动生成一个随机的 salt
并将其与密码结合，然后生成哈希值。因此，即使输入相同的密码，每次调用 `encode` 方法生成的哈希值也会不同。

#### 2. 验证密码

```java
public static boolean matchesPassword(String rawPassword, String encodedPassword) {
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    return passwordEncoder.matches(rawPassword, encodedPassword);
}
```

`matchesPassword` 方法使用 `BCryptPasswordEncoder` 来验证输入的明文密码和已加密的密码是否匹配。
`BCryptPasswordEncoder` 会从加密的密码中提取出 salt，并用这个 salt 对输入的明文密码进行哈希处理，然后将生成的哈希值与加密的密码进行比较。

### bcrypt 工作原理

- **随机 salt**：每次加密时，bcrypt 会生成一个随机的 salt。
- **哈希计算**：bcrypt 将 salt 和密码结合进行哈希计算。
- **存储格式**：生成的哈希值包括 salt 和哈希值，所以即使输入的密码相同，每次加密的结果也不同。

### 示例代码

下面是一个完整的示例，演示如何使用 `BCryptPasswordEncoder` 进行密码加密和验证：

```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtils {

    public static String encryptPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    /**
     * 判断密码是否相同
     *
     * @param rawPassword     真实密码
     * @param encodedPassword 加密后字符
     * @return 结果
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public static void main(String[] args) {
        String password = "myPassword";

        // 加密密码
        String encryptedPassword1 = encryptPassword(password);
        String encryptedPassword2 = encryptPassword(password);

        System.out.println("Encrypted Password 1: " + encryptedPassword1);
        System.out.println("Encrypted Password 2: " + encryptedPassword2);

        // 验证密码
        boolean matches1 = matchesPassword(password, encryptedPassword1);
        boolean matches2 = matchesPassword(password, encryptedPassword2);

        System.out.println("Matches 1: " + matches1);
        System.out.println("Matches 2: " + matches2);
    }
}
```

### 输出示例

```
Encrypted Password 1: $2a$10$EIX/hKpKPQzAblhPlc1C0e6QeJ2uTI2Lbc4Q.Qc.DU/xu/y5Z2Fey
Encrypted Password 2: $2a$10$uJdWzy8kO3GzA9OBFbWqHe.G5Rr7fBHpGCHhAqvayGgRBcNGvO9cG
Matches 1: true
Matches 2: true
```

你会注意到，即使两次加密相同的明文密码，生成的加密结果也是不同的。这是因为 bcrypt 使用了随机生成的 salt。

### 总结

- `BCryptPasswordEncoder` 使用 bcrypt 算法，它为每个密码生成一个唯一的 salt。
- 即使输入相同的密码，每次加密生成的哈希值也是不同的。
- 哈希值包括 salt 和哈希值，因此在验证时，可以从哈希值中提取出 salt，用于对输入的明文密码进行哈希处理和比较。