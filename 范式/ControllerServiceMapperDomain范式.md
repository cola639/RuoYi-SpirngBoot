在设计和实现基于Spring Boot的Controller-Service-Mapper架构时，遵循一些最佳实践可以帮助你创建高效、可维护和可扩展的应用程序。以下是一些常见的最佳实践建议和范式：

### 1. Controller 层最佳实践

- **职责单一**：Controller层的职责应该是处理HTTP请求，调用Service层方法，并返回响应。不要在Controller中实现业务逻辑。
- **使用注解**：使用Spring MVC注解（如`@RestController`、`@RequestMapping`、`@GetMapping`、`@PostMapping`等）来定义请求映射和处理方法。
- **请求和响应对象**：使用DTO（Data Transfer Object）来封装请求和响应数据，避免直接暴露实体对象。
- **统一异常处理**：使用`@ControllerAdvice`和`@ExceptionHandler`来统一处理异常，确保返回一致的错误响应格式。

```java

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public UserDTO createUser(@RequestBody UserDTO userDTO) {
        return userService.createUser(userDTO);
    }

    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return userService.updateUser(id, userDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
```

### 2. Service 层最佳实践

- **职责单一**：Service层应该只包含业务逻辑。避免直接进行数据库操作，应该通过Mapper层进行数据访问。
- **事务管理**：使用`@Transactional`注解来管理事务，确保业务操作的原子性和一致性。
- **接口和实现**：使用接口来定义服务层方法，并提供相应的实现。这有助于提高代码的可测试性和可维护性。

```java
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<UserDTO> getAllUsers() {
        return userMapper.findAll();
    }

    @Override
    public UserDTO getUserById(Long id) {
        return userMapper.findById(id);
    }

    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        userMapper.insert(userDTO);
        return userDTO;
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userMapper.findById(id);
        if (existingUser != null) {
            userDTO.setId(id);
            userMapper.update(userDTO);
            return userDTO;
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userMapper.delete(id);
    }
}
```

### 3. Mapper 层最佳实践

- **使用MyBatis注解或XML配置**：可以选择使用MyBatis注解或XML文件来定义SQL语句。XML配置适用于复杂查询，而注解适用于简单查询。
- **接口定义**：Mapper接口只负责定义数据访问方法，不包含业务逻辑。
- **类型安全**：确保Mapper方法和SQL语句中的参数和返回值类型一致，避免类型转换错误。

#### 使用MyBatis注解

```java
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM users")
    List<UserDTO> findAll();

    @Select("SELECT * FROM users WHERE id = #{id}")
    UserDTO findById(Long id);

    @Insert("INSERT INTO users(name, email) VALUES(#{name}, #{email})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(UserDTO userDTO);

    @Update("UPDATE users SET name = #{name}, email = #{email} WHERE id = #{id}")
    void update(UserDTO userDTO);

    @Delete("DELETE FROM users WHERE id = #{id}")
    void delete(Long id);
}
```

#### 使用MyBatis XML配置

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.example.mapper.UserMapper">

    <select id="findAll" resultType="com.example.dto.UserDTO">
        SELECT * FROM users
    </select>

    <select id="findById" parameterType="long" resultType="com.example.dto.UserDTO">
        SELECT * FROM users WHERE id = #{id}
    </select>

    <insert id="insert" parameterType="com.example.dto.UserDTO" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO users(name, email) VALUES(#{name}, #{email})
    </insert>

    <update id="update" parameterType="com.example.dto.UserDTO">
        UPDATE users SET name = #{name}, email = #{email} WHERE id = #{id}
    </update>

    <delete id="delete" parameterType="long">
        DELETE FROM users WHERE id = #{id}
    </delete>

</mapper>
```

### 4. 实体和DTO

- **分离实体和DTO**：使用DTO来传输数据，避免直接暴露数据库实体对象。这样可以增加安全性和灵活性。
- **数据转换**：在Service层中进行实体和DTO的转换，使用工具类（如MapStruct）简化转换过程。

```java
public class User {
    private Long id;
    private String name;
    private String email;

    // Getters and Setters
}

public class UserDTO {
    private Long id;
    private String name;
    private String email;

    // Getters and Setters
}
```

### 5. 统一异常处理

- **全局异常处理**：使用`@ControllerAdvice`和`@ExceptionHandler`注解来捕获并处理全局异常，确保返回一致的错误响应格式。

```java
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse("NOT_FOUND", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

### 6. 事务管理

- **声明式事务**：使用`@Transactional`注解来管理事务，确保数据库操作的一致性和原子性。

```java
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        userMapper.insert(userDTO);
        return userDTO;
    }

    // 其他方法
}
```

### 总结

通过遵循这些最佳实践，可以创建一个清晰、可维护和可扩展的Spring
Boot应用程序。分层架构（Controller-Service-Mapper）有助于将职责分离，使代码更容易理解和管理，同时也提高了应用程序的可测试性和可维护性。