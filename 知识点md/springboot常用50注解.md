在Spring Boot中，注解是非常重要且常用的工具，能够帮助开发者简化配置、管理依赖和控制行为。以下是常用的50个注解，按类别分类列出。

### 一、核心注解

1. `@SpringBootApplication`
2. `@ComponentScan`
3. `@Configuration`
4. `@Bean`
5. `@Import`
6. `@ImportResource`
7. `@Profile`
8. `@PropertySource`
9. `@Value`
10. `@Component`

### 二、Web相关注解

1. `@Controller`
2. `@RestController`
3. `@RequestMapping`
4. `@GetMapping`
5. `@PostMapping`
6. `@PutMapping`
7. `@DeleteMapping`
8. `@PatchMapping`
9. `@RequestBody`
10. `@ResponseBody`
11. `@PathVariable`
12. `@RequestParam`
13. `@RequestHeader`
14. `@CookieValue`
15. `@ModelAttribute`
16. `@SessionAttributes`
17. `@ExceptionHandler`
18. `@ControllerAdvice`
19. `@CrossOrigin`

### 三、数据访问相关注解

1. `@Repository`
2. `@Transactional`
3. `@PersistenceContext`
4. `@PersistenceUnit`
5. `@Query`
6. `@Modifying`
7. `@Param`

### 四、Spring AOP相关注解

1. `@Aspect`
2. `@Before`
3. `@After`
4. `@AfterReturning`
5. `@AfterThrowing`
6. `@Around`
7. `@Pointcut`

### 五、Spring Security相关注解

1. `@EnableWebSecurity`
2. `@EnableGlobalMethodSecurity`
3. `@Secured`
4. `@RolesAllowed`
5. `@PreAuthorize`
6. `@PostAuthorize`
7. `@PreFilter`
8. `@PostFilter`

### 六、测试相关注解

1. `@SpringBootTest`
2. `@WebMvcTest`
3. `@DataJpaTest`
4. `@MockBean`
5. `@SpyBean`
6. `@BeforeEach`
7. `@AfterEach`

### 七、其他注解

1. `@Autowired`
2. `@Qualifier`
3. `@Primary`
4. `@Lazy`
5. `@Scope`
6. `@EventListener`
7. `@Conditional`

### 注解详细说明

#### 核心注解

1. **`@SpringBootApplication`**：综合注解，包含`@Configuration`、`@EnableAutoConfiguration`、`@ComponentScan`。
2. **`@ComponentScan`**：指定要扫描的包。
3. **`@Configuration`**：定义配置类。
4. **`@Bean`**：定义一个Bean。
5. **`@Import`**：导入其他配置类。
6. **`@ImportResource`**：导入XML配置文件。
7. **`@Profile`**：指定配置的适用环境。
8. **`@PropertySource`**：加载属性文件。
9. **`@Value`**：注入属性值。
10. **`@Component`**：一个基础注解，表明这个类是一个Spring Bean，应该被Spring容器自动检测和实例化。

#### Web相关注解

1. **`@Controller`**：定义控制器。
2. **`@RestController`**：组合注解，`@Controller`和`@ResponseBody`。
3. **`@RequestMapping`**：映射HTTP请求到处理方法。
4. **`@GetMapping`**：映射HTTP GET请求到处理方法。
5. **`@PostMapping`**：映射HTTP POST请求到处理方法。
6. **`@PutMapping`**：映射HTTP PUT请求到处理方法。
7. **`@DeleteMapping`**：映射HTTP DELETE请求到处理方法。
8. **`@PatchMapping`**：映射HTTP PATCH请求到处理方法。
9. **`@RequestBody`**：将请求体映射到方法参数。
10. **`@ResponseBody`**：将方法返回值映射到响应体。
11. **`@PathVariable`**：映射URI模板变量到方法参数。
12. **`@RequestParam`**：映射请求参数到方法参数。
13. **`@RequestHeader`**：映射请求头到方法参数。
14. **`@CookieValue`**：映射Cookie值到方法参数。
15. **`@ModelAttribute`**：绑定请求参数到模型。
16. **`@SessionAttributes`**：声明Session属性。
17. **`@ExceptionHandler`**：处理控制器方法中的异常。
18. **`@ControllerAdvice`**：全局处理控制器的异常。
19. **`@CrossOrigin`**：允许跨域访问。

#### 数据访问相关注解

1. **`@Repository`**：定义数据访问组件。
2. **`@Transactional`**：声明事务。
3. **`@PersistenceContext`**：注入`EntityManager`。
4. **`@PersistenceUnit`**：注入`EntityManagerFactory`。
5. **`@Query`**：定义JPQL查询。
6. **`@Modifying`**：声明更新查询。
7. **`@Param`**：绑定查询参数。

#### Spring AOP相关注解 Action oriented programming

1. **`@Aspect`**：定义切面。
2. **`@Before`**：定义前置通知。
3. **`@After`**：定义后置通知。
4. **`@AfterReturning`**：定义返回通知。
5. **`@AfterThrowing`**：定义异常通知。
6. **`@Around`**：定义环绕通知。
7. **`@Pointcut`**：定义切点。

#### Spring Security相关注解

1. **`@EnableWebSecurity`**：启用Web安全。
2. **`@EnableGlobalMethodSecurity`**：启用全局方法安全。
3. **`@Secured`**：定义安全角色。
4. **`@RolesAllowed`**：定义允许的角色。
5. **`@PreAuthorize`**：方法调用前的权限检查。
6. **`@PostAuthorize`**：方法调用后的权限检查。
7. **`@PreFilter`**：方法调用前的过滤。
8. **`@PostFilter`**：方法调用后的过滤。

#### 测试相关注解

1. **`@SpringBootTest`**：Spring Boot集成测试。
2. **`@WebMvcTest`**：Web层测试。
3. **`@DataJpaTest`**：JPA层测试。
4. **`@MockBean`**：注入Mock对象。
5. **`@SpyBean`**：注入Spy对象。
6. **`@BeforeEach`**：每个测试方法前执行。
7. **`@AfterEach`**：每个测试方法后执行。

#### 其他注解

1. **`@Autowired`**：自动注入依赖。
2. **`@Qualifier`**：指定注入的Bean。
3. **`@Primary`**：指定主要的Bean。
4. **`@Lazy`**：延迟初始化Bean。
5. **`@Scope`**：指定Bean的作用域。
6. **`@EventListener`**：事件监听器。
7. **`@Conditional`**：条件化配置。

这些注解是Spring Boot中最常用的，熟练掌握这些注解可以极大提高开发效率和代码质量。

`@RequestParam` 是 Spring 框架中的一个注解，用于将请求参数绑定到控制器方法的参数上。这个注解可以用于处理来自 HTTP GET 或
POST 请求的参数。

### 使用场景

1. **单个请求参数**：
    - 绑定 URL 查询参数或表单参数到方法参数。
2. **多个请求参数**：
    - 处理多个查询参数或表单参数。

### 语法

```java
@RequestParam(value = "paramName", required = true / false, defaultValue = "default")
```

- **value**: 请求参数的名字。
- **required**: 该参数是否为必需的，默认为 true。如果请求中缺少该参数且未设置默认值，将抛出异常。
- **defaultValue**: 如果请求中缺少该参数，使用的默认值。

### 请求方式

- **GET 请求**：
  ```http
  GET /endpoint?param1=value1&param2=value2
  ```

- **POST 请求**：
  ```http
  POST /endpoint
  Content-Type: application/x-www-form-urlencoded

  param1=value1&param2=value2
  ```

### 示例代码

1. **单个请求参数**

```java
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    @GetMapping("/greet")
    public String greet(@RequestParam(value = "name", required = false, defaultValue = "World") String name) {
        return "Hello, " + name;
    }
}
```

- **请求**:
  ```http
  GET /greet?name=John
  ```
- **响应**:
  ```http
  Hello, John
  ```

- **请求（无参数）**:
  ```http
  GET /greet
  ```
- **响应**:
  ```http
  Hello, World
  ```

2. **多个请求参数**

```java
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    @PostMapping("/add")
    public String add(@RequestParam("num1") int num1, @RequestParam("num2") int num2) {
        return "Sum: " + (num1 + num2);
    }
}
```

- **请求**:
  ```http
  POST /add
  Content-Type: application/x-www-form-urlencoded

  num1=5&num2=10
  ```
- **响应**:
  ```http
  Sum: 15
  ```

### 解析

当 Spring MVC 处理请求时，会自动解析请求参数，并将其转换为控制器方法中的参数类型。

1. **绑定和转换**：
    - `@RequestParam` 的值（如 `paramName`）与请求中的参数名匹配。
    - Spring 会尝试将请求参数的值转换为控制器方法参数的类型。
    - 如果 `required` 为 true 且请求中缺少参数，Spring 会抛出 `MissingServletRequestParameterException`。

2. **默认值**：
    - 如果 `defaultValue` 设置了值，则当请求中缺少参数时，将使用默认值。
    - 这种方式避免了 `required` 参数的约束。

### 请求参数示例

- **GET 请求**:
  ```http
  GET /endpoint?param1=value1&param2=value2
  ```

- **POST 请求**:
  ```http
  POST /endpoint
  Content-Type: application/x-www-form-urlencoded

  param1=value1&param2=value2
  ```

在以上示例中，`@RequestParam` 注解帮助我们轻松地从请求中提取参数，并在控制器方法中使用它们。这种方式非常适合处理简单的查询参数和表单数据。